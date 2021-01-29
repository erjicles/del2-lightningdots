package com.delsquared.lightningdots.ads;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.globals.GlobalSettings;
import com.delsquared.lightningdots.globals.GlobalState;
import com.delsquared.lightningdots.utilities.UtilityFunctions;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AdHelper {
    private static final String CLASS_NAME = AdHelper.class.getSimpleName();

    public static void setupGlobalAdRequestConfiguration(@NonNull Context context) {
        String methodName = CLASS_NAME + ".setupGlobalAdRequestConfiguration";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Set global ad request configuration
        // Add specific devices to test device list
        List<String> testDeviceIds = new ArrayList<>();
        testDeviceIds.add(AdRequest.DEVICE_ID_EMULATOR);
        testDeviceIds.addAll(
                Arrays.asList(
                        context.getResources().getStringArray(R.array.test_device_ids_ads)
                )
        );
        RequestConfiguration requestConfiguration = MobileAds.getRequestConfiguration()
                .toBuilder()
                .setTestDeviceIds(testDeviceIds)
                .build();
        MobileAds.setRequestConfiguration(requestConfiguration);

        // TODO: Handle consent flow before initializing
        MobileAds.initialize(context, initializationStatus -> {
            GlobalState.getInstance().setIsMobileAdsInitialized(true, initializationStatus);

            // Initialize the muted status
            boolean isAudioMuted = GlobalSettings.getInstance().getIsAudioMuted();
            UtilityFunctions.logInfo(methodName, "Calling mobileAds.setAppMuted(" + isAudioMuted + ")");
            MobileAds.setAppMuted(isAudioMuted);
        });
    }

    public static AdRequest getAdRequest() {
        String methodName = CLASS_NAME + ".getAdRequest";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Create the ad request builder
        // Test ads:
        // ca-app-pub-3940256099942544/6300978111
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();

        // Create the bundle for user ad preferences
        Bundle extras = new Bundle();
        if (ConsentStatus.NON_PERSONALIZED.equals(GlobalSettings.getInstance().getConsentStatus())) {
            extras.putString("npa", "1");
        }

        // Create the ad request
        AdRequest adRequest = adRequestBuilder
                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                .build();

        UtilityFunctions.logDebug(methodName, "adRequest contentUrl: " + adRequest.getContentUrl());

        return adRequest;
    }

    /**
     * Creates a new InterstitialAd using the {@link R.string#ads_ad_unit_game_interstitials} ad
     * unit id. Starts loading the ad and sets up a listener to load a new ad after the previous
     * ad is closed.
     * @param context The context of the activity/fragment creating the interstitial ad
     * @return The new interstitial ad
     */
    public static InterstitialAd getAndStartLoadingInterstitialAd(@NonNull Context context) {
        String methodName = CLASS_NAME + ".getInterstitialAd";
        UtilityFunctions.logDebug(methodName, "Entered");

        InterstitialAd interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(context.getString(R.string.ads_ad_unit_game_interstitials));

        loadInterstitialAd(interstitialAd);

        // Set an AdListener.
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                UtilityFunctions.logInfo(methodName, "Entered onAdLoaded() for interstitial ad");
            }

            @Override
            public void onAdClosed() {
                UtilityFunctions.logInfo(methodName, "Entered onAdClosed() for interstitial ad");
                // Start loading the next ad so it's ready to show when needed
                loadInterstitialAd(interstitialAd);
            }
        });

        return interstitialAd;
    }

    /**
     * Starts loading an interstitial ad by calling loadAd() on the provided interstitialAd
     * @param interstitialAd The interstitial ad object to start loading ads
     */
    public static void loadInterstitialAd(@NonNull InterstitialAd interstitialAd) {
        String methodName = CLASS_NAME + ".loadInterstitialAd";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Check if MobileAds.initialize() has not completed
        if (!GlobalState.getInstance().getIsMobileAdsInitialized()) {
            UtilityFunctions.logWarning(methodName, "MobileAds.initialize() has not completed, skipping");
            return;
        }

        // Check if the user has purchased the no ads item
        // or if the user is a non-consenting EEU user
        if (!GlobalSettings.getInstance().getAreAdsEnabled()) {
            UtilityFunctions.logInfo(methodName, "Ads are disabled, skipping");
            return;
        }

        if (interstitialAd.isLoaded()) {
            UtilityFunctions.logDebug(methodName, "interstitialAd.isLoaded() is true, skipping");
            return;
        }
        if (interstitialAd.isLoading()) {
            UtilityFunctions.logDebug(methodName, "interstitialAd.isLoading() is true, skipping");
            return;
        }

        // Get the ad request
        AdRequest adRequest = getAdRequest();

        // Start loading the ad
        UtilityFunctions.logDebug(methodName, "Calling interstitialAd.loadAd()");
        interstitialAd.loadAd(adRequest);
    }

    /**
     * Shows an interstitial ad if the ad is loaded.
     * If the ad is not loaded, and no ad is loading, then attempts to start loading an ad
     * @param interstitialAd The interstitial ad object
     */
    public static void showInterstitialAd(@NonNull InterstitialAd interstitialAd) {
        String methodName = CLASS_NAME + ".showInterstitialAd";
        UtilityFunctions.logDebug(methodName, "Entered");

        if (interstitialAd.isLoaded()) {
            UtilityFunctions.logInfo(methodName, "Showing interstitial ad");
            interstitialAd.show();
        } else if (!interstitialAd.isLoading()) {
            UtilityFunctions.logInfo(methodName, "Interstitial ad is neither loaded nor loading; starting loading...");
            loadInterstitialAd(interstitialAd);
        }

    }

}
