package com.delsquared.lightningdots.activities;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.CheckBox;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.fragments.FragmentSettings;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

public class ActivitySettings extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new FragmentSettings())
                    .commit();
        }

        // Register screen view
        UtilityFunctions.registerScreenView(this, getString(R.string.ga_screenname_activitysettings));
    }

    public void clicked_button_delete_game_history(View view) {
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

    public void checkChanged_ShowInstructions(View view) {

        FragmentSettings fragmentSettings = (FragmentSettings) getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragmentSettings != null) {
            CheckBox checkBoxShowInstructions = (CheckBox) view;
            boolean isChecked = checkBoxShowInstructions.isChecked();
            fragmentSettings.checkboxChanged_ShowInstructions(isChecked);
        }

        // Track the button click
        UtilityFunctions.sendEventTracker(
                this
                , getString(R.string.event_category_buttonclick)
                , getString(R.string.event_actionid_settings_googleanalytics)
                , getString(R.string.fragment_settings_checkbox_show_instructions)
                , 0);

    }

    public void onClick_ChangeConsent(View view) {

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
}
