package com.app.today;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Objects;

public class AlarmActivity extends AppCompatActivity {
    ImageView alarmIcon;
    FloatingActionButton snoozeAlarm, stopAlarm;
    TextView alarmTime, alarmLabel;
    private boolean ring = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ring);
        Objects.requireNonNull(getSupportActionBar()).hide();
        alarmIcon = findViewById(R.id.alarmIcon);
        snoozeAlarm = findViewById(R.id.snoozeAlarm);
        stopAlarm = findViewById(R.id.stopAlarm);
        alarmTime = findViewById(R.id.alarmTime);
        alarmLabel = findViewById(R.id.alarmLabel);
        DatabaseUtilities alarms = new DatabaseUtilities();
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            String id = extras.getString("alarmID");
            if(alarms.has(id)) {
                assert id != null;
                Log.i("! AlarmActivity started with ID", id);
                Alarm alarm = alarms.get(id);
                Log.i("? days", alarm.getTime() + ", " + alarm.getDays());
                if (alarm.getDays() == null) {
                    ring = true;
                    alarms.delete(id);
                } else {
                    String[] tokenized = alarm.getDays().split(" ");
                    Calendar time = Calendar.getInstance();
                    for (String s : tokenized)
                        if (time.get(Calendar.DAY_OF_WEEK) == Integer.parseInt(s))
                            ring = true;
                }
                if (ring)
                    displayAlarm(alarm.getTime(), alarm.getLabel());
            } else {
                assert id != null;
                Log.e("? alarms.has id = false", id);
                goHome();
            }
        } else {
            Log.e("? AlarmActivity extras = null", String.valueOf(getIntent().getExtras()));
            goHome();
        }
    }
    private void displayAlarm(String time, String label) {
        alarmTime.setText(time);
        alarmLabel.setText(label);

        //animateIcon();
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
    private Runnable animateIcon() {
        alarmIcon.animate().scaleXBy(1).scaleYBy(1).setDuration(2000).withEndAction(new Runnable() {
            @Override
            public void run() {
                alarmIcon.animate().scaleXBy(-1).scaleYBy(-1).setDuration(2000).withEndAction(animateIcon());
            }
        });
        return null;
    }
}
