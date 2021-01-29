package com.delsquared.lightningdots.utilities;

import android.app.Application;
import android.app.backup.BackupManager;
import android.content.Context;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.billing.BillingHelper;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

public class LightningDotsApplication extends Application {
    private static final String CLASS_NAME = LightningDotsApplication.class.getSimpleName();

    public static final Object lockSharedPreferences = new Object();

    public static int numberOfGameTransitions = 0;

    public LightningDotsApplication() {
        super();
        String methodName = CLASS_NAME + ".constructor";
        UtilityFunctions.logDebug(methodName, "Entered");
    }

    @Override
    public void onCreate() {
        String methodName = CLASS_NAME + ".onCreate";
        UtilityFunctions.logDebug(methodName, "Entered");
        super.onCreate();

        ApplicationStartupHelper.performStartupTasks(this);
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
