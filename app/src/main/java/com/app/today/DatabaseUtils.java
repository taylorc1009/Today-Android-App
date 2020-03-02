package com.app.today;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseUtils extends SQLiteOpenHelper {
    private static final String NAME = "alarm.db";
    private static final String TABLE = "alarms";
    private static final String COLUMN_ONE = "id";
    private static final String COLUMN_TWO = "days";
    private static final String COLUMN_THREE = "label";
    private static final String COLUMN_FOUR = "time";

    public DatabaseUtils(Context context) {
        super(context, NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create = "CREATE TABLE " + TABLE + " (" + COLUMN_ONE + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_TWO + " TEXT," + COLUMN_THREE + " TEXT," + COLUMN_FOUR + " TEXT)";
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }
}
