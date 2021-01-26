package com.delsquared.lightningdots.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.utilities.LightningDotsApplication;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;

public class FragmentAdsBottomBanner extends androidx.fragment.app.Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ads_bottom_banner, container, false);
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

    @Override
    public void onResume() {
        super.onResume();
        LightningDotsApplication.logDebugMessage("FragmentAdsBottomBanner.onResume()");

        // Set up observer to keep track of global ad status
        LightningDotsApplication.adStatusObservable.observe(this, object -> {
            LightningDotsApplication.logDebugMessage("adStatusObservable callback in FragmentAdsBottomBanner");
            handleToggleAds();
        });
        handleToggleAds();

    }

    @SuppressWarnings("unused")
    public static FragmentAdsBottomBanner newInstance() {

		// Create the new instance

        return new FragmentAdsBottomBanner();

	}

	public void handleToggleAds() {

	    Activity activity = getActivity();
	    if (activity == null) {
	        LightningDotsApplication.logDebugErrorMessage("activity is null");
	        return;
        }

	    boolean areAdsEnabled = LightningDotsApplication.getAreAdsEnabled();
        LightningDotsApplication.logDebugMessage("areAdsEnabled: " + areAdsEnabled);
        toggleAds(areAdsEnabled);

    }

    private void toggleAds(boolean toggleOn) {
	    LightningDotsApplication.logDebugMessage("toggleAds: toggleOn: " + toggleOn);

        // Get the adview
        View fragmentView = getView();
        if (fragmentView != null) {

            Activity activity = getActivity();
            if (activity == null) {
                LightningDotsApplication.logDebugErrorMessage("activity is null");
                return;
            }

            // Get the adview
            AdView adView = fragmentView.findViewById(R.id.adView);

            if (adView != null) {

                // Check if the user purchased the no ads item
                if (!toggleOn) {

                    // Disable loading ads
                    adView.setEnabled(false);

                    // Hide the ads
                    fragmentView.setVisibility(View.GONE);

                } else {

                    // Enable loading ads
                    adView.setEnabled(true);

                    // Show the ads
                    fragmentView.setVisibility(View.VISIBLE);

                    MobileAds.initialize(getContext(), initializationStatus -> startLoadingAds());

                }

            }

        }

    }

    private void startLoadingAds() {
        // Get the adview
        View fragmentView = getView();
        if (fragmentView == null) {
            LightningDotsApplication.logDebugErrorMessage("Fragment view is null");
            return;
        }

        // Get the adview
        AdView adView = fragmentView.findViewById(R.id.adView);

        // Create the ad request builder
        // Global request configuration is set in ActivityMain constructor
        // Test ads:
        // ca-app-pub-3940256099942544/6300978111
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();

        // Create the bundle for user ad preferences
        Bundle extras = new Bundle();
        if (ConsentStatus.NON_PERSONALIZED.equals(LightningDotsApplication.getConsentStatus())) {
            extras.putString("npa", "1");
        }

        // Create the ad request
        AdRequest adRequest = adRequestBuilder
                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                .build();

        // Load the ad
        adView.loadAd(adRequest);

        // Add a listener to handle when the ad fails to load
        adView.setAdListener(new AdListener() {

            @SuppressWarnings("EmptyMethod")
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);

                View fragmentView = getView();

                if (fragmentView != null) {

                    // Get the adview
                    AdView adView = fragmentView.findViewById(R.id.adView);

                    if (adView != null) {

                        // Hide the adView
                        adView.setVisibility(View.GONE);

                    }

                }

            }

        });
    }

}
