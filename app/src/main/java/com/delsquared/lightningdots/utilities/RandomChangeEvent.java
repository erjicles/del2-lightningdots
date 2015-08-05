package com.delsquared.lightningdots.utilities;

import com.delsquared.lightningdots.ntuple.NTuple;
import com.delsquared.lightningdots.ntuple.NTupleType;

public class RandomChangeEvent {

    public final String objectName;
    public final String objectProfileName;
    public final String variableName;
    public RandomChangeEvent(
            String objectName
            , String objectProfileName
            , String variableName) {
        this.objectName = objectName;
        this.objectProfileName = objectProfileName;
        this.variableName = variableName;
    }

    public NTuple toRandomChangeTriggerKey() {
        return nTupleTypeRandomChangeTriggerKey.createNTuple(
                objectName
                , objectProfileName
                , variableName
        );
    }

    public static final NTupleType nTupleTypeRandomChangeTriggerKey =
            NTupleType.DefaultFactory.create(
                    String.class        // Target object name
                    , String.class      // Target object profile name
                    , String.class      // Target variable name
            );

}
