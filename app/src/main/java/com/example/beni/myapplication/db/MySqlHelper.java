package com.example.beni.myapplication.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by beni on 5/2/2017.
 */

public class MySqlHelper extends SQLiteOpenHelper{
    private static final String DB_NAME = "github.db";
    private static final int DB_VERSION = 1;
    public MySqlHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DbContract.Repository.TABLE + "("
            +DbContract.Repository._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            +DbContract.Repository.ID + " INTEGER UNIQUE ON CONFLICT ABORT,"
                +DbContract.Repository.NAME + " TEXT,"
            +DbContract.Repository.URL + " TEXT,"
            +DbContract.Repository.HTML_URL + " TEXT,"
            +DbContract.Repository.IS_PUBLIC + " BOOLEAN)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE" + DbContract.Repository.TABLE);
        onCreate(db);
    }
}
