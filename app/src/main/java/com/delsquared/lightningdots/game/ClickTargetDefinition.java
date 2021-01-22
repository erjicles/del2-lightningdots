package com.delsquared.lightningdots.game;

public class ClickTargetDefinition {
    @SuppressWarnings("unused")
    public final String name;
    public final ClickTargetProfileScript clickTargetProfileScript;

    public ClickTargetDefinition(
            String name
            , ClickTargetProfileScript clickTargetProfileScript) {
        this.name = name;
        this.clickTargetProfileScript = clickTargetProfileScript;
    }
}
