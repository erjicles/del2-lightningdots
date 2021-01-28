package com.delsquared.lightningdots.activities;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

public class ActivityTermsOfService extends FragmentActivity {
	private static final String CLASS_NAME = ActivityTermsOfService.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		String methodName = CLASS_NAME + ".onCreate";
		UtilityFunctions.logDebug(methodName, "Entered");
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_terms_of_service);
		
		// Register screen view
		UtilityFunctions.registerScreenView(this, getString(R.string.ga_screenname_activitytermsofservice));
	    
	}

}
