package com.delsquared.lightningdots.utilities;

import android.app.Activity;
import android.app.Application;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.delsquared.lightningdots.BuildConfig;
import com.delsquared.lightningdots.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

public class LightningDotsApplication extends Application {

    public static Object lockSharedPreferences = new Object();

    public static boolean hasPurchasedNoAds = false;

    // Variables for settings
    public static boolean settingShowInstructions = true;

    public static int numberOfGameTransitions = 0;

    public static boolean hasDisplayedUnableToSetUpBillingAlert = false;

    public static final String[] base64EncodedPublicKey = {
            "Z2Q2CJOB733BfUyLf0S9ZhzJ5TU5"
            , "AQ8AMIIBCgKCAQEAhdHu+MsgOKKV"
            , "giUawlAtbIwkNICKo/KwriBzZ+Zb"
            , "MIIBIjANBgkqhkiG9w0BAQEFAAOC"
            , "h0gRdlv8zcqjuD1I3j+AUhmm1mFU"
            , "215HgjLs9eLUTN4l7BFU9I6lrFsV"
            , "q78XLVNXTVjf4pO2LBgHHrcAVGFq"
            , "lEUNVB3gGdOWANARWD58qLOMKRDT"
            , "N/8QWq6exwbvmQ02xTz7KT5tNSiY"
            , "7ZkCFyd1gD6SlXO5XnH2CniB/oWd"
            , "Dm/ashzxpphzzAwqzTE4ydSpWcXF"
            , "JTMQVg4dQzKMblpwnAMBSqzqZj3v"
            , "txZZKwi04f8SJ5EzWggPowIDAQAB"
            , "k5588KBGtmNxcR/5weRuwtKJifi7"
    };
    public static final int[] keySequence = {
            3, 1, 2, 13, 7, 0, 10, 5, 6, 8, 4, 9, 11, 12
    };

    public static final String logTag = "LightningDots";

    public LightningDotsApplication() {

        super();

    }

    /**
     * Enum used to identify the tracker that needs to be used for tracking.
     *
     * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
     * storing them all in Application object helps ensure that they are created only once per
     * application instance.
     */

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    synchronized Tracker getTracker(TrackerName trackerId) {

        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(R.xml.app_tracker)
                    : analytics.newTracker(R.xml.global_tracker);

            mTrackers.put(trackerId, t);

        }

        return mTrackers.get(trackerId);

    }

    public static String constructBase64EncodedPublicKey() {
        String returnString = "";
        for (int i = 0; i < keySequence.length; i++) {
            returnString += base64EncodedPublicKey[keySequence[i]];
        }
        return returnString;
    }

    public static void setHasPurchasedNoAds(Context context, boolean hasPurchasedNoAds) {

        LightningDotsApplication.hasPurchasedNoAds = hasPurchasedNoAds;

        // Get the shared preferences reference
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preferences_file_name), Activity.MODE_PRIVATE);

        synchronized (lockSharedPreferences) {

            // Update the shared preferences file
            sharedPref.edit()
                    .putBoolean(
                            context.getString(R.string.pref_product_remove_ads)
                            , hasPurchasedNoAds)
                    .commit();

        }

        logDebugMessage("Calling data changed in setHasPurchasedNoAds()...");
        dataChanged(context);
    }

    public static void setShowInstructions(Context context, boolean showInstructions) {

        LightningDotsApplication.settingShowInstructions = showInstructions;

        // Get the shared preferences reference
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preferences_file_name), Activity.MODE_PRIVATE);

        synchronized (lockSharedPreferences) {

            // Update the shared preferences file
            sharedPref.edit()
                    .putBoolean(
                            context.getString(R.string.pref_setting_show_instructions)
                            , showInstructions)
                    .commit();

        }

        logDebugMessage("Calling data changed in setShowInstructions()...");
        dataChanged(context);

    }

    public static void dataChanged(Context context) {
        try {

            logDebugMessage("LightningDotsApplication.dataChanged()...");

            // Call the backup manager data changed
            BackupManager backupManager = new BackupManager(context);
            backupManager.dataChanged();

            logDebugMessage("dataChanged() call complete.");
        } catch (Exception e) {
            logDebugErrorMessage("Call to dataChanged() failed...");
            logDebugErrorMessage(e.getMessage());
        }
    }

    public static void logDebugMessage(String message) {
        if (BuildConfig.DEBUG) {
            Log.d(logTag, message);
        }
    }

    public static void logDebugErrorMessage(String message) {
        if (BuildConfig.DEBUG) {
            Log.e(logTag, message);
        }
    }

}
