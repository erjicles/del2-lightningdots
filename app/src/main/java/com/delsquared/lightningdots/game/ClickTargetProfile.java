package com.delsquared.lightningdots.game;

import android.util.Pair;

import com.delsquared.lightningdots.utilities.PositionEvolver;
import com.delsquared.lightningdots.utilities.PositionEvolverVariable;

import java.util.ArrayList;
import java.util.HashMap;

public class ClickTargetProfile {

    public final String name;
    public final double scriptTransitionValue;
    public final String shape;
    public final boolean isClickable;
    public final ClickTarget.VISIBILITY visibility;

    public final HashMap<String, ProfileVariableValues> mapProfileVariableValues;

    public ClickTargetProfile(
            String name
            , double scriptTransitionValue
            , String shape
            , boolean isClickable
            , ClickTarget.VISIBILITY visibility
            , HashMap<String, ProfileVariableValues> mapProfileVariableValues) {

        this.name = name;
        this.scriptTransitionValue = scriptTransitionValue;
        this.shape = shape;
        this.isClickable = isClickable;
        this.visibility = visibility;
        this.mapProfileVariableValues = mapProfileVariableValues;

    }

    public enum TRANSITION_CONTINUITY {
        CONTINUOUS
        , DISCONTINUOUS
        , DEFAULT
    }

    public static class ProfileVariableValues {
        public final String name;
        public final double minimumValue;
        public final double initialValue;
        public final double maximumValue;
        public final boolean randomInitialValue;
        public final boolean randomInitialSign;
        public final boolean canChange;
        public final PositionEvolver.RandomChangeEffect randomChangeEffect;
        public final PositionEvolver.BoundaryEffect boundaryEffect;
        public final TRANSITION_CONTINUITY transitionContinuity;

        public ProfileVariableValues(
                String name
                , double minimumValue
                , double initialValue
                , double maximumValue
                , boolean randomInitialValue
                , boolean randomInitialSign
                , boolean canChange
                , PositionEvolver.RandomChangeEffect randomChangeEffect
                , PositionEvolver.BoundaryEffect boundaryEffect
                , TRANSITION_CONTINUITY transitionContinuity) {
            this.name = name;
            this.minimumValue = minimumValue;
            this.initialValue = initialValue;
            this.maximumValue = maximumValue;
            this.randomInitialValue = randomInitialValue;
            this.randomInitialSign = randomInitialSign;
            this.canChange = canChange;
            this.randomChangeEffect = randomChangeEffect;
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
                    , randomInitialValue
                    , randomInitialSign
                    , canChange
                    , randomChangeEffect
                    , boundaryEffect
                    , transitionContinuity
            );
        }
    }



}
