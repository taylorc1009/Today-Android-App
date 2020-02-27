package com.app.today;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Objects;

public class AlarmRing extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Intent received !!!", String.valueOf(Objects.requireNonNull(intent.getExtras()).getInt("alarmID")));
        if (intent.hasExtra("alarmID")) {
            Intent sendAlarmIntent = new Intent(context, AlarmActivity.class);
            sendAlarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            sendAlarmIntent.putExtra("alarmID", intent.getExtras().getInt("alarmID"));
            Log.i("Attempting to start AlarmActivity with ID", String.valueOf(intent.getExtras().getInt("alarmID")));
            context.startActivity(sendAlarmIntent);
        }
    }
}
