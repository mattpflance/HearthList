package com.mattpflance.hearthlist.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.mattpflance.hearthlist.R;

import java.util.Arrays;
import java.util.Set;

public class DeckCreationDialog extends DialogFragment {

    public static final int CREATION_CODE = 1;
    public static final String DECK_NAME_KEY = "Deck_Name_Key";
    public static final String DECK_CLASS_KEY = "Deck_Class_Key";

    private Spinner mSpinner;
    private EditText mEditText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // First inflate the view we need to setup the custom view
        final View customView = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_deck_creation, null);

        // Find views
        mEditText = (EditText) customView.findViewById(R.id.deck_name_edit_text);

        mSpinner = (Spinner) customView.findViewById(R.id.class_spinner);

        // Set the Spinner values
        SharedPreferences prefs = getActivity().getSharedPreferences(null, Context.MODE_PRIVATE);
        Set<String> classSet = prefs.getStringSet(getString(R.string.card_class_key), null);
        if (classSet != null) {
            classSet.remove(getString(R.string.neutral));
            int length = classSet.size();
            String[] classArr = classSet.toArray(new String[length]);
            if (mSpinner != null) {
                Arrays.sort(classArr);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        getContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        classArr);
                mSpinner.setAdapter(adapter);
            }
        }

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.title_deck_creation))
                .setPositiveButton(getString(R.string.label_deck_positive),
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        returnData();
                    }
                })
                .setNegativeButton(getString(R.string.label_deck_negative),
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                })
                .setView(customView);

        return builder.create();
    }

    private void returnData() {
        Intent intent = new Intent();
        String deckName = mEditText.getText().toString();
        intent.putExtra(DECK_NAME_KEY, (deckName.length() > 0) ? deckName : getString(R.string.default_deck_name));
        intent.putExtra(DECK_CLASS_KEY, (String) mSpinner.getSelectedItem());
        getTargetFragment().onActivityResult(
                getTargetRequestCode(), CREATION_CODE, intent);
    }
}
