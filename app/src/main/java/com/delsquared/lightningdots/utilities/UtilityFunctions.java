package com.delsquared.lightningdots.utilities;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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


    }

}
