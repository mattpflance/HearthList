package com.mattpflance.hearthlist.models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.mattpflance.hearthlist.data.HearthListContract;

import java.util.ArrayList;

/**
 * A HearthStone card
 */
public class Card implements Parcelable {

    // Can possibly use Tag Manager to update these values
    public static final int MIN_MANA_COST = 0;
    public static final int MAX_MANA_COST = 25;

    // Only want to show a subset of the data
    public static final String[] CARD_COLUMNS = {
            HearthListContract.CardEntry.COLUMN_NAME,
            HearthListContract.CardEntry.COLUMN_SET,
            HearthListContract.CardEntry.COLUMN_TYPE,
            HearthListContract.CardEntry.COLUMN_RARITY,
            HearthListContract.CardEntry.COLUMN_PLAYER_CLASS,
            HearthListContract.CardEntry.COLUMN_COST,
            HearthListContract.CardEntry.COLUMN_RACE,
            HearthListContract.CardEntry.COLUMN_ATTACK,
            HearthListContract.CardEntry.COLUMN_HEALTH,
            HearthListContract.CardEntry.COLUMN_ARTIST,
            HearthListContract.CardEntry.COLUMN_TEXT,
            HearthListContract.CardEntry.COLUMN_FLAVOR,
            HearthListContract.CardEntry.COLUMN_MECHANICS,
            HearthListContract.CardEntry.COLUMN_REG_IMG_URL,
            HearthListContract.CardEntry.COLUMN_GOLD_IMG_URL,
            HearthListContract.CardEntry.COLUMN_HOW_TO_GET,
            HearthListContract.CardEntry.COLUMN_HOW_TO_GET_GOLD
    };

    // Cursor cols for readability
    public static final int COL_NAME = 0;
    public static final int COL_SET = 1;
    public static final int COL_TYPE = 2;
    public static final int COL_RARITY = 3;
    public static final int COL_CLASS = 4;
    public static final int COL_COST = 5;
    public static final int COL_RACE = 6;
    public static final int COL_ATTACK = 7;
    public static final int COL_HEALTH = 8;
    public static final int COL_ARTIST = 9;
    public static final int COL_TEXT = 10;
    public static final int COL_FLAVOR = 11;
    public static final int COL_MECHANICS = 12;
    public static final int COL_REG_IMG_URL = 13;
    public static final int COL_GOLD_IMG_URL = 14;
    public static final int COL_HOW_REG = 15;
    public static final int COL_HOW_GOLD = 16;

    // Card fields
    private String mCardName;
    private String mCardSet;
    private String mCardType;
    private String mCardRarity;
    private String mPlayerClass;
    private int mManaCost;
    private String mRace;
    private int mAttack;
    private int mHealth;
    private String mArtist;
    private String mText;
    private String mFlavorText;
    private ArrayList<String> mMechanics;
    private String mRegImageUrl;
    private String mGoldImageUrl;
    private String mHowToGetReg;
    private String mHowToGetGold;

    public Card(Cursor cursor) {
        mCardName = cursor.getString(COL_NAME);
        mCardSet = cursor.getString(COL_SET);
        mCardType = cursor.getString(COL_TYPE);
        mCardRarity = cursor.getString(COL_RARITY);
        mPlayerClass = cursor.getString(COL_CLASS);
        mManaCost = cursor.getInt(COL_COST);
        mRace = cursor.getString(COL_RACE);
        mAttack = cursor.getInt(COL_ATTACK);
        mHealth = cursor.getInt(COL_HEALTH);
        mArtist = cursor.getString(COL_ARTIST);
        mText = cursor.getString(COL_TEXT);
        mFlavorText = cursor.getString(COL_FLAVOR);
        // TODO: String -> ArrayList<String>
        //mMechanics = ;
        mRegImageUrl = cursor.getString(COL_REG_IMG_URL);
        mGoldImageUrl = cursor.getString(COL_GOLD_IMG_URL);
        mHowToGetReg = cursor.getString(COL_HOW_REG);
        mHowToGetGold = cursor.getString(COL_HOW_GOLD);
    }

    private Card(Parcel in) {
        mCardName = in.readString();
        mCardSet = in.readString();
        mCardType = in.readString();
        mCardRarity = in.readString();
        mPlayerClass = in.readString();
        mManaCost = in.readInt();
        mRace = in.readString();
        mAttack = in.readInt();
        mHealth = in.readInt();
        mArtist = in.readString();
        mText = in.readString();
        mFlavorText = in.readString();
        in.readList(mMechanics, ArrayList.class.getClassLoader());
        mRegImageUrl = in.readString();
        mGoldImageUrl = in.readString();
        mHowToGetReg = in.readString();
        mHowToGetGold = in.readString();
    }

    public int describeContents() { return 0; }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mCardName);
        out.writeString(mCardSet);
        out.writeString(mCardType);
        out.writeString(mCardRarity);
        out.writeString(mPlayerClass);
        out.writeInt(mManaCost);
        out.writeString(mRace);
        out.writeInt(mAttack);
        out.writeInt(mHealth);
        out.writeString(mArtist);
        out.writeString(mText);
        out.writeString(mFlavorText);
        out.writeSerializable(mMechanics);
        out.writeString(mRegImageUrl);
        out.writeString(mGoldImageUrl);
        out.writeString(mHowToGetReg);
        out.writeString(mHowToGetGold);
    }

    public static final Parcelable.Creator<Card> CREATOR = new Parcelable.Creator<Card>() {
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    public String getName() { return mCardName; }
    public String getCardSet() { return mCardSet; }
    public String getType() { return mCardType; }
    public String getRarity() { return mCardRarity; }
    public String getPlayerClass() { return mPlayerClass; }
    public String getRace() { return mRace; }
    public ArrayList<String> getMechanics() { return mMechanics; }
    public String getArtist() { return mArtist; }
    public String getFlavorText() { return mFlavorText; }
    public String getRegImageUrl() { return mRegImageUrl; }
    public String getGoldImageUrl() { return mGoldImageUrl; }
    public String getHowToGetReg() { return mHowToGetReg; }
    public String getHowToGetGold() { return mHowToGetGold; }
}
