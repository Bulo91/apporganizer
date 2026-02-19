package com.example.apporganizer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "apporganizer.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_APP_STATE = "app_state";
    public static final String COL_PACKAGE = "package_name";
    public static final String COL_CATEGORY = "category";
    public static final String COL_CONFIDENCE = "confidence";
    public static final String COL_UPDATED_AT = "updated_at";

    public AppDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_APP_STATE + " (" +
                COL_PACKAGE + " TEXT PRIMARY KEY, " +
                COL_CATEGORY + " TEXT NOT NULL, " +
                COL_CONFIDENCE + " INTEGER NOT NULL, " +
                COL_UPDATED_AT + " INTEGER NOT NULL" +
                ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Simple pour l’instant
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APP_STATE);
        onCreate(db);
    }
}
