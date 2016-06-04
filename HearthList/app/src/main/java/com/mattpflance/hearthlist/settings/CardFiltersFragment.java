package com.mattpflance.hearthlist.settings;

import android.os.Bundle;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.mattpflance.hearthlist.CardsFragment;
import com.mattpflance.hearthlist.R;

import java.util.ArrayList;

public class CardFiltersFragment extends PreferenceFragment {

    CardFiltersCallback mCallback;

    private static ArrayList<String> mSelectionArgs;

    /**
     * Fragment callback to communicate between the CardsFragment
     */
    public interface CardFiltersCallback {
        void setFilterSelectionArgs(ArrayList<String> selectionArgs);
    }

    public CardFiltersFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static CardFiltersFragment newInstance(ArrayList<String> selectionArgs) {
        CardFiltersFragment fragment = new CardFiltersFragment();
        mSelectionArgs = selectionArgs;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            mCallback = CardsFragment.getInstance();
        }

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.cards_filter_preferences);

        // TODO Dynamically add options from SharedPreferences and update with mSelectionArgs
        PreferenceScreen prefScreen = getPreferenceScreen();
        PreferenceCategory prefCat = (PreferenceCategory)
                prefScreen.findPreference(getString(R.string.title_mana_cost_filters));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Just before leaving the fragment update the selection args
        mCallback.setFilterSelectionArgs(mSelectionArgs);
    }

}
