// https://content.guardianapis.com/search?api-key=07f8c2ea-493e-4429-ae47-74ade74d113c

package com.app.today;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.androdocs.httprequest.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class HeadlineReceiver {
    private static final String API = "07f8c2ea-493e-4429-ae47-74ade74d113c";
    static class headlineReciever extends AsyncTask<String, Void, String> {
        private List<String> headlines = new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //updateWeather(View.VISIBLE, View.GONE, View.GONE, View.GONE);
        }
        @Override
        public String doInBackground(String... args) {
            return HttpRequest.excuteGet("https://content.guardianapis.com/search?api-key=" + API);
        }
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject results = new JSONObject(result);

                JSONArray resultsArray = results.getJSONObject("response").getJSONArray("results");

                //Log.i("??? ", String.valueOf(jsonObj.getJSONObject("response").getJSONArray("results")));
                for(int i = 0; i < resultsArray.length(); i++) {
                    JSONObject jsonObj = resultsArray.getJSONObject(i);
                    Log.i("???", jsonObj.getString("id"));

                    //Headline headline = new Headline()
                    //headlines.add()
                }
                /*JSONObject main = jsonObj.getJSONObject("");
                JSONObject wind = jsonObj.getJSONObject("wind");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);
                long updatedAt = jsonObj.getLong("dt");*/
            } catch (JSONException e) {
                //updateWeather(View.GONE, View.VISIBLE, View.GONE, View.VISIBLE);
            }
        }
    }
}