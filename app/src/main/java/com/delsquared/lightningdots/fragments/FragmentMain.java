package com.delsquared.lightningdots.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.database.LoaderHelperGameResult;
import com.delsquared.lightningdots.game.LevelDefinitionLadderHelper;
import com.delsquared.lightningdots.game.Game;
import com.delsquared.lightningdots.game.GameResult;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

public class FragmentMain extends androidx.fragment.app.Fragment {
    private static final String CLASS_NAME = FragmentMain.class.getSimpleName();

    public FragmentMain() {
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        String methodName = CLASS_NAME + ".onCreateView";
        UtilityFunctions.logDebug(methodName, "Entered");

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        FragmentActivity activity = getActivity();
        if (activity == null) {
            UtilityFunctions.logError(methodName, "activity is null", null);
            return rootView;
        }
        Context applicationContext = activity.getApplicationContext();
        if (applicationContext == null) {
            UtilityFunctions.logWtf(methodName, "applicationContext is null");
            return rootView;
        }

        // Set the trademark text color
        FragmentTrademark fragmentTrademark =
                (FragmentTrademark) getChildFragmentManager().findFragmentById(R.id.fragment_main_fragment_trademark);
        if (fragmentTrademark != null) {
            fragmentTrademark.setTextColor(getResources().getColor(R.color.white));
        }

        // Get the best score achieved for time attack games
        int bestScoreTimeAttack = 0;
        int bestAwardLevelTimeAttack = 0;
        LoaderHelperGameResult loaderHelperGameResult = new LoaderHelperGameResult(applicationContext);
        GameResult bestGameResultTimeAttack = loaderHelperGameResult.loadBestSuccessfulRun(
                Game.GameType.TIME_ATTACK.ordinal()
                , (int) Game.getInstance().getGameTimeLimitMillis());
        if (bestGameResultTimeAttack != null) {
            bestScoreTimeAttack = bestGameResultTimeAttack.getUserClicks();
            bestAwardLevelTimeAttack = bestGameResultTimeAttack.getAwardLevel();
        }
        String bestScoreTimeAttackString =
                String.format(
                        getString(R.string.mainactivity_gamemenu_timeattack_bestscore)
                        , bestScoreTimeAttack);
        // Check if we should show the 4 bolts message
        int[] targetUserClicksTimeAttackArray = getResources().getIntArray(R.array.game_values_targetUserClicksTimeAttackPer15Seconds);
        if (bestAwardLevelTimeAttack >= targetUserClicksTimeAttackArray.length) {
            // Show the 4 bolts message
            String textHighestAwardFormatString =
                    getResources().getQuantityString(
                            R.plurals.fragment_main_textview_timeattack_achieved_highest_award,
                            targetUserClicksTimeAttackArray.length);
            String textHighestAward = String.format(textHighestAwardFormatString, targetUserClicksTimeAttackArray.length);
            TextView textViewAchievedHighestAwardTimeAttack = rootView.findViewById(R.id.fragment_main_textview_timeattack_achieved_highest_award);
            textViewAchievedHighestAwardTimeAttack.setText(textHighestAward);
            textViewAchievedHighestAwardTimeAttack.setVisibility(View.VISIBLE);
        }

        // Set the time attack best score string
        TextView textViewTimeAttackBestScore = rootView.findViewById(R.id.fragment_main_textview_timeattack_best_score);
        textViewTimeAttackBestScore.setText(bestScoreTimeAttackString);

        // Get the highest scripted level for ladder games
        int highestScriptedLevel = LevelDefinitionLadderHelper.getHighestScriptedLevel(getActivity());
        // Get the highest level achieved for ladder games
        int currentLevelAgility = 0;
        GameResult bestGameResultAgility = loaderHelperGameResult.loadBestSuccessfulRun(
                Game.GameType.AGILITY.ordinal()
                , (int) Game.getInstance().getGameTimeLimitMillis());
        if (bestGameResultAgility != null) {
            currentLevelAgility = bestGameResultAgility.getGameLevel();
        }
        currentLevelAgility++;
        if (currentLevelAgility > highestScriptedLevel) {
            currentLevelAgility = highestScriptedLevel;

            // Show the "beat the game" textview
            TextView textViewBeatGame = rootView.findViewById(R.id.fragment_main_textview_ladder_beat_game);
            textViewBeatGame.setVisibility(View.VISIBLE);
        }
        String currentLevelAgilityString =
                String.format(
                        getString(R.string.mainactivity_gamemenu_ladder_currentlevel)
                        , currentLevelAgility);

        // Set the time attack best score string
        TextView textViewAgilityCurrentLevel = rootView.findViewById(R.id.fragment_main_textview_ladder_current_level);
        textViewAgilityCurrentLevel.setText(currentLevelAgilityString);

        return rootView;
    }

}
