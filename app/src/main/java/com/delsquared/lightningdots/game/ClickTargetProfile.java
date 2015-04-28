package com.delsquared.lightningdots.game;

import com.delsquared.lightningdots.utilities.PositionEvolver;

public class ClickTargetProfile {

    // Target radius values
    public final double minimumTargetRadiusInches;
    public final double initialTargetRadiusInches;
    public final double maximumTargetRadiusInches;
    public final boolean randomInitialTargetRadius;

    // Target speed values
    public final double minimumTargetSpeedInchesPerSecond;
    public final double initialTargetSpeedInchesPerSecond;
    public final double maximumTargetSpeedInchesPerSecond;
    public final boolean randomInitialTargetSpeed;
    public final double minimumTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond;
    public final double initialTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond;
    public final double maximumTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond;
    public final boolean randomInitialTargetSpeedChange;
    public final boolean randomInitialTargetSpeedChangeSign;

    // Target radius change values
    public final double minimumTargetDRadiusAbsoluteValueInchesPerSecond;
    public final double initialTargetDRadiusAbsoluteValueInchesPerSecond;
    public final double maximumTargetDRadiusAbsoluteValueInchesPerSecond;
    public final boolean randomInitialTargetDRadius;
    public final boolean randomInitialTargetDRadiusSign;
    public final double minimumTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond;
    public final double initialTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond;
    public final double maximumTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond;
    public final boolean randomInitialTargetDRadiusChange;
    public final boolean randomInitialTargetDRadiusChangeSign;

    // Target direction values
    public final double initialTargetDirectionAngleRadians;
    public final boolean randomInitialTargetDirectionAngle;

    // Target direction change values
    public final double minimumTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond;
    public final double initialTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond;
    public final double maximumTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond;
    public final boolean randomInitialTargetDirectionAngleChange;
    public final boolean randomInitialTargetDirectionAngleChangeSign;

    // Random change values
    public final double probabilityOfRandomPositionChangePerSecond;
    public final double probabilityOfRandomSpeedChangePerSecond;
    public final double probabilityOfRandomDirectionChangePerSecond;
    public final double probabilityOfRandomDSpeedChangePerSecond;
    public final double probabilityOfRandomDDirectionChangePerSecond;
    public final double probabilityOfRandomRadiusChangePerSecond;
    public final double probabilityOfRandomDRadiusChangePerSecond;
    public final double probabilityOfRandomD2RadiusChangePerSecond;

    // Variable change values
    public final boolean canChangePosition;
    public final boolean canChangeSpeed;
    public final boolean canChangeDirection;
    public final boolean canChangeRadius;
    public final boolean canChangeDRadius;

    // Random change booleans
    public final boolean canRandomlyChangePosition;
    public final boolean canRandomlyChangeSpeed;
    public final boolean canRandomlyChangeDirection;
    public final boolean canRandomlyChangeDSpeed;
    public final boolean canRandomlyChangeDDirection;
    public final boolean canRandomlyChangeRadius;
    public final boolean canRandomlyChangeDRadius;
    public final boolean canRandomlyChangeD2Radius;

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

    // Boundary effect values
    public final PositionEvolver.BOUNDARY_EFFECT boundaryEffectPositionHorizontal;
    public final PositionEvolver.BOUNDARY_EFFECT boundaryEffectPositionVertical;
    public final PositionEvolver.BOUNDARY_EFFECT boundaryEffectSpeed;
    public final PositionEvolver.BOUNDARY_EFFECT boundaryEffectDirection;
    public final PositionEvolver.BOUNDARY_EFFECT boundaryEffectDSpeed;
    public final PositionEvolver.BOUNDARY_EFFECT boundaryEffectDDirection;
    public final PositionEvolver.BOUNDARY_EFFECT boundaryEffectRadius;
    public final PositionEvolver.BOUNDARY_EFFECT boundaryEffectDRadius;
    public final PositionEvolver.BOUNDARY_EFFECT boundaryEffectD2Radius;

    // Script transition values
    public final TRANSITION_CONTINUITY transitionContinuityPosition;
    public final TRANSITION_CONTINUITY transitionContinuitySpeed;
    public final TRANSITION_CONTINUITY transitionContinuityDirection;
    public final TRANSITION_CONTINUITY transitionContinuitySpeedChange;
    public final TRANSITION_CONTINUITY transitionContinuityDirectionChange;
    public final TRANSITION_CONTINUITY transitionContinuityRadius;
    public final TRANSITION_CONTINUITY transitionContinuityDRadius;
    public final TRANSITION_CONTINUITY transitionContinuityDRadiusChange;

    public ClickTargetProfile(

            // Target radius values
            double minimumTargetRadiusInches
            , double initialTargetRadiusInches
            , double maximumTargetRadiusInches
            , boolean randomInitialTargetRadius

            // Target speed values
            , double minimumTargetSpeedInchesPerSecond
            , double initialTargetSpeedInchesPerSecond
            , double maximumTargetSpeedInchesPerSecond
            , boolean randomInitialTargetSpeed
            , double minimumTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond
            , double initialTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond
            , double maximumTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond
            , boolean randomInitialTargetSpeedChange
            , boolean randomInitialTargetSpeedChangeSign

            // Target radius change values
            , double minimumTargetDRadiusAbsoluteValueInchesPerSecond
            , double initialTargetDRadiusAbsoluteValueInchesPerSecond
            , double maximumTargetDRadiusAbsoluteValueInchesPerSecond
            , boolean randomInitialTargetDRadius
            , boolean randomInitialTargetDRadiusSign
            , double minimumTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond
            , double initialTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond
            , double maximumTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond
            , boolean randomInitialTargetDRadiusChange
            , boolean randomInitialTargetDRadiusChangeSign

            // Target direction values
            , double initialTargetDirectionAngleRadians
            , boolean randomInitialTargetDirectionAngle

            // Target direction change values
            , double minimumTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond
            , double initialTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond
            , double maximumTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond
            , boolean randomInitialTargetDirectionAngleChange
            , boolean randomInitialTargetDirectionAngleChangeSign

            // Random change values
            , double probabilityOfRandomPositionChangePerSecond
            , double probabilityOfRandomSpeedChangePerSecond
            , double probabilityOfRandomDirectionChangePerSecond
            , double probabilityOfRandomDSpeedChangePerSecond
            , double probabilityOfRandomDDirectionChangePerSecond
            , double probabilityOfRandomRadiusChangePerSecond
            , double probabilityOfRandomDRadiusChangePerSecond
            , double probabilityOfRandomD2RadiusChangePerSecond

            // Variable change values
            , boolean canChangePosition
            , boolean canChangeSpeed
            , boolean canChangeDirection
            , boolean canChangeRadius
            , boolean canChangeDRadius

            // Random change booleans
            , boolean canRandomlyChangePosition
            , boolean canRandomlyChangeSpeed
            , boolean canRandomlyChangeDirection
            , boolean canRandomlyChangeDSpeed
            , boolean canRandomlyChangeDDirection
            , boolean canRandomlyChangeRadius
            , boolean canRandomlyChangeDRadius
            , boolean canRandomlyChangeD2Radius

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

            // Boundary effect values
            , PositionEvolver.BOUNDARY_EFFECT boundaryEffectPositionHorizontal
            , PositionEvolver.BOUNDARY_EFFECT boundaryEffectPositionVertical
            , PositionEvolver.BOUNDARY_EFFECT boundaryEffectSpeed
            , PositionEvolver.BOUNDARY_EFFECT boundaryEffectDirection
            , PositionEvolver.BOUNDARY_EFFECT boundaryEffectDSpeed
            , PositionEvolver.BOUNDARY_EFFECT boundaryEffectDDirection
            , PositionEvolver.BOUNDARY_EFFECT boundaryEffectRadius
            , PositionEvolver.BOUNDARY_EFFECT boundaryEffectDRadius
            , PositionEvolver.BOUNDARY_EFFECT boundaryEffectD2Radius

            // Script transition values
            , TRANSITION_CONTINUITY transitionContinuityPosition
            , TRANSITION_CONTINUITY transitionContinuitySpeed
            , TRANSITION_CONTINUITY transitionContinuityDirection
            , TRANSITION_CONTINUITY transitionContinuitySpeedChange
            , TRANSITION_CONTINUITY transitionContinuityDirectionChange
            , TRANSITION_CONTINUITY transitionContinuityRadius
            , TRANSITION_CONTINUITY transitionContinuityDRadius
            , TRANSITION_CONTINUITY transitionContinuityDRadiusChange

    ) {

        // Target radius values
        this.minimumTargetRadiusInches = minimumTargetRadiusInches;
        this.initialTargetRadiusInches = initialTargetRadiusInches;
        this.maximumTargetRadiusInches = maximumTargetRadiusInches;
        this.randomInitialTargetRadius = randomInitialTargetRadius;

        // Target speed values
        this.minimumTargetSpeedInchesPerSecond = minimumTargetSpeedInchesPerSecond;
        this.initialTargetSpeedInchesPerSecond = initialTargetSpeedInchesPerSecond;
        this.maximumTargetSpeedInchesPerSecond = maximumTargetSpeedInchesPerSecond;
        this.randomInitialTargetSpeed = randomInitialTargetSpeed;
        this.minimumTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond = minimumTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond;
        this.initialTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond = initialTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond;
        this.maximumTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond = maximumTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond;
        this.randomInitialTargetSpeedChange = randomInitialTargetSpeedChange;
        this.randomInitialTargetSpeedChangeSign = randomInitialTargetSpeedChangeSign;

        // Target radius change values
        this.minimumTargetDRadiusAbsoluteValueInchesPerSecond = minimumTargetDRadiusAbsoluteValueInchesPerSecond;
        this.initialTargetDRadiusAbsoluteValueInchesPerSecond = initialTargetDRadiusAbsoluteValueInchesPerSecond;
        this.maximumTargetDRadiusAbsoluteValueInchesPerSecond = maximumTargetDRadiusAbsoluteValueInchesPerSecond;
        this.randomInitialTargetDRadius = randomInitialTargetDRadius;
        this.randomInitialTargetDRadiusSign = randomInitialTargetDRadiusSign;
        this.minimumTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond = minimumTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond;
        this.initialTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond = initialTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond;
        this.maximumTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond = maximumTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond;
        this.randomInitialTargetDRadiusChange = randomInitialTargetDRadiusChange;
        this.randomInitialTargetDRadiusChangeSign = randomInitialTargetDRadiusChangeSign;

        // Target direction values
        this.initialTargetDirectionAngleRadians = initialTargetDirectionAngleRadians;
        this.randomInitialTargetDirectionAngle = randomInitialTargetDirectionAngle;

        // Target direction change values
        this.minimumTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond = minimumTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond;
        this.initialTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond = initialTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond;
        this.maximumTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond = maximumTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond;
        this.randomInitialTargetDirectionAngleChange = randomInitialTargetDirectionAngleChange;
        this.randomInitialTargetDirectionAngleChangeSign = randomInitialTargetDirectionAngleChangeSign;

        // Random change values
        this.probabilityOfRandomPositionChangePerSecond = probabilityOfRandomPositionChangePerSecond;
        this.probabilityOfRandomSpeedChangePerSecond = probabilityOfRandomSpeedChangePerSecond;
        this.probabilityOfRandomDirectionChangePerSecond = probabilityOfRandomDirectionChangePerSecond;
        this.probabilityOfRandomDSpeedChangePerSecond = probabilityOfRandomDSpeedChangePerSecond;
        this.probabilityOfRandomDDirectionChangePerSecond = probabilityOfRandomDDirectionChangePerSecond;
        this.probabilityOfRandomRadiusChangePerSecond = probabilityOfRandomRadiusChangePerSecond;
        this.probabilityOfRandomDRadiusChangePerSecond = probabilityOfRandomDRadiusChangePerSecond;
        this.probabilityOfRandomD2RadiusChangePerSecond = probabilityOfRandomD2RadiusChangePerSecond;

        // Variable change values
        this.canChangePosition = canChangePosition;
        this.canChangeSpeed = canChangeSpeed;
        this.canChangeDirection = canChangeDirection;
        this.canChangeRadius = canChangeRadius;
        this.canChangeDRadius = canChangeDRadius;

        // Random change booleans
        this.canRandomlyChangePosition = canRandomlyChangePosition;
        this.canRandomlyChangeSpeed = canRandomlyChangeSpeed;
        this.canRandomlyChangeDirection = canRandomlyChangeDirection;
        this.canRandomlyChangeDSpeed = canRandomlyChangeDSpeed;
        this.canRandomlyChangeDDirection = canRandomlyChangeDDirection;
        this.canRandomlyChangeRadius = canRandomlyChangeRadius;
        this.canRandomlyChangeDRadius = canRandomlyChangeDRadius;
        this.canRandomlyChangeD2Radius = canRandomlyChangeD2Radius;

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

        // Boundary effect values
        this.boundaryEffectPositionHorizontal = boundaryEffectPositionHorizontal;
        this.boundaryEffectPositionVertical = boundaryEffectPositionVertical;
        this.boundaryEffectSpeed = boundaryEffectSpeed;
        this.boundaryEffectDirection = boundaryEffectDirection;
        this.boundaryEffectDSpeed = boundaryEffectDSpeed;
        this.boundaryEffectDDirection = boundaryEffectDDirection;
        this.boundaryEffectRadius = boundaryEffectRadius;
        this.boundaryEffectDRadius = boundaryEffectDRadius;
        this.boundaryEffectD2Radius = boundaryEffectD2Radius;

        // Script transition values
        this.transitionContinuityPosition = transitionContinuityPosition;
        this.transitionContinuitySpeed = transitionContinuitySpeed;
        this.transitionContinuityDirection = transitionContinuityDirection;
        this.transitionContinuitySpeedChange = transitionContinuitySpeedChange;
        this.transitionContinuityDirectionChange = transitionContinuityDirectionChange;
        this.transitionContinuityRadius = transitionContinuityRadius;
        this.transitionContinuityDRadius = transitionContinuityDRadius;
        this.transitionContinuityDRadiusChange = transitionContinuityDRadiusChange;

    }

    public enum TRANSITION_CONTINUITY {
        CONTINUOUS
        , DISCONTINUOUS
        , DEFAULT
    }
}
