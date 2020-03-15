package com.app.today;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import java.util.Calendar;
import java.util.Objects;

public class AlarmActivity extends AppCompatActivity {
    //UI attributes
    ImageView stopAlarm;
    TextView alarmTime, alarmLabel;

    //Used to determine whether the alarm should ring today
    private boolean ring = false;

    //Create an instance of database utilities to get the alarm data from the database
    DatabaseUtilities alarmUtils = new DatabaseUtilities();

    //AlarmManager used to remove redundant alarms in this activity
    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ring);

        //Hide the ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();

        findViewById(R.id.ringLoading).setVisibility(View.VISIBLE);
        findViewById(R.id.alarmRingGroup).setVisibility(View.GONE);

        //Initialize the UI attributes
        stopAlarm = findViewById(R.id.stopAlarm);
        alarmTime = findViewById(R.id.alarmTime);
        alarmLabel = findViewById(R.id.alarmLabel);

        //Get this intents extras
        Bundle extras = getIntent().getExtras();

        //If the alarm ID is defined, continue
        //Else return to the MainActivity as we need an ID to continue
        if(extras != null) {
            //Get the alarm ID from the intent extras
            final String id = extras.getString("alarmID");
            assert id != null;
            Log.i("! AlarmActivity initiated with ID", id);

            //Now we will query the database using our Task
            //This here is the main reason for our Task, if we didn't use it we would get a NullPointerException
            //from having no alarm data to use
            DatabaseUtilities.FirebaseQuery firebaseQuery = new DatabaseUtilities.FirebaseQuery(alarmUtils.myRef);
            final Task<DataSnapshot> load = firebaseQuery.start();
            load.addOnCompleteListener(new DatabaseUtilities.completeListener() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    super.onComplete(task);
                    if(task.isSuccessful()) {
                        //Now we will iterate through our results to get the data matching the ID
                        for(DataSnapshot snapshot : Objects.requireNonNull(load.getResult()).getChildren()) {
                            Alarm alarm = snapshot.getValue(Alarm.class);
                            assert alarm != null;
                            if(alarm.getId().equals(id)) {
                                //If the user didn't specify a day in their alarm, continue
                                //Else perform a check to see if the alarm is due to ring today
                                if(alarm.getDays() == null || alarm.getDays().equals("")) {
                                    ring = true;

                                    //Because this alarm wasn't specified to repeat, we will delete it
                                    //from both the table and the AlarmManager now
                                    //Like the procedure for this in AlarmSystem (delete button), we
                                    //need to make an exact PendingIntent which corresponds to the one
                                    //stored so the Alarm Manager can find and delete it
                                    alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                    Intent alarmIntent = new Intent(getApplicationContext(), AlarmRing.class);
                                    alarmIntent.putExtra("alarmID", id);
                                    alarmIntent.setAction("com.app.today.FireAlarm");
                                    PendingIntent alarmSender = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, 0);

                                    //Sometimes the PendingIntent may be null before it's deleted
                                    //this seems to happen when a new instance of AlarmManager is created
                                    //because I don't think the alarm trigger is implemented properly
                                    try {
                                        alarmManager.cancel(alarmSender);
                                        alarmSender.cancel();
                                    } catch (NullPointerException e) {
                                        Log.e("? Local PendingIntent was null", "new instance of AlarmManager?");
                                    }
                                    alarmUtils.delete(id);
                                } else {
                                    //The data type of the Calendar days are integers, I had to make a list
                                    //of which days the user checked and convert it into a string for
                                    //Firebase to be able to store it, so I used a tokenizer to get them back
                                    String[] tokenized = alarm.getDays().split(" ");

                                    //Get the current time (Calendar instance)
                                    Calendar time = Calendar.getInstance();

                                    //For every day the user check, compare them to the current day integer
                                    //value and determine if the alarm is due to ring
                                    for(String s : tokenized) {
                                        if (time.get(Calendar.DAY_OF_WEEK) == Integer.parseInt(s)) {
                                            ring = true;
                                            break;
                                        }
                                    }
                                }
                                //If the alarm was due to ring, do so
                                //Else end this activity
                                if (ring)
                                    displayAlarm(alarm);
                                else {
                                    goHome();
                                    finish();
                                }
                                break;
                            }
                        }
                        if(!ring) {
                            Log.e("? alarm ID not found in the database", id);
                            goHome();
                        }
                    }
                    else {
                        Log.e("? Firebase Query error", "query failed");
                        goHome();
                    }
                }
            });
        } else {
            Log.e("? AlarmActivity extras = null", String.valueOf(getIntent().getExtras()));
            goHome();
        }
    }

    //Used to display the alarms data in the UI
    private void displayAlarm(Alarm alarm) {
        findViewById(R.id.ringLoading).setVisibility(View.GONE);
        findViewById(R.id.alarmRingGroup).setVisibility(View.VISIBLE);

        alarmTime.setText(alarm.getTime());
        //Check if the user entered a label to prevent NullPointerException
        if(alarm.getLabel() != null)
            alarmLabel.setText(alarm.getLabel());
        else
            alarmLabel.setText("");

        //Play the alarm ringtone from resources and stop when the user stops the alarm
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

    //Returns the user to the MainActivity
    private void goHome() {
        Intent mainActivity = new Intent(AlarmActivity.this, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainActivity);
        finish();
    }
}
