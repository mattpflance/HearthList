package com.mattpflance.hearthlist;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private final int CARDS_TAB = 0;
    private final int DECKS_TAB = 1;

    public static final int NUM_PAGES = 2;

    private Context mContext;
    private CardsFragment mCardsFragment;
    private DecksFragment mDecksFragment;

    public PagerAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case CARDS_TAB:
                if (mCardsFragment == null) {
                    SharedPreferences prefs = mContext.getSharedPreferences(null, Context.MODE_PRIVATE);
                    int minMana = prefs.getInt(mContext.getString(R.string.min_mana_key), 0);
                    int maxMana = prefs.getInt(mContext.getString(R.string.max_mana_key), 99);
                    mCardsFragment = CardsFragment.newInstance(minMana, maxMana);
                }
                return mCardsFragment;
            case DECKS_TAB:
                if (mDecksFragment == null) mDecksFragment = DecksFragment.newInstance();
                return mDecksFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() { return NUM_PAGES; }

    /**
     * Return the tab's name
     */
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case CARDS_TAB:
                return mContext.getString(R.string.tab_layout_cards);
            case DECKS_TAB:
                return mContext.getString(R.string.tab_layout_decks);
            default:
                return mContext.getString(R.string.tab_layout_error);
        }
    }
}