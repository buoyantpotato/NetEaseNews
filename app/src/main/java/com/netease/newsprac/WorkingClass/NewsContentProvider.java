package com.netease.newsprac.WorkingClass;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by linzhou on 16-7-24.
 */

public class NewsContentProvider extends ContentProvider {

    private final String TAG = getClass().getSimpleName();
    private static UriMatcher uriMatcher = null;
    private static final int NewsGarage = 1;
    private DatabaseHelper dbHelper;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(NewsReaderData.AUTHORITY, NewsReaderData.News.TABLE_NAME, NewsGarage);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return (dbHelper == null) ? false : true;
    }


    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case NewsGarage:
                return db.query(NewsReaderData.News.TABLE_NAME,
                        strings, s, strings1, null, null, s1);
            default:
                Log.e(TAG + " URI: ", "The URI you provided is invalid.");
                throw new IllegalArgumentException("The URI " + uri.toString() + " is invalid.");
        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        switch (uriMatcher.match(uri)) {
            case NewsGarage:
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                long rowID = db.insert(NewsReaderData.News.TABLE_NAME, null, contentValues);
                Uri insertUri = Uri.withAppendedPath(uri, "/" + rowID);


                getContext().getContentResolver().notifyChange(uri, null);
                return insertUri;

            default:
                Log.e(TAG + " URI: ", "The URI you provided is invalid.");
                throw new IllegalArgumentException("The URI " + uri.toString() + " is invalid.");
        }
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        switch (uriMatcher.match(uri)) {
            case NewsGarage:
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                //long id = ContentUris.parseId(uri);

                return db.delete(NewsReaderData.News.TABLE_NAME, s, strings);

            default:
                Log.e(TAG + " URI: ", "The URI you provided is invalid.");
                throw new IllegalArgumentException("The URI " + uri.toString() + " is invalid.");
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        switch (uriMatcher.match(uri)) {
            case NewsGarage:
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                return db.update(NewsReaderData.News.TABLE_NAME, contentValues, s, strings);

            default:
                Log.e(TAG + " URI: ", "The URI you provided is invalid.");
                throw new IllegalArgumentException("The URI " + uri.toString() + " is invalid.");
        }
    }
}
