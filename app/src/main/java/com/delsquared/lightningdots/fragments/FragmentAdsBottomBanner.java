package com.delsquared.lightningdots.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.ads.AdHelper;
import com.delsquared.lightningdots.globals.GlobalSettings;
import com.delsquared.lightningdots.globals.GlobalState;
import com.delsquared.lightningdots.utilities.UtilityFunctions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

public class FragmentAdsBottomBanner extends androidx.fragment.app.Fragment {
    private static final String CLASS_NAME = FragmentAdsBottomBanner.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        String methodName = CLASS_NAME + ".onCreate";
        UtilityFunctions.logDebug(methodName, "Entered");
        super.onCreate(savedInstanceState);
    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String methodName = CLASS_NAME + ".onCreateView";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ads_bottom_banner, container, false);
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
        String methodName = CLASS_NAME + ".onActivityCreated";
        UtilityFunctions.logDebug(methodName, "Entered");
		super.onActivityCreated(savedInstanceState);
	}

    @Override
    public void onResume() {
        String methodName = CLASS_NAME + ".onResume";
        UtilityFunctions.logDebug(methodName, "Entered");
        super.onResume();

        // Set up observer to keep track of global ad status
        GlobalSettings.areAdsEnabledObservable.observe(this, globalSettings -> {
            UtilityFunctions.logDebug(methodName, "globalSettingsObservable callback in FragmentAdsBottomBanner");
            handleToggleAds();
        });
        GlobalState.globalStateObserver.observe(this, globalState -> {
            UtilityFunctions.logDebug(methodName, "globalStateObserver callback in FragmentAdsBottomBanner");
            handleToggleAds();
        });
        handleToggleAds();

    }

    @SuppressWarnings("unused")
    public static FragmentAdsBottomBanner newInstance() {
        String methodName = CLASS_NAME + ".newInstance";
        UtilityFunctions.logDebug(methodName, "Entered");

		// Create the new instance
        return new FragmentAdsBottomBanner();

	}

	public void handleToggleAds() {
        String methodName = CLASS_NAME + ".handleToggleAds";
        UtilityFunctions.logDebug(methodName, "Entered");

	    Activity activity = getActivity();
	    if (activity == null) {
	        UtilityFunctions.logError(methodName, "activity is null", null);
	        return;
        }

	    if (!GlobalState.getInstance().getIsMobileAdsInitialized()) {
	        UtilityFunctions.logDebug(methodName, "MobileAds not yet initialized");
	        return;
        }

	    boolean areAdsEnabled = GlobalSettings.getInstance().getAreAdsEnabled();
        UtilityFunctions.logDebug(methodName, "areAdsEnabled: " + areAdsEnabled);
        toggleAds(areAdsEnabled);

    }

    /**
     * This should only be called after MobileAds.initialize() has completed.
     * @param toggleOn Flag indicating if ads should be on or off
     */
    private void toggleAds(boolean toggleOn) {
        String methodName = CLASS_NAME + ".toggleAds";
        UtilityFunctions.logDebug(methodName, "Entered");
	    UtilityFunctions.logDebug(methodName,"toggleAds: toggleOn: " + toggleOn);

        // Get the adview
        LinearLayout fragmentView = (LinearLayout) getView();
        if (fragmentView == null) {
            UtilityFunctions.logError(methodName, "fragmentView is null");
            return;
        }

        Activity activity = getActivity();
        if (activity == null) {
            UtilityFunctions.logError(methodName, "activity is null", null);
            return;
        }

        // Get the adview
        AdView adView = fragmentView.findViewById(R.id.adview_bottom_banner);

        // Handle toggling ads off
        if (!toggleOn) {
            fragmentView.setVisibility(View.GONE);
            // Toggle ads off by removing the adview
            if (adView == null) {
                UtilityFunctions.logDebug(methodName, "adView is already null");
                return;
            }
            UtilityFunctions.logDebug(methodName, "Removing adView");
            fragmentView.removeView(adView);
            return;
        }

        // If the adview already exists, then we're all set
        if (adView != null) {
            UtilityFunctions.logDebug(methodName, "adView already exists");
            return;
        }

        fragmentView.setVisibility(View.VISIBLE);

        // Inflate the adview and add it to the parent, and load ads
        getLayoutInflater().inflate(R.layout.adview_bottom_banner, fragmentView);
        startLoadingAds();

    }

    /**
     * This starts loading ads. It should only be called after MobileAds.initialize() is complete
     * and if ads are toggled on
     */
    private void startLoadingAds() {
        String methodName = CLASS_NAME + ".startLoadingAds";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Get the adview
        View fragmentView = getView();
        if (fragmentView == null) {
            UtilityFunctions.logError(methodName, "fragmentView is null", null);
            return;
        }

        // Get the adview
        AdView adView = fragmentView.findViewById(R.id.adview_bottom_banner);
        if (adView.isLoading()) {
            UtilityFunctions.logDebug(methodName, "AdView is already loading ads");
            return;
        }

        // Get the ad request
        AdRequest adRequest = AdHelper.getAdRequest();

        // Load the ad
        adView.loadAd(adRequest);

        // Add a listener to handle when the ad fails to load
        adView.setAdListener(new AdListener() {

            @SuppressWarnings("EmptyMethod")
            @Override
            public void onAdLoaded() {
                UtilityFunctions.logInfo(methodName, "Entered onAdLoaded");
                super.onAdLoaded();

                View fragmentView = getView();
                if (fragmentView == null) {
                    UtilityFunctions.logError(methodName, "onAdLoaded: fragmentView is null");
                    return;
                }

                // Show the ads
                fragmentView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                UtilityFunctions.logInfo(methodName, "Entered onAdFailedToLoad");
                super.onAdFailedToLoad(loadAdError);

                View fragmentView = getView();
                if (fragmentView == null) {
                    UtilityFunctions.logError(methodName, "onAdFailedToLoad: fragmentView is null");
                    return;
                }

                // Hide the ads
                fragmentView.setVisibility(View.GONE);

            }

        });
    }

}
