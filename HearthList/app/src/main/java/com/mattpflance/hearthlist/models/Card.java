package com.mattpflance.hearthlist.models;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * A Class that stores relevant data for HearthStone cards.
 */
public class Card {

    private String mName;
    private String mCardSet;
    private String mCardType;
    private String mCardRarity;
    private boolean mCollectible;
    private String mPlayerClass;
    private int mManaCost;
    private String mRace;
    private int mAttack;
    private int mHealth;
    private String mArtist;
    private String mText;
    private String mFlavorText;
    private String mHowToGet;
    private String mHowToGetGold;
    private List<String> mMechanics;
    //private Bitmap mRegImg;
    //private Bitmap mGoldImg;

    public Card(ArrayList<String> params, List<String> mechanics) {
        mName = params.get(0);
        mCardSet = params.get(1);
        mCardType = params.get(2);
        mCardRarity = params.get(3);
        mCollectible = Boolean.parseBoolean(params.get(4));
        mPlayerClass = params.get(5);
        mManaCost = Integer.parseInt(params.get(6));
        mRace = params.get(7);
        mAttack = Integer.parseInt(params.get(8));
        mHealth = Integer.parseInt(params.get(9));
        mArtist = params.get(10);
        mText = params.get(11);
        mFlavorText = params.get(12);
        mHowToGet = params.get(13);
        mHowToGetGold = params.get(14);
        mMechanics = mechanics;
    }

}
