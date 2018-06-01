package com.delsquared.lightningdots.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
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

public class FragmentAdsBottomBanner extends android.support.v4.app.Fragment {
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		
        // Inflate the layout for this fragment
		View theLayout = (View) inflater.inflate(R.layout.fragment_ads_bottom_banner, container, false);
	    
        return theLayout;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

    @Override
    public void onResume() {
        super.onResume();
        LightningDotsApplication.logDebugMessage("FragmentAdsBottomBanner.onResume()");

        handleToggleAds();

    }

    public static FragmentAdsBottomBanner newInstance(boolean addMargins) {

		// Create the new instance
		FragmentAdsBottomBanner f = new FragmentAdsBottomBanner();
		
        return f;

	}

	public void handleToggleAds() {

        // Get the shared preference for removing ads
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preferences_file_name), Activity.MODE_PRIVATE);
        boolean hasPurchasedNoAds = false;
        synchronized (LightningDotsApplication.lockSharedPreferences) {
            if (sharedPref.contains(getString(R.string.pref_product_remove_ads))) {
                hasPurchasedNoAds = sharedPref.getBoolean(getString(R.string.pref_product_remove_ads), false);
            }
        }
        hasPurchasedNoAds = hasPurchasedNoAds || LightningDotsApplication.hasPurchasedNoAds;

        // Track if this is a EEA user who hasn't given consent
        boolean isEEAUserWithoutConsent =
                LightningDotsApplication.userIsFromEEA
                        && (
                        LightningDotsApplication.userPrefersNoAds
                                || ConsentStatus.UNKNOWN.equals(LightningDotsApplication.consentStatus));

        LightningDotsApplication.logDebugMessage("hasPurchasedNoAds: " + hasPurchasedNoAds + "; isEEAUserWithoutConsent: " + isEEAUserWithoutConsent);
        boolean toggleAdsOff = hasPurchasedNoAds || isEEAUserWithoutConsent;
        LightningDotsApplication.logDebugMessage("toggleAdsOff: " + toggleAdsOff);
        toggleAds(!toggleAdsOff);

    }

    private void toggleAds(boolean toggleOn) {
	    LightningDotsApplication.logDebugMessage("toggleAds: toggleOn: " + toggleOn);

        // Get the adview
        View fragmentView = getView();
        if (fragmentView != null) {

            // Get the value for removing ads
            SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preferences_file_name), Activity.MODE_PRIVATE);
            boolean hasPurchasedNoAds = false;
            if (sharedPref.contains(getString(R.string.pref_product_remove_ads))) {
                hasPurchasedNoAds = sharedPref.getBoolean(getString(R.string.pref_product_remove_ads), false);
            }
            hasPurchasedNoAds = hasPurchasedNoAds || LightningDotsApplication.hasPurchasedNoAds;
            sharedPref.edit()
                    .putBoolean(
                            getString(R.string.pref_product_remove_ads)
                            , hasPurchasedNoAds)
                    .commit();
            LightningDotsApplication.hasPurchasedNoAds = hasPurchasedNoAds;

            // Get the adview
            AdView adView = (AdView) fragmentView.findViewById(R.id.adView);

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

                    // Create the ad request builder
                    AdRequest.Builder adRequestBuilder = new AdRequest.Builder()
                            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR);

                    // Check if we are in debug mode
                    //if (BuildConfig.DEBUG) {

                    // Add specific devices to test device list
                    // Motorolla Droid Rarz Maxx HD
                    adRequestBuilder.addTestDevice("8D7B51BEF3C6133F9AF035FCDF3ADF9A");
                    // Samsung Galaxy S5
                    adRequestBuilder.addTestDevice("C9BC6FE19C043A1AD5D28B767D91CE18");

                    //}

                    // Create the bundle for user ad preferences
                    Bundle extras = new Bundle();
                    if (ConsentStatus.NON_PERSONALIZED.equals(LightningDotsApplication.consentStatus)) {
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

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();

//                            View fragmentView = (View) getView();
//
//                            if (fragmentView != null) {
//
//                                // Get the adview
//                                AdView adView = (AdView) fragmentView.findViewById(R.id.adView);
//
//                                if (adView != null) {
//
//                                    // Show the adView
//                                    adView.setVisibility(View.VISIBLE);
//
//                                }
//
//                            }

                        }

                        @Override
                        public void onAdFailedToLoad(int errorCode) {
                            super.onAdFailedToLoad(errorCode);

                            View fragmentView = (View) getView();

                            if (fragmentView != null) {

                                // Get the adview
                                AdView adView = (AdView) fragmentView.findViewById(R.id.adView);

                                if (adView != null) {

                                    // Hide the adView
                                    adView.setVisibility(View.GONE);

                                }

                            }

                        }

                    });

                }

            }

        }

    }

}
