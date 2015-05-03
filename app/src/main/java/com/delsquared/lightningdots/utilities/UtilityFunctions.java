package com.delsquared.lightningdots.utilities;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.TypedValue;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class UtilityFunctions {

    public static boolean getHasInternetConnection(Context context) {

        // Initialize the result
        boolean isConnected = false;

        try {

            ConnectivityManager cm =
                    (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

        } catch (Exception e) {

            // Reset the connected flag
            isConnected = false;

        }

        return isConnected;

    }

    public static void sendEventTracker(
            Activity activity,
            String category
            , String action
            , String label
            , long value) {

        try {

            // Get tracker.
            Tracker t = ((LightningDotsApplication) activity.getApplication()).getTracker(
                    LightningDotsApplication.TrackerName.APP_TRACKER);

            // Build the event
            HitBuilders.EventBuilder theEvent = new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action)
                    .setLabel(label)
                    .setValue(value);

            // Build and send an Event.
            t.send(theEvent.build());

        } catch (Exception e) {

        }

    }

    public static void registerScreenView(Activity activity, String screenName) {

        try {

            // Get the app tracker.
            Tracker appTracker = ((LightningDotsApplication) activity.getApplication()).getTracker(
                    LightningDotsApplication.TrackerName.APP_TRACKER);

            // Get the global tracker
            Tracker globalTracker = ((LightningDotsApplication) activity.getApplication()).getTracker(
                    LightningDotsApplication.TrackerName.GLOBAL_TRACKER);

            // Set screen name.
            // Where path is a String representing the screen name.
            appTracker.setScreenName(screenName);
            globalTracker.setScreenName(screenName);

            // Send a screen view.
            appTracker.send(new HitBuilders.AppViewBuilder().build());
            globalTracker.send(new HitBuilders.AppViewBuilder().build());

        } catch (Exception e) {

        }

    }

    public static float getResourceFloatValue(Context context, int resourceID) {
        float resultValue = 0.0f;

        // Get the value
        try {
            TypedValue typedValueResourceHelper = new TypedValue();
            context.getResources().getValue(resourceID, typedValueResourceHelper, true);
            resultValue = typedValueResourceHelper.getFloat();
        } catch (Exception e) {
            resultValue = 0.0f;
        }

        return resultValue;
    }

    public static double generateRandomValue(double minimumValue, double maximumValue, boolean mirrorAbsoluteValue) {

        // Initialize the result
        double resultValue = 0.0;

        resultValue = minimumValue + (Math.random() * (maximumValue - minimumValue));

        if (mirrorAbsoluteValue) {
            resultValue *= getRandomSign();
        }

        return resultValue;

    }

    public static double getRandomSign() {
        return (Math.random() <= 0.5) ? -1.0 : 1.0;
    }

}
