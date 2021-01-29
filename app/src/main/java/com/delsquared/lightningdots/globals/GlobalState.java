package com.delsquared.lightningdots.globals;

import androidx.lifecycle.MutableLiveData;

import com.delsquared.lightningdots.utilities.UtilityFunctions;
import com.google.android.gms.ads.initialization.InitializationStatus;

public class GlobalState {
    private static final String CLASS_NAME = GlobalState.class.getSimpleName();
    private static final GlobalState INSTANCE = new GlobalState();
    private static final Object lockGlobalState = new Object();
    public static final MutableLiveData<GlobalState> globalStateObserver = new MutableLiveData<>();

    /**
     * Flag indicating if MobileAds.initialize() has completed
     */
    private boolean isMobileAdsInitialized = false;

    /**
     * The MobileAds initialization status returned after MobileAds.initialize() completes
     */
    private InitializationStatus mobileAdsInitializationStatus = null;

    public static GlobalState getInstance() {
        String methodName = CLASS_NAME + ".getInstance";
        UtilityFunctions.logDebug(methodName, "Entered");
        return INSTANCE;
    }

    public boolean getIsMobileAdsInitialized() {
        String methodName = CLASS_NAME + ".getIsMobileAdsInitialized";
        UtilityFunctions.logDebug(methodName, "Entered");
        UtilityFunctions.logDebug(methodName, "isMobileAdsInitialized: " + this.isMobileAdsInitialized);

        return this.isMobileAdsInitialized;
    }

    @SuppressWarnings("unused")
    public InitializationStatus getMobileAdsInitializationStatus() {
        String methodName = CLASS_NAME + ".getMobileAdsInitializationStatus";
        UtilityFunctions.logDebug(methodName, "Entered");
        UtilityFunctions.logDebug(methodName, "mobileAdsInitializationStatus: " + this.mobileAdsInitializationStatus);

        return this.mobileAdsInitializationStatus;
    }

    @SuppressWarnings("unused")
    public void setIsMobileAdsInitialized(boolean isMobileAdsInitialized) {
        String methodName = CLASS_NAME + ".setIsMobileAdsInitialized(boolean isMobileAdsInitialized)";
        UtilityFunctions.logDebug(methodName, "Entered");
        UtilityFunctions.logDebug(methodName, "isMobileAdsInitialized: " + isMobileAdsInitialized);

        setIsMobileAdsInitialized(isMobileAdsInitialized, null);
    }

    public void setIsMobileAdsInitialized(boolean isMobileAdsInitialized, InitializationStatus initializationStatus) {
        String methodName = CLASS_NAME + ".setIsMobileAdsInitialized(boolean isMobileAdsInitialized, InitializationStatus initializationStatus)";
        UtilityFunctions.logDebug(methodName, "Entered");
        UtilityFunctions.logDebug(methodName, "isMobileAdsInitialized: " + isMobileAdsInitialized);
        UtilityFunctions.logDebug(methodName, "initializationStatus: " + initializationStatus);

        synchronized (lockGlobalState) {
            this.isMobileAdsInitialized = isMobileAdsInitialized;
            this.mobileAdsInitializationStatus = initializationStatus;
        }

        globalStateObserver.postValue(this);
    }
}
