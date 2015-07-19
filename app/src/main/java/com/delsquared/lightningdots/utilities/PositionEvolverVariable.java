package com.delsquared.lightningdots.utilities;

import com.delsquared.lightningdots.game.ClickTargetProfile;

public class PositionEvolverVariable {

    private String name;
    private double value;
    private double minimumValue;
    private double initialValue;
    private double maximumValue;
    private boolean usesInitialValueMultipliers;
    private boolean randomInitialValue;
    private boolean randomInitialSign;
    private boolean canChange;
    private PositionEvolver.RandomChangeEffect randomChangeEffect;
    private PositionEvolver.BoundaryEffect boundaryEffect;
    private ClickTargetProfile.TRANSITION_CONTINUITY transitionContinuity;

    private double totalTimeElapsedSinceLastRandomChangeSeconds = 0.0;

    public PositionEvolverVariable() {
        name = "";
        value = 0.0;
        minimumValue = 0.0;
        initialValue = 0.0;
        maximumValue = 0.0;
        usesInitialValueMultipliers = false;
        randomInitialValue = false;
        randomInitialSign = false;
        canChange = false;
        randomChangeEffect = new PositionEvolver.RandomChangeEffect();
        boundaryEffect = new PositionEvolver.BoundaryEffect();
        transitionContinuity = ClickTargetProfile.TRANSITION_CONTINUITY.DEFAULT;
    }

    public PositionEvolverVariable(
            String name
            , double value
            , double minimumValue
            , double initialValue
            , double maximumValue
            , boolean usesInitialValueMultipliers
            , boolean randomInitialValue
            , boolean randomInitialSign
            , boolean canChange
            , PositionEvolver.RandomChangeEffect randomChangeEffect
            , PositionEvolver.BoundaryEffect boundaryEffect
            , ClickTargetProfile.TRANSITION_CONTINUITY transitionContinuity) {
        this.name = name;
        this.value = initialValue;
        this.minimumValue = minimumValue;
        this.initialValue = initialValue;
        this.maximumValue = maximumValue;
        this.usesInitialValueMultipliers = usesInitialValueMultipliers;
        this.randomInitialValue = randomInitialValue;
        this.randomInitialSign = randomInitialSign;
        this.canChange = canChange;
        this.randomChangeEffect = randomChangeEffect;
        this.boundaryEffect = boundaryEffect;
        this.transitionContinuity = transitionContinuity;
    }

    public PositionEvolverVariable(ClickTargetProfile.ProfileVariableValues profileVariableValues) {
        this.name = profileVariableValues.name;
        this.value = profileVariableValues.initialValue;
        this.minimumValue = profileVariableValues.minimumValue;
        this.initialValue = profileVariableValues.initialValue;
        this.maximumValue = profileVariableValues.maximumValue;
        this.usesInitialValueMultipliers = profileVariableValues.usesInitialValueMultipliers;
        this.randomInitialValue = profileVariableValues.randomInitialValue;
        this.randomInitialSign = profileVariableValues.randomInitialSign;
        this.canChange = profileVariableValues.canChange;
        this.randomChangeEffect = profileVariableValues.randomChangeEffect;
        this.boundaryEffect = profileVariableValues.boundaryEffect;
        this.transitionContinuity = profileVariableValues.transitionContinuity;
    }

    public String getName() { return this.name; }
    public double getValue() { return this.value; }
    public double getMinimumValue() { return this.minimumValue; }
    public double getInitialValue() { return this.initialValue; }
    public double getMaximumValue() { return this.maximumValue; }
    public boolean getUsesInitialValueMultipliers() { return this.usesInitialValueMultipliers; }
    public boolean getRandomInitialValue() { return this.randomInitialValue; }
    public boolean getRandomInitialSign() { return this.randomInitialSign; }
    public boolean getCanChange() { return this.canChange; }
    public PositionEvolver.RandomChangeEffect getRandomChangeEffect() { return this.randomChangeEffect; }
    public PositionEvolver.BoundaryEffect getBoundaryEffect() { return this.boundaryEffect; }
    public ClickTargetProfile.TRANSITION_CONTINUITY getTransitionContinuity() { return this.transitionContinuity; }
    public double getTotalTimeElapsedSinceLastRandomChangeSeconds() { return this.totalTimeElapsedSinceLastRandomChangeSeconds; }

    public void setTotalTimeElapsedSinceLastRandomChangeSeconds(double totalTimeElapsedSinceLastRandomChangeSeconds) {
        this.totalTimeElapsedSinceLastRandomChangeSeconds = totalTimeElapsedSinceLastRandomChangeSeconds;
    }
    public void setValue(double value) { this.value = value; }
    public void reinitializeValue(double value) {
        if (usesInitialValueMultipliers) {
            this.value = minimumValue + (value * (maximumValue - minimumValue));
        } else {
            this.value = value;
        }
    }

    public void incrementTotalTimeElapsedSinceLastRandomChangeSeconds(double dt) {
        this.totalTimeElapsedSinceLastRandomChangeSeconds += dt;
    }

    public void randomizeValue() {

        // Generate new value
        value = minimumValue + (Math.random() * (maximumValue - minimumValue));

        // Check if we should mirror absolute value boundaries
        if (boundaryEffect.mirrorAbsoluteValueBoundaries) {

            // Randomize sign
            value *= (Math.random() <= 0.5) ? 1 : -1;

        }
    }

    public void moveValue(double dx) {
        value += dx;
    }

    public void setBoundaryValues(double minimumValue, double maximumValue) {
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
    }

    public boolean getShouldRandomlyChange(double dt) {

        // Initialize the result
        boolean shouldRandomlyChange = false;

        // Check if it should change randomly in regular intervals
        if (randomChangeEffect.randomChangeInterval == PositionEvolver.RandomChangeEffect.RANDOM_CHANGE_INTERVAL.REGULAR) {

            // Check if the time since the last random change has exceeded the interval
            if (totalTimeElapsedSinceLastRandomChangeSeconds >= randomChangeEffect.randomChangeValue) {

                // Set the random change flag to true
                shouldRandomlyChange = true;

                // Reset the timer
                totalTimeElapsedSinceLastRandomChangeSeconds = 0.0;

            }

        }

        // It should change randomly in random intervals
        else if (randomChangeEffect.randomChangeInterval == PositionEvolver.RandomChangeEffect.RANDOM_CHANGE_INTERVAL.RANDOM) {

            // Generate random value
            double probabilityThreshold = 1.0 - Math.pow(1.0 - randomChangeEffect.randomChangeValue, dt);
            double changeCheck = Math.random();

            // Check if it should randomly change
            if (changeCheck < probabilityThreshold) {

                // Set the random change flag
                shouldRandomlyChange = true;

            }

        }

        return shouldRandomlyChange;

    }

    public int checkBoundaryReachedNoDX() {

        // Initialize if we reached a boundary
        int boundaryReached = 0;

        // Check if the boundaries are absolute values that are mirrored positive and negative
        if (boundaryEffect.mirrorAbsoluteValueBoundaries) {

            double negativeMinimumX = -1.0 * maximumValue;
            double negativeMaximumX = -1.0 * minimumValue;

            if (value <= negativeMinimumX) {
                boundaryReached = -2;
            }

            if (value >= maximumValue) {
                boundaryReached = 2;
            }

            if (value >= negativeMaximumX && value <= minimumValue) {

                double middleValue = negativeMaximumX + ((minimumValue - negativeMaximumX) / 2.0);

                if (value <= middleValue) {
                    boundaryReached = -1;
                } else {
                    boundaryReached = 1;
                }

            }

        } else {

            if (value <= minimumValue) {
                boundaryReached = -1;
            }

            if (value >= maximumValue) {
                boundaryReached = 1;
            }

        }

        return boundaryReached;

    }

    public int checkBoundaryReached(int dxDirection) {

        // Initialize if we reached a boundary
        int boundaryReached = 0;

        // Check if the boundaries are absolute values that are mirrored positive and negative
        if (boundaryEffect.mirrorAbsoluteValueBoundaries) {

            double negativeMinimumValue = -1.0 * maximumValue;
            double negativeMaximumValue = -1.0 * minimumValue;

            if (dxDirection < 0 && (
                    (value < negativeMinimumValue)
                            || (canChange && value == negativeMinimumValue))) {
                boundaryReached = -2;
            }

            if (dxDirection > 0 && (
                    (value > maximumValue)
                            || (canChange && value == maximumValue))) {
                boundaryReached = 2;
            }

            if (dxDirection < 0 && (
                    (value > negativeMaximumValue && value < minimumValue)
                            || (canChange && value == minimumValue))) {
                boundaryReached = 1;
            }

            if (dxDirection > 0 && (
                    (value > negativeMaximumValue && value < minimumValue)
                            || (canChange && value == negativeMaximumValue))) {
                boundaryReached = -1;
            }

        } else {

            if (dxDirection < 0 && (
                    (value < minimumValue)
                            || (canChange && value == minimumValue))) {
                boundaryReached = -1;
            }

            if (dxDirection > 0 && (
                    (value > maximumValue)
                            || (canChange && value == maximumValue))) {
                boundaryReached = 1;
            }

        }

        return boundaryReached;
    }

    public BoundaryHandlerValues processBoundary(int boundaryReached) {

        // Check if no boundary was reached
        if (boundaryReached == 0) {
            return new BoundaryHandlerValues(
                    value
                    , false
            );
        }

        // Check the extremal case where the min = max
        if (minimumValue == maximumValue) {
            double newValue = minimumValue;
            if (boundaryEffect.mirrorAbsoluteValueBoundaries
                    && (boundaryEffect.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.BOUNCE
                    || boundaryEffect.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.PERIODIC
                    || boundaryEffect.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.PERIODIC_REFLECTIVE)) {
                newValue *= -1.0;
            }
            return new BoundaryHandlerValues(
                    newValue
                    , boundaryEffect.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.BOUNCE
                    || boundaryEffect.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.PERIODIC_REFLECTIVE
            );
        }

        double newValue = value;
        boolean bounce = false;

        double valueRange = maximumValue - minimumValue;

        int typeOfBoundaryReached = boundaryReached;
        double minimumValueToUse = minimumValue;
        double maximumValueToUse = maximumValue;
        double otherRegionMinimumValue = -1.0 * maximumValue;
        double otherRegionMaximumValue = -1.0 * minimumValue;
        if (boundaryEffect.mirrorAbsoluteValueBoundaries) {
            double negativeMinimumValue = -1.0 * maximumValue;
            double negativeMaximumValue = -1.0 * minimumValue;
            switch (boundaryReached) {
                case -2:
                    minimumValueToUse = negativeMinimumValue;
                    maximumValueToUse = negativeMaximumValue;
                    otherRegionMinimumValue = minimumValue;
                    otherRegionMaximumValue = maximumValue;
                    typeOfBoundaryReached = -1;
                    break;
                case -1:
                    minimumValueToUse = negativeMinimumValue;
                    maximumValueToUse = negativeMaximumValue;
                    otherRegionMinimumValue = minimumValue;
                    otherRegionMaximumValue = maximumValue;
                    typeOfBoundaryReached = 1;
                    break;
                case 1:
                    typeOfBoundaryReached = -1;
                    break;
                case 2:
                    typeOfBoundaryReached = 1;
                    break;
            }
        }

        double boundaryOverflow = (typeOfBoundaryReached == 1) ?
                newValue - maximumValueToUse
                : minimumValueToUse - newValue;

        double remainingOverflow = boundaryOverflow % valueRange;

        int numberOfBoundariesReached = (int) Math.floor(boundaryOverflow / valueRange) + 1;

        switch (boundaryEffect.boundaryEffect) {
            case STICK:
                newValue = (typeOfBoundaryReached == -1) ? minimumValueToUse : maximumValueToUse;
                break;

            case BOUNCE:
                if (numberOfBoundariesReached % 2 == 0) {
                    newValue = (typeOfBoundaryReached == 1) ?
                            minimumValueToUse + remainingOverflow
                            : maximumValueToUse - remainingOverflow;
                } else {
                    newValue = (typeOfBoundaryReached == 1) ?
                            maximumValueToUse - remainingOverflow
                            : minimumValueToUse + remainingOverflow;
                    bounce = true;
                }
                break;

            case PERIODIC:
                if (boundaryEffect.mirrorAbsoluteValueBoundaries) {

                    if (numberOfBoundariesReached % 2 == 1) {

                        if (typeOfBoundaryReached == 1) {
                            newValue = minimumValueToUse + remainingOverflow;
                        } else {
                            newValue = maximumValueToUse - remainingOverflow;
                        }

                    } else {

                        if (typeOfBoundaryReached == 1) {
                            newValue = otherRegionMinimumValue + remainingOverflow;
                        } else {
                            newValue = otherRegionMaximumValue - remainingOverflow;
                        }

                    }

                } else {

                    if (typeOfBoundaryReached == 1) {
                        newValue = minimumValueToUse + remainingOverflow;
                    } else {
                        newValue = maximumValueToUse - remainingOverflow;
                    }

                }

                break;

            case PERIODIC_REFLECTIVE:
                if (numberOfBoundariesReached % 2 == 0) {
                    newValue = (typeOfBoundaryReached == 1) ?
                            minimumValueToUse + remainingOverflow
                            : maximumValueToUse - remainingOverflow;
                } else {
                    newValue = (typeOfBoundaryReached == 1) ?
                            maximumValueToUse - remainingOverflow
                            : minimumValueToUse + remainingOverflow;
                    bounce = true;
                }

                break;

            default: // STICK
                newValue = (typeOfBoundaryReached == -1) ? minimumValueToUse : maximumValueToUse;
        }
        return new BoundaryHandlerValues(
                newValue,
                bounce);
    }

    public static class BoundaryHandlerValues {
        public final double newValue;
        public final boolean bounceValue;

        public BoundaryHandlerValues(
                double newValue
                , boolean bounceValue) {
            this.newValue = newValue;
            this.bounceValue = bounceValue;
        }

    }

}
