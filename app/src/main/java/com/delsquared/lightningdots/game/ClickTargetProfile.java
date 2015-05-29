package com.delsquared.lightningdots.game;

import com.delsquared.lightningdots.utilities.PositionEvolver;

import java.util.ArrayList;

public class ClickTargetProfile {

    // Target position values
    public final ProfileVariableValues targetPositionHorizontalValuesInches;
    public final ProfileVariableValues targetPositionVerticalValuesInches;

    // Target speed values
    public final ProfileVariableValues targetSpeedValuesInchesPerSecond;

    // Target direction values
    public final ProfileVariableValues targetDirectionAngleValuesRadians;

    // Target DSpeed values
    public final ProfileVariableValues targetDSpeedValuesInchesPerSecondPerSecond;

    // Target DDirection values
    public final ProfileVariableValues targetDDirectionValuesRadiansPerSecond;

    // Target radius values
    public final ProfileVariableValues targetRadiusValuesInches;

    // Target DRadius values
    public final ProfileVariableValues targetDRadiusValuesInchesPerSecond;

    // Target D2Radius values
    public final ProfileVariableValues targetD2RadiusValuesInchesPerSecondPerSecond;

    // Target rotation values
    public final ProfileVariableValues targetRotationValuesRadians;

    // Target DRotation values
    public final ProfileVariableValues targetDRotationValuesRadiansPerSecond;

    // Target D2Rotation values
    public final ProfileVariableValues targetD2RotationValuesRadiansPerSecondPerSecond;

    // Tie random changes to other random changes
    public final boolean tieRandomPositionChangeToRandomSpeedChange;
    public final boolean tieRandomPositionChangeToRandomDirectionChange;
    public final boolean tieRandomSpeedChangeToRandomDirectionChange;
    public final boolean tieRandomSpeedChangeToRandomDSpeedChange;
    public final boolean tieRandomSpeedChangeToRandomDDirectionChange;
    public final boolean tieRandomDirectionChangeToRandomSpeedChange;
    public final boolean tieRandomDirectionChangeToRandomDSpeedChange;
    public final boolean tieRandomDirectionChangeToRandomDDirectionChange;
    public final boolean tieRandomRadiusChangeToRandomDRadiusChange;
    public final boolean tieRandomDRadiusChangeToRandomD2RadiusChange;
    public final boolean tieRandomRadiusChangeToRandomPositionChange;
    public final boolean tieRandomRadiusChangeToRandomSpeedChange;
    public final boolean tieRandomRadiusChangeToRandomDirectionChange;
    public final boolean tieRandomDRadiusChangeToRandomSpeedChange;
    public final boolean tieRandomDRadiusChangeToRandomDirectionChange;
    public final boolean tieRandomDRadiusChangeToRandomDSpeedChange;
    public final boolean tieRandomDRadiusChangeToRandomDDirectionChange;
    public final boolean tieRandomRotationChangeToRandomDRotationChange;
    public final boolean tieRandomDRotationChangeToRandomD2RotationChange;

    public final String targetShape;

    public ClickTargetProfile(

            // Target position values
            ProfileVariableValues targetPositionHorizontalValuesInches
            , ProfileVariableValues targetPositionVerticalValuesInches

            // Target speed values
            , ProfileVariableValues targetSpeedValuesInchesPerSecond

            // Target direction values
            , ProfileVariableValues targetDirectionAngleValuesRadians

            // Target DSpeed values
            , ProfileVariableValues targetDSpeedValuesInchesPerSecondPerSecond

            // Target DDirection values
            , ProfileVariableValues targetDDirectionValuesRadiansPerSecond

            // Target radius values
            , ProfileVariableValues targetRadiusValuesInches

            // Target DRadius values
            , ProfileVariableValues targetDRadiusValuesInchesPerSecond

            // Target D2Radius values
            , ProfileVariableValues targetD2RadiusValuesInchesPerSecondPerSecond

            // Target rotation values
            , ProfileVariableValues targetRotationValuesRadians

            // Target DRotation values
            , ProfileVariableValues targetDRotationValuesRadiansPerSecond

            // Target D2Rotation values
            , ProfileVariableValues targetD2RotationValuesRadiansPerSecondPerSecond

            // Tie random changes to other random changes
            , boolean tieRandomPositionChangeToRandomSpeedChange
            , boolean tieRandomPositionChangeToRandomDirectionChange
            , boolean tieRandomSpeedChangeToRandomDirectionChange
            , boolean tieRandomSpeedChangeToRandomDSpeedChange
            , boolean tieRandomSpeedChangeToRandomDDirectionChange
            , boolean tieRandomDirectionChangeToRandomSpeedChange
            , boolean tieRandomDirectionChangeToRandomDSpeedChange
            , boolean tieRandomDirectionChangeToRandomDDirectionChange
            , boolean tieRandomRadiusChangeToRandomDRadiusChange
            , boolean tieRandomDRadiusChangeToRandomD2RadiusChange
            , boolean tieRandomRadiusChangeToRandomPositionChange
            , boolean tieRandomRadiusChangeToRandomSpeedChange
            , boolean tieRandomRadiusChangeToRandomDirectionChange
            , boolean tieRandomDRadiusChangeToRandomSpeedChange
            , boolean tieRandomDRadiusChangeToRandomDirectionChange
            , boolean tieRandomDRadiusChangeToRandomDSpeedChange
            , boolean tieRandomDRadiusChangeToRandomDDirectionChange
            , boolean tieRandomRotationChangeToRandomDRotationChange
            , boolean tieRandomDRotationChangeToRandomD2RotationChange

            // Polygon type
            , String targetShape

    ) {

        // Target position values
        this.targetPositionHorizontalValuesInches = targetPositionHorizontalValuesInches;
        this.targetPositionVerticalValuesInches = targetPositionVerticalValuesInches;

        // Target speed values
        this.targetSpeedValuesInchesPerSecond = targetSpeedValuesInchesPerSecond;

        // Target direction values
        this.targetDirectionAngleValuesRadians = targetDirectionAngleValuesRadians;

        // Target DSpeed values
        this.targetDSpeedValuesInchesPerSecondPerSecond = targetDSpeedValuesInchesPerSecondPerSecond;

        // Target DDirection values
        this.targetDDirectionValuesRadiansPerSecond = targetDDirectionValuesRadiansPerSecond;

        // Target radius values
        this.targetRadiusValuesInches = targetRadiusValuesInches;

        // Target DRadius values
        this.targetDRadiusValuesInchesPerSecond = targetDRadiusValuesInchesPerSecond;

        // Target D2Radius values
        this.targetD2RadiusValuesInchesPerSecondPerSecond = targetD2RadiusValuesInchesPerSecondPerSecond;

        // Target rotation values
        this.targetRotationValuesRadians = targetRotationValuesRadians;

        // Target DRotation values
        this.targetDRotationValuesRadiansPerSecond = targetDRotationValuesRadiansPerSecond;

        // Target D2Rotation values
        this.targetD2RotationValuesRadiansPerSecondPerSecond = targetD2RotationValuesRadiansPerSecondPerSecond;

        // Tie random changes to other random changes
        this.tieRandomPositionChangeToRandomSpeedChange = tieRandomPositionChangeToRandomSpeedChange;
        this.tieRandomPositionChangeToRandomDirectionChange = tieRandomPositionChangeToRandomDirectionChange;
        this.tieRandomSpeedChangeToRandomDirectionChange = tieRandomSpeedChangeToRandomDirectionChange;
        this.tieRandomSpeedChangeToRandomDSpeedChange = tieRandomSpeedChangeToRandomDSpeedChange;
        this.tieRandomSpeedChangeToRandomDDirectionChange = tieRandomSpeedChangeToRandomDDirectionChange;
        this.tieRandomDirectionChangeToRandomSpeedChange = tieRandomDirectionChangeToRandomSpeedChange;
        this.tieRandomDirectionChangeToRandomDSpeedChange = tieRandomDirectionChangeToRandomDSpeedChange;
        this.tieRandomDirectionChangeToRandomDDirectionChange = tieRandomDirectionChangeToRandomDDirectionChange;
        this.tieRandomRadiusChangeToRandomDRadiusChange = tieRandomRadiusChangeToRandomDRadiusChange;
        this.tieRandomDRadiusChangeToRandomD2RadiusChange = tieRandomDRadiusChangeToRandomD2RadiusChange;
        this.tieRandomRadiusChangeToRandomPositionChange = tieRandomRadiusChangeToRandomPositionChange;
        this.tieRandomRadiusChangeToRandomSpeedChange = tieRandomRadiusChangeToRandomSpeedChange;
        this.tieRandomRadiusChangeToRandomDirectionChange = tieRandomRadiusChangeToRandomDirectionChange;
        this.tieRandomDRadiusChangeToRandomSpeedChange = tieRandomDRadiusChangeToRandomSpeedChange;
        this.tieRandomDRadiusChangeToRandomDirectionChange = tieRandomDRadiusChangeToRandomDirectionChange;
        this.tieRandomDRadiusChangeToRandomDSpeedChange = tieRandomDRadiusChangeToRandomDSpeedChange;
        this.tieRandomDRadiusChangeToRandomDDirectionChange = tieRandomDRadiusChangeToRandomDDirectionChange;
        this.tieRandomRotationChangeToRandomDRotationChange = tieRandomRotationChangeToRandomDRotationChange;
        this.tieRandomDRotationChangeToRandomD2RotationChange = tieRandomDRotationChangeToRandomD2RotationChange;

        // Polygon type
        this.targetShape = targetShape;

    }

    public enum TRANSITION_CONTINUITY {
        CONTINUOUS
        , DISCONTINUOUS
        , DEFAULT
    }

    public static class ProfileVariableValues {
        public final double minimumValue;
        public final double initialValue;
        public final double maximumValue;
        public final boolean randomInitialValue;
        public final boolean absoluteValuedValues;
        public final boolean randomInitialSign;
        public final boolean canChange;
        public final boolean canRandomlyChange;
        public final PositionEvolver.RANDOM_CHANGE_INTERVAL randomChangeInterval;
        public final double randomChangeIntervalValue;
        public final PositionEvolver.BOUNDARY_EFFECT boundaryEffect;
        public final TRANSITION_CONTINUITY transitionContinuity;

        public ProfileVariableValues(
                double minimumValue
                , double initialValue
                , double maximumValue
                , boolean randomInitialValue
                , boolean absoluteValuedValues
                , boolean randomInitialSign
                , boolean canChange
                , boolean canRandomlyChange
                , PositionEvolver.RANDOM_CHANGE_INTERVAL randomChangeInterval
                , double randomChangeIntervalValue
                , PositionEvolver.BOUNDARY_EFFECT boundaryEffect
                , TRANSITION_CONTINUITY transitionContinuity) {
            this.minimumValue = minimumValue;
            this.initialValue = initialValue;
            this.maximumValue = maximumValue;
            this.randomInitialValue = randomInitialValue;
            this.absoluteValuedValues = absoluteValuedValues;
            this.randomInitialSign = randomInitialSign;
            this.canChange = canChange;
            this.canRandomlyChange = canRandomlyChange;
            this.randomChangeInterval = randomChangeInterval;
            this.randomChangeIntervalValue = randomChangeIntervalValue;
            this.boundaryEffect = boundaryEffect;
            this.transitionContinuity = transitionContinuity;
        }
    }
}
