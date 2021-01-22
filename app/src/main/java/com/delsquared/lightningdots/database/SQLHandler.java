package com.delsquared.lightningdots.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SQLHandler {

	SQLiteDatabase sqlDatabase;
	final GameSQLiteHelper dbHelper;

	public SQLHandler(Context context) {
		dbHelper = GameSQLiteHelper.getInstance(context);
		sqlDatabase = dbHelper.getWritableDatabase();
	}

	public void beginTransaction() {
		try {
			synchronized (GameSQLiteHelper.sDataLock) {
				sqlDatabase.beginTransaction();
			}
		} catch (Exception e) {
			Log.e(GameSQLiteHelper.sDataLockExceptionTag, "SQLHandler.beginTransaction(): " + e.getMessage());
		}
	}

	public void setTransactionSuccessful() {
		try {
			synchronized (GameSQLiteHelper.sDataLock) {
				sqlDatabase.setTransactionSuccessful();
			}
		} catch (Exception e) {
			Log.e(GameSQLiteHelper.sDataLockExceptionTag, "SQLHandler.setTransactionSuccessful(): " + e.getMessage());
		}
	}

	public void endTransaction() {
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
		//try {
		//if (sqlDatabase.isOpen()) {
		//	sqlDatabase.close();
		//}

		try {
			synchronized (GameSQLiteHelper.sDataLock) {
				sqlDatabase = dbHelper.getWritableDatabase();
				sqlDatabase.execSQL(query);
			}
		} catch (Exception e) {
			Log.e(GameSQLiteHelper.sDataLockExceptionTag, "SQLHandler.executeQuery(String query): " + e.getMessage());
		}

		//System.out.println("Query executed successfully.");

		//} catch (Exception e) {
		//	System.out.println("DATABASE ERROR " + e);
		//}
	}

	public void executeQuery(String query, Object[] args) {
		//try {
		//if (sqlDatabase.isOpen()) {
		//	sqlDatabase.close();
		//}

		try {
			synchronized (GameSQLiteHelper.sDataLock) {
				sqlDatabase = dbHelper.getWritableDatabase();
				sqlDatabase.execSQL(query, args);
			}
		} catch (Exception e) {
			Log.e(GameSQLiteHelper.sDataLockExceptionTag, "SQLHandler.executeQuery(String query, Object[] args): " + e.getMessage());
		}

		//System.out.println("Query executed successfully.");

		//} catch (Exception e) {
		//	System.out.println("DATABASE ERROR " + e);
		//}
	}

	@SuppressWarnings("unused")
	public Cursor selectQuery(String query) {
		Cursor c1 = null;
		//try {
		//if (sqlDatabase.isOpen()) {
		//	sqlDatabase.close();
		//}

		try {
			synchronized (GameSQLiteHelper.sDataLock) {
				sqlDatabase = dbHelper.getWritableDatabase();
				c1 = sqlDatabase.rawQuery(query, null);
			}
		} catch (Exception e) {
			Log.e(GameSQLiteHelper.sDataLockExceptionTag, "SQLHandler.selectQuery(String query): " + e.getMessage());
		}

		//} catch (Exception e) {
		//	System.out.println("DATABASE ERROR " + e);
		//}

		//System.out.println("Select query successful.");

		return c1;
	}

	public Cursor selectQuery(String query, String[] selectionArgs) {
		Cursor c1 = null;

		//if (sqlDatabase.isOpen()) {
		//	sqlDatabase.close();
		//}

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
