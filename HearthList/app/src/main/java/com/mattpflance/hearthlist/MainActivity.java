package com.mattpflance.hearthlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements
        PagerFragment.OnFragmentInteractionListener,
        CardsFragment.OnFragmentInteractionListener,
        DecksFragment.OnFragmentInteractionListener {

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

        // Check SharedPreferences
        SharedPreferences prefs = getSharedPreferences(null, Context.MODE_PRIVATE);
        boolean alreadyDownloaded = prefs.getBoolean(getString(R.string.card_download_key), false);
        if (!alreadyDownloaded) {
            startService(new Intent(this, DataDownloadIntentService.class));
        }

        mPagerFragment = (PagerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.view_pager_fragment);

        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), this);

        mViewPager = (ViewPager) mPagerFragment.getView();
        mViewPager.setAdapter(mPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.setupWithViewPager(mViewPager);


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        if (fab != null) {
//            fab.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//                }
//            });
//        }

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
}
