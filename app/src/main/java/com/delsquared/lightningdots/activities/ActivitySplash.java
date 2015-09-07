package com.delsquared.lightningdots.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ActivitySplash extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    public final static String JSON_VERSIONS_STRING_KEY = "json_versions_string";
    public final static String VERSIONS_LOAD_SUCCESSFUL_KEY = "versions_load_successful";

    protected boolean splashTimeoutFinished = false;
    protected boolean versionsLoadComplete = false;
    protected boolean versionsLoadSuccessful = false;
    protected String jsonVersionsString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Get the shared preferences
        SharedPreferences userPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Get the setting on whether we should opt out of google analytics
        //boolean googleAnalyticsOptOut =
        //        userPrefs.getBoolean(getString(R.string.pref_general_googleanalytics_key), false);

        // Toggle the google analytics opt out app setting
        //GoogleAnalytics.getInstance(this).setAppOptOut(googleAnalyticsOptOut);



        // Get and execute the thread that gets the current terms of service version
        VersionsAsyncTask versionsTask = new VersionsAsyncTask(getApplicationContext());
        versionsTask.execute();


        // Initiate the splash wait
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                splashTimeoutFinished = true;
                finishSplash();

            }

        }, SPLASH_TIME_OUT);
    }

    private void finishSplash() {

        if (versionsLoadComplete == true && splashTimeoutFinished == true) {

            // Launch the main activity
            Intent mainIntent = new Intent(ActivitySplash.this, ActivityMain.class);
            mainIntent.putExtra(JSON_VERSIONS_STRING_KEY, jsonVersionsString);
            mainIntent.putExtra(VERSIONS_LOAD_SUCCESSFUL_KEY, versionsLoadSuccessful);
            startActivity(mainIntent);

            // Close this activity
            finish();

        }

    }

    private class VersionsAsyncTask extends AsyncTask<Void, Void, String> {

        private Context mContext;

        public VersionsAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected String doInBackground(Void... params) {

            // Initialize the result string
            String jsonResultString = "";

            // Initialize if we are connected
            boolean isConnected = UtilityFunctions.getHasInternetConnection(mContext);

            // Check if we have an internet connection
            if (isConnected) {

                URL versionsWebServiceUrl = null;
                HttpURLConnection versionsUrlConnection = null;

                try {

                    // Get the request timeouts
                    int requestTimeout = Integer.parseInt(mContext.getString(R.string.request_timeout));
                    int requestSocketTimeout = Integer.parseInt(mContext.getString(R.string.request_socket_timeout));

                    versionsWebServiceUrl = new URL(mContext.getString(R.string.versions_url));

                    versionsUrlConnection =
                            (HttpURLConnection) versionsWebServiceUrl.openConnection();
                    versionsUrlConnection.setReadTimeout(requestTimeout);
                    versionsUrlConnection.setConnectTimeout(requestSocketTimeout);
                    versionsUrlConnection.setRequestMethod("POST");
                    versionsUrlConnection.setRequestProperty("Content-type", "application/json");

                    InputStream versionsInputStream = new BufferedInputStream(versionsUrlConnection.getInputStream());

                    try {

                        BufferedReader versionsReader = new BufferedReader(new InputStreamReader(versionsInputStream));

                        StringBuilder versionsStringBuilder = new StringBuilder();

                        String currentLine;
                        while ((currentLine = versionsReader.readLine()) != null) {
                            versionsStringBuilder.append(currentLine);
                        }

                        jsonResultString = versionsStringBuilder.toString();

                    } catch (Exception e) {

                        throw e;

                    } finally {
                        if (versionsInputStream != null) {
                            versionsInputStream.close();
                        }
                    }

                } catch (Exception e) {

                    // Reset the result string
                    jsonResultString = "";

                } finally {
                    if (versionsUrlConnection != null) {
                        versionsUrlConnection.disconnect();
                    }
                }

            }

            return jsonResultString;
        }

        @Override
        protected void onPostExecute(String result) {

            jsonVersionsString = result;
            versionsLoadComplete = true;
            versionsLoadSuccessful = (result.length() > 0);
            finishSplash();

        }

    }
}
