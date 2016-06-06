package com.mattpflance.hearthlist.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import com.mattpflance.hearthlist.CardsFragment;
import com.mattpflance.hearthlist.R;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class CardFiltersDialog extends DialogFragment implements
        CompoundButton.OnCheckedChangeListener {

    public static final int FILTER_CODE = 0;
    public static final String SELECTION_ARG_KEY = "SELECT_KEY";

    private ArrayList<String> mSelectionArgs;

    private RangeSeekBar mSeekBar;
    private Spinner mClassSpinner;
    private Spinner mCardSetSpinner;

    public static CardFiltersDialog newInstance(ArrayList<String> selectionArgs) {
        CardFiltersDialog f = new CardFiltersDialog();
        Bundle args = new Bundle();
        args.putStringArrayList(SELECTION_ARG_KEY, selectionArgs);
        f.setArguments(args);
        return f;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // First inflate the view we need to setup the custom view
        final View customView = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_card_filters, null);

        mSelectionArgs = getArguments().getStringArrayList(SELECTION_ARG_KEY);

        // Get our filters from SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences(null, Context.MODE_PRIVATE);

        // Mana Cost
        mSeekBar = (RangeSeekBar) customView.findViewById(R.id.mana_range_seek_bar);
        if (mSeekBar != null) {
            // Set min/max mana costs
            int minMana = prefs.getInt(getString(R.string.min_mana_key), 0);
            int maxMana = prefs.getInt(getString(R.string.max_mana_key), 99);
            mSeekBar.setRangeValues(minMana, maxMana);
            mSeekBar.setTextAboveThumbsColor(R.color.black);
            // Set currently selected filters
            mSeekBar.setSelectedMinValue(Integer.parseInt(mSelectionArgs.get(CardsFragment.ARGS_MIN_MANA)));
            mSeekBar.setSelectedMaxValue(Integer.parseInt(mSelectionArgs.get(CardsFragment.ARGS_MAX_MANA)));
        }

        // Classes
        Set<String> classSet = prefs.getStringSet(getString(R.string.card_class_key), null);
        mClassSpinner = (Spinner) customView.findViewById(R.id.class_spinner);
        if (classSet != null && mClassSpinner != null) {
            int length = classSet.size();
            String[] classArr = classSet.toArray(new String[length]);
            Arrays.sort(classArr);

            // Add 'None' to the top
            ArrayList<String> temp = new ArrayList<>(Arrays.asList(classArr));
            temp.add(0, getString(R.string.no_filter));
            classArr = temp.toArray(new String[length+1]);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    classArr);
            mClassSpinner.setAdapter(adapter);

            // Set the user selected value
            String selection = mSelectionArgs.get(CardsFragment.ARGS_CLASS);
            mClassSpinner.setSelection(selection != null ? temp.indexOf(selection) : 0);
        }

        // Card Sets
        Set<String> cardSet = prefs.getStringSet(getString(R.string.card_sets_key), null);
        mCardSetSpinner = (Spinner) customView.findViewById(R.id.card_set_spinner);
        if (cardSet != null && mCardSetSpinner != null) {
            int length = cardSet.size();
            String[] cardSetArr = cardSet.toArray(new String[length]);
            Arrays.sort(cardSetArr);

            // Add 'None' to the top
            ArrayList<String> temp = new ArrayList<>(Arrays.asList(cardSetArr));
            temp.add(0, getString(R.string.no_filter));
            cardSetArr = temp.toArray(new String[length+1]);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    cardSetArr);
            mCardSetSpinner.setAdapter(adapter);

            // Set the user selected value
            String selection = mSelectionArgs.get(CardsFragment.ARGS_CARD_SET);
            mCardSetSpinner.setSelection(selection != null ? temp.indexOf(selection) : 0);
        }

        // Mechanics
        Set<String> mechanicsSet = prefs.getStringSet(getString(R.string.card_mechanics_key), null);
        GridLayout gl = (GridLayout) customView.findViewById(R.id.mechanics_grid_layout);
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
                CheckBox cb = new CheckBox(customView.getContext());
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

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.title_card_filters))
                .setPositiveButton(getString(R.string.label_cards_positive),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                returnData();
                            }
                        })
                .setNegativeButton(getString(R.string.label_cards_negative),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        })
                .setView(customView);

        return builder.create();
    }

    private void returnData() {
        // Put all data in the SelectionArgs
        mSelectionArgs.set(CardsFragment.ARGS_MIN_MANA, mSeekBar.getSelectedMinValue().toString());
        mSelectionArgs.set(CardsFragment.ARGS_MAX_MANA, mSeekBar.getSelectedMaxValue().toString());
        mSelectionArgs.set(CardsFragment.ARGS_CLASS, checkForNull(mClassSpinner.getSelectedItem().toString()));
        mSelectionArgs.set(CardsFragment.ARGS_CARD_SET, checkForNull(mCardSetSpinner.getSelectedItem().toString()));
        // Bundle back to CardsFragment
        Intent intent = new Intent();
        intent.putExtra(SELECTION_ARG_KEY, mSelectionArgs);
        getTargetFragment().onActivityResult(
                getTargetRequestCode(), FILTER_CODE, intent);
    }

    private String checkForNull(String str) {
        return str.equals("None") ? null : str;
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
    }
}
