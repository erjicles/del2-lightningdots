package com.delsquared.lightningdots.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.utilities.EEAConsentManager;
import com.delsquared.lightningdots.utilities.IEEAConsentListener;
import com.delsquared.lightningdots.utilities.LightningDotsApplication;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

public class ActivitySplash extends Activity implements IEEAConsentListener {

    // Splash screen timer
    private static final int SPLASH_TIME_OUT = 3000;
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

        // Get and execute the thread that gets the current terms of service version
        startGetVersions();


        // Initiate the splash wait
        new Handler().postDelayed(() -> {

            splashTimeoutFinished = true;
            finishSplash();

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

            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(getContext());

            // Make the request
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    versionsWebServiceUrl,
                    null,
                    response -> finishGetVersions(response.toString()),
                    error -> {
                        LightningDotsApplication.logDebugErrorMessage(error.getMessage());
                        finishGetVersions("");
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

        if (versionsLoadComplete && splashTimeoutFinished) {

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
        } else {

            // Launch the main activity
            Intent mainIntent = new Intent(ActivitySplash.this, ActivityMain.class);
            mainIntent.putExtra(JSON_VERSIONS_STRING_KEY, jsonVersionsString);
            mainIntent.putExtra(VERSIONS_LOAD_SUCCESSFUL_KEY, versionsLoadSuccessful);
            startActivity(mainIntent);
        }
        // Close this activity
        finish();

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
