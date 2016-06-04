package com.mattpflance.hearthlist.settings;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class CardFiltersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new CardFiltersFragment())
                .commit();
    }
}
