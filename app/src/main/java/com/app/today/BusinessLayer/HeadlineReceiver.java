// Google News API - 3906607150f944d0a94a74da4c17f51b << results appear to require a Google library to parse the .json response

package com.app.today.BusinessLayer;

import android.util.Log;
import com.androdocs.httprequest.HttpRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class HeadlineReceiver {
    //The Guardian API key, used to get news headlines
    private static final String NEWS_API = "07f8c2ea-493e-4429-ae47-74ade74d113c";

    //Used to return a list of the headlines retrieved
    public static List<Headline> getHeadlines() {
        List<Headline> headlines = new ArrayList<>();

        //Try to get the news headlines from a requested .json file
        //Else return null so the UI thread knows there was an error
        try {
            JSONObject results = new JSONObject(makeRequest());//.getJSONObject("response").getJSONArray("results");
            JSONArray resultsArray = results.getJSONObject("response").getJSONArray("results");

            //Store the results into a list
            for(int i = 0; i < resultsArray.length(); i++) {
                JSONObject jsonObj = resultsArray.getJSONObject(i);
                Log.i("? results obj " + i, String.valueOf(jsonObj));

                //Used to remove system char indicators from the URL string
                StringBuilder thumbnail = new StringBuilder("");
                for(char c : jsonObj.getJSONObject("fields").getString("thumbnail").toCharArray())
                    if(c != '\\')
                        thumbnail.append(c);

                headlines.add(new Headline(jsonObj.getString("webTitle"), jsonObj.getString("webUrl"), thumbnail.toString()));
            }
        } catch (JSONException e) {
            Log.e("? JSONException", "failed to parse request/result", e);
            return null;
        } catch (NullPointerException e) {
            Log.e("? NullPointerException", ".json result was not populated", e);
            return null;
        }
        //Return the headlines to be displayed if successful
        return headlines;
    }

    //Used to make and return the HttpRequest for the headlines
    private static String makeRequest() {
        return HttpRequest.excuteGet("https://content.guardianapis.com/search?section=world&show-fields=thumbnail&api-key=" + NEWS_API);
    }
}