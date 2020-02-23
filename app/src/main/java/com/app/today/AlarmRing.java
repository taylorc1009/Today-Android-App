package com.app.today;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AlarmRing extends AppCompatActivity {
    ImageView alarmIcon;
    FloatingActionButton snoozeAlarm, stopAlarm;
    TextView alarmTime, alarmLabel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ring);
        alarmIcon = findViewById(R.id.alarmIcon);
        snoozeAlarm = findViewById(R.id.snoozeAlarm);
        stopAlarm = findViewById(R.id.stopAlarm);
        alarmTime = findViewById(R.id.alarmTime);
        alarmLabel = findViewById(R.id.alarmLabel);

        //animate alarm icon to scale in and out

        MediaPlayer ringtone = MediaPlayer.create(this, R.raw.alarmheaven);
        ringtone.start();
    }
}
