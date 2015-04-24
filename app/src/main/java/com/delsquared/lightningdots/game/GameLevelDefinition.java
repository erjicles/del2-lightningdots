package com.delsquared.lightningdots.game;

import java.util.ArrayList;

public class GameLevelDefinition {

    public final long gameTimeLimitMillis;

    public final ArrayList<Integer> arrayListTargetUserClicks;

    public final ClickTargetProfileScript clickTargetProfileScript;


    public GameLevelDefinition(
            long gameTimeLimitMillis
            , ArrayList<Integer> arrayListTargetUserClicks
            , ClickTargetProfileScript clickTargetProfileScript

    ) {
        this.gameTimeLimitMillis = gameTimeLimitMillis;
        this.arrayListTargetUserClicks = arrayListTargetUserClicks;
        this.clickTargetProfileScript = clickTargetProfileScript;

    }

}
