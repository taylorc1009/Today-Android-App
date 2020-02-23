package com.app.today;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmRing extends BroadcastReceiver {

    int pendingIntentID; //same as alarmID

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Intent received !!!", String.valueOf(intent.getExtras().getInt("alarmID")));
        if (intent.hasExtra("alarmID")) {
            pendingIntentID = intent.getExtras().getInt("alarmID");
            Intent sendAlarmIntent = new Intent(context, AlarmActivity.class);
            sendAlarmIntent.putExtra("pendingIntentID", pendingIntentID);
            Log.i("AlarmRing has been called", String.valueOf(intent.getExtras().getInt("alarmID")));
            context.startService(sendAlarmIntent);
        }
        /*StringBuilder sb = new StringBuilder();
        sb.append("Action: " + intent.getAction() + "\n");
        sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
        String log = sb.toString();
        Log.d(TAG, log);
        Toast.makeText(context, log, Toast.LENGTH_LONG).show();*/
    }
}
