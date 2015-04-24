package com.delsquared.lightningdots.utilities;

public class PositionEvolver {

    private PositionVector X = new PositionVector(0.0, 0.0, 0.0);

    private MODE mode = MODE.CARTESIAN_3D;

    private boolean isConstantX1 = true;
    private boolean isConstantX2 = true;
    private boolean isConstantX3 = true;
    private PositionEvolver positionEvolverDXdt = null;

    private double minimumX1 = Double.NEGATIVE_INFINITY;
    private double minimumX2 = Double.NEGATIVE_INFINITY;
    private double minimumX3 = Double.NEGATIVE_INFINITY;

    private double maximumX1 = Double.POSITIVE_INFINITY;
    private double maximumX2 = Double.POSITIVE_INFINITY;
    private double maximumX3 = Double.POSITIVE_INFINITY;

    private boolean canRandomlyChangePosition = false;
    private double probabilityOfRandomPositionChangePerSecond = 0.00;

    private boolean canRandomlyChangeX1 = false;
    private boolean canRandomlyChangeX2 = false;
    private boolean canRandomlyChangeX3 = false;
    private double probabilityOfRandomChangePerSecondX1 = 0.00;
    private double probabilityOfRandomChangePerSecondX2 = 0.00;
    private double probabilityOfRandomChangePerSecondX3 = 0.00;

    //private boolean bouncesOnBoundaryValue = true;
    private BOUNDARY_EFFECT boundaryEffectX1;
    private BOUNDARY_EFFECT boundaryEffectX2;
    private BOUNDARY_EFFECT boundaryEffectX3;

    public PositionEvolver(
            PositionVector X
            , MODE mode
            , boolean isConstantX1
            , boolean isConstantX2
            , boolean isConstantX3
            , PositionEvolver positionEvolverDXdt
            , double minimumX1
            , double minimumX2
            , double minimumX3
            , double maximumX1
            , double maximumX2
            , double maximumX3
            , boolean canRandomlyChangePosition
            , double probabilityOfRandomPositionChangePerSecond
            , boolean canRandomlyChangeX1
            , boolean canRandomlyChangeX2
            , boolean canRandomlyChangeX3
            , double probabilityOfRandomChangePerSecondX1
            , double probabilityOfRandomChangePerSecondX2
            , double probabilityOfRandomChangePerSecondX3
            , BOUNDARY_EFFECT boundaryEffectX1
            , BOUNDARY_EFFECT boundaryEffectX2
            , BOUNDARY_EFFECT boundaryEffectX3) {

        this.X = X;

        this.mode = mode;

        this.isConstantX1 = isConstantX1;
        this.isConstantX2 = isConstantX2;
        this.isConstantX3 = isConstantX3;
        this.positionEvolverDXdt = positionEvolverDXdt;

        this.minimumX1 = minimumX1;
        this.minimumX2 = minimumX2;
        this.minimumX3 = minimumX3;

        this.maximumX1 = maximumX1;
        this.maximumX2 = maximumX2;
        this.maximumX3 = maximumX3;

        this.canRandomlyChangePosition = canRandomlyChangePosition;
        this.probabilityOfRandomPositionChangePerSecond = probabilityOfRandomPositionChangePerSecond;

        this.canRandomlyChangeX1 = canRandomlyChangeX1;
        this.canRandomlyChangeX2 = canRandomlyChangeX2;
        this.canRandomlyChangeX3 = canRandomlyChangeX3;
        this.probabilityOfRandomChangePerSecondX1 = probabilityOfRandomChangePerSecondX1;
        this.probabilityOfRandomChangePerSecondX2 = probabilityOfRandomChangePerSecondX2;
        this.probabilityOfRandomChangePerSecondX3 = probabilityOfRandomChangePerSecondX3;

        this.boundaryEffectX1 = boundaryEffectX1;
        this.boundaryEffectX2 = boundaryEffectX2;
        this.boundaryEffectX3 = boundaryEffectX3;
    }

    public PositionVector getX() { return X; }

    public PositionVector getX(MODE convertToMode) {

        if (this.mode == convertToMode) {
            return X;
        }

        else if (this.mode == MODE.POLAR_2D
                && convertToMode == MODE.CARTESIAN_2D) {
            // x = X1 * cos(X2)
            // y = X1 * sin(X2)
            return new PositionVector(
                    X.X1 * Math.cos(X.X2)
                    , X.X1 * Math.sin(X.X2)
                    , 0.0
            );
        }

        else if (this.mode == MODE.SPHERICAL_3D
                && convertToMode == MODE.CARTESIAN_3D) {
            // x = X1 * cos(X2) * sin(X3)
            // y = X1 * sin(X2) * sin(X3)
            // z = X1 * cos(X3)
            double sinX3 = Math.sin(X.X3);
            return new PositionVector(
                    X.X1 * Math.cos(X.X2) * sinX3
                    , X.X1 * Math.sin(X.X2) * sinX3
                    , X.X1 * Math.cos(X.X3)
            );
        }

        else if (this.mode == MODE.CARTESIAN_2D
                && convertToMode == MODE.POLAR_2D) {
            // r = sqrt(X1^2 + X2^2)
            // phi = atan2(X2/X1)
            return new PositionVector(
                    Math.sqrt((X.X1 * X.X1) + (X.X2 * X.X2))
                    , Math.atan2(X.X2, X.X1)
                    , 0.0
            );
        }

        return new PositionVector(0.0, 0.0, 0.0);

    }

    public PositionEvolver getPositionEvolverDXdt() {
        return positionEvolverDXdt;
    }

    public PositionVector getDXdt() {
        if (positionEvolverDXdt != null) {
            return positionEvolverDXdt.getX();
        }
        return new PositionVector();
    }

    public PositionVector getDXdt(MODE mode) {
        if (positionEvolverDXdt != null) {
            return positionEvolverDXdt.getX(mode);
        }
        return new PositionVector();
    }

    public void setBoundaryValues(
            double minimumX1
            , double minimumX2
            , double minimumX3
            , double maximumX1
            , double maximumX2
            , double maximumX3) {
        this.minimumX1 = minimumX1;
        this.minimumX2 = minimumX2;
        this.minimumX3 = minimumX3;
        this.maximumX1 = maximumX1;
        this.maximumX2 = maximumX2;
        this.maximumX3 = maximumX3;
    }

    public void evolveTime(double dt) {

        if (canRandomlyChangePosition == true) {

            // Q = 1 - (1 - p)^(1 / N)
            // Q = probability of change in partial unit
            // P = probability of change in one unit
            // N = number of partial units in one unit
            // N = 1 second / time elapsed since last update in seconds
            double probabilityThreshold = 1.0 - Math.pow(1.0 - probabilityOfRandomPositionChangePerSecond, dt);
            double positionChangeCheck = Math.random();
            if (positionChangeCheck < probabilityThreshold) {
                X = new PositionVector(
                        minimumX1 + (Math.random() * (maximumX1 - minimumX1))
                        , minimumX2 + (Math.random() * (maximumX2 - minimumX2))
                        , minimumX3 + (Math.random() * (maximumX3 - minimumX3)));
            }
        }

        if (canRandomlyChangeX1 == true) {
            double probabilityThreshold = 1.0 - Math.pow(1.0 - probabilityOfRandomChangePerSecondX1, dt);
            double changeCheckX1 = Math.random();
            if (changeCheckX1 < probabilityThreshold) {
                X = new PositionVector(
                        minimumX1 + (Math.random() * (maximumX1 - minimumX1))
                        , X.X2
                        , X.X3);
            }
        }
        if (canRandomlyChangeX2 == true) {
            double probabilityThreshold = 1.0 - Math.pow(1.0 - probabilityOfRandomChangePerSecondX2, dt);
            double changeCheckX2 = Math.random();
            if (changeCheckX2 < probabilityThreshold) {
                X = new PositionVector(
                        X.X1
                        , minimumX2 + (Math.random() * (maximumX2 - minimumX2))
                        , X.X3);
            }
        }
        if (canRandomlyChangeX3 == true) {
            double probabilityThreshold = 1.0 - Math.pow(1.0 - probabilityOfRandomChangePerSecondX3, dt);
            double changeCheckX3 = Math.random();
            if (changeCheckX3 < probabilityThreshold) {
                X = new PositionVector(
                        X.X1
                        , X.X2
                        , minimumX3 + (Math.random() * (maximumX3 - minimumX3)));
            }
        }

        PositionVector dXdt = null;
        double dX1 = 0.0;
        double dX2 = 0.0;
        double dX3 = 0.0;
        if (positionEvolverDXdt != null) {
            dXdt = positionEvolverDXdt.getX(mode);
            if (isConstantX1 == false) {
                dX1 = dXdt.X1 * dt;
            }
            if (isConstantX2 == false) {
                dX2 = dXdt.X2 * dt;
            }
            if (isConstantX3 == false) {
                dX3 = dXdt.X3 * dt;
            }
            positionEvolverDXdt.evolveTime(dt);
        }

        double newX1 = X.X1 + dX1;
        double newX2 = X.X2 + dX2;
        double newX3 = X.X3 + dX3;

        int boundaryReachedX1 = 0;
        int boundaryReachedX2 = 0;
        int boundaryReachedX3 = 0;

        boolean bounceX1 = false;
        boolean bounceX2 = false;
        boolean bounceX3 = false;

        if ((newX1 < minimumX1)
                || (isConstantX1 == false && newX1 == minimumX1)) { boundaryReachedX1 = -1; }
        if ((newX2 < minimumX2)
                || (isConstantX2 == false && newX2 == minimumX2)) { boundaryReachedX2 = -1; }
        if ((newX3 < minimumX3)
                || (isConstantX3 == false && newX3 == minimumX3)) { boundaryReachedX3 = -1; }

        if ((newX1 > maximumX1)
                || (isConstantX1 == false && newX1 == maximumX1)) { boundaryReachedX1 = 1; }
        if ((newX2 > maximumX2)
                || (isConstantX2 == false && newX2 == maximumX2)) { boundaryReachedX2 = 1; }
        if ((newX3 > maximumX3)
                || (isConstantX3 == false && newX3 == maximumX3)) { boundaryReachedX3 = 1; }

        if (boundaryReachedX1 != 0) {
            BoundaryHandlerValues boundaryHandlerValues = processBoundary(
                    boundaryReachedX1
                    , newX1
                    , minimumX1
                    , maximumX1
                    , boundaryEffectX1);
            newX1 = boundaryHandlerValues.newValue;
            bounceX1 = boundaryHandlerValues.bounceValue;
        }

        if (boundaryReachedX2 != 0) {
            BoundaryHandlerValues boundaryHandlerValues = processBoundary(
                    boundaryReachedX2
                    , newX2
                    , minimumX2
                    , maximumX2
                    , boundaryEffectX2);
            newX2 = boundaryHandlerValues.newValue;
            bounceX2 = boundaryHandlerValues.bounceValue;
        }

        if (boundaryReachedX3 != 0) {
            BoundaryHandlerValues boundaryHandlerValues = processBoundary(
                    boundaryReachedX3
                    , newX3
                    , minimumX3
                    , maximumX3
                    , boundaryEffectX3);
            newX3 = boundaryHandlerValues.newValue;
            bounceX3 = boundaryHandlerValues.bounceValue;
        }

        if (positionEvolverDXdt != null &&
                (bounceX1
                || bounceX2
                || bounceX3)) {
            positionEvolverDXdt.bounce(
                    mode
                    , bounceX1
                    , bounceX2
                    , bounceX3);
        }

        X = new PositionVector(newX1, newX2, newX3);

    }

    public void bounce(
            MODE mode
            , boolean bounceX1
            , boolean bounceX2
            , boolean bounceX3) {

        if (this.mode == mode) {
            X = new PositionVector(
                    (bounceX1)? X.X1 * -1.0 : X.X1
                    , (bounceX2)? X.X2 * -1.0 : X.X2
                    , (bounceX3)? X.X3 * -1.0 : X.X3);
        }

        else if (this.mode == MODE.POLAR_2D && mode == MODE.CARTESIAN_2D) {
            double newX2 = X.X2;
            if (bounceX1) {
                newX2 = Math.PI - X.X2;
            }
            if (bounceX2) {
                newX2 *= -1.0;
            }
            X = new PositionVector(
                    X.X1
                    , newX2
                    , 0.0);
        }

    }

    public enum MODE {
        ONE_DIMENSION
        , CARTESIAN_2D
        , CARTESIAN_3D
        , POLAR_2D
        , SPHERICAL_3D
    }

    public enum BOUNDARY_EFFECT {
        STICK
        , BOUNCE
        , PERIODIC
        , PERIODIC_REFLECTIVE
    }

    private BoundaryHandlerValues processBoundary(
            int boundaryReached
            , double currentValue
            , double minimumValue
            , double maximumValue
            , BOUNDARY_EFFECT boundaryEffect) {

        double newValue = currentValue;
        boolean bounce = false;

        double valueRange = maximumValue - minimumValue;

        switch (boundaryEffect) {
            case STICK:
                newValue = (boundaryReached == -1) ? minimumValue : maximumValue;
                break;

            case BOUNCE:
                double boundaryOverflow = (boundaryReached == 1) ?
                        newValue - maximumValue
                        : minimumValue - newValue;

                int numberOfBounces = (int) Math.floor(boundaryOverflow / valueRange) + 1;
                double remainingOverflow = boundaryOverflow % valueRange;

                if (numberOfBounces % 2 == 0) {
                    newValue = (boundaryReached == 1) ?
                            minimumValue + remainingOverflow
                            : maximumValue - remainingOverflow;
                } else {
                    newValue = (boundaryReached == 1) ?
                            maximumValue - remainingOverflow
                            : minimumValue + remainingOverflow;
                    bounce = true;
                }

                break;

            case PERIODIC:
                if (boundaryReached == 1) {
                    newValue = minimumValue + ((newValue - maximumValue) % valueRange);
                } else {
                    newValue = maximumValue - ((minimumValue - newValue) % valueRange);
                }
                break;

            case PERIODIC_REFLECTIVE:
                double overflow = (boundaryReached == 1) ?
                        newValue - maximumValue
                        : minimumValue - newValue;
                int numberOfReflectionsCrossed = (int) Math.floor(overflow / valueRange) + 1;
                double remainingOverlap = overflow % valueRange;
                if (numberOfReflectionsCrossed % 2 == 1) {
                    newValue = (boundaryReached == 1) ?
                            maximumValue - remainingOverlap
                            : minimumValue + remainingOverlap;
                } else {
                    newValue = (boundaryReached == 1) ?
                            minimumValue + remainingOverlap
                            : maximumValue - remainingOverlap;
                }
                break;

            default: // STICK
                newValue = (boundaryReached == -1) ? minimumValue : maximumValue;
        }
        return new BoundaryHandlerValues(
                newValue,
                bounce);
    }

    private class BoundaryHandlerValues {
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
