package com.delsquared.lightningdots.utilities;

import com.delsquared.lightningdots.ntuple.NTuple;
import com.delsquared.lightningdots.ntuple.NTupleType;

public class TransitionEvent {

    public final String objectName;
    public final String objectProfileName;
    public TransitionEvent(
            String objectName
            , String objectProfileName) {
        this.objectName = objectName;
        this.objectProfileName = objectProfileName;
    }

    public NTuple toTransitionTriggerKey() {
        return nTupleTypeTransitionTriggerKey.createNTuple(
                objectName
                , objectProfileName
        );
    }

    public static final NTupleType nTupleTypeTransitionTriggerKey =
            NTupleType.DefaultFactory.create(
                    String.class        // Target object
                    , String.class      // Target object profile
            );

}
