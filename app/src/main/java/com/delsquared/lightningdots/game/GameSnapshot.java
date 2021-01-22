package com.delsquared.lightningdots.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameSnapshot {

	private final Game.GameType gameType;
	private final int gameLevel;
    private final Game.GameState gameState;
	private final int displayStartTime;
	private final long endTimeMillis;
	private final int displayTimeRemaining;
    private final ArrayList<Integer> arrayListTargetUserClicks;
    private final boolean isLevelComplete;
    private final int awardLevel;
    private final boolean isNewHighScore;
    private final Map<String, ClickTargetSnapshot> mapClickTargetSnapshots;

	public GameSnapshot() {
		gameType = Game.GameType.AGILITY;
		gameLevel = 1;
        gameState = Game.GameState.STOPPED;
		displayStartTime = 0;
		endTimeMillis = 0;
		displayTimeRemaining = 0;
        arrayListTargetUserClicks = new ArrayList<>();
        isLevelComplete = false;
        awardLevel = 0;
        isNewHighScore = false;
		mapClickTargetSnapshots = new HashMap<>();
	}

	public GameSnapshot(
			Game.GameType gameType
			, int gameLevel
            , Game.GameState gameState
			, int displayStartTime
			, long endTimeMillis
			, int displayTimeRemaining
            , ArrayList<Integer> arrayListTargetUserClicks
            , boolean isLevelComplete
            , int awardLevel
            , boolean isNewHighScore
			, Map<String, ClickTargetSnapshot> mapClickTargetSnapshots
	) {
		this.gameType = gameType;
		this.gameLevel = gameLevel;
        this.gameState = gameState;
		this.displayStartTime = displayStartTime;
		this.endTimeMillis = endTimeMillis;
		this.displayTimeRemaining = displayTimeRemaining;
        this.arrayListTargetUserClicks = arrayListTargetUserClicks;
        this.isLevelComplete = isLevelComplete;
        this.awardLevel = awardLevel;
        this.isNewHighScore = isNewHighScore;
		this.mapClickTargetSnapshots = mapClickTargetSnapshots;
	}

	public Game.GameType getGameType() { return gameType; }
	public int getGameLevel() { return gameLevel; }
    public Game.GameState getGameState() { return gameState; }
	public int getDisplayStartTime() { return displayStartTime; }
	public long getEndTimeMillis() { return endTimeMillis; }
	public int getDisplayTimeRemaining() { return displayTimeRemaining; }
    public ArrayList<Integer> getArrayListTargetUserClicks() { return arrayListTargetUserClicks; }
    public boolean getIsLevelComplete() { return isLevelComplete; }
    public int getAwardLevel() { return awardLevel; }
    public boolean getIsNewHighScore() { return isNewHighScore; }
    public Map<String, ClickTargetSnapshot> getMapClickTargetSnapshots() { return mapClickTargetSnapshots; }

    public boolean equals(GameSnapshot otherGameSnapshot) {

        if (otherGameSnapshot == null) return false;
        if (this.gameType != otherGameSnapshot.getGameType()) return false;
        if (this.gameLevel != otherGameSnapshot.getGameLevel()) return false;
        if (this.gameState != otherGameSnapshot.getGameState()) return false;
        if (this.displayStartTime != otherGameSnapshot.getDisplayStartTime()) return false;
        if (this.endTimeMillis != otherGameSnapshot.getEndTimeMillis()) return false;
        if (this.displayTimeRemaining != otherGameSnapshot.getDisplayTimeRemaining()) return false;
        if (!this.arrayListTargetUserClicks.equals(otherGameSnapshot.getArrayListTargetUserClicks())) return false;
        if (this.isLevelComplete != otherGameSnapshot.getIsLevelComplete()) return false;
        if (this.awardLevel != otherGameSnapshot.getAwardLevel()) return false;
        if (this.isNewHighScore != otherGameSnapshot.getIsNewHighScore()) return false;
        return this.mapClickTargetSnapshots.equals(otherGameSnapshot.getMapClickTargetSnapshots());

    }
}
