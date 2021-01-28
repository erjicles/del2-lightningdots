package com.delsquared.lightningdots.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.delsquared.lightningdots.utilities.UtilityFunctions;

public class SQLHandler {
	private static final String CLASS_NAME = SQLHandler.class.getSimpleName();

	SQLiteDatabase sqlDatabase;
	final GameSQLiteHelper dbHelper;

	public SQLHandler(Context context) {
		String methodName = CLASS_NAME + ".constructor";
		UtilityFunctions.logDebug(methodName, "Entered");

		dbHelper = GameSQLiteHelper.getInstance(context);
		sqlDatabase = dbHelper.getWritableDatabase();
	}

	public void beginTransaction() {
		String methodName = CLASS_NAME + ".beginTransaction";
		UtilityFunctions.logDebug(methodName, "Entered");

		try {
			synchronized (GameSQLiteHelper.sDataLock) {
				sqlDatabase.beginTransaction();
			}
		} catch (Exception e) {
			Log.e(GameSQLiteHelper.sDataLockExceptionTag, "SQLHandler.beginTransaction(): " + e.getMessage());
		}
	}

	public void setTransactionSuccessful() {
		String methodName = CLASS_NAME + ".setTransactionSuccessful";
		UtilityFunctions.logDebug(methodName, "Entered");

		try {
			synchronized (GameSQLiteHelper.sDataLock) {
				sqlDatabase.setTransactionSuccessful();
			}
		} catch (Exception e) {
			Log.e(GameSQLiteHelper.sDataLockExceptionTag, "SQLHandler.setTransactionSuccessful(): " + e.getMessage());
		}
	}

	public void endTransaction() {
		String methodName = CLASS_NAME + ".endTransaction";
		UtilityFunctions.logDebug(methodName, "Entered");

		try {
			synchronized (GameSQLiteHelper.sDataLock) {
				sqlDatabase.endTransaction();
			}
		} catch (Exception e) {
			Log.e(GameSQLiteHelper.sDataLockExceptionTag, "SQLHandler.endTransaction(): " + e.getMessage());
		}
	}

	@SuppressWarnings("unused")
	public void executeQuery(String query) {
		String methodName = CLASS_NAME + ".executeQuery(String query)";
		UtilityFunctions.logDebug(methodName, "Entered");

		try {
			synchronized (GameSQLiteHelper.sDataLock) {
				sqlDatabase = dbHelper.getWritableDatabase();
				sqlDatabase.execSQL(query);
			}
		} catch (Exception e) {
			Log.e(GameSQLiteHelper.sDataLockExceptionTag, "SQLHandler.executeQuery(String query): " + e.getMessage());
		}
	}

	public void executeQuery(String query, Object[] args) {
		String methodName = CLASS_NAME + ".executeQuery(String query, Object[] args)";
		UtilityFunctions.logDebug(methodName, "Entered");

		try {
			synchronized (GameSQLiteHelper.sDataLock) {
				sqlDatabase = dbHelper.getWritableDatabase();
				sqlDatabase.execSQL(query, args);
			}
		} catch (Exception e) {
			Log.e(GameSQLiteHelper.sDataLockExceptionTag, "SQLHandler.executeQuery(String query, Object[] args): " + e.getMessage());
		}
	}

	@SuppressWarnings("unused")
	public Cursor selectQuery(String query) {
		String methodName = CLASS_NAME + ".selectQuery(String query)";
		UtilityFunctions.logDebug(methodName, "Entered");

		Cursor c1 = null;

		try {
			synchronized (GameSQLiteHelper.sDataLock) {
				sqlDatabase = dbHelper.getWritableDatabase();
				c1 = sqlDatabase.rawQuery(query, null);
			}
		} catch (Exception e) {
			Log.e(GameSQLiteHelper.sDataLockExceptionTag, "SQLHandler.selectQuery(String query): " + e.getMessage());
		}

		return c1;
	}

	public Cursor selectQuery(String query, String[] selectionArgs) {
		String methodName = CLASS_NAME + ".selectQuery(String query, String[] selectionArgs)";
		UtilityFunctions.logDebug(methodName, "Entered");

		Cursor c1 = null;

		try {
			synchronized (GameSQLiteHelper.sDataLock) {
				sqlDatabase = dbHelper.getReadableDatabase();
				c1 = sqlDatabase.rawQuery(query, selectionArgs);
			}
		} catch (Exception e) {
			Log.e(GameSQLiteHelper.sDataLockExceptionTag, "SQLHandler.selectQuery(String query, String[] selectionArgs): " + e.getMessage());
		}

		return c1;
	}

}
