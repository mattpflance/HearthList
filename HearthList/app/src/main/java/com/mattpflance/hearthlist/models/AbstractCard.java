package com.mattpflance.hearthlist.models;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * A Class that stores relevant data for HearthStone cards.
 */
public abstract class AbstractCard {

    protected final int ABSTRACT_CARD_VAR_COUNT = 10;

    protected String mId;
    protected String mName;
    protected String mCardSet;
    protected String mCardRarity;
    protected String mPlayerClass;
    protected boolean mCollectible;
    protected int mManaCost;
    protected String mArtist;
    protected String mText;
    protected String mFlavorText;
    protected List<String> mMechanics;
    protected Bitmap mRegImg;
    protected Bitmap mGoldImg;

    protected AbstractCard(ArrayList<String> params, List<String> mechanics) {
        mId = params.get(0);
        mName = params.get(1);
        mCardSet = params.get(2);
        mCardRarity = params.get(3);
        mPlayerClass = params.get(4);
        mCollectible = Boolean.parseBoolean(params.get(5));
        mManaCost = Integer.parseInt(params.get(6));
        mArtist = params.get(7);
        mText = params.get(8);
        mFlavorText = params.get(9);
        mMechanics = mechanics;
    }

}
