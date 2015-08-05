package com.delsquared.lightningdots.game;

import android.util.Pair;

import com.delsquared.lightningdots.utilities.BoundaryEffect;
import com.delsquared.lightningdots.utilities.PositionEvolver;
import com.delsquared.lightningdots.utilities.PositionEvolverVariable;
import com.delsquared.lightningdots.utilities.TimedChangeHandler;
import com.delsquared.lightningdots.utilities.TransitionContinuity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClickTargetProfile {

    public final String name;
    public final double scriptTransitionValue;
    public final String shape;
    public final boolean isClickable;
    public final ClickTarget.VISIBILITY visibility;
    public final double mass;

    public final Map<String, ProfileVariableValues> mapProfileVariableValues;

    public ClickTargetProfile(
            String name
            , double scriptTransitionValue
            , String shape
            , boolean isClickable
            , ClickTarget.VISIBILITY visibility
            , double mass
            , Map<String, ProfileVariableValues> mapProfileVariableValues) {

        this.name = name;
        this.scriptTransitionValue = scriptTransitionValue;
        this.shape = shape;
        this.isClickable = isClickable;
        this.visibility = visibility;
        this.mapProfileVariableValues = mapProfileVariableValues;
        this.mass = mass;

    }

    public static class ProfileVariableValues {
        public final String name;
        public final double minimumValue;
        public final double initialValue;
        public final double maximumValue;
        public final boolean usesInitialValueMultipliers;
        public final boolean randomInitialValue;
        public final boolean randomInitialSign;
        public final boolean canChange;
        public final TimedChangeHandler timedChangeHandler;
        public final BoundaryEffect boundaryEffect;
        public final TransitionContinuity transitionContinuity;

        public ProfileVariableValues(
                String name
                , double minimumValue
                , double initialValue
                , double maximumValue
                , boolean usesInitialValueMultipliers
                , boolean randomInitialValue
                , boolean randomInitialSign
                , boolean canChange
                , TimedChangeHandler timedChangeHandler
                , BoundaryEffect boundaryEffect
                , TransitionContinuity transitionContinuity) {
            this.name = name;
            this.minimumValue = minimumValue;
            this.initialValue = initialValue;
            this.maximumValue = maximumValue;
            this.usesInitialValueMultipliers = usesInitialValueMultipliers;
            this.randomInitialValue = randomInitialValue;
            this.randomInitialSign = randomInitialSign;
            this.canChange = canChange;
            this.timedChangeHandler = timedChangeHandler;
            this.boundaryEffect = boundaryEffect;
            this.transitionContinuity = transitionContinuity;
        }

        public PositionEvolverVariable toPositionEvolverVariable() {
            return toPositionEvolverVariable(this.initialValue);
        }

        public PositionEvolverVariable toPositionEvolverVariable(double initialValue) {
            return toPositionEvolverVariable(this.minimumValue, initialValue, this.maximumValue);
        }

        public PositionEvolverVariable toPositionEvolverVariable(double minimumValue, double initialValue, double maximumValue) {
            return new PositionEvolverVariable(
                    name
                    , initialValue
                    , minimumValue
                    , initialValue
                    , maximumValue
                    , usesInitialValueMultipliers
                    , randomInitialValue
                    , randomInitialSign
                    , canChange
                    , timedChangeHandler
                    , boundaryEffect
                    , transitionContinuity
            );
        }
    }



}
