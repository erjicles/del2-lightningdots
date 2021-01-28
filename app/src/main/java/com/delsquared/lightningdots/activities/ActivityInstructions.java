package com.delsquared.lightningdots.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.fragment.app.FragmentActivity;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.fragments.FragmentInstructions;
import com.delsquared.lightningdots.game.Game;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

public class ActivityInstructions extends FragmentActivity {
    private static final String CLASS_NAME = ActivityInstructions.class.getSimpleName();

    public static final String EXTRA_GAME_TYPE = "com.delsquared.lightningdots.gametype";

    private int gameType = Game.GameType.AGILITY.ordinal();

    protected void onCreate(Bundle savedInstanceState) {
        String methodName = CLASS_NAME + ".onCreate";
        UtilityFunctions.logDebug(methodName, "Entered");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        // Get the game type from the intent
        Intent intent = getIntent();
        gameType = Game.GameType.AGILITY.ordinal();
        try {
            gameType = intent.getIntExtra(ActivityMain.EXTRA_GAME_TYPE, Game.GameType.AGILITY.ordinal());
        } catch (Exception e) {
            UtilityFunctions.logError(methodName, "Exception getting gameType EXTRA from intent", e);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, FragmentInstructions.newInstance(gameType))
                    .commit();
        }

        // Register screen view
        UtilityFunctions.registerScreenView(this, getString(R.string.ga_screenname_activityinstructions));

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        String methodName = CLASS_NAME + ".onWindowFocusChanged";
        UtilityFunctions.logDebug(methodName, "Entered");
        super.onWindowFocusChanged(hasFocus);

        FragmentInstructions fragmentInstructions =
                (FragmentInstructions) getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragmentInstructions != null) {
            fragmentInstructions.startInstructionsAnimation();
        }
    }

    public void checkChanged_NeverShowThisAgain(View view) {
        String methodName = CLASS_NAME + ".checkChanged_NeverShowThisAgain";
        UtilityFunctions.logDebug(methodName, "Entered");

        FragmentInstructions fragmentInstructions =
                (FragmentInstructions) getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragmentInstructions != null) {
            CheckBox checkBoxNeverShowThisAgain = (CheckBox) view;
            boolean isChecked = checkBoxNeverShowThisAgain.isChecked();
            fragmentInstructions.checkChanged_NeverShowThisAgain(isChecked);
        }

        // Track the button click
        UtilityFunctions.sendEventTracker(
                this
                , getString(R.string.event_category_buttonclick)
                , getString(R.string.event_actionid_settings_googleanalytics)
                , getString(R.string.fragment_settings_checkbox_show_instructions)
                , 0);

    }

    public void onClick_PlayNow(@SuppressWarnings("unused") View view) {
        String methodName = CLASS_NAME + ".onClick_PlayNow";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Start the game activity
        Intent intent = new Intent(this, ActivityGame.class);
        intent.putExtra(EXTRA_GAME_TYPE, gameType);
        startActivity(intent);

    }

}
