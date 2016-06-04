package com.mattpflance.hearthlist;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.mattpflance.hearthlist.data.HearthListContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * A class that extends IntentService to download and populate our ContentProvider
 * initially, and every time a new expansion in HearthStone comes out
 */
public class DataDownloadIntentService extends IntentService {

    final String LOG_TAG = "DDIntentService";

    final int IMAGE_TYPE_REGULAR = 0;
    final int IMAGE_TYPE_GOLD = 1;

    final String[] URL_COLUMNS = new String[] {
            HearthListContract.CardEntry._ID,
            HearthListContract.CardEntry.COLUMN_REG_IMG_URL,
            HearthListContract.CardEntry.COLUMN_GOLD_IMG_URL
    };

    final int COLUMN_ID = 0;
    final int COLUMN_REG_URL = 1;
    final int COLUMN_GOLD_URL = 2;

    // Names of JSON args needed
    final String MHS_NAME = "name";
    final String MHS_TYPE = "type";
    final String MHS_RARITY = "rarity";
    final String MHS_CLASS = "playerClass";
    final String MHS_COST = "cost";
    final String MHS_RACE = "race";
    final String MHS_ATTACK = "attack";
    final String MHS_MINION_HP = "health";
    final String MHS_WEAPON_HP = "durability";
    final String MHS_ARTIST = "artist";
    final String MHS_TEXT = "text";
    final String MHS_FLAVOR = "flavor";
    final String MHS_MECHANICS = "mechanics";
    final String MHS_HTG = "howToGet";
    final String MHS_HTG_GOLD = "howToGetGold";
    final String MHS_IMG = "img";
    final String MHS_GOLD_IMG = "imgGold";

    // Used to query image urls faster
    private Cursor mCursor;

    public DataDownloadIntentService() {
        super("com.mattpflance.hearthlist.DataDownloadIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long startTime = System.currentTimeMillis();
        long endTime = 0;

        OkHttpClient mClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://omgvamp-hearthstone-v1.p.mashape.com/cards?collectible=1")
                .header("X-Mashape-Key", BuildConfig.MASHAPE_HEARTHSTONE_API_KEY)
                .build();

        String response;
        try {
            response = mClient.newCall(request).execute().body().string();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Request failed.");
            // TODO send an error to the client via BroadcastReceiver
            return;
        }

        // Check if the response is in JSON format, send error return otherwise

        JSONObject cardSets;
        try {
            cardSets = new JSONObject(response);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Response is not in JSON format.");
            // TODO send an error to the client via BroadcastReceiver
            return;
        }

        downloadCardData(cardSets);
        endTime = System.currentTimeMillis() - startTime;
        Log.e("CardData", "Finished downloading in " + endTime + " ms.");

        // Get cursor
        String sortOrder = HearthListContract.CardEntry.COLUMN_COST + " ASC";
        mCursor = getContentResolver().query(HearthListContract.CardEntry.CONTENT_URI,
                URL_COLUMNS, null, null, sortOrder);

        startTime = System.currentTimeMillis();
        downloadImages(IMAGE_TYPE_REGULAR);
        endTime = System.currentTimeMillis() - startTime;
        Log.e("RegImages", "Finished downloading in " + endTime + " ms.");

        startTime = System.currentTimeMillis();
        downloadImages(IMAGE_TYPE_GOLD);
        endTime = System.currentTimeMillis() - startTime;
        Log.e("GoldImages", "Finished downloading in " + endTime + " ms.");

        mCursor.close();

        // Store a boolean (for now) so we do not make a second API call after download starts
        SharedPreferences prefs = getSharedPreferences(null, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(getString(R.string.card_download_key), true);
        editor.commit();
    }

    private void downloadCardData(JSONObject cardSets) {

        int numOfCardSets = cardSets.length();
        JSONArray cardSetNames = cardSets.names();

        for (int i=0; i<numOfCardSets; i++) {

            String cardSetName = null;
            try {
                cardSetName = cardSetNames.getString(i);
            } catch (JSONException e) { Log.i(LOG_TAG, "No card set at position " + i); }
            String cardSetNameLower = cardSetName.toLowerCase();

            // Then we can process the cards we want
            JSONArray cardSet = null;
            try {
                cardSet = cardSets.getJSONArray(cardSetName);
            } catch (JSONException e) { Log.i(LOG_TAG, "Could not find cardSet array."); }

            int cardSetLength = cardSet.length();
            Vector<ContentValues> cVVector = new Vector<>(cardSetLength);
            for (int j = 0; j < cardSetLength; j++) {

                JSONObject card = null;
                try {
                    card = cardSet.getJSONObject(j);
                } catch (JSONException e) { Log.i(LOG_TAG, "No card at position " + j); }

                // Always present
                String type = "hero";
                try {
                    type = card.getString(MHS_TYPE);
                } catch (JSONException e) { Log.i(LOG_TAG, "Card has no type."); }
                String typeLower = type.toLowerCase();

                // App does not want heroes
                if (!typeLower.equals("hero")) {

                    // Get the card's name (always present)
                    String name = null;
                    try {
                        name = card.getString(MHS_NAME);
                    } catch (JSONException e) { Log.i(LOG_TAG, "Card has no name."); }

                    // If no playerClass exists, the card is of Neutral class
                    String playerClass = "Neutral";
                    try {
                        playerClass = card.getString(MHS_CLASS);
                    } catch (JSONException e) { Log.i(LOG_TAG, "Card has no class."); }

                    // If no card rarity exists, the card is of Free rarity
                    String rarity = "Free";
                    try {
                        rarity = card.getString(MHS_RARITY);
                    } catch (JSONException e) { Log.i(LOG_TAG, "Card has no rarity."); }

                    // Get the card's mana cost (always present)
                    int cost = -1;
                    try {
                        cost = card.getInt(MHS_COST);
                    } catch (JSONException e) { Log.i(LOG_TAG, "Card has no cost??"); }

                    // Need to check if spell, minion, or weapon
                    // These values will always be present
                    int attack = -1;
                    int health = -1;
                    String race = null;
                    if (!typeLower.equals("spell")) {

                        try {
                            attack = card.getInt(MHS_ATTACK);

                            // WeaponHP = "durability", MinionHP = "health"
                            if (typeLower.equals("minion")) {
                                health = card.getInt(MHS_MINION_HP);

                                // some minions belong to a race
                                try {
                                    race = card.get(MHS_RACE).toString();
                                } catch (NullPointerException|JSONException e) {
                                    Log.i(LOG_TAG, "Card has no race.");
                                }
                            } else {
                                health = card.getInt(MHS_WEAPON_HP);
                            }
                        } catch (JSONException e) {
                            Log.i(LOG_TAG, "Card has values?");
                        }
                    }

                    // Text on a card
                    String text = null;
                    try {
                        text = card.getString(MHS_TEXT);
                    } catch (JSONException e) { Log.i(LOG_TAG, "Card has no text."); }

                    // Card's flavor text, if applicable
                    String flavor = null;
                    try {
                        flavor = card.getString(MHS_FLAVOR);
                    } catch (JSONException e) { Log.i(LOG_TAG, "Card has no flavor text."); }

                    // The name of the card's artist, if applicable
                    String artist = null;
                    try {
                        artist = card.getString(MHS_ARTIST);
                    } catch (JSONException e) { Log.i(LOG_TAG, "Card has no artist."); }

                    String mechanics = null;
                    try {
                        JSONArray mechanicsArr = card.getJSONArray(MHS_MECHANICS);
                        mechanics = mechanicsArr.toString();
                    } catch (JSONException e) { Log.i(LOG_TAG, "Card has no mechanics."); }

                    // Some cards have a HowToGet field
                    String HTG = null;
                    String HTGGold = null;
                    if (cardSetNameLower.equals("basic") ||
                            cardSetNameLower.equals("promotion") ||
                            cardSetNameLower.equals("reward")) {
                        try {
                            HTG = card.getString(MHS_HTG);
                        } catch (JSONException e) { Log.i(LOG_TAG, "No HowToGet info."); }
                        try {
                            HTGGold = card.getString(MHS_HTG_GOLD);
                        } catch (JSONException e) { Log.i(LOG_TAG, "No HowToGetGold info."); }
                    }

                    String imageUrl = null;
                    try {
                        imageUrl = card.getString(MHS_IMG);
                    } catch (JSONException e) { Log.i(LOG_TAG, "No image url."); }

                    String goldImageUrl = null;
                    try {
                        goldImageUrl = card.getString(MHS_GOLD_IMG);
                    } catch (JSONException e) { Log.i(LOG_TAG, "No gold image url."); }

                    // Create ContentValues to store in out db
                    ContentValues cardValues = new ContentValues();

                    cardValues.put(HearthListContract.CardEntry.COLUMN_NAME, name);
                    cardValues.put(HearthListContract.CardEntry.COLUMN_SET, cardSetName);
                    cardValues.put(HearthListContract.CardEntry.COLUMN_TYPE, type);
                    cardValues.put(HearthListContract.CardEntry.COLUMN_RARITY, rarity);
                    cardValues.put(HearthListContract.CardEntry.COLUMN_PLAYER_CLASS, playerClass);
                    cardValues.put(HearthListContract.CardEntry.COLUMN_COST, cost);
                    cardValues.put(HearthListContract.CardEntry.COLUMN_ATTACK, attack);
                    cardValues.put(HearthListContract.CardEntry.COLUMN_HEALTH, health);
                    cardValues.put(HearthListContract.CardEntry.COLUMN_RACE, race);
                    cardValues.put(HearthListContract.CardEntry.COLUMN_TEXT, text);
                    cardValues.put(HearthListContract.CardEntry.COLUMN_FLAVOR, flavor);
                    cardValues.put(HearthListContract.CardEntry.COLUMN_ARTIST, artist);
                    cardValues.put(HearthListContract.CardEntry.COLUMN_MECHANICS, mechanics);
                    cardValues.put(HearthListContract.CardEntry.COLUMN_REG_IMG_URL, imageUrl);
                    cardValues.put(HearthListContract.CardEntry.COLUMN_GOLD_IMG_URL, goldImageUrl);
                    cardValues.put(HearthListContract.CardEntry.COLUMN_HOW_TO_GET, HTG);
                    cardValues.put(HearthListContract.CardEntry.COLUMN_HOW_TO_GET_GOLD, HTGGold);

                    cVVector.add(cardValues);
                }
            }

            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContentResolver().bulkInsert(HearthListContract.CardEntry.CONTENT_ITEM_URI, cvArray);
            }
        }
    }

    private void downloadImages(int imageType) {
        // Reset the cursor to first position
        mCursor.moveToFirst();

        do {
            // TODO DON'T download original size
            Glide.with(this)
                    // Url becomes name of the file
                    .load((imageType == IMAGE_TYPE_REGULAR) ?
                            mCursor.getString(COLUMN_REG_URL) : mCursor.getString(COLUMN_GOLD_URL))
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

        } while (mCursor.moveToNext());
    }
}
