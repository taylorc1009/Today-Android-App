package com.app.today;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlarmSystem extends AppCompatActivity {
    static FloatingActionButton alarmBack, alarmAdd;
    TableLayout alarmTable;
    //protected SimpleDateFormat time = new SimpleDateFormat("HH:mm");
    protected Date time = new Date();
    protected String label;
    protected List<Integer> days = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_system);
        alarmBack = findViewById(R.id.alarmBack);
        alarmAdd = findViewById(R.id.alarmAdd);
        alarmTable = findViewById(R.id.alarmTable);
        alarmBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "pressed", Toast.LENGTH_LONG).show();
                Intent mainReturn = new Intent(AlarmSystem.this, MainActivity.class);
                startActivity(mainReturn);
            }
        });
        alarmAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent addAlarm = new Intent(AlarmSystem.this, GetAlarmInfo.class);
                startActivity(addAlarm);
            }
        });

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
    protected void updateTable() {
        // Create a new row to be added.
        TableRow alarm = new TableRow(this);
        alarm.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        TextView timeTxt = new TextView(this);
        timeTxt.setText(time.toString());
        timeTxt.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        alarm.addView(timeTxt);
        /*ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone();
        constraintSet.connect(R.id.imageView,ConstraintSet.RIGHT,R.id.check_answer1,ConstraintSet.RIGHT,0);
        constraintSet.connect(R.id.imageView,ConstraintSet.TOP,R.id.check_answer1,ConstraintSet.TOP,0);
        constraintSet.applyTo(constraintLayout);*/

        /* //Create a Button to be the row-content.
        TextView b = new TextView(this);
        b.setText("Dynamic Button");
        b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        // Add Button to row.
        tr.addView(b);*/

        // Add row to TableLayout.
        //tr.setBackgroundResource(R.drawable.sf_gradient_03);
        alarmTable.addView(alarm);//, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }
}
