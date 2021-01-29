package com.delsquared.lightningdots.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.ads.AdHelper;
import com.delsquared.lightningdots.globals.GlobalSettings;

/**
 * Provides helper functions for performing application startup
 */
public abstract class ApplicationStartupHelper {
    private static final String CLASS_NAME = ApplicationStartupHelper.class.getSimpleName();

    public static void performStartupTasks(@NonNull Context applicationContext) {
        String methodName = CLASS_NAME + ".performStartupTasks";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Load saved preferences
        loadSavedPreferences(applicationContext);

        // Initialize global services
        AdHelper.setupGlobalAdRequestConfiguration(applicationContext);

    }

    private static void loadSavedPreferences(@NonNull Context context) {
        String methodName = CLASS_NAME + ".loadSavedPreferences";
        UtilityFunctions.logDebug(methodName, "Entered");

        GlobalSettings globalSettings = GlobalSettings.getInstance();

        // Get the saved preferences reference
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preferences_file_name), Context.MODE_PRIVATE);

        // Set the global isAudioMuted flag
        globalSettings.setIsAudioMuted(sharedPref.getBoolean(context.getString(R.string.pref_setting_mute_audio), false));
        UtilityFunctions.logDebug(methodName, "...loaded isAudioMuted: " + globalSettings.getIsAudioMuted());

        // Set the global isShowInstructions flag
        globalSettings.setIsShowInstructions(sharedPref.getBoolean(context.getString(R.string.pref_setting_show_instructions), true));
        UtilityFunctions.logDebug(methodName, "...loaded isShowInstructions: " + globalSettings.getIsShowInstructions());

        // ---------- END Load saved settings ---------- //
    }



}
