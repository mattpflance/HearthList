package com.mattpflance.hearthlist.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.mattpflance.hearthlist.R;

public class CardFiltersFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.cards_filter_preferences);

        // TODO Dynamically add options from SharedPreferences

    }

}
