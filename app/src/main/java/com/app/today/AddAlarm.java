package com.app.today;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddAlarm extends AlarmManager {
    static FloatingActionButton alarmBack;
    CheckBox chkMon, chkTues, chkWed, chkThurs, chkFri, chkSat, chkSun;
    EditText hour, minute;
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
        hour = findViewById(R.id.hour);
        minute = findViewById(R.id.minute);
        alarmBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent mainReturn = new Intent(AddAlarm.this, AlarmManager.class);
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
        /*chkMon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });*/
    }
}
