package com.app.today;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Objects;

public class AlarmRing extends BroadcastReceiver {
    //This is our alarm receiver, the intent we set in the AlarmManager is instructed to broadcast to this
    //The receiver is defined in the AndroidManifest
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("! alarm PendingIntent received with ID", Objects.requireNonNull(Objects.requireNonNull(intent.getExtras()).getString("alarmID")));
        if (intent.hasExtra("alarmID")) {
            //Start the AlarmActivity with the alarm ID specified
            Intent sendAlarmIntent = new Intent(context, AlarmActivity.class);
            sendAlarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            sendAlarmIntent.putExtra("alarmID", intent.getExtras().getString("alarmID"));
            Log.i("? attempting to start AlarmActivity with ID", Objects.requireNonNull(intent.getExtras().getString("alarmID")));
            context.startActivity(sendAlarmIntent);
        }
    }
}
