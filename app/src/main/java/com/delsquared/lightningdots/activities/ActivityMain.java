package com.delsquared.lightningdots.activities;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.fragments.AcceptTermsDialog;
import com.delsquared.lightningdots.fragments.FragmentMain;
import com.delsquared.lightningdots.game.Game;
import com.delsquared.lightningdots.utilities.LightningDotsApplication;
import com.delsquared.lightningdots.utilities.UtilityFunctions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActivityMain extends FragmentActivity implements
        AcceptTermsDialog.AcceptTermsDialogListener {
    private static final String CLASS_NAME = ActivityMain.class.getSimpleName();

	public static final String EXTRA_GAME_TYPE = "com.delsquared.lightningdots.gametype";

    AcceptTermsDialog termsDialog;
    protected String currentTermsVersion = "";
    protected String currentPrivacyPolicyVersion = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String methodName = CLASS_NAME + ".onCreate";
        UtilityFunctions.logDebug(methodName, "Entered");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new FragmentMain())
                    .commit();
        }

        // Set global ad request configuration
        // Add specific devices to test device list
        List<String> testDeviceIds = new ArrayList<>();
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

        // Get the billing helper instance
        LightningDotsApplication application = (LightningDotsApplication)getApplication();
        application.getBillingHelperInstanceAndStartConnection();

        // Register screen view
        UtilityFunctions.registerScreenView(this, getString(R.string.ga_screenname_activitymain));

	}

	@Override
    public void onResume() {
        String methodName = CLASS_NAME + ".onResume";
        UtilityFunctions.logDebug(methodName, "Entered");
        super.onResume();
        // Get the billing helper instance
        LightningDotsApplication application = (LightningDotsApplication)getApplication();
        application.getBillingHelperInstanceAndStartConnection();
    }

    @Override
    public void onDestroy() {
        String methodName = CLASS_NAME + ".onDestroy";
        UtilityFunctions.logDebug(methodName, "Entered");
        super.onDestroy();
    }

    public void clicked_timeAttack(@SuppressWarnings("unused") View view) {
        String methodName = CLASS_NAME + ".clicked_timeAttack";
        UtilityFunctions.logDebug(methodName, "Entered");
        launchGame(Game.GameType.TIME_ATTACK.ordinal());
    }

    public void clicked_ladder(@SuppressWarnings("unused") View view) {
        String methodName = CLASS_NAME + ".clicked_ladder";
        UtilityFunctions.logDebug(methodName, "Entered");
        launchGame(Game.GameType.AGILITY.ordinal());
    }

    public void clicked_settings(@SuppressWarnings("unused") View view) {
        String methodName = CLASS_NAME + ".clicked_settings";
        UtilityFunctions.logDebug(methodName, "Entered");
        launchSettings();
    }

    public void clicked_about(@SuppressWarnings("unused") View view) {
        String methodName = CLASS_NAME + ".clicked_about";
        UtilityFunctions.logDebug(methodName, "Entered");
        launchAbout();
    }

    public void clicked_store(@SuppressWarnings("unused") View view) {
        String methodName = CLASS_NAME + ".clicked_store";
        UtilityFunctions.logDebug(methodName, "Entered");
        launchStore();
    }

    private void launchGame(int gameType) {
        String methodName = CLASS_NAME + ".launchGame";
        UtilityFunctions.logDebug(methodName, "Entered");

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
        String methodName = CLASS_NAME + ".launchSettings";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Start the settings activity
        Intent settingsIntent = new Intent(this, ActivitySettings.class);
        startActivity(settingsIntent);

    }

    public void launchAbout() {
        String methodName = CLASS_NAME + ".launchAbout";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Start the about activity
        Intent aboutActivityIntent = new Intent(this, ActivityAbout.class);
        startActivity(aboutActivityIntent);

    }

    public void launchStore() {
        String methodName = CLASS_NAME + ".launchStore";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Start the store activity
        Intent intentStoreActivity = new Intent(this, ActivityStore.class);
        startActivity(intentStoreActivity);

    }

    private void toggleAcceptTermsDialog() {
        String methodName = CLASS_NAME + ".toggleAcceptTermsDialog";
        UtilityFunctions.logDebug(methodName, "Entered");

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
        int acceptedTermsMajorVersion;
        int acceptedPrivacyPolicyMajorVersion = 0;

        try
        {

            // Get the currently accepted major versions
            acceptedTermsMajorVersion = Integer.parseInt(acceptedTermsOfServiceVersion.split("\\.")[0]);
            acceptedPrivacyPolicyMajorVersion = Integer.parseInt(acceptedPrivacyPolicyVersion.split("\\.")[0]);

        } catch (Exception e) {

            // Reset the currently accepted major versions
            acceptedTermsMajorVersion = 0;

        }

        // Initialize the current version strings
        currentTermsVersion = "";
        currentPrivacyPolicyVersion = "";
        int currentTermsMajorVersion;
        int currentPrivacyPolicyMajorVersion = 0;

        // Get the data from the intent
        Intent intent = getIntent();
        String jsonVersionsString = intent.getStringExtra(ActivitySplash.JSON_VERSIONS_STRING_KEY);
        boolean versionsLoadSuccessful = intent.getBooleanExtra(ActivitySplash.VERSIONS_LOAD_SUCCESSFUL_KEY, false);

        if (versionsLoadSuccessful) {

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
                } else if (acceptedTermsMajorVersion >= currentPrivacyPolicyMajorVersion) {

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
                currentTermsVersion = "-1.00";
                currentPrivacyPolicyVersion = "-1.00";

                // Show the dialog for first time users indicating no internet connection
                showAcceptTermsDialog(AcceptTermsDialog.ShowAcceptTermsDialogReason.NO_INTERNET);

            }

        }

    }

    private void showAcceptTermsDialog(AcceptTermsDialog.ShowAcceptTermsDialogReason showReason) {
        String methodName = CLASS_NAME + ".showAcceptTermsDialog";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Get the support fragment manager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Create and show the dialog
        termsDialog = AcceptTermsDialog.newInstance(showReason);
        termsDialog.show(fragmentManager, "com.delsquared.lightningdots.acceptTermsDialog");

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        String methodName = CLASS_NAME + ".onDialogPositiveClick";
        UtilityFunctions.logDebug(methodName, "Entered");

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preferences_file_name), MODE_PRIVATE);
        synchronized (LightningDotsApplication.lockSharedPreferences) {
            sharedPref.edit()
                    .putString(
                            getString(R.string.pref_legal_acceptedtermsversion)
                            , currentTermsVersion)
                    .putString(
                            getString(R.string.pref_legal_acceptedprivacyversion)
                            , currentPrivacyPolicyVersion)
                    .apply();
        }

        // Log the data changed
        UtilityFunctions.logDebug(methodName, "...calling dataChanged()");
        LightningDotsApplication.dataChanged(this);

    }

    @Override
    public void OnFragmentAttached() {
        String methodName = CLASS_NAME + ".OnFragmentAttached";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Get the dialog
        Dialog td = termsDialog.getDialog();
        if (td == null) {
            UtilityFunctions.logWarning(methodName, "...terms dialog is null");
            return;
        }

        // Make it so that touching outside the dialog does not close it
        td.setCanceledOnTouchOutside(false);

        // Make it so the back button does not close the dialog
        td.setCancelable(false);

    }

}
