package com.delsquared.lightningdots.game;

public interface InterfaceGameCallback {
    void onLevelCompleted(int nextLevel);
    void onLevelFailed();
    void onGameEnded();
    void onLevelDecrementSelected();
    void onLevelIncrementSelected();
    int onGetCurrentLevel();
    int onGetCurrentGameType();
    int onGetHighestScriptedLevel();
    GameResult onGetGameResultHighScoreOverall();
    GameResult onGetGameResultHighScoreCurrentLevel();
}