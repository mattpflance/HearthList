package com.mattpflance.hearthlist.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

public class HearthListContract {

    // Content Provider's name
    public static final String CONTENT_AUTHORITY = "com.mattpflance.hearthlist";

    // Base URI for our Content Provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths for db
    public static final String PATH_ALL_CARDS = "cards";
    public static final String PATH_CARD = "card";
    public static final String PATH_ALL_DECKS = "decks";
    public static final String PATH_DECK = "deck";

    /* Inner class that defines the table contents of the card table */
    public static final class CardEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ALL_CARDS).build();
        public static final Uri CONTENT_ITEM_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_ALL_CARDS)
                .appendPath(PATH_CARD)
                .build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ALL_CARDS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ALL_CARDS;

        // Table name
        public static final String TABLE_NAME = "cards";

        // Card name
        public static final String COLUMN_NAME = "card_name";
        // Card set the card is in
        public static final String COLUMN_SET= "card_set";
        // Card's type (Minion, Spell, Weapon)
        public static final String COLUMN_TYPE= "card_type";
        // Card rarity
        public static final String COLUMN_RARITY = "card_rarity";
        // Card class (Mage, Warrior, etc)
        public static final String COLUMN_PLAYER_CLASS = "card_player_class";
        // Card's mana cost
        public static final String COLUMN_COST = "card_cost";
        // Card's race (Dragon, Mech, Murloc, etc)
        public static final String COLUMN_RACE = "card_race";
        // Card's attack
        public static final String COLUMN_ATTACK = "card_attack";
        // Card's health/durability
        public static final String COLUMN_HEALTH = "card_health";
        // The card's artist
        public static final String COLUMN_ARTIST = "card_artist";
        // Card's text
        public static final String COLUMN_TEXT = "card_text";
        // Card's flavor text
        public static final String COLUMN_FLAVOR = "card_flavor";
        // A JSON encoded array of a card's mechanics
        public static final String COLUMN_MECHANICS = "card_mechanics";
        // Reg image url and blob
        public static final String COLUMN_REG_IMG_URL = "card_reg_image_url";
        public static final String COLUMN_REG_IMG = "card_reg_image";
        // Gold image url and blob
        public static final String COLUMN_GOLD_IMG_URL = "card_gold_image_url";
        public static final String COLUMN_GOLD_IMG = "card_gold_image";
        // Only Basic, Promotion, Reward cards will have a HowToGet
        public static final String COLUMN_HOW_TO_GET = "card_get";
        public static final String COLUMN_HOW_TO_GET_GOLD = "card_get_gold";
    }

    /* Inner class that defines the table contents of the deck table */
    public static final class DeckEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ALL_DECKS).build();
        public static final Uri CONTENT_ITEM_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_ALL_DECKS)
                .appendPath(PATH_DECK)
                .build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ALL_DECKS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ALL_DECKS;

        public static final String TABLE_NAME = "decks";

        // Name of the deck
        public static final String COLUMN_NAME = "deck_name";
        // The class that the deck is for
        public static final String COLUMN_CLASS = "deck_class";
        // A JSON encoded array of all cards in the deck
        public static final String COLUMN_DECK_ARRAY = "deck_array";
    }
}