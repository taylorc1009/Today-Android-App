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
    boolean ringing = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ring);
        alarmIcon = findViewById(R.id.alarmIcon);
        snoozeAlarm = findViewById(R.id.snoozeAlarm);
        stopAlarm = findViewById(R.id.stopAlarm);
        alarmTime = findViewById(R.id.alarmTime);
        alarmLabel = findViewById(R.id.alarmLabel);

        Log.i("! AlarmActivity started", "post ID here");

        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(System.currentTimeMillis());
        String timeStr = time.get(Calendar.HOUR) + ":" + time.get(Calendar.MINUTE);
        alarmTime.setText(timeStr);

        Objects.requireNonNull(getSupportActionBar()).hide();
        //animateIcon();
        final MediaPlayer ringtone = MediaPlayer.create(this, R.raw.alarmheaven);
        ringtone.start();
        stopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(AlarmActivity.this, MainActivity.class);
                startActivity(mainActivity);
                ringtone.stop();
                ringing = false;
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
