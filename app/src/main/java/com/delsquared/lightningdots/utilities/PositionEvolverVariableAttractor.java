package com.delsquared.lightningdots.utilities;

import com.delsquared.lightningdots.ntuple.NTuple;
import com.delsquared.lightningdots.ntuple.NTupleType;

import java.util.List;
import java.util.Map;

public class PositionEvolverVariableAttractor {

    public final TYPE type;

    public final String sourceObjectName;
    public final String sourceObjectProfileName;
    public final String targetObjectName;
    public final String targetObjectProfileName;
    public final String positionEvolverFamilyName;
    public final String positionEvolverName;
    OrderedObjectCollection<PositionEvolverVariableAttractorVariable> collectionAttractorVariables;

    public final MODE mode;
    public final double mass;
    public final boolean isRepeller;

    public PositionEvolverVariableAttractor(
            TYPE type
            , String sourceObjectName
            , String sourceObjectProfileName
            , String targetObjectName
            , String targetObjectProfileName
            , String positionEvolverFamilyName
            , String positionEvolverName
            , OrderedObjectCollection<PositionEvolverVariableAttractorVariable> collectionAttractorVariables
            , MODE mode
            , double mass
            , boolean isRepeller) {
        this.type = type;
        this.sourceObjectName = sourceObjectName;
        this.sourceObjectProfileName = sourceObjectProfileName;
        this.targetObjectName = targetObjectName;
        this.targetObjectProfileName = targetObjectProfileName;
        this.positionEvolverFamilyName = positionEvolverFamilyName;
        this.positionEvolverName = positionEvolverName;
        this.collectionAttractorVariables = collectionAttractorVariables;
        this.mode = mode;
        this.mass = mass;
        this.isRepeller = isRepeller;
    }

    public NTuple getSourceKey() {
        return nTupleTypePositionEvolverVariableAttractorKey.createNTuple(
                sourceObjectName
                , sourceObjectProfileName
                , positionEvolverFamilyName
                , positionEvolverName
        );
    }
    public NTuple getTargetKey() {
        return nTupleTypePositionEvolverVariableAttractorKey.createNTuple(
                targetObjectName
                , targetObjectProfileName
                , positionEvolverFamilyName
                , positionEvolverName
        );
    }

    public enum TYPE {
        FIXED_VALUE
        , OTHER_OBJECT
    }

    public enum MODE {
        SNAP_TO_VECTOR
        , MAX_TO_VECTOR
        , GRAVITY_FIELD
        , GRAVITY
    }

    public static final NTupleType nTupleTypePositionEvolverVariableAttractorKey =
            NTupleType.DefaultFactory.create(
                    String.class        // Source object name
                    , String.class      // Source object profile name
                    , String.class      // Position evolver family name
                    , String.class      // Position evolver name
            );
}
