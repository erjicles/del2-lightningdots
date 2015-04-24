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

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

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

                try {

                    // Get the versions web service url
                    String versionsWebServiceUrl = mContext.getString(R.string.versions_url);

                    // Get the request timeouts
                    int requestTimeout = Integer.parseInt(mContext.getString(R.string.request_timeout));
                    int requestSocketTimeout = Integer.parseInt(mContext.getString(R.string.request_socket_timeout));

                    // Create the http request parameters
                    HttpParams httpParameters = new BasicHttpParams();

                    // Set the timeout for waiting for a connection to be established
                    HttpConnectionParams.setConnectionTimeout(httpParameters, requestTimeout);

                    // Set the timeout for waiting for a response
                    HttpConnectionParams.setSoTimeout(httpParameters, requestSocketTimeout);

                    // Create the http client
                    DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);

                    // Create the http post
                    HttpPost httppost = new HttpPost(versionsWebServiceUrl);

                    // Set the post header
                    httppost.setHeader("Content-type", "application/json");

                    // Execute the request
                    HttpResponse response = httpclient.execute(httppost);
                    jsonResultString = EntityUtils.toString(response.getEntity());

                } catch (Exception e) {

                    // Reset the result string
                    jsonResultString = "";

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
