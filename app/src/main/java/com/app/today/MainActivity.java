package com.app.today;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.textView);
        CalendarContentResolver calendar = new CalendarContentResolver(this);
        Set<String> CalSet = calendar.getCalendars();
        CharSequence instances = "";
        for(String s : CalSet) {
            instances = instances + "," + s;
        }
        textView.setText(instances);
    }
}
