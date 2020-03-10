package com.app.today;

import android.util.Log;
import com.androdocs.httprequest.HttpRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

class HeadlineReceiver {
    //The Guardian API key, used to get news headlines
    private static final String API = "07f8c2ea-493e-4429-ae47-74ade74d113c";

    //Used to return a list of the headlines retrieved
    static List<String> getHeadlines() {
        List<String> headlines = new ArrayList<>();
        //Try to get the news headlines from a requested .json file
        //Else return null so the UI thread knows there was an error
        try {
            JSONObject results = new JSONObject(makeRequest());
            JSONArray resultsArray = results.getJSONObject("response").getJSONArray("results");

            //Store the results into a list
            for(int i = 0; i < resultsArray.length(); i++) {
                JSONObject jsonObj = resultsArray.getJSONObject(i);
                Log.i("? results obj " + i, String.valueOf(jsonObj));

                headlines.add(jsonObj.getString("webTitle"));
            }
        } catch (JSONException e) {
            Log.e("JSONException", "failed to parse request/result", e);
            return null;
        }
        //Return the headlines to be displayed if successful
        return headlines;
    }

    //Used to make and return the HttpRequest for the headlines
    private static String makeRequest() {
        return HttpRequest.excuteGet("https://content.guardianapis.com/search?section=world&api-key=" + API);
    }
}