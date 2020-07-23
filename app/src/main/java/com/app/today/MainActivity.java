/*  40398643 | Taylor Courtney
 *
 *  TODO
 *   Make requestLocationUpdates into a task, so we do not try to display weather results until we have a location
 *   Interchangeable weather units
 *   Add a wait between alarmGreet animIn and animOut
 *   Fix alarm not ringing on app kill
 *   - Add an edit alarm option to AlarmSystem
 *   Add tabs? Home and Alarms
 *   - A side menu may be better, so you can have the log out option in there too
 *   Add API keys to a JSON file and encrypt it?
 *   - Could do the same with the google-services.json
 *   It would be cool to make the weather details do the slide up with scroll down animation you see in, for example, Spotify playlist titles
 *   Get reminders from calendar
 *   - Possibly more details too, for example locations
 *   - Improve calendar event layouts
 *   Add a feature to view a sample of news reports?
 *   Fix EditText handle colours in SignInActivity
 *   Add a sliding down expand animation
 *   Move the change alpha animation to AppUtilities
 * */

package com.app.today;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import androidx.viewpager2.widget.ViewPager2;
import me.relex.circleindicator.CircleIndicator3;

public class MainActivity extends AppCompatActivity {
    //Used to determine which permission we're asking for
    static final int PERMISSIONS_REQUEST_CODE = 0;

    //UI attributes
    TextView lastWUpdateTxt, forecastTxt, highsTxt, lowsTxt, temperatureTxt, windTxt, calTitle, newsTitle, weatherError;
    ImageView alarmMore, weatherIcon, logOut, refresh;
    ConstraintLayout weatherStats, calendarDetails, newsHeadingGroup;
    ProgressBar weatherLoad, calLoad, newsLoad;
    TableLayout calTable;
    ViewPager2 headlinePager;
    CircleIndicator3 headlineIndicator;

    //OpenWeatherMap key, used to get the weather details
    private static final String WEATHER_API = "2a2d2e85e492fe3c92b568f4fe3ce854";
    //The Guardian API key, used to get news headlines
    private static final String NEWS_API = "07f8c2ea-493e-4429-ae47-74ade74d113c";

    //Used to verify Firebase sign in
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    protected FirebaseUser user;

    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Acquires the current user and determines whether they are logged in or not
        user = mAuth.getCurrentUser();

        if (user == null) {
            //If not, go to sign in page
            Intent alarmActivity = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(alarmActivity);
            finish();
        } else {
            //If they are, begin initializing the homepage
            Log.i("! user is signed in", Objects.requireNonNull(user.getEmail()));

            //Initialize UI attributes
            weatherLoad = findViewById(R.id.weatherLoad);
            weatherStats = findViewById(R.id.weatherStats);
            weatherError = findViewById(R.id.weatherError);
            weatherIcon = findViewById(R.id.weatherIcon);
            lastWUpdateTxt = findViewById(R.id.lastWUpdate);
            forecastTxt = findViewById(R.id.forecast);
            highsTxt = findViewById(R.id.highsTxt);
            lowsTxt = findViewById(R.id.lowsTxt);
            temperatureTxt = findViewById(R.id.temperature);
            windTxt = findViewById(R.id.windSpeed);
            calLoad = findViewById(R.id.calLoad);
            calendarDetails = findViewById(R.id.calendarDetails);
            calTitle = findViewById(R.id.calTitle);
            calTable = findViewById(R.id.calTable);
            alarmMore = findViewById(R.id.alarmIcon);
            newsTitle = findViewById(R.id.newsTitle);
            newsLoad = findViewById(R.id.newsLoad);
            newsHeadingGroup = findViewById(R.id.newsHeadingGroup);
            headlinePager = findViewById(R.id.headlinePager);
            headlineIndicator = findViewById(R.id.headlineIndicator);
            logOut = findViewById(R.id.logOut);
            refresh = findViewById(R.id.refresh);

            //If the user clicks the alarm icon, go to AlarmSystem
            alarmMore.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent alarmActivity = new Intent(MainActivity.this, AlarmSystem.class);
                    startActivity(alarmActivity);
                }
            });
            logOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Signs the user out and returns them to the sign in page
                    mAuth.signOut();
                    Intent signIn = new Intent(MainActivity.this, SignInActivity.class);
                    startActivity(signIn);
                    finish();
                }
            });
            refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> permissionsList = new ArrayList<>();

                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                        weatherTask();
                    else {
                        updateWeatherView(View.VISIBLE, View.GONE, View.GONE);
                        permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
                    }
                    if (checkSelfPermission(Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED)
                        calendarTask();
                    else {
                        updateCalendarView(View.VISIBLE, View.GONE);
                        permissionsList.add(Manifest.permission.READ_CALENDAR);
                    }
                    headlineTask();

                    if(!permissionsList.isEmpty())
                        requestPermissions(permissionsList.toArray(new String[0]), PERMISSIONS_REQUEST_CODE);
                }
            });

            headlineTask();
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CALENDAR}, PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Converts the array to a list making it easier to manage permission results (by using contains
        //and indexOf; we don't want to check results for permissions we didn't request)
        ArrayList<String> permissionsList = new ArrayList<>(Arrays.asList(permissions));

        //TODO
        // When testing this, permissions don't appear to be requested in the order they're stored in
        // the String[] parameter, so could this affect the correlation between the index of a
        // permission and its grantResult index?

        //These permission result statements are in place to prevent any execution of these methods
        //if the user denied permission(s)
        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.length > 0) {
            if(permissionsList.contains(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[permissionsList.indexOf(Manifest.permission.ACCESS_FINE_LOCATION)] == PackageManager.PERMISSION_GRANTED)
                    weatherTask();
                else
                    updateWeatherView(View.GONE, View.GONE, View.VISIBLE);
            }

            if(permissionsList.contains(Manifest.permission.READ_CALENDAR)) {
                if (grantResults[permissionsList.indexOf(Manifest.permission.READ_CALENDAR)] == PackageManager.PERMISSION_GRANTED)
                    calendarTask();
                else {
                    calTitle.setText(getString(R.string.calError));
                    updateCalendarView(View.GONE, View.VISIBLE);
                }
            }
        }
    }

    private void weatherTask() {
        //This method is executed within a new thread as it downloads data, thus ending after we want the
        //UI to be drawn; if we left this method to execute on the UI thread then it would freeze all
        //parent procedures (including drawing of UI widgets) until this one is finished (because a single
        //thread executes methods in a serial fashion, nothing runs in parallel)
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeatherView(View.VISIBLE, View.GONE, View.GONE);
                final List<String> weatherDetails = weatherReceiver();

                if (weatherDetails != null && !weatherDetails.isEmpty()) {
                    weatherIcon.post(new Runnable() {
                        @Override
                        public void run() {
                            int weatherID = Integer.parseInt(weatherDetails.get(6));
                            //Based on the weather ID, this will determine which drawable weather icon to use
                            if (weatherID == 800)
                                weatherIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.clear));
                            else if (weatherID == 801)
                                weatherIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.fair));
                            else if (weatherID == 802)
                                weatherIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.lightclouds));
                            else if (weatherID == 803 || weatherID == 804)
                                weatherIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.clouds));
                            else if (weatherID >= 500 && weatherID <= 504)
                                weatherIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.lightrain));
                            else if (weatherID == 511 || (weatherID >= 600 && weatherID <= 602) || (weatherID >= 611 && weatherID <= 613) || weatherID == 615 || weatherID == 616 || (weatherID >= 620 && weatherID <= 622))
                                weatherIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ice));
                            else if ((weatherID >= 520 && weatherID <= 522) || weatherID == 531 || (weatherID >= 300 && weatherID <= 302) || (weatherID >= 310 && weatherID <= 314) || weatherID == 321)
                                weatherIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rain));
                            else if ((weatherID >= 200 && weatherID <= 202) || (weatherID >= 210 && weatherID <= 212) || weatherID == 221 || (weatherID >= 230 && weatherID <= 232))
                                weatherIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.storm));
                            else if (weatherID == 701 || weatherID == 711 || weatherID == 721 || weatherID == 731 || weatherID == 741 || weatherID == 751 || weatherID == 761 || weatherID == 762 || weatherID == 771 || weatherID == 781)
                                weatherIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.atmosphere));
                            else
                                weatherIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.info));
                        }
                    });

                    //Displays our weather details; this method is invoked post-drawing (in a different
                    //thread) of the weather widgets in order to avoid halting the UI thread for this
                    //method to finish executing
                    forecastTxt.post(new Runnable() {
                        @Override
                        public void run() {
                            forecastTxt.setText(weatherDetails.get(0).toUpperCase());
                        }
                    });
                    temperatureTxt.post(new Runnable() {
                        @Override
                        public void run() {
                            temperatureTxt.setText(weatherDetails.get(1));
                        }
                    });
                    highsTxt.post(new Runnable() {
                        @Override
                        public void run() {
                            highsTxt.setText(weatherDetails.get(2));
                        }
                    });
                    lowsTxt.post(new Runnable() {
                        @Override
                        public void run() {
                            lowsTxt.setText(weatherDetails.get(3));
                        }
                    });
                    windTxt.post(new Runnable() {
                        @Override
                        public void run() {
                            windTxt.setText(weatherDetails.get(4));
                        }
                    });
                    lastWUpdateTxt.post(new Runnable() {
                        @Override
                        public void run() {
                            lastWUpdateTxt.setText(weatherDetails.get(5));
                        }
                    });

                    updateWeatherView(View.GONE, View.VISIBLE, View.GONE);
                } else
                    updateWeatherView(View.GONE, View.GONE, View.VISIBLE);
            }
        }).start();
    }

    @SuppressLint("MissingPermission")
    private List<String> weatherReceiver() {
        List<String> weatherDetails = new ArrayList<>();

        //if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Creates an instance of the location service and retrieves the last know location
            //We do this so the system can determine later if there's a location update, and if
            //there is, request a weather update for the new location
            /*final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            assert locationManager != null;*/

            /*class looperThread extends Thread {
                @Override
                public void run() {
                    Looper.prepare();

                    Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, new LocationListener() {
                                    @Override
                                    public void onLocationChanged(Location location) {
                                        locationManager.removeUpdates(this);
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
                                });
                            }
                        }
                    };
                    Looper.loop();
                }
            }*/

            /*locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    locationManager.removeUpdates(this);
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}
                @Override
                public void onProviderEnabled(String provider) {}
                @Override
                public void onProviderDisabled(String provider) {}
            });*/

            /*@SuppressLint("MissingPermission")
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);*/

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000); // milliseconds
        locationRequest.setFastestInterval(3000); // milliseconds
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY); // makes a GPS request using a moderate amount of system resources

        LocationServices.getFusedLocationProviderClient(MainActivity.this).requestLocationUpdates(locationRequest, new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(MainActivity.this).removeLocationUpdates(this);
                if(locationResult != null && locationResult.getLocations().size() > 0) {
                    int latestLocationIndex = locationResult.getLocations().size() - 1;
                    longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                    latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                }
                else {
                    //Puts the result out of range so no location is used later
                    longitude = 1000;
                    latitude = 1000;
                }
            }
        }, Looper.getMainLooper());

        //If result is in range with the max and min longitude and latitude
        if((longitude >= -180 && longitude <= 180) && (latitude >= -90 && latitude <= 90)) {
            try {
                /*assert location != null;
                double longitude = location.getLongitude(), latitude = location.getLatitude();*/

                String result = new HttpRequestTask().execute("https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&units=metric&APPID=" + WEATHER_API).get();
                JSONObject jsonObj = new JSONObject(result);

                //Organises the results into suitable Objects
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject wind = jsonObj.getJSONObject("wind");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);
                long updatedAt = jsonObj.getLong("dt");

                //Add the objects main values into a list of strings to be displayed
                weatherDetails.add(weather.getString("description"));

                //Round numerical values to display a whole number
                weatherDetails.add(Math.round(Double.parseDouble(main.getString("temp"))) + "°C");
                weatherDetails.add(Math.round(Double.parseDouble(main.getString("temp_max"))) + "°C");
                weatherDetails.add(Math.round(Double.parseDouble(main.getString("temp_min"))) + "°C");
                //The metric unit of wind speed is in metres per second, ideally we'd use miles per hour (imperial) but there is no OpenWeatherMap query to allow imperial wind speed and metric temperatures
                // TODO could query imperial units then convert the temperature to celsius? Or allow the user to decide the units?
                if (Integer.parseInt(wind.getString("speed")) > 1)
                    weatherDetails.add(Math.round(Double.parseDouble(wind.getString("speed"))) + " METRE(S)/SECOND WINDS");
                else
                    weatherDetails.add(Math.round(Double.parseDouble(wind.getString("speed"))) + " METRE/SECOND WINDS");
                weatherDetails.add("last updated: " + new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).format(new Date(updatedAt * 1000)));

                weatherDetails.add(weather.getString("id"));

                return weatherDetails;
            } catch (NullPointerException | JSONException e) {
                Log.e("? NullPointerException upon retrieving weather", e.toString());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //Used to hide/show certain views based on the parameters received
    private void updateWeatherView(final int load, final int group, final int error) {
        weatherLoad.post(new Runnable() {
            @Override
            public void run() {
                weatherLoad.setVisibility(load);
            }
        });
        weatherStats.post(new Runnable() {
            @Override
            public void run() {
                weatherStats.setVisibility(group);
            }
        });
        weatherError.post(new Runnable() {
            @Override
            public void run() {
                weatherError.setVisibility(error);
            }
        });
    }

    //Used to handle the response from the CalendarContentResolver
    private void calendarTask() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateCalendarView(View.VISIBLE, View.GONE);
                final String title;
                final List<Event> calendar;
                Context context = calTable.getContext();

                if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED)
                    //Create a CalendarContentResolver to query and return the events of the calendar
                    //CalendarContentResolver resolver = new CalendarContentResolver();
                    calendar = CalendarContentResolver.getCalendar(context);
                else
                    calendar = null;

                //If the calendar permission was granted, then this should continue
                //Else show error message
                if(calendar != null) {
                    //If there are events happening today, add them to the UI table
                    //Else show there are no events today
                    if(!calendar.isEmpty()) {
                        title = getString(R.string.calTitle);

                        calTable.post(new Runnable() {
                            @Override
                            public void run() {
                                //First, clear the table of calendar events in the UI
                                calTable.removeAllViews();

                                for(Event event : calendar)
                                    createCalendarTableRow(event.getTitle(), event.getDuration(), event.getDescription());

                                calTable.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));

                                /* This should be used to fix the layout constraints as for some reason they're removed after using 'view.setLayoutParams'
                                Instead, I've cheated and put the table in its own ConstraintLayout - this isn't the most practical fix
                                ConstraintSet setLayout = new ConstraintSet();
                                setLayout.clone((ConstraintLayout) calTable.getParent());
                                setLayout.connect(findViewById(R.id.calIcon).getId(), ConstraintSet.BOTTOM, calTable.getId(), ConstraintSet.TOP);
                                setLayout.connect(((ConstraintLayout) calTable.getParent()).getId(), ConstraintSet.START, calTable.getId(), ConstraintSet.START);
                                setLayout.applyTo((ConstraintLayout) calTable.getParent());*/
                            }
                        });
                    } else
                        title = getString(R.string.calEmpty);
                }
                else
                    title = getString(R.string.calError);

                calTitle.post(new Runnable() {
                    @Override
                    public void run() {
                        calTitle.setText(title);
                    }
                });

                updateCalendarView(View.GONE, View.VISIBLE);
            }
        }).start();
    }

    private void updateCalendarView(final int load, final int group) {
        calLoad.post(new Runnable() {
            @Override
            public void run() {
                calLoad.setVisibility(load);
            }
        });
        calendarDetails.post(new Runnable() {
            @Override
            public void run() {
                calendarDetails.setVisibility(group);
            }
        });
    }

    private void createCalendarTableRow(String title, String duration, String description) {
        String output;
        Context context = getApplicationContext();
        int rowId = calTable.getChildCount();

        //Creates a new TableRow to be added to the table
        final TableRow eventRow = new TableRow(getApplicationContext());

        CardView card = AppUtilities.createTableCard(context, 16, 8, 8, 8, 8);

        ConstraintLayout constraintLayout = AppUtilities.createConstraintLayout(context, 2000+rowId, 8, 8, 8, 8);

        output = title + " | " + duration;
        TextView eventTxt = AppUtilities.createText(context, 2100+rowId, output, 16, R.color.black, Typeface.BOLD);
        constraintLayout.addView(eventTxt);

        output = description;
        final TextView eventDesc = AppUtilities.createText(context, 2200+rowId, output, 15, R.color.black, Typeface.BOLD);
        eventDesc.setAlpha(0);
        eventDesc.setVisibility(View.GONE);
        constraintLayout.addView(eventDesc);

        final ImageView expand = AppUtilities.createDrawableImage(context, 2300+rowId, R.drawable.expand, 48, 48);
        expand.setColorFilter(ContextCompat.getColor(context, R.color.colorSecondary));
        constraintLayout.addView(expand);

        expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eventDesc.getVisibility() == View.GONE) {
                    expand.animate().rotation(180);
                    eventDesc.setVisibility(View.VISIBLE);
                    eventDesc.animate().alpha(1).setDuration(300).setListener(null);
                }
                else {
                    expand.animate().rotation(0);
                    eventDesc.animate().alpha(0).setDuration(300).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            eventDesc.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        constraintSet.connect(expand.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END, 8);
        constraintSet.connect(expand.getId(), ConstraintSet.TOP, eventTxt.getId(), ConstraintSet.TOP);
        constraintSet.connect(expand.getId(), ConstraintSet.BOTTOM, eventTxt.getId(), ConstraintSet.BOTTOM);

        constraintSet.connect(eventTxt.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START, 8);
        constraintSet.connect(eventTxt.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP);
        constraintSet.connect(eventTxt.getId(), ConstraintSet.END, expand.getId(), ConstraintSet.START, 8);
        constraintSet.setHorizontalBias(eventTxt.getId(), 0f);

        constraintSet.connect(eventDesc.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START, 8);
        constraintSet.connect(eventDesc.getId(), ConstraintSet.TOP, eventTxt.getId(), ConstraintSet.BOTTOM, 8);
        constraintSet.connect(eventDesc.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END, 8);
        constraintSet.setHorizontalBias(eventDesc.getId(), 0f);

        constraintSet.setHorizontalWeight(eventTxt.getId(), 1.0f);
        constraintSet.setHorizontalWeight(eventDesc.getId(), 1.0f);

        constraintSet.applyTo(constraintLayout);

        card.addView(constraintLayout);
        eventRow.addView(card);
        calTable.addView(eventRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }

    private void headlineTask() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateHeadlinesView(View.VISIBLE, View.GONE);
                final ArrayList<Headline> headlines = headlineReceiver();
                String title;

                //If results aren't empty, display them in the headline table
                //Else show headline error
                if(headlines != null && !headlines.isEmpty()) {
                    //TODO you should probably take a look at the bitmaps lecture to make this more memory efficient? I don't imagine the ViewPager will do this for you

                    headlinePager.post(new Runnable() {
                        @Override
                        public void run() {
                            //headlinePager.removeAllViews(); <-- ViewPager2 seems to do this automatically?

                            final HeadlineAdapter adapter = new HeadlineAdapter(headlinePager.getContext(), headlines);
                            headlinePager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
                            headlinePager.setAdapter(adapter);
                            headlinePager.setOffscreenPageLimit(3);

                            headlinePager.setPageTransformer(new ViewPager2.PageTransformer() {
                                @Override
                                public void transformPage(@NonNull View page, float position) {
                                    float offset = position * - 30;
                                    if (position < -1) {
                                        page.setTranslationX(-offset);
                                    } else if (position <= 1) {
                                        float scaleFactor = Math.max(0.7f, 1 - Math.abs(position));
                                        page.setTranslationX(offset);
                                        page.setScaleY(scaleFactor);
                                        page.setAlpha(scaleFactor);
                                    } else {
                                        page.setAlpha(0);
                                        page.setTranslationX(offset);
                                    }

                                    // NO ZOOM TRANSITION
                                    /*if (headlinePager.getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL)
                                        if (ViewCompat.getLayoutDirection(headlinePager) == ViewCompat.LAYOUT_DIRECTION_RTL)
                                            page.setTranslationX(-offset);
                                        else
                                            page.setTranslationX(offset);
                                    else
                                        page.setTranslationY(offset);*/

                                }
                            });
                            headlineIndicator.post(new Runnable() {
                                @Override
                                public void run() {
                                    headlineIndicator.setViewPager(headlinePager);
                                    adapter.registerAdapterDataObserver(headlineIndicator.getAdapterDataObserver());
                                }
                            });
                        }
                    });

                    title = getString(R.string.newsTitle);
                } else
                    title = getString(R.string.newsError);

                final String finalTitle = title;
                newsTitle.post(new Runnable() {
                    @Override
                    public void run() {
                        newsTitle.setText(finalTitle);
                    }
                });

                updateHeadlinesView(View.GONE, View.VISIBLE);
            }
        }).start();
    }

    private ArrayList<Headline> headlineReceiver() {
        ArrayList<Headline> headlines = new ArrayList<>();

        //Try to get the news headlines from a requested .json file
        //Else return null so the UI thread knows there was an error
        try {
            String result = new HttpRequestTask().execute("https://content.guardianapis.com/search?section=world&show-fields=thumbnail&api-key=" + NEWS_API).get();

            JSONObject results = new JSONObject(result);
            JSONArray resultsArray = results.getJSONObject("response").getJSONArray("results");

            //Store the results into a list
            for(int i = 0; i < resultsArray.length(); i++) {
                JSONObject jsonObj = resultsArray.getJSONObject(i);
                Log.i("? results obj " + i, String.valueOf(jsonObj));

                //Used to remove system char indicators from the URL string
                StringBuilder thumbnail = new StringBuilder();
                for(char c : jsonObj.getJSONObject("fields").getString("thumbnail").toCharArray())
                    if(c != '\\')
                        thumbnail.append(c);

                headlines.add(new Headline(jsonObj.getString("webTitle"), jsonObj.getString("webUrl"), thumbnail.toString()));
            }

            return headlines;
        } catch (JSONException e) {
            Log.e("? JSONException", "failed to parse request/result", e);
        } catch (NullPointerException e) {
            Log.e("? NullPointerException", ".json result was not populated", e);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Used to hide/show certain views based on the parameters
    private void updateHeadlinesView(final int load, final int headline) {
        newsLoad.post(new Runnable() {
            @Override
            public void run() {
                newsLoad.setVisibility(load);
            }
        });
        newsHeadingGroup.post(new Runnable() {
            @Override
            public void run() {
                newsHeadingGroup.setVisibility(headline);
            }
        });
        headlinePager.post(new Runnable() {
            @Override
            public void run() {
                headlinePager.setVisibility(headline);
            }
        });
    }
}