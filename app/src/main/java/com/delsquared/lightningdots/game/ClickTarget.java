package com.delsquared.lightningdots.game;


import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import com.delsquared.lightningdots.utilities.Polygon;
import com.delsquared.lightningdots.utilities.PolygonHelper;
import com.delsquared.lightningdots.utilities.PositionEvolver;
import com.delsquared.lightningdots.utilities.PositionVector;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

import java.util.ArrayList;

public class ClickTarget {

    private ClickTargetProfileScript clickTargetProfileScript;
    private PositionEvolver positionEvolverXPixels;
	private PositionEvolver positionEvolverRadiusPixels;
    private Polygon polygonTargetShape = null;

    private double canvasWidth = 1.0;
    private double canvasHeight = 1.0;

    public ClickTarget() {

        clickTargetProfileScript = new ClickTargetProfileScript();

        positionEvolverXPixels = new PositionEvolver(
                ""
                , new PositionVector(0.0, 0.0, 0.0)
                , PositionEvolver.MODE.CARTESIAN_2D
                , true
                , true
                , true
                , null
                , 0.0
                , 0.0
                , 0.0
                , 0.0
                , 0.0
                , 0.0
                , false
                , 0.0
                , PositionEvolver.RANDOM_CHANGE_INTERVAL.CONSTANT
                , false
                , false
                , false
                , 0.0
                , 0.0
                , 0.0
                , PositionEvolver.RANDOM_CHANGE_INTERVAL.CONSTANT
                , PositionEvolver.RANDOM_CHANGE_INTERVAL.CONSTANT
                , PositionEvolver.RANDOM_CHANGE_INTERVAL.CONSTANT
                , false
                , false
                , false
                , false
                , false
                , false
                , false
                , new PositionEvolver.BoundaryEffect()
                , new PositionEvolver.BoundaryEffect()
                , new PositionEvolver.BoundaryEffect());
        positionEvolverRadiusPixels = new PositionEvolver(
                ""
                , new PositionVector(0.0, 0.0, 0.0)
                , PositionEvolver.MODE.ONE_DIMENSION
                , true
                , true
                , true
                , null
                , 0.0
                , 0.0
                , 0.0
                , 0.0
                , 0.0
                , 0.0
                , false
                , 0.0
                , PositionEvolver.RANDOM_CHANGE_INTERVAL.CONSTANT
                , false
                , false
                , false
                , 0.0
                , 0.0
                , 0.0
                , PositionEvolver.RANDOM_CHANGE_INTERVAL.CONSTANT
                , PositionEvolver.RANDOM_CHANGE_INTERVAL.CONSTANT
                , PositionEvolver.RANDOM_CHANGE_INTERVAL.CONSTANT
                , false
                , false
                , false
                , false
                , false
                , false
                , false
                , new PositionEvolver.BoundaryEffect()
                , new PositionEvolver.BoundaryEffect()
                , new PositionEvolver.BoundaryEffect());
    }

	public ClickTarget(
            Context context
            , ClickTargetProfileScript clickTargetProfileScript
            , double canvasWidth
            , double canvasHeight)
	{
        this.clickTargetProfileScript = clickTargetProfileScript;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;

        // Initialize the click target to its current profile
        transitionClickTargetProfile(
                context
                , true);
	}

    public PositionVector getXPixels() {
        if (positionEvolverXPixels == null)
            return null;
        return positionEvolverXPixels.getX();
    }
    public double getRadiusPixels() {
        if (positionEvolverRadiusPixels == null)
            return 0.0;
        return positionEvolverRadiusPixels.getX().X1;
    }

    public ArrayList<PositionVector> getArrayListCenterPoints() {

        // Initialize the result
        ArrayList<PositionVector> arrayListCenterPoints = new ArrayList<>();

        // Check if the position evolver exists
        if (positionEvolverXPixels == null) {
            return arrayListCenterPoints;
        }

        // Add the true center point
        arrayListCenterPoints.add(new PositionVector(positionEvolverXPixels.getX()));

        // Check if either X or Y has periodic boundary effect
        // If not, then there won't be any secondary points
        if (positionEvolverXPixels.getBoundaryEffectX1().boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.PERIODIC
                || positionEvolverXPixels.getBoundaryEffectX1().boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.PERIODIC_REFLECTIVE
                || positionEvolverXPixels.getBoundaryEffectX2().boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.PERIODIC
                || positionEvolverXPixels.getBoundaryEffectX2().boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.PERIODIC_REFLECTIVE) {

            double targetX = positionEvolverXPixels.getX().X1;
            double targetY = positionEvolverXPixels.getX().X2;
            double radius = getRadiusPixels();
            double minimumX = positionEvolverXPixels.getMinimumX1();
            double maximumX = positionEvolverXPixels.getMaximumX1();
            double minimumY = positionEvolverXPixels.getMinimumX2();
            double maximumY = positionEvolverXPixels.getMaximumX2();
            double widthX = maximumX - minimumX;
            double heightY = maximumY - minimumY;
            boolean minimumBoundaryReachedX = false;
            boolean maximumBoundaryReachedX = false;
            boolean minimumBoundaryReachedY = false;
            boolean maximumBoundaryReachedY = false;
            double radiusMinimumX = targetX - radius;
            double radiusMaximumX = targetX + radius;
            double radiusMinimumY = targetY - radius;
            double radiusMaximumY = targetY + radius;
            if (radiusMinimumX < minimumX) minimumBoundaryReachedX = true;
            if (radiusMaximumX > maximumX) maximumBoundaryReachedX = true;
            if (radiusMinimumY < minimumY) minimumBoundaryReachedY = true;
            if (radiusMaximumY > maximumY) maximumBoundaryReachedY = true;

            double newTargetX1 = targetX;
            double newTargetX2 = targetX;
            double newTargetY1 = targetY;
            double newTargetY2 = targetY;

            if (minimumBoundaryReachedX || maximumBoundaryReachedX) {

                if (positionEvolverXPixels.getBoundaryEffectX1().boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.PERIODIC) {

                    if (minimumBoundaryReachedX) {
                        double overflow = (minimumX - radiusMinimumX) % widthX;
                        newTargetX1 = maximumX - overflow + radius;
                    }

                    if (maximumBoundaryReachedX) {
                        double overflow = (radiusMaximumX - maximumX) % widthX;
                        newTargetX2 = minimumX + overflow - radius;
                    }

                } else if (positionEvolverXPixels.getBoundaryEffectX1().boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.PERIODIC_REFLECTIVE) {

                    if (minimumBoundaryReachedX) {
                        double boundaryOverflow = minimumX - radiusMinimumX;
                        double overflow = boundaryOverflow % widthX;
                        int numberOfBoundariesReached = (int) Math.floor(boundaryOverflow / widthX) + 1;

                        if (numberOfBoundariesReached % 2 == 0) {
                            newTargetX1 = maximumX - overflow + radius;
                        } else {
                            newTargetX1 = minimumX + overflow - radius;
                        }
                    }

                    if (maximumBoundaryReachedX) {
                        double boundaryOverflow = radiusMaximumX - maximumX;
                        double overflow = boundaryOverflow % widthX;
                        int numberOfBoundariesReached = (int) Math.floor(boundaryOverflow / widthX) + 1;

                        if (numberOfBoundariesReached % 2 == 0) {
                            newTargetX2 = minimumX + overflow - radius;
                        } else {
                            newTargetX2 = maximumX - overflow + radius;
                        }
                    }

                }

            }

            if (minimumBoundaryReachedY || maximumBoundaryReachedY) {

                if (positionEvolverXPixels.getBoundaryEffectX2().boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.PERIODIC) {

                    if (minimumBoundaryReachedY) {
                        double overflow = (minimumY - radiusMinimumY) % heightY;
                        newTargetY1 = maximumY - overflow + radius;
                    }

                    if (maximumBoundaryReachedY) {
                        double overflow = (radiusMaximumY - maximumY) % heightY;
                        newTargetY2 = minimumY + overflow - radius;
                    }

                } else if (positionEvolverXPixels.getBoundaryEffectX2().boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.PERIODIC_REFLECTIVE) {

                    if (minimumBoundaryReachedY) {
                        double boundaryOverflow = minimumY - radiusMinimumY;
                        double overflow = boundaryOverflow % heightY;
                        int numberOfBoundariesReached = (int) Math.floor(boundaryOverflow / heightY) + 1;

                        if (numberOfBoundariesReached % 2 == 0) {
                            newTargetY1 = maximumY - overflow + radius;
                        } else {
                            newTargetY1 = minimumY + overflow - radius;
                        }
                    }

                    if (maximumBoundaryReachedY) {
                        double boundaryOverflow = radiusMaximumY - maximumY;
                        double overflow = boundaryOverflow % heightY;
                        int numberOfBoundariesReached = (int) Math.floor(boundaryOverflow / heightY) + 1;

                        if (numberOfBoundariesReached % 2 == 0) {
                            newTargetY2 = minimumY + overflow - radius;
                        } else {
                            newTargetY2 = maximumY - overflow + radius;
                        }
                    }

                }
            }

            if (minimumBoundaryReachedX) {
                // newTargetX1, targetY;
                arrayListCenterPoints.add(new PositionVector(newTargetX1, targetY));
            }
            if (maximumBoundaryReachedX) {
                //newTargetX2, targetY;
                arrayListCenterPoints.add(new PositionVector(newTargetX2, targetY));
            }
            if (minimumBoundaryReachedY) {
                //targetX, newTargetY1;
                arrayListCenterPoints.add(new PositionVector(targetX, newTargetY1));
            }
            if (maximumBoundaryReachedY) {
                //targetX, newTargetY2;
                arrayListCenterPoints.add(new PositionVector(targetX, newTargetY2));
            }
            if (minimumBoundaryReachedX && minimumBoundaryReachedY) {
                //newTargetX1, newTargetY1;
                arrayListCenterPoints.add(new PositionVector(newTargetX1, newTargetY1));
            }
            if (minimumBoundaryReachedX && maximumBoundaryReachedY) {
                //newTargetX1, newTargetY2;
                arrayListCenterPoints.add(new PositionVector(newTargetX1, newTargetY2));
            }
            if (maximumBoundaryReachedX && minimumBoundaryReachedY) {
                //newTargetX2, newTargetY1;
                arrayListCenterPoints.add(new PositionVector(newTargetX2, newTargetY1));
            }
            if (maximumBoundaryReachedX && maximumBoundaryReachedY) {
                //newTargetX2, newTargetY2;
                arrayListCenterPoints.add(new PositionVector(newTargetX2, newTargetY2));
            }

        }

        return arrayListCenterPoints;

    }

    public void setCanvasWidthAndHeight(double canvasWidth, double canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        resetPositionBoundary();
    }

    public boolean pointIsInsideTarget(double pointX, double pointY) {

        boolean returnValue = false;

        if (positionEvolverXPixels == null)
            return false;

        // Get the target radius
        double radius = getRadiusPixels();

        // Get the list of center points
        ArrayList<PositionVector> arrayListCenterPoints = getArrayListCenterPoints();

        // Loop through the center points
        for (PositionVector currentCenterPoint : arrayListCenterPoints) {

            // Check if the target is a polygon
            if (polygonTargetShape != null) {

                // Translate the polygon to the current center point
                Polygon currentPolygon = polygonTargetShape.duplicate();
                currentPolygon.setCenterPoint(currentCenterPoint);

                // Check if the point is inside the polygon
                returnValue = currentPolygon.pointIsInsidePolygon(pointX, pointY);

            } else { // The target is a circle

                double dx = currentCenterPoint.X1 - pointX;
                double dy = currentCenterPoint.X2 - pointY;

                returnValue = (dx * dx) + (dy * dy) <= (radius * radius);

            }

            if (returnValue) break;

        }

        return returnValue;
    }

    public void resetPositionBoundary() {

        if (positionEvolverXPixels == null) {
            return;
        }

        double radius = getRadiusPixels();

        double minimumX = 0.0;
        double maximumX = canvasWidth;
        double minimumY = 0.0;
        double maximumY = canvasHeight;

        if (positionEvolverXPixels.getBoundaryEffectX1().boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.BOUNCE
                || positionEvolverXPixels.getBoundaryEffectX1().boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.STICK) {
            minimumX = radius / 2.0;
            maximumX = canvasWidth - (radius / 2.0);
        }
        if (positionEvolverXPixels.getBoundaryEffectX2().boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.BOUNCE
                || positionEvolverXPixels.getBoundaryEffectX2().boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.STICK) {
            minimumY = radius / 2.0;
            maximumY = canvasHeight - (radius / 2.0);
        }
        positionEvolverXPixels.setBoundaryValues(
                minimumX
                , minimumY
                , 0.0
                , maximumX
                , maximumY
                , 0.0);
    }

	public void processElapsedTimeMillis(
			Context context
			, long timeElapsedSinceLastUpdateMillis) {

		// Get the resources
		Resources resources = context.getResources();

		double timeElapsedSinceLastUpdateSeconds = timeElapsedSinceLastUpdateMillis / 1000.0;

        if (positionEvolverXPixels != null) {

            // Evolve the position and radius
            positionEvolverXPixels.evolveTime(timeElapsedSinceLastUpdateSeconds, true, true, true);
            positionEvolverRadiusPixels.evolveTime(timeElapsedSinceLastUpdateSeconds, true, true, true);

            // Update the polygon
            if (polygonTargetShape != null) {
                if (Double.isNaN(positionEvolverRadiusPixels.getX().X2)) {
                    int blah = 0;
                    blah++;
                }
                polygonTargetShape.setProperties(
                        new PositionVector(positionEvolverXPixels.getX())
                        , positionEvolverRadiusPixels.getX().X1
                        , positionEvolverRadiusPixels.getX().X2
                );
            }

        }

        // Process the click target profile transition
        boolean transitionPerformed = clickTargetProfileScript.processTransition(timeElapsedSinceLastUpdateMillis);
        if (transitionPerformed) {

            // Transition the click target to the new profile
            transitionClickTargetProfile(
                    context
                    , false);
        }

	}

	public ClickTargetSnapshot getClickTargetSnapshot() {
        PositionVector targetX = new PositionVector();
        PositionVector targetDX = new PositionVector();
        PositionVector targetD2X = new PositionVector();
        PositionVector targetRadiusX = new PositionVector();
        PositionVector targetDRadius = new PositionVector();
        PositionVector targetD2Radius = new PositionVector();
        if (positionEvolverXPixels != null) {
            targetX = positionEvolverXPixels.getX();
            if (positionEvolverXPixels.getDXdt() != null) {
                targetDX = positionEvolverXPixels.getDXdt();
            }
            if (positionEvolverXPixels.getPositionEvolverDXdt() != null
                    && positionEvolverXPixels.getPositionEvolverDXdt().getPositionEvolverDXdt() != null) {
                targetD2X = positionEvolverXPixels.getPositionEvolverDXdt().getPositionEvolverDXdt().getX();
            }

        }
        if (positionEvolverRadiusPixels != null) {
            targetRadiusX = positionEvolverRadiusPixels.getX();
            if (positionEvolverRadiusPixels.getDXdt() != null) {
                targetDRadius = positionEvolverRadiusPixels.getDXdt();
            }
            if (positionEvolverRadiusPixels.getPositionEvolverDXdt() != null
                    && positionEvolverRadiusPixels.getPositionEvolverDXdt().getPositionEvolverDXdt() != null) {
                targetD2Radius = positionEvolverRadiusPixels.getPositionEvolverDXdt().getPositionEvolverDXdt().getX();
            }
        }

		return new ClickTargetSnapshot(
                targetX
                , targetDX
                , targetD2X
                , targetRadiusX
                , targetDRadius
                , targetD2Radius
                , getArrayListCenterPoints()
                , polygonTargetShape
        );
	}

    public void transitionClickTargetProfile(
            Context context
            , boolean initializeClickTarget) {

        ClickTargetProfile clickTargetProfile = clickTargetProfileScript.getCurrentClickTargetProfile();

        if (clickTargetProfile == null) {
            return;
        }

        // Determine continuity
        boolean continuousX = false;
        boolean continuousSpeed = false;
        boolean continuousDirection = false;
        boolean continuousSpeedChange = false;
        boolean continuousDirectionChange = false;
        boolean continuousRadius = false;
        boolean continuousDRadius = false;
        boolean continuousD2Radius = false;
        boolean continuousRotation = false;
        boolean continuousDRotation = false;
        boolean continuousD2Rotation = false;

        // If we are not initializing the click target, then determine the continuity of the values
        // (if we are initializing, then all need to be discontinuously initialized)
        if (initializeClickTarget == false) {

            switch (clickTargetProfile.targetPositionHorizontalValuesInches.transitionContinuity) {
                case CONTINUOUS:
                    continuousX = true;
                    break;
                case DISCONTINUOUS:
                    continuousX = false;
                    break;
                default:
                    continuousX = clickTargetProfile.targetPositionHorizontalValuesInches.canChange;

            }
            switch (clickTargetProfile.targetSpeedValuesInchesPerSecond.transitionContinuity) {
                case CONTINUOUS:
                    continuousSpeed = true;
                    break;
                case DISCONTINUOUS:
                    continuousSpeed = false;
                    break;
                default:
                    continuousSpeed = clickTargetProfile.targetSpeedValuesInchesPerSecond.canChange;
            }
            switch (clickTargetProfile.targetDirectionAngleValuesRadians.transitionContinuity) {
                case CONTINUOUS:
                    continuousDirection = true;
                    break;
                case DISCONTINUOUS:
                    continuousDirection = false;
                    break;
                default:
                    continuousDirection = clickTargetProfile.targetDirectionAngleValuesRadians.canChange;
            }
            switch (clickTargetProfile.targetDSpeedValuesInchesPerSecondPerSecond.transitionContinuity) {
                case CONTINUOUS:
                    continuousSpeedChange = true;
                    break;
                case DISCONTINUOUS:
                    continuousSpeedChange = false;
                    break;
            }
            switch (clickTargetProfile.targetDDirectionValuesRadiansPerSecond.transitionContinuity) {
                case CONTINUOUS:
                    continuousDirectionChange = true;
                    break;
                case DISCONTINUOUS:
                    continuousDirectionChange = false;
                    break;
            }
            switch (clickTargetProfile.targetRadiusValuesInches.transitionContinuity) {
                case CONTINUOUS:
                    continuousRadius = true;
                    break;
                case DISCONTINUOUS:
                    continuousRadius = false;
                    break;
                default:
                    continuousRadius = clickTargetProfile.targetRadiusValuesInches.canChange;
            }
            switch (clickTargetProfile.targetDRadiusValuesInchesPerSecond.transitionContinuity) {
                case CONTINUOUS:
                    continuousDRadius = true;
                    break;
                case DISCONTINUOUS:
                    continuousDRadius = false;
                    break;
                default:
                    continuousDRadius = clickTargetProfile.targetDRadiusValuesInchesPerSecond.canChange;
            }
            switch (clickTargetProfile.targetD2RadiusValuesInchesPerSecondPerSecond.transitionContinuity) {
                case CONTINUOUS:
                    continuousD2Radius = true;
                    break;
                case DISCONTINUOUS:
                    continuousD2Radius = false;
                    break;
            }
            switch (clickTargetProfile.targetRotationValuesRadians.transitionContinuity) {
                case CONTINUOUS:
                    continuousRotation = true;
                    break;
                case DISCONTINUOUS:
                    continuousRotation = false;
                    break;
            }
            switch (clickTargetProfile.targetDRotationValuesRadiansPerSecond.transitionContinuity) {
                case CONTINUOUS:
                    continuousDRotation = true;
                    break;
                case DISCONTINUOUS:
                    continuousDRotation = false;
                    break;
            }
            switch (clickTargetProfile.targetD2RotationValuesRadiansPerSecondPerSecond.transitionContinuity) {
                case CONTINUOUS:
                    continuousD2Rotation = true;
                    break;
                case DISCONTINUOUS:
                    continuousD2Rotation = false;
                    break;
            }

        }

        // Convert values from inches to pixels
        // Radius
        double initialTargetRadiusPixels =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) clickTargetProfile.targetRadiusValuesInches.initialValue
                        , context.getResources().getDisplayMetrics());
        double minimumTargetRadiusPixels =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) clickTargetProfile.targetRadiusValuesInches.minimumValue
                        , context.getResources().getDisplayMetrics());
        double maximumTargetRadiusPixels =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) clickTargetProfile.targetRadiusValuesInches.maximumValue
                        , context.getResources().getDisplayMetrics());

        // Speed and Speed Change
        double initialTargetSpeedPixelsPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) clickTargetProfile.targetSpeedValuesInchesPerSecond.initialValue
                        , context.getResources().getDisplayMetrics());
        double minimumTargetSpeedPixelsPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) clickTargetProfile.targetSpeedValuesInchesPerSecond.minimumValue
                        , context.getResources().getDisplayMetrics());
        double maximumTargetSpeedPixelsPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) clickTargetProfile.targetSpeedValuesInchesPerSecond.maximumValue
                        , context.getResources().getDisplayMetrics());
        double initialTargetSpeedChangeAbsoluteValuePixelsPerSecondPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) clickTargetProfile.targetDSpeedValuesInchesPerSecondPerSecond.initialValue
                        , context.getResources().getDisplayMetrics());
        double minimumTargetSpeedChangeAbsoluteValuePixelsPerSecondPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) clickTargetProfile.targetDSpeedValuesInchesPerSecondPerSecond.minimumValue
                        , context.getResources().getDisplayMetrics());
        double maximumTargetSpeedChangeAbsoluteValuePixelsPerSecondPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) clickTargetProfile.targetDSpeedValuesInchesPerSecondPerSecond.maximumValue
                        , context.getResources().getDisplayMetrics());

        // DRadius and DRadius Change
        double initialTargetDRadiusAbsoluteValuePixelsPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) clickTargetProfile.targetDRadiusValuesInchesPerSecond.initialValue
                        , context.getResources().getDisplayMetrics());
        double minimumTargetDRadiusAbsoluteValuePixelsPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) clickTargetProfile.targetDRadiusValuesInchesPerSecond.minimumValue
                        , context.getResources().getDisplayMetrics());
        double maximumTargetDRadiusAbsoluteValuePixelsPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) clickTargetProfile.targetDRadiusValuesInchesPerSecond.maximumValue
                        , context.getResources().getDisplayMetrics());
        double initialTargetDRadiusChangeAbsoluteValuePixelsPerSecondPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) clickTargetProfile.targetD2RadiusValuesInchesPerSecondPerSecond.initialValue
                        , context.getResources().getDisplayMetrics());
        double minimumTargetDRadiusChangeAbsoluteValuePixelsPerSecondPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) clickTargetProfile.targetD2RadiusValuesInchesPerSecondPerSecond.minimumValue
                        , context.getResources().getDisplayMetrics());
        double maximumTargetDRadiusChangeAbsoluteValuePixelsPerSecondPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) clickTargetProfile.targetD2RadiusValuesInchesPerSecondPerSecond.maximumValue
                        , context.getResources().getDisplayMetrics());


        // Calculate the boundaries
        double minimumPixelsX = 0.0;
        double maximumPixelsX = canvasWidth;
        double minimumPixelsY = 0.0;
        double maximumPixelsY = canvasHeight;
        if (clickTargetProfile.targetPositionHorizontalValuesInches.boundaryEffect.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.BOUNCE
                || clickTargetProfile.targetPositionHorizontalValuesInches.boundaryEffect.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.STICK) {
            minimumPixelsX = initialTargetRadiusPixels / 2.0;
            maximumPixelsX = canvasWidth - (initialTargetRadiusPixels / 2.0);
        }
        if (clickTargetProfile.targetPositionVerticalValuesInches.boundaryEffect.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.BOUNCE
                || clickTargetProfile.targetPositionVerticalValuesInches.boundaryEffect.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.STICK) {
            minimumPixelsY = initialTargetRadiusPixels / 2.0;
            maximumPixelsY = canvasHeight - (initialTargetRadiusPixels / 2.0);
        }

        double width = maximumPixelsX - minimumPixelsX;
        double height = maximumPixelsY - minimumPixelsY;


        double targetSpeedChangePixelsPerSecondPerSecond = 0.0;
        double targetDirectionAngleChangeRadiansPerSecond = 0.0;
        // Speed Change
        if (!initializeClickTarget && continuousSpeedChange == true) {
            if (positionEvolverXPixels != null
                    && positionEvolverXPixels.getPositionEvolverDXdt() != null
                    && positionEvolverXPixels.getPositionEvolverDXdt().getPositionEvolverDXdt() != null) {

                PositionEvolver positionEvolverD2Xdt2Pixels = positionEvolverXPixels.getPositionEvolverDXdt().getPositionEvolverDXdt();
                targetSpeedChangePixelsPerSecondPerSecond = positionEvolverD2Xdt2Pixels.getX().X1;

            }
        } else {
            if (clickTargetProfile.targetSpeedValuesInchesPerSecond.canChange) {

                if (clickTargetProfile.targetDSpeedValuesInchesPerSecondPerSecond.randomInitialValue) {
                    targetSpeedChangePixelsPerSecondPerSecond =
                            UtilityFunctions.generateRandomValue(
                                    minimumTargetSpeedChangeAbsoluteValuePixelsPerSecondPerSecond
                                    , maximumTargetSpeedChangeAbsoluteValuePixelsPerSecondPerSecond
                                    , true);
                } else {
                    targetSpeedChangePixelsPerSecondPerSecond = initialTargetSpeedChangeAbsoluteValuePixelsPerSecondPerSecond;
                    if (clickTargetProfile.targetDSpeedValuesInchesPerSecondPerSecond.randomInitialSign) {
                        targetSpeedChangePixelsPerSecondPerSecond *= UtilityFunctions.getRandomSign();
                    }
                }

            }
        }
        // Direction Angle Change
        if (!initializeClickTarget && continuousDirectionChange) {
            if (positionEvolverXPixels != null
                    && positionEvolverXPixels.getPositionEvolverDXdt() != null
                    && positionEvolverXPixels.getPositionEvolverDXdt().getPositionEvolverDXdt() != null) {

                PositionEvolver positionEvolverD2Xdt2Pixels = positionEvolverXPixels.getPositionEvolverDXdt().getPositionEvolverDXdt();
                targetDirectionAngleChangeRadiansPerSecond = positionEvolverD2Xdt2Pixels.getX().X2;

            }
        } else {
            if (clickTargetProfile.targetDirectionAngleValuesRadians.canChange) {

                if (clickTargetProfile.targetDDirectionValuesRadiansPerSecond.randomInitialValue) {
                    targetDirectionAngleChangeRadiansPerSecond =
                            UtilityFunctions.generateRandomValue(
                                    clickTargetProfile.targetDDirectionValuesRadiansPerSecond.minimumValue
                                    , clickTargetProfile.targetDDirectionValuesRadiansPerSecond.maximumValue
                                    , true);
                } else {
                    targetDirectionAngleChangeRadiansPerSecond = clickTargetProfile.targetDDirectionValuesRadiansPerSecond.initialValue;
                    if (clickTargetProfile.targetDDirectionValuesRadiansPerSecond.randomInitialSign) {
                        targetDirectionAngleChangeRadiansPerSecond *= UtilityFunctions.getRandomSign();
                    }
                }

            }
        }

        PositionEvolver targetD2Xdt2Pixels = new PositionEvolver(

                // Name
                "d2Xdt2"

                // Initial position and coordinate system
                , new PositionVector(
                        targetSpeedChangePixelsPerSecondPerSecond
                        , targetDirectionAngleChangeRadiansPerSecond
                        , 0.0)
                , PositionEvolver.MODE.POLAR_2D

                // Is constant X1, X2, X3
                , true
                , true
                , true

                // Position evolver d2Xdt2
                , null

                // Minimum X1, X2, X3
                , minimumTargetSpeedChangeAbsoluteValuePixelsPerSecondPerSecond
                , clickTargetProfile.targetDDirectionValuesRadiansPerSecond.minimumValue
                , 0.0

                // Maximum X1, X2, X3
                , maximumTargetSpeedChangeAbsoluteValuePixelsPerSecondPerSecond
                , clickTargetProfile.targetDDirectionValuesRadiansPerSecond.maximumValue
                , 0.0

                // Can randomly change position
                , false
                , 0.0
                , PositionEvolver.RANDOM_CHANGE_INTERVAL.CONSTANT

                // Can randomly change X1, X2, X3
                , clickTargetProfile.targetDSpeedValuesInchesPerSecondPerSecond.canRandomlyChange
                , clickTargetProfile.targetDDirectionValuesRadiansPerSecond.canRandomlyChange
                , false

                // Probability of random change X1, X2, X3
                , (clickTargetProfile.targetDSpeedValuesInchesPerSecondPerSecond.canRandomlyChange) ? clickTargetProfile.targetDSpeedValuesInchesPerSecondPerSecond.randomChangeIntervalValue : 0.0
                , (clickTargetProfile.targetDDirectionValuesRadiansPerSecond.canRandomlyChange) ? clickTargetProfile.targetDDirectionValuesRadiansPerSecond.randomChangeIntervalValue : 0.0
                , 0.0

                // Random change interval
                , clickTargetProfile.targetDSpeedValuesInchesPerSecondPerSecond.randomChangeInterval
                , clickTargetProfile.targetDDirectionValuesRadiansPerSecond.randomChangeInterval
                , PositionEvolver.RANDOM_CHANGE_INTERVAL.CONSTANT

                // Tie random new X1, X2, X3 to random new DX1, DX2, DX3
                , false
                , false
                , false
                , false
                , false
                , false
                , false

                // Boundary effect
                , clickTargetProfile.targetDSpeedValuesInchesPerSecondPerSecond.boundaryEffect
                , clickTargetProfile.targetDDirectionValuesRadiansPerSecond.boundaryEffect
                , new PositionEvolver.BoundaryEffect()
        );

        double targetSpeedPixelsPerSecond = 0.0;
        double targetDirectionAngleRadians = 0.0;
        // Speed
        if (!initializeClickTarget && continuousSpeed) {
            if (positionEvolverXPixels != null
                    && positionEvolverXPixels.getPositionEvolverDXdt() != null) {
                PositionEvolver positionEvolverDXdtPixels = positionEvolverXPixels.getPositionEvolverDXdt();
                targetSpeedPixelsPerSecond = positionEvolverDXdtPixels.getX().X1;
            }
        } else {
            if (clickTargetProfile.targetPositionHorizontalValuesInches.canChange) {
                if (clickTargetProfile.targetSpeedValuesInchesPerSecond.randomInitialValue) {
                    targetSpeedPixelsPerSecond =
                            UtilityFunctions.generateRandomValue(
                                    minimumTargetSpeedPixelsPerSecond
                                    , maximumTargetSpeedPixelsPerSecond
                                    , false);
                } else {
                    targetSpeedPixelsPerSecond = initialTargetSpeedPixelsPerSecond;
                }
            }
        }
        // Direction Angle
        if (!initializeClickTarget && continuousDirection) {
            if (positionEvolverXPixels != null
                    && positionEvolverXPixels.getPositionEvolverDXdt() != null) {
                PositionEvolver positionEvolverDXdtPixels = positionEvolverXPixels.getPositionEvolverDXdt();
                targetDirectionAngleRadians = positionEvolverDXdtPixels.getX().X2;
            }
        } else {
            if (clickTargetProfile.targetPositionHorizontalValuesInches.canChange) {
                if (clickTargetProfile.targetDirectionAngleValuesRadians.randomInitialValue) {
                    targetDirectionAngleRadians =
                            UtilityFunctions.generateRandomValue(
                                    0.0
                                    , 2.0 * Math.PI
                                    , false);
                } else {
                    targetDirectionAngleRadians = clickTargetProfile.targetDirectionAngleValuesRadians.initialValue;
                }
            }
        }

        PositionEvolver targetDXdtPixels = new PositionEvolver(

                // Name
                "dXdt"

                // Initial position and coordinate system
                , new PositionVector(
                        targetSpeedPixelsPerSecond
                        , targetDirectionAngleRadians
                        , 0.0)
                , PositionEvolver.MODE.POLAR_2D

                // X1, X2, X3 are constant
                , !clickTargetProfile.targetSpeedValuesInchesPerSecond.canChange
                , !clickTargetProfile.targetDirectionAngleValuesRadians.canChange
                , true

                // dXdt Position Evolver
                , targetD2Xdt2Pixels

                // Min X1, X2, X3
                , minimumTargetSpeedPixelsPerSecond
                , 0.0
                , 0.0

                // Max X1, X2, X3
                , maximumTargetSpeedPixelsPerSecond
                , Math.PI * 2.0
                , 0.0

                // Random position changes
                , false
                , 0.0
                , PositionEvolver.RANDOM_CHANGE_INTERVAL.CONSTANT

                // Random changes in speed and direction
                , clickTargetProfile.targetSpeedValuesInchesPerSecond.canRandomlyChange
                , clickTargetProfile.targetDirectionAngleValuesRadians.canRandomlyChange
                , false

                // Probabilities of speed/direction change
                , clickTargetProfile.targetSpeedValuesInchesPerSecond.randomChangeIntervalValue
                , clickTargetProfile.targetDirectionAngleValuesRadians.randomChangeIntervalValue
                , 0.0

                // Random change interval
                , clickTargetProfile.targetSpeedValuesInchesPerSecond.randomChangeInterval
                , clickTargetProfile.targetDirectionAngleValuesRadians.randomChangeInterval
                , PositionEvolver.RANDOM_CHANGE_INTERVAL.CONSTANT

                // Tie new random X1, X2, X3 to new random DX1, DX2, DX3
                , clickTargetProfile.tieRandomSpeedChangeToRandomDDirectionChange
                , clickTargetProfile.tieRandomDirectionChangeToRandomSpeedChange
                , clickTargetProfile.tieRandomSpeedChangeToRandomDSpeedChange
                , clickTargetProfile.tieRandomSpeedChangeToRandomDDirectionChange
                , clickTargetProfile.tieRandomDirectionChangeToRandomDSpeedChange
                , clickTargetProfile.tieRandomDirectionChangeToRandomDDirectionChange
                , false

                // Boundary effect
                , clickTargetProfile.targetSpeedValuesInchesPerSecond.boundaryEffect
                , clickTargetProfile.targetDirectionAngleValuesRadians.boundaryEffect
                , new PositionEvolver.BoundaryEffect()
        );

        double XPixels = 0.0;
        double YPixels = 0.0;
        if (!initializeClickTarget && continuousX) {
            if (positionEvolverXPixels != null) {
                XPixels = positionEvolverXPixels.getX().X1;
                YPixels = positionEvolverXPixels.getX().X2;
            }
        } else {
            XPixels = minimumPixelsX + (width / 2.0);
            YPixels = minimumPixelsY + (height / 2.0);

            if (clickTargetProfile.targetPositionHorizontalValuesInches.boundaryEffect.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.PERIODIC
                    || clickTargetProfile.targetPositionHorizontalValuesInches.boundaryEffect.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.PERIODIC_REFLECTIVE) {
                minimumPixelsX = 0.0;
                maximumPixelsX = width;
            }

            if (clickTargetProfile.targetPositionVerticalValuesInches.boundaryEffect.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.PERIODIC
                    || clickTargetProfile.targetPositionVerticalValuesInches.boundaryEffect.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.PERIODIC_REFLECTIVE) {
                minimumPixelsY = 0.0;
                maximumPixelsY = height;
            }
        }

        PositionEvolver targetXPixels = new PositionEvolver(

                // Name
                "X"

                // Initial position and coordinate system
                , new PositionVector(
                        XPixels
                        , YPixels
                        , 0.0)
                , PositionEvolver.MODE.CARTESIAN_2D

                // X1, X2, X3 are constant
                , !clickTargetProfile.targetPositionHorizontalValuesInches.canChange
                , !clickTargetProfile.targetPositionHorizontalValuesInches.canChange
                , true

                // dXdt Position evolver
                , targetDXdtPixels

                // Minimum X1, X2, X3
                , minimumPixelsX
                , minimumPixelsY
                , 0.0

                // Maximum X1, X2, X3
                , maximumPixelsX
                , maximumPixelsY
                , 0.0

                // Can randomly change position
                , clickTargetProfile.targetPositionHorizontalValuesInches.canRandomlyChange
                , clickTargetProfile.targetPositionHorizontalValuesInches.randomChangeIntervalValue
                , clickTargetProfile.targetPositionHorizontalValuesInches.randomChangeInterval

                // Can randomly change X1, X2, X3
                , false
                , false
                , false

                // Probability of random change X1, X2, X3
                , 0.0
                , 0.0
                , 0.0

                // Random change interval
                , PositionEvolver.RANDOM_CHANGE_INTERVAL.CONSTANT
                , PositionEvolver.RANDOM_CHANGE_INTERVAL.CONSTANT
                , PositionEvolver.RANDOM_CHANGE_INTERVAL.CONSTANT

                // Tie new random X1, X2, X3 to new random DX1, DX2, DX3
                , false
                , false
                , clickTargetProfile.tieRandomPositionChangeToRandomSpeedChange
                , clickTargetProfile.tieRandomPositionChangeToRandomDirectionChange
                , clickTargetProfile.tieRandomPositionChangeToRandomSpeedChange
                , clickTargetProfile.tieRandomPositionChangeToRandomDirectionChange
                , false

                // Boundary effect
                , clickTargetProfile.targetPositionHorizontalValuesInches.boundaryEffect
                , clickTargetProfile.targetPositionVerticalValuesInches.boundaryEffect
                , new PositionEvolver.BoundaryEffect()
        );

        double targetD2RadiusPixels = 0.0;
        double targetD2RotationRadians = 0.0;
        // D2Radius
        if (!initializeClickTarget && continuousD2Radius) {
            if (positionEvolverRadiusPixels != null
                    && positionEvolverRadiusPixels.getPositionEvolverDXdt() != null
                    && positionEvolverRadiusPixels.getPositionEvolverDXdt().getPositionEvolverDXdt() != null) {
                PositionEvolver positionEvolverDRadiusChangePixels = positionEvolverRadiusPixels.getPositionEvolverDXdt().getPositionEvolverDXdt();
                targetD2RadiusPixels = positionEvolverDRadiusChangePixels.getX().X1;
            }
        } else {
            if (clickTargetProfile.targetDRadiusValuesInchesPerSecond.canChange) {
                if (clickTargetProfile.targetD2RadiusValuesInchesPerSecondPerSecond.randomInitialValue) {
                    targetD2RadiusPixels = UtilityFunctions.generateRandomValue(
                            minimumTargetDRadiusChangeAbsoluteValuePixelsPerSecondPerSecond
                            , maximumTargetDRadiusChangeAbsoluteValuePixelsPerSecondPerSecond
                            , true);
                } else {
                    targetD2RadiusPixels = initialTargetDRadiusChangeAbsoluteValuePixelsPerSecondPerSecond;
                    if (clickTargetProfile.targetD2RadiusValuesInchesPerSecondPerSecond.randomInitialSign) {
                        targetD2RadiusPixels *= UtilityFunctions.getRandomSign();
                    }
                }
            }
        }
        // D2Rotation
        if (!initializeClickTarget && continuousD2Rotation) {
            if (positionEvolverRadiusPixels != null
                    && positionEvolverRadiusPixels.getPositionEvolverDXdt() != null
                    && positionEvolverRadiusPixels.getPositionEvolverDXdt().getPositionEvolverDXdt() != null) {
                PositionEvolver positionEvolverD2RadiusPixels = positionEvolverRadiusPixels.getPositionEvolverDXdt().getPositionEvolverDXdt();
                targetD2RotationRadians = positionEvolverD2RadiusPixels.getX().X2;
            }
        } else {
            if (clickTargetProfile.targetDRotationValuesRadiansPerSecond.canChange) {
                if (clickTargetProfile.targetD2RotationValuesRadiansPerSecondPerSecond.randomInitialValue) {
                    targetD2RotationRadians = UtilityFunctions.generateRandomValue(
                            clickTargetProfile.targetD2RotationValuesRadiansPerSecondPerSecond.minimumValue
                            , clickTargetProfile.targetD2RotationValuesRadiansPerSecondPerSecond.maximumValue
                            , true);
                } else {
                    targetD2RotationRadians = clickTargetProfile.targetD2RotationValuesRadiansPerSecondPerSecond.initialValue;
                    if (clickTargetProfile.targetD2RotationValuesRadiansPerSecondPerSecond.randomInitialSign) {
                        targetD2RotationRadians *= UtilityFunctions.getRandomSign();
                    }
                }
            }
        }

        PositionEvolver targetD2RadiusDt2Pixels = new PositionEvolver(

                // Name
                "d2Radiusdt2"

                // Initial position and coordinate system
                , new PositionVector(
                        targetD2RadiusPixels
                        , targetD2RotationRadians
                        , 0.0)
                , PositionEvolver.MODE.CARTESIAN_2D

                // Is constant X1, X2, X3
                , true
                , true
                , true

                // PositionEvolver dXdt
                , null

                // Minimum X1, X2, X3
                , minimumTargetDRadiusChangeAbsoluteValuePixelsPerSecondPerSecond
                , clickTargetProfile.targetD2RotationValuesRadiansPerSecondPerSecond.minimumValue
                , 0.0

                // Maximum X1, X2, X3
                , maximumTargetDRadiusChangeAbsoluteValuePixelsPerSecondPerSecond
                , clickTargetProfile.targetD2RotationValuesRadiansPerSecondPerSecond.maximumValue
                , 0.0

                // Can randomly change position
                , false
                , 0.0
                , PositionEvolver.RANDOM_CHANGE_INTERVAL.CONSTANT

                // Can change X1, X2, X3
                , clickTargetProfile.targetD2RadiusValuesInchesPerSecondPerSecond.canRandomlyChange
                , clickTargetProfile.targetD2RotationValuesRadiansPerSecondPerSecond.canRandomlyChange
                , false

                // Probability of change X1, X2, X3
                , clickTargetProfile.targetD2RadiusValuesInchesPerSecondPerSecond.randomChangeIntervalValue
                , clickTargetProfile.targetD2RotationValuesRadiansPerSecondPerSecond.randomChangeIntervalValue
                , 0.0

                // Random change interval
                , clickTargetProfile.targetD2RadiusValuesInchesPerSecondPerSecond.randomChangeInterval
                , clickTargetProfile.targetD2RotationValuesRadiansPerSecondPerSecond.randomChangeInterval
                , PositionEvolver.RANDOM_CHANGE_INTERVAL.CONSTANT

                // Tie random new X1, X2, X3 to new random DX1, DX2, DX3
                , false
                , false
                , false
                , false
                , false
                , false
                , false

                // Boundary effect
                , clickTargetProfile.targetD2RadiusValuesInchesPerSecondPerSecond.boundaryEffect
                , clickTargetProfile.targetD2RotationValuesRadiansPerSecondPerSecond.boundaryEffect
                , new PositionEvolver.BoundaryEffect()
        );

        double targetDRadiusPixels = 0.0;
        double targetDRotationRadians = 0.0;
        // DRadius
        if (!initializeClickTarget && continuousDRadius) {
            if (positionEvolverRadiusPixels != null
                    && positionEvolverRadiusPixels.getPositionEvolverDXdt() != null) {
                PositionEvolver positionEvolverDRadiusPixels = positionEvolverRadiusPixels.getPositionEvolverDXdt();
                targetDRadiusPixels = positionEvolverDRadiusPixels.getX().X1;
            }
        } else {
            if (clickTargetProfile.targetRadiusValuesInches.canChange) {
                if (clickTargetProfile.targetDRadiusValuesInchesPerSecond.randomInitialValue) {
                    targetDRadiusPixels = UtilityFunctions.generateRandomValue(
                            minimumTargetDRadiusAbsoluteValuePixelsPerSecond
                            , maximumTargetDRadiusAbsoluteValuePixelsPerSecond
                            , true);
                } else {
                    targetDRadiusPixels = initialTargetDRadiusAbsoluteValuePixelsPerSecond;
                    if (clickTargetProfile.targetDRadiusValuesInchesPerSecond.randomInitialSign) {
                        targetDRadiusPixels *= UtilityFunctions.getRandomSign();
                    }
                }
            }
        }
        // DRotation
        if (!initializeClickTarget && continuousDRotation) {
            if (positionEvolverRadiusPixels != null
                    && positionEvolverRadiusPixels.getPositionEvolverDXdt() != null) {
                PositionEvolver positionEvolverDRadiusPixels = positionEvolverRadiusPixels.getPositionEvolverDXdt();
                targetDRotationRadians = positionEvolverDRadiusPixels.getX().X2;
            }
        } else {
            if (clickTargetProfile.targetRotationValuesRadians.canChange) {
                if (clickTargetProfile.targetDRotationValuesRadiansPerSecond.randomInitialValue) {
                    targetDRotationRadians =
                            UtilityFunctions.generateRandomValue(
                                    clickTargetProfile.targetDRotationValuesRadiansPerSecond.minimumValue
                                    , clickTargetProfile.targetDRotationValuesRadiansPerSecond.maximumValue
                                    , true);
                } else {
                    targetDRotationRadians = clickTargetProfile.targetDRotationValuesRadiansPerSecond.initialValue;
                    if (clickTargetProfile.targetDRotationValuesRadiansPerSecond.randomInitialSign) {
                        double randomSign = UtilityFunctions.getRandomSign();
                        targetDRotationRadians *= randomSign;
                    }
                }
            }
        }

        PositionEvolver targetDRadiusDtPixels = new PositionEvolver(

                // Name
                "dRadiusdt"

                // Initial position and coordinate system
                , new PositionVector(
                        targetDRadiusPixels
                        , targetDRotationRadians
                        , 0.0)
                , PositionEvolver.MODE.CARTESIAN_2D

                // Is constant X1, X2, X3
                , !clickTargetProfile.targetDRadiusValuesInchesPerSecond.canChange
                , !clickTargetProfile.targetDRotationValuesRadiansPerSecond.canChange
                , true

                // PositionEvolver dXdt
                , targetD2RadiusDt2Pixels

                // Minimum X1, X2, X3
                , minimumTargetDRadiusAbsoluteValuePixelsPerSecond
                , clickTargetProfile.targetDRotationValuesRadiansPerSecond.minimumValue
                , 0.0

                // Maximum X1, X2, X3
                , maximumTargetDRadiusAbsoluteValuePixelsPerSecond
                , clickTargetProfile.targetDRotationValuesRadiansPerSecond.maximumValue
                , 0.0

                // Can randomly change position
                , false
                , 0.0
                , PositionEvolver.RANDOM_CHANGE_INTERVAL.CONSTANT

                // Can change X1, X2, X3
                , clickTargetProfile.targetDRadiusValuesInchesPerSecond.canRandomlyChange
                , clickTargetProfile.targetDRotationValuesRadiansPerSecond.canRandomlyChange
                , false

                // Probability of change X1, X2, X3
                , clickTargetProfile.targetDRadiusValuesInchesPerSecond.randomChangeIntervalValue
                , clickTargetProfile.targetDRotationValuesRadiansPerSecond.randomChangeIntervalValue
                , 0.0

                // Random change interval
                , clickTargetProfile.targetDRadiusValuesInchesPerSecond.randomChangeInterval
                , clickTargetProfile.targetDRotationValuesRadiansPerSecond.randomChangeInterval
                , PositionEvolver.RANDOM_CHANGE_INTERVAL.CONSTANT

                // Tie new random X1, X2, X3 to new random DX1, DX2, DX3
                , false
                , false
                , clickTargetProfile.tieRandomDRadiusChangeToRandomD2RadiusChange
                , false
                , false
                , clickTargetProfile.tieRandomDRotationChangeToRandomD2RotationChange
                , false

                // Boundary effect
                , clickTargetProfile.targetDRadiusValuesInchesPerSecond.boundaryEffect
                , clickTargetProfile.targetDRotationValuesRadiansPerSecond.boundaryEffect
                , new PositionEvolver.BoundaryEffect()
        );


        double radiusPixels = 0.0;
        double rotationRadians = 0.0;
        // Radius
        if (!initializeClickTarget && continuousRadius) {
            if (positionEvolverRadiusPixels != null) {
                radiusPixels = positionEvolverRadiusPixels.getX().X1;
            }
        } else {
            if (clickTargetProfile.targetRadiusValuesInches.randomInitialValue) {
                radiusPixels = UtilityFunctions.generateRandomValue(
                        minimumTargetRadiusPixels
                        , maximumTargetRadiusPixels
                        , false);
            } else {
                radiusPixels = initialTargetRadiusPixels;
            }
        }
        // Rotation
        if (!initializeClickTarget && continuousRotation) {
            if (positionEvolverRadiusPixels != null) {
                rotationRadians = positionEvolverRadiusPixels.getX().X2;
            }
        } else {
            if (clickTargetProfile.targetRotationValuesRadians.randomInitialValue) {
                rotationRadians = UtilityFunctions.generateRandomValue(
                        clickTargetProfile.targetRotationValuesRadians.minimumValue
                        , clickTargetProfile.targetRotationValuesRadians.maximumValue
                        , false);
            } else {
                rotationRadians = clickTargetProfile.targetRotationValuesRadians.initialValue;
            }
        }

        PositionEvolver targetRadiusPixels = new PositionEvolver(

                // Name
                "radius"

                // Initial position and coordinate system
                , new PositionVector(
                        radiusPixels
                        , rotationRadians
                        , 0.0)
                , PositionEvolver.MODE.CARTESIAN_2D

                // X1, X2, X3 are constant
                , !clickTargetProfile.targetRadiusValuesInches.canChange
                , !clickTargetProfile.targetRotationValuesRadians.canChange
                , true

                // Position evolver DXdt
                , targetDRadiusDtPixels

                // Minimum X1, X2, X3
                , minimumTargetRadiusPixels
                , clickTargetProfile.targetRotationValuesRadians.minimumValue
                , 0.0

                // Maximum X1, X2, X3
                , maximumTargetRadiusPixels
                , clickTargetProfile.targetRotationValuesRadians.maximumValue
                , 0.0

                // Can randomly change position
                , false
                , 0.0
                , PositionEvolver.RANDOM_CHANGE_INTERVAL.CONSTANT

                // Can randomly change X1, X2, X3
                , clickTargetProfile.targetRadiusValuesInches.canRandomlyChange
                , clickTargetProfile.targetRotationValuesRadians.canRandomlyChange
                , false

                // Probability of change X1, X2, X3
                , clickTargetProfile.targetRadiusValuesInches.randomChangeIntervalValue
                , clickTargetProfile.targetRotationValuesRadians.randomChangeIntervalValue
                , 0.0

                // Random change interval
                , clickTargetProfile.targetRadiusValuesInches.randomChangeInterval
                , clickTargetProfile.targetRotationValuesRadians.randomChangeInterval
                , PositionEvolver.RANDOM_CHANGE_INTERVAL.CONSTANT

                // Tie new random X1, X2, X3 to new random DX1, DX2, DX3
                , false
                ,false
                , clickTargetProfile.tieRandomRadiusChangeToRandomDRadiusChange
                , false
                , false
                , clickTargetProfile.tieRandomRotationChangeToRandomDRotationChange
                , false

                // Boundary effect
                , clickTargetProfile.targetRadiusValuesInches.boundaryEffect
                , clickTargetProfile.targetRotationValuesRadians.boundaryEffect
                , new PositionEvolver.BoundaryEffect()
        );

        positionEvolverXPixels = targetXPixels;
        positionEvolverRadiusPixels = targetRadiusPixels;

        // Get the polygon
        polygonTargetShape = PolygonHelper.getPolygon(context, clickTargetProfile.targetShape);

        // Check if we have a polygon
        if (polygonTargetShape != null) {

            // Set the polygon's properties
            polygonTargetShape.setProperties(
                    new PositionVector(
                            XPixels
                            , YPixels
                    )
                    , radiusPixels
                    , rotationRadians
            );

        }

    }

}
