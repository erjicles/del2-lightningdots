package com.delsquared.lightningdots.globals;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.utilities.LightningDotsApplication;
import com.delsquared.lightningdots.utilities.UtilityFunctions;
import com.google.ads.consent.ConsentStatus;
import com.google.android.gms.ads.MobileAds;

public class GlobalSettings {
    private static final String CLASS_NAME = GlobalSettings.class.getSimpleName();
    private static final GlobalSettings INSTANCE = new GlobalSettings();
    private static final Object lockGLobalSettings = new Object();

    // Global observables
    // These allow listeners to watch various settings
    public static final MutableLiveData<GlobalSettings> areAdsEnabledObservable = new MutableLiveData<>();
    public static final MutableLiveData<GlobalSettings> isAudioMutedObservable = new MutableLiveData<>();

    /**
     * Flag indicating if the instructions animation should be shown before the user plays the
     * game
     */
    private boolean isShowInstructions = true;

    /**
     * Flag indicating if game audio is muted.
     */
    private boolean isAudioMuted = false;

    /**
     * Flag indicating if the user has purchased no ads
     */
    private boolean hasPurchasedNoAds = false;

    /**
     * Consent status for EEA users
     */
    private ConsentStatus consentStatus = ConsentStatus.UNKNOWN;

    /**
     * Flag indicating if EEA user prefers no ads
     */
    private boolean userPrefersNoAds = false;

    /**
     * Flag indicating if user is from the EEA
     */
    private boolean isUserFromEEA = false;

    @NonNull
    public static GlobalSettings getInstance() {
        return INSTANCE;
    }

    public boolean getIsShowInstructions() {
        String methodName = CLASS_NAME + ".getIsShowInstructions";
        UtilityFunctions.logDebug(methodName, "Entered");
        UtilityFunctions.logDebug(methodName, "Getting isShowInstructions: " + isShowInstructions);

        return this.isShowInstructions;
    }

    public void setIsShowInstructions(boolean isShowInstructions) {
        String methodName = CLASS_NAME + ".setShowInstructions(boolean isShowInstructions)";
        UtilityFunctions.logDebug(methodName, "Entered");
        UtilityFunctions.logDebug(methodName, "isShowInstructions: " + isShowInstructions);

        setIsShowInstructions(null, isShowInstructions);
    }

    public void setIsShowInstructions(Context context, boolean isShowInstructions) {
        String methodName = CLASS_NAME + ".setShowInstructions(Context context, boolean isShowInstructions)";
        UtilityFunctions.logDebug(methodName, "Entered");
        UtilityFunctions.logDebug(methodName, "isShowInstructions: " + isShowInstructions);

        synchronized (lockGLobalSettings) {

            this.isShowInstructions = isShowInstructions;

            if (context != null) {
                UtilityFunctions.logInfo(methodName, "Saving value to shared preferences file");

                // Get the shared preferences reference
                SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preferences_file_name), Context.MODE_PRIVATE);

                synchronized (LightningDotsApplication.lockSharedPreferences) {

                    // Update the shared preferences file
                    sharedPref.edit()
                            .putBoolean(
                                    context.getString(R.string.pref_setting_show_instructions)
                                    , isShowInstructions)
                            .apply();

                }

                UtilityFunctions.logDebug(methodName, "Calling LightningDotsApplication.dataChanged...");
                LightningDotsApplication.dataChanged(context);
            }

        }

    }

    public boolean getIsAudioMuted() {
        String methodName = CLASS_NAME + ".getIsAudioMuted";
        UtilityFunctions.logDebug(methodName, "Entered");
        UtilityFunctions.logDebug(methodName, "Getting isAudioMuted: " + isAudioMuted);

        return this.isAudioMuted;
    }

    public void setIsAudioMuted(boolean isAudioMuted) {
        String methodName = CLASS_NAME + ".setIsAudioMuted(boolean isAudioMuted)";
        UtilityFunctions.logDebug(methodName, "Entered");
        UtilityFunctions.logDebug(methodName, "isAudioMuted: " + isAudioMuted);

        setIsAudioMuted(null, isAudioMuted);
    }

    public void setIsAudioMuted(Context context, boolean isAudioMuted) {
        String methodName = CLASS_NAME + ".setIsAudioMuted(Context context, boolean isAudioMuted)";
        UtilityFunctions.logDebug(methodName, "Entered");
        UtilityFunctions.logDebug(methodName, "isAudioMuted: " + isAudioMuted);

        synchronized (lockGLobalSettings) {

            this.isAudioMuted = isAudioMuted;

            if (context != null) {
                UtilityFunctions.logInfo(methodName, "Saving value to shared preferences file");

                // Get the shared preferences reference
                SharedPreferences sharedPref = context.getSharedPreferences(
                        context.getString(R.string.preferences_file_name),
                        Activity.MODE_PRIVATE);

                synchronized (LightningDotsApplication.lockSharedPreferences) {

                    // Update the shared preferences file
                    sharedPref.edit()
                            .putBoolean(
                                    context.getString(R.string.pref_setting_mute_audio)
                                    , isAudioMuted)
                            .apply();

                }

                UtilityFunctions.logDebug(methodName, "Calling LightningDotsApplication.dataChanged...");
                LightningDotsApplication.dataChanged(context);
            }

        }

        // Set the muted status if MobileAds.initialize() has completed
        if (GlobalState.getInstance().getIsMobileAdsInitialized()) {
            UtilityFunctions.logInfo(methodName, "Calling mobileAds.setAppMuted(" + isAudioMuted + ")");
            MobileAds.setAppMuted(isAudioMuted);
        }

        UtilityFunctions.logDebug(methodName, "Posting updated value to observable");
        isAudioMutedObservable.postValue(this);
    }

    public void setHasPurchasedNoAds(boolean hasPurchasedNoAds) {
        String methodName = CLASS_NAME + ".setHasPurchasedNoAds";
        UtilityFunctions.logDebug(methodName, "Entered");
        UtilityFunctions.logInfo(methodName, "Setting hasPurchasedNoAds: " + hasPurchasedNoAds);

        this.hasPurchasedNoAds = hasPurchasedNoAds;
        areAdsEnabledObservable.postValue(this);
    }

    public ConsentStatus getConsentStatus() {
        String methodName = CLASS_NAME + ".getConsentStatus";
        UtilityFunctions.logDebug(methodName, "Entered");
        UtilityFunctions.logInfo(methodName, "Getting consentStatus: " + consentStatus);

        return this.consentStatus;
    }

    public  void setConsentStatus(ConsentStatus consentStatus) {
        String methodName = CLASS_NAME + ".setConsentStatus";
        UtilityFunctions.logDebug(methodName, "Entered");
        UtilityFunctions.logInfo(methodName, "Setting consentStatus: " + consentStatus);

        this.consentStatus = consentStatus;
        areAdsEnabledObservable.postValue(this);
    }

    public boolean getIsUserFromEEA() {
        String methodName = CLASS_NAME + ".getIsUserFromEEA";
        UtilityFunctions.logDebug(methodName, "Entered");
        UtilityFunctions.logInfo(methodName, "Getting isUserFromEEA: " + isUserFromEEA);

        return this.isUserFromEEA;
    }
    public void setIsUserFromEEA(boolean isUserFromEEA) {
        String methodName = CLASS_NAME + ".setIsUserFromEEA";
        UtilityFunctions.logDebug(methodName, "Entered");
        UtilityFunctions.logInfo(methodName, "Setting isUserFromEEA: " + isUserFromEEA);

        this.isUserFromEEA = isUserFromEEA;
        areAdsEnabledObservable.postValue(this);
    }

    public boolean getUserPrefersNoAds() {
        String methodName = CLASS_NAME + ".getUserPrefersNoAds";
        UtilityFunctions.logDebug(methodName, "Entered");
        UtilityFunctions.logInfo(methodName, "Getting userPrefersNoAds: " + userPrefersNoAds);

        return this.userPrefersNoAds;
    }
    public void setUserPrefersNoAds(boolean userPrefersNoAds) {
        String methodName = CLASS_NAME + ".setUserPrefersNoAds";
        UtilityFunctions.logDebug(methodName, "Entered");
        UtilityFunctions.logInfo(methodName, "Setting userPrefersNoAds: " + userPrefersNoAds);

        this.userPrefersNoAds = userPrefersNoAds;
        areAdsEnabledObservable.postValue(this);
    }

    public boolean getAreAdsEnabled() {
        String methodName = CLASS_NAME + ".getAreAdsEnabled";
        UtilityFunctions.logDebug(methodName, "Entered");
        UtilityFunctions.logDebug(methodName,
                "hasPurchasedNoAds: " + hasPurchasedNoAds
                        + "; userIsFromEEA: " + isUserFromEEA
                        + "; userPrefersNoAds: " + userPrefersNoAds);
        boolean areAdsEnabled = !hasPurchasedNoAds
                && !(isUserFromEEA && userPrefersNoAds);
        UtilityFunctions.logDebug(methodName, "...areAdsEnabled: " + areAdsEnabled);
        return areAdsEnabled;
    }
}
