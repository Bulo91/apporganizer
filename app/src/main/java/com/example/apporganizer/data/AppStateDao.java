package com.example.apporganizer.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;

public class AppStateDao {

    private final AppDbHelper dbHelper;

    public AppStateDao(Context context) {
        this.dbHelper = new AppDbHelper(context);
    }

    public Map<String, StoredState> getAllStates() {
        Map<String, StoredState> map = new HashMap<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.query(
                AppDbHelper.TABLE_APP_STATE,
                new String[]{AppDbHelper.COL_PACKAGE, AppDbHelper.COL_CATEGORY, AppDbHelper.COL_CONFIDENCE, AppDbHelper.COL_UPDATED_AT},
                null, null, null, null, null
        );

        while (c.moveToNext()) {
            String pkg = c.getString(0);
            String cat = c.getString(1);
            int conf = c.getInt(2);
            long updatedAt = c.getLong(3);
            map.put(pkg, new StoredState(cat, conf, updatedAt));
        }

        c.close();
        db.close();
        return map;
    }

    public void upsertState(String packageName, String category, int confidence) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AppDbHelper.COL_PACKAGE, packageName);
        values.put(AppDbHelper.COL_CATEGORY, category);
        values.put(AppDbHelper.COL_CONFIDENCE, confidence);
        values.put(AppDbHelper.COL_UPDATED_AT, System.currentTimeMillis());

        db.insertWithOnConflict(
                AppDbHelper.TABLE_APP_STATE,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        );

        db.close();
    }

    public static class StoredState {
        public final String category;
        public final int confidence;
        public final long updatedAt;

        public StoredState(String category, int confidence, long updatedAt) {
            this.category = category;
            this.confidence = confidence;
            this.updatedAt = updatedAt;
        }
    }
}
