package com.mattpflance.hearthlist;

import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;

import com.mattpflance.hearthlist.models.Card;

public class CardDetailsActivity extends AppCompatActivity {

    public static String CARD_ARG_ID = "CARD_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);

        // Get the card and setup the ActionBar
        Card card = getIntent().getParcelableExtra(CARD_ARG_ID);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(card.getName());
        }

        // Add the fragment if its the first time
        if (savedInstanceState == null && findViewById(R.id.card_details_fragment_container) != null) {

            // Create a new Fragment to be placed in the activity layout
            CardDetailsFragment cardDetailsFragment = CardDetailsFragment.newInstance();

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

    // This is overridden to the Up button can act as the System Back button
    // such that the user is brought to the same spot on the list that they were
    // previously at
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.bottom_to_top);
    }

}
