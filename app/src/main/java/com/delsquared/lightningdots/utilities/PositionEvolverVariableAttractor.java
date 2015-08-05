package com.delsquared.lightningdots.utilities;

import com.delsquared.lightningdots.ntuple.NTuple;
import com.delsquared.lightningdots.ntuple.NTupleType;

public class PositionEvolverVariableAttractor {

    public final TYPE type;

    public final String sourceObjectName;
    public final String sourceObjectProfileName;
    public final String sourcePositionEvolverFamilyName;
    public final String sourcePositionEvolverName;
    public final String targetObjectName;
    public final String targetObjectProfileName;
    public final String targetPositionEvolverFamilyName;
    public final String targetPositionEvolverName;
    public final String variableName;
    public final double initialFixedValue;

    public final MODE mode;
    public final double mass;
    public final boolean isRepeller;
    public final boolean isPercent;
    private double value;

    public PositionEvolverVariableAttractor(
            TYPE type
            , String sourceObjectName
            , String sourceObjectProfileName
            , String sourcePositionEvolverFamilyName
            , String sourcePositionEvolverName
            , String targetObjectName
            , String targetObjectProfileName
            , String targetPositionEvolverFamilyName
            , String targetPositionEvolverName
            , String variableName
            , double initialFixedValue
            , MODE mode
            , double mass
            , boolean isRepeller
            , boolean isPercent) {
        this.type = type;
        this.sourceObjectName = sourceObjectName;
        this.sourceObjectProfileName = sourceObjectProfileName;
        this.sourcePositionEvolverFamilyName = sourcePositionEvolverFamilyName;
        this.sourcePositionEvolverName = sourcePositionEvolverName;
        this.targetObjectName = targetObjectName;
        this.targetObjectProfileName = targetObjectProfileName;
        this.targetPositionEvolverFamilyName = targetPositionEvolverFamilyName;
        this.targetPositionEvolverName = targetPositionEvolverName;
        this.variableName = variableName;
        this.initialFixedValue = initialFixedValue;
        this.mode = mode;
        this.mass = mass;
        this.isRepeller = isRepeller;
        this.isPercent = isPercent;
        this.value = initialFixedValue;
    }

    public double getValue() { return this.value; }
    public void setValue(double value) { this.value = value; }
    public void resetValue(double minimumValue, double maximumValue) {
        if (isPercent) {
            value = minimumValue + (initialFixedValue * (maximumValue - minimumValue));
        } else {
            value = initialFixedValue;
        }
    }

    public NTuple getSourceKey() {
        return nTupleTypePositionEvolverVariableAttractorKey.createNTuple(
                sourceObjectName
                , sourceObjectProfileName
                , sourcePositionEvolverFamilyName
                , sourcePositionEvolverName
        );
    }
    public NTuple getTargetKey() {
        return nTupleTypePositionEvolverVariableAttractorKey.createNTuple(
                targetObjectName
                , targetObjectProfileName
                , targetPositionEvolverFamilyName
                , targetPositionEvolverName
        );
    }

    public enum TYPE {
        FIXED_VALUE
        , OTHER_OBJECT
    }

    public enum MODE {
        CONSTANT_SPEED
        , GRAVITY
    }

    public static final NTupleType nTupleTypePositionEvolverVariableAttractorKey =
            NTupleType.DefaultFactory.create(
                    String.class        // Target object name
                    , String.class      // Target object profile name
                    , String.class      // Target position evolver family name
                    , String.class      // Target position evolver name
            );
}
