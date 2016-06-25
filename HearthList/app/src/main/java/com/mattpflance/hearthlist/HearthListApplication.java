package com.mattpflance.hearthlist;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.tagmanager.ContainerHolder;
import com.google.android.gms.tagmanager.TagManager;

/**
 * Singleton instance of the Application to store singleton values
 */
public class HearthListApplication extends Application {

    public String mTMCardSet;
    public Tracker mTracker;

    public ContainerHolder mContainerHolder;
    public TagManager mTagManager;

    public TagManager getTagManager() {
        if (mTagManager == null) {
            mTagManager = TagManager.getInstance(this);
        }
        return mTagManager;
    }

    public void setContainerHolder(ContainerHolder containerHolder) {
        mContainerHolder = containerHolder;
    }

    public ContainerHolder getContainerHolder() { return mContainerHolder; }

    public void setTMCardSet(String str) {
        mTMCardSet = str;
    }

    public String getTMCardSet() { return mTMCardSet; }

    public void startTracking() {
        // Create the singleton Tracker
        if (mTracker == null) {
            GoogleAnalytics ga = GoogleAnalytics.getInstance(this);
            mTracker = ga.newTracker(R.xml.track_app);
            ga.enableAutoActivityReports(this);
        }
    }

    public Tracker getTracker() {
        // Make sure tracker exists
        startTracking();
        return mTracker;
    }

}
