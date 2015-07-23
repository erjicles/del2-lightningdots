package com.delsquared.lightningdots.game;


import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import com.delsquared.lightningdots.ntuple.NTuple;
import com.delsquared.lightningdots.utilities.Polygon;
import com.delsquared.lightningdots.utilities.PolygonHelper;
import com.delsquared.lightningdots.utilities.PositionEvolver;
import com.delsquared.lightningdots.utilities.PositionEvolverVariable;
import com.delsquared.lightningdots.utilities.PositionVector;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClickTarget {

    public static final String POSITION_EVOLVER_NAME_X = "X";
    public static final String POSITION_EVOLVER_NAME_DX = "dXdt";
    public static final String POSITION_EVOLVER_NAME_D2X = "d2Xdt2";
    public static final String POSITION_EVOLVER_NAME_RADIUS = "radius";
    public static final String POSITION_EVOLVER_NAME_DRADIUS = "dRadius";
    public static final String POSITION_EVOLVER_NAME_D2RADIUS = "d2Radius";
    public static final String POSITION_EVOLVER_NAME_ROTATION = "rotation";
    public static final String POSITION_EVOLVER_NAME_DROTATION = "dRotation";
    public static final String POSITION_EVOLVER_NAME_D2ROTATION = "d2Rotation";

    public static final String VARIABLE_NAME_X = "position_horizontal";
    public static final String VARIABLE_NAME_Y = "position_vertical";
    public static final String VARIABLE_NAME_SPEED = "speed";
    public static final String VARIABLE_NAME_DIRECTION = "direction";
    public static final String VARIABLE_NAME_DSPEED = "dSpeed";
    public static final String VARIABLE_NAME_DDIRECTION = "dDirection";
    public static final String VARIABLE_NAME_RADIUS = "radius";
    public static final String VARIABLE_NAME_DRADIUS = "dRadius";
    public static final String VARIABLE_NAME_D2RADIUS = "d2Radius";
    public static final String VARIABLE_NAME_ROTATION = "rotation";
    public static final String VARIABLE_NAME_DROTATION = "dRotation";
    public static final String VARIABLE_NAME_D2ROTATION = "d2Rotation";

    private String name;
    private ClickTargetProfileScript clickTargetProfileScript;
    private List<PositionEvolver> listPositionEvolvers;
    private Polygon polygonTargetShape = null;
    private boolean isClickable = true;
    private VISIBILITY visibility = VISIBILITY.VISIBLE;

    private double canvasWidth = 1.0;
    private double canvasHeight = 1.0;

    public ClickTarget() {

        name = "";

        clickTargetProfileScript = new ClickTargetProfileScript();

        listPositionEvolvers = new ArrayList<>();

    }

	public ClickTarget(
            Context context
            , String name
            , ClickTargetProfileScript clickTargetProfileScript
            , double canvasWidth
            , double canvasHeight)
	{
        this.name = name;
        this.clickTargetProfileScript = clickTargetProfileScript;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;

        listPositionEvolvers = new ArrayList<>();

        // Initialize the click target to its current profile
        transitionClickTargetProfile(
                context
                , true);
	}

    public PositionVector getXPixels() {
        PositionEvolver positionEvolverX = getPositionEvolver(POSITION_EVOLVER_NAME_X);
        if (positionEvolverX != null) {
            return positionEvolverX.getX();
        }
        return null;
    }

    public double getRadiusPixels() {
        PositionEvolver positionEvolverRadius = getPositionEvolver(POSITION_EVOLVER_NAME_RADIUS);
        if (positionEvolverRadius != null) {
            return positionEvolverRadius.getX().getValue(0);
        }
        return 0.0;
    }

    public PositionEvolver getPositionEvolverFromVariableName(String variableName) {

        // Get the position evolver name
        String positionEvolverName = "";
        if (mapTranslateVariableNameToPositionEvolverName.containsKey(variableName)) {
            positionEvolverName = mapTranslateVariableNameToPositionEvolverName.get(variableName);
        }

        return getPositionEvolver(positionEvolverName);

    }

    public double getVariableValue(String variableName) {

        // Get the position evolver
        PositionEvolver positionEvolver = getPositionEvolverFromVariableName(variableName);

        if (positionEvolver != null) {

            // Return the value
            return positionEvolver.getVariableValue(variableName);

        }

         return 0.0;
    }

    public void setVariableValue(String variableName, double variableValue) {
        setVariableValue(variableName, variableValue, false);
    }

    public void setVariableValue(String variableName, double variableValue, boolean treatAsInitialValue) {

        // Get the position evolver
        PositionEvolver positionEvolver = getPositionEvolverFromVariableName(variableName);

        if (positionEvolver != null) {

            // Set the value
            positionEvolver.setVariableValue(variableName, variableValue, treatAsInitialValue);

        }

    }

    public void randomizeVariableValue(String variableName) {

        // Get the position evolver
        PositionEvolver positionEvolver = getPositionEvolverFromVariableName(variableName);

        if (positionEvolver != null) {

            // Randomize the value
            positionEvolver.randomizeVariableValue(variableName);

        }

    }

    public PositionEvolver getPositionEvolver(String positionEvolverName) {

        // Loop through the top level PositionEvolvers first
        for (PositionEvolver positionEvolver : listPositionEvolvers) {
            if (positionEvolver.getName().contentEquals(positionEvolverName)) {
                return positionEvolver;
            }
        }

        // Do a deep dive
        for (PositionEvolver positionEvolver : listPositionEvolvers) {

            // Set the current position evolver to the dX position evolver
            PositionEvolver currentPositionEvolver = positionEvolver.getPositionEvolverDXdt();

            // Loop while the dX position evolver is not null
            while (currentPositionEvolver != null) {

                // Check if the current position evolver is the one we are looking for
                if (currentPositionEvolver.getName().contentEquals(positionEvolverName)) {
                    return currentPositionEvolver;
                }

                // Set the next position evolver to the next dX position evolver
                currentPositionEvolver = currentPositionEvolver.getPositionEvolverDXdt();
            }
        }
        return null;
    }

    public String getName() {
        return this.name;
    }

    public String getCurrentClickTargetProfileName() {
        if (clickTargetProfileScript != null) {
            ClickTargetProfile currentClickTargetProfile = clickTargetProfileScript.getCurrentClickTargetProfile();
            if (currentClickTargetProfile != null) {
                return currentClickTargetProfile.name;
            }
        }
        return "";
    }

    public List<PositionVector> getListCenterPoints() {

        // Initialize the result
        List<PositionVector> listCenterPoints = new ArrayList<>();

        // Get the X PositionEvolver
        PositionEvolver positionEvolverXPixels = getPositionEvolver(POSITION_EVOLVER_NAME_X);

        // Check if the position evolver exists
        if (positionEvolverXPixels == null) {
            return listCenterPoints;
        }

        // Add the true center point
        listCenterPoints.add(new PositionVector(positionEvolverXPixels.getX()));

        // Get the X and Y boundary effects
        PositionEvolver.BoundaryEffect boundaryEffectX = positionEvolverXPixels.getBoundaryEffect(VARIABLE_NAME_X);
        PositionEvolver.BoundaryEffect boundaryEffectY = positionEvolverXPixels.getBoundaryEffect(VARIABLE_NAME_Y);

        // Check if either X or Y has periodic boundary effect
        // If not, then there won't be any secondary points
        if (boundaryEffectX.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.PERIODIC
                || boundaryEffectX.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.PERIODIC_REFLECTIVE
                || boundaryEffectY.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.PERIODIC
                || boundaryEffectY.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.PERIODIC_REFLECTIVE) {

            double targetX = positionEvolverXPixels.getX().getValue(0);
            double targetY = positionEvolverXPixels.getX().getValue(1);
            double radius = getRadiusPixels();
            double minimumX = positionEvolverXPixels.getMinimumValue(VARIABLE_NAME_X);
            double maximumX = positionEvolverXPixels.getMaximumValue(VARIABLE_NAME_X);
            double minimumY = positionEvolverXPixels.getMinimumValue(VARIABLE_NAME_Y);
            double maximumY = positionEvolverXPixels.getMaximumValue(VARIABLE_NAME_Y);
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

                if (boundaryEffectX.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.PERIODIC) {

                    if (minimumBoundaryReachedX) {
                        double overflow = (minimumX - radiusMinimumX) % widthX;
                        newTargetX1 = maximumX - overflow + radius;
                    }

                    if (maximumBoundaryReachedX) {
                        double overflow = (radiusMaximumX - maximumX) % widthX;
                        newTargetX2 = minimumX + overflow - radius;
                    }

                } else if (boundaryEffectX.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.PERIODIC_REFLECTIVE) {

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

                if (boundaryEffectY.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.PERIODIC) {

                    if (minimumBoundaryReachedY) {
                        double overflow = (minimumY - radiusMinimumY) % heightY;
                        newTargetY1 = maximumY - overflow + radius;
                    }

                    if (maximumBoundaryReachedY) {
                        double overflow = (radiusMaximumY - maximumY) % heightY;
                        newTargetY2 = minimumY + overflow - radius;
                    }

                } else if (boundaryEffectY.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.PERIODIC_REFLECTIVE) {

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
                listCenterPoints.add(new PositionVector(newTargetX1, targetY));
            }
            if (maximumBoundaryReachedX) {
                //newTargetX2, targetY;
                listCenterPoints.add(new PositionVector(newTargetX2, targetY));
            }
            if (minimumBoundaryReachedY) {
                //targetX, newTargetY1;
                listCenterPoints.add(new PositionVector(targetX, newTargetY1));
            }
            if (maximumBoundaryReachedY) {
                //targetX, newTargetY2;
                listCenterPoints.add(new PositionVector(targetX, newTargetY2));
            }
            if (minimumBoundaryReachedX && minimumBoundaryReachedY) {
                //newTargetX1, newTargetY1;
                listCenterPoints.add(new PositionVector(newTargetX1, newTargetY1));
            }
            if (minimumBoundaryReachedX && maximumBoundaryReachedY) {
                //newTargetX1, newTargetY2;
                listCenterPoints.add(new PositionVector(newTargetX1, newTargetY2));
            }
            if (maximumBoundaryReachedX && minimumBoundaryReachedY) {
                //newTargetX2, newTargetY1;
                listCenterPoints.add(new PositionVector(newTargetX2, newTargetY1));
            }
            if (maximumBoundaryReachedX && maximumBoundaryReachedY) {
                //newTargetX2, newTargetY2;
                listCenterPoints.add(new PositionVector(newTargetX2, newTargetY2));
            }

        }

        return listCenterPoints;

    }

    public void setCanvasWidthAndHeight(double canvasWidth, double canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        resetPositionBoundary();
    }

    public boolean pointIsInsideTarget(double pointX, double pointY) {

        boolean returnValue = false;

        // Get the X position evolver
        PositionEvolver positionEvolverXPixels = getPositionEvolver(POSITION_EVOLVER_NAME_X);

        if (positionEvolverXPixels == null)
            return false;

        // Get the target radius
        double radius = getRadiusPixels();

        // Get the list of center points
        ArrayList<PositionVector> arrayListCenterPoints = new ArrayList<>(getListCenterPoints());

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

                double dx = currentCenterPoint.getValue(0) - pointX;
                double dy = currentCenterPoint.getValue(1) - pointY;

                returnValue = (dx * dx) + (dy * dy) <= (radius * radius);

            }

            if (returnValue) break;

        }

        return returnValue;
    }

    public void resetPositionBoundary() {

        // Get the position evolver
        PositionEvolver positionEvolverXPixels = getPositionEvolver(POSITION_EVOLVER_NAME_X);

        if (positionEvolverXPixels == null) {
            return;
        }

        double radius = getRadiusPixels();

        double minimumX = 0.0;
        double maximumX = canvasWidth;
        double minimumY = 0.0;
        double maximumY = canvasHeight;

        PositionEvolver.BoundaryEffect boundaryEffectX = positionEvolverXPixels.getBoundaryEffect(VARIABLE_NAME_X);
        PositionEvolver.BoundaryEffect boundaryEffectY = positionEvolverXPixels.getBoundaryEffect(VARIABLE_NAME_Y);

        if (boundaryEffectX.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.BOUNCE
                || boundaryEffectX.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.STICK) {
            minimumX = radius / 2.0;
            maximumX = canvasWidth - (radius / 2.0);
        }
        if (boundaryEffectY.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.BOUNCE
                || boundaryEffectY.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.STICK) {
            minimumY = radius / 2.0;
            maximumY = canvasHeight - (radius / 2.0);
        }
        positionEvolverXPixels.setBoundaryValues(VARIABLE_NAME_X, minimumX, maximumX);
        positionEvolverXPixels.setBoundaryValues(VARIABLE_NAME_Y, minimumY, maximumY);

    }

    public ArrayList<RandomChangeEvent> checkRandomChanges(double dt) {

        // Create the result list
        ArrayList<RandomChangeEvent> arrayListRandomChangeEvents = new ArrayList<>();

        // Loop through the position evolvers
        for (PositionEvolver currentPositionEvolver : listPositionEvolvers) {

            // Check the position evolver for random changes
            List<String> listRandomChanges = currentPositionEvolver.checkRandomChanges(
                    dt
                    , new HashMap<String, Boolean>()
            );

            // Convert the list of change variables to RandomChangeEvents
            for (String currentRandomChangeVariableName : listRandomChanges) {
                RandomChangeEvent randomChangeEvent = new RandomChangeEvent(
                        name
                        , getCurrentClickTargetProfileName()
                        , currentRandomChangeVariableName
                );
                arrayListRandomChangeEvents.add(randomChangeEvent);
            }

        }

        return arrayListRandomChangeEvents;
    }

    public boolean checkProfileTransition(double dt) {

        return clickTargetProfileScript.processTransition(dt);

    }

	public void processElapsedTimeMillis(
			Context context
			, double dt
            , Map<String, Boolean> mapEvolveVariables
            , Map<String, Boolean> mapRandomChanges
            , String transitionToClickTargetProfileName) {

        // Check if the visibility is gone
        if (visibility == VISIBILITY.GONE) {
            return;
        }

		// Get the resources
		Resources resources = context.getResources();

        // Loop through the position evolvers
        for (PositionEvolver positionEvolver : listPositionEvolvers) {

            // Evolve the position and radius
            positionEvolver.evolveTime(
                    dt
                    , mapEvolveVariables
                    , mapRandomChanges);

        }

        // Update the polygon
        if (polygonTargetShape != null) {

            // Get the position evolvers
            PositionEvolver positionEvolverX = getPositionEvolver(POSITION_EVOLVER_NAME_X);
            PositionEvolver positionEvolverRadius = getPositionEvolver(POSITION_EVOLVER_NAME_RADIUS);
            PositionEvolver positionEvolverRotation = getPositionEvolver(POSITION_EVOLVER_NAME_ROTATION);

            if (positionEvolverX != null
                    && positionEvolverRadius != null
                    && positionEvolverRotation != null) {

                polygonTargetShape.setProperties(
                        new PositionVector(positionEvolverX.getX())
                        , positionEvolverRadius.getX().getValue(0)
                        , positionEvolverRotation.getX().getValue(0)
                );

            }

        }

        // Process the click target profile transition
        // Check if the click target is in the map
        if (transitionToClickTargetProfileName != null) {

            // Check if the target profile doesn't match the current profile
            if (!clickTargetProfileScript.getCurrentClickTargetProfile().name.contentEquals(transitionToClickTargetProfileName)) {

                // Set the click target profile
                clickTargetProfileScript.setCurrentClickTargetProfileName(transitionToClickTargetProfileName);

            }

            // Transition the click target to the new profile
            transitionClickTargetProfile(
                    context
                    , false
            );

        }

	}

	public ClickTargetSnapshot getClickTargetSnapshot() {

        // Get the position evolvers
        PositionEvolver positionEvolverXPixels = getPositionEvolver(POSITION_EVOLVER_NAME_X);
        PositionEvolver positionEvolverRadiusPixels = getPositionEvolver(POSITION_EVOLVER_NAME_RADIUS);
        PositionEvolver positionEvolverRotationRadians = getPositionEvolver(POSITION_EVOLVER_NAME_ROTATION);

        PositionVector targetX = new PositionVector();
        PositionVector targetDX = new PositionVector();
        PositionVector targetD2X = new PositionVector();
        PositionVector targetRadius = new PositionVector();
        PositionVector targetDRadius = new PositionVector();
        PositionVector targetD2Radius = new PositionVector();
        PositionVector targetRotation = new PositionVector();
        PositionVector targetDRotation = new PositionVector();
        PositionVector targetD2Rotation = new PositionVector();
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
            targetRadius = positionEvolverRadiusPixels.getX();
            if (positionEvolverRadiusPixels.getDXdt() != null) {
                targetDRadius = positionEvolverRadiusPixels.getDXdt();
            }
            if (positionEvolverRadiusPixels.getPositionEvolverDXdt() != null
                    && positionEvolverRadiusPixels.getPositionEvolverDXdt().getPositionEvolverDXdt() != null) {
                targetD2Radius = positionEvolverRadiusPixels.getPositionEvolverDXdt().getPositionEvolverDXdt().getX();
            }
        }
        if (positionEvolverRotationRadians != null) {
            targetRotation = positionEvolverRotationRadians.getX();
            if (positionEvolverRotationRadians.getDXdt() != null) {
                targetDRotation = positionEvolverRotationRadians.getDXdt();
            }
            if (positionEvolverRotationRadians.getPositionEvolverDXdt() != null
                    && positionEvolverRotationRadians.getPositionEvolverDXdt().getPositionEvolverDXdt() != null) {
                targetD2Rotation = positionEvolverRotationRadians.getPositionEvolverDXdt().getPositionEvolverDXdt().getX();
            }
        }

		return new ClickTargetSnapshot(
                name
                , targetX
                , targetDX
                , targetD2X
                , targetRadius
                , targetDRadius
                , targetD2Radius
                , targetRotation
                , targetDRotation
                , targetD2Rotation
                , getListCenterPoints()
                , polygonTargetShape
                , isClickable
                , visibility
        );
	}

    public void transitionClickTargetProfile(
            Context context
            , boolean initializeClickTarget) {

        ClickTargetProfile clickTargetProfile = clickTargetProfileScript.getCurrentClickTargetProfile();

        if (clickTargetProfile == null) {
            return;
        }

        // Get the profile variable values
        ClickTargetProfile.ProfileVariableValues variableValuesPositionHorizontal =
                clickTargetProfile.mapProfileVariableValues.get(VARIABLE_NAME_X);
        ClickTargetProfile.ProfileVariableValues variableValuesPositionVertical =
                clickTargetProfile.mapProfileVariableValues.get(VARIABLE_NAME_Y);
        ClickTargetProfile.ProfileVariableValues variableValuesSpeed =
                clickTargetProfile.mapProfileVariableValues.get(VARIABLE_NAME_SPEED);
        ClickTargetProfile.ProfileVariableValues variableValuesDirection =
                clickTargetProfile.mapProfileVariableValues.get(VARIABLE_NAME_DIRECTION);
        ClickTargetProfile.ProfileVariableValues variableValuesDSpeed =
                clickTargetProfile.mapProfileVariableValues.get(VARIABLE_NAME_DSPEED);
        ClickTargetProfile.ProfileVariableValues variableValuesDDirection =
                clickTargetProfile.mapProfileVariableValues.get(VARIABLE_NAME_DDIRECTION);
        ClickTargetProfile.ProfileVariableValues variableValuesRadius =
                clickTargetProfile.mapProfileVariableValues.get(VARIABLE_NAME_RADIUS);
        ClickTargetProfile.ProfileVariableValues variableValuesDRadius =
                clickTargetProfile.mapProfileVariableValues.get(VARIABLE_NAME_DRADIUS);
        ClickTargetProfile.ProfileVariableValues variableValuesD2Radius =
                clickTargetProfile.mapProfileVariableValues.get(VARIABLE_NAME_D2RADIUS);
        ClickTargetProfile.ProfileVariableValues variableValuesRotation =
                clickTargetProfile.mapProfileVariableValues.get(VARIABLE_NAME_ROTATION);
        ClickTargetProfile.ProfileVariableValues variableValuesDRotation =
                clickTargetProfile.mapProfileVariableValues.get(VARIABLE_NAME_DROTATION);
        ClickTargetProfile.ProfileVariableValues variableValuesD2Rotation =
                clickTargetProfile.mapProfileVariableValues.get(VARIABLE_NAME_D2ROTATION);

        // Determine continuity
        boolean continuousX = false;
        boolean continuousSpeed = false;
        boolean continuousDirection = false;
        boolean continuousDSpeed = false;
        boolean continuousDDirection = false;
        boolean continuousRadius = false;
        boolean continuousDRadius = false;
        boolean continuousD2Radius = false;
        boolean continuousRotation = false;
        boolean continuousDRotation = false;
        boolean continuousD2Rotation = false;

        // If we are not initializing the click target, then determine the continuity of the values
        // (if we are initializing, then all need to be discontinuously initialized)
        if (initializeClickTarget == false) {
            switch (variableValuesPositionHorizontal.transitionContinuity) {
                case CONTINUOUS:
                    continuousX = true;
                    break;
                case DISCONTINUOUS:
                    continuousX = false;
                    break;
                default:
                    continuousX = variableValuesPositionHorizontal.canChange;

            }
            switch (variableValuesSpeed.transitionContinuity) {
                case CONTINUOUS:
                    continuousSpeed = true;
                    break;
                case DISCONTINUOUS:
                    continuousSpeed = false;
                    break;
                default:
                    continuousSpeed = variableValuesSpeed.canChange;
            }
            switch (variableValuesDirection.transitionContinuity) {
                case CONTINUOUS:
                    continuousDirection = true;
                    break;
                case DISCONTINUOUS:
                    continuousDirection = false;
                    break;
                default:
                    continuousDirection = variableValuesDirection.canChange;
            }
            switch (variableValuesDSpeed.transitionContinuity) {
                case CONTINUOUS:
                    continuousDSpeed = true;
                    break;
                case DISCONTINUOUS:
                    continuousDSpeed = false;
                    break;
            }
            switch (variableValuesDDirection.transitionContinuity) {
                case CONTINUOUS:
                    continuousDDirection = true;
                    break;
                case DISCONTINUOUS:
                    continuousDDirection = false;
                    break;
            }
            switch (variableValuesRadius.transitionContinuity) {
                case CONTINUOUS:
                    continuousRadius = true;
                    break;
                case DISCONTINUOUS:
                    continuousRadius = false;
                    break;
                default:
                    continuousRadius = variableValuesRadius.canChange;
            }
            switch (variableValuesDRadius.transitionContinuity) {
                case CONTINUOUS:
                    continuousDRadius = true;
                    break;
                case DISCONTINUOUS:
                    continuousDRadius = false;
                    break;
                default:
                    continuousDRadius = variableValuesDRadius.canChange;
            }
            switch (variableValuesD2Radius.transitionContinuity) {
                case CONTINUOUS:
                    continuousD2Radius = true;
                    break;
                case DISCONTINUOUS:
                    continuousD2Radius = false;
                    break;
            }
            switch (variableValuesRotation.transitionContinuity) {
                case CONTINUOUS:
                    continuousRotation = true;
                    break;
                case DISCONTINUOUS:
                    continuousRotation = false;
                    break;
            }
            switch (variableValuesDRotation.transitionContinuity) {
                case CONTINUOUS:
                    continuousDRotation = true;
                    break;
                case DISCONTINUOUS:
                    continuousDRotation = false;
                    break;
            }
            switch (variableValuesD2Rotation.transitionContinuity) {
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
                        , (float) variableValuesRadius.initialValue
                        , context.getResources().getDisplayMetrics());
        double minimumTargetRadiusPixels =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) variableValuesRadius.minimumValue
                        , context.getResources().getDisplayMetrics());
        double maximumTargetRadiusPixels =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) variableValuesRadius.maximumValue
                        , context.getResources().getDisplayMetrics());

        // Speed and Speed Change
        double initialTargetSpeedPixelsPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) variableValuesSpeed.initialValue
                        , context.getResources().getDisplayMetrics());
        double minimumTargetSpeedPixelsPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) variableValuesSpeed.minimumValue
                        , context.getResources().getDisplayMetrics());
        double maximumTargetSpeedPixelsPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) variableValuesSpeed.maximumValue
                        , context.getResources().getDisplayMetrics());
        double initialTargetDSpeedAbsoluteValuePixelsPerSecondPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) variableValuesDSpeed.initialValue
                        , context.getResources().getDisplayMetrics());
        double minimumTargetDSpeedAbsoluteValuePixelsPerSecondPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) variableValuesDSpeed.minimumValue
                        , context.getResources().getDisplayMetrics());
        double maximumTargetDSpeedAbsoluteValuePixelsPerSecondPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) variableValuesDSpeed.maximumValue
                        , context.getResources().getDisplayMetrics());

        // DRadius and DRadius Change
        double initialTargetDRadiusAbsoluteValuePixelsPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) variableValuesDRadius.initialValue
                        , context.getResources().getDisplayMetrics());
        double minimumTargetDRadiusAbsoluteValuePixelsPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) variableValuesDRadius.minimumValue
                        , context.getResources().getDisplayMetrics());
        double maximumTargetDRadiusAbsoluteValuePixelsPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) variableValuesDRadius.maximumValue
                        , context.getResources().getDisplayMetrics());
        double initialTargetD2RadiusAbsoluteValuePixelsPerSecondPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) variableValuesD2Radius.initialValue
                        , context.getResources().getDisplayMetrics());
        double minimumTargetD2RadiusAbsoluteValuePixelsPerSecondPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) variableValuesD2Radius.minimumValue
                        , context.getResources().getDisplayMetrics());
        double maximumTargetD2RadiusAbsoluteValuePixelsPerSecondPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) variableValuesD2Radius.maximumValue
                        , context.getResources().getDisplayMetrics());

        // Get the position evolvers
        PositionEvolver positionEvolverXPixels = getPositionEvolver(POSITION_EVOLVER_NAME_X);
        PositionEvolver positionEvolverRadiusPixels = getPositionEvolver(POSITION_EVOLVER_NAME_RADIUS);
        PositionEvolver positionEvolverRotationRadians = getPositionEvolver(POSITION_EVOLVER_NAME_ROTATION);


        // -------------------- BEGIN D2Position -------------------- //
        double targetDSpeedPixelsPerSecondPerSecond = 0.0;
        double targetDDirectionAngleRadiansPerSecond = 0.0;
        // DSpeed
        if (!initializeClickTarget && continuousDSpeed == true) {
            if (positionEvolverXPixels != null
                    && positionEvolverXPixels.getPositionEvolverDXdt() != null
                    && positionEvolverXPixels.getPositionEvolverDXdt().getPositionEvolverDXdt() != null) {

                PositionEvolver positionEvolverD2Xdt2Pixels = positionEvolverXPixels.getPositionEvolverDXdt().getPositionEvolverDXdt();
                targetDSpeedPixelsPerSecondPerSecond = positionEvolverD2Xdt2Pixels.getX().getValue(0);

            }
        } else {
            if (variableValuesSpeed.canChange) {

                if (variableValuesDSpeed.randomInitialValue) {
                    targetDSpeedPixelsPerSecondPerSecond =
                            UtilityFunctions.generateRandomValue(
                                    minimumTargetDSpeedAbsoluteValuePixelsPerSecondPerSecond
                                    , maximumTargetDSpeedAbsoluteValuePixelsPerSecondPerSecond
                                    , true);
                } else {
                    targetDSpeedPixelsPerSecondPerSecond = initialTargetDSpeedAbsoluteValuePixelsPerSecondPerSecond;
                    if (variableValuesDSpeed.randomInitialSign) {
                        targetDSpeedPixelsPerSecondPerSecond *= UtilityFunctions.getRandomSign();
                    }
                }

            }
        }
        // Direction Angle Change
        if (!initializeClickTarget && continuousDDirection) {
            if (positionEvolverXPixels != null
                    && positionEvolverXPixels.getPositionEvolverDXdt() != null
                    && positionEvolverXPixels.getPositionEvolverDXdt().getPositionEvolverDXdt() != null) {

                PositionEvolver positionEvolverD2Xdt2Pixels = positionEvolverXPixels.getPositionEvolverDXdt().getPositionEvolverDXdt();
                targetDDirectionAngleRadiansPerSecond = positionEvolverD2Xdt2Pixels.getX().getValue(1);

            }
        } else {
            if (variableValuesDirection.canChange) {

                if (variableValuesDDirection.randomInitialValue) {
                    targetDDirectionAngleRadiansPerSecond =
                            UtilityFunctions.generateRandomValue(
                                    variableValuesDDirection.minimumValue
                                    , variableValuesDDirection.maximumValue
                                    , true);
                } else {
                    targetDDirectionAngleRadiansPerSecond = variableValuesDDirection.initialValue;
                    if (variableValuesDDirection.randomInitialSign) {
                        targetDDirectionAngleRadiansPerSecond *= UtilityFunctions.getRandomSign();
                    }
                }

            }
        }

        // Create the variables list
        ArrayList<PositionEvolverVariable> arrayListPositionEvolverVariablesD2X = new ArrayList<>();
        PositionEvolverVariable dSpeed = variableValuesDSpeed.toPositionEvolverVariable(
                minimumTargetDSpeedAbsoluteValuePixelsPerSecondPerSecond
                , targetDSpeedPixelsPerSecondPerSecond
                , maximumTargetDSpeedAbsoluteValuePixelsPerSecondPerSecond
        );
        PositionEvolverVariable dDirection = variableValuesDDirection.toPositionEvolverVariable(
                targetDDirectionAngleRadiansPerSecond
        );
        arrayListPositionEvolverVariablesD2X.add(dSpeed);
        arrayListPositionEvolverVariablesD2X.add(dDirection);

        // Create the position evolver
        PositionEvolver targetD2Xdt2Pixels = new PositionEvolver(
                POSITION_EVOLVER_NAME_D2X
                , arrayListPositionEvolverVariablesD2X
                , null
                , PositionEvolver.MODE.POLAR_2D
                , new PositionEvolver.RandomChangeEffect()
        );
        // -------------------- END D2Position -------------------- //



        // -------------------- BEGIN DPosition -------------------- //
        double targetSpeedPixelsPerSecond = 0.0;
        double targetDirectionAngleRadians = 0.0;
        // Speed
        if (!initializeClickTarget && continuousSpeed) {
            if (positionEvolverXPixels != null
                    && positionEvolverXPixels.getPositionEvolverDXdt() != null) {
                PositionEvolver positionEvolverDXdtPixels = positionEvolverXPixels.getPositionEvolverDXdt();
                targetSpeedPixelsPerSecond = positionEvolverDXdtPixels.getX().getValue(0);
            }
        } else {
            if (variableValuesPositionHorizontal.canChange) {
                if (variableValuesSpeed.randomInitialValue) {
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
                targetDirectionAngleRadians = positionEvolverDXdtPixels.getX().getValue(1);
            }
        } else {
            if (variableValuesPositionHorizontal.canChange) {
                if (variableValuesDirection.randomInitialValue) {
                    targetDirectionAngleRadians =
                            UtilityFunctions.generateRandomValue(
                                    0.0
                                    , 2.0 * Math.PI
                                    , false);
                } else {
                    targetDirectionAngleRadians = variableValuesDirection.initialValue;
                }
            }
        }

        // Create the variables list
        ArrayList<PositionEvolverVariable> arrayListPositionEvolverVariablesDX = new ArrayList<>();
        PositionEvolverVariable speed = variableValuesSpeed.toPositionEvolverVariable(
                minimumTargetSpeedPixelsPerSecond
                , targetSpeedPixelsPerSecond
                , maximumTargetSpeedPixelsPerSecond
        );
        PositionEvolverVariable direction = variableValuesDirection.toPositionEvolverVariable(
                targetDirectionAngleRadians
        );
        arrayListPositionEvolverVariablesDX.add(speed);
        arrayListPositionEvolverVariablesDX.add(direction);

        // Create the position evolver
        PositionEvolver targetDXdtPixels = new PositionEvolver(
                POSITION_EVOLVER_NAME_DX
                , arrayListPositionEvolverVariablesDX
                , targetD2Xdt2Pixels
                , PositionEvolver.MODE.POLAR_2D
                , new PositionEvolver.RandomChangeEffect()
        );
        // -------------------- END DPosition -------------------- //


        // -------------------- BEGIN Position -------------------- //
        // Calculate the boundaries
        double minimumPixelsX = 0.0;
        double maximumPixelsX = canvasWidth;
        double minimumPixelsY = 0.0;
        double maximumPixelsY = canvasHeight;
        double currentRadiusPixels = initialTargetRadiusPixels;
        if (!initializeClickTarget
                && positionEvolverRadiusPixels != null) {
            currentRadiusPixels = positionEvolverRadiusPixels.getX().getValue(0);
        }
        if (variableValuesPositionHorizontal.boundaryEffect.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.BOUNCE
                || variableValuesPositionHorizontal.boundaryEffect.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.STICK) {
            minimumPixelsX = currentRadiusPixels / 2.0;
            maximumPixelsX = canvasWidth - (currentRadiusPixels / 2.0);
        }
        if (variableValuesPositionVertical.boundaryEffect.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.BOUNCE
                || variableValuesPositionVertical.boundaryEffect.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.STICK) {
            minimumPixelsY = currentRadiusPixels / 2.0;
            maximumPixelsY = canvasHeight - (currentRadiusPixels / 2.0);
        }

        double width = maximumPixelsX - minimumPixelsX;
        double height = maximumPixelsY - minimumPixelsY;


        double XPixels = 0.0;
        double YPixels = 0.0;
        if (!initializeClickTarget && continuousX) {
            if (positionEvolverXPixels != null) {
                XPixels = positionEvolverXPixels.getX().getValue(0);
                YPixels = positionEvolverXPixels.getX().getValue(1);
            }
        } else {

            // Check if we need to renormalize the min and max position values based on boundary effect
            if (variableValuesPositionHorizontal.boundaryEffect.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.PERIODIC
                    || variableValuesPositionHorizontal.boundaryEffect.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.PERIODIC_REFLECTIVE) {
                minimumPixelsX = 0.0;
                maximumPixelsX = width;
            }
            if (variableValuesPositionVertical.boundaryEffect.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.PERIODIC
                    || variableValuesPositionVertical.boundaryEffect.boundaryEffect == PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.PERIODIC_REFLECTIVE) {
                minimumPixelsY = 0.0;
                maximumPixelsY = height;
            }

            // Set starting position
            // Check if we should randomize the starting X position
            if (variableValuesPositionHorizontal.randomInitialValue) {
                XPixels = minimumPixelsX + (UtilityFunctions.generateRandomValue(0.0, width, false));
            } else { // Do not randomize starting position
                // Check if we should center
                if (variableValuesPositionHorizontal.initialValue == -1.0) {
                    XPixels = minimumPixelsX + (width / 2.0);
                } else { // Use specific starting position
                    XPixels = minimumPixelsX + (width * variableValuesPositionHorizontal.initialValue);
                }

            }
            // Check if we should randomize the starting Y position
            if (variableValuesPositionVertical.randomInitialValue) {
                YPixels = minimumPixelsY + (UtilityFunctions.generateRandomValue(0.0, height, false));
            } else { // Do not randomize starting position
                // Check if we should center
                if (variableValuesPositionVertical.initialValue == -1.0) {
                    YPixels = minimumPixelsY + (height / 2.0);
                } else { // Use specific starting position
                    YPixels = minimumPixelsY + (height * variableValuesPositionVertical.initialValue);
                }

            }
        }

        // Create the variables list
        ArrayList<PositionEvolverVariable> arrayListPositionEvolverVariablesX = new ArrayList<>();
        PositionEvolverVariable x = variableValuesPositionHorizontal.toPositionEvolverVariable(
                minimumPixelsX
                , XPixels
                , maximumPixelsX
        );
        PositionEvolverVariable y = variableValuesPositionVertical.toPositionEvolverVariable(
                minimumPixelsY
                , YPixels
                , maximumPixelsY
        );
        arrayListPositionEvolverVariablesX.add(x);
        arrayListPositionEvolverVariablesX.add(y);

        // Create the position evolver
        PositionEvolver targetXPixels = new PositionEvolver(
                POSITION_EVOLVER_NAME_X
                , arrayListPositionEvolverVariablesX
                , targetDXdtPixels
                , PositionEvolver.MODE.CARTESIAN_2D
                , variableValuesPositionHorizontal.randomChangeEffect
        );
        // -------------------- END Position -------------------- //



        // -------------------- BEGIN D2Radius -------------------- //
        double targetD2RadiusPixels = 0.0;

        // D2Radius
        if (!initializeClickTarget && continuousD2Radius) {
            if (positionEvolverRadiusPixels != null
                    && positionEvolverRadiusPixels.getPositionEvolverDXdt() != null
                    && positionEvolverRadiusPixels.getPositionEvolverDXdt().getPositionEvolverDXdt() != null) {
                PositionEvolver positionEvolverDRadiusChangePixels = positionEvolverRadiusPixels.getPositionEvolverDXdt().getPositionEvolverDXdt();
                targetD2RadiusPixels = positionEvolverDRadiusChangePixels.getX().getValue(0);
            }
        } else {
            if (variableValuesDRadius.canChange) {
                if (variableValuesD2Radius.randomInitialValue) {
                    targetD2RadiusPixels = UtilityFunctions.generateRandomValue(
                            minimumTargetD2RadiusAbsoluteValuePixelsPerSecondPerSecond
                            , maximumTargetD2RadiusAbsoluteValuePixelsPerSecondPerSecond
                            , true);
                } else {
                    targetD2RadiusPixels = initialTargetD2RadiusAbsoluteValuePixelsPerSecondPerSecond;
                    if (variableValuesD2Radius.randomInitialSign) {
                        targetD2RadiusPixels *= UtilityFunctions.getRandomSign();
                    }
                }
            }
        }

        // Create the variables list
        ArrayList<PositionEvolverVariable> arrayListPositionEvolverVariablesD2Radius = new ArrayList<>();
        PositionEvolverVariable d2Radius = variableValuesD2Radius.toPositionEvolverVariable(
                minimumTargetD2RadiusAbsoluteValuePixelsPerSecondPerSecond
                , targetD2RadiusPixels
                , maximumTargetD2RadiusAbsoluteValuePixelsPerSecondPerSecond
        );
        arrayListPositionEvolverVariablesD2Radius.add(d2Radius);

        // Create the position evolver
        PositionEvolver targetD2RadiusDt2Pixels = new PositionEvolver(
                POSITION_EVOLVER_NAME_D2RADIUS
                , arrayListPositionEvolverVariablesD2Radius
                , null
                , PositionEvolver.MODE.ONE_DIMENSION
                , new PositionEvolver.RandomChangeEffect()
        );
        // -------------------- END D2Radius -------------------- //



        // -------------------- BEGIN DRadius -------------------- //
        double targetDRadiusPixels = 0.0;

        // DRadius
        if (!initializeClickTarget && continuousDRadius) {
            if (positionEvolverRadiusPixels != null
                    && positionEvolverRadiusPixels.getPositionEvolverDXdt() != null) {
                PositionEvolver positionEvolverDRadiusPixels = positionEvolverRadiusPixels.getPositionEvolverDXdt();
                targetDRadiusPixels = positionEvolverDRadiusPixels.getX().getValue(0);
            }
        } else {
            if (variableValuesRadius.canChange) {
                if (variableValuesDRadius.randomInitialValue) {
                    targetDRadiusPixels = UtilityFunctions.generateRandomValue(
                            minimumTargetDRadiusAbsoluteValuePixelsPerSecond
                            , maximumTargetDRadiusAbsoluteValuePixelsPerSecond
                            , true);
                } else {
                    targetDRadiusPixels = initialTargetDRadiusAbsoluteValuePixelsPerSecond;
                    if (variableValuesDRadius.randomInitialSign) {
                        targetDRadiusPixels *= UtilityFunctions.getRandomSign();
                    }
                }
            }
        }

        // Create the variables list
        ArrayList<PositionEvolverVariable> arrayListPositionEvolverVariablesDRadius = new ArrayList<>();
        PositionEvolverVariable dRadius = variableValuesDRadius.toPositionEvolverVariable(
                minimumTargetDRadiusAbsoluteValuePixelsPerSecond
                , targetDRadiusPixels
                , maximumTargetDRadiusAbsoluteValuePixelsPerSecond
        );
        arrayListPositionEvolverVariablesDRadius.add(dRadius);

        // Create the position evolver
        PositionEvolver targetDRadiusDtPixels = new PositionEvolver(
                POSITION_EVOLVER_NAME_DRADIUS
                , arrayListPositionEvolverVariablesDRadius
                , targetD2RadiusDt2Pixels
                , PositionEvolver.MODE.ONE_DIMENSION
                , new PositionEvolver.RandomChangeEffect()
        );
        // -------------------- END DRadius -------------------- //



        // -------------------- BEGIN Radius -------------------- //
        double radiusPixels = 0.0;

        // Radius
        if (!initializeClickTarget && continuousRadius) {
            if (positionEvolverRadiusPixels != null) {
                radiusPixels = positionEvolverRadiusPixels.getX().getValue(0);
            }
        } else {
            if (variableValuesRadius.randomInitialValue) {
                radiusPixels = UtilityFunctions.generateRandomValue(
                        minimumTargetRadiusPixels
                        , maximumTargetRadiusPixels
                        , false);
            } else {
                radiusPixels = initialTargetRadiusPixels;
            }
        }

        // Create the variables list
        ArrayList<PositionEvolverVariable> arrayListPositionEvolverVariablesRadius = new ArrayList<>();
        PositionEvolverVariable radius = variableValuesRadius.toPositionEvolverVariable(
                minimumTargetRadiusPixels
                , radiusPixels
                , maximumTargetRadiusPixels
        );
        arrayListPositionEvolverVariablesRadius.add(radius);

        // Create the position evolver
        PositionEvolver targetRadiusPixels = new PositionEvolver(
                POSITION_EVOLVER_NAME_RADIUS
                , arrayListPositionEvolverVariablesRadius
                , targetDRadiusDtPixels
                , PositionEvolver.MODE.ONE_DIMENSION
                , new PositionEvolver.RandomChangeEffect()
        );
        // -------------------- END Radius -------------------- //



        // -------------------- BEGIN D2Rotation -------------------- //
        double targetD2RotationRadians = 0.0;
        // D2Rotation
        if (!initializeClickTarget && continuousD2Rotation) {
            if (positionEvolverRotationRadians != null
                    && positionEvolverRotationRadians.getPositionEvolverDXdt() != null
                    && positionEvolverRotationRadians.getPositionEvolverDXdt().getPositionEvolverDXdt() != null) {
                PositionEvolver positionEvolverD2RotationRadians = positionEvolverRotationRadians.getPositionEvolverDXdt().getPositionEvolverDXdt();
                targetD2RotationRadians = positionEvolverD2RotationRadians.getX().getValue(0);
            }
        } else {
            if (variableValuesDRotation.canChange) {
                if (variableValuesD2Rotation.randomInitialValue) {
                    targetD2RotationRadians = UtilityFunctions.generateRandomValue(
                            variableValuesD2Rotation.minimumValue
                            , variableValuesD2Rotation.maximumValue
                            , true);
                } else {
                    targetD2RotationRadians = variableValuesD2Rotation.initialValue;
                    if (variableValuesD2Rotation.randomInitialSign) {
                        targetD2RotationRadians *= UtilityFunctions.getRandomSign();
                    }
                }
            }
        }

        // Create the variables list
        ArrayList<PositionEvolverVariable> arrayListPositionEvolverVariablesD2Rotation = new ArrayList<>();
        PositionEvolverVariable d2Rotation = variableValuesD2Rotation.toPositionEvolverVariable(targetD2RotationRadians);
        arrayListPositionEvolverVariablesD2Rotation.add(d2Rotation);

        // Create the position evolver
        PositionEvolver targetD2RotationRadiansPerSecondPerSecond = new PositionEvolver(
                POSITION_EVOLVER_NAME_D2ROTATION
                , arrayListPositionEvolverVariablesD2Rotation
                , null
                , PositionEvolver.MODE.ONE_DIMENSION
                , new PositionEvolver.RandomChangeEffect()
        );
        // -------------------- END D2Rotation -------------------- //



        // -------------------- BEGIN DRotation -------------------- //
        double targetDRotationRadians = 0.0;
        // DRotation
        if (!initializeClickTarget && continuousDRotation) {
            if (positionEvolverRotationRadians != null
                    && positionEvolverRotationRadians.getPositionEvolverDXdt() != null) {
                PositionEvolver positionEvolverDRotationRadians = positionEvolverRotationRadians.getPositionEvolverDXdt();
                targetDRotationRadians = positionEvolverDRotationRadians.getX().getValue(0);
            }
        } else {
            if (variableValuesRotation.canChange) {
                if (variableValuesDRotation.randomInitialValue) {
                    targetDRotationRadians =
                            UtilityFunctions.generateRandomValue(
                                    variableValuesDRotation.minimumValue
                                    , variableValuesDRotation.maximumValue
                                    , true);
                } else {
                    targetDRotationRadians = variableValuesDRotation.initialValue;
                    if (variableValuesDRotation.randomInitialSign) {
                        double randomSign = UtilityFunctions.getRandomSign();
                        targetDRotationRadians *= randomSign;
                    }
                }
            }
        }

        // Create the variables list
        ArrayList<PositionEvolverVariable> arrayListPositionEvolverVariablesDRotation = new ArrayList<>();
        PositionEvolverVariable dRotation = variableValuesDRotation.toPositionEvolverVariable(targetDRotationRadians);
        arrayListPositionEvolverVariablesDRotation.add(dRotation);

        // Create the position evolver
        PositionEvolver targetDRotationRadiansPerSecond = new PositionEvolver(
                POSITION_EVOLVER_NAME_DROTATION
                , arrayListPositionEvolverVariablesDRotation
                , targetD2RotationRadiansPerSecondPerSecond
                , PositionEvolver.MODE.ONE_DIMENSION
                , new PositionEvolver.RandomChangeEffect()
        );
        // -------------------- END DRotation -------------------- //



        // -------------------- BEGIN Rotation -------------------- //
        double rotationRadians = 0.0;
        // Rotation
        if (!initializeClickTarget && continuousRotation) {
            if (positionEvolverRotationRadians != null) {
                rotationRadians = positionEvolverRotationRadians.getX().getValue(0);
            }
        } else {
            if (variableValuesRotation.randomInitialValue) {
                rotationRadians = UtilityFunctions.generateRandomValue(
                        variableValuesRotation.minimumValue
                        , variableValuesRotation.maximumValue
                        , false);
            } else {
                rotationRadians = variableValuesRotation.initialValue;
            }
        }

        // Create the variables list
        ArrayList<PositionEvolverVariable> arrayListPositionEvolverVariablesRotation = new ArrayList<>();
        PositionEvolverVariable rotation = variableValuesRotation.toPositionEvolverVariable(rotationRadians);
        arrayListPositionEvolverVariablesRotation.add(rotation);

        // Create the position evolver
        PositionEvolver targetRotationRadians = new PositionEvolver(
                POSITION_EVOLVER_NAME_ROTATION
                , arrayListPositionEvolverVariablesRotation
                , targetDRotationRadiansPerSecond
                , PositionEvolver.MODE.ONE_DIMENSION
                , new PositionEvolver.RandomChangeEffect()
        );
        // -------------------- END Rotation -------------------- //

        // First clear the position evolver list
        this.listPositionEvolvers.clear();

        // Add the new position evolvers to the list
        this.listPositionEvolvers.add(targetXPixels);
        this.listPositionEvolvers.add(targetRadiusPixels);
        this.listPositionEvolvers.add(targetRotationRadians);

        // Get the polygon
        polygonTargetShape = PolygonHelper.getPolygon(context, clickTargetProfile.shape);

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

        // Set the status
        isClickable = clickTargetProfile.isClickable;
        visibility = clickTargetProfile.visibility;

    }

    public static class RandomChangeEvent {
        public final String clickTargetName;
        public final String clickTargetProfileName;
        public final String variable;
        public RandomChangeEvent(
                String clickTargetName
                , String clickTargetProfileName
                , String variable) {
            this.clickTargetName = clickTargetName;
            this.clickTargetProfileName = clickTargetProfileName;
            this.variable = variable;
        }

        public NTuple toRandomChangeTriggerKey() {
            return LevelDefinitionLadder.nTupleTypeRandomChangeTriggerKey.createNTuple(
                    clickTargetName
                    , clickTargetProfileName
                    , variable
            );
        }
    }

    public static class ClickTargetProfileTransitionEvent {
        public final String clickTargetName;
        public final String clickTargetProfileName;
        public ClickTargetProfileTransitionEvent(
                String clickTargetName
                , String clickTargetProfileName) {
            this.clickTargetName = clickTargetName;
            this.clickTargetProfileName = clickTargetProfileName;
        }

        public NTuple toTransitionTriggerKey() {
            return LevelDefinitionLadder.nTupleTypeTransitionTriggerKey.createNTuple(
                    clickTargetName
                    , clickTargetProfileName
            );
        }
    }

    public enum VISIBILITY {
        VISIBLE
        , HIDDEN
        , GONE
    }

    private HashMap<String, String> mapTranslateVariableNameToPositionEvolverName = new HashMap<String, String>() {{
        put(VARIABLE_NAME_X, POSITION_EVOLVER_NAME_X);
        put(VARIABLE_NAME_Y, POSITION_EVOLVER_NAME_X);
        put(VARIABLE_NAME_SPEED, POSITION_EVOLVER_NAME_DX);
        put(VARIABLE_NAME_DIRECTION, POSITION_EVOLVER_NAME_DX);
        put(VARIABLE_NAME_DSPEED, POSITION_EVOLVER_NAME_D2X);
        put(VARIABLE_NAME_DDIRECTION, POSITION_EVOLVER_NAME_D2X);
        put(VARIABLE_NAME_RADIUS, POSITION_EVOLVER_NAME_RADIUS);
        put(VARIABLE_NAME_DRADIUS, POSITION_EVOLVER_NAME_DRADIUS);
        put(VARIABLE_NAME_D2RADIUS, POSITION_EVOLVER_NAME_D2RADIUS);
        put(VARIABLE_NAME_ROTATION, POSITION_EVOLVER_NAME_ROTATION);
        put(VARIABLE_NAME_DROTATION, POSITION_EVOLVER_NAME_DROTATION);
        put(VARIABLE_NAME_D2ROTATION, POSITION_EVOLVER_NAME_D2ROTATION);
    }};

}
