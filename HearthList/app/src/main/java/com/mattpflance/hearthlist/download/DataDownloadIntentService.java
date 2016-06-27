package com.mattpflance.hearthlist.download;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.mattpflance.hearthlist.BuildConfig;
import com.mattpflance.hearthlist.R;
import com.mattpflance.hearthlist.Utility;
import com.mattpflance.hearthlist.data.HearthListContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
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
        super("com.mattpflance.hearthlist.download.DataDownloadIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (!Utility.isConnectedToInternet(this)) {
            Intent localIntent = new Intent(DataDownloadResponseReceiver.BROADCAST_ACTION)
                    .putExtra(
                            DataDownloadResponseReceiver.STATUS,
                            DataDownloadResponseReceiver.NO_INTERNET);
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            this.startService(intent);
            return;
        }

        OkHttpClient mClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://omgvamp-hearthstone-v1.p.mashape.com/cards?collectible=1")
                .header("X-Mashape-Key", BuildConfig.MASHAPE_HEARTHSTONE_API_KEY)
                .build();

        String response;
        try {
            response = mClient.newCall(request).execute().body().string();
        } catch (IOException e) {
            Intent localIntent = new Intent(DataDownloadResponseReceiver.BROADCAST_ACTION)
                    .putExtra(
                    DataDownloadResponseReceiver.STATUS,
                    DataDownloadResponseReceiver.REQUEST_ERR);
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            return;
        }

        // Check if the response is in JSON format, send error return otherwise
        JSONObject cardSets;
        try {
            cardSets = new JSONObject(response);
        } catch (JSONException e) {
            Intent localIntent = new Intent(DataDownloadResponseReceiver.BROADCAST_ACTION)
                    .putExtra(
                    DataDownloadResponseReceiver.STATUS,
                    DataDownloadResponseReceiver.JSON_ERR);
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            return;
        }

        downloadCardData(cardSets);

        // Get cursor
        String sortOrder = HearthListContract.CardEntry.COLUMN_COST + " ASC";
        mCursor = getContentResolver().query(HearthListContract.CardEntry.CONTENT_URI,
                URL_COLUMNS, null, null, sortOrder);

        downloadImages(IMAGE_TYPE_REGULAR);
        downloadImages(IMAGE_TYPE_GOLD);

        mCursor.close();

        // Send the intent to register we are downloading and not to download again
        Intent localIntent = new Intent(DataDownloadResponseReceiver.BROADCAST_ACTION)
                    .putExtra(
                    DataDownloadResponseReceiver.STATUS,
                    DataDownloadResponseReceiver.SUCCESS);
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    private void downloadCardData(JSONObject cardSets) {

        int numOfCardSets = cardSets.length();
        JSONArray cardSetNames = cardSets.names();

        int minMana = 0;
        int maxMana = 0;
        HashSet<String> uniqueCardClasses = new HashSet<>();
        HashSet<String> uniqueCardSets = new HashSet<>();
        HashSet<String> uniqueMechanics = new HashSet<>();

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
                        for (int m = 0; m<mechanicsArr.length(); m++) {
                            String mechanic = mechanicsArr.getJSONObject(m).getString(MHS_NAME);
                            if (!mechanic.equals("InvisibleDeathrattle")) {
                                uniqueMechanics.add(mechanic);
                            }
                        }
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

                    // Check for new min and max mana costs
                    if (cost > maxMana) maxMana = cost;
                    else if (cost < minMana) minMana = cost;

                    uniqueCardSets.add(cardSetName);
                    uniqueCardClasses.add(playerClass);
                }
            }

            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContentResolver().bulkInsert(HearthListContract.CardEntry.CONTENT_ITEM_URI, cvArray);
            }
        }
        // Add filtering values to shared preferences
        SharedPreferences prefs = getSharedPreferences(null, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(getString(R.string.min_mana_key), minMana);
        editor.putInt(getString(R.string.max_mana_key), maxMana);
        editor.putStringSet(getString(R.string.card_class_key), uniqueCardClasses);
        editor.putStringSet(getString(R.string.card_sets_key), uniqueCardSets);
        editor.putStringSet(getString(R.string.card_mechanics_key), uniqueMechanics);
        editor.apply();
    }

    private void downloadImages(int imageType) {
        // Reset the cursor to first position
        mCursor.moveToFirst();

        do {
            // TODO DON'T download original size
            //FutureTarget<File> future =
            Glide.with(this)
                    // Url becomes name of the file
                    .load((imageType == IMAGE_TYPE_REGULAR) ?
                            mCursor.getString(COLUMN_REG_URL) : mCursor.getString(COLUMN_GOLD_URL))
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

            // Force a synchronous execution
//            try {
//                future.get();
//            } catch (InterruptedException | ExecutionException e) {
//                e.printStackTrace();
//            }

        } while (mCursor.moveToNext());

//        if (imageType == IMAGE_TYPE_GOLD) {
//            // Successfully downloaded all images!
//            Intent localIntent = new Intent(DataDownloadResponseReceiver.BROADCAST_ACTION)
//                    .putExtra(
//                    DataDownloadResponseReceiver.STATUS,
//                    DataDownloadResponseReceiver.SUCCESS);
//            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
//        }
    }
}
