package com.delsquared.lightningdots.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.activities.ActivityMain;
import com.delsquared.lightningdots.activities.ActivitySplash;
import com.delsquared.lightningdots.activities.ActivityStore;
import com.delsquared.lightningdots.database.DeleterHelperGameResult;
import com.delsquared.lightningdots.game.Game;
import com.delsquared.lightningdots.utilities.EEAConsentManager;
import com.delsquared.lightningdots.utilities.IEEAConsentListener;
import com.delsquared.lightningdots.utilities.LightningDotsApplication;


public class FragmentSettings extends android.support.v4.app.Fragment implements IEEAConsentListener {

    public static FragmentSettings newInstance() {

        FragmentSettings fragment = new FragmentSettings();

        return fragment;
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
        CheckBox checkBoxShowInstructions = (CheckBox) rootView.findViewById(R.id.fragment_settings_checkbox_show_instructions);
        checkBoxShowInstructions.setChecked(LightningDotsApplication.settingShowInstructions);

        // ---------- END Initialize the settings ---------- //

        // ---------- BEGIN Toggle the ad consent ---------- //
        LinearLayout linearLayoutAdConsent = (LinearLayout) rootView.findViewById(R.id.fragment_settings_linearlayout_settingsmenu_change_consent);
        linearLayoutAdConsent.setVisibility(LightningDotsApplication.userIsFromEEA ? View.VISIBLE : View.GONE);
        // ---------- END Toggle the ad consent ---------- //

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            //mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    public void clicked_button_delete_game_history() {

        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.fragment_settings_text_dialog_delete_game_history_title))
                .setMessage(getString(R.string.fragment_settings_text_dialog_delete_game_history))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        // Delete the game history
                        DeleterHelperGameResult deleterHelperGameResult = new DeleterHelperGameResult(getActivity().getApplicationContext());
                        deleterHelperGameResult.deleteGameResultsByGameType(Game.GameType.TIME_ATTACK.ordinal());
                        deleterHelperGameResult.deleteGameResultsByGameType(Game.GameType.ENDURANCE.ordinal());
                        deleterHelperGameResult.deleteGameResultsByGameType(Game.GameType.AGILITY.ordinal());

                        // Display a toast to the user informing them that their game history has been deleted
                        Context context = getActivity().getApplicationContext();
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, getString(R.string.fragment_settings_toast_game_history_deleted), duration);
                        toast.show();

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    public void checkboxChanged_ShowInstructions(boolean isChecked) {

        // Set the show instructions setting
        LightningDotsApplication.setShowInstructions(getContext(), isChecked);

    }

    public void onClick_ChangeConsent() {
        EEAConsentManager consentManager = new EEAConsentManager(this);
        consentManager.handleAdConsent(true);
    }

    @Override
    public void onHandleConsentFinished() {
        FragmentAdsBottomBanner fragmentAdsBottomBanner =
                (FragmentAdsBottomBanner) getChildFragmentManager().findFragmentById(R.id.fragment_main_fragment_ads_bottom_banner);
        fragmentAdsBottomBanner.handleToggleAds();

        // Check if the user is an EEA user who wants no ads
        if (LightningDotsApplication.userPrefersNoAds) {
            // Launch the store activity
            Intent storeIntent = new Intent(getActivity(), ActivityStore.class);
            startActivity(storeIntent);
        }
    }
}
