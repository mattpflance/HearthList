package com.mattpflance.hearthlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class CardFiltersActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener {

    static final String SELECTION_ARG_KEY = "SELECT_KEY";

    private ArrayList<String> mSelectionArgs;
    private Intent mIntent;

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
            Spinner spinner = (Spinner) findViewById(R.id.class_spinner);
            if (spinner != null) {
                Arrays.sort(classArr);

                // Add 'None' to the top
                ArrayList<String> temp = new ArrayList<>(Arrays.asList(classArr));
                temp.add(0, getString(R.string.no_filter));
                classArr = temp.toArray(new String[length+1]);

                spinner.setOnItemSelectedListener(this);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        classArr);
                spinner.setAdapter(adapter);

                // Set the user selected value
                String selection = mSelectionArgs.get(CardsFragment.ARGS_CLASS);
                spinner.setSelection(selection != null ? temp.indexOf(selection) : 0);
            }
        }

        // Card Sets
        Set<String> cardSet = prefs.getStringSet(getString(R.string.card_sets_key), null);
        if (cardSet != null) {
            int length = cardSet.size();
            String[] cardSetArr = cardSet.toArray(new String[length]);
            Spinner spinner = (Spinner) findViewById(R.id.card_set_spinner);
            if (spinner != null) {
                Arrays.sort(cardSetArr);

                // Add 'None' to the top
                ArrayList<String> temp = new ArrayList<>(Arrays.asList(cardSetArr));
                temp.add(0, getString(R.string.no_filter));
                cardSetArr = temp.toArray(new String[length+1]);

                spinner.setOnItemSelectedListener(this);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        cardSetArr);
                spinner.setAdapter(adapter);

                // Set the user selected value
                String selection = mSelectionArgs.get(CardsFragment.ARGS_CARD_SET);
                spinner.setSelection(selection != null ? temp.indexOf(selection) : 0);
            }

        }

        // Mechanics
        Set<String> mechanicsSet = prefs.getStringSet(getString(R.string.card_mechanics_key), null);
        GridLayout gl = (GridLayout) findViewById(R.id.mechanics_grid_layout);
        if (mechanicsSet != null && gl != null) {
            int length = mechanicsSet.size();
            String[] mechanicsArr = mechanicsSet.toArray(new String[length]);
            Arrays.sort(mechanicsArr);

            String mechanicsStr = mSelectionArgs.get(CardsFragment.ARGS_MECHANICS);

            ArrayList<String> selectedMechanics = null;
            if (mechanicsStr != null)
                    selectedMechanics = new ArrayList<>(Arrays.asList(mechanicsStr.split("-")));

            // TODO optimize how the grid is displayed
            int colWidth = getResources().getDisplayMetrics().widthPixels/(gl.getColumnCount()+1);

            for (int i=0; i<length; i++) {
                CheckBox cb = new CheckBox(this);
                cb.setText(mechanicsArr[i]);
                cb.setScaleX(0.8f);
                cb.setScaleY(0.8f);
                cb.setWidth(colWidth);

                if (selectedMechanics != null && selectedMechanics.contains("%"+mechanicsArr[i]+"%"))
                    cb.setChecked(true);

                cb.setOnCheckedChangeListener(this);
                gl.addView(cb);
            }

        }

        updateActivityResult();
    }

    /**
     * OnItemSelectedListener implementation for Class Spinner
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String value = (String) parent.getItemAtPosition(position);
        if (value != null) {
            value = value.equals(getString(R.string.no_filter)) ? null : value;
            int spinnerId = parent.getId();
            if (spinnerId == R.id.class_spinner) {
                mSelectionArgs.set(CardsFragment.ARGS_CLASS, value);
            } else if (spinnerId == R.id.card_set_spinner) {
                mSelectionArgs.set(CardsFragment.ARGS_CARD_SET, value);
            }
        }
        updateActivityResult();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }

    /**
     * OnCheckedChangeListener implementation for Mechanics
     */
    @Override
    public void onCheckedChanged(CompoundButton checkBox, boolean isChecked) {
        String mechanicsStr = mSelectionArgs.get(CardsFragment.ARGS_MECHANICS);
        // Put string into ArrayList to work with
        ArrayList<String> mechanics;
        if (mechanicsStr == null) {
            mechanics = new ArrayList<>();
        }  else {
            mechanics = new ArrayList<>(Arrays.asList(mechanicsStr.split("-")));
        }

        // Add/Remove the mechanic that was checked/unchecked
        String mechanic = "%" + checkBox.getText().toString() + "%";
        if (isChecked) {
            mechanics.add(mechanic);
        } else {
            mechanics.remove(mechanic);
        }

        // Convert back to String
        String prefix = "";
        StringBuilder builder = null;
        if (mechanics.size() > 0) {
            builder = new StringBuilder();
            for (String m : mechanics) {
                builder.append(prefix);
                prefix = "-";
                builder.append(m);
            }
        }

        mSelectionArgs.set(CardsFragment.ARGS_MECHANICS,
                (builder == null) ? null : builder.toString());
        updateActivityResult();
    }

    /**
     * Helper method to send an update to this activity's result whenever a change is made
     */
    private void updateActivityResult() {
        mIntent.putExtra(CardFiltersActivity.SELECTION_ARG_KEY, mSelectionArgs);
        setResult(RESULT_OK, mIntent);
    }
}
