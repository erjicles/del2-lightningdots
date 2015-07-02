package com.delsquared.lightningdots.game;

public class ClickTargetDefinition {
    public final String name;
    public final ClickTargetProfileScript clickTargetProfileScript;

    public ClickTargetDefinition() {
        name = "";
        clickTargetProfileScript = new ClickTargetProfileScript();
    }

    public ClickTargetDefinition(
            String name
            , ClickTargetProfileScript clickTargetProfileScript) {
        this.name = name;
        this.clickTargetProfileScript = clickTargetProfileScript;
    }
}
