package com.delsquared.lightningdots.game;

public interface InterfaceGameCallback {
    public void onLevelCompleted(int nextLevel);
    public void onLevelFailed();
    public void onGameEnded();
    public void onLevelDecrementSelected();
    public void onLevelIncrementSelected();
    public int onGetCurrentLevel();
    public int onGetCurrentGameType();
    public int onGetHighestScriptedLevel();
    public GameResult onGetGameResultHighScoreOverall();
    public GameResult onGetGameResultHighScoreCurrentLevel();
}