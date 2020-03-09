package com.app.today;

import android.util.Log;
import com.androdocs.httprequest.HttpRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

class HeadlineReceiver {
    private static final String API = "07f8c2ea-493e-4429-ae47-74ade74d113c";
    List<String> getHeadlines() {
        List<String> headlines = new ArrayList<>();
        try {
            JSONObject results = new JSONObject(makeRequest());
            JSONArray resultsArray = results.getJSONObject("response").getJSONArray("results");
            for(int i = 0; i < resultsArray.length(); i++) {
                JSONObject jsonObj = resultsArray.getJSONObject(i);
                Log.i("? results obj " + i, String.valueOf(jsonObj));

                //Headline headline = new Headline(jsonObj.getString("webTitle"), jsonObj.getString("webUrl"));//, jsonObj.getString("sectionName")
                headlines.add(jsonObj.getString("webTitle"));
            }
        } catch (JSONException e) {
            Log.e("JSONException", "failed to parse request/result", e);
            return null;
        }
        return headlines;
    }
    private String makeRequest() {
        return HttpRequest.excuteGet("https://content.guardianapis.com/search?section=news&api-key=" + API);
    }
}