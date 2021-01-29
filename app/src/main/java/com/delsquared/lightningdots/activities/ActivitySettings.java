package com.delsquared.lightningdots.activities;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.CheckBox;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.billing.BillingConstants;
import com.delsquared.lightningdots.billing.BillingHelper;
import com.delsquared.lightningdots.fragments.FragmentSettings;
import com.delsquared.lightningdots.globals.GlobalSettings;
import com.delsquared.lightningdots.utilities.LightningDotsApplication;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

public class ActivitySettings extends FragmentActivity {
    private static final String CLASS_NAME = ActivitySettings.class.getSimpleName();

    // The helper for billing
    BillingHelper billingHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String methodName = CLASS_NAME + ".onCreate";
        UtilityFunctions.logDebug(methodName, "Entered");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new FragmentSettings())
                    .commit();
        }

        LightningDotsApplication application = (LightningDotsApplication) getApplication();
        this.billingHelper = application.getBillingHelperInstanceAndStartConnection();

        // Register screen view
        UtilityFunctions.registerScreenView(this, getString(R.string.ga_screenname_activitysettings));
    }

    public void clicked_button_delete_game_history(@SuppressWarnings("unused") View view) {
        String methodName = CLASS_NAME + ".clicked_button_delete_game_history";
        UtilityFunctions.logDebug(methodName, "Entered");

        FragmentSettings fragmentSettings = (FragmentSettings) getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragmentSettings != null) {
            fragmentSettings.clicked_button_delete_game_history();
        }

        // Track the button click
        UtilityFunctions.sendEventTracker(
                this
                , getString(R.string.event_category_buttonclick)
                , getString(R.string.event_actionid_settings_googleanalytics)
                , getString(R.string.fragment_settings_button_delete_game_history)
                , 0);
    }

    public void onClickShowInstructions(View view) {
        String methodName = CLASS_NAME + ".onClickShowInstructions";
        UtilityFunctions.logDebug(methodName, "Entered");

        FragmentSettings fragmentSettings = (FragmentSettings) getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragmentSettings != null) {
            CheckBox checkBoxShowInstructions = (CheckBox) view;
            boolean isChecked = checkBoxShowInstructions.isChecked();
            fragmentSettings.onClickShowInstructions(isChecked);
        }

        // Track the button click
        UtilityFunctions.sendEventTracker(
                this
                , getString(R.string.event_category_buttonclick)
                , getString(R.string.event_actionid_settings_googleanalytics)
                , getString(R.string.fragment_settings_checkbox_show_instructions)
                , 0);

    }

    public void onClickMuteAudio(View view) {
        String methodName = CLASS_NAME + ".onClickMuteAudio";
        UtilityFunctions.logDebug(methodName, "Entered");

        FragmentSettings fragmentSettings = (FragmentSettings) getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragmentSettings != null) {
            CheckBox checkBoxMuteAudio = (CheckBox) view;
            boolean isAudioMuted = checkBoxMuteAudio.isChecked();
            fragmentSettings.onClickMuteAudio(isAudioMuted);
        }

        // Track the button click
        UtilityFunctions.sendEventTracker(
                this
                , getString(R.string.event_category_buttonclick)
                , getString(R.string.event_actionid_settings_googleanalytics)
                , getString(R.string.fragment_settings_checkbox_mute_audio)
                , 0);
    }

    public void onClick_ChangeConsent(@SuppressWarnings("unused") View view) {
        String methodName = CLASS_NAME + ".onClick_ChangeConsent";
        UtilityFunctions.logDebug(methodName, "Entered");

        FragmentSettings fragmentSettings = (FragmentSettings) getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragmentSettings != null) {
            fragmentSettings.onClick_ChangeConsent();
        }

        // Track the button click
        UtilityFunctions.sendEventTracker(
                this
                , getString(R.string.event_category_buttonclick)
                , getString(R.string.event_actionid_settings_googleanalytics)
                , getString(R.string.fragment_settings_button_change_consent)
                , 0);

    }

    public void onClickConsumeAdZap(@SuppressWarnings("unused") View view) {
        String methodName = CLASS_NAME + ".onClickConsumeAdZap";
        UtilityFunctions.logDebug(methodName, "Entered");

        boolean isConsumingPurchase = this.billingHelper.consumePurchaseBySku(BillingConstants.PRODUCT_SKU_REMOVE_ADS);
        if (isConsumingPurchase) {
            UtilityFunctions.logInfo(methodName, "Consuming AdZap, setting hasPurchasedNoAds global flag to false");
            GlobalSettings.getInstance().setHasPurchasedNoAds(false);
        }

        // Track the button click
        UtilityFunctions.sendEventTracker(
                this
                , getString(R.string.event_category_buttonclick)
                , getString(R.string.event_actionid_settings_googleanalytics)
                , getString(R.string.fragment_settings_actionsmenu_button_consume_adzap_text)
                , 0);
    }
}
