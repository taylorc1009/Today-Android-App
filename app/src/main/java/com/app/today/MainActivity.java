/*
*       40398643 | Taylor Courtney
* */

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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.HandlerThread;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;
import android.widget.Toolbar;
import com.androdocs.httprequest.HttpRequest;
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
    //Used to determine which permission we're asking for
    static final int MY_PERMISSIONS_REQUEST_READ_CALENDAR = 0;
    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    //UI attributes
    TextView lastWUpdateTxt, forecastTxt, highsLowsTxt, temperatureTxt, windTxt, calTitle, newsTitle;
    ImageView alarmMore, weatherIcon;
    TableLayout calTable, newsTable;

    //Used to get and store weather details
    private static final String weatherAPI = "2a2d2e85e492fe3c92b568f4fe3ce854";

    //Used to verify Firebase sign in
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    protected FirebaseUser user;

    //Used to hardcode the location for accurate weather results
    private boolean hardcodeLoc = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Acquires the current user and determines whether they are logged in or not
        user = mAuth.getCurrentUser();
        if(user == null) {
            //If not, go to sign in page
            Intent alarmActivity = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(alarmActivity);
            finish();
        } else {
            //If they are, begin initializing the homepage

            //Used to add an options menu to the ActionBar
            Toolbar toolbar = findViewById(R.id.action_logOut);
            setActionBar(toolbar);

            Log.i("! user is signed in", Objects.requireNonNull(user.getEmail()));

            //Initialize UI attributes
            weatherIcon = findViewById(R.id.weatherIcon);
            lastWUpdateTxt = findViewById(R.id.lastWUpdate);
            forecastTxt = findViewById(R.id.forecast);
            highsLowsTxt = findViewById(R.id.highsLows);
            temperatureTxt = findViewById(R.id.temperature);
            windTxt = findViewById(R.id.windSpeed);
            calTitle = findViewById(R.id.calTitle);
            calTable = findViewById(R.id.calTable);
            alarmMore = findViewById(R.id.alarmMore);
            newsTitle = findViewById(R.id.newsTitle);
            newsTable = findViewById(R.id.newsTable);

            //Request required permissions then begin UI operations
            if(reqPermission(MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION))
                new weatherTask().execute();
            if(reqPermission(MY_PERMISSIONS_REQUEST_READ_CALENDAR))
                updateCalendarView();
            new headlineReceiver().execute();

            //If the user clicks the Alarms "+", go to AlarmSystem
            alarmMore.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent alarmActivity = new Intent(MainActivity.this, AlarmSystem.class);
                    startActivity(alarmActivity);
                }
            });
        }
    }

    //Uses our 'menu.xml' file to define the ActionBar menu and pushes it to the display
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Defines the actions for each option in the action bar menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //It receives a value passed on-click of the ActionBar menu and determines what to do by using a switch case
        switch (item.getItemId()) {
            case R.id.action_logOut:
                //Signs the user out and returns them to the sign in page
                mAuth.signOut();
                Intent signIn = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(signIn);
                finish();
                return true;
            case R.id.action_hardcodeWeather:
                item.setChecked(!item.isChecked());
                hardcodeLoc = item.isChecked();
            case R.id.action_refreshWeather:
                //Refreshes the weather (requests location access beforehand)
                if(reqPermission(MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)) {
                    new weatherTask().execute();
                    Toast.makeText(MainActivity.this, "! INFO: If refresh fails, there's no new weather data to pull or the API request limit for today has been reached", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.action_refreshCalendar:
                //Refreshes the calendar
                if(reqPermission(MY_PERMISSIONS_REQUEST_READ_CALENDAR))
                    updateCalendarView();
                return true;
            case R.id.action_refreshNews:
                //Refreshes the news headlines
                new headlineReceiver().execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //AsyncTask for getting the weather, this is used to determine actions for before (onPreExecute) and after (onPostExecute) another
    //action (doInBackground, which as the name implies can be done in the background)
    class weatherTask extends AsyncTask<String, Void, String> {
        //Attributes defined globally for this subclass so they can be accessed by inner classes
        private double longitude, latitude;

        //onPreExecute instructs the UI to show that the weather is loading
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            updateWeatherView(View.VISIBLE, View.GONE, View.GONE, View.GONE);
        }

        //doInBackground gets the users location then uses it to retrieve the weather in their location
        @Override
        protected String doInBackground(String... args) {
            if(hardcodeLoc)
                return HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?q=Edinburgh&units=metric&APPID=" + weatherAPI);
            else {
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
                //The GPS permission is required for the completion of weather retrieval
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //Creates an instance of the location service and retrieves the last know location
                    //We do this so the system can determine later if there's a location update, and if
                    //there is, request a weather update for the new location
                    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    assert lm != null;
                    lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    HandlerThread t = new HandlerThread("handlerThread");
                    t.start();
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener, t.getLooper());
                    //t.quit(); <-- causes a dead thread warning, critical?
                    return HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&units=metric&APPID=" + weatherAPI);
                }
            }
            return null;
        }

        //onPostExecute basically handles the weather response:
        @Override
        protected void onPostExecute(String result) {
            //If there's weather data to use, i.e. the .json response isn't empty, try to update the UI too
            //Else show the error message (in the catch)
            try {
                //Retrieves the .json result
                JSONObject jsonObj = new JSONObject(result);

                //Organises the results into suitable Objects
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject wind = jsonObj.getJSONObject("wind");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);
                long updatedAt = jsonObj.getLong("dt");

                List<String> weatherDetails = new ArrayList<>();

                //Add the objects main values into a list of strings to be displayed
                weatherDetails.add(weather.getString("description"));
                weatherDetails.add(main.getString("temp")  + "°C");
                weatherDetails.add(main.getString("temp_min") + "°C min / " + main.getString("temp_max") + "°C max");
                weatherDetails.add(wind.getString("speed") + " mph winds");
                weatherDetails.add("last updated: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000)));

                int weatherID = Integer.parseInt(weather.getString("id"));
                //Based on the weather ID, this will determine which drawable weather icon to use
                if(weatherID == 800)
                    weatherIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.clear));
                else if(weatherID == 801)
                    weatherIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.fair));
                else if(weatherID == 802)
                    weatherIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.lightclouds));
                else if(weatherID == 803 || weatherID == 804)
                    weatherIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.clouds));
                else if(weatherID >= 500 && weatherID <= 504)
                    weatherIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.lightrain));
                else if(weatherID == 511 || (weatherID >= 600 && weatherID <= 602) || (weatherID >= 611 && weatherID <= 613) || weatherID == 615 || weatherID == 616 || (weatherID >= 620 && weatherID <= 622))
                    weatherIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ice));
                else if((weatherID >= 520 && weatherID <= 522) || weatherID == 531 || (weatherID >= 300 && weatherID <= 302) || (weatherID >= 310 && weatherID <= 314) || weatherID == 321)
                    weatherIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rain));
                else if((weatherID >= 200 && weatherID <= 202) || (weatherID >= 210 && weatherID <= 212) || weatherID == 221 || (weatherID >= 230 && weatherID <= 232))
                    weatherIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.storm));
                else if(weatherID == 701 || weatherID == 711 || weatherID == 721 || weatherID == 731 || weatherID == 741 || weatherID == 751 || weatherID == 761 || weatherID == 762 || weatherID == 771 || weatherID == 781)
                    weatherIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.atmosphere));
                else
                    weatherIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.info));

                //If .json response isn't empty, display the response in the suitable UI views
                //Else throw an empty .json message
                if(!weatherDetails.isEmpty()) {
                    forecastTxt.setText(weatherDetails.get(0).toUpperCase());
                    temperatureTxt.setText(weatherDetails.get(1));
                    highsLowsTxt.setText(weatherDetails.get(2));
                    windTxt.setText(weatherDetails.get(3));
                    lastWUpdateTxt.setText(weatherDetails.get(4));
                    updateWeatherView(View.GONE, View.VISIBLE, View.VISIBLE, View.GONE);
                } else
                    throw new JSONException("Weather JSON empty... was HttpRequest unsuccessful?");
            } catch (JSONException e) {
                Log.e("? weather JSONException", ".json empty?", e);
                updateWeatherView(View.GONE, View.VISIBLE, View.GONE, View.VISIBLE);
            }
        }
    }

    //Used to hide/show certain views based on the parameters received
    private void updateWeatherView(int load, int card, int group, int error) {
        findViewById(R.id.weatherLoad).setVisibility(load);
        findViewById(R.id.weatherCard).setVisibility(card);
        findViewById(R.id.weatherStats).setVisibility(group);
        findViewById(R.id.weatherError).setVisibility(error);
    }

    //Used to handle the response from the CalendarContentResolver
    private void updateCalendarView() {
        //First, clear the table of calendar events in the UI
        calTable.removeAllViews();

        //Create a CalendarContentResolver to query and return the events of the calendar
        CalendarContentResolver resolver = new CalendarContentResolver();
        List<Event> calendar = resolver.getCalendar(this);

        //If the calendar permission was granted, then this should continue
        //Else show error message
        if(calendar != null) {
            //If there are events happening today, add them to the UI table
            //Else show there are no events today
            if(!calendar.isEmpty()) {
                calTitle.setText(R.string.calTitle);
                for(Event event : calendar) {
                    //Creates a new TableRow to be added to the table
                    TableRow eventRow = new TableRow(getApplicationContext());

                    //Creates and defines a TextView to display an event
                    TextView eventTxt = new TextView(getApplicationContext());
                    String output;
                    //If the current event is not happening all day, display its start and end times
                    //Else display it as an all day event
                    if(!event.isAllDay())
                        output = event.getTitle() + " | " + resolver.getTimeString(event.getBegin().getTime()) + "-" + resolver.getTimeString(event.getEnd().getTime());
                    else
                        output = event.getTitle() + " | All day";
                    eventTxt.setText(output);
                    eventTxt.setTextSize(15);
                    //Define the layout parameters and margins of the event text view
                    //LayoutParameters are basically the width, height and weight (number of lines) of the display
                    TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
                    params.setMargins(0, 12, 0, 8);
                    eventTxt.setLayoutParams(params);

                    //Adds the text to the row, then the row to the table with the layout parameters
                    eventRow.addView(eventTxt);
                    calTable.addView(eventRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                }
            } else
                calTitle.setText(R.string.calEmpty);
        }
        else
            calTitle.setText(R.string.calError);
    }

    //AsyncTask for getting headlines
    class headlineReceiver extends AsyncTask<String, Void, List<Headline>> {
        //onPreExecute shows the user the weather is loading
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            updateHeadlinesView(View.VISIBLE, View.GONE, View.GONE);
        }

        //doInBackground retrieves headlines and passes them to onPostExecute
        @Override
        protected List<Headline> doInBackground(String... strings) {
            return HeadlineReceiver.getHeadlines();
        }

        //onPostExecutes shows the results to the user
        @Override
        protected void onPostExecute(List<Headline> result) {
            //Clean the table before new data is presented
            newsTable.removeAllViews();
            //If results aren't empty, display them in the headline table
            //Else show headline error
            if(result != null) {
                //Integer used just to label the headlines with a number
                int i = 0;
                for(final Headline headline : result) {
                    //This if is only used to prevent useless headlines from being displayed, for some reason The Guardian API gives us them
                    if(!(headline.getTitle().equals("Corrections and clarifications") || headline.getTitle().equals("This week’s correction | For the record"))) {
                        i++;
                        //Creates a new table row, defines the headline TextView and adds it to the row, and then the row to the table
                        TableRow newsRow = new TableRow(getApplicationContext());
                        TextView titleTxt = new TextView(getApplicationContext());
                        String output = i + ". " + headline.getTitle();
                        titleTxt.setText(output);
                        titleTxt.setTextSize(15);
                        titleTxt.setPadding(0, 15, 0, 0);
                        titleTxt.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));

                        newsRow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String url = headline.getUrl();
                                if (!url.startsWith("http://") && !url.startsWith("https://"))
                                    url = "http://" + url;
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(browserIntent);
                            }
                        });

                        newsRow.addView(titleTxt);
                        newsTable.addView(newsRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    }
                }
                newsTitle.setText(R.string.newsTitle);
                updateHeadlinesView(View.GONE, View.VISIBLE, View.VISIBLE);
            } else {
                newsTitle.setText(R.string.newsError);
                updateHeadlinesView(View.GONE, View.VISIBLE, View.GONE);
            }
        }
    }

    //Used to hide/show certain views based on the parameters
    private void updateHeadlinesView(int load, int card, int table) {
        findViewById(R.id.newsLoad).setVisibility(load);
        findViewById(R.id.newsCard).setVisibility(card);
        newsTable.setVisibility(table);
    }

    //Used by the app to request a permission (p)
    private boolean reqPermission(int p) {
        switch(p) {
            case MY_PERMISSIONS_REQUEST_READ_CALENDAR:
                //If the permission is not already granted, request it
                //Else return true/granted
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, MY_PERMISSIONS_REQUEST_READ_CALENDAR);
                } else { return true; }
                //Checks again after the request was made to check the result
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    return true;
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                } else { return true; }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    return true;
        }
        //If the permission was not granted, returns false
        return false;
    }
}