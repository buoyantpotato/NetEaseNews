package com.netease.newsprac.WorkingClass;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by linzhou on 16-7-22.
 */

public class DatabaseHelper extends SQLiteOpenHelper implements NewsReaderData {

    public DatabaseHelper(Context context) {
        super(context, NewsReaderData.DB_NAME, null, NewsReaderData.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_CMD = "CREATE TABLE IF NOT EXISTS "
                + News.TABLE_NAME + "("
                + News.ID + " INTEGER PRIMARY KEY, "
                + News.URL_LINK + " TEXT, "
                + News.IS_READ + " TEXT);";
        sqLiteDatabase.execSQL(CREATE_CMD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldV, int newV) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + News.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
