package com.delsquared.lightningdots.database;

import android.app.backup.BackupManager;
import android.content.Context;
import android.util.Log;

import com.delsquared.lightningdots.backup.LightningDotsBackupAgentHelper;
import com.delsquared.lightningdots.game.GameResult;
import com.delsquared.lightningdots.utilities.LightningDotsApplication;

public class SaveHelperGameResult {

	public SQLHandler sqlHandler;
	Context context;

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

		// Initialize the sql handler
		sqlHandler = new SQLHandler(context);

		// Set the context
		this.context = context;

	}

	public void saveGameResult(GameResult gameResult) {

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
