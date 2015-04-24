package com.delsquared.lightningdots.activities;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

import java.util.Calendar;

public class ActivityAbout extends FragmentActivity {
	
	String versionName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		// Set the version name
		versionName = "";
		try {
			versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TextView textViewVersion = (TextView) findViewById(R.id.about_activity_textview_version);
		textViewVersion.setText(versionName);
		
		// Set the copyright message
		TextView textViewCopyright = (TextView) findViewById(R.id.about_activity_textview_copyright);
		Calendar calendar = Calendar.getInstance();
		int currentYear = calendar.get(Calendar.YEAR);
		String yearText = "";
		if (currentYear > 2014)
			yearText = "2014-";
		yearText += Integer.toString(currentYear);
		String copyrightText =
				String.format(
                        getString(R.string.activity_about_aboutapp_copyright)
                        , getString(R.string.app_name)
                        , yearText
                        , getString(R.string.company_name));
		textViewCopyright.setText(copyrightText);
		
		// Register screen view
		UtilityFunctions.registerScreenView(this, getString(R.string.ga_screenname_activityabout));
		
	}
	
	public void termsAndConditions(View view) {
		
		// Get the TOS intent
		Intent intentTermsAndConditions = new Intent(this, ActivityTermsOfService.class);
		
		// Start the new activity
		startActivity(intentTermsAndConditions);
	
	}
	
	public void privacyPolicy(View view) {
		
		// Get the privacy policy intent
		Intent intentPrivacyPolicy = new Intent(this, ActivityPrivacyPolicy.class);
		
		// Start the new activity
		startActivity(intentPrivacyPolicy);
	
	}

}
