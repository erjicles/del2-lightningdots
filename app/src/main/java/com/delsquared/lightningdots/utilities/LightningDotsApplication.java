package com.delsquared.lightningdots.utilities;

import android.app.Application;

import com.delsquared.lightningdots.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

public class LightningDotsApplication extends Application {

    public static boolean hasPurchasedNoAds = false;

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

}
