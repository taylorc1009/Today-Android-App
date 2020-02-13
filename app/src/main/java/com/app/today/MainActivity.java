package com.app.today;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.app.CoreComponentFactory; // --> unused but the runtime needs it
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.androdocs.httprequest.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    static final int MY_PERMISSIONS_REQUEST_READ_CALENDAR = 0;
    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    TextView lastWUpdateTxt, forecastTxt, highsLowsTxt, temperatureTxt, windTxt;
    List<String> weatherDetails = new ArrayList<>();
    static final String API = "2a2d2e85e492fe3c92b568f4fe3ce854";
    private double longitude, latitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lastWUpdateTxt = findViewById(R.id.lastWUpdate);
        forecastTxt = findViewById(R.id.forecast);
        highsLowsTxt = findViewById(R.id.highsLows);
        temperatureTxt = findViewById(R.id.temperature);
        windTxt = findViewById(R.id.windSpeed);

        class weatherTask extends AsyncTask<String, Void, String> {
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
                    Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());
                    @Override
                    public void onLocationChanged(Location location) {
                        /*final String result = doSomeLongRuningOperation(location);
                        MAIN_HANDLER.post( new Runnable() {
                            @Override
                            public void run() {
                                doSomeOperationOnUIThread(result):
                            }
                        });*/
                        longitude = location.getLongitude();
                        latitude = location.getLatitude();
                    }
                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }
                    @Override
                    public void onProviderEnabled(String provider) {

                    }
                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    HandlerThread t = new HandlerThread("handlerThread");
                    t.start();
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener, t.getLooper());
                    if(location != null)
                        return HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&APPID=" + API);
                        //return HttpRequest.excuteGet("https://api.darksky.net/" + API + "/" + latitude + "," + longitude);
                        //return HttpRequest.excuteGet("https://api.darksky.net/forecast/fc7495b2595be4d034d506c6a16bcda3/37.8267,-122.4233");
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
                    Long updatedAt = jsonObj.getLong("dt");
                    weatherDetails.add(weather.getString("description"));
                    weatherDetails.add(main.getString("temp") + "°C");
                    weatherDetails.add(main.getString("temp_min") + "/" + main.getString("temp_max") + "°C");
                    weatherDetails.add(wind.getString("speed"));
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
        if (reqPermission(MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION))
            new weatherTask().execute(); //--> http request fails
        if (reqPermission(MY_PERMISSIONS_REQUEST_READ_CALENDAR)) {
            CalendarContentResolver calendar = new CalendarContentResolver(getApplicationContext());
            Set<String> CalSet = calendar.getCalendar();
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