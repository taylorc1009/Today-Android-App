package com.app.today;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddAlarm extends AlarmManager {
    static FloatingActionButton alarmBack;
    static CheckBox chkMon, chkTues, chkWed, chkThurs, chkFri, chkSat, chkSun;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);
        alarmBack = findViewById(R.id.alarmBack);
        chkMon = findViewById(R.id.chkMon);
        chkTues = findViewById(R.id.chkTues);
        chkWed = findViewById(R.id.chkWed);
        chkThurs = findViewById(R.id.chkThurs);
        chkFri = findViewById(R.id.chkFri);
        chkSat = findViewById(R.id.chkSat);
        chkSun = findViewById(R.id.chkSun);
        alarmBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent mainReturn = new Intent(AddAlarm.this, AlarmManager.class);
                startActivity(mainReturn);
            }
        });
        chkMon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
    }
}
