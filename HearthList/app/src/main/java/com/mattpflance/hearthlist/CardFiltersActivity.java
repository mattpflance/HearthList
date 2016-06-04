package com.mattpflance.hearthlist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;
import java.util.Set;

public class CardFiltersActivity extends AppCompatActivity {

    private ArrayList<String> mSelectionArgs;
    private Intent mIntent;

    public static final String SELECTION_ARG_KEY = "SELECT_KEY";

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_filters);

        mIntent = getIntent();
        mSelectionArgs = mIntent.getStringArrayListExtra(SELECTION_ARG_KEY);

        // Get our filters from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(null, Context.MODE_PRIVATE);

        // Mana Cost
        RangeSeekBar<Integer> seekBar = (RangeSeekBar) findViewById(R.id.mana_range_seek_bar);
        if (seekBar != null) {
            seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
                @Override
                public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                    mSelectionArgs.set(CardsFragment.ARGS_MIN_MANA, ""+minValue);
                    mSelectionArgs.set(CardsFragment.ARGS_MAX_MANA, ""+maxValue);

                    updateActivityResult();
                }
            });
            int minMana = prefs.getInt(getString(R.string.min_mana_key), 0);
            int maxMana = prefs.getInt(getString(R.string.max_mana_key), 99);
            seekBar.setRangeValues(minMana, maxMana);
            seekBar.setTextAboveThumbsColor(R.color.black);
            seekBar.setSelectedMinValue(Integer.parseInt(mSelectionArgs.get(CardsFragment.ARGS_MIN_MANA)));
            seekBar.setSelectedMaxValue(Integer.parseInt(mSelectionArgs.get(CardsFragment.ARGS_MAX_MANA)));
        }

        // Classes
        Set<String> classSet = prefs.getStringSet(getString(R.string.card_class_key), null);
        if (classSet != null) {
            int length = classSet.size();
            String[] classArr = classSet.toArray(new String[length]);
            // TODO Add classes

        }

        // Card Sets
        Set<String> cardSet = prefs.getStringSet(getString(R.string.card_sets_key), null);
        if (cardSet != null) {
            int length = cardSet.size();
            String[] cardSetArr = cardSet.toArray(new String[length]);
            // TODO Add card sets

        }

        // Mechanics
        Set<String> mechanicsSet = prefs.getStringSet(getString(R.string.card_mechanics_key), null);
        if (mechanicsSet != null) {
            int length = mechanicsSet.size();
            String[] mechanicsArr = mechanicsSet.toArray(new String[length]);
            // TODO Add mechanics

        }

        updateActivityResult();
    }

    private void updateActivityResult() {
        mIntent.putExtra(CardFiltersActivity.SELECTION_ARG_KEY, mSelectionArgs);
        setResult(RESULT_OK, mIntent);
    }
}
