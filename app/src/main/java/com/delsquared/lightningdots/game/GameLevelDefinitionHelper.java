package com.delsquared.lightningdots.game;

import android.content.Context;

import com.delsquared.lightningdots.R;

import java.util.ArrayList;

public class GameLevelDefinitionHelper {

    public static GameLevelDefinition getGameLevelDefinition(Context context, Game.GameType gameType, int gameLevel) {

        // Get the game time limit
        long gameTimeLimitMillis = context.getResources().getInteger(R.integer.game_values_defaultGameTimeLimitMillis);
        switch (gameType) {
            case AGILITY:
                gameTimeLimitMillis = context.getResources().getInteger(R.integer.game_values_defaultGameTimeLimitAgilityMillis);
                break;

            case TIME_ATTACK:
                gameTimeLimitMillis = context.getResources().getInteger(R.integer.game_values_defaultGameTimeLimitTimeAttackMillis);
                break;
        }

        // Get the target user clicks
        ArrayList<Integer> arrayListTargetUserClicks = new ArrayList<Integer>();
        if (gameType == Game.GameType.AGILITY) {

            int[] targetUserClicksArray = context.getResources().getIntArray(R.array.game_values_targetUserClicksAgilityPer15Seconds);

            for (int currentIndex = 0; currentIndex < targetUserClicksArray.length; currentIndex++) {
                float currentUserClicksPer15Seconds = targetUserClicksArray[currentIndex];
                arrayListTargetUserClicks.add(
                        (int) Math.ceil(currentUserClicksPer15Seconds * gameTimeLimitMillis / 15000.0));
            }
        } else if (gameType == Game.GameType.TIME_ATTACK) {

            int[] targetUserClicksArray = context.getResources().getIntArray(R.array.game_values_targetUserClicksTimeAttackPer15Seconds);

            for (int currentIndex = 0; currentIndex < targetUserClicksArray.length; currentIndex++) {
                float currentUserClicksPer15Seconds = targetUserClicksArray[currentIndex];
                arrayListTargetUserClicks.add(
                        (int) Math.ceil(currentUserClicksPer15Seconds * gameTimeLimitMillis / 15000.0));
            }
        }

        // Get the click target profile script
        ClickTargetProfileScript clickTargetProfileScript =
                ClickTargetProfileScriptHelper.getClickTargetProfileScript(
                        context
                        , gameType
                        , gameLevel);

        // Create the game level definition
        GameLevelDefinition gameLevelDefinition = new GameLevelDefinition(
                gameTimeLimitMillis
                , arrayListTargetUserClicks
                , clickTargetProfileScript);

        return gameLevelDefinition;

    }

	public static int getAwardLevel(Game game) {

		// Initialize the result
		int levelCompleted = 0;

		synchronized (game.lockGame) {

            /*
            // Initialize the game user clicks per milli
            double gameUserClicksPerMilli = 0.0;

            // Get the game data
            double numberOfValidUserClicks = (double) game.getListValidUserClick().size();
            double gameTimeElapsedMillis = (double) game.getGameTimeElapsedMillis();
            if (gameTimeElapsedMillis > game.getGameTimeLimitMillis()) {
                gameTimeElapsedMillis = game.getGameTimeLimitMillis();
            }

            // Calculate the game user clicks per milli
            if (gameTimeElapsedMillis != 0.0) {
                gameUserClicksPerMilli = numberOfValidUserClicks / gameTimeElapsedMillis;
            } else {
                gameUserClicksPerMilli = numberOfValidUserClicks;
            }
            */

            // Get the number of valid user clicks
            int numberOfValidUserClicks = game.getListValidUserClick().size();

            // Get the target user clicks array list
            ArrayList<Integer> arrayListTargetUserClicks = game.getArrayListTargetUserClicks();

			switch (game.getGameType()) {

				case TIME_ATTACK:

                    // Determine the highest award achieved, 0 being not completing the level at all
                    for (levelCompleted = 0; levelCompleted < arrayListTargetUserClicks.size(); levelCompleted++) {
                        if (numberOfValidUserClicks < arrayListTargetUserClicks.get(levelCompleted)) {
                            break;
                        }
                    }

					break;

				case ENDURANCE:
					break;

				case AGILITY:

                    // Determine the highest award achieved, 0 being not completing the level at all
                    for (levelCompleted = 0; levelCompleted < arrayListTargetUserClicks.size(); levelCompleted++) {
                        if (numberOfValidUserClicks < arrayListTargetUserClicks.get(levelCompleted)) {
                            break;
                        }
                    }

					break;

				default:
					break;

			}

		}

		return levelCompleted;
	}

}
