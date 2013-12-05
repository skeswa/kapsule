package com.epsilonlabs.kapsule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sandile on 12/4/13.
 */
public class DbHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "kapsuledb";

    // Table name
    private static final String TABLE_NAME = "artifacts";

    // Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_KAPSULE_ID = "kapsule_id";
    private static final String KEY_KEY = "key";
    private static final String KEY_VALUE = "value";

    public DbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_KAPSULE_ID + " INTEGER,"
                + KEY_KEY + " TEXT,"
                + KEY_VALUE + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Create tables again
        onCreate(db);
    }

    public void doPut(int kapsuleId, String key, String value) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{KEY_ID},
                KEY_KAPSULE_ID + " = ? AND " + KEY_KEY + " = ?",
                new String[]{String.valueOf(kapsuleId), String.valueOf(key)},
                null, null, null, null);
        if (cursor != null)
            if (cursor.getCount() > 0) {
                // In this case, we just do an update on this row's value
                cursor.moveToFirst();
                long id = cursor.getLong(0);
                // Put the update together
                ContentValues values = new ContentValues();
                values.put(KEY_VALUE, value);
                // Run the update
                db.close();
                db = this.getWritableDatabase();
                db.update(TABLE_NAME, values, KEY_ID + " = ?", new String[]{String.valueOf(id)});
                // Finish up
                db.close();
                return;
            }
        // There isn't an existing row - make a new one
        ContentValues values = new ContentValues();
        values.put(KEY_KAPSULE_ID, kapsuleId);
        values.put(KEY_KEY, value);
        values.put(KEY_VALUE, value);
        // Run the insert
        db.close();
        db = this.getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public String doGet(int kapsuleId, String key) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{KEY_VALUE},
                KEY_KAPSULE_ID + " = ? AND " + KEY_KEY + " = ?",
                new String[]{String.valueOf(kapsuleId), String.valueOf(key)},
                null, null, null, null);
        if (cursor != null)
            if (cursor.getCount() > 0) {
                // We only care about the first result
                cursor.moveToFirst();
                String value = cursor.getString(0);
                // Finish up
                db.close();
                return value;
            }
        // We didn't find anything
        return null;
    }

    public void doDelete(int kapsuleId, String key) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_KAPSULE_ID + " = ? AND " + KEY_KEY + " = ?",
                new String[]{String.valueOf(kapsuleId), String.valueOf(key)});
        db.close();
    }

    public void doDelete(int kapsuleId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_KAPSULE_ID + " = ?",
                new String[]{String.valueOf(kapsuleId)});
        db.close();
    }
}
