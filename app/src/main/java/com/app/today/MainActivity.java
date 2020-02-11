package com.app.today;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    static final int MY_PERMISSIONS_REQUEST_READ_CALENDAR = 1;
    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    TextView lastWUpdateTxt, forecastTxt, highsLowsTxt, temperatureTxt, windTxt;
    List<String> weatherDetails = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lastWUpdateTxt = findViewById(R.id.lastWUpdate);
        forecastTxt = findViewById(R.id.forecast);
        highsLowsTxt = findViewById(R.id.highsLows);
        temperatureTxt = findViewById(R.id.temperature);
        //temp_minTxt = findViewById(R.id.temp_min);
        //temp_maxTxt = findViewById(R.id.temp_max);
        //sunriseTxt = findViewById(R.id.sunrise);
        //sunsetTxt = findViewById(R.id.sunset);
        windTxt = findViewById(R.id.windSpeed);

        WeatherReceiver receiver = new WeatherReceiver();


        /* Populating extracted data into our views */
        //addressTxt.setText(address);
        forecastTxt.setText(weatherDetails.get(0).toUpperCase());
        temperatureTxt.setText(weatherDetails.get(1));
        highsLowsTxt.setText(weatherDetails.get(2));
        windTxt.setText(weatherDetails.get(3));
        lastWUpdateTxt.setText(weatherDetails.get(4));

        //temp_minTxt.setText(tempMin);
        //temp_maxTxt.setText(tempMax);
        //sunriseTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunrise * 1000)));
        //sunsetTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunset * 1000)));
        //pressureTxt.setText(pressure);
        //humidityTxt.setText(humidity);

        /* Views populated, Hiding the loader, Showing the main design */
        findViewById(R.id.weatherLoad).setVisibility(View.GONE);
        findViewById(R.id.weatherGroup).setVisibility(View.VISIBLE);

        if (checkSelfPermission(Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            CalendarContentResolver calendar = new CalendarContentResolver(this);
            Set<String> CalSet = calendar.getCalendars();
        }
    }
}
    /*public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CALENDAR: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(MainActivity.this, "Calendar permission GRANTED", Toast.LENGTH_LONG);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Calendar permission denied...", Toast.LENGTH_LONG);
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Location permission GRANTED", Toast.LENGTH_LONG);
                } else {
                    Toast.makeText(MainActivity.this, "Location permission denied...", Toast.LENGTH_LONG);
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }*/