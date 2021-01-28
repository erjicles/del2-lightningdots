package com.delsquared.lightningdots.database;

import android.content.Context;

import com.delsquared.lightningdots.game.GameResult;
import com.delsquared.lightningdots.utilities.LightningDotsApplication;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

public class DeleterHelperGameResult {
    private static final String CLASS_NAME = DeleterHelperGameResult.class.getSimpleName();

    public final SQLHandler sqlHandler;
    final Context context;

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
        String methodName = CLASS_NAME + ".constructor";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Initialize the sql handler
        sqlHandler = new SQLHandler(context);

        // Set the context
        this.context = context;

    }

    public void deleteGameResultsByGameType(int gameType) {
        String methodName = CLASS_NAME + ".deleteGameResultsByGameType";
        UtilityFunctions.logDebug(methodName, "Entered");

        try {

            Object[] args = {
                    gameType
            };

            sqlHandler.beginTransaction();
            sqlHandler.executeQuery(SQL_GAMERESULT_DELETE_BY_GAMETYPE, args);
            sqlHandler.setTransactionSuccessful();

            UtilityFunctions.logDebug(methodName, "Calling data changed in deleteGameResultByGameType()...");

            LightningDotsApplication.dataChanged(context);

        } catch (Exception e) {

            // TODO: Error handling

        } finally {
            sqlHandler.endTransaction();
        }

    }

    @SuppressWarnings("unused")
    public void deleteGameResultsByGameTypeAndLevel(int gameType, int gameLevel) {
        String methodName = CLASS_NAME + ".deleteGameResultsByGameTypeAndLevel";
        UtilityFunctions.logDebug(methodName, "Entered");

        try {

            Object[] args = {
                    gameType
                    , gameLevel
            };

            sqlHandler.beginTransaction();
            sqlHandler.executeQuery(SQL_GAMERESULT_DELETE_BY_GAMETYPE_AND_LEVEL, args);
            sqlHandler.setTransactionSuccessful();

            UtilityFunctions.logDebug(methodName, "Calling data changed in deleteGameResultByGameTypeAndLevel()...");

            LightningDotsApplication.dataChanged(context);

        } catch (Exception e) {

            // TODO: Error handling

        } finally {
            sqlHandler.endTransaction();
        }

    }

}
