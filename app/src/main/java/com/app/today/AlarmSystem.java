package com.app.today;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static android.app.PendingIntent.FLAG_ONE_SHOT;

public class AlarmSystem extends AppCompatActivity {
    static FloatingActionButton alarmBack, alarmAdd, alarmSave;
    CheckBox chkMon, chkTues, chkWed, chkThurs, chkFri, chkSat, chkSun;
    EditText hour, minute, alarmLabel;
    TextView alarmEmpty;
    ProgressBar alarmLoad;
    ConstraintLayout addGroup;
    TableLayout alarmTable;
    private String label = null;
    private List<Integer> days = new ArrayList<>();
    private Long alarmTime = null;
    private String UITime = null;

    AlarmManager alarmManager;
    PendingIntent alarmSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_system);
        alarmBack = findViewById(R.id.alarmBack);
        alarmAdd = findViewById(R.id.alarmAdd);
        alarmTable = findViewById(R.id.alarmTable);
        alarmEmpty = findViewById(R.id.alarmEmpty);
        alarmLoad = findViewById(R.id.alarmLoad);
        alarmBack = findViewById(R.id.alarmBack);
        alarmSave = findViewById(R.id.alarmSave);
        addGroup = findViewById(R.id.addGroup);
        chkMon = findViewById(R.id.chkMon);
        chkTues = findViewById(R.id.chkTues);
        chkWed = findViewById(R.id.chkWed);
        chkThurs = findViewById(R.id.chkThurs);
        chkFri = findViewById(R.id.chkFri);
        chkSat = findViewById(R.id.chkSat);
        chkSun = findViewById(R.id.chkSun);
        hour = findViewById(R.id.hour);
        minute = findViewById(R.id.minute);
        alarmLabel = findViewById(R.id.alarmLabel);
        alarmTime = null;
        UITime = null;
        label = null;
        days.clear();
        new alarmRetrieve().execute();
        alarmBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearAddUI();
                Intent mainReturn = new Intent(AlarmSystem.this, MainActivity.class);
                //mainReturn.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(mainReturn);
                finish();
            }
        });
        alarmAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(addGroup.getVisibility() == View.GONE)
                    addGroup.setVisibility(View.VISIBLE);
                    //change add button image to close/X
                else
                    addGroup.setVisibility(View.GONE);
            }
        });
        hour.addTextChangedListener(new TextWatcher() {
            int hr;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0) {
                    hr = Integer.parseInt(s.toString());
                    if(!(hr >= 0 && hr <= 23 && s.length() <= 2)) {
                        if(s.length() != 0)
                            hour.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                        else
                            hour.getText().clear();
                }
                else
                    hr = 0;
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        minute.addTextChangedListener(new TextWatcher() {
            int min;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0) {
                    min = Integer.parseInt(s.toString());
                    if(!(min >= 0 && min <= 59 && s.length() <= 2)) {
                        if(s.length() != 0)
                            minute.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                        else
                            minute.getText().clear();
                    }
                }
                else
                    min = 0;
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        alarmLabel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 100) {
                    alarmLabel.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                    Toast.makeText(AlarmSystem.this, "Label may consist of 100 characters max", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        alarmSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!(hour.getText().toString().equals("") || minute.getText().toString().equals(""))) {
                    Calendar validationDate = Calendar.getInstance();
                    validationDate.setTime(new Date());
                    validationDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour.getText().toString()));
                    validationDate.set(Calendar.MINUTE, Integer.parseInt(minute.getText().toString()));
                    validationDate.set(Calendar.SECOND, 0);
                    Log.i("Time comparison", "is " + System.currentTimeMillis() + " > " + validationDate.getTimeInMillis() + "?");
                    Log.i("Date == ", validationDate.getTime().toString());
                    UITime = hour.getText() + ":" + minute.getText();
                    alarmTime = validationDate.getTimeInMillis();
                    int alarmID = 0; //generate an alarm ID based on what already exists in the database
                    if (System.currentTimeMillis() > validationDate.getTimeInMillis() && !(chkMon.isChecked() || chkTues.isChecked() || chkWed.isChecked() || chkThurs.isChecked() || chkFri.isChecked() || chkSat.isChecked() || chkSun.isChecked())) {
                        Toast.makeText(getApplicationContext(), "Cannot set an alarm for a past time...", Toast.LENGTH_LONG).show();
                    } else if (chkMon.isChecked() || chkTues.isChecked() || chkWed.isChecked() || chkThurs.isChecked() || chkFri.isChecked() || chkSat.isChecked() || chkSun.isChecked()) {
                        if (!(alarmLabel.getText().toString().equals("")))
                            label = alarmLabel.getText().toString();
                        if (chkMon.isChecked())
                            days.add(Calendar.MONDAY);
                        if (chkTues.isChecked())
                            days.add(Calendar.TUESDAY);
                        if (chkWed.isChecked())
                            days.add(Calendar.WEDNESDAY);
                        if (chkThurs.isChecked())
                            days.add(Calendar.THURSDAY);
                        if (chkFri.isChecked())
                            days.add(Calendar.FRIDAY);
                        if (chkSat.isChecked())
                            days.add(Calendar.SATURDAY);
                        if (chkSun.isChecked())
                            days.add(Calendar.SUNDAY);
                        for (int i = 0; i <= days.size(); i++)
                            scheduleAlarm(days.get(i), alarmID);
                        clearAddUI();
                    } else {
                        //set the test amount of alarms allowed to 10?
                        scheduleAlarm(9, new Random().nextInt(10)); //make 9 our equivalent of null, 0 might be used as Sunday in some instances?
                        clearAddUI();
                    }
                }
                else {
                    if(hour.getText().toString().equals(""))
                        hour.setHintTextColor(Color.RED);
                    if(minute.getText().toString().equals(""))
                        minute.setHintTextColor(Color.RED);
                    Toast.makeText(getApplicationContext(), "Both Hour and Minute must be defined...", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    class alarmRetrieve extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            alarmTable.setVisibility(View.GONE);
            alarmEmpty.setVisibility(View.GONE);
            alarmLoad.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(String... strings) {
            //get stored alarms
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            alarmLoad.setVisibility(View.GONE);
            if(alarmTime != null) {
                alarmEmpty.setVisibility(View.GONE);
                alarmTable.setVisibility(View.VISIBLE);

                TableRow alarm = new TableRow(getApplicationContext());
                alarm.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                TextView timeTxt = new TextView(getApplicationContext());

                timeTxt.setText(UITime);
                timeTxt.setTextSize(30);
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                params.setMargins(30, 15, 0, 8);
                timeTxt.setLayoutParams(params);
                alarm.addView(timeTxt);

                alarmTable.addView(alarm, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            }
            else {
                alarmTable.setVisibility(View.GONE);
                alarmEmpty.setVisibility(View.VISIBLE);
            }
        }
    }
    private void scheduleAlarm(int dayOfWeek, Integer alarmID) {

        /* perhaps the best way to do this is set the alarm to trigger every day, then once that time in the
        * day has come, pull the alarm matching the ID we want from the database and check if it is due
        * to ring on the current day. */

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        calendar.setTimeInMillis(alarmTime);

        /* Check we aren't setting it in the past which would trigger it to fire instantly
        if(calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 7);
        }*/

        // Set this to whatever you were planning to do at the given time
        Intent alarmIntent = new Intent(this, AlarmRing.class);
        alarmIntent.putExtra("alarmID", alarmID);
        alarmIntent.setAction("com.app.today.FireAlarm");
        alarmSender = PendingIntent.getBroadcast(this.getApplicationContext(), alarmID, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        //upon ring, transition to alarm activity
        //perhaps pull an id and ring the alarm matching that id, alarms could be stored
        //in a database, plus it might be easier to view and delete them this way?



        Log.i("Attempted to invoke AlarmManager system", alarmID.toString());
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, alarmTime, AlarmManager.INTERVAL_DAY, alarmSender); //AlarmManager.INTERVAL_DAY * 7 <-- changed from a week to a day to fit proposal
        //Currently this will fire the alarm every day, but if you want it to ring on set days every week, it will ring corresponding to the amount of days
        //you checked at the same time. So say you checked 4 days, it will fire 4 alarms at the same time every day. Either set the interval back to 7 so
        //the alarm for that day doesn't fire until next week, or only schedule it if it hasn't already been added to the database, i.e. alarm ID doesn't
        //already exist
    }

    /* Possible alarm operators

    public void cancelPeriodicSchedule(PendingIntent sender) {
        if (am != null) {
            if (sender != null) {
                am.cancel(sender);
                sender.cancel();
            }
        }
        // Deactivate Broadcast Receiver to stop receiving broadcasts
        deactivateBroadcastreceiver();
    }
    private void activateBroadcastReceiver() {
        PackageManager pm = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, AlarmReceiver.class);
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        Toast.makeText(context, "activated", Toast.LENGTH_LONG).show();
    }
    private void deactivateBroadcastreceiver() {
        PackageManager pm = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, AlarmReceiver.class);
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        Toast.makeText(context, "cancelled", Toast.LENGTH_LONG).show();
    }*/

    private void clearAddUI() {
        addGroup.setVisibility(View.GONE);
        hour.getText().clear();
        minute.getText().clear();
        if(chkMon.isChecked())
            chkMon.toggle();
        if(chkTues.isChecked())
            chkTues.toggle();
        if(chkWed.isChecked())
            chkWed.toggle();
        if(chkThurs.isChecked())
            chkThurs.toggle();
        if(chkFri.isChecked())
            chkFri.toggle();
        if(chkSat.isChecked())
            chkSat.toggle();
        if(chkSun.isChecked())
            chkSun.toggle();
        alarmLabel.getText().clear();
    }
}