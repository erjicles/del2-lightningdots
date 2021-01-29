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
import com.delsquared.lightningdots.globals.GlobalSettings;
import com.delsquared.lightningdots.utilities.EEAConsentManager;
import com.delsquared.lightningdots.utilities.IEEAConsentListener;
import com.delsquared.lightningdots.utilities.UtilityFunctions;


public class FragmentSettings extends androidx.fragment.app.Fragment implements IEEAConsentListener {
    private static final String CLASS_NAME = FragmentSettings.class.getSimpleName();

    @SuppressWarnings("unused")
    public static FragmentSettings newInstance() {

        return new FragmentSettings();
    }

    public FragmentSettings() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        String methodName = CLASS_NAME + ".onCreate";
        UtilityFunctions.logDebug(methodName, "Entered");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String methodName = CLASS_NAME + ".onCreateView";
        UtilityFunctions.logDebug(methodName, "Entered");

        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        // Set the trademark text color
        FragmentTrademark fragmentTrademark =
                (FragmentTrademark) getChildFragmentManager().findFragmentById(R.id.fragment_main_fragment_trademark);
        if (fragmentTrademark != null) {
            fragmentTrademark.setTextColor(getResources().getColor(R.color.white));
        }

        // ---------- BEGIN Initialize the settings ---------- //

        // Initialize the mute audio checkbox
        CheckBox checkBoxMuteAudio = rootView.findViewById(R.id.fragment_settings_checkbox_mute_audio);
        checkBoxMuteAudio.setChecked(GlobalSettings.getInstance().getIsAudioMuted());

        // Initialize the show instructions checkbox
        CheckBox checkBoxShowInstructions = rootView.findViewById(R.id.fragment_settings_checkbox_show_instructions);
        checkBoxShowInstructions.setChecked(GlobalSettings.getInstance().getIsShowInstructions());

        // ---------- END Initialize the settings ---------- //


        // ---------- BEGIN Toggle the ad consent ---------- //
        LinearLayout linearLayoutAdConsent = rootView.findViewById(R.id.fragment_settings_linearlayout_settingsmenu_change_consent);
        linearLayoutAdConsent.setVisibility(GlobalSettings.getInstance().getIsUserFromEEA() ? View.VISIBLE : View.GONE);
        // ---------- END Toggle the ad consent ---------- //

        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        String methodName = CLASS_NAME + ".onAttach";
        UtilityFunctions.logDebug(methodName, "Entered");
        super.onAttach(context);
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onDetach() {
        String methodName = CLASS_NAME + ".onDetach";
        UtilityFunctions.logDebug(methodName, "Entered");
        super.onDetach();
    }

    public void clicked_button_delete_game_history() {
        String methodName = CLASS_NAME + ".clicked_button_delete_game_history";
        UtilityFunctions.logDebug(methodName, "Entered");

        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.fragment_settings_text_dialog_delete_game_history_title))
                .setMessage(getString(R.string.fragment_settings_text_dialog_delete_game_history))
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    FragmentActivity activity = getActivity();
                    if (activity == null) {
                        UtilityFunctions.logError(methodName, "activity is null", null);
                        return;
                    }
                    Context applicationContext = activity.getApplicationContext();
                    if (applicationContext == null) {
                        UtilityFunctions.logWtf(methodName, "applicationContext is null");
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

    public void onClickShowInstructions(boolean isChecked) {
        String methodName = CLASS_NAME + ".onClickShowInstructions";
        UtilityFunctions.logDebug(methodName, "Entered");

        Context context = getContext();
        if (context == null) {
            UtilityFunctions.logError(methodName, "context is null");
            return;
        }

        // Set the show instructions setting
        GlobalSettings.getInstance().setIsShowInstructions(context, isChecked);

    }

    public void onClickMuteAudio(boolean isAudioMuted) {
        String methodName = CLASS_NAME + ".onClickMuteAudio";
        UtilityFunctions.logDebug(methodName, "Entered");

        Context context = getContext();
        if (context == null) {
            UtilityFunctions.logError(methodName, "context is null");
            return;
        }

        // Set the show instructions setting
        GlobalSettings.getInstance().setIsAudioMuted(context, isAudioMuted);

    }

    public void onClick_ChangeConsent() {
        String methodName = CLASS_NAME + ".onClick_ChangeConsent";
        UtilityFunctions.logDebug(methodName, "Entered");

        EEAConsentManager consentManager = new EEAConsentManager(this);
        consentManager.handleAdConsent(true);
    }

    @Override
    public void onHandleConsentFinished() {
        String methodName = CLASS_NAME + ".onHandleConsentFinished";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Check if the user is an EEA user who wants no ads
        if (GlobalSettings.getInstance().getUserPrefersNoAds()) {
            // Launch the store activity
            Intent storeIntent = new Intent(getActivity(), ActivityStore.class);
            startActivity(storeIntent);
        }
    }
}
