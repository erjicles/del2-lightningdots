package com.delsquared.lightningdots.game;

import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

public class GameResult {

	private final int _id;
	private final int game_type;
	private final int game_level;
	private final int game_time;
    public final boolean game_success;
    public final int award_level;
	private final int user_clicks;
	private final Date game_timestamp;

	public GameResult(
			int _id
			, int game_type
			, int game_level
			, int game_time
            , boolean game_success
            , int award_level
            , int user_clicks
			, Date game_timestamp) {
		this._id = _id;
		this.game_type = game_type;
		this.game_level = game_level;
		this.game_time = game_time;
        this.game_success = game_success;
        this.award_level = award_level;
        this.user_clicks = user_clicks;
		this.game_timestamp = game_timestamp;
	}

	public int getGameType() { return game_type; }
	public int getGameLevel() { return game_level; }
	public int getGameTime() { return game_time; }
	public boolean getGameSuccess() { return game_success; }
    public int getAwardLevel() { return award_level; }
	public int getUserClicks() { return user_clicks; }
	public Date getGameTimestamp() { return game_timestamp; }

    /*
    public void setGameType(int game_type) { this.game_type = game_type; }
	public void setGameLevel(int game_level) { this.game_level = game_level; }
	public void setGameTime(int game_time) { this.game_time = game_time; }
	public void setGameSuccess(boolean game_success) { this.game_success = game_success; }
	public void setUserClicks(int user_clicks) { this.user_clicks = user_clicks; }
	public void setGameTimestamp(Date game_timestamp) { this.game_timestamp = game_timestamp; }
    */

	// Table and column names
	public static final String TABLENAME_GAMERESULTS = "gameresults";
	public static final String TABLE_GAMERESULTS_COLUMNNAME_ID = "_id";
	public static final String TABLE_GAMERESULTS_COLUMNNAME_GAMETYPE = "game_type";
	public static final String TABLE_GAMERESULTS_COLUMNNAME_GAMELEVEL = "game_level";
	public static final String TABLE_GAMERESULTS_COLUMNNAME_GAMETIME = "game_time";
	public static final String TABLE_GAMERESULTS_COLUMNNAME_GAMESUCCESS = "game_success";
    public static final String TABLE_GAMERESULTS_COLUMNNAME_AWARDLEVEL = "award_level";
	public static final String TABLE_GAMERESULTS_COLUMNNAME_USERCLICKS = "user_clicks";
	public static final String TABLE_GAMERESULTS_COLUMNNAME_GAMETIMESTAMP = "game_timestamp";

	// Database creation SQL statement
	private static final String DATABASE_CREATE_TABLE_GAMERESULTS =
			"CREATE TABLE \""
			+ TABLENAME_GAMERESULTS
			+ "\" (\""
			+ TABLE_GAMERESULTS_COLUMNNAME_ID
			+ "\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , \""
			+ TABLE_GAMERESULTS_COLUMNNAME_GAMETYPE
			+ "\" INTEGER NOT NULL , \""
			+ TABLE_GAMERESULTS_COLUMNNAME_GAMELEVEL
			+ "\" INTEGER NOT NULL , \""
			+ TABLE_GAMERESULTS_COLUMNNAME_GAMETIME
			+ "\" INTEGER NOT NULL , \""
			+ TABLE_GAMERESULTS_COLUMNNAME_GAMESUCCESS
			+ "\" BOOL NOT NULL , \""
            + TABLE_GAMERESULTS_COLUMNNAME_AWARDLEVEL
            + "\" INTEGER NOT NULL ,  \""
			+ TABLE_GAMERESULTS_COLUMNNAME_USERCLICKS
			+ "\" INTEGER NOT NULL ,  \""
			+ TABLE_GAMERESULTS_COLUMNNAME_GAMETIMESTAMP
			+ "\" DATETIME NOT NULL  DEFAULT CURRENT_TIMESTAMP);";

	// Index creation statements
	private static final String DATABASE_CREATE_INDEX_NONUNIQUE_gameresults_gametimestamp =
			"CREATE INDEX \"NONUNIQUE_gameresults_gametimestamp\" ON \""
			+ TABLENAME_GAMERESULTS
			+ "\" (\""
			+ TABLE_GAMERESULTS_COLUMNNAME_GAMETIMESTAMP
			+ "\" DESC);";

	private static final String DATABASE_CREATE_INDEX_NONUNIQUE_gameresults_gametype_gamelevel_gametime_userclicks =
			"CREATE INDEX \"NONUNIQUE_gameresults_gametype_gamelevel_gamesuccess_gametime_userclicks\" ON \""
			+ TABLENAME_GAMERESULTS
			+ "\" (\""
			+ TABLE_GAMERESULTS_COLUMNNAME_GAMETYPE
			+ "\" ASC, \""
			+ TABLE_GAMERESULTS_COLUMNNAME_GAMELEVEL
			+ "\" DESC, \""
			+ TABLE_GAMERESULTS_COLUMNNAME_GAMETIME
			+ "\" DESC, \""
			+ TABLE_GAMERESULTS_COLUMNNAME_GAMESUCCESS
			+ "\" DESC, \""
			+ TABLE_GAMERESULTS_COLUMNNAME_USERCLICKS
			+ "\" DESC);";

	private static final String DATABASE_CREATE_INDEX_NONUNIQUE_gameresults_gametype_gametimestamp =
			"CREATE INDEX \"NONUNIQUE_gameresults_gametype_gametimestamp\" ON \""
			+ TABLENAME_GAMERESULTS
			+ "\" (\""
			+ TABLE_GAMERESULTS_COLUMNNAME_GAMETYPE
			+ "\" ASC, \""
			+ TABLE_GAMERESULTS_COLUMNNAME_GAMETIMESTAMP
			+ "\" DESC);";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_TABLE_GAMERESULTS);
		database.execSQL(DATABASE_CREATE_INDEX_NONUNIQUE_gameresults_gametimestamp);
		database.execSQL(DATABASE_CREATE_INDEX_NONUNIQUE_gameresults_gametype_gamelevel_gametime_userclicks);
		database.execSQL(DATABASE_CREATE_INDEX_NONUNIQUE_gameresults_gametype_gametimestamp);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
								 int newVersion) {
		database.execSQL("DROP TABLE IF EXISTS " + DATABASE_CREATE_TABLE_GAMERESULTS);
		onCreate(database);
	}


}
