package com.app.today;

/*import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;*/
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

class DatabaseUtils { //extends SQLiteOpenHelper {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("alarms");

    private List<Alarm> alarms = new ArrayList<>();
    private boolean found = false;

    void store(Alarm alarm) {
        myRef.child(alarm.getId()).setValue(alarm);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Alarm alarmS = dataSnapshot.getValue(Alarm.class);
                assert alarmS != null;
                Log.d("! Firebase success", "Value is: " + myRef.getKey() + ", " + alarmS.toString());
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("? Firebase database", "Failed to read value.", error.toException());
            }
        });
    }
    String newKey() {
        return myRef.push().getKey();
    }
    List<Alarm> get() {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //alarms.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Alarm alarm = snapshot.getValue(Alarm.class);
                    alarms.add(alarm);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //alarms.clear();
                Log.e("? database pull cancelled", "alarms retrieved before cancellation = " + alarms.size(), databaseError.toException());
            }
        });
        if(!alarms.isEmpty())
            return alarms;
        else
            return null;
    }
    boolean has(String id) {
        myRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Alarm alarm = dataSnapshot.getValue(Alarm.class);
                Log.i("? alarm found?", String.valueOf(alarm) + " (null = no/false)");
                found = alarm != null;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                found = false;
            }
        });
        return found;
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
