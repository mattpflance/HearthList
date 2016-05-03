package com.mattpflance.hearthlist.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.mattpflance.hearthlist.data.HearthListContract.CardEntry;
import com.mattpflance.hearthlist.data.HearthListContract.DeckEntry;

public class HearthListDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "hearthlist.db";

    public HearthListDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_CARD_TABLE =
                "CREATE TABLE " + CardEntry.TABLE_NAME + " (" +
                CardEntry._ID + " INTEGER PRIMARY KEY," +
                CardEntry.COLUMN_NAME + " TEXT UNIQUE NOT NULL, " +
                CardEntry.COLUMN_SET + " TEXT NOT NULL, " +
                CardEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                CardEntry.COLUMN_FACTION + " TEXT NOT NULL, " +
                CardEntry.COLUMN_RARITY + " TEXT NOT NULL, " +
                CardEntry.COLUMN_TEXT + " TEXT NOT NULL, " +
                CardEntry.COLUMN_FLAVOR + " TEXT NOT NULL, " +
                CardEntry.COLUMN_COLLECT + " BOOLEAN NOT NULL, " +
                CardEntry.COLUMN_RACE + " TEXT NOT NULL, " +
                CardEntry.COLUMN_REG_IMG + " BLOB, " +
                CardEntry.COLUMN_GOLD_IMG + " BLOB " +
                " );";

        final String SQL_CREATE_DECK_TABLE =
                "CREATE TABLE " + DeckEntry.TABLE_NAME + " (" +
                DeckEntry._ID + " INTEGER PRIMARY KEY," +
                DeckEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                DeckEntry.COLUMN_CLASS + " TEXT NOT NULL, " +
                DeckEntry.COLUMN_DECK_ARRAY + " TEXT " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_CARD_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_DECK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Drop and update card table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CardEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}