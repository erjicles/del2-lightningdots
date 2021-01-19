package com.delsquared.lightningdots.activities;

import androidx.fragment.app.FragmentActivity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.billing_utilities.IabResult;
import com.delsquared.lightningdots.billing_utilities.Purchase;
import com.delsquared.lightningdots.fragments.AcceptTermsDialog;
import com.delsquared.lightningdots.fragments.FragmentMain;
import com.delsquared.lightningdots.game.Game;
import com.delsquared.lightningdots.utilities.LightningDotsApplication;
import com.delsquared.lightningdots.utilities.PurchaseHelper;
import com.delsquared.lightningdots.utilities.UtilityFunctions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActivityMain extends FragmentActivity implements AcceptTermsDialog.AcceptTermsDialogListener {

	public static final String EXTRA_GAME_TYPE = "com.delsquared.lightningdots.gametype";

    AcceptTermsDialog termsDialog;
    protected String currentTermsVersion = "";
    protected String currentPrivacyPolicyVersion = "";

    PurchaseHelper purchaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new FragmentMain())
                    .commit();
        }

        // Set global ad request configuration
        // Add specific devices to test device list
        List<String> testDeviceIds = new ArrayList<String>();
        testDeviceIds.add(AdRequest.DEVICE_ID_EMULATOR);
        testDeviceIds.addAll(
                Arrays.asList(
                        getResources().getStringArray(R.array.test_device_ids_ads)
                )
        );
        RequestConfiguration requestConfiguration = MobileAds.getRequestConfiguration()
                .toBuilder()
                .setTestDeviceIds(testDeviceIds)
                .build();
        MobileAds.setRequestConfiguration(requestConfiguration);

        // Handle showing the accept terms dialog
        toggleAcceptTermsDialog();

        // ---------- BEGIN Load saved settings ---------- //
        // Get the saved preferences reference
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preferences_file_name), MODE_PRIVATE);

        // Set the global settings flag from the saved prefs
        LightningDotsApplication.settingShowInstructions =
                sharedPref.getBoolean(getString(R.string.pref_setting_show_instructions), true);

        // ---------- END Load saved settings ---------- //

        // Setup the purchase helper
        purchaseHelper = new PurchaseHelper(
                this
                , new PurchaseHelper.InterfaceSetupFinishedCallback() {

                    @Override
                    public void onSetupFinished(boolean success, IabResult result) {

                    }

                }
                , new PurchaseHelper.InterfaceQueryInventoryCallback() {

                    @Override
                    public void onQueryInventoryFinished() {
                    }
                }
                , new PurchaseHelper.InterfacePurchaseFinishedCallback() {

                    @Override
                    public void onPurchaseFinished(Purchase purchase) {
                    }
                }
        );

        // Register screen view
        UtilityFunctions.registerScreenView(this, getString(R.string.ga_screenname_activitymain));

	}

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (purchaseHelper != null) {
            purchaseHelper.onDestroy();
        }
        purchaseHelper = null;
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            launchSettings();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */

    public void clicked_timeAttack(View view) {
        launchGame(Game.GameType.TIME_ATTACK.ordinal());
    }

    public void clicked_ladder(View view) {
        launchGame(Game.GameType.AGILITY.ordinal());
    }

    public void clicked_settings(View view) { launchSettings(); }

    public void clicked_about(View view) { launchAbout(); }

    public void clicked_store(View view) { launchStore(); }

    private void launchGame(int gameType) {

        // Check if we should show the instructions
        if (LightningDotsApplication.settingShowInstructions
                && gameType == Game.GameType.AGILITY.ordinal()) {

            // Start the instructions activity
            Intent intent = new Intent(this, ActivityInstructions.class);
            intent.putExtra(EXTRA_GAME_TYPE, gameType);
            startActivity(intent);

        } else { // Do not show instructions

            // Start the game activity
            Intent intent = new Intent(this, ActivityGame.class);
            intent.putExtra(EXTRA_GAME_TYPE, gameType);
            startActivity(intent);

        }

    }

    public void launchSettings() {

        // Start the settings activity
        Intent settingsIntent = new Intent(this, ActivitySettings.class);
        startActivity(settingsIntent);

    }

    public void launchAbout() {

        // Start the about activity
        Intent aboutActivityIntent = new Intent(this, ActivityAbout.class);
        startActivity(aboutActivityIntent);

    }

    public void launchStore() {

        // Start the store activity
        Intent intentStoreActivity = new Intent(this, ActivityStore.class);
        startActivity(intentStoreActivity);

    }

    private void toggleAcceptTermsDialog() {

        // Get the terms of service saved preference
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preferences_file_name), MODE_PRIVATE);
        String acceptedTermsOfServiceVersion =
                sharedPref.getString(
                        getString(R.string.pref_legal_acceptedtermsversion)
                        , getString(R.string.terms_of_service_defaultversion));

        // Get the privacy policy saved preference
        String acceptedPrivacyPolicyVersion =
                sharedPref.getString(
                        getString(R.string.pref_legal_acceptedprivacyversion)
                        , getString(R.string.privacy_policy_defaultversion));

        // Initialize the currently accepted major versions
        int acceptedTermsMajorVersion = 0;
        int acceptedPrivacyPolicyMajorVersion = 0;

        try
        {

            // Get the currently accepted major versions
            acceptedTermsMajorVersion = Integer.parseInt(acceptedTermsOfServiceVersion.split("\\.")[0]);
            acceptedPrivacyPolicyMajorVersion = Integer.parseInt(acceptedPrivacyPolicyVersion.split("\\.")[0]);

        } catch (Exception e) {

            // Reset the currently accepted major versions
            acceptedTermsMajorVersion = 0;
            acceptedPrivacyPolicyMajorVersion = 0;

        }

        // Initialize the current version strings
        currentTermsVersion = "";
        currentPrivacyPolicyVersion = "";
        int currentTermsMajorVersion = 0;
        int currentPrivacyPolicyMajorVersion = 0;

        // Get the data from the intent
        Intent intent = getIntent();
        String jsonVersionsString = intent.getStringExtra(ActivitySplash.JSON_VERSIONS_STRING_KEY);
        boolean versionsLoadSuccessful = intent.getBooleanExtra(ActivitySplash.VERSIONS_LOAD_SUCCESSFUL_KEY, false);

        if (versionsLoadSuccessful == true) {

            // Get the json keys for the versions
            String key1 = getString(R.string.json_response_key_termsofservice);
            String key2 = getString(R.string.json_response_key_privacypolicy);

            try {

                // Parse the JSON
                JSONObject jObject = new JSONObject(jsonVersionsString);
                currentTermsVersion = jObject.getString(key1);
                currentPrivacyPolicyVersion = jObject.getString(key2);

                // Get the current major versions
                currentTermsMajorVersion = Integer.parseInt(currentTermsVersion.split("\\.")[0]);
                currentPrivacyPolicyMajorVersion = Integer.parseInt(currentPrivacyPolicyVersion.split("\\.")[0]);

            } catch(Exception e) {

                // Reset the current versions
                currentTermsVersion = "";
                currentPrivacyPolicyVersion = "";
                currentTermsMajorVersion = 0;
                currentPrivacyPolicyMajorVersion = 0;

            }

            if (acceptedTermsMajorVersion == 0
                    || acceptedPrivacyPolicyMajorVersion == 0) {

                // Show the dialog for first time users
                showAcceptTermsDialog(AcceptTermsDialog.ShowAcceptTermsDialogReason.NONE_ACCEPTED);

                // Check if the accepted terms version or accepted privacy version are less than the current version
            } else if (acceptedTermsMajorVersion < currentTermsMajorVersion
                    || acceptedPrivacyPolicyMajorVersion < currentPrivacyPolicyMajorVersion) {

                // Check if there is only a new version of the terms of service
                if (acceptedTermsMajorVersion < currentTermsMajorVersion
                        && acceptedPrivacyPolicyMajorVersion >= currentPrivacyPolicyMajorVersion) {

                    // Show the dialog indicating a new version of the terms of service
                    showAcceptTermsDialog(AcceptTermsDialog.ShowAcceptTermsDialogReason.NEW_VERSION_TERMS);

                    // Check if there is only a new version of the privacy policy
                } else if (acceptedTermsMajorVersion >= currentPrivacyPolicyMajorVersion
                        && acceptedPrivacyPolicyMajorVersion < currentPrivacyPolicyMajorVersion) {

                    // Show the dialog indicating a new version of the privacy policy
                    showAcceptTermsDialog(AcceptTermsDialog.ShowAcceptTermsDialogReason.NEW_VERSION_PRIVACYPOLICY);

                } else { // There is a new version of both the terms of service and privacy policy

                    // Show the dialog indicating a new version of both the terms of service and the privacy policy
                    showAcceptTermsDialog(AcceptTermsDialog.ShowAcceptTermsDialogReason.NEW_VERSION_BOTH);

                }


            }

        } else { // Load failed

            // Check if the user has never accepted the terms
            if (acceptedTermsMajorVersion == 0
                    || acceptedPrivacyPolicyMajorVersion == 0) {

                // Save a value of -1, so that this won't show again the next time there is not
                // an internet connection, but so that the saved value is always less than the
                // current value
                currentTermsMajorVersion = -1;
                currentPrivacyPolicyMajorVersion = -1;
                currentTermsVersion = "-1.00";
                currentPrivacyPolicyVersion = "-1.00";

                // Show the dialog for first time users indicating no internet connection
                showAcceptTermsDialog(AcceptTermsDialog.ShowAcceptTermsDialogReason.NO_INTERNET);

            }

        }

    }

    private void showAcceptTermsDialog(AcceptTermsDialog.ShowAcceptTermsDialogReason showReason) {

        // Get the support fragment manager
        FragmentManager fragmentManager = getFragmentManager();

        // Create and show the dialog
        termsDialog = AcceptTermsDialog.newInstance(showReason);
        termsDialog.show(fragmentManager, "com.delsquared.lightningdots.acceptTermsDialog");

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preferences_file_name), MODE_PRIVATE);
        synchronized (LightningDotsApplication.lockSharedPreferences) {
            sharedPref.edit()
                    .putString(
                            getString(R.string.pref_legal_acceptedtermsversion)
                            , currentTermsVersion)
                    .putString(
                            getString(R.string.pref_legal_acceptedprivacyversion)
                            , currentPrivacyPolicyVersion)
                    .commit();
        }

        // Log the data changed
        LightningDotsApplication.logDebugMessage("Calling dataChanged() in ActivityMain.onDialogPositiveClick()...");
        LightningDotsApplication.dataChanged(this);

    }

    @Override
    public void OnFragmentAttached() {

        // Get the dialog
        Dialog td = (Dialog) termsDialog.getDialog();

        // Make it so that touching outside the dialog does not close it
        td.setCanceledOnTouchOutside(false);

        // Make it so the back button does not close the dialog
        td.setCancelable(false);

    }


}
