package com.delsquared.lightningdots.game;

import java.util.ArrayList;

public class GameLevelDefinition {

    public final long gameTimeLimitMillis;

    public final ArrayList<Integer> arrayListTargetUserClicks;

    public final LevelDefinitionLadder levelDefinitionLadder;

    public GameLevelDefinition() {
        gameTimeLimitMillis = 15000;
        arrayListTargetUserClicks = new ArrayList<>();
        levelDefinitionLadder = new LevelDefinitionLadder();
    }

    public GameLevelDefinition(
            long gameTimeLimitMillis
            , ArrayList<Integer> arrayListTargetUserClicks
            , LevelDefinitionLadder levelDefinitionLadder

    ) {
        this.gameTimeLimitMillis = gameTimeLimitMillis;
        this.arrayListTargetUserClicks = arrayListTargetUserClicks;
        this.levelDefinitionLadder = levelDefinitionLadder;

    }

}
