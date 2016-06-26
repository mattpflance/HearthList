package com.mattpflance.hearthlist;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tagmanager.ContainerHolder;
import com.google.android.gms.tagmanager.TagManager;
import com.mattpflance.hearthlist.data.HearthListDbHelper;
import com.mattpflance.hearthlist.download.DataDownloadIntentService;
import com.mattpflance.hearthlist.download.DataDownloadResponseReceiver;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    TagManager mTagManager;

    private AdView mBannerAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Track for Analytics
        ((HearthListApplication) getApplication()).startTracking();

        // Set up TagManager & ContainerHolder
        loadTMContainer();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), this);
        if (viewPager != null)
            viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        if (tabLayout != null) {
            tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
            tabLayout.setupWithViewPager(viewPager, true);
        }

        mBannerAd = (AdView) findViewById(R.id.banner_ad);
        if (mBannerAd != null) {
            AdRequest request = new AdRequest.Builder().build();
            mBannerAd.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(int i) {
                    mBannerAd.setVisibility(View.GONE);
                    super.onAdFailedToLoad(i);
                }

                @Override
                public void onAdLoaded() {
                    mBannerAd.setVisibility(View.VISIBLE);
                    super.onAdLoaded();
                }

                @Override
                public void onAdOpened() {
                    // TODO add analytic tracking
                    super.onAdOpened();
                }
            });
            mBannerAd.loadAd(request);
            mBannerAd.setVisibility(View.GONE);
        }
    }

    private void loadTMContainer() {
        final HearthListApplication myApp = ((HearthListApplication) getApplication());

        mTagManager = myApp.getTagManager();

        PendingResult pendingResult =
                mTagManager.loadContainerPreferFresh(getString(R.string.gtm_default_string),
                        R.raw.gtm_default);

        pendingResult.setResultCallback(new ResultCallback<ContainerHolder>() {
            @Override
            public void onResult(@NonNull ContainerHolder containerHolder) {
                // Check for error
                if (!containerHolder.getStatus().isSuccess()) {
                    // error
                    return;
                }

                containerHolder.refresh();

                // Set the card
                String cardSet = containerHolder.getContainer()
                        .getString(getString(R.string.tag_manager_update_key));
                myApp.setTMCardSet(cardSet);

                // Load container holder
                myApp.setContainerHolder(containerHolder);

                // Auto update cards if needed
                checkForAutoUpdate();
            }
        }, 2, TimeUnit.SECONDS);
    }

    /**
     * Helper function that checks Tag Manager and SharedPrefs to see
     * if the app needs to start the DataDownloadIntentService to get
     * new cards.
     */
    private void checkForAutoUpdate() {

        HearthListApplication myApp = ((HearthListApplication) getApplication());

        // Gets whatever CardSet the app is up-to-date
        SharedPreferences prefs = getSharedPreferences(null, Context.MODE_PRIVATE);
        String cardSetDownloaded = prefs.getString(getString(R.string.card_download_key), "");

        // If the Strings are not the same, we need to download new data!
        if (!(myApp.getTMCardSet().equals(cardSetDownloaded))) {

            /**
             * Before we start the intent service, specify an intent filter for the BroadcastReceiver
             */

            // Create intent filter and receiver
            IntentFilter statusIntentFilter = new IntentFilter(
                    DataDownloadResponseReceiver.BROADCAST_ACTION);
            DataDownloadResponseReceiver responseReceiver = new DataDownloadResponseReceiver();

            // Register intent filter and receiver with local broadcast manager
            LocalBroadcastManager.getInstance(this).registerReceiver(
                    responseReceiver,
                    statusIntentFilter);

            // Now start the service
            startService(new Intent(this, DataDownloadIntentService.class));
        }
    }

}
