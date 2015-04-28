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

    private boolean mirrorAbsoluteValueBoundariesX1 = false;
    private boolean mirrorAbsoluteValueBoundariesX2 = false;
    private boolean mirrorAbsoluteValueBoundariesX3 = false;

    private boolean canRandomlyChangePosition = false;
    private double probabilityOfRandomPositionChangePerSecond = 0.00;

    private boolean canRandomlyChangeX1 = false;
    private boolean canRandomlyChangeX2 = false;
    private boolean canRandomlyChangeX3 = false;
    private double probabilityOfRandomChangePerSecondX1 = 0.00;
    private double probabilityOfRandomChangePerSecondX2 = 0.00;
    private double probabilityOfRandomChangePerSecondX3 = 0.00;

    private boolean tieNewRandomX1ToNewRandomX2 = false;
    private boolean tieNewRandomX2ToNewRandomX1 = false;
    private boolean tieNewRandomX1ToNewRandomDX1 = false;
    private boolean tieNewRandomX1ToNewRandomDX2 = false;
    private boolean tieNewRandomX2ToNewRandomDX1 = false;
    private boolean tieNewRandomX2ToNewRandomDX2 = false;
    private boolean tieNewRandomX3ToNewRandomDX3 = false;

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
            , boolean mirrorAbsoluteValueBoundariesX1
            , boolean mirrorAbsoluteValueBoundariesX2
            , boolean mirrorAbsoluteValueBoundariesX3
            , boolean canRandomlyChangePosition
            , double probabilityOfRandomPositionChangePerSecond
            , boolean canRandomlyChangeX1
            , boolean canRandomlyChangeX2
            , boolean canRandomlyChangeX3
            , double probabilityOfRandomChangePerSecondX1
            , double probabilityOfRandomChangePerSecondX2
            , double probabilityOfRandomChangePerSecondX3
            , boolean tieNewRandomX1ToNewRandomX2
            , boolean tieNewRandomX2ToNewRandomX1
            , boolean tieNewRandomX1ToNewRandomDX1
            , boolean tieNewRandomX1ToNewRandomDX2
            , boolean tieNewRandomX2ToNewRandomDX1
            , boolean tieNewRandomX2ToNewRandomDX2
            , boolean tieNewRandomX3ToNewRandomDX3
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

        this.mirrorAbsoluteValueBoundariesX1 = mirrorAbsoluteValueBoundariesX1;
        this.mirrorAbsoluteValueBoundariesX2 = mirrorAbsoluteValueBoundariesX2;
        this.mirrorAbsoluteValueBoundariesX3 = mirrorAbsoluteValueBoundariesX3;

        this.canRandomlyChangePosition = canRandomlyChangePosition;
        this.probabilityOfRandomPositionChangePerSecond = probabilityOfRandomPositionChangePerSecond;

        this.canRandomlyChangeX1 = canRandomlyChangeX1;
        this.canRandomlyChangeX2 = canRandomlyChangeX2;
        this.canRandomlyChangeX3 = canRandomlyChangeX3;
        this.probabilityOfRandomChangePerSecondX1 = probabilityOfRandomChangePerSecondX1;
        this.probabilityOfRandomChangePerSecondX2 = probabilityOfRandomChangePerSecondX2;
        this.probabilityOfRandomChangePerSecondX3 = probabilityOfRandomChangePerSecondX3;

        this.tieNewRandomX1ToNewRandomX2 = tieNewRandomX1ToNewRandomX2;
        this.tieNewRandomX2ToNewRandomX1 = tieNewRandomX2ToNewRandomX1;
        this.tieNewRandomX1ToNewRandomDX1 = tieNewRandomX1ToNewRandomDX1;
        this.tieNewRandomX1ToNewRandomDX2 = tieNewRandomX1ToNewRandomDX2;
        this.tieNewRandomX2ToNewRandomDX1 = tieNewRandomX2ToNewRandomDX1;
        this.tieNewRandomX2ToNewRandomDX2 = tieNewRandomX2ToNewRandomDX2;
        this.tieNewRandomX3ToNewRandomDX3 = tieNewRandomX3ToNewRandomDX3;

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

    public void generateNewRandomPosition(
            boolean generateNewRandomX1
            , boolean generateNewRandomX2
            , boolean generateNewRandomX3) {

        if (generateNewRandomX1
                || generateNewRandomX2
                || generateNewRandomX3) {

            if (generateNewRandomX1) {
                if (tieNewRandomX1ToNewRandomX2) {
                    generateNewRandomX2 = true;
                }
            }

            if (generateNewRandomX2) {
                if (tieNewRandomX2ToNewRandomX1) {
                    generateNewRandomX1 = true;
                }
            }

            double newX1 = X.X1;
            double newX2 = X.X2;
            double newX3 = X.X3;
            if (generateNewRandomX1) {
                newX1 = minimumX1 + (Math.random() * (maximumX1 - minimumX1));

                if (mirrorAbsoluteValueBoundariesX1) {
                    newX1 *= (Math.random() <= 0.5) ? 1 : -1;
                }
            }
            if (generateNewRandomX2) {
                newX2 = minimumX2 + (Math.random() * (maximumX2 - minimumX2));

                if (mirrorAbsoluteValueBoundariesX2) {
                    newX2 *= (Math.random() <= 0.5) ? 1 : -1;
                }
            }
            if (generateNewRandomX3) {
                newX3 = minimumX3 + (Math.random() * (maximumX3 - minimumX3));

                if (mirrorAbsoluteValueBoundariesX3) {
                    newX3 *= (Math.random() <= 0.5) ? 1 : -1;
                }
            }
            X = new PositionVector(newX1, newX2, newX3);

            if (positionEvolverDXdt != null) {

                boolean generateNewRandomDX1 = false;
                boolean generateNewRandomDX2 = false;
                boolean generateNewRandomDX3 = false;

                if (generateNewRandomX1) {
                    if (tieNewRandomX1ToNewRandomDX1) {
                        generateNewRandomDX1 = true;
                    }
                    if (tieNewRandomX1ToNewRandomDX2) {
                        generateNewRandomDX2 = true;
                    }
                }
                if (generateNewRandomX2) {
                    if (tieNewRandomX2ToNewRandomDX1) {
                        generateNewRandomDX1 = true;
                    }
                    if (tieNewRandomX2ToNewRandomDX2) {
                        generateNewRandomDX2 = true;
                    }
                }
                if (generateNewRandomX3 && tieNewRandomX3ToNewRandomDX3) {
                    generateNewRandomDX3 = true;

                    if (tieNewRandomX3ToNewRandomDX3) {
                        generateNewRandomDX3 = true;
                    }
                }

                if (generateNewRandomDX1
                        || generateNewRandomDX2
                        || generateNewRandomDX3) {

                    positionEvolverDXdt.generateNewRandomPosition(
                            generateNewRandomDX1
                            , generateNewRandomDX2
                            , generateNewRandomDX3);
                }

            }

        }

    }

    public void evolveTime(
            double dt
            , boolean evolveX1
            , boolean evolveX2
            , boolean evolveX3) {

        boolean generateNewRandomX1 = false;
        boolean generateNewRandomX2 = false;
        boolean generateNewRandomX3 = false;

        if (canRandomlyChangePosition) {

            // Q = 1 - (1 - p)^(1 / N)
            // Q = probability of change in partial unit
            // P = probability of change in one unit
            // N = number of partial units in one unit
            // N = 1 second / time elapsed since last update in seconds
            double probabilityThreshold = 1.0 - Math.pow(1.0 - probabilityOfRandomPositionChangePerSecond, dt);
            double positionChangeCheck = Math.random();
            if (positionChangeCheck < probabilityThreshold) {
                generateNewRandomX1 = evolveX1;
                generateNewRandomX2 = evolveX2;
                generateNewRandomX3 = evolveX3;
            }

        } else {

            if (canRandomlyChangeX1) {
                double probabilityThreshold = 1.0 - Math.pow(1.0 - probabilityOfRandomChangePerSecondX1, dt);
                double changeCheckX1 = Math.random();
                if (changeCheckX1 < probabilityThreshold) {
                    generateNewRandomX1 = evolveX1;
                }
            }
            if (canRandomlyChangeX2) {
                double probabilityThreshold = 1.0 - Math.pow(1.0 - probabilityOfRandomChangePerSecondX2, dt);
                double changeCheckX2 = Math.random();
                if (changeCheckX2 < probabilityThreshold) {
                    generateNewRandomX2 = evolveX2;
                }
            }
            if (canRandomlyChangeX3) {
                double probabilityThreshold = 1.0 - Math.pow(1.0 - probabilityOfRandomChangePerSecondX3, dt);
                double changeCheckX3 = Math.random();
                if (changeCheckX3 < probabilityThreshold) {
                    generateNewRandomX3 = evolveX3;
                }
            }

        }

        if (generateNewRandomX1
                || generateNewRandomX2
                || generateNewRandomX3) {
            generateNewRandomPosition(generateNewRandomX1, generateNewRandomX2, generateNewRandomX3);
        }

        PositionVector dXdt = null;
        double dX1 = 0.0;
        double dX2 = 0.0;
        double dX3 = 0.0;
        if (positionEvolverDXdt != null) {
            dXdt = positionEvolverDXdt.getX(mode);
            if (!isConstantX1
                    && evolveX1
                    && !generateNewRandomX1) {
                dX1 = dXdt.X1 * dt;
            }
            if (!isConstantX2
                    && evolveX2
                    && !generateNewRandomX2) {
                dX2 = dXdt.X2 * dt;
            }
            if (!isConstantX3
                    && evolveX3
                    && !generateNewRandomX3) {
                dX3 = dXdt.X3 * dt;
            }

            boolean generatedNewRandomDX1 = generateNewRandomX1 && tieNewRandomX1ToNewRandomDX1;
            boolean generatedNewRandomDX2 = generateNewRandomX2 && tieNewRandomX2ToNewRandomDX2;
            boolean generatedNewRandomDX3 = generateNewRandomX3 && tieNewRandomX3ToNewRandomDX3;
                positionEvolverDXdt.evolveTime(
                        dt
                        , evolveX1 && !generatedNewRandomDX1
                        , evolveX2 && !generatedNewRandomDX2
                        , evolveX3 && !generatedNewRandomDX3);

        }

        double newX1 = X.X1 + dX1;
        double newX2 = X.X2 + dX2;
        double newX3 = X.X3 + dX3;

        int boundaryReachedX1 = checkBoundaryReached(newX1, dX1, minimumX1, maximumX1, isConstantX1, mirrorAbsoluteValueBoundariesX1);
        int boundaryReachedX2 = checkBoundaryReached(newX2, dX2, minimumX2, maximumX2, isConstantX2, mirrorAbsoluteValueBoundariesX2);
        int boundaryReachedX3 = checkBoundaryReached(newX3, dX2, minimumX3, maximumX3, isConstantX3, mirrorAbsoluteValueBoundariesX3);

        boolean bounceX1 = false;
        boolean bounceX2 = false;
        boolean bounceX3 = false;

        if (boundaryReachedX1 != 0) {
            BoundaryHandlerValues boundaryHandlerValues = processBoundary(
                    boundaryReachedX1
                    , newX1
                    , minimumX1
                    , maximumX1
                    , mirrorAbsoluteValueBoundariesX1
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
                    , mirrorAbsoluteValueBoundariesX2
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
                    , mirrorAbsoluteValueBoundariesX3
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

    private int checkBoundaryReached(
            double X
            , double DX
            , double minimumX
            , double maximumX
            , boolean isConstantX
            , boolean mirrorAbsoluteValueBoundariesX) {

        // Initialize if we reached a boundary
        int boundaryReached = 0;

        // Check if the boundaries are absolute values that are mirrored positive and negative
        if (mirrorAbsoluteValueBoundariesX) {

            double negativeMinimumX = -1.0 * maximumX;
            double negativeMaximumX = -1.0 * minimumX;

            if (DX < 0.0 && (
                    (X < negativeMinimumX)
                    || (!isConstantX && X == negativeMinimumX))) {
                boundaryReached = -2;
            }

            if (DX > 0.0 && (
                    (X > maximumX)
                    || (!isConstantX && X == maximumX))) {
                boundaryReached = 2;
            }

            if (DX < 0.0 && (
                    (X > negativeMaximumX && X < minimumX)
                    || (!isConstantX && X == minimumX))) {
                boundaryReached = 1;
            }

            if (DX > 0.0 && (
                    (X > negativeMaximumX && X < minimumX)
                    || (!isConstantX && X == negativeMaximumX))) {
                boundaryReached = -1;
            }

        } else {

            if (DX < 0.0 && (
                    (X < minimumX)
                    || (!isConstantX && X == minimumX))) {
                boundaryReached = -1;
            }

            if (DX > 0.0 && (
                    (X > maximumX)
                    || (!isConstantX && X == maximumX))) {
                boundaryReached = 1;
            }

        }

        return boundaryReached;
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
            , boolean mirrorAbsoluteValueBoundaries
            , BOUNDARY_EFFECT boundaryEffect) {

        double newValue = currentValue;
        boolean bounce = false;

        double valueRange = maximumValue - minimumValue;

        int typeOfBoundaryReached = boundaryReached;
        double minimumValueToUse = minimumValue;
        double maximumValueToUse = maximumValue;
        double otherRegionMinimumValue = -1.0 * maximumValue;
        double otherRegionMaximumValue = -1.0 * minimumValue;
        if (mirrorAbsoluteValueBoundaries) {
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

        switch (boundaryEffect) {
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
                if (mirrorAbsoluteValueBoundaries) {

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
                }

                break;

            default: // STICK
                newValue = (typeOfBoundaryReached == -1) ? minimumValueToUse : maximumValueToUse;
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