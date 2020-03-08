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
    boolean ring = false;
    DatabaseUtils alarms = new DatabaseUtils();
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

        Log.i("! AlarmActivity started", "post ID here");

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            String id = extras.getString("alarmID");
            Alarm alarm = alarms.get(id);
            Calendar time = Calendar.getInstance();
            String[] tokenized = alarm.getDays().split(",");

            if(alarm.getDays().equals(""))
                ring = true;
                //delete alarm from database
            else
                for (int i = 1; i < tokenized.length; i++)
                    if (time.get(Calendar.DAY_OF_WEEK) == Integer.parseInt(tokenized[i]))
                        ring = true;
            if(ring)
                displayAlarm(alarm.getTime(), alarm.getLabel());
        } else
            Log.e("? AlarmActivity extras = null", String.valueOf(getIntent().getExtras()));
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
                Intent mainActivity = new Intent(AlarmActivity.this, MainActivity.class);
                startActivity(mainActivity);
                ringtone.stop();
                finish();
            }
        });
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
