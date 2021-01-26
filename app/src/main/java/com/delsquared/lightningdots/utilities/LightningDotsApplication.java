package com.delsquared.lightningdots.utilities;

import android.app.Activity;
import android.app.Application;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.delsquared.lightningdots.BuildConfig;
import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.billing_utilities.BillingHelper;
import com.google.ads.consent.ConsentStatus;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

public class LightningDotsApplication extends Application {

    public static final Object lockSharedPreferences = new Object();

    public static final MutableLiveData<Object> adStatusObservable = new MutableLiveData<>();

    private static boolean hasPurchasedNoAds = false;
    private static ConsentStatus consentStatus = ConsentStatus.UNKNOWN;
    private static boolean userPrefersNoAds = false;
    private static boolean userIsFromEEA = false;

    // Variables for settings
    public static boolean settingShowInstructions = true;

    public static int numberOfGameTransitions = 0;

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
        @SuppressWarnings({"SpellCheckingInspection", "unused"})
        ECOMMERCE_TRACKER, // Tracker used by all e-commerce transactions from a company.
    }

    final HashMap<TrackerName, Tracker> mTrackers = new HashMap<>();

    synchronized Tracker getTracker(TrackerName trackerId) {

        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(R.xml.app_tracker)
                    : analytics.newTracker(R.xml.global_tracker);

            mTrackers.put(trackerId, t);

        }

        return mTrackers.get(trackerId);

    }

    public BillingHelper getBillingHelperInstanceAndStartConnection() {
        BillingHelper billingHelper = BillingHelper.getInstance(this);
        billingHelper.startConnection();
        return billingHelper;
    }

    public static void setHasPurchasedNoAds(boolean hasPurchasedNoAds) {
        logDebugMessage("Setting hasPurchasedNoAds: " + hasPurchasedNoAds);
        LightningDotsApplication.hasPurchasedNoAds = hasPurchasedNoAds;
        adStatusObservable.postValue(null);
    }

    public static ConsentStatus getConsentStatus() {
        logDebugMessage("Getting consentStatus: " + consentStatus);
        return LightningDotsApplication.consentStatus;
    }
    public static void setConsentStatus(ConsentStatus consentStatus) {
        logDebugMessage("Setting consentStatus: " + consentStatus);
        LightningDotsApplication.consentStatus = consentStatus;
        adStatusObservable.postValue(null);
    }

    public static boolean getUserIsFromEEA() {
        logDebugMessage("Getting userIsFromEEA: " + userIsFromEEA);
        return LightningDotsApplication.userIsFromEEA;
    }
    public static void setUserIsFromEEA(boolean userIsFromEEA) {
        logDebugMessage("Setting userIsFromEEA: " + userIsFromEEA);
        LightningDotsApplication.userIsFromEEA = userIsFromEEA;
    }

    public static boolean getUserPrefersNoAds() {
        logDebugMessage("Getting userPrefersNoAds: " + userPrefersNoAds);
        return LightningDotsApplication.userPrefersNoAds;
    }
    public static void setUserPrefersNoAds(boolean userPrefersNoAds) {
        logDebugMessage("Setting userPrefersNoAds: " + userPrefersNoAds);
        LightningDotsApplication.userPrefersNoAds = userPrefersNoAds;
        adStatusObservable.postValue(null);
    }

    public static boolean getAreAdsEnabled() {
        logDebugMessage("getAreAdsEnabled(): hasPurchasedNoAds: " + hasPurchasedNoAds
                + "; userIsFromEEA: " + userIsFromEEA
                + "; userPrefersNoAds: " + userPrefersNoAds);
        boolean areAdsEnabled = !hasPurchasedNoAds
                && !(userIsFromEEA && userPrefersNoAds);
        logDebugMessage("...areAdsEnabled: " + areAdsEnabled);
        return areAdsEnabled;
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
                    .apply();

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
