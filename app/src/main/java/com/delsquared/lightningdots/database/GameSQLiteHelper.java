package com.delsquared.lightningdots.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.game.GameResult;
import com.delsquared.lightningdots.game.GameType;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

public class GameSQLiteHelper extends SQLiteOpenHelper {
	private static final String CLASS_NAME = GameSQLiteHelper.class.getSimpleName();

	// Singleton
	private static GameSQLiteHelper databaseInstance = null;

	// Object for intrinsic lock
	public static final Object sDataLock = new Object();
	public static final String sDataLockExceptionTag = "lightningdots";

	public GameSQLiteHelper(Context context) {
		super(context
				, context.getString(R.string.database_name)
				, null
				, Integer.parseInt(context.getString(R.string.database_version)));
		String methodName = CLASS_NAME + ".constructor";
		UtilityFunctions.logDebug(methodName, "Entered");
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		String methodName = CLASS_NAME + ".onCreate";
		UtilityFunctions.logDebug(methodName, "Entered");

		GameType.onCreate(database);
		GameResult.onCreate(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String methodName = CLASS_NAME + ".onUpgrade";
		UtilityFunctions.logDebug(methodName, "Entered");

		GameType.onUpgrade(db, oldVersion, newVersion);
		GameResult.onUpgrade(db, oldVersion, newVersion);
	}

	public static GameSQLiteHelper getInstance(Context context) {
		String methodName = CLASS_NAME + ".getInstance";
		UtilityFunctions.logDebug(methodName, "Entered");

		// Use the application context, which will ensure that you
		// don't accidentally leak an Activity's context.
		// See this article for more information: http://bit.ly/6LRzfx
		if (databaseInstance == null) {
			databaseInstance = new GameSQLiteHelper(context.getApplicationContext());
		}
		return databaseInstance;
	}

}