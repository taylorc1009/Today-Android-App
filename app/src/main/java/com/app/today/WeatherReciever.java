// OpenWeatherMap API key - 772b4855c9a17c457e882407a698bcf0 (might change after a certain duration)

package com.app.today;

import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;
import android.location.LocationManager;
import android.location.Location;
import android.location.LocationListener;
import com.androdocs.httprequest.HttpRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WeatherReciever {
    String API = "772b4855c9a17c457e882407a698bcf0";

    TextView addressTxt, updated_atTxt, statusTxt, tempTxt, temp_minTxt, temp_maxTxt, sunriseTxt, sunsetTxt, windTxt, pressureTxt, humidityTxt;

    addressTxt = findViewById(R.id.address);
    updated_atTxt = findViewById(R.id.updated_at);
    statusTxt = findViewById(R.id.status);
    tempTxt = findViewById(R.id.temp);
    temp_minTxt = findViewById(R.id.temp_min);
    temp_maxTxt = findViewById(R.id.temp_max);
    sunriseTxt = findViewById(R.id.sunrise);
    sunsetTxt = findViewById(R.id.sunset);
    windTxt = findViewById(R.id.wind);
    pressureTxt = findViewById(R.id.pressure);
    humidityTxt = findViewById(R.id.humidity);

    class weatherTask extends AsyncTask<String, Void, String> {
        findViewById(R.id.loader).setVisibility(View.VISIBLE);
        findViewById(R.id.mainContainer).setVisibility(View.GONE);
        findViewById(R.id.errorText).setVisibility(View.GONE);
    }

    protected String doInBackground() {
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        private final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
            }
        };
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?lat=" + LAT + "&lon=" + LON + "&units=metric&appid=" + API);
        return response;
    }

    protected void getWeather(String result) {
        try {
            JSONObject jsonObj = new JSONObject(result);
            JSONObject main = jsonObj.getJSONObject("main");
            JSONObject sys = jsonObj.getJSONObject("sys");
            JSONObject wind = jsonObj.getJSONObject("wind");
            JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

            Long updatedAt = jsonObj.getLong("dt");
            String updatedAtText = "Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000));
            String temp = main.getString("temp") + "°C";
            String tempMin = "Min Temp: " + main.getString("temp_min") + "°C";
            String tempMax = "Max Temp: " + main.getString("temp_max") + "°C";
            String pressure = main.getString("pressure");
            String humidity = main.getString("humidity");

            Long sunrise = sys.getLong("sunrise");
            Long sunset = sys.getLong("sunset");
            String windSpeed = wind.getString("speed");
            String weatherDescription = weather.getString("description");

            String address = jsonObj.getString("name") + ", " + sys.getString("country");


            /* Populating extracted data into our views */
            addressTxt.setText(address);
            updated_atTxt.setText(updatedAtText);
            statusTxt.setText(weatherDescription.toUpperCase());
            tempTxt.setText(temp);
            temp_minTxt.setText(tempMin);
            temp_maxTxt.setText(tempMax);
            sunriseTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunrise * 1000)));
            sunsetTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunset * 1000)));
            windTxt.setText(windSpeed);
            pressureTxt.setText(pressure);
            humidityTxt.setText(humidity);

            /* Views populated, Hiding the loader, Showing the main design */
            findViewById(R.id.loader).setVisibility(View.GONE);
            findViewById(R.id.mainContainer).setVisibility(View.VISIBLE);


        } catch (JSONException e) {
            findViewById(R.id.loader).setVisibility(View.GONE);
            findViewById(R.id.errorText).setVisibility(View.VISIBLE);
        }

    }
}
