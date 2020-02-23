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

public class AlarmSystem extends AppCompatActivity {
    static FloatingActionButton alarmBack, alarmAdd, alarmSave;
    CheckBox chkMon, chkTues, chkWed, chkThurs, chkFri, chkSat, chkSun;
    EditText hour, minute, alarmLabel;
    TextView alarmEmpty;
    ProgressBar alarmLoad;
    ConstraintLayout addGroup;
    TableLayout alarmTable;
    private Date time = null;
    private String label = null;
    private List<Integer> days = new ArrayList<>();
    private Long alarmTime = null;
    private String UITime;
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
        time = null;
        label = null;
        days.clear();
        new alarmRetrieve().execute();
        alarmBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "pressed", Toast.LENGTH_LONG).show();
                new alarmRetrieve().cancel(true);
                Intent mainReturn = new Intent(AlarmSystem.this, MainActivity.class);
                startActivity(mainReturn);
            }
        });
        alarmAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Intent addAlarm = new Intent(AlarmSystem.this, GetAlarmInfo.class);
                //startActivity(addAlarm);
                if(addGroup.getVisibility() == View.GONE)
                    addGroup.setVisibility(View.VISIBLE);
                    //change add button image to close/X
                else
                    addGroup.setVisibility(View.GONE);
            }
        });
        hour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int hr = Integer.parseInt(s.toString());
                if(!(hr >= 0 && hr <= 23 && s.length() <= 2))
                    hour.getText().delete(hour.getText().length() - 1, hour.getText().length());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        minute.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int hr = Integer.parseInt(s.toString());
                if(!(hr >= 0 && hr <= 59 && s.length() <= 2))
                    minute.getText().delete(minute.getText().length() - 1, minute.getText().length());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        alarmLabel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 100)
                    alarmLabel.getText().delete(alarmLabel.getText().length() - 1, alarmLabel.getText().length());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        alarmSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!(hour.getText().toString().equals("") || minute.getText().toString().equals(""))) {
                    final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                    UITime = hour.getText() + ":" + minute.getText();
                    try {
                        time = formatter.parse(UITime);
                        alarmTime = time.getTime();
                    } catch (ParseException e) {
                        Log.e("Failed to parse UITime (time string)", e.toString());
                    } catch (NullPointerException e) {
                        Log.e("Date time == null", e.toString());
                    }
                    Calendar validationDate = Calendar.getInstance();
                    validationDate.setTime(new Date());
                    validationDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour.getText().toString()));
                    validationDate.set(Calendar.MINUTE, Integer.parseInt(minute.getText().toString()));
                    validationDate.set(Calendar.SECOND, 0);
                    Log.i("Time comparison", "is " + System.currentTimeMillis() + " > " + validationDate.getTimeInMillis() + "?");
                    Log.i("Date == ", validationDate.getTime().toString());
                    if(System.currentTimeMillis() > validationDate.getTimeInMillis() && !(chkMon.isChecked() || chkTues.isChecked() || chkWed.isChecked() || chkThurs.isChecked() || chkFri.isChecked() || chkSat.isChecked() || chkSun.isChecked())) {
                        Toast.makeText(getApplicationContext(), "Cannot set an alarm for a past time...", Toast.LENGTH_LONG).show();
                    } else {
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
                        //hour.setText(null);
                        //minute.setText(null);
                        new alarmRetrieve().execute();
                        new alarmRetrieve().cancel(true);
                        //scheduleAlarm(); //once method completes, clear all attributes required
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
        protected void onCancelled() {
            addGroup.setVisibility(View.GONE);
            //hour.getText().clear();
            //minute.getText().clear();
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
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            alarmTable.setVisibility(View.GONE);
            alarmEmpty.setVisibility(View.GONE);
            alarmLoad.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(String... strings) {

            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            alarmLoad.setVisibility(View.GONE);
            if(time != null) {
                alarmEmpty.setVisibility(View.GONE);
                alarmTable.setVisibility(View.VISIBLE);
                //TableLayout table = new TableLayout(getParent());
                // Create a new row to be added.
                TableRow alarm = new TableRow(getApplicationContext());
                alarm.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                TextView timeTxt = new TextView(getApplicationContext());
                timeTxt.setText(time.toString());
                timeTxt.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                alarm.addView(timeTxt);

                alarmTable.addView(alarm, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                //getParent().setContentView(alarmTable);
            }
            else {
                alarmTable.setVisibility(View.GONE);
                alarmEmpty.setVisibility(View.VISIBLE);
            }
        }
    }
    /*private void scheduleAlarm(int dayOfWeek) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        // Check we aren't setting it in the past which would trigger it to fire instantly
        if(calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 7);
        }
        // Set this to whatever you were planning to do at the given time
        PendingIntent yourIntent;// <-- upon ring, transition to alarm activity
        //perhaps pull an id and ring the alarm matching that id, alarms could be stored
        //in a database, plus it might be easier to view and delete them this way?
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, yourIntent);
    }
    private void setUpAlarms() {
        scheduleAlarm(Calendar.MONDAY);
        scheduleAlarm(Calendar.FRIDAY);
    }*/
}