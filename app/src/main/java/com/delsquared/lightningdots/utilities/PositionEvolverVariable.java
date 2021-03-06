package com.delsquared.lightningdots.utilities;

import com.delsquared.lightningdots.game.ClickTargetProfile;

public class PositionEvolverVariable implements INamedObject {

    @SuppressWarnings("unused")
    private PositionEvolver parentPositionEvolver;

    private final String name;
    private double value;
    private double oldValue;
    private double minimumValue;
    private final double initialValue;
    private double maximumValue;
    private final boolean usesInitialValueMultipliers;
    private final boolean randomInitialValue;
    private final boolean randomInitialSign;
    private final boolean canChange;
    private final TimedChangeHandler timedChangeHandler;
    private final BoundaryEffect boundaryEffect;
    private final TransitionContinuity transitionContinuity;

    @SuppressWarnings("unused")
    public PositionEvolverVariable() {
        name = "";
        value = 0.0;
        oldValue = 0.0;
        minimumValue = 0.0;
        initialValue = 0.0;
        maximumValue = 0.0;
        usesInitialValueMultipliers = false;
        randomInitialValue = false;
        randomInitialSign = false;
        canChange = false;
        timedChangeHandler = new TimedChangeHandler();
        boundaryEffect = new BoundaryEffect();
        transitionContinuity = TransitionContinuity.DEFAULT;
    }

    public PositionEvolverVariable(
            String name,
            double minimumValue
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
        this.value = initialValue;
        this.oldValue = initialValue;
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

    @SuppressWarnings("unused")
    public PositionEvolverVariable(ClickTargetProfile.ProfileVariableValues profileVariableValues) {
        this.name = profileVariableValues.name;
        this.value = profileVariableValues.initialValue;
        this.oldValue = profileVariableValues.initialValue;
        this.minimumValue = profileVariableValues.minimumValue;
        this.initialValue = profileVariableValues.initialValue;
        this.maximumValue = profileVariableValues.maximumValue;
        this.usesInitialValueMultipliers = profileVariableValues.usesInitialValueMultipliers;
        this.randomInitialValue = profileVariableValues.randomInitialValue;
        this.randomInitialSign = profileVariableValues.randomInitialSign;
        this.canChange = profileVariableValues.canChange;
        this.timedChangeHandler = profileVariableValues.timedChangeHandler;
        this.boundaryEffect = profileVariableValues.boundaryEffect;
        this.transitionContinuity = profileVariableValues.transitionContinuity;
    }

    public String getName() { return this.name; }
    public double getValue() { return this.value; }
    public double getOldValue() { return this.oldValue; }
    public double getMinimumValue() { return this.minimumValue; }
    @SuppressWarnings("unused")
    public double getInitialValue() { return this.initialValue; }
    public double getMaximumValue() { return this.maximumValue; }
    @SuppressWarnings("unused")
    public boolean getUsesInitialValueMultipliers() { return this.usesInitialValueMultipliers; }
    @SuppressWarnings("unused")
    public boolean getRandomInitialValue() { return this.randomInitialValue; }
    @SuppressWarnings("unused")
    public boolean getRandomInitialSign() { return this.randomInitialSign; }
    public boolean getCanChange() { return this.canChange; }
    @SuppressWarnings("unused")
    public TimedChangeHandler getTimedChangeHandler() { return this.timedChangeHandler; }
    public BoundaryEffect getBoundaryEffect() { return this.boundaryEffect; }
    @SuppressWarnings("unused")
    public TransitionContinuity getTransitionContinuity() { return this.transitionContinuity; }

    /**
     * Sets the variable value to the new specified value.
     * Sets the old value to the current value.
     * @param value The value to set
     */
    public void setValue(double value) {
        this.oldValue = value;
        this.value = value;
    }

    /**
     * Resets the value to the specified new value.
     * Resets the old value to the new value.
     * @param value The value to reset
     */
    public void reinitializeValue(double value) {
        if (usesInitialValueMultipliers) {
            this.value = minimumValue + (value * (maximumValue - minimumValue));
        } else {
            this.value = value;
        }
        this.oldValue = this.value;
    }

    /**
     * Resets the value to a random value between the minimum and maximum.
     * Also resets the old value to the new random value.
     */
    public void randomizeValue() {

        // Generate new value
        value = UtilityFunctions.generateRandomValue(minimumValue, maximumValue, false);

        // Check if we should mirror absolute value boundaries
        if (boundaryEffect.mirrorAbsoluteValueBoundaries) {

            // Randomize sign
            value *= UtilityFunctions.getRandomSign();

        }

        this.oldValue = this.value;
    }

    /**
     * Adds dx to the current value. Sets the old value to the current value.
     * @param dx The value by which to move
     */
    public void moveValue(double dx) {
        this.oldValue = this.value;
        value += dx;
    }

    public void setBoundaryValues(double minimumValue, double maximumValue) {
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
    }

    public boolean checkTimedChange(double dt) {
        return timedChangeHandler.checkTimedChange(dt);
    }

    @SuppressWarnings("unused")
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
                    && (boundaryEffect.boundaryType == BoundaryType.HARD
                    || boundaryEffect.boundaryType == BoundaryType.PERIODIC
                    || boundaryEffect.boundaryType == BoundaryType.PERIODIC_REFLECTIVE)) {
                newValue *= -1.0;
            }
            return new BoundaryHandlerValues(
                    newValue
                    , boundaryEffect.boundaryType == BoundaryType.HARD
                    || boundaryEffect.boundaryType == BoundaryType.PERIODIC_REFLECTIVE
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

        switch (boundaryEffect.boundaryType) {
            case STICKY:
                //noinspection DuplicateBranchesInSwitch
                newValue = (typeOfBoundaryReached == -1) ? minimumValueToUse : maximumValueToUse;
                break;

            case HARD:
                double newMinValue = minimumValueToUse + remainingOverflow;
                double newMaxValue = maximumValueToUse - remainingOverflow;
                if (numberOfBoundariesReached % 2 == 0) {
                    newValue = (typeOfBoundaryReached == 1) ?
                            newMinValue
                            : newMaxValue;
                } else {
                    newValue = (typeOfBoundaryReached == 1) ?
                            newMaxValue
                            : newMinValue;
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

}
