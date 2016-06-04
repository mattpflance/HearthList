package com.mattpflance.hearthlist.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public class CardFiltersActivity extends AppCompatActivity {

    public static final String SELECTION_ARG_KEY = "SELECT_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        ArrayList<String> selectionArgs = intent.getStringArrayListExtra(SELECTION_ARG_KEY);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, CardFiltersFragment.newInstance(selectionArgs))
                .commit();
    }
}
