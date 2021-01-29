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
import com.delsquared.lightningdots.globals.GlobalSettings;
import com.delsquared.lightningdots.utilities.EEAConsentManager;
import com.delsquared.lightningdots.utilities.IEEAConsentListener;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

public class ActivitySplash extends Activity implements IEEAConsentListener {
    private static final String CLASS_NAME = ActivitySplash.class.getSimpleName();

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
        String methodName = CLASS_NAME + ".onCreate";
        UtilityFunctions.logDebug(methodName, "Entered");
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
        String methodName = CLASS_NAME + ".startGetVersions";
        UtilityFunctions.logDebug(methodName, "Entered");

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
                        UtilityFunctions.logError(methodName, "Exception retrieving versions", error);
                        finishGetVersions("");
                    }
            );

            // Add the request to the RequestQueue
            queue.add(jsonObjectRequest);

        } catch (Exception e) {

            // Reset the result string
            UtilityFunctions.logError(methodName, "Exception retrieving versions", e);
            finishGetVersions("");

        }

    }

    private void finishGetVersions(String result) {
        String methodName = CLASS_NAME + ".finishGetVersions";
        UtilityFunctions.logDebug(methodName, "Entered");

        jsonVersionsString = result;
        versionsLoadComplete = true;
        versionsLoadSuccessful = (result.length() > 0);
        UtilityFunctions.logDebug(methodName, "Result: " + result);
        finishSplash();
    }

    private void finishSplash() {
        String methodName = CLASS_NAME + ".finishSplash";
        UtilityFunctions.logDebug(methodName, "Entered");

        if (versionsLoadComplete && splashTimeoutFinished) {

            // Handle checking for consent to show personalized ads
            EEAConsentManager consentManager = new EEAConsentManager(this);
            consentManager.handleAdConsent();

        }

    }

    private void continueToApp() {
        String methodName = CLASS_NAME + ".continueToApp";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Check if the user is an EEA user who wants no ads
        if (GlobalSettings.getInstance().getUserPrefersNoAds()) {
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
        String methodName = CLASS_NAME + ".getContext";
        UtilityFunctions.logDebug(methodName, "Entered");
        return this;
    }

    @Override
    public void onHandleConsentFinished() {
        String methodName = CLASS_NAME + ".onHandleConsentFinished";
        UtilityFunctions.logDebug(methodName, "Entered");
        continueToApp();
    }

}
