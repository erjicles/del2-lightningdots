package com.delsquared.lightningdots.utilities;

import android.app.Activity;
import android.app.Application;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.billing_utilities.BillingHelper;
import com.google.ads.consent.ConsentStatus;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

public class LightningDotsApplication extends Application {
    private static final String CLASS_NAME = LightningDotsApplication.class.getSimpleName();

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
        String methodName = CLASS_NAME + ".constructor";
        UtilityFunctions.logDebug(methodName, "Entered");
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
        String methodName = CLASS_NAME + ".getTracker";
        UtilityFunctions.logDebug(methodName, "Entered");

        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(R.xml.app_tracker)
                    : analytics.newTracker(R.xml.global_tracker);

            mTrackers.put(trackerId, t);

        }

        return mTrackers.get(trackerId);

    }

    public BillingHelper getBillingHelperInstanceAndStartConnection() {
        String methodName = CLASS_NAME + ".getBillingHelperInstanceAndStartConnection";
        UtilityFunctions.logDebug(methodName, "Entered");

        BillingHelper billingHelper = BillingHelper.getInstance(this);
        billingHelper.startConnection();
        return billingHelper;
    }

    public static void setHasPurchasedNoAds(boolean hasPurchasedNoAds) {
        String methodName = CLASS_NAME + ".setHasPurchasedNoAds";
        UtilityFunctions.logDebug(methodName, "Entered");

        UtilityFunctions.logInfo(methodName, "Setting hasPurchasedNoAds: " + hasPurchasedNoAds);
        LightningDotsApplication.hasPurchasedNoAds = hasPurchasedNoAds;
        adStatusObservable.postValue(null);
    }

    public static ConsentStatus getConsentStatus() {
        return LightningDotsApplication.consentStatus;
    }
    public static void setConsentStatus(ConsentStatus consentStatus) {
        LightningDotsApplication.consentStatus = consentStatus;
        adStatusObservable.postValue(null);
    }

    public static boolean getUserIsFromEEA() {
        return LightningDotsApplication.userIsFromEEA;
    }
    public static void setUserIsFromEEA(boolean userIsFromEEA) {
        LightningDotsApplication.userIsFromEEA = userIsFromEEA;
    }

    public static boolean getUserPrefersNoAds() {
        return LightningDotsApplication.userPrefersNoAds;
    }
    public static void setUserPrefersNoAds(boolean userPrefersNoAds) {
        LightningDotsApplication.userPrefersNoAds = userPrefersNoAds;
        adStatusObservable.postValue(null);
    }

    public static boolean getAreAdsEnabled() {
        String methodName = CLASS_NAME + ".getAreAdsEnabled";
        UtilityFunctions.logDebug(methodName, "Entered");
        UtilityFunctions.logDebug(methodName,
                "getAreAdsEnabled(): hasPurchasedNoAds: " + hasPurchasedNoAds
                + "; userIsFromEEA: " + userIsFromEEA
                + "; userPrefersNoAds: " + userPrefersNoAds);
        boolean areAdsEnabled = !hasPurchasedNoAds
                && !(userIsFromEEA && userPrefersNoAds);
        UtilityFunctions.logDebug(methodName, "...areAdsEnabled: " + areAdsEnabled);
        return areAdsEnabled;
    }

    public static void setShowInstructions(Context context, boolean showInstructions) {
        String methodName = CLASS_NAME + ".setShowInstructions";
        UtilityFunctions.logDebug(methodName, "Entered");

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

        UtilityFunctions.logDebug(methodName, "Calling data changed in setShowInstructions()...");
        dataChanged(context);

    }

    public static void dataChanged(Context context) {
        String methodName = CLASS_NAME + ".dataChanged";
        UtilityFunctions.logDebug(methodName, "Entered");

        try {

            // Call the backup manager data changed
            BackupManager backupManager = new BackupManager(context);
            backupManager.dataChanged();

            UtilityFunctions.logDebug(methodName,"dataChanged() call complete.");
        } catch (Exception e) {
            UtilityFunctions.logError(methodName, "Call to dataChanged() failed", e);
        }
    }

}
