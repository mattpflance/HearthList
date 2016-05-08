package com.mattpflance.hearthlist.models;

import java.util.ArrayList;

/**
 * A Class that stores relevant data for HearthStone cards.
 */
public abstract class AbstractCard {

    protected final int ABSTRACT_CARD_VAR_COUNT = 6;

    protected String mId;
    protected String mName;
    protected String mCardSet;
    protected String mCardRarity;
    protected String mPlayerClass;
    protected boolean mCollectible;

    protected AbstractCard(ArrayList<String> params) {
        mId = params.get(0);
        mName = params.get(1);
        mCardSet = params.get(2);
        mCardRarity = params.get(3);
        mPlayerClass = params.get(4);
        mCollectible = Boolean.parseBoolean(params.get(5));
    }

}
