package com.delsquared.lightningdots.activities;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

public class ActivityPrivacyPolicy extends FragmentActivity {
	private static final String CLASS_NAME = ActivityPrivacyPolicy.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		String methodName = CLASS_NAME + ".onCreate";
		UtilityFunctions.logDebug(methodName, "Entered");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_privacy_policy);
		
		// Register screen view
		UtilityFunctions.registerScreenView(this, getString(R.string.ga_screenname_activityprivacypolicy));
			    
	}

}
