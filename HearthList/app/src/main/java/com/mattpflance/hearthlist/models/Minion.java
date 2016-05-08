package com.mattpflance.hearthlist.models;

import java.util.ArrayList;
import java.util.List;

public class Minion extends AbstractCard {

    private String mArtist;
    private String mText;
    private String mFlavorText;
    private String mRace;
    private int mAttack;
    private int mHealth;
    private List<String> mMechanics;

    public Minion(ArrayList<String> params){
        super(params);

        mArtist = params.get(ABSTRACT_CARD_VAR_COUNT);
        mText = params.get(1 + ABSTRACT_CARD_VAR_COUNT);
        mFlavorText = params.get(2 + ABSTRACT_CARD_VAR_COUNT);
        mRace = params.get(3 + ABSTRACT_CARD_VAR_COUNT);
        mAttack = Integer.parseInt(params.get(4 + ABSTRACT_CARD_VAR_COUNT));
        mHealth = Integer.parseInt(params.get(5 + ABSTRACT_CARD_VAR_COUNT));
        mMechanics = params.subList(6 + ABSTRACT_CARD_VAR_COUNT, params.size());
    }

}
