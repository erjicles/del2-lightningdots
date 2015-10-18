package com.delsquared.lightningdots.database;

import android.app.backup.BackupManager;
import android.content.Context;
import android.util.Log;

import com.delsquared.lightningdots.game.GameResult;
import com.delsquared.lightningdots.utilities.LightningDotsApplication;

public class DeleterHelperGameResult {

    public SQLHandler sqlHandler;
    Context context;

    private static final String SQL_GAMERESULT_DELETE_BY_GAMETYPE =
            "DELETE FROM "
            + GameResult.TABLENAME_GAMERESULTS
            + " WHERE "
            + GameResult.TABLE_GAMERESULTS_COLUMNNAME_GAMETYPE + " = ?;";

    private static final String SQL_GAMERESULT_DELETE_BY_GAMETYPE_AND_LEVEL =
            "DELETE FROM "
            + GameResult.TABLENAME_GAMERESULTS
            + " WHERE "
            + GameResult.TABLE_GAMERESULTS_COLUMNNAME_GAMETYPE + " = ? "
            + " AND " + GameResult.TABLE_GAMERESULTS_COLUMNNAME_GAMELEVEL + " = ?;";

    public DeleterHelperGameResult(Context context) {

        // Initialize the sql handler
        sqlHandler = new SQLHandler(context);

        // Set the context
        this.context = context;

    }

    public void deleteGameResultsByGameType(int gameType) {

        try {

            Object[] args = {
                    gameType
            };

            sqlHandler.beginTransaction();
            sqlHandler.executeQuery(SQL_GAMERESULT_DELETE_BY_GAMETYPE, args);
            sqlHandler.setTransactionSuccessful();

            LightningDotsApplication.logDebugMessage("Calling data changed in deleteGameResultByGameType()...");

            LightningDotsApplication.dataChanged(context);

        } catch (Exception e) {

            // TODO: Error handling

        } finally {
            sqlHandler.endTransaction();
        }

    }

    public void deleteGameResultsByGameTypeAndLevel(int gameType, int gameLevel) {

        try {

            Object[] args = {
                    gameType
                    , gameLevel
            };

            sqlHandler.beginTransaction();
            sqlHandler.executeQuery(SQL_GAMERESULT_DELETE_BY_GAMETYPE_AND_LEVEL, args);
            sqlHandler.setTransactionSuccessful();

            LightningDotsApplication.logDebugMessage("Calling data changed in deleteGameResultByGameTypeAndLevel()...");

            LightningDotsApplication.dataChanged(context);

        } catch (Exception e) {

            // TODO: Error handling

        } finally {
            sqlHandler.endTransaction();
        }

    }

}
