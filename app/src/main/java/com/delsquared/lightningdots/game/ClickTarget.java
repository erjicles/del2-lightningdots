package com.delsquared.lightningdots.game;


import android.content.Context;

import com.delsquared.lightningdots.utilities.BoundaryEffect;
import com.delsquared.lightningdots.utilities.BoundaryType;
import com.delsquared.lightningdots.utilities.CoordinateSystemType;
import com.delsquared.lightningdots.utilities.IPositionEvolvingPolygonalObject;
import com.delsquared.lightningdots.utilities.LightningDotsApplication;
import com.delsquared.lightningdots.utilities.OrderedObjectCollection;
import com.delsquared.lightningdots.utilities.Polygon;
import com.delsquared.lightningdots.utilities.PolygonHelper;
import com.delsquared.lightningdots.utilities.PositionEvolver;
import com.delsquared.lightningdots.utilities.PositionEvolverFamily;
import com.delsquared.lightningdots.utilities.PositionEvolverVariable;
import com.delsquared.lightningdots.utilities.PositionVector;
import com.delsquared.lightningdots.utilities.TimedChangeHandler;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClickTarget implements IPositionEvolvingPolygonalObject {

    public static final String POSITION_EVOLVER_FAMILY_NAME_X = "X";
    public static final String POSITION_EVOLVER_FAMILY_NAME_RADIUS = "radius";
    public static final String POSITION_EVOLVER_FAMILY_NAME_ROTATION = "rotation";

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

    private final String name;
    private final ClickTargetProfileScript clickTargetProfileScript;
    private OrderedObjectCollection<PositionEvolverFamily> collectionPositionEvolverFamilies;
    private Polygon polygonTargetShape = null;
    private boolean isClickable = true;
    private VISIBILITY visibility = VISIBILITY.VISIBLE;

    private final Context context;
    private double canvasWidth;
    private double canvasHeight;

    public ClickTarget(
            Context context
            , String name
            , ClickTargetProfileScript clickTargetProfileScript
            , double canvasWidth
            , double canvasHeight) {
        this.name = name;
        this.clickTargetProfileScript = clickTargetProfileScript;
        this.context = context;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        collectionPositionEvolverFamilies = new OrderedObjectCollection<>();

        // Initialize the click target to its current profile
        transitionClickTargetProfile(true);
	}

    public void setCurrentProfile(String profileName) {
        setCurrentProfile(profileName, false);
    }

    public void setCurrentProfile(String profileName, boolean initializeClickTarget) {
        clickTargetProfileScript.setCurrentClickTargetProfileName(profileName);
        transitionClickTargetProfile(initializeClickTarget);
    }

    public OrderedObjectCollection<PositionEvolverFamily> getCollectionPositionEvolverFamilies() {
        return collectionPositionEvolverFamilies;
    }

    public PositionEvolverFamily getPositionEvolverFamily(String positionEvolverFamilyName) {
        return collectionPositionEvolverFamilies.getObject(positionEvolverFamilyName);
    }

    public String getCurrentProfileName() {
        return getCurrentClickTargetProfileName();
    }

    public List<String> getListProfileNames() {
        return clickTargetProfileScript.getListClickTargetProfileNames();
    }

    public double getRadiusPixels() {
        PositionEvolverFamily positionEvolverFamilyRadius = collectionPositionEvolverFamilies.getObject(POSITION_EVOLVER_NAME_RADIUS);
        if (positionEvolverFamilyRadius != null) {
            PositionEvolver positionEvolverRadius = positionEvolverFamilyRadius.getPositionEvolver(0);
            if (positionEvolverRadius != null) {
                return positionEvolverRadius.getX().getValue(0);
            }
        }
        return 0.0;
    }

    public PositionEvolver getPositionEvolver(String positionEvolverName) {
        if (mapTranslatePositionEvolverNameToPositionEvolverFamilyName.containsKey(positionEvolverName)) {
            PositionEvolverFamily positionEvolverFamily =
                    collectionPositionEvolverFamilies.getObject(
                            mapTranslatePositionEvolverNameToPositionEvolverFamilyName.get(positionEvolverName));
            if (positionEvolverFamily != null) {
                return positionEvolverFamily.getPositionEvolver(positionEvolverName);
            }
        }
        return null;
    }

    public PositionEvolver getPositionEvolverX() { return getPositionEvolver(POSITION_EVOLVER_NAME_X); }
    public PositionEvolver getPositionEvolverRadius() { return getPositionEvolver(POSITION_EVOLVER_NAME_RADIUS); }
    public PositionEvolver getPositionEvolverRotation() { return getPositionEvolver(POSITION_EVOLVER_NAME_ROTATION); }

    public PositionEvolver getPositionEvolverFromVariableName(String variableName) {
        String positionEvolverName;
        if (mapTranslateVariableNameToPositionEvolverName.containsKey(variableName)) {
            positionEvolverName = mapTranslateVariableNameToPositionEvolverName.get(variableName);
            return getPositionEvolver(positionEvolverName);
        }
        return null;
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

    @SuppressWarnings("unused")
    public double getOldVariableValue(String variableName) {

        // Get the position evolver
        PositionEvolver positionEvolver = getPositionEvolverFromVariableName(variableName);

        if (positionEvolver != null) {

            // Return the value
            return positionEvolver.getOldVariableValue(variableName);

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

    public Polygon getPolygon() { return this.polygonTargetShape; }

    public double getMass() {
        ClickTargetProfile clickTargetProfile = clickTargetProfileScript.getCurrentClickTargetProfile();
        if (clickTargetProfile != null) {
            return clickTargetProfile.mass;
        }
        return 1.0;
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
        BoundaryEffect boundaryEffectX = positionEvolverXPixels.getBoundaryEffect(VARIABLE_NAME_X);
        BoundaryEffect boundaryEffectY = positionEvolverXPixels.getBoundaryEffect(VARIABLE_NAME_Y);

        // Check if either X or Y has periodic boundary effect
        // If not, then there won't be any secondary points
        if (boundaryEffectX.boundaryType == BoundaryType.PERIODIC
                || boundaryEffectX.boundaryType == BoundaryType.PERIODIC_REFLECTIVE
                || boundaryEffectY.boundaryType == BoundaryType.PERIODIC
                || boundaryEffectY.boundaryType == BoundaryType.PERIODIC_REFLECTIVE) {

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

                if (boundaryEffectX.boundaryType == BoundaryType.PERIODIC) {

                    if (minimumBoundaryReachedX) {
                        double overflow = (minimumX - radiusMinimumX) % widthX;
                        newTargetX1 = maximumX - overflow + radius;
                    }

                    if (maximumBoundaryReachedX) {
                        double overflow = (radiusMaximumX - maximumX) % widthX;
                        newTargetX2 = minimumX + overflow - radius;
                    }

                } else if (boundaryEffectX.boundaryType == BoundaryType.PERIODIC_REFLECTIVE) {

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

                if (boundaryEffectY.boundaryType == BoundaryType.PERIODIC) {

                    if (minimumBoundaryReachedY) {
                        double overflow = (minimumY - radiusMinimumY) % heightY;
                        newTargetY1 = maximumY - overflow + radius;
                    }

                    if (maximumBoundaryReachedY) {
                        double overflow = (radiusMaximumY - maximumY) % heightY;
                        newTargetY2 = minimumY + overflow - radius;
                    }

                } else if (boundaryEffectY.boundaryType == BoundaryType.PERIODIC_REFLECTIVE) {

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
                //noinspection SuspiciousNameCombination
                listCenterPoints.add(new PositionVector(newTargetX1, targetY));
            }
            if (maximumBoundaryReachedX) {
                //newTargetX2, targetY;
                //noinspection SuspiciousNameCombination
                listCenterPoints.add(new PositionVector(newTargetX2, targetY));
            }
            if (minimumBoundaryReachedY) {
                //targetX, newTargetY1;
                //noinspection SuspiciousNameCombination
                listCenterPoints.add(new PositionVector(targetX, newTargetY1));
            }
            if (maximumBoundaryReachedY) {
                //targetX, newTargetY2;
                //noinspection SuspiciousNameCombination
                listCenterPoints.add(new PositionVector(targetX, newTargetY2));
            }
            if (minimumBoundaryReachedX && minimumBoundaryReachedY) {
                //newTargetX1, newTargetY1;
                //noinspection SuspiciousNameCombination
                listCenterPoints.add(new PositionVector(newTargetX1, newTargetY1));
            }
            if (minimumBoundaryReachedX && maximumBoundaryReachedY) {
                //newTargetX1, newTargetY2;
                //noinspection SuspiciousNameCombination
                listCenterPoints.add(new PositionVector(newTargetX1, newTargetY2));
            }
            if (maximumBoundaryReachedX && minimumBoundaryReachedY) {
                //newTargetX2, newTargetY1;
                //noinspection SuspiciousNameCombination
                listCenterPoints.add(new PositionVector(newTargetX2, newTargetY1));
            }
            if (maximumBoundaryReachedX && maximumBoundaryReachedY) {
                //newTargetX2, newTargetY2;
                //noinspection SuspiciousNameCombination
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

        BoundaryEffect boundaryEffectX = positionEvolverXPixels.getBoundaryEffect(VARIABLE_NAME_X);
        BoundaryEffect boundaryEffectY = positionEvolverXPixels.getBoundaryEffect(VARIABLE_NAME_Y);

        if (boundaryEffectX.boundaryType == BoundaryType.HARD
                || boundaryEffectX.boundaryType == BoundaryType.STICKY) {
            minimumX = radius / 2.0;
            maximumX = canvasWidth - (radius / 2.0);
        }
        if (boundaryEffectY.boundaryType == BoundaryType.HARD
                || boundaryEffectY.boundaryType == BoundaryType.STICKY) {
            minimumY = radius / 2.0;
            maximumY = canvasHeight - (radius / 2.0);
        }
        positionEvolverXPixels.setBoundaryValues(VARIABLE_NAME_X, minimumX, maximumX);
        positionEvolverXPixels.setBoundaryValues(VARIABLE_NAME_Y, minimumY, maximumY);

    }

    public boolean checkProfileTransition(double dt) {
        return clickTargetProfileScript.processTransition(dt);
    }

	public ClickTargetSnapshot getClickTargetSnapshot() {

        // Get the position evolvers
        PositionEvolver positionEvolverX = getPositionEvolver(POSITION_EVOLVER_NAME_X);
        PositionEvolver positionEvolverDX = getPositionEvolver(POSITION_EVOLVER_NAME_DX);
        PositionEvolver positionEvolverD2X = getPositionEvolver(POSITION_EVOLVER_NAME_D2X);
        PositionEvolver positionEvolverRadius = getPositionEvolver(POSITION_EVOLVER_NAME_RADIUS);
        PositionEvolver positionEvolverDRadius = getPositionEvolver(POSITION_EVOLVER_NAME_DRADIUS);
        PositionEvolver positionEvolverD2Radius = getPositionEvolver(POSITION_EVOLVER_NAME_D2RADIUS);
        PositionEvolver positionEvolverRotation = getPositionEvolver(POSITION_EVOLVER_NAME_ROTATION);
        PositionEvolver positionEvolverDRotation = getPositionEvolver(POSITION_EVOLVER_NAME_DROTATION);
        PositionEvolver positionEvolverD2Rotation = getPositionEvolver(POSITION_EVOLVER_NAME_D2ROTATION);

        PositionVector targetX = new PositionVector();
        PositionVector targetDX = new PositionVector();
        PositionVector targetD2X = new PositionVector();
        PositionVector targetRadius = new PositionVector();
        PositionVector targetDRadius = new PositionVector();
        PositionVector targetD2Radius = new PositionVector();
        PositionVector targetRotation = new PositionVector();
        PositionVector targetDRotation = new PositionVector();
        PositionVector targetD2Rotation = new PositionVector();
        if (positionEvolverX != null) { targetX = positionEvolverX.getX(); }
        if (positionEvolverDX != null) { targetDX = positionEvolverDX.getX(); }
        if (positionEvolverD2X != null) { targetD2X = positionEvolverD2X.getX(); }
        if (positionEvolverRadius != null) { targetRadius = positionEvolverRadius.getX(); }
        if (positionEvolverDRadius != null) { targetDRadius = positionEvolverDRadius.getX(); }
        if (positionEvolverD2Radius != null) { targetD2Radius = positionEvolverD2Radius.getX(); }
        if (positionEvolverRotation != null) { targetRotation = positionEvolverRotation.getX(); }
        if (positionEvolverDRotation != null) { targetDRotation = positionEvolverDRotation.getX(); }
        if (positionEvolverD2Rotation != null) { targetD2Rotation = positionEvolverD2Rotation.getX(); }

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

    public void transitionClickTargetProfile(boolean initializeClickTarget) {

        ClickTargetProfile clickTargetProfile = clickTargetProfileScript.getCurrentClickTargetProfile();

        if (clickTargetProfile == null) {
            return;
        }

        // Get the position evolvers
        PositionEvolver positionEvolverX = getPositionEvolver(POSITION_EVOLVER_NAME_X);
        PositionEvolver positionEvolverDX = getPositionEvolver(POSITION_EVOLVER_NAME_DX);
        PositionEvolver positionEvolverD2X = getPositionEvolver(POSITION_EVOLVER_NAME_D2X);
        PositionEvolver positionEvolverRadius = getPositionEvolver(POSITION_EVOLVER_NAME_RADIUS);
        PositionEvolver positionEvolverDRadius = getPositionEvolver(POSITION_EVOLVER_NAME_DRADIUS);
        PositionEvolver positionEvolverD2Radius = getPositionEvolver(POSITION_EVOLVER_NAME_D2RADIUS);
        PositionEvolver positionEvolverRotation = getPositionEvolver(POSITION_EVOLVER_NAME_ROTATION);
        PositionEvolver positionEvolverDRotation = getPositionEvolver(POSITION_EVOLVER_NAME_DROTATION);
        PositionEvolver positionEvolverD2Rotation = getPositionEvolver(POSITION_EVOLVER_NAME_D2ROTATION);

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

        if (variableValuesPositionHorizontal == null) {
            LightningDotsApplication.logDebugErrorMessage("variableValuesPositionHorizontal is null");
            return;
        }
        if (variableValuesPositionVertical == null) {
            LightningDotsApplication.logDebugErrorMessage("variableValuesPositionVertical is null");
            return;
        }
        if (variableValuesSpeed == null) {
            LightningDotsApplication.logDebugErrorMessage("variableValuesSpeed is null");
            return;
        }
        if (variableValuesDirection == null) {
            LightningDotsApplication.logDebugErrorMessage("variableValuesDirection is null");
            return;
        }
        if (variableValuesDSpeed == null) {
            LightningDotsApplication.logDebugErrorMessage("variableValuesDSpeed is null");
            return;
        }
        if (variableValuesDDirection == null) {
            LightningDotsApplication.logDebugErrorMessage("variableValuesDDirection is null");
            return;
        }
        if (variableValuesRadius == null) {
            LightningDotsApplication.logDebugErrorMessage("variableValuesRadius is null");
            return;
        }
        if (variableValuesDRadius == null) {
            LightningDotsApplication.logDebugErrorMessage("variableValuesDRadius is null");
            return;
        }
        if (variableValuesD2Radius == null) {
            LightningDotsApplication.logDebugErrorMessage("variableValuesD2Radius is null");
            return;
        }
        if (variableValuesRotation == null) {
            LightningDotsApplication.logDebugErrorMessage("variableValuesRotation is null");
            return;
        }
        if (variableValuesDRotation == null) {
            LightningDotsApplication.logDebugErrorMessage("variableValuesDRotation is null");
            return;
        }
        if (variableValuesD2Rotation == null) {
            LightningDotsApplication.logDebugErrorMessage("variableValuesD2Rotation is null");
            return;
        }

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
        if (!initializeClickTarget) {
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

        // -------------------- BEGIN D2Position -------------------- //
        double targetDSpeedPixelsPerSecondPerSecond = 0.0;
        double targetDDirectionAngleRadiansPerSecond = 0.0;
        // DSpeed
        if (!initializeClickTarget && continuousDSpeed) {
            if (positionEvolverD2X != null) {
                targetDSpeedPixelsPerSecondPerSecond = positionEvolverD2X.getX().getValue(0);
            }
        } else {
            if (variableValuesSpeed.canChange) {

                if (variableValuesDSpeed.randomInitialValue) {
                    targetDSpeedPixelsPerSecondPerSecond =
                            UtilityFunctions.generateRandomValue(
                                    variableValuesDSpeed.minimumValue
                                    , variableValuesDSpeed.maximumValue
                                    , true);
                } else {
                    targetDSpeedPixelsPerSecondPerSecond = variableValuesDSpeed.initialValue;
                    if (variableValuesDSpeed.randomInitialSign) {
                        targetDSpeedPixelsPerSecondPerSecond *= UtilityFunctions.getRandomSign();
                    }
                }

            }
        }
        // Direction Angle Change
        if (!initializeClickTarget && continuousDDirection) {
            if (positionEvolverD2X != null) {
                targetDDirectionAngleRadiansPerSecond = positionEvolverD2X.getX().getValue(1);
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
        List<PositionEvolverVariable> listPositionEvolverVariablesD2X = new ArrayList<>();
        PositionEvolverVariable dSpeed = variableValuesDSpeed.toPositionEvolverVariable(
                variableValuesDSpeed.minimumValue
                , targetDSpeedPixelsPerSecondPerSecond
                , variableValuesDSpeed.maximumValue
        );
        PositionEvolverVariable dDirection = variableValuesDDirection.toPositionEvolverVariable(
                targetDDirectionAngleRadiansPerSecond
        );
        listPositionEvolverVariablesD2X.add(dSpeed);
        listPositionEvolverVariablesD2X.add(dDirection);

        // Create the position evolver
        PositionEvolver positionEvolverD2XNew = new PositionEvolver(
                POSITION_EVOLVER_NAME_D2X
                , listPositionEvolverVariablesD2X
                , CoordinateSystemType.SPHERICAL
                , new TimedChangeHandler()
        );
        // -------------------- END D2Position -------------------- //



        // -------------------- BEGIN DPosition -------------------- //
        double targetSpeedPixelsPerSecond = 0.0;
        double targetDirectionAngleRadians = 0.0;
        // Speed
        if (!initializeClickTarget && continuousSpeed) {
            if (positionEvolverDX != null) {
                targetSpeedPixelsPerSecond = positionEvolverDX.getX().getValue(0);
            }
        } else {
            if (variableValuesPositionHorizontal.canChange) {
                if (variableValuesSpeed.randomInitialValue) {
                    targetSpeedPixelsPerSecond =
                            UtilityFunctions.generateRandomValue(
                                    variableValuesSpeed.minimumValue
                                    , variableValuesSpeed.maximumValue
                                    , false);
                } else {
                    targetSpeedPixelsPerSecond = variableValuesSpeed.initialValue;
                }
            }
        }
        // Direction Angle
        if (!initializeClickTarget && continuousDirection) {
            if (positionEvolverDX != null) {
                targetDirectionAngleRadians = positionEvolverDX.getX().getValue(1);
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
        List<PositionEvolverVariable> listPositionEvolverVariablesDX = new ArrayList<>();
        PositionEvolverVariable speed = variableValuesSpeed.toPositionEvolverVariable(
                variableValuesSpeed.minimumValue
                , targetSpeedPixelsPerSecond
                , variableValuesSpeed.maximumValue
        );
        PositionEvolverVariable direction = variableValuesDirection.toPositionEvolverVariable(
                targetDirectionAngleRadians
        );
        listPositionEvolverVariablesDX.add(speed);
        listPositionEvolverVariablesDX.add(direction);

        // Create the position evolver
        PositionEvolver positionEvolverDXNew = new PositionEvolver(
                POSITION_EVOLVER_NAME_DX
                , listPositionEvolverVariablesDX
                , CoordinateSystemType.SPHERICAL
                , new TimedChangeHandler()
        );
        // -------------------- END DPosition -------------------- //


        // -------------------- BEGIN Position -------------------- //
        // Calculate the boundaries
        double minimumPixelsX = 0.0;
        double maximumPixelsX = canvasWidth;
        double minimumPixelsY = 0.0;
        double maximumPixelsY = canvasHeight;
        double currentRadiusPixels = variableValuesRadius.initialValue;
        if (!initializeClickTarget
                && positionEvolverRadius != null) {
            currentRadiusPixels = positionEvolverRadius.getX().getValue(0);
        }
        if (variableValuesPositionHorizontal.boundaryEffect.boundaryType == BoundaryType.HARD
                || variableValuesPositionHorizontal.boundaryEffect.boundaryType == BoundaryType.STICKY) {
            minimumPixelsX = currentRadiusPixels / 2.0;
            maximumPixelsX = canvasWidth - (currentRadiusPixels / 2.0);
        }
        if (variableValuesPositionVertical.boundaryEffect.boundaryType == BoundaryType.HARD
                || variableValuesPositionVertical.boundaryEffect.boundaryType == BoundaryType.STICKY) {
            minimumPixelsY = currentRadiusPixels / 2.0;
            maximumPixelsY = canvasHeight - (currentRadiusPixels / 2.0);
        }

        double width = maximumPixelsX - minimumPixelsX;
        double height = maximumPixelsY - minimumPixelsY;


        double XPixels = 0.0;
        double YPixels = 0.0;
        if (!initializeClickTarget && continuousX) {
            if (positionEvolverX != null) {
                XPixels = positionEvolverX.getX().getValue(0);
                YPixels = positionEvolverX.getX().getValue(1);
            }
        } else {

            // Check if we need to renormalize the min and max position values based on boundary effect
            if (variableValuesPositionHorizontal.boundaryEffect.boundaryType == BoundaryType.PERIODIC
                    || variableValuesPositionHorizontal.boundaryEffect.boundaryType == BoundaryType.PERIODIC_REFLECTIVE) {
                minimumPixelsX = 0.0;
                maximumPixelsX = width;
            }
            if (variableValuesPositionVertical.boundaryEffect.boundaryType == BoundaryType.PERIODIC
                    || variableValuesPositionVertical.boundaryEffect.boundaryType == BoundaryType.PERIODIC_REFLECTIVE) {
                minimumPixelsY = 0.0;
                maximumPixelsY = height;
            }

            // Set starting position
            // Check if we should randomize the starting X position
            if (variableValuesPositionHorizontal.randomInitialValue) {
                XPixels = minimumPixelsX + (UtilityFunctions.generateRandomValue(0.0, width, false));
            } else { // Do not randomize starting position
                // Set starting position
                XPixels = minimumPixelsX + (width * variableValuesPositionHorizontal.initialValue);
            }
            // Check if we should randomize the starting Y position
            if (variableValuesPositionVertical.randomInitialValue) {
                YPixels = minimumPixelsY + (UtilityFunctions.generateRandomValue(0.0, height, false));
            } else { // Do not randomize starting position
                // Set starting position
                YPixels = minimumPixelsY + (height * variableValuesPositionVertical.initialValue);
            }
        }

        // Create the variables list
        List<PositionEvolverVariable> listPositionEvolverVariablesX = new ArrayList<>();
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
        listPositionEvolverVariablesX.add(x);
        listPositionEvolverVariablesX.add(y);

        // Create the position evolver
        PositionEvolver positionEvolverXNew = new PositionEvolver(
                POSITION_EVOLVER_NAME_X
                , listPositionEvolverVariablesX
                , CoordinateSystemType.CARTESIAN
                , new TimedChangeHandler()
        );
        // -------------------- END Position -------------------- //



        // -------------------- BEGIN D2Radius -------------------- //
        double targetD2RadiusPixels = 0.0;

        // D2Radius
        if (!initializeClickTarget && continuousD2Radius) {
            if (positionEvolverD2Radius != null) {
                targetD2RadiusPixels = positionEvolverD2Radius.getX().getValue(0);
            }
        } else {
            if (variableValuesDRadius.canChange) {
                if (variableValuesD2Radius.randomInitialValue) {
                    targetD2RadiusPixels = UtilityFunctions.generateRandomValue(
                            variableValuesD2Radius.minimumValue
                            , variableValuesD2Radius.maximumValue
                            , true);
                } else {
                    targetD2RadiusPixels = variableValuesD2Radius.initialValue;
                    if (variableValuesD2Radius.randomInitialSign) {
                        targetD2RadiusPixels *= UtilityFunctions.getRandomSign();
                    }
                }
            }
        }

        // Create the variables list
        List<PositionEvolverVariable> listPositionEvolverVariablesD2Radius = new ArrayList<>();
        PositionEvolverVariable d2Radius = variableValuesD2Radius.toPositionEvolverVariable(
                variableValuesD2Radius.minimumValue
                , targetD2RadiusPixels
                , variableValuesD2Radius.maximumValue
        );
        listPositionEvolverVariablesD2Radius.add(d2Radius);

        // Create the position evolver
        PositionEvolver positionEvolverD2RadiusNew = new PositionEvolver(
                POSITION_EVOLVER_NAME_D2RADIUS
                , listPositionEvolverVariablesD2Radius
                , CoordinateSystemType.CARTESIAN
                , new TimedChangeHandler()
        );
        // -------------------- END D2Radius -------------------- //



        // -------------------- BEGIN DRadius -------------------- //
        double targetDRadiusPixels = 0.0;

        // DRadius
        if (!initializeClickTarget && continuousDRadius) {
            if (positionEvolverDRadius != null) {
                targetDRadiusPixels = positionEvolverDRadius.getX().getValue(0);
            }
        } else {
            if (variableValuesRadius.canChange) {
                if (variableValuesDRadius.randomInitialValue) {
                    targetDRadiusPixels = UtilityFunctions.generateRandomValue(
                            variableValuesDRadius.minimumValue
                            , variableValuesDRadius.maximumValue
                            , true);
                } else {
                    targetDRadiusPixels = variableValuesDRadius.initialValue;
                    if (variableValuesDRadius.randomInitialSign) {
                        targetDRadiusPixels *= UtilityFunctions.getRandomSign();
                    }
                }
            }
        }

        // Create the variables list
        List<PositionEvolverVariable> listPositionEvolverVariablesDRadius = new ArrayList<>();
        PositionEvolverVariable dRadius = variableValuesDRadius.toPositionEvolverVariable(
                variableValuesDRadius.minimumValue
                , targetDRadiusPixels
                , variableValuesDRadius.maximumValue
        );
        listPositionEvolverVariablesDRadius.add(dRadius);

        // Create the position evolver
        PositionEvolver positionEvolverDRadiusNew = new PositionEvolver(
                POSITION_EVOLVER_NAME_DRADIUS
                , listPositionEvolverVariablesDRadius
                , CoordinateSystemType.CARTESIAN
                , new TimedChangeHandler()
        );
        // -------------------- END DRadius -------------------- //



        // -------------------- BEGIN Radius -------------------- //
        double radiusPixels = 0.0;

        // Radius
        if (!initializeClickTarget && continuousRadius) {
            if (positionEvolverRadius != null) {
                radiusPixels = positionEvolverRadius.getX().getValue(0);
            }
        } else {
            if (variableValuesRadius.randomInitialValue) {
                radiusPixels = UtilityFunctions.generateRandomValue(
                        variableValuesRadius.minimumValue
                        , variableValuesRadius.maximumValue
                        , false);
            } else {
                radiusPixels = variableValuesRadius.initialValue;
            }
        }

        // Create the variables list
        List<PositionEvolverVariable> listPositionEvolverVariablesRadius = new ArrayList<>();
        PositionEvolverVariable radius = variableValuesRadius.toPositionEvolverVariable(
                variableValuesRadius.minimumValue
                , radiusPixels
                , variableValuesRadius.maximumValue
        );
        listPositionEvolverVariablesRadius.add(radius);

        // Create the position evolver
        PositionEvolver positionEvolverRadiusNew = new PositionEvolver(
                POSITION_EVOLVER_NAME_RADIUS
                , listPositionEvolverVariablesRadius
                , CoordinateSystemType.CARTESIAN
                , new TimedChangeHandler()
        );
        // -------------------- END Radius -------------------- //



        // -------------------- BEGIN D2Rotation -------------------- //
        double targetD2RotationRadians = 0.0;
        // D2Rotation
        if (!initializeClickTarget && continuousD2Rotation) {
            if (positionEvolverD2Rotation != null) {
                targetD2RotationRadians = positionEvolverD2Rotation.getX().getValue(0);
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
        List<PositionEvolverVariable> listPositionEvolverVariablesD2Rotation = new ArrayList<>();
        PositionEvolverVariable d2Rotation = variableValuesD2Rotation.toPositionEvolverVariable(targetD2RotationRadians);
        listPositionEvolverVariablesD2Rotation.add(d2Rotation);

        // Create the position evolver
        PositionEvolver positionEvolverD2RotationNew = new PositionEvolver(
                POSITION_EVOLVER_NAME_D2ROTATION
                , listPositionEvolverVariablesD2Rotation
                , CoordinateSystemType.CARTESIAN
                , new TimedChangeHandler()
        );
        // -------------------- END D2Rotation -------------------- //



        // -------------------- BEGIN DRotation -------------------- //
        double targetDRotationRadians = 0.0;
        // DRotation
        if (!initializeClickTarget && continuousDRotation) {
            if (positionEvolverDRotation != null) {
                targetDRotationRadians = positionEvolverDRotation.getX().getValue(0);
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
        List<PositionEvolverVariable> listPositionEvolverVariablesDRotation = new ArrayList<>();
        PositionEvolverVariable dRotation = variableValuesDRotation.toPositionEvolverVariable(targetDRotationRadians);
        listPositionEvolverVariablesDRotation.add(dRotation);

        // Create the position evolver
        PositionEvolver positionEvolverDRotationNew = new PositionEvolver(
                POSITION_EVOLVER_NAME_DROTATION
                , listPositionEvolverVariablesDRotation
                , CoordinateSystemType.CARTESIAN
                , new TimedChangeHandler()
        );
        // -------------------- END DRotation -------------------- //



        // -------------------- BEGIN Rotation -------------------- //
        double rotationRadians = 0.0;
        // Rotation
        if (!initializeClickTarget && continuousRotation) {
            if (positionEvolverRotation != null) {
                rotationRadians = positionEvolverRotation.getX().getValue(0);
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
        List<PositionEvolverVariable> listPositionEvolverVariablesRotation = new ArrayList<>();
        PositionEvolverVariable rotation = variableValuesRotation.toPositionEvolverVariable(rotationRadians);
        listPositionEvolverVariablesRotation.add(rotation);

        // Create the position evolver
        PositionEvolver positionEvolverRotationNew = new PositionEvolver(
                POSITION_EVOLVER_NAME_ROTATION
                , listPositionEvolverVariablesRotation
                , CoordinateSystemType.CARTESIAN
                , new TimedChangeHandler()
        );
        // -------------------- END Rotation -------------------- //

        // Create the position evolver lists
        List<PositionEvolver> listPositionEvolversX = new ArrayList<>();
        listPositionEvolversX.add(positionEvolverXNew);
        listPositionEvolversX.add(positionEvolverDXNew);
        listPositionEvolversX.add(positionEvolverD2XNew);

        List<PositionEvolver> listPositionEvolversRadius = new ArrayList<>();
        listPositionEvolversRadius.add(positionEvolverRadiusNew);
        listPositionEvolversRadius.add(positionEvolverDRadiusNew);
        listPositionEvolversRadius.add(positionEvolverD2RadiusNew);

        List<PositionEvolver> listPositionEvolversRotation = new ArrayList<>();
        listPositionEvolversRotation.add(positionEvolverRotationNew);
        listPositionEvolversRotation.add(positionEvolverDRotationNew);
        listPositionEvolversRotation.add(positionEvolverD2RotationNew);

        // Create the position evolver families list
        List<PositionEvolverFamily> listPositionEvolverFamilies = new ArrayList<>();
        listPositionEvolverFamilies.add(
                new PositionEvolverFamily(
                        POSITION_EVOLVER_FAMILY_NAME_X
                        , new OrderedObjectCollection<>(listPositionEvolversX)
        ));
        listPositionEvolverFamilies.add(
                new PositionEvolverFamily(
                        POSITION_EVOLVER_FAMILY_NAME_RADIUS
                        , new OrderedObjectCollection<>(listPositionEvolversRadius)
        ));
        listPositionEvolverFamilies.add(
                new PositionEvolverFamily(
                        POSITION_EVOLVER_FAMILY_NAME_ROTATION
                        , new OrderedObjectCollection<>(listPositionEvolversRotation)
        ));

        // Replace the collection of position evolver families
        collectionPositionEvolverFamilies = new OrderedObjectCollection<>(listPositionEvolverFamilies);

        // Get the polygon
        polygonTargetShape = PolygonHelper.getPolygon(context, clickTargetProfile.shape);

        // Check if we have a polygon
        if (polygonTargetShape != null && context != null) {

            // Set the polygon's properties
            //noinspection SuspiciousNameCombination
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

    public enum VISIBILITY {
        VISIBLE
        , HIDDEN
        , GONE
    }
    
    private final HashMap<String, String> mapTranslatePositionEvolverNameToPositionEvolverFamilyName = new HashMap<String, String>() {{
        put(POSITION_EVOLVER_NAME_X, POSITION_EVOLVER_FAMILY_NAME_X);
        put(POSITION_EVOLVER_NAME_DX, POSITION_EVOLVER_FAMILY_NAME_X);
        put(POSITION_EVOLVER_NAME_D2X, POSITION_EVOLVER_FAMILY_NAME_X);
        put(POSITION_EVOLVER_NAME_RADIUS, POSITION_EVOLVER_FAMILY_NAME_RADIUS);
        put(POSITION_EVOLVER_NAME_DRADIUS, POSITION_EVOLVER_FAMILY_NAME_RADIUS);
        put(POSITION_EVOLVER_NAME_D2RADIUS, POSITION_EVOLVER_FAMILY_NAME_RADIUS);
        put(POSITION_EVOLVER_NAME_ROTATION, POSITION_EVOLVER_FAMILY_NAME_ROTATION);
        put(POSITION_EVOLVER_NAME_DROTATION, POSITION_EVOLVER_FAMILY_NAME_ROTATION);
        put(POSITION_EVOLVER_NAME_D2ROTATION, POSITION_EVOLVER_FAMILY_NAME_ROTATION);
    }};

//    private final HashMap<String, String> mapTranslateVariableNameToPositionEvolverFamilyName = new HashMap<String, String>() {{
//        put(VARIABLE_NAME_X, POSITION_EVOLVER_FAMILY_NAME_X);
//        put(VARIABLE_NAME_Y, POSITION_EVOLVER_FAMILY_NAME_X);
//        put(VARIABLE_NAME_SPEED, POSITION_EVOLVER_FAMILY_NAME_X);
//        put(VARIABLE_NAME_DIRECTION, POSITION_EVOLVER_FAMILY_NAME_X);
//        put(VARIABLE_NAME_DSPEED, POSITION_EVOLVER_FAMILY_NAME_X);
//        put(VARIABLE_NAME_DDIRECTION, POSITION_EVOLVER_FAMILY_NAME_X);
//        put(VARIABLE_NAME_RADIUS, POSITION_EVOLVER_FAMILY_NAME_RADIUS);
//        put(VARIABLE_NAME_DRADIUS, POSITION_EVOLVER_FAMILY_NAME_RADIUS);
//        put(VARIABLE_NAME_D2RADIUS, POSITION_EVOLVER_FAMILY_NAME_RADIUS);
//        put(VARIABLE_NAME_ROTATION, POSITION_EVOLVER_FAMILY_NAME_ROTATION);
//        put(VARIABLE_NAME_DROTATION, POSITION_EVOLVER_FAMILY_NAME_ROTATION);
//        put(VARIABLE_NAME_D2ROTATION, POSITION_EVOLVER_FAMILY_NAME_ROTATION);
//    }};

    private final HashMap<String, String> mapTranslateVariableNameToPositionEvolverName = new HashMap<String, String>() {{
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
