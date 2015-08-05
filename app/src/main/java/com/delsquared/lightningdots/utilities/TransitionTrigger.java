package com.delsquared.lightningdots.utilities;

import java.util.List;

public class TransitionTrigger {

    public final String sourceObjectName;
    public final String sourceObjectProfileName;
    public final String targetObjectName;
    public final String targetObjectProfileName;
    public final boolean randomTargetObject;
    public final boolean randomTargetObjectProfile;
    public final List<SyncVariableTrigger> listSyncVariableTriggers;

    public TransitionTrigger(
            String sourceObjectName
            , String sourceObjectProfileName
            , String targetObjectName
            , String targetObjectProfileName
            , boolean randomTargetObject
            , boolean randomTargetObjectProfile
            , List<SyncVariableTrigger> listSyncVariableTriggers) {
        this.sourceObjectName = sourceObjectName;
        this.sourceObjectProfileName = sourceObjectProfileName;
        this.targetObjectName = targetObjectName;
        this.targetObjectProfileName = targetObjectProfileName;
        this.randomTargetObject = randomTargetObject;
        this.randomTargetObjectProfile = randomTargetObjectProfile;
        this.listSyncVariableTriggers = listSyncVariableTriggers;
    }

    public TransitionEvent toTransitionEvent() {
        return new TransitionEvent(
                targetObjectName
                , targetObjectProfileName
        );
    }

}
