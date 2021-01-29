package com.delsquared.lightningdots.ads;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.globals.GlobalSettings;
import com.delsquared.lightningdots.globals.GlobalState;
import com.delsquared.lightningdots.utilities.UtilityFunctions;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

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
     * Loads an InterstitialAd using the {@link R.string#ads_ad_unit_game_interstitials} ad
     * unit id. When the ad is loaded, passes the interstitial ad instance to the provided
     * {@link IInterstitialAdHolder} activity/fragment.
     * @param context The context of the activity/fragment that will show the interstitial ad
     */
    public static void loadInterstitialAd(
            @NonNull Context context,
            @NonNull IInterstitialAdHolder interstitialAdHolder) {
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

        // Get the ad request
        AdRequest adRequest = getAdRequest();

        // Load the interstitial ad
        // Documentation found here:
        // https://developers.google.com/admob/android/interstitial-fullscreen
        // Test ad unit:
        // ca-app-pub-3940256099942544/1033173712
        InterstitialAd.load(
                context,
                context.getString(R.string.ads_ad_unit_game_interstitials),
                adRequest,
                new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                UtilityFunctions.logInfo(methodName, "Entered onAdLoaded");

                // Setup the full screen callback listener
                setupInterstitialAdFullScreenContentCallback(interstitialAd, interstitialAdHolder);

                // Pass the interstitialAd instance to the interstitialAdDisplayer activity
                interstitialAdHolder.onInterstitialAdLoaded(interstitialAd);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                UtilityFunctions.logDebug(methodName, "Entered onAdFailedToLoad");
                UtilityFunctions.logWarning(methodName, "loadAdError message: " + loadAdError.getMessage());
            }
        });
    }

    private static void setupInterstitialAdFullScreenContentCallback(
            @NonNull InterstitialAd interstitialAd,
            @NonNull IInterstitialAdHolder interstitialAdHolder) {
        String methodName = CLASS_NAME + ".setupInterstitialAdFullScreenContentCallback";
        UtilityFunctions.logDebug(methodName, "Entered");

        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when fullscreen content is dismissed.
                UtilityFunctions.logDebug(methodName, "Entered onAdDismissedFullScreenContent()");
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when fullscreen content failed to show.
                UtilityFunctions.logDebug(methodName, "Entered onAdFailedToShowFullScreenContent()");
                UtilityFunctions.logWarning(methodName, "adError message: " + adError.getMessage());
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when fullscreen content is shown.
                // Make sure to set your reference to null so you don't
                // show it a second time.
                UtilityFunctions.logDebug(methodName, "Entered onAdShowedFullScreenContent()");

                interstitialAdHolder.onAdShowedFullScreenContent();
            }
        });
    }

    /**
     * Shows an interstitial ad
     * @param interstitialAd The interstitial ad object
     * @param activity The activity that should show the ad
     */
    public static void showInterstitialAd(
            @NonNull InterstitialAd interstitialAd,
            Activity activity) {
        String methodName = CLASS_NAME + ".showInterstitialAd";
        UtilityFunctions.logDebug(methodName, "Entered");

        interstitialAd.show(activity);

    }

}
