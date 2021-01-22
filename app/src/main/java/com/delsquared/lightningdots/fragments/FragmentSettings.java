package com.delsquared.lightningdots.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.activities.ActivityStore;
import com.delsquared.lightningdots.database.DeleterHelperGameResult;
import com.delsquared.lightningdots.game.Game;
import com.delsquared.lightningdots.utilities.EEAConsentManager;
import com.delsquared.lightningdots.utilities.IEEAConsentListener;
import com.delsquared.lightningdots.utilities.LightningDotsApplication;

import java.util.Objects;


public class FragmentSettings extends androidx.fragment.app.Fragment implements IEEAConsentListener {

    @SuppressWarnings("unused")
    public static FragmentSettings newInstance() {

        return new FragmentSettings();
    }

    public FragmentSettings() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        // Set the trademark text color
        FragmentTrademark fragmentTrademark =
                (FragmentTrademark) getChildFragmentManager().findFragmentById(R.id.fragment_main_fragment_trademark);
        if (fragmentTrademark != null) {
            fragmentTrademark.setTextColor(getResources().getColor(R.color.white));
        }

        // ---------- BEGIN Initialize the settings ---------- //

        // Initialize the show instructions checkbox
        CheckBox checkBoxShowInstructions = rootView.findViewById(R.id.fragment_settings_checkbox_show_instructions);
        checkBoxShowInstructions.setChecked(LightningDotsApplication.settingShowInstructions);

        // ---------- END Initialize the settings ---------- //

        // ---------- BEGIN Toggle the ad consent ---------- //
        LinearLayout linearLayoutAdConsent = rootView.findViewById(R.id.fragment_settings_linearlayout_settingsmenu_change_consent);
        linearLayoutAdConsent.setVisibility(LightningDotsApplication.userIsFromEEA ? View.VISIBLE : View.GONE);
        // ---------- END Toggle the ad consent ---------- //

        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    public void clicked_button_delete_game_history() {

        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.fragment_settings_text_dialog_delete_game_history_title))
                .setMessage(getString(R.string.fragment_settings_text_dialog_delete_game_history))
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    FragmentActivity activity = getActivity();
                    if (activity == null) {
                        LightningDotsApplication.logDebugErrorMessage("activity is null");
                        return;
                    }
                    Context applicationContext = activity.getApplicationContext();
                    if (applicationContext == null) {
                        LightningDotsApplication.logDebugErrorMessage("application context is null");
                        return;
                    }

                    // Delete the game history
                    DeleterHelperGameResult deleterHelperGameResult = new DeleterHelperGameResult(applicationContext);
                    deleterHelperGameResult.deleteGameResultsByGameType(Game.GameType.TIME_ATTACK.ordinal());
                    deleterHelperGameResult.deleteGameResultsByGameType(Game.GameType.ENDURANCE.ordinal());
                    deleterHelperGameResult.deleteGameResultsByGameType(Game.GameType.AGILITY.ordinal());

                    // Display a toast to the user informing them that their game history has been deleted
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(applicationContext, getString(R.string.fragment_settings_toast_game_history_deleted), duration);
                    toast.show();

                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    // do nothing
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    public void checkboxChanged_ShowInstructions(boolean isChecked) {

        // Set the show instructions setting
        LightningDotsApplication.setShowInstructions(Objects.requireNonNull(getContext()), isChecked);

    }

    public void onClick_ChangeConsent() {
        EEAConsentManager consentManager = new EEAConsentManager(this);
        consentManager.handleAdConsent(true);
    }

    @Override
    public void onHandleConsentFinished() {
        FragmentAdsBottomBanner fragmentAdsBottomBanner =
                (FragmentAdsBottomBanner) getChildFragmentManager().findFragmentById(R.id.fragment_main_fragment_ads_bottom_banner);
        if (fragmentAdsBottomBanner == null) {
            LightningDotsApplication.logDebugErrorMessage("fragmentAdsBottomBanner is null");
            return;
        }
        fragmentAdsBottomBanner.handleToggleAds();

        // Check if the user is an EEA user who wants no ads
        if (LightningDotsApplication.userPrefersNoAds) {
            // Launch the store activity
            Intent storeIntent = new Intent(getActivity(), ActivityStore.class);
            startActivity(storeIntent);
        }
    }
}
