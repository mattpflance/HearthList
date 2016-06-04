package com.mattpflance.hearthlist.settings;


import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mattpflance.hearthlist.R;

public class CardFiltersFragment extends PreferenceFragment {

//    public static CardFiltersFragment newInstance(String param1, String param2) {
//        CardFiltersFragment fragment = new CardFiltersFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.cards_filter_preferences);

        // TODO Dynamically add options from SharedPreferences

    }

}
