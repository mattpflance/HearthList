package com.mattpflance.hearthlist;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mattpflance.hearthlist.data.HearthListContract;
import com.mattpflance.hearthlist.models.Card;

public class CardDetailsActivity extends AppCompatActivity {

    public static String CARD_ARG_ID = "CARD_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);

        if (findViewById(R.id.card_details_fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            CardDetailsFragment cardDetailsFragment = CardDetailsFragment.newInstance();

            // Create a Card
            Card card = getIntent().getParcelableExtra(CARD_ARG_ID);

            Bundle bundle = new Bundle();
            bundle.putParcelable(CARD_ARG_ID, card);

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            cardDetailsFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.card_details_fragment_container, cardDetailsFragment).commit();

        }
    }
}
