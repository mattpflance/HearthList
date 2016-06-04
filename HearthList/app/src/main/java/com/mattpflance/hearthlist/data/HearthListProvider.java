package com.mattpflance.hearthlist.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class HearthListProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private HearthListDbHelper mOpenHelper;

    static final int ALL_CARDS = 100;
    static final int CARD = 101;
    static final int ALL_DECKS = 300;
    static final int DECK = 301;

    private static final String sCardFiltersManaSelection =
            // Min mana cost
            HearthListContract.CardEntry.COLUMN_COST + " >= ? AND " +
            // Max mana cost
            HearthListContract.CardEntry.COLUMN_COST + " <= ?";

    private static final String sCardFiltersClassSelection =
            HearthListContract.CardEntry.COLUMN_PLAYER_CLASS + " = ?";

    // TODO Fix. SQL query isn't valid
    private static final String sCardFiltersCardSetSelection =
            " ? LIKE \"%" + HearthListContract.CardEntry.COLUMN_SET + "%\"";

    // TODO Fix. SQL query isn't valid
    private static final String sCardFiltersMechanicsSelection =
            " ? LIKE \"%" + HearthListContract.CardEntry.COLUMN_MECHANICS;

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = HearthListContract.CONTENT_AUTHORITY;

        // Matches for cards
        matcher.addURI(authority, HearthListContract.PATH_ALL_CARDS, ALL_CARDS);
        matcher.addURI(authority, HearthListContract.PATH_ALL_CARDS + "/*", CARD);

        // Matches for decks
        matcher.addURI(authority, HearthListContract.PATH_ALL_DECKS, ALL_DECKS);
        matcher.addURI(authority, HearthListContract.PATH_ALL_DECKS + "/*", DECK);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new HearthListDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ALL_CARDS:
                return HearthListContract.CardEntry.CONTENT_TYPE;
            case CARD:
                return HearthListContract.CardEntry.CONTENT_ITEM_TYPE;
            case ALL_DECKS:
                return HearthListContract.DeckEntry.CONTENT_TYPE;
            case DECK:
                return HearthListContract.DeckEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        /* We query all cards/decks when displaying data and never a single card or deck */

        String filterSelection = null;
        if (selectionArgs != null) {
            int numArgs = selectionArgs.length;
            filterSelection = sCardFiltersManaSelection +
                    // Class filter check
                    ((numArgs > 2 && selectionArgs[2] != null) ?  " AND " + sCardFiltersClassSelection : "") +
                    // Card Set filter check
                    ((numArgs > 3 && selectionArgs[3] != null) ? " AND " + sCardFiltersCardSetSelection : "") +
                    // Mechanics filter check
                    ((numArgs > 4 && selectionArgs[4] != null) ? " AND " + sCardFiltersMechanicsSelection : "");
        }

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case ALL_CARDS:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        HearthListContract.CardEntry.TABLE_NAME,
                        projection,
                        filterSelection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case ALL_DECKS:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        HearthListContract.DeckEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case CARD: {
                long _id = db.insert(HearthListContract.CardEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = HearthListContract.CardEntry.CONTENT_URI;
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case DECK: {
                long _id = db.insert(HearthListContract.DeckEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = HearthListContract.DeckEntry.CONTENT_URI;
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case CARD:
                rowsDeleted = db.delete(HearthListContract.CardEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case DECK:
                rowsDeleted = db.delete(HearthListContract.DeckEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case CARD:
                rowsUpdated = db.update(HearthListContract.CardEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case DECK:
                rowsUpdated = db.update(HearthListContract.DeckEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CARD:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(HearthListContract.CardEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}