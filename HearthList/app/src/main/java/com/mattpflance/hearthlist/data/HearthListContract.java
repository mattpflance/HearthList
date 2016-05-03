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
    public static final String PATH_CARD = "cards";
    public static final String PATH_DECK = "decks";

    /* Inner class that defines the table contents of the card table */
    public static final class CardEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CARD).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CARD;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CARD;

        // Table name
        public static final String TABLE_NAME = "cards";

        // Card name
        public static final String COLUMN_NAME = "card_name";
        // Card set the card is in
        public static final String COLUMN_SET= "card_set";
        // Card type
        public static final String COLUMN_TYPE = "card_type";
        // Card's faction (Mage, Warrior, Neutral, etc)
        public static final String COLUMN_FACTION = "card_faction";
        // Card rarity
        public static final String COLUMN_RARITY = "card_rarity";
        // Card's text
        public static final String COLUMN_TEXT = "card_text";
        // Card's flavor text
        public static final String COLUMN_FLAVOR = "card_flavor";
        // If the card is collectible or not
        public static final String COLUMN_COLLECT = "card_collectible";
        // Card's race (Dragon, Mech, Murloc, etc)
        public static final String COLUMN_RACE = "card_race";
        // Card's regular image
        public static final String COLUMN_REG_IMG = "card_reg_image";
        // Card's gold image
        public static final String COLUMN_GOLD_IMG = "card_gold_image";
    }

    /* Inner class that defines the table contents of the deck table */
    public static final class DeckEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DECK).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DECK;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DECK;

        public static final String TABLE_NAME = "decks";

        // Name of the deck
        public static final String COLUMN_NAME = "deck_name";
        // The class that the deck is for
        public static final String COLUMN_CLASS = "deck_class";
        // A JSON encoded array of all cards in the deck
        public static final String COLUMN_DECK_ARRAY = "deck_array";
    }
}