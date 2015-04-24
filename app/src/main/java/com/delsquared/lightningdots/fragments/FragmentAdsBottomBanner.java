package com.delsquared.lightningdots.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.delsquared.lightningdots.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class FragmentAdsBottomBanner extends Fragment {
	
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
		
		// Get the adview
		View fragmentView = getView();
		if (fragmentView != null) {
		
			AdView adView = (AdView) fragmentView.findViewById(R.id.adView);
			
			if (adView != null) {
			
				// Create the ad request
			    AdRequest adRequest = new AdRequest.Builder()
			        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
			        .addTestDevice("8D7B51BEF3C6133F9AF035FCDF3ADF9A")
			        .build();
			    
			    // Load the ad
			    adView.loadAd(adRequest);
			    
			    // Add a listener to handle when the ad fails to load
			    adView.setAdListener(new AdListener() {
			    	
			    	@Override
			    	public void onAdLoaded() {
			    		super.onAdLoaded();
			    		
			    		View fragmentView = (View) getView();
			    		
			    		if (fragmentView != null) {
			    		
			    			// Get the adview
			    			AdView adView = (AdView) fragmentView.findViewById(R.id.adView);
			    			
			    			if (adView != null) {
			    			
			    				// Show the adView
			    				adView.setVisibility(View.VISIBLE);
			    				
			    			}
			    			
			    		}
			    		
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
	
	public static FragmentAdsBottomBanner newInstance(boolean addMargins) {

		// Create the new instance
		FragmentAdsBottomBanner f = new FragmentAdsBottomBanner();
		
        return f;
    }

}
