package com.mattpflance.hearthlist.models;

import com.mattpflance.hearthlist.enums.CardRarity;
import com.mattpflance.hearthlist.enums.PlayerClass;

import java.util.ArrayList;

/**
 * A Class that stores relevant data for HearthStone cards.
 */
public abstract class AbstractCard {

    protected String mId;
    protected String mName;
    protected String mCardSet;
    protected CardRarity mCardRarity;
    protected PlayerClass mPlayerClass;

    protected AbstractCard(ArrayList<String> params) {
        mId = params.get(0);
        mName = params.get(1);
        mCardSet = params.get(2);
        mCardRarity = CardRarity.values()[Integer.parseInt(params.get(3))];
        mPlayerClass = PlayerClass.values()[Integer.parseInt(params.get(4))];
    }

}
