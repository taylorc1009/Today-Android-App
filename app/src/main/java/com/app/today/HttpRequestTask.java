// source - https://medium.com/@JasonCromer/android-asynctask-http-request-tutorial-6b429d833e28

package com.app.today;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class HttpRequestTask extends AsyncTask<String, Void, String> {
    private static final String REQUEST_METHOD = "GET";
    private static final int READ_TIMEOUT = 15000;
    private static final int CONNECTION_TIMEOUT = 15000;

    @Override
    protected String doInBackground(String... strings) {
        String stringUrl = strings[0];
        String result = null;
        String inputLine;

        try {
            //Create a URL object holding our url
            URL myUrl = new URL(stringUrl);

            //Create a connection
            HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();

            //Set methods and timeouts
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            //Connect to the url
            connection.connect();

            //Create a new InputStreamReader
            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());

            //Create a new buffered reader and String Builder
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();

            //Check if the line we are reading is not null
            while((inputLine = reader.readLine()) != null)
                stringBuilder.append(inputLine);

            //Close our InputStream and Buffered reader
            reader.close();
            streamReader.close();

            //Set our result equal to our stringBuilder
            result = stringBuilder.toString();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}