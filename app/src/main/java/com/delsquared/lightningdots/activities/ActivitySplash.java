package com.delsquared.lightningdots.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.utilities.EEAConsentManager;
import com.delsquared.lightningdots.utilities.IEEAConsentListener;
import com.delsquared.lightningdots.utilities.LightningDotsApplication;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

import org.json.JSONObject;

public class ActivitySplash extends Activity implements IEEAConsentListener {

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


        // Get and execute the thread that gets the current terms of service version
        startGetVersions();


        // Initiate the splash wait
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                splashTimeoutFinished = true;
                finishSplash();

            }

        }, SPLASH_TIME_OUT);
    }

    private void startGetVersions() {

        Context context = getContext();

        // If there's no internet connection, use default version
        if (!UtilityFunctions.getHasInternetConnection(context)) {
            finishGetVersions("");
        }

        try {

            // Get the versions web service url
            String versionsWebServiceUrl = context.getString(R.string.versions_url);

            // Get the request timeouts
            int requestTimeout = Integer.parseInt(context.getString(R.string.request_timeout));
            int requestSocketTimeout = Integer.parseInt(context.getString(R.string.request_socket_timeout));

            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(getContext());

            // Make the request
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    versionsWebServiceUrl,
                    null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            finishGetVersions(response.toString());
                        }

                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            LightningDotsApplication.logDebugErrorMessage(error.getMessage());
                            finishGetVersions("");
                        }

                    }
            );

            // Add the request to the RequestQueue
            queue.add(jsonObjectRequest);

        } catch (Exception e) {

            // Reset the result string
            LightningDotsApplication.logDebugErrorMessage(e.getMessage());
            finishGetVersions("");

        }

    }

    private void finishGetVersions(String result) {
        jsonVersionsString = result;
        versionsLoadComplete = true;
        versionsLoadSuccessful = (result.length() > 0);
        finishSplash();
    }

    private void finishSplash() {

        if (versionsLoadComplete == true && splashTimeoutFinished == true) {

            // Handle checking for consent to show personalized ads
            EEAConsentManager consentManager = new EEAConsentManager(this);
            consentManager.handleAdConsent();

        }

    }

    private void continueToApp() {

        // Check if the user is an EEA user who wants no ads
        if (LightningDotsApplication.userPrefersNoAds) {
            // Launch the store activity
            Intent storeIntent = new Intent(ActivitySplash.this, ActivityStore.class);
            startActivity(storeIntent);
            finish();
        } else {

            // Launch the main activity
            Intent mainIntent = new Intent(ActivitySplash.this, ActivityMain.class);
            mainIntent.putExtra(JSON_VERSIONS_STRING_KEY, jsonVersionsString);
            mainIntent.putExtra(VERSIONS_LOAD_SUCCESSFUL_KEY, versionsLoadSuccessful);
            startActivity(mainIntent);

            // Close this activity
            finish();

        }

    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onHandleConsentFinished() {
        continueToApp();
    }

}
