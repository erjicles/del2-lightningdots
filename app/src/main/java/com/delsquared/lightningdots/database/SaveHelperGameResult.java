package com.delsquared.lightningdots.database;

import android.content.Context;
import android.util.Log;

import com.delsquared.lightningdots.game.GameResult;
import com.delsquared.lightningdots.utilities.LightningDotsApplication;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

public class SaveHelperGameResult {
	private static final String CLASS_NAME = SaveHelperGameResult.class.getSimpleName();

	public final SQLHandler sqlHandler;
	final Context context;

	private static final String SQL_GAMERESULT_SAVE =
			"INSERT INTO "
			+ GameResult.TABLENAME_GAMERESULTS
			+ " ("
			+ GameResult.TABLE_GAMERESULTS_COLUMNNAME_GAMETYPE
			+ ", "
			+ GameResult.TABLE_GAMERESULTS_COLUMNNAME_GAMELEVEL
			+ ", "
			+ GameResult.TABLE_GAMERESULTS_COLUMNNAME_GAMETIME
			+ ", "
			+ GameResult.TABLE_GAMERESULTS_COLUMNNAME_GAMESUCCESS
			+ ", "
            + GameResult.TABLE_GAMERESULTS_COLUMNNAME_AWARDLEVEL
            + ", "
			+ GameResult.TABLE_GAMERESULTS_COLUMNNAME_USERCLICKS
			+ ") VALUES (?, ?, ?, ?, ?, ?);";

	public SaveHelperGameResult(Context context) {
		String methodName = CLASS_NAME + ".constructor";
		UtilityFunctions.logDebug(methodName, "Entered");

		// Initialize the sql handler
		sqlHandler = new SQLHandler(context);

		// Set the context
		this.context = context;

	}

	public void saveGameResult(GameResult gameResult) {
		String methodName = CLASS_NAME + ".saveGameResult";
		UtilityFunctions.logDebug(methodName, "Entered");

		try {

			Object[] args = {
					gameResult.getGameType()
					, gameResult.getGameLevel()
					, gameResult.getGameTime()
					, gameResult.getGameSuccess()
                    , gameResult.getAwardLevel()
					, gameResult.getUserClicks()
			};

			sqlHandler.beginTransaction();
			sqlHandler.executeQuery(SQL_GAMERESULT_SAVE, args);
			sqlHandler.setTransactionSuccessful();

			Log.d(LightningDotsApplication.logTag, "Calling data changed in saveGameResult()...");

			LightningDotsApplication.dataChanged(context);

		} catch (Exception e) {

			// TODO: Error handling

		} finally {
			sqlHandler.endTransaction();
		}

	}

}
