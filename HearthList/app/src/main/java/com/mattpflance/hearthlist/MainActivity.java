package com.mattpflance.hearthlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tagmanager.ContainerHolder;
import com.google.android.gms.tagmanager.TagManager;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    TagManager mTagManager;

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
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    private void loadTMContainer() {
        mTagManager = ((HearthListApplication) getApplication()).getTagManager();

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

                // Load container holder
                ((HearthListApplication) getApplication()).setContainerHolder(containerHolder);

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

        // Gets whatever CardSet the app is up-to-date
        SharedPreferences prefs = getSharedPreferences(null, Context.MODE_PRIVATE);
        String cardSetDownloaded = prefs.getString(getString(R.string.card_download_key), "");

        // Check TagManager to see what the current CardSet is
        ContainerHolder holder = ((HearthListApplication) getApplication()).getContainerHolder();
        String currentCardSet = holder.getContainer()
                .getString(getString(R.string.tag_manager_update_key));

        // If the Strings are not the same, we need to download new data!
        if (!(currentCardSet.equals(cardSetDownloaded))) {
            Log.d("checkForAutoUpdate", "Downloading new cards!");
            startService(new Intent(this, DataDownloadIntentService.class));
        }
    }

}
