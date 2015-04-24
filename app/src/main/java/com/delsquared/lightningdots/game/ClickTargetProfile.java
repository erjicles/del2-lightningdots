package com.delsquared.lightningdots.game;

public class ClickTargetProfile {

    // Target radius values
    public final double initialTargetRadiusInches;
    public final double minimumTargetRadiusInches;

    // Target speed values
    public final double initialTargetSpeedInchesPerSecond;
    public final double maximumTargetSpeedInchesPerSecond;
    public final double maximumTargetSpeedChangeInchesPerSecondPerSecond;

    // Target radius change values
    public final double maximumTargetDRadiusInchesPerSecond;
    public final double maximumTargetDRadiusChangeInchesPerSecondPerSecond;

    // Target direction change values
    public final double maximumTargetDirectionAngleChangeRadiansPerSecond;

    // Random change values
    public final double probabilityOfRandomPositionChangePerSecond;
    public final double probabilityOfRandomDirectionChangePerSecond;
    public final double probabilityOfRandomRadiusChangePerSecond;
    public final double probabilityOfRandomDRadiusChangePerSecond;
    public final double probabilityOfRandomSpeedChangePerSecond;

    public final boolean canChangePosition;
    public final boolean canChangeSpeed;
    public final boolean canChangeDirection;
    public final boolean canChangeRadius;
    public final boolean canChangeDRadius;

    public final boolean canRandomlyChangePosition;
    public final boolean canRandomlyChangeSpeed;
    public final boolean canRandomlyChangeDirection;
    public final boolean canRandomlyChangeRadius;
    public final boolean canRandomlyChangeDRadius;

    // Script transition values
    TRANSITION_CONTINUITY transitionContinuityPosition;
    TRANSITION_CONTINUITY transitionContinuitySpeed;
    TRANSITION_CONTINUITY transitionContinuityDirection;
    TRANSITION_CONTINUITY transitionContinuitySpeedChange;
    TRANSITION_CONTINUITY transitionContinuityDirectionChange;
    TRANSITION_CONTINUITY transitionContinuityRadius;
    TRANSITION_CONTINUITY transitionContinuityDRadius;
    TRANSITION_CONTINUITY transitionContinuityDRadiusChange;

    public ClickTargetProfile(
            double initialTargetRadiusInches
            , double minimumTargetRadiusInches
            , double initialTargetSpeedInchesPerSecond
            , double maximumTargetSpeedInchesPerSecond
            , double maximumTargetSpeedChangeInchesPerSecondPerSecond
            , double maximumTargetDRadiusInchesPerSecond
            , double maximumTargetDRadiusChangeInchesPerSecondPerSecond
            , double maximumTargetDirectionAngleChangeRadiansPerSecond

            , double probabilityOfRandomPositionChangePerSecond
            , double probabilityOfRandomDirectionChangePerSecond
            , double probabilityOfRandomRadiusChangePerSecond
            , double probabilityOfRandomDRadiusChangePerSecond
            , double probabilityOfRandomSpeedChangePerSecond

            , boolean canChangePosition
            , boolean canChangeSpeed
            , boolean canChangeDirection
            , boolean canChangeRadius
            , boolean canChangeDRadius

            , boolean canRandomlyChangePosition
            , boolean canRandomlyChangeSpeed
            , boolean canRandomlyChangeDirection
            , boolean canRandomlyChangeRadius
            , boolean canRandomlyChangeDRadius

            , TRANSITION_CONTINUITY transitionContinuityPosition
            , TRANSITION_CONTINUITY transitionContinuitySpeed
            , TRANSITION_CONTINUITY transitionContinuityDirection
            , TRANSITION_CONTINUITY transitionContinuitySpeedChange
            , TRANSITION_CONTINUITY transitionContinuityDirectionChange
            , TRANSITION_CONTINUITY transitionContinuityRadius
            , TRANSITION_CONTINUITY transitionContinuityDRadius
            , TRANSITION_CONTINUITY transitionContinuityDRadiusChange

    ) {
        this.initialTargetRadiusInches = initialTargetRadiusInches;
        this.minimumTargetRadiusInches = minimumTargetRadiusInches;
        this.initialTargetSpeedInchesPerSecond = initialTargetSpeedInchesPerSecond;
        this.maximumTargetSpeedInchesPerSecond = maximumTargetSpeedInchesPerSecond;
        this.maximumTargetSpeedChangeInchesPerSecondPerSecond = maximumTargetSpeedChangeInchesPerSecondPerSecond;
        this.maximumTargetDRadiusInchesPerSecond = maximumTargetDRadiusInchesPerSecond;
        this.maximumTargetDRadiusChangeInchesPerSecondPerSecond = maximumTargetDRadiusChangeInchesPerSecondPerSecond;
        this.maximumTargetDirectionAngleChangeRadiansPerSecond = maximumTargetDirectionAngleChangeRadiansPerSecond;

        this.probabilityOfRandomPositionChangePerSecond = probabilityOfRandomPositionChangePerSecond;
        this.probabilityOfRandomDirectionChangePerSecond = probabilityOfRandomDirectionChangePerSecond;
        this.probabilityOfRandomRadiusChangePerSecond = probabilityOfRandomRadiusChangePerSecond;
        this.probabilityOfRandomDRadiusChangePerSecond = probabilityOfRandomDRadiusChangePerSecond;
        this.probabilityOfRandomSpeedChangePerSecond = probabilityOfRandomSpeedChangePerSecond;

        this.canChangePosition = canChangePosition;
        this.canChangeSpeed = canChangeSpeed;
        this.canChangeDirection = canChangeDirection;
        this.canChangeRadius = canChangeRadius;
        this.canChangeDRadius = canChangeDRadius;

        this.canRandomlyChangePosition = canRandomlyChangePosition;
        this.canRandomlyChangeSpeed = canRandomlyChangeSpeed;
        this.canRandomlyChangeDirection = canRandomlyChangeDirection;
        this.canRandomlyChangeRadius = canRandomlyChangeRadius;
        this.canRandomlyChangeDRadius = canRandomlyChangeDRadius;

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
