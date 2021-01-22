package com.delsquared.lightningdots.utilities;

import com.delsquared.lightningdots.ntuple.NTuple;
import com.delsquared.lightningdots.ntuple.NTupleType;

public class SyncVariableTrigger {

    public final String sourceObjectName;
    public final String sourceObjectProfileName;
    public final String targetObjectName;
    public final String targetObjectProfileName;
    public final String variableName;
    public final MODE mode;
    private double value;

    public SyncVariableTrigger(
            String sourceObjectName
            , String sourceObjectProfileName
            , String targetObjectName
            , String targetObjectProfileName
            , String variableName
            , MODE mode
            , double value) {
        this.sourceObjectName = sourceObjectName;
        this.sourceObjectProfileName = sourceObjectProfileName;
        this.targetObjectName = targetObjectName;
        this.targetObjectProfileName = targetObjectProfileName;
        this.variableName = variableName;
        this.mode = mode;
        this.value = value;
    }

    public double getValue() { return value; }
    @SuppressWarnings("unused")
    public void setValue(double value) { this.value = value; }

    @SuppressWarnings("unused")
    public NTuple getKey() {
        return nTupleTypeSyncVariableTriggerKey.createNTuple(
                targetObjectName
                , targetObjectProfileName
                , value
        );
    }

    public enum MODE {
        SNAP_TO_TARGET
        , RANDOMIZE
        , LITERAL_VALUE
    }

    public static final NTupleType nTupleTypeSyncVariableTriggerKey =
            NTupleType.DefaultFactory.create(
                    String.class
                    , String.class
                    , String.class
            );

}
