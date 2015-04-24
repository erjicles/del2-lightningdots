package com.delsquared.lightningdots.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.game.GameResult;
import com.delsquared.lightningdots.game.GameType;

public class GameSQLiteHelper extends SQLiteOpenHelper {

	// Singleton
	private static GameSQLiteHelper databaseInstance = null;

	private static final String DATABASE_NAME = "";
	private static final int DATABASE_VERSION = 1;

	// Object for intrinsic lock
	public static final Object sDataLock = new Object();
	public static final String sDataLockExceptionTag = "lightningdots";

	public GameSQLiteHelper(Context context) {
		super(context
				, context.getString(R.string.database_name)
				, null
				, Integer.parseInt(context.getString(R.string.database_version)));
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		GameType.onCreate(database);
		GameResult.onCreate(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		GameType.onUpgrade(db, oldVersion, newVersion);
		GameResult.onUpgrade(db, oldVersion, newVersion);
	}

	public static GameSQLiteHelper getInstance(Context context) {

		// Use the application context, which will ensure that you
		// don't accidentally leak an Activity's context.
		// See this article for more information: http://bit.ly/6LRzfx
		if (databaseInstance == null) {
			databaseInstance = new GameSQLiteHelper(context.getApplicationContext());
		}
		return databaseInstance;
	}

}