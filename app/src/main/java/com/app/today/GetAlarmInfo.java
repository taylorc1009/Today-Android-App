package com.app.today;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GetAlarmInfo extends AlarmSystem {
    static FloatingActionButton alarmBack, alarmSave;
    CheckBox chkMon, chkTues, chkWed, chkThurs, chkFri, chkSat, chkSun;
    EditText hour, minute, alarmLabel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_alarm_info);
        alarmBack = findViewById(R.id.alarmBack);
        alarmSave = findViewById(R.id.alarmSave);
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
        alarmBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent mainReturn = new Intent(GetAlarmInfo.this, AlarmSystem.class);
                startActivity(mainReturn);
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
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String timeStr = hour.getText() + ":" + minute.getText();
                //Date convertedStr = new Date();
                try {
                    time = sdf.parse(timeStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(!(alarmLabel.getText().toString().equals("")))
                    label = alarmLabel.getText().toString();
                //time = convertedStr;
                if(chkMon.isChecked())
                    days.add(Calendar.MONDAY);
                if(chkTues.isChecked())
                    days.add(Calendar.TUESDAY);
                if(chkWed.isChecked())
                    days.add(Calendar.WEDNESDAY);
                if(chkThurs.isChecked())
                    days.add(Calendar.THURSDAY);
                if(chkFri.isChecked())
                    days.add(Calendar.FRIDAY);
                if(chkSat.isChecked())
                    days.add(Calendar.SATURDAY);
                if(chkSun.isChecked())
                    days.add(Calendar.SUNDAY);
                //scheduleAlarm();
            }
        });
    }
}
