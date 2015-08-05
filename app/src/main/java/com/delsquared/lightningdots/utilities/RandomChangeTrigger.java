package com.delsquared.lightningdots.utilities;

import java.util.List;

public class RandomChangeTrigger {

    public final String sourceObjectName;
    public final String sourceObjectProfileName;
    public final String sourceVariableName;
    public final String targetObjectName;
    public final String targetObjectProfileName;
    public final String targetVariableName;
    public final List<SyncVariableTrigger> listSyncVariableTriggers;

    public RandomChangeTrigger(
            String sourceObjectName
            , String sourceObjectProfileName
            , String sourceVariableName
            , String targetObjectName
            , String targetObjectProfileName
            , String targetVariableName
            , List<SyncVariableTrigger> listSyncVariableTriggers) {
        this.sourceObjectName = sourceObjectName;
        this.sourceObjectProfileName = sourceObjectProfileName;
        this.sourceVariableName = sourceVariableName;
        this.targetObjectName = targetObjectName;
        this.targetObjectProfileName = targetObjectProfileName;
        this.targetVariableName = targetVariableName;
        this.listSyncVariableTriggers = listSyncVariableTriggers;
    }

    public RandomChangeEvent toRandomChangeEvent() {
        return new RandomChangeEvent(
                targetObjectName
                , targetObjectProfileName
                , targetVariableName
        );
    }

}
