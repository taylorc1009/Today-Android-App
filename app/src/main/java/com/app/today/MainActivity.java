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
    @TargetApi(Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.textView);
        CalendarContentResolver calendar = new CalendarContentResolver(this);
        Set<String> CalSet = calendar.getCalendars();
        CharSequence[] instances = new CharSequence[CalSet.size()];
        Integer i = new Integer(0);
        CalSet.forEach((inst) -> {
            instances[i] = inst;
            i++;
        });
        /*String calendarInstance = calendar.getCalendars();
        char[] instance = new char[calendarInstance.length()];
        for(int i = 0; i < calendarInstance.length(); i++)
            instance[i] = calendarInstance.charAt(i);*/
        textView.setText(instance);
    }
}
