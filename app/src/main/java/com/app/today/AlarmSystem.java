package com.app.today;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import android.icu.util.Calendar;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import java.util.List;
import java.util.Objects;

public class AlarmSystem extends AppCompatActivity {
    //UI attributes
    ImageView alarmBack, alarmAdd, alarmSave;
    CardView addCard;
    CheckBox chkMon, chkTues, chkWed, chkThurs, chkFri, chkSat, chkSun;
    EditText hour, minute, alarmLabel;
    ConstraintLayout addGroup;
    TableLayout alarmTable;
    ProgressBar alarmLoad;
    TextView alarmEmpty;

    //Utilities for managing/setting alarms
    AlarmManager alarmManager;
    PendingIntent alarmSender;

    //DatabaseUtilities to interact with the Firebase database
    DatabaseUtilities alarms = new DatabaseUtilities();
    List<Alarm> alarmList = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_system);
        
        //Initialize the UI attributes
        addCard = findViewById(R.id.addCard);
        alarmBack = findViewById(R.id.alarmBack);
        alarmAdd = findViewById(R.id.alarmAdd);
        alarmTable = findViewById(R.id.alarmTable);
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
        alarmLoad = findViewById(R.id.alarmLoad);
        alarmEmpty = findViewById(R.id.alarmEmpty);

        //Initialize alarmManager with the system AlarmManager class
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //Update the list of alarms and the table UI
        retrieveAlarms();

        //Button to return to the home page
        alarmBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearAddAlarmUI();
                AppUtilities.hideKeyboardFrom(getApplicationContext(), alarmBack.getRootView().findFocus());
                Intent mainReturn = new Intent(AlarmSystem.this, MainActivity.class);
                //On return to the MainActivity, clear the previous instance of MainActivity on the stack
                mainReturn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainReturn);
                finish();
            }
        });

        //Button to show the CardView for adding alarms
        alarmAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(addCard.getVisibility() == View.GONE) {
                    addCard.setVisibility(View.VISIBLE);
                    alarmAdd.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.cancel));
                }
                else {
                    AppUtilities.hideKeyboardFrom(getApplicationContext(), alarmAdd.getRootView().findFocus());
                    addCard.setVisibility(View.GONE);
                    alarmAdd.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.add));
                }
            }
        });

        //Text field for entering the alarm hour
        hour.addTextChangedListener(new TextWatcher() {
            int hr;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //If the length is greater than 0 run validity check
                //Else reset the number to check against
                if(s.length() > 0) {
                    hr = Integer.parseInt(s.toString());
                    //If the hour is out of range, validate
                    //Else if it's in range and complete with 2 numbers or the first integer in the hour should not have a following integer, move to minute field
                    if(!(hr >= 0 && hr <= 23 && s.length() <= 2)) {
                        //If number length is greater than 0, simulate backspace pressed
                        //Else clear the output field
                        //This prevents a NullPointerException
                        if(s.length() != 0)
                            hour.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                        else
                            hour.getText().clear();
                    }
                    else if((hr >= 0 && hr <= 23 && s.length() == 2) || (s.length() == 1 && ((hr >= 3 && hr <= 9) || hr == 0))) {
                        hour.clearFocus();
                        minute.requestFocus();
                    }
                }
                else
                    hr = 0;
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        //Text field for entering the alarm minute
        minute.addTextChangedListener(new TextWatcher() {
            int min;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Refer to the hour TextWatcher for validation comments
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

        //Text field for the alarm label
        alarmLabel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Simulate backspace press if the CharSequence length if over 100
                if(s.length() > 100) {
                    alarmLabel.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                    Toast.makeText(AlarmSystem.this, "Label may consist of 100 characters max", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        //Button for saving an alarm
        alarmSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //If both the hour and minute fields are populated, continue
                //Else highlight the EditText's red and ask for re-entry
                if(!(hour.getText().toString().equals("") || minute.getText().toString().equals(""))) {
                    //Build a time to compare the users requested time to the system time
                    Calendar validationDate = AppUtilities.buildTime(Integer.parseInt(hour.getText().toString()), Integer.parseInt(minute.getText().toString()), 0, 0);
                    long now = System.currentTimeMillis();
                    //Store the time of the users alarm in milliseconds for the system to use
                    long alarmTime = validationDate.getTimeInMillis();

                    Log.i("? time comparison", "is " + now + " > " + alarmTime + "?");
                    Log.i("? date == ", validationDate.getTime().toString());

                    //Check if the alarm is set for a time in the past, we do this because if it is the alarm will ring instantly
                    //We do not need to check if it is set to repeat as it will only ring on the days it is set, so we can check if it is due to ring later
                    if (now > alarmTime && !(chkMon.isChecked() || chkTues.isChecked() || chkWed.isChecked() || chkThurs.isChecked() || chkFri.isChecked() || chkSat.isChecked() || chkSun.isChecked())) {
                        Toast.makeText(getApplicationContext(), "Cannot set an alarm for a past time...", Toast.LENGTH_LONG).show();
                    } else {
                        AppUtilities.hideKeyboardFrom(getApplicationContext(), alarmSave.getRootView().findFocus());

                        //This is only used to convert a single integer hour/minute to a 24hr time
                        StringBuilder hr = new StringBuilder(hour.getText().toString());
                        StringBuilder min = new StringBuilder(minute.getText().toString());
                        if(hr.length() == 1)
                            hr.insert(0, 0);
                        if(min.length() == 1)
                            min.insert(0, 0);

                        //Utilize these functions to set up the data we need for the alarm
                        Alarm alarm = createDatabaseInstance(hr.toString(), min.toString(), alarmLabel.getText().toString());
                        scheduleAlarm(alarm, alarmTime);

                        //Update the UI
                        clearAddAlarmUI();
                        retrieveAlarms();
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

    //Update the alarm table in the UI with the alarms in the database
    private void retrieveAlarms() {
        //Show the user the alarms are loading
        updateAlarmsView(View.GONE, View.VISIBLE, View.GONE);

        //Clear the UI and stored alarm data
        alarmTable.removeAllViews();
        alarmList.clear();

        //This part is explained more in-depth in the DatabaseUtilities class

        //Create a query task for the database using the Firebase database reference
        DatabaseUtilities.FirebaseQuery firebaseQuery = new DatabaseUtilities.FirebaseQuery(alarms.myRef);

        //Start the query and have it store the Continuation result of type DataSnapshot
        final Task<DataSnapshot> load = firebaseQuery.start();

        //Add an completeListener to the Task and override the onComplete method so we can
        //get it to do what we need
        load.addOnCompleteListener(new DatabaseUtilities.completeListener() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                super.onComplete(task);
                //If the query was successful, store the data in the List
                //Else return null so the UI knows to say otherwise
                if(task.isSuccessful())
                    for (DataSnapshot snapshot : Objects.requireNonNull(load.getResult()).getChildren())
                        alarmList.add(snapshot.getValue(Alarm.class));
                else
                    alarmList = null;
                //If there are results, list them in the UI
                //Else show there aren't any in the UI
                if(alarmList != null && !alarmList.isEmpty()) {
                    for(Alarm alarm : alarmList)
                        createAlarmTableRow(alarm.getId(), alarm.getTime(), alarm.getLabel(), alarm.getDays());

                    //alarmTable.setLayoutParams(new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT));
                    updateAlarmsView(View.VISIBLE, View.GONE, View.GONE);
                }
                else
                    updateAlarmsView(View.GONE, View.GONE, View.VISIBLE);
            }
        });
    }

    //Used to show/hide parts of the UI based on the parameters
    private void updateAlarmsView(int table, int load, int error) {
        alarmLoad.setVisibility(load);
        alarmTable.setVisibility(table);
        alarmEmpty.setVisibility(error);
    }

    //Used to create an instance of Alarm with the data we need to store
    private Alarm createDatabaseInstance(String hour, String minute, String label) {
        String days = "";
        //Checks what days were checked to add the Calendar value of them to the database
        if(chkMon.isChecked())
            days += " " + Calendar.MONDAY;
        if(chkTues.isChecked())
            days += " " + Calendar.TUESDAY;
        if(chkWed.isChecked())
            days += " " + Calendar.WEDNESDAY;
        if(chkThurs.isChecked())
            days += " " + Calendar.THURSDAY;
        if(chkFri.isChecked())
            days += " " + Calendar.FRIDAY;
        if(chkSat.isChecked())
            days += " " + Calendar.SATURDAY;
        if(chkSun.isChecked())
            days += " " + Calendar.SUNDAY;

        //Create a new Alarm object
        return new Alarm(alarms.newKey(), days.trim(), label, hour + ":" + minute);
    }

    //Create a new row to add to the TableLayout in the alarm UI
    private void createAlarmTableRow(final String id, String time, String label, String days) {
        String output;
        Context context = getApplicationContext();
        int rowId = alarmTable.getChildCount();

        //Create a new row and add it to the TableLayout with the specified width and height
        final TableRow alarmRow = new TableRow(getApplicationContext());
        //Store the ID of the alarm displayed in this row to allow us to know which one the user chooses to delete later
        alarmRow.setTag(id);

        CardView card = AppUtilities.createTableCard(context, 16, 0, 8, 0, 8);

        ConstraintLayout constraintLayout = AppUtilities.createConstraintLayout(context, 1000+rowId, 8, 8, 8, 8);

        //Create a TextView for the alarm time and define its visual parameters
        TextView timeTxt = AppUtilities.createText(context, 1100+rowId, time, 40, R.color.black, Typeface.BOLD);
        constraintLayout.addView(timeTxt);

        //Uses the Calendar days stored in the Firebase database to determine which days, if any, this alarm
        //will repeat on and build an output string
        StringBuilder daysOutput = new StringBuilder("Days:");
        if(!days.equals("")) {
            String[] tokenized = days.split(" ");
            for (String s : tokenized) {
                int d = Integer.parseInt(s);
                if (d == Calendar.MONDAY)
                    daysOutput.append(" MON");
                if (d == Calendar.TUESDAY)
                    daysOutput.append(" TUE");
                if (d == Calendar.WEDNESDAY)
                    daysOutput.append(" WED");
                if (d == Calendar.THURSDAY)
                    daysOutput.append(" THUR");
                if (d == Calendar.FRIDAY)
                    daysOutput.append(" FRI");
                if (d == Calendar.SATURDAY)
                    daysOutput.append(" SAT");
                if (d == Calendar.SUNDAY)
                    daysOutput.append(" SUN");
            }
        }
        else
            daysOutput.append(" NON-REPEATING");
        TextView daysTxt = AppUtilities.createText(context, 1200+rowId, daysOutput.toString(), 12, R.color.black, Typeface.ITALIC);
        constraintLayout.addView(daysTxt);

        //Shows alarm label only if it's defined, otherwise indicate there isn't one
        if(!(label == null || label.equals("")))
            output = "Label: " + label;
        else
            output = "Label: (n/a)";
        TextView labelTxt = AppUtilities.createText(context, 1300+rowId, output, 12, R.color.black, Typeface.ITALIC);
        constraintLayout.addView(labelTxt);

        ImageView editBtn = AppUtilities.createDrawableImage(context, 1400+rowId, R.drawable.edit, 50, 50);
        constraintLayout.addView(editBtn);

        //A button used to delete an alarm shown in the list
        ImageView deleteBtn = AppUtilities.createDrawableImage(context, 1500+rowId, R.drawable.bin, 75, 75);
        constraintLayout.addView(deleteBtn);

        //Add an OnClickListener to the delete button
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Delete the alarm from the database using the ID stored in the view tag
                alarms.delete(String.valueOf(alarmRow.getTag()));

                //To cancel the alarm in the AlarmManager, we need to recreate it exactly how it is created when it's scheduled
                //Refer to scheduleAlarm method to see how it is made
                Intent alarmIntent = new Intent(getApplicationContext(), AlarmRing.class);
                alarmIntent.putExtra("alarmID", String.valueOf(alarmRow.getTag()));
                alarmIntent.setAction("com.app.today.FireAlarm");
                PendingIntent alarmSender = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, 0);

                //Sometimes the PendingIntent may be null before it's deleted
                //this seems to happen when a new instance of AlarmManager is created
                //because I don't think the alarm trigger is implemented properly
                try {
                    alarmManager.cancel(alarmSender);
                    alarmSender.cancel();
                } catch (NullPointerException e) {
                    Log.e("? AlarmManager PendingIntent was null", e.toString());
                }
                retrieveAlarms();
            }
        });

        //ConstraintSet setLayout is used to set constraints on views which we tell it to, you will see setLayout.connect below
        ConstraintSet constraintSet = new ConstraintSet();
        //We need to clone the current condition of the ConstraintLayout to the Set before we can add constraints to them
        constraintSet.clone(constraintLayout);

        //Use setLayout to constrain the timeTxt start to the ConstraintLayout start, and top to top
        constraintSet.connect(timeTxt.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        constraintSet.connect(timeTxt.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP);

        //Constrains the daysTxt to the end and top of timeTxt
        constraintSet.connect(daysTxt.getId(), ConstraintSet.START, timeTxt.getId(), ConstraintSet.END, 8);
        constraintSet.connect(daysTxt.getId(), ConstraintSet.END, editBtn.getId(), ConstraintSet.START, 8);
        constraintSet.connect(daysTxt.getId(), ConstraintSet.TOP, timeTxt.getId(), ConstraintSet.TOP, 22);
        constraintSet.setHorizontalBias(daysTxt.getId(), 0.0f);

        //Constrains the label to the end of timeTxt and bottom of daysTxt, only if labelTxt was defined
        constraintSet.connect(labelTxt.getId(), ConstraintSet.START, timeTxt.getId(), ConstraintSet.END, 8);
        constraintSet.connect(labelTxt.getId(), ConstraintSet.END, editBtn.getId(), ConstraintSet.START, 8);
        constraintSet.connect(labelTxt.getId(), ConstraintSet.TOP, daysTxt.getId(), ConstraintSet.BOTTOM, 8);
        constraintSet.setHorizontalBias(labelTxt.getId(), 0.0f);

        constraintSet.connect(deleteBtn.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP);
        constraintSet.connect(deleteBtn.getId(), ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(deleteBtn.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END);

        constraintSet.connect(editBtn.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP);
        constraintSet.connect(editBtn.getId(), ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(editBtn.getId(), ConstraintSet.END, deleteBtn.getId(), ConstraintSet.START, 16);

        //Applies our constraints we defined using setLayout.connect
        constraintSet.applyTo(constraintLayout);

        card.addView(constraintLayout);
        alarmRow.addView(card);
        alarmTable.addView(alarmRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT, 1.0f));
    }

    //Empties the view to add an alarm
    private void clearAddAlarmUI() {
        hour.getText().clear();
        minute.getText().clear();
        //Uncheck boxes that were checked
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
        addCard.setVisibility(View.GONE);
        alarmAdd.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.add));
    }
    private void scheduleAlarm(Alarm alarm, long alarmTime) {
        //Creates the intent of the activity to be started upon alarm ring
        Intent alarmIntent = new Intent(this, AlarmRing.class);
        //Give the intent a parameter with the ID of the alarm so it can get the data it needs from the database when it rings
        alarmIntent.putExtra("alarmID", alarm.getId());
        //Used to call the receiver AlarmRing when the intent should start
        alarmIntent.setAction("com.app.today.FireAlarm");
        //Create a PendingIntent for the above intent to be started when the alarm should ring
        alarmSender = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, PendingIntent.FLAG_ONE_SHOT);

        Log.i("? attempted to invoke AlarmManager with ID", alarm.getId());
        //Get an instance of the alarm manager to store our alarm
        assert alarmManager != null;
        //Set the alarm to repeat at the same time every day with our PendingIntent, we will check upon ring if
        //the alarm is meant to ring that day, if not then cancel the process
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, alarmTime, AlarmManager.INTERVAL_DAY, alarmSender);

        //Store the alarm data in the database
        alarms.store(alarm);
    }
}