package com.delsquared.lightningdots.fragments;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

public class FragmentPrivacyPolicy extends Fragment {
	private static final String CLASS_NAME = FragmentPrivacyPolicy.class.getSimpleName();
	
	private static final String MARGINS_KEY = "addMargins";
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		String methodName = CLASS_NAME + ".onCreateView";
		UtilityFunctions.logDebug(methodName, "Entered");

        // Inflate the layout for this fragment
		View theLayout = inflater.inflate(R.layout.fragment_privacy_policy, container, false);
		
		// Get the webview
	    WebView webViewPrivacyPolicy = theLayout.findViewById(R.id.fragment_privacypolicy_webview_privacypolicy);
	    
	    // Get the args
	    Bundle args = getArguments();
	    
    	// Get the privacy policy url
	    String privacyPolicyUrl = getString(R.string.privacy_policy_url);
	    
	    // Set the web view client
	    webViewPrivacyPolicy.setWebViewClient(new WebViewClient() {
	    	
	    	public void onPageFinished(WebView view, String url) {
	    		View mainView = getView();
	    		if (mainView == null) {
					UtilityFunctions.logError(methodName, "mainView is null", null);
					return;
				}
	    		// Get the loading spinner
	    		ProgressBar loadingProgressBar =
						mainView.findViewById(R.id.fragment_privacypolicy_progressbar_loadingprogressbar);
	    		if (loadingProgressBar == null) {
	    			UtilityFunctions.logError(methodName, "loadingProgressBar is null", null);
	    			return;
				}
	    		
	    		// Set the visibility to invisible
	    		loadingProgressBar.setVisibility(View.GONE);
	    	}
	    	
	    	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
	    		
	    		String mime = "text/html";
	    	    String encoding = "utf-8";
	    	    
	    	    String htmlString = getString(R.string.dialog_accepttermsnointernet_nointernet)
	    	    		+ getString(R.string.dialog_accepttermsnointernet_text)
	    				.replace(
	    						getString(R.string.dialog_accepttermsnointernet_termsurlplaceholder)
	    						, getString(R.string.terms_of_service_url))
	    				.replace(
	    						getString(R.string.dialog_accepttermsnointernet_privacypolicyurlplaceholder)
	    						, getString(R.string.privacy_policy_url))
	    				.replace(
	    						getString(R.string.dialog_accepttermsnointernet_termsplaceholder)
	    						, getString(R.string.terms_of_service_activity_title))
	    				.replace(
	    						getString(R.string.dialog_accepttermsnointernet_privacypolicyplaceholder)
	    						, getString(R.string.privacy_policy_activity_title));
	    	    
	    		view.loadData(htmlString, mime, encoding);
	    	}
	    });
	    
	    webViewPrivacyPolicy.loadUrl(privacyPolicyUrl);
	    
	    // Check if we need to add margins
	    if (args != null && args.getBoolean(MARGINS_KEY)) {
	    	
	    	// Add margins
            webViewPrivacyPolicy.setPadding(
            		theLayout.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin)
            		, theLayout.getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin)
            		, theLayout.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin)
            		, theLayout.getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin));
            
	    }
	    
        return theLayout;
    }

	@SuppressWarnings("unused")
	public static FragmentPrivacyPolicy newInstance(
			boolean addMargins) {
		String methodName = CLASS_NAME + ".newInstance";
		UtilityFunctions.logDebug(methodName, "Entered");

		// Create the new instance
		FragmentPrivacyPolicy f = new FragmentPrivacyPolicy();
		
		// Add the flag whether to programmatically set the margins
		Bundle args = new Bundle();
		args.putBoolean(MARGINS_KEY, addMargins);
		f.setArguments(args);
		
        return f;
    }

}
