package com.example.chop;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ChopChopDB extends SQLiteOpenHelper {
    private static final String CREATE_TABLE_MYCAL = "CREATE TABLE IF NOT EXISTS MYCAL(use INTEGER PRIMARY KEY, cal INTEGER);";

    public ChopChopDB(Context c){
        super(c, "ChopChopDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MYCAL);
        db.execSQL("CREATE TABLE IF NOT EXISTS CHANGE(date TEXT PRIMARY KEY, picPATH TEXT, weight TEXT, waist TEXT, thigh TEXT);");
        db.execSQL("INSERT INTO MYCAL(use, cal) values (0, 0);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS MYCAL");
        db.execSQL("DROP TABLE IF EXISTS DIET");
        db.execSQL("DROP TABLE IF EXISTS CHANGE");
        onCreate(db);
    }
}
