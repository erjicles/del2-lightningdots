package com.delsquared.lightningdots.game;

import android.database.sqlite.SQLiteDatabase;

public class GameType {

	// Table and column names
	public static final String TABLENAME_GAMETYPE = "gametypes";
	public static final String TABLE_GAMETYPE_COLUMNNAME_ID = "_id";
	public static final String TABLE_GAMETYPE_COLUMNNAME_GAMETYPE = "game_type";
	public static final String TABLE_GAMETYPE_COLUMNNAME_NAME = "name";

	// Database creation SQL statement
	private static final String DATABASE_CREATE_TABLE_GAMERETYPE =
			"CREATE TABLE \""
			+ TABLENAME_GAMETYPE
			+ "\" (\""
			+ TABLE_GAMETYPE_COLUMNNAME_ID
			+ "\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , \""
			+ TABLE_GAMETYPE_COLUMNNAME_GAMETYPE
			+ "\" INTEGER NOT NULL  UNIQUE , \""
			+ TABLE_GAMETYPE_COLUMNNAME_NAME
			+ "\" TEXT NOT NULL  UNIQUE );";

	// Create indexes
	private static final String DATABASE_CREATE_INDEX_UNIQUE_gametypes_gametype =
			"CREATE INDEX \"UNIQUE_gametypes_gametype\" ON \""
			+ TABLENAME_GAMETYPE
			+ "\" (\""
			+ TABLE_GAMETYPE_COLUMNNAME_GAMETYPE
			+ "\" ASC);";

	// Insert game types
	private static final String DATABASE_INSERT_GAMETYPE_TIMEATTACK =
			"INSERT INTO "
			+ TABLENAME_GAMETYPE
			+ " ("
			+ TABLE_GAMETYPE_COLUMNNAME_GAMETYPE
			+ ", "
			+ TABLE_GAMETYPE_COLUMNNAME_NAME
			+ ") VALUES (" + Game.GameType.TIME_ATTACK.ordinal() + ", 'Time Attack');";
	private static final String DATABASE_INSERT_GAMETYPE_ENDURANCE =
			"INSERT INTO "
			+ TABLENAME_GAMETYPE
			+ " ("
			+ TABLE_GAMETYPE_COLUMNNAME_GAMETYPE
			+ ", "
			+ TABLE_GAMETYPE_COLUMNNAME_NAME
			+ ") VALUES (" + Game.GameType.ENDURANCE.ordinal() + ", 'Endurance');";
	private static final String DATABASE_INSERT_GAMETYPE_AGILITY =
			"INSERT INTO "
			+ TABLENAME_GAMETYPE
			+ " ("
			+ TABLE_GAMETYPE_COLUMNNAME_GAMETYPE
			+ ", "
			+ TABLE_GAMETYPE_COLUMNNAME_NAME
			+ ") VALUES (" + Game.GameType.AGILITY.ordinal() + ", 'Agility');";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_TABLE_GAMERETYPE);
		database.execSQL(DATABASE_CREATE_INDEX_UNIQUE_gametypes_gametype);
		database.execSQL(DATABASE_INSERT_GAMETYPE_TIMEATTACK);
		database.execSQL(DATABASE_INSERT_GAMETYPE_ENDURANCE);
		database.execSQL(DATABASE_INSERT_GAMETYPE_AGILITY);
	}

	@SuppressWarnings({"EmptyMethod", "unused"})
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
								 int newVersion) {
		// TODO: Put upgrade code here
	}
}