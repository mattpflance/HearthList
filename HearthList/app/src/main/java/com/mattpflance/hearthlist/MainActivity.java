package com.mattpflance.hearthlist;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.mattpflance.hearthlist.data.HearthListContract;
import com.mattpflance.hearthlist.data.HearthListDbHelper;
import com.mattpflance.hearthlist.models.Card;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements
        PagerFragment.OnFragmentInteractionListener,
        CardsFragment.OnFragmentInteractionListener,
        DecksFragment.OnFragmentInteractionListener {

    private OkHttpClient mClient;
    private PagerAdapter mPagerAdapter;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private PagerFragment mPagerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mClient = new OkHttpClient();

        try {
            getCardsAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mPagerFragment = (PagerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.view_pager_fragment);

        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), this);

        mViewPager = (ViewPager) mPagerFragment.getView();
        mViewPager.setAdapter(mPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.setupWithViewPager(mViewPager);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }

    }

    private void getCardsAsync() throws IOException {
        Request request = new Request.Builder()
                .url("https://omgvamp-hearthstone-v1.p.mashape.com/cards")
                .header("X-Mashape-Key", BuildConfig.MASHAPE_HEARTHSTONE_API_KEY)
                .build();

        mClient.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                        LinkedTreeMap cardSets = new Gson().fromJson(response.body().charStream(), LinkedTreeMap.class);

                        storeIntoProvider(cardSets);

                        // TODO Store value into SharedPreferences to stop another API call

                    }
                });
    }

    private void storeIntoProvider(LinkedTreeMap cardSets) throws NullPointerException {

        final String LOG_TAG = "StoreIntoProvider";

        // First, drop the table if it exists
        new HearthListDbHelper(this).clearTable(HearthListContract.CardEntry.TABLE_NAME);

        // Names of JSON args needed
        final String MHS_NAME = "name";
        //final String MHS_SET = "cardSet"; // Not needed since we get the array of card sets
        final String MHS_TYPE = "type";
        final String MHS_RARITY = "rarity";
        final String MHS_COLLECT = "collectible";
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
        final String MHS_IMG = "img";
        final String MHS_GOLD_IMG = "imgGold";
        final String MHS_HTG = "howToGet";
        final String MHS_HTG_GOLD = "howToGetGold";

        for (Object entry : cardSets.entrySet()) {
            // Check for meaningful card sets only
            LinkedTreeMap.Entry cardSet = (LinkedTreeMap.Entry) entry;
            String cardSetName = cardSet.getKey().toString();
            String cardSetNameLower = cardSetName.toLowerCase();
            if (!(cardSetNameLower.equals("credits") || cardSetNameLower.equals("debug") ||
                  cardSetNameLower.equals("missions") || cardSetNameLower.equals("hero skins") ||
                  cardSetNameLower.equals("tavern brawl"))) {

                // Then we can process the cards we want
                ArrayList cards = (ArrayList) cardSet.getValue();
                Vector<ContentValues> cVVector = new Vector<>(cards.size());

                for (Object cardSetCard : cards) {

                    LinkedTreeMap card = (LinkedTreeMap) cardSetCard;

                    /**
                     * We only care if the card is collectible and a minion, spell, or weapon
                     * Unfortunately, the API is messy and try/catch blocks are needed everywhere..
                     */

                    // Not always present
                    boolean collectible = false;
                    try {
                        collectible = Boolean.parseBoolean(card.get(MHS_COLLECT).toString());
                    } catch (NullPointerException e) { Log.i(LOG_TAG, "No field found."); }

                    // Always present
                    String type = card.get(MHS_TYPE).toString();
                    String typeLower = type.toLowerCase();
                    if (collectible && !typeLower.equals("hero")) {
                        // Get our values to store
                        String name = card.get(MHS_NAME).toString();

                        // Not always present
                        String playerClass = "Neutral";
                        try {
                            playerClass = card.get(MHS_CLASS).toString();
                        } catch (NullPointerException e) { Log.i(LOG_TAG, "No field found."); }

                        // Not always present
                        String rarity = "Free";
                        try {
                            rarity = card.get(MHS_RARITY).toString();
                        } catch (NullPointerException e) { Log.i(LOG_TAG, "No field found."); }

                        int cost = (int) Double.parseDouble(card.get(MHS_COST).toString());
                        int attack = -1;
                        int health = -1;
                        String race = null;
                        if (!typeLower.equals("spell")) {
                            attack = (int) Double.parseDouble(card.get(MHS_ATTACK).toString());
                            if (typeLower.equals("minion")) {
                                health = (int) Double.parseDouble(card.get(MHS_MINION_HP).toString());

                                try {
                                    race = card.get(MHS_RACE).toString();
                                } catch (NullPointerException e) { Log.i(LOG_TAG, "No field found."); }
                            } else {
                                health = (int) Double.parseDouble(card.get(MHS_WEAPON_HP).toString());
                            }
                        }

                        String text = "";
                        try {
                            text = card.get(MHS_TEXT).toString();
                        } catch (NullPointerException e) { Log.i(LOG_TAG, "No field found."); }

                        String flavor = "";
                        try {
                            flavor = card.get(MHS_FLAVOR).toString();
                        } catch (NullPointerException e) { Log.i(LOG_TAG, "No field found."); }

                        String artist = "";
                        try {
                            artist = card.get(MHS_ARTIST).toString();
                        } catch (NullPointerException e) { Log.i(LOG_TAG, "No field found."); }

                        //String mechanics = card.getJSONArray(MHS_MECHANICS);
                        String HTG = null;
                        String HTGGold = null;
                        if (cardSetNameLower.equals("basic") ||
                                cardSetNameLower.equals("promotion") ||
                                cardSetNameLower.equals("reward")) {
                            try {
                                HTG = card.get(MHS_HTG).toString();
                            } catch (NullPointerException e) { Log.i(LOG_TAG, "No field found."); }
                            try {
                                HTGGold = card.get(MHS_HTG_GOLD).toString();
                            } catch (NullPointerException e) { Log.i(LOG_TAG, "No field found."); }
                        }
                                String bitmapUrl;
                                byte[] image = null;
                                try {
                                    bitmapUrl = card.get(MHS_IMG).toString();
                                    image = Glide.with(this)
                                            .load(bitmapUrl)
                                            .asBitmap()
                                            .toBytes()
                                            .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                            .get();
                                } catch (ExecutionException|InterruptedException e) { Log.e(LOG_TAG, "Error: " + e);
                                } catch (NullPointerException npe) { Log.i(LOG_TAG, "No field found."); }

// TODO Download goldImages after the cards list is complete
//                                String goldBitmapUrl = card.getString(MHS_GOLD_IMG);
//                                byte[] goldImage = null;
//                                try {
//                                    goldImage = Glide.with(this)
//                                            .load(goldBitmapUrl)
//                                            .asGif()
//                                            .toBytes()
//                                            .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                                            .get();
//                                } catch (ExecutionException ee) {
//                                    ee.printStackTrace();
//                                } catch (InterruptedException ie) {
//                                    ie.printStackTrace();
//                                }
                        // Create ContentValues to store in out db
                        ContentValues cardValues = new ContentValues();

                        cardValues.put(HearthListContract.CardEntry.COLUMN_NAME, name);
                        cardValues.put(HearthListContract.CardEntry.COLUMN_SET, cardSetName);
                        cardValues.put(HearthListContract.CardEntry.COLUMN_TYPE, type);
                        cardValues.put(HearthListContract.CardEntry.COLUMN_RARITY, rarity);
                        cardValues.put(HearthListContract.CardEntry.COLUMN_COLLECT, collectible);
                        cardValues.put(HearthListContract.CardEntry.COLUMN_PLAYER_CLASS, playerClass);
                        cardValues.put(HearthListContract.CardEntry.COLUMN_COST, cost);
                        cardValues.put(HearthListContract.CardEntry.COLUMN_ATTACK, attack);
                        cardValues.put(HearthListContract.CardEntry.COLUMN_HEALTH, health);
                        cardValues.put(HearthListContract.CardEntry.COLUMN_RACE, race);
                        cardValues.put(HearthListContract.CardEntry.COLUMN_TEXT, text);
                        cardValues.put(HearthListContract.CardEntry.COLUMN_FLAVOR, flavor);
                        cardValues.put(HearthListContract.CardEntry.COLUMN_ARTIST, artist);
                        cardValues.put(HearthListContract.CardEntry.COLUMN_HOW_TO_GET, HTG);
                        cardValues.put(HearthListContract.CardEntry.COLUMN_HOW_TO_GET_GOLD, HTGGold);
                        cardValues.put(HearthListContract.CardEntry.COLUMN_REG_IMG, image);
//                        cardValues.put(HearthListContract.CardEntry.COLUMN_GOLD_IMG, goldImage);

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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri){

    }

    static class CardSets {
        Map<String, Card[]> cardSetMap;
    }

}
