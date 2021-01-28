package com.delsquared.lightningdots.database;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.delsquared.lightningdots.game.GameResult;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LoaderHelperGameResult {
	private static final String CLASS_NAME = LoaderHelperGameResult.class.getSimpleName();

	public final SQLHandler sqlHandler;

	private static final String SQL_GAMERESULT_LOADBESTRUN =
			"SELECT * "
			+ "FROM " + GameResult.TABLENAME_GAMERESULTS + " "
			+ "WHERE " + GameResult.TABLE_GAMERESULTS_COLUMNNAME_GAMETYPE + " = ? "
			+ "AND " + GameResult.TABLE_GAMERESULTS_COLUMNNAME_GAMETIME + " = ? "
			+ "ORDER BY " + GameResult.TABLE_GAMERESULTS_COLUMNNAME_GAMELEVEL + " DESC, "
			+ GameResult.TABLE_GAMERESULTS_COLUMNNAME_USERCLICKS + " DESC "
			+ "LIMIT 1";

    private static final String SQL_GAMERESULT_LOADBESTRUN_FORLEVEL =
            "SELECT * "
            + "FROM " + GameResult.TABLENAME_GAMERESULTS + " "
            + "WHERE " + GameResult.TABLE_GAMERESULTS_COLUMNNAME_GAMETYPE + " = ? "
            + "AND " + GameResult.TABLE_GAMERESULTS_COLUMNNAME_GAMELEVEL + " = ? "
            + "AND " + GameResult.TABLE_GAMERESULTS_COLUMNNAME_GAMETIME + " = ? "
            + "ORDER BY " + GameResult.TABLE_GAMERESULTS_COLUMNNAME_GAMELEVEL + " DESC, "
            + GameResult.TABLE_GAMERESULTS_COLUMNNAME_USERCLICKS + " DESC "
            + "LIMIT 1";

	private static final String SQL_GAMERESULT_LOADBESTSUCCESSFULRUN =
			"SELECT * "
			+ "FROM " + GameResult.TABLENAME_GAMERESULTS + " "
			+ "WHERE " + GameResult.TABLE_GAMERESULTS_COLUMNNAME_GAMETYPE + " = ? "
			+ "AND " + GameResult.TABLE_GAMERESULTS_COLUMNNAME_GAMETIME + " = ? "
			+ "AND " + GameResult.TABLE_GAMERESULTS_COLUMNNAME_GAMESUCCESS + " = 1 "
			+ "ORDER BY " + GameResult.TABLE_GAMERESULTS_COLUMNNAME_GAMELEVEL + " DESC, "
			+ GameResult.TABLE_GAMERESULTS_COLUMNNAME_USERCLICKS + " DESC "
			+ "LIMIT 1";

	public LoaderHelperGameResult(Context context) {
		String methodName = CLASS_NAME + ".constructor";
		UtilityFunctions.logDebug(methodName, "Entered");

		// Initialize the sql handler
		sqlHandler = new SQLHandler(context);

		// Set the context

	}

	@SuppressWarnings("unused")
	public GameResult loadBestRun(int gameType, int gameTime) {
		String methodName = CLASS_NAME + ".loadBestRun";
		UtilityFunctions.logDebug(methodName, "Entered");

		// Initialize the result
		GameResult result = null;

		// Initialize the cursor

		try (Cursor resultCursor = sqlHandler.selectQuery(
				SQL_GAMERESULT_LOADBESTRUN
				, new String[]{
						Integer.toString(gameType)
						, Integer.toString(gameTime)})) {

			// Run the query and get the result cursor

			// Check if we received a result
			if (resultCursor != null) {

				// Loop through the cursor
				while (resultCursor.moveToNext()) {

					// Load the current game info
					result = loadGameResult(resultCursor);

				}
			}
		} catch (Exception e) {

			// TODO: Error handling

		}

		// Close the result cursor

		return result;
	}

	public GameResult loadBestSuccessfulRun(int gameType, int gameTime) {
		String methodName = CLASS_NAME + ".loadBestSuccessfulRun";
		UtilityFunctions.logDebug(methodName, "Entered");

		// Initialize the result
		GameResult result = null;

		// Initialize the cursor

		try (Cursor resultCursor = sqlHandler.selectQuery(
				SQL_GAMERESULT_LOADBESTSUCCESSFULRUN
				, new String[]{
						Integer.toString(gameType)
						, Integer.toString(gameTime)})) {

			// Run the query and get the result cursor

			// Check if we received a result
			if (resultCursor != null) {

				// Loop through the cursor
				while (resultCursor.moveToNext()) {

					// Load the current game info
					result = loadGameResult(resultCursor);

				}
			}
		} catch (Exception e) {

			// TODO: Error handling

		}

		// Close the result cursor

		return result;
	}

    public GameResult loadBestRunForLevel(int gameType, int gameLevel, int gameTime) {
		String methodName = CLASS_NAME + ".loadBestRunForLevel";
		UtilityFunctions.logDebug(methodName, "Entered");

        // Initialize the result
        GameResult result = null;

        // Initialize the cursor

		try (Cursor resultCursor = sqlHandler.selectQuery(
				SQL_GAMERESULT_LOADBESTRUN_FORLEVEL
				, new String[]{
						Integer.toString(gameType)
						, Integer.toString(gameLevel)
						, Integer.toString(gameTime)})) {

			// Run the query and get the result cursor

			// Check if we received a result
			if (resultCursor != null) {

				// Loop through the cursor
				while (resultCursor.moveToNext()) {

					// Load the current game info
					result = loadGameResult(resultCursor);

				}
			}
		} catch (Exception e) {

			// TODO: Error handling

		}

		// Close the result cursor

		return result;

    }

	public static GameResult loadGameResult(Cursor resultCursor) {
		String methodName = CLASS_NAME + ".loadGameResult";
		UtilityFunctions.logDebug(methodName, "Entered");

		// Initialize the result
		GameResult gameResult = null;

		// Check if the cursor is at an invalid position
		if (resultCursor == null || resultCursor.isBeforeFirst() || resultCursor.isAfterLast()) {
			return null;
		}

		// Get the internal id
		int id = resultCursor.getInt(
				resultCursor.getColumnIndex(
						GameResult.TABLE_GAMERESULTS_COLUMNNAME_ID
				)
		);

		// Get the game type
		int gameType = resultCursor.getInt(
				resultCursor.getColumnIndex(
						GameResult.TABLE_GAMERESULTS_COLUMNNAME_GAMETYPE
				)
		);

		// Get the game level
		int gameLevel = resultCursor.getInt(
				resultCursor.getColumnIndex(
						GameResult.TABLE_GAMERESULTS_COLUMNNAME_GAMELEVEL
				)
		);

        // Get the game time
        int gameTime = resultCursor.getInt(
                resultCursor.getColumnIndex(
                        GameResult.TABLE_GAMERESULTS_COLUMNNAME_GAMETIME
                )
        );

		// Get the game success
		boolean gameSuccess = resultCursor.getInt(
				resultCursor.getColumnIndex(
						GameResult.TABLE_GAMERESULTS_COLUMNNAME_GAMESUCCESS
				)
		) > 0;

        int awardLevel = resultCursor.getInt(
                resultCursor.getColumnIndex(
                        GameResult.TABLE_GAMERESULTS_COLUMNNAME_AWARDLEVEL
                )
        );

		// Get the user clicks
		int userClicks = resultCursor.getInt(
				resultCursor.getColumnIndex(
						GameResult.TABLE_GAMERESULTS_COLUMNNAME_USERCLICKS
				)
		);

		// Get the game timestamp
		String gameTimestamp = resultCursor.getString(
				resultCursor.getColumnIndex(
						GameResult.TABLE_GAMERESULTS_COLUMNNAME_GAMETIMESTAMP
				)
		);
		Date gameDatetime = null;
		DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		try {
			gameDatetime = iso8601Format.parse(gameTimestamp);
		} catch (ParseException e) {
			Log.e("wee", "Parsing ISO8601 datetime failed", e);
		}

		// Check if we have a valid id
		if (id > 0) {

			// Initialize the game result with the data
			gameResult = new GameResult(
					id
					, gameType
					, gameLevel
					, gameTime
					, gameSuccess
                    , awardLevel
					, userClicks
					, gameDatetime);

		}

		return gameResult;

	}
}
