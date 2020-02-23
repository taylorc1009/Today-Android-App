package com.app.today;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmRing extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Intent received !!!", String.valueOf(intent.getExtras().getInt("alarmID")));
        if (intent.hasExtra("alarmID")) {
            Intent sendAlarmIntent = new Intent(context, AlarmActivity.class);
            sendAlarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            sendAlarmIntent.putExtra("alarmID", intent.getExtras().getInt("alarmID"));
            Log.i("Attempting to start AlarmActivity with ID", String.valueOf(intent.getExtras().getInt("alarmID")));
            context.startActivity(sendAlarmIntent);
        }
        /*StringBuilder sb = new StringBuilder();
        sb.append("Action: " + intent.getAction() + "\n");
        sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
        String log = sb.toString();
        Log.d(TAG, log);
        Toast.makeText(context, log, Toast.LENGTH_LONG).show();*/
    }
}
