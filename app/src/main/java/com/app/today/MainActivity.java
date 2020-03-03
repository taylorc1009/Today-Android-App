/* TODO
*   Add the users alarm data to a database and display it in the AlarmSystem table
*   - Still need to be able to delete and edit alarms (could use alarmAdd UI to edit?)
*   Add alarm icon scale animation in AlarmActivity
*   Add CardView and/or ScrollingActivity?
*   Fix SignInActivity EditText cursor and handle colour
*  */

package com.app.today;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.HandlerThread;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;
import android.widget.Toolbar;
import com.androdocs.httprequest.HttpRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    static final int MY_PERMISSIONS_REQUEST_READ_CALENDAR = 0;
    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    TextView lastWUpdateTxt, forecastTxt, highsLowsTxt, temperatureTxt, windTxt, calTitle;
    ImageView alarmMore;
    TableLayout calTable;
    Button button;

    List<String> weatherDetails = new ArrayList<>();
    static final String API = "2a2d2e85e492fe3c92b568f4fe3ce854";

    List<Event> calendar = new ArrayList<>();

    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    protected FirebaseUser user;

    private boolean isHome = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = mAuth.getCurrentUser();
        if(user == null) {
            Log.i("! user is NOT signed in, on home page true or false?", String.valueOf(isHome));
            Intent alarmActivity = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(alarmActivity);
            finish();
        } else {
            Toolbar toolbar = findViewById(R.id.action_logOut);
            setActionBar(toolbar);
            Log.i("! user is signed in", Objects.requireNonNull(user.getEmail()));
            lastWUpdateTxt = findViewById(R.id.lastWUpdate);
            forecastTxt = findViewById(R.id.forecast);
            highsLowsTxt = findViewById(R.id.highsLows);
            temperatureTxt = findViewById(R.id.temperature);
            windTxt = findViewById(R.id.windSpeed);
            calTitle = findViewById(R.id.calTitle);
            calTable = findViewById(R.id.calTable);
            alarmMore = findViewById(R.id.alarmMore);
            if(reqPermission(MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION))
                new weatherTask().execute();
            if(reqPermission(MY_PERMISSIONS_REQUEST_READ_CALENDAR))
                updateCalendar();
            alarmMore.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent alarmActivity = new Intent(MainActivity.this, AlarmSystem.class);
                    //alarmActivity.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                    startActivity(alarmActivity);
                }
            });

            button = findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent alarmActivity = new Intent(MainActivity.this, AlarmActivity.class);
                    startActivity(alarmActivity);
                    finish();
                }
            });
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        isHome = true;
    }
    @Override
    protected void onPause() {
        super.onPause();
        isHome = false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logOut:
                mAuth.signOut();
                Intent signIn = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(signIn);
                finish();
                return true;
            case R.id.action_refreshWeather:
                if(reqPermission(MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)) {
                    new weatherTask().execute();
                    Toast.makeText(MainActivity.this, "!INFO: If refresh fails, there's no new weather data to pull or the API request limit for today has been reached", Toast.LENGTH_LONG).show();
                }
            case R.id.action_refreshCalendar:
                if(reqPermission(MY_PERMISSIONS_REQUEST_READ_CALENDAR))
                    updateCalendar();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    class weatherTask extends AsyncTask<String, Void, String> {
        private double longitude, latitude;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            updateWeather(View.VISIBLE, View.GONE, View.GONE, View.GONE);
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
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                HandlerThread t = new HandlerThread("handlerThread");
                t.start();
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener, t.getLooper());
                //t.quit(); <-- causes a dead thread warning, critical?
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
                if(!weatherDetails.isEmpty()) {
                    forecastTxt.setText(weatherDetails.get(0).toUpperCase());
                    temperatureTxt.setText(weatherDetails.get(1));
                    highsLowsTxt.setText(weatherDetails.get(2));
                    windTxt.setText(weatherDetails.get(3));
                    lastWUpdateTxt.setText(weatherDetails.get(4));
                    updateWeather(View.GONE, View.VISIBLE, View.VISIBLE, View.GONE);
                } else
                    updateWeather(View.GONE, View.VISIBLE, View.GONE, View.VISIBLE);
            } catch (JSONException e) {
                updateWeather(View.GONE, View.VISIBLE, View.GONE, View.VISIBLE);
            }
        }
    }
    private void updateWeather(int load, int card, int group, int error ) {
        findViewById(R.id.weatherLoad).setVisibility(load);
        findViewById(R.id.weatherCard).setVisibility(card);
        findViewById(R.id.weatherStats).setVisibility(group);
        findViewById(R.id.weatherError).setVisibility(error);
    }
    private void updateCalendar() {
        calTable.removeAllViews();
        calendar.clear();
        CalendarContentResolver resolver = new CalendarContentResolver();
        calendar = resolver.getCalendar(this);
        if(calendar != null) {
            if(!calendar.isEmpty()) {
                for(int i = 0; i < calendar.size(); i++) {
                    Event event = calendar.get(i);
                    TableRow eventRow = new TableRow(getApplicationContext());
                    eventRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    TextView timeTxt = new TextView(getApplicationContext());
                    String output;
                    if(!event.isAllDay())
                        output = event.getTitle() + " | " + resolver.getTimeString(event.getBegin().getTime()) + "-" + resolver.getTimeString(event.getEnd().getTime());
                    else
                        output = event.getTitle() + " | All day";
                    timeTxt.setText(output);
                    timeTxt.setTextSize(14);
                    TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                    params.setMargins(30, 15, 0, 8);
                    timeTxt.setLayoutParams(params);

                    calTitle.setText(R.string.calTitle);
                    eventRow.addView(timeTxt);
                    calTable.addView(eventRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                }
            } else
                calTitle.setText(R.string.calEmpty);
        }
        else
            calTitle.setText(R.string.calError);
    }
    private boolean reqPermission(int p) {
        switch(p) {
            case MY_PERMISSIONS_REQUEST_READ_CALENDAR:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    /*if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CALENDAR)) {
                    } else { */
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, MY_PERMISSIONS_REQUEST_READ_CALENDAR);
                } else { return true; }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    return true;
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    /*if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    } else { */
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                } else { return true; }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    return true;
        }
        return false;
    }
}