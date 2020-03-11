package com.app.today;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class AlarmActivity extends AppCompatActivity {
    //UI attributes
    ImageView stopAlarm;
    TextView alarmTime, alarmLabel;

    //Used to determine whether the alarm should ring today
    private boolean ring = false;

    //Create an instance of database utilities to get the alarm data from the database
    DatabaseUtilities alarmUtils = new DatabaseUtilities();
    Alarm alarm = new Alarm();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ring);

        //Hide the ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();

        //Initialize the UI attributes
        stopAlarm = findViewById(R.id.stopAlarm);
        alarmTime = findViewById(R.id.alarmTime);
        alarmLabel = findViewById(R.id.alarmLabel);

        //Get this intents extras
        Bundle extras = getIntent().getExtras();

        //If the alarm ID is defined, continue
        //Else return to the MainAtivity
        if(extras != null) {
            //Get the alarm ID from the intent extras
            final String id = extras.getString("alarmID");

            assert id != null;
            Log.i("alarm id", id);
            alarm = null;
            DatabaseUtilities.FirebaseQuery firebaseQuery = new DatabaseUtilities.FirebaseQuery(alarmUtils.myRef);
            final Task<DataSnapshot> load = firebaseQuery.start();
            load.addOnCompleteListener(new DatabaseUtilities.completeListener() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    super.onComplete(task);
                    if(task.isSuccessful()) {
                        for (DataSnapshot snapshot : Objects.requireNonNull(load.getResult()).getChildren()) {
                            if(Objects.requireNonNull(snapshot.getValue(Alarm.class)).getId().equals(id)) {
                                alarm = snapshot.getValue(Alarm.class);
                                assert alarm != null;
                                if (alarm.getDays() == null) {
                                    ring = true;
                                    alarmUtils.delete(id);
                                } else {
                                    String[] tokenized = alarm.getDays().split(" ");
                                    Calendar time = Calendar.getInstance();
                                    for (String s : tokenized)
                                        if (time.get(Calendar.DAY_OF_WEEK) == Integer.parseInt(s))
                                            ring = true;
                                }
                                if (ring)
                                    displayAlarm();
                                break;
                            }
                        }
                    }
                    else {
                        Log.e("? Firebase Query error", "AlarmActivity ID = null or the ID to find doesn't exist in the table");
                        goHome();
                    }
                }
            });
        } else {
            Log.e("? AlarmActivity extras = null", String.valueOf(getIntent().getExtras()));
            goHome();
        }
    }

    private void displayAlarm() {
        alarmTime.setText(alarm.getTime());
        if(alarm.getLabel() != null)
            alarmLabel.setText(alarm.getLabel());
        else
            alarmLabel.setText("");
        final MediaPlayer ringtone = MediaPlayer.create(this, R.raw.alarmheaven);
        ringtone.start();
        stopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ringtone.stop();
                goHome();
            }
        });
    }

    private void goHome() {
        Intent mainActivity = new Intent(AlarmActivity.this, MainActivity.class);
        startActivity(mainActivity);
        finish();
    }
}
