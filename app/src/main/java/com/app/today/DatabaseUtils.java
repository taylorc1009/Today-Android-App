package com.app.today;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

class DatabaseUtils { //extends SQLiteOpenHelper {

    // Write a message to the database
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("alarms");

    void store(Alarm alarm) {
        myRef.child(alarm.getId()).setValue(alarm);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Alarm value = dataSnapshot.getValue(Alarm.class);
                assert value != null;
                Log.d("! Firebase success", "Value is: " + myRef.getKey() + ", " + value.toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("? Firebase database", "Failed to read value.", error.toException());
            }
        });
    }
    String newKey() {
        return myRef.push().getKey();
    }
    ArrayList<Alarm> get() {
        return null;
    }
    boolean has(int id) {
        return false;
    }
    void delete() {

    }
    /*private static final String NAME = "alarm.db";
    private static final String TABLE = "alarms";
    private static final String COLUMN_ONE = "id";
    private static final String COLUMN_TWO = "days";
    private static final String COLUMN_THREE = "label";
    private static final String COLUMN_FOUR = "time";

    DatabaseUtils(Context context) {
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
    }*/
}
