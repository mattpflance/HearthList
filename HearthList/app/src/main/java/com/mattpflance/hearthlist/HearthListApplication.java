package com.mattpflance.hearthlist;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Singleton instance of the Application to store singleton values
 */
public class HearthListApplication extends Application {

    public Tracker mTracker;

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
