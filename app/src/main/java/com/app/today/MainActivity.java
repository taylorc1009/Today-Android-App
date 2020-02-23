package com.app.today;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
//import androidx.core.app.CoreComponentFactory; // --> unused but the runtime needs it
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;
import com.androdocs.httprequest.HttpRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.app.today.Event;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    static final int MY_PERMISSIONS_REQUEST_READ_CALENDAR = 0;
    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    TextView lastWUpdateTxt, forecastTxt, highsLowsTxt, temperatureTxt, windTxt, event1Txt, event2Txt, event3Txt, calTitle;
    static FloatingActionButton alarmMore;

    List<String> weatherDetails = new ArrayList<>();
    static final String API = "2a2d2e85e492fe3c92b568f4fe3ce854";

    //List<Event> calendar = new ArrayList<>();
    protected static List<String> nameOfEvent = new ArrayList<>();
    protected static List<String> startDates = new ArrayList<>();
    protected static List<String> endDates = new ArrayList<>();
    protected static List<String> descriptions = new ArrayList<>();

    Button AlarmTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lastWUpdateTxt = findViewById(R.id.lastWUpdate);
        forecastTxt = findViewById(R.id.forecast);
        highsLowsTxt = findViewById(R.id.highsLows);
        temperatureTxt = findViewById(R.id.temperature);
        windTxt = findViewById(R.id.windSpeed);
        calTitle = findViewById(R.id.calTitle);
        event1Txt = findViewById(R.id.event1txt);
        event2Txt = findViewById(R.id.event2txt);
        event3Txt = findViewById(R.id.event3txt);
        alarmMore = findViewById(R.id.alarmMore);

        AlarmTest = findViewById(R.id.button);
        AlarmTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent alarmActivity = new Intent(MainActivity.this, AlarmActivity.class);
                startActivity(alarmActivity);
            }
        });

        if (reqPermission(MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION))
            new weatherTask().execute();
        if (reqPermission(MY_PERMISSIONS_REQUEST_READ_CALENDAR)) {
            new CalendarContentResolver(getApplicationContext());
            updateCalendar();
        }
        alarmMore.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "pressed", Toast.LENGTH_LONG).show();
                Intent alarmActivity = new Intent(MainActivity.this, AlarmSystem.class);
                startActivity(alarmActivity);
            }
        });
    }
    class weatherTask extends AsyncTask<String, Void, String> {
        private double longitude, latitude;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.weatherLoad).setVisibility(View.VISIBLE);
            findViewById(R.id.weatherGroup).setVisibility(View.GONE);
            findViewById(R.id.weatherError).setVisibility(View.GONE);
        }
        @Override
        public String doInBackground(String... args) {
            //return HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?q=Edinburgh,GB&APPID=" + API);
            final LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}
                @Override
                public void onProviderEnabled(String provider) {}
                @Override
                public void onProviderDisabled(String provider) {}
            };
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                HandlerThread t = new HandlerThread("handlerThread");
                t.start();
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener, t.getLooper());
                //t.quit();
                return HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&APPID=" + API);
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject wind = jsonObj.getJSONObject("wind");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);
                long updatedAt = jsonObj.getLong("dt");
                weatherDetails.add(weather.getString("description"));
                weatherDetails.add(main.getString("temp") + "°C");
                weatherDetails.add(main.getString("temp_min") + "°C min - " + main.getString("temp_max") + "°C max");
                weatherDetails.add(wind.getString("speed") + " mph winds");
                weatherDetails.add("last updated: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000)));
                if (!weatherDetails.isEmpty()) {
                    forecastTxt.setText(weatherDetails.get(0).toUpperCase());
                    temperatureTxt.setText(weatherDetails.get(1));
                    highsLowsTxt.setText(weatherDetails.get(2));
                    windTxt.setText(weatherDetails.get(3));
                    lastWUpdateTxt.setText(weatherDetails.get(4));
                    findViewById(R.id.weatherLoad).setVisibility(View.GONE);
                    findViewById(R.id.weatherGroup).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.weatherLoad).setVisibility(View.GONE);
                    findViewById(R.id.weatherError).setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                findViewById(R.id.weatherLoad).setVisibility(View.GONE);
                findViewById(R.id.weatherError).setVisibility(View.VISIBLE);
            }
        }
    }
    private void updateCalendar() {
        //if(calendar.isEmpty()) {
        Resources resources = this.getResources();
        if(nameOfEvent.isEmpty()) {
            calTitle.setText(resources.getString(R.string.calError));
            findViewById(R.id.calTable).setVisibility(View.GONE);
            //Log.i("calendar", calendar.get(0).getTitle());
        }
        else {
            //if (CalendarContentResolver.compareDate(calendar.get(0).getStartDate())) {
            if(CalendarContentResolver.compareDate(startDates.get(0))) {
                calTitle.setText(resources.getString(R.string.calEmpty));
                findViewById(R.id.calTable).setVisibility(View.GONE);
                //Log.i("startDates 0 ==", startDates.get(1));
                //event1Txt.setText("Coming up soon: " + nameOfEvent.get(0) + ", " + startDates.get(0));
            } else {
                calTitle.setText(resources.getString(R.string.calTitle));
                findViewById(R.id.calTable).setVisibility(View.VISIBLE);
                event1Txt.setText(nameOfEvent.get(0) + ", " + startDates.get(0));
                if (CalendarContentResolver.compareDate(startDates.get(1))) {
                    findViewById(R.id.calSep2).setVisibility(View.GONE);
                    event2Txt.setVisibility(View.GONE);
                    findViewById(R.id.calSep3).setVisibility(View.GONE);
                    event3Txt.setVisibility(View.GONE);
                    //event2Txt.setText("Coming up soon: " + nameOfEvent.get(1) + ", " + startDates.get(1));
                } else {
                    findViewById(R.id.calSep2).setVisibility(View.VISIBLE);
                    event2Txt.setVisibility(View.VISIBLE);
                    event2Txt.setText(nameOfEvent.get(1) + ", " + startDates.get(1));
                    if (CalendarContentResolver.compareDate(startDates.get(2))) {
                        findViewById(R.id.calSep3).setVisibility(View.GONE);
                        event3Txt.setVisibility(View.GONE);
                        //event3Txt.setText("Coming up soon: " + nameOfEvent.get(2) + ", " + startDates.get(2));
                    } else {
                        findViewById(R.id.calSep3).setVisibility(View.VISIBLE);
                        event3Txt.setVisibility(View.VISIBLE);
                        event3Txt.setText(nameOfEvent.get(2) + ", " + startDates.get(2));
                    }
                }
            }
        }
    }
    private boolean reqPermission(int p) {
        switch(p) {
            case MY_PERMISSIONS_REQUEST_READ_CALENDAR:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CALENDAR)) {
                    } else { ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, MY_PERMISSIONS_REQUEST_READ_CALENDAR); }
                } else { return true; }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    return true;
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    } else { ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION); }
                } else { return true; }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    return true;
        }
        return false;
    }
}