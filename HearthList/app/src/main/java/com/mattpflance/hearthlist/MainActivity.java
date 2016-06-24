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

        // Check SharedPreferences
        SharedPreferences prefs = getSharedPreferences(null, Context.MODE_PRIVATE);
        boolean alreadyDownloaded = prefs.getBoolean(getString(R.string.card_download_key), false);
        if (!alreadyDownloaded) {
            startService(new Intent(this, DataDownloadIntentService.class));
        }

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
            }
        }, 2, TimeUnit.SECONDS);
    }

}
