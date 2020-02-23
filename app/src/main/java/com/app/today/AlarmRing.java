package com.app.today;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;

public class AlarmRing extends AppCompatActivity {
    ImageView alarmIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ring);
        alarmIcon = findViewById(R.id.alarmIcon);
        alarmIcon.setMinimumWidth(200);
        alarmIcon.setMinimumHeight(200);
        MediaPlayer ringtone = MediaPlayer.create(this, R.raw.alarmheaven);
        ringtone.start();

    }
}
