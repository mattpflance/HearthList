package com.mattpflance.hearthlist.models;

import java.util.ArrayList;
import java.util.List;

public class Minion extends AbstractCard {

    private String mRace;
    private int mAttack;
    private int mHealth;

    public Minion(ArrayList<String> params, List<String> mechanics){
        super(params, mechanics);

        mRace = params.get(ABSTRACT_CARD_VAR_COUNT);
        mAttack = Integer.parseInt(params.get(1 + ABSTRACT_CARD_VAR_COUNT));
        mHealth = Integer.parseInt(params.get(2 + ABSTRACT_CARD_VAR_COUNT));
    }

}
