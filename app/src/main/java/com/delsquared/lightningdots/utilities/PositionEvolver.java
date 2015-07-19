package com.delsquared.lightningdots.utilities;

import com.delsquared.lightningdots.game.ClickTarget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class PositionEvolver {

    private String name;

    private ArrayList<PositionEvolverVariable> X = new ArrayList<>();
    private PositionEvolver positionEvolverDXdt = null;

    private MODE mode = MODE.CARTESIAN_3D;

    private RandomChangeEffect randomChangeEffect;
    private double totalTimeElapsedSinceLastRandomChangePositionSeconds = 0;

    public PositionEvolver(

            String name

            , ArrayList<PositionEvolverVariable> X
            , PositionEvolver positionEvolverDXdt

            , MODE mode

            , RandomChangeEffect randomChangeEffect) {

        this.name = name;

        this.X = X;
        this.positionEvolverDXdt = positionEvolverDXdt;

        this.mode = mode;

        this.randomChangeEffect = randomChangeEffect;
        this.totalTimeElapsedSinceLastRandomChangePositionSeconds = 0;

    }

    public String getName() { return name; }

    public PositionEvolverVariable getPositionEvolverVariable(String variableName) {

        // Loop through the variables
        for (PositionEvolverVariable currentVariable : X) {

            // Check if we found the variable
            if (currentVariable.getName().contentEquals(variableName)) {

                // Return the current value
                return currentVariable;

            }
        }

        return null;

    }

    public double getVariableValue(String variableName) {

        // Get the PositionEvolverVariable
        PositionEvolverVariable positionEvolverVariable = getPositionEvolverVariable(variableName);

        if (positionEvolverVariable != null) {

            // Return the current value
            return positionEvolverVariable.getValue();

        }

        return 0.0;

    }

    public void setVariableValue(String variableName, double variableValue) {
        setVariableValue(variableName, variableValue, false);
    }

    public void setVariableValue(String variableName, double variableValue, boolean treatAsInitialValue) {

        // Get the PositionEvolverVariable
        PositionEvolverVariable positionEvolverVariable = getPositionEvolverVariable(variableName);

        if (positionEvolverVariable != null) {

            // Check if we should treat this as a reinitialization
            if (treatAsInitialValue) {

                // Reinitialize the value
                positionEvolverVariable.reinitializeValue(variableValue);

            } else {

                // Set the value
                positionEvolverVariable.setValue(variableValue);

            }

        }

    }

    public void randomizeVariableValue(String variableName) {

        // Get the PositionEvolverVariable
        PositionEvolverVariable positionEvolverVariable = getPositionEvolverVariable(variableName);

        if (positionEvolverVariable != null) {

            // Generate random value
            positionEvolverVariable.randomizeValue();

        }

    }

    public PositionVector getX() {

        ArrayList<Double> resultX = new ArrayList<Double>();

        // Loop through the variables
        for (PositionEvolverVariable currentVariable : X) {
            resultX.add(currentVariable.getValue());
        }

        return new PositionVector(resultX);
    }

    public PositionVector getX(MODE convertToMode) {

        if (this.mode == convertToMode) {
            return getX();
        }

        else if (this.mode == MODE.POLAR_2D
                && convertToMode == MODE.CARTESIAN_2D) {
            double X1 = X.get(0).getValue();
            double X2 = X.get(1).getValue();
            return new PositionVector(
                    X1 * Math.cos(X2)
                    , X1 * Math.sin(X2)
            );
        }

        else if (this.mode == MODE.SPHERICAL_3D
                && convertToMode == MODE.CARTESIAN_3D) {
            double X1 = X.get(0).getValue();
            double X2 = X.get(1).getValue();
            double X3 = X.get(2).getValue();
            double sinX3 = Math.sin(X3);
            return new PositionVector(
                    X1 * Math.cos(X2) * sinX3
                    , X1 * Math.sin(X2) * sinX3
                    , X1 * Math.cos(X3)
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

    public double getMinimumValue(String variableName) {

        // Get the PositionEvolverVariable
        PositionEvolverVariable positionEvolverVariable = getPositionEvolverVariable(variableName);

        if (positionEvolverVariable != null) {

            // Get the boundary value
            return positionEvolverVariable.getMinimumValue();

        }

        return 0.0;

    }

    public double getMaximumValue(String variableName) {

        // Get the PositionEvolverVariable
        PositionEvolverVariable positionEvolverVariable = getPositionEvolverVariable(variableName);

        if (positionEvolverVariable != null) {

            // Get the boundary value
            return positionEvolverVariable.getMaximumValue();

        }

        return 0.0;

    }
    public BoundaryEffect getBoundaryEffect(String variableName) {

        // Get the PositionEvolverVariable
        PositionEvolverVariable positionEvolverVariable = getPositionEvolverVariable(variableName);

        if (positionEvolverVariable != null) {

            // Get the boundary value
            return positionEvolverVariable.getBoundaryEffect();

        }

        return new BoundaryEffect();

    }

    public void setBoundaryValues(
            String variableName
            , double minimumValue
            , double maximumValue) {

        // Get the PositionEvolverVariable
        PositionEvolverVariable positionEvolverVariable = getPositionEvolverVariable(variableName);

        if (positionEvolverVariable != null) {

            // Set the boundary value
            positionEvolverVariable.setBoundaryValues(minimumValue, maximumValue);

        }

    }

    public void generateNewRandomPosition(HashMap<String, Boolean> generateNewRandomValues) {

        // Loop through the variables
        for (PositionEvolverVariable positionEvolverVariable : X) {

            // Check if we should generate a new random variable
            if (generateNewRandomValues.containsKey(positionEvolverVariable.getName())) {

                // Generate the new random value
                positionEvolverVariable.randomizeValue();

            }

        }

        // Generate new random values for dX
        if (positionEvolverDXdt != null) {
            positionEvolverDXdt.generateNewRandomPosition(generateNewRandomValues);
        }

    }

    public ArrayList<String> checkRandomChanges(
            double dt
            , HashMap<String, Boolean> mapEvolveVariables) {

        // Initialize the list of variables that should randomly change
        ArrayList<String> arrayListGenerateNewRandomValues = new ArrayList<>();

        // -------------------- BEGIN Check for random changes -------------------- //

        // Increment the total time since last global random change
        totalTimeElapsedSinceLastRandomChangePositionSeconds += dt;

        // Increment the total time since last random change for each variable
        for (PositionEvolverVariable positionEvolverVariable : X) {
            positionEvolverVariable.incrementTotalTimeElapsedSinceLastRandomChangeSeconds(dt);
        }

        // Check if we can make global random changes
        if (randomChangeEffect.canRandomlyChange) {

            // Check if we should make global random changes at regular intervals
            if (randomChangeEffect.randomChangeInterval == RandomChangeEffect.RANDOM_CHANGE_INTERVAL.REGULAR) {

                // Check if we have exceeded the global random change timer
                if (totalTimeElapsedSinceLastRandomChangePositionSeconds >= randomChangeEffect.randomChangeValue) {

                    // Loop through the variables
                    for (PositionEvolverVariable positionEvolverVariable : X) {

                        // Get the variable name
                        String variableName = positionEvolverVariable.getName();

                        // Set the generate new random value flag
                        boolean generateNewRandomValue = true;
                        if (mapEvolveVariables.containsKey(variableName)) {
                            generateNewRandomValue = mapEvolveVariables.get(variableName);
                        }

                        // Check if we should generate a new random value
                        if (generateNewRandomValue) {

                            // Add the variable name to the list
                            arrayListGenerateNewRandomValues.add(variableName);

                        }

                    }

                    // Reset the global random change timer
                    totalTimeElapsedSinceLastRandomChangePositionSeconds = 0.0;

                }

            }

            // Check if we should make global random changes at random intervals
            else if (randomChangeEffect.randomChangeInterval == RandomChangeEffect.RANDOM_CHANGE_INTERVAL.RANDOM) {

                // Q = 1 - (1 - p)^(1 / N)
                // Q = probability of change in partial unit
                // P = probability of change in one unit
                // N = number of partial units in one unit
                // N = 1 second / time elapsed since last update in seconds
                double probabilityThreshold = 1.0 - Math.pow(1.0 - randomChangeEffect.randomChangeValue, dt);
                double positionChangeCheck = Math.random();
                if (positionChangeCheck < probabilityThreshold) {

                    // Loop through the variables
                    for (PositionEvolverVariable positionEvolverVariable : X) {

                        // Get the variable name
                        String variableName = positionEvolverVariable.getName();

                        // Set the generate new random value flag
                        boolean generateNewRandomValue = true;
                        if (mapEvolveVariables.containsKey(variableName)) {
                            generateNewRandomValue = mapEvolveVariables.get(variableName);
                        }

                        // Check if we should generate a new random value
                        if (generateNewRandomValue) {

                            // Add the generate new random value flag to the map
                            arrayListGenerateNewRandomValues.add(variableName);

                        }

                    }

                }

            }

        }

        // We can't make global random changes
        // Check the individual variables
        else {

            // Loop through the variables
            for (PositionEvolverVariable positionEvolverVariable : X) {

                // Get the variable name
                String variableName = positionEvolverVariable.getName();

                // Get the random change effect
                RandomChangeEffect randomChangeEffect = positionEvolverVariable.getRandomChangeEffect();

                // Check if the current variable can randomly change
                if (randomChangeEffect.canRandomlyChange) {

                    // Check if we should randomly change at regular intervals
                    if (randomChangeEffect.randomChangeInterval == RandomChangeEffect.RANDOM_CHANGE_INTERVAL.REGULAR) {

                        // Check if we have exceeded the random change timer
                        if (positionEvolverVariable.getTotalTimeElapsedSinceLastRandomChangeSeconds() >= randomChangeEffect.randomChangeValue) {

                            // Set the generate new random value flag
                            boolean generateNewRandomValue = true;
                            if (mapEvolveVariables.containsKey(variableName)) {
                                generateNewRandomValue = mapEvolveVariables.get(variableName);
                            }

                            // Check if we should generate a new random value
                            if (generateNewRandomValue) {

                                // Add the generate new random value flag to the map
                                arrayListGenerateNewRandomValues.add(variableName);

                            }

                            // Reset the random change timer
                            positionEvolverVariable.setTotalTimeElapsedSinceLastRandomChangeSeconds(0.0);

                        }

                    }

                    // Check if we should randomly change at random intervals
                    else if (randomChangeEffect.randomChangeInterval == RandomChangeEffect.RANDOM_CHANGE_INTERVAL.RANDOM) {

                        double probabilityThreshold = 1.0 - Math.pow(1.0 - randomChangeEffect.randomChangeValue, dt);
                        double changeCheck = Math.random();
                        if (changeCheck < probabilityThreshold) {

                            // Set the generate new random value flag
                            boolean generateNewRandomValue = true;
                            if (mapEvolveVariables.containsKey(variableName)) {
                                generateNewRandomValue = mapEvolveVariables.get(variableName);
                            }

                            // Check if we should generate a new random value
                            if (generateNewRandomValue) {

                                // Add the generate new random value flag to the map
                                arrayListGenerateNewRandomValues.add(variableName);

                            }

                        }

                    }

                }

            }

        }

        // -------------------- END Check for random changes -------------------- //

        // Add the random change list for dX variables
        if (positionEvolverDXdt != null) {
            arrayListGenerateNewRandomValues.addAll(positionEvolverDXdt.checkRandomChanges(dt, mapEvolveVariables));
        }

        return arrayListGenerateNewRandomValues;

    }

    public void evolveTime(
            double dt
            , HashMap<String, Boolean> mapEvolveVariables
            , HashMap<String, Boolean> mapGenerateNewRandomValues) {

        if (dt > 1.0) {
            int blah = 0;
            blah++;

        }

        // Generate new random values as needed
        generateNewRandomPosition(mapGenerateNewRandomValues);

        // Get the current dX
        ArrayList<Double> currentDXdt = null;
        if (positionEvolverDXdt != null) {
            currentDXdt = positionEvolverDXdt.getX(mode).getX();
        }

        // Check if we have a dX
        if (positionEvolverDXdt != null
                && currentDXdt != null) {

            // Initialize the bounce
            ArrayList<Boolean> bounceVariables = new ArrayList<>();

            // Loop through the variables
            for (int currentVariableIndex = 0; currentVariableIndex < X.size(); currentVariableIndex++) {

                // Get the current variable
                PositionEvolverVariable positionEvolverVariable = X.get(currentVariableIndex);

                // Get the current variable name
                String variableName = positionEvolverVariable.getName();

                // Get the dx for the current variable
                double dxdt = currentDXdt.get(currentVariableIndex);

                // Get the flag on whether we should evolve this variable
                boolean evolveVariable = true;
                if (mapEvolveVariables.containsKey(variableName)) {
                    evolveVariable = mapEvolveVariables.get(variableName);
                }

                // Get the flag on whether we generated a new value
                boolean generatedNewRandomValue = false;
                if (mapGenerateNewRandomValues.containsKey(variableName)) {
                    generatedNewRandomValue = mapGenerateNewRandomValues.get(variableName);
                }

                // Check if we should evolve the variable
                if (positionEvolverVariable.getCanChange()
                        && evolveVariable
                        && !generatedNewRandomValue) {

                    // Get the change in value
                    double dx = dxdt * dt;

                    // Change the variable value
                    positionEvolverVariable.moveValue(dx);

                }

                // -------------------- BEGIN Check boundaries -------------------- //

                // Get if the variable is increasing or decreasing
                int variableDirection = 0;
                if (dxdt > 0.0) {
                    variableDirection = 1;
                } else if (dxdt < 0.0) {
                    variableDirection = -1;
                }

                // Get the boundary reached
                int boundaryReached = positionEvolverVariable.checkBoundaryReached(variableDirection);

                // Check if we actually reached a boundary
                if (boundaryReached != 0) {

                    // Get the boundary handler values
                    PositionEvolverVariable.BoundaryHandlerValues boundaryHandlerValues =
                            positionEvolverVariable.processBoundary(boundaryReached);
                    double newValue = boundaryHandlerValues.newValue;

                    // Add the bounce value to the bounce variables list
                    bounceVariables.add(boundaryHandlerValues.bounceValue);

                    // Set the new value
                    positionEvolverVariable.setValue(newValue);

                } else {

                    // Add no bounce to the bounce variable list
                    bounceVariables.add(false);

                }


                // -------------------- END Check boundaries -------------------- //

            }

            // Evolve dX
            positionEvolverDXdt.evolveTime(
                    dt
                    , mapEvolveVariables
                    , mapGenerateNewRandomValues
            );

            // Process bounce if necessary
            positionEvolverDXdt.bounce(mode, bounceVariables);

        }

    }



    public void bounce(
            MODE mode
            , ArrayList<Boolean> bounceVariables) {

        // Check if the modes are the same
        if (this.mode == mode) {

            // Loop through the variables
            for (int currentVariableIndex = 0; currentVariableIndex < X.size(); currentVariableIndex++) {

                // Get the current variable
                PositionEvolverVariable currentVariable = X.get(currentVariableIndex);

                // Get the bounce value
                boolean bounce = false;
                if (bounceVariables.size() > currentVariableIndex) {
                    bounce = bounceVariables.get(currentVariableIndex);
                }

                // Check if we should bounce this variable
                if (bounce) {

                    // Bounce the current variable
                    currentVariable.setValue(currentVariable.getValue() * -1.0);

                }

            }

        }

        // Check if we are bouncing from cartesian to polar
        else if (this.mode == MODE.POLAR_2D && mode == MODE.CARTESIAN_2D) {

            // Get the bounce flags
            boolean bounceX = bounceVariables.get(0);
            boolean bounceY = bounceVariables.get(1);

            // Get the direction variable
            PositionEvolverVariable variableDirection = X.get(1);

            // Check if we are bouncing horizontally
            if (bounceX) {

                // Bounce horizontally
                variableDirection.setValue(
                        Math.PI - variableDirection.getValue()
                );

            }

            // Check if we are bounding vertically
            if (bounceY) {

                // Bounce vertically
                variableDirection.setValue(
                        variableDirection.getValue() * -1.0
                );

            }

        }

    }

    public enum MODE {
        ONE_DIMENSION
        , CARTESIAN_2D
        , CARTESIAN_3D
        , POLAR_2D
        , SPHERICAL_3D
    }

    public static class RandomChangeEffect {

        public final boolean canRandomlyChange;
        public final double randomChangeValue;
        public final RANDOM_CHANGE_INTERVAL randomChangeInterval;
        public final boolean bounceOnRandomChange;

        public RandomChangeEffect() {
            canRandomlyChange = false;
            randomChangeValue = 0.0;
            randomChangeInterval = RANDOM_CHANGE_INTERVAL.CONSTANT;
            bounceOnRandomChange = false;
        }

        public RandomChangeEffect(
                boolean canRandomlyChange
                , double randomChangeValue
                , RANDOM_CHANGE_INTERVAL randomChangeInterval) {
            this.canRandomlyChange = canRandomlyChange;
            this.randomChangeValue = randomChangeValue;
            this.randomChangeInterval = randomChangeInterval;
            this.bounceOnRandomChange = false;
        }

        public RandomChangeEffect(
                boolean canRandomlyChange
                , double randomChangeValue
                , RANDOM_CHANGE_INTERVAL randomChangeInterval
                , boolean bounceOnRandomChange) {
            this.canRandomlyChange = canRandomlyChange;
            this.randomChangeValue = randomChangeValue;
            this.randomChangeInterval = randomChangeInterval;
            this.bounceOnRandomChange = bounceOnRandomChange;
        }

        public enum RANDOM_CHANGE_INTERVAL {
            CONSTANT
            , REGULAR
            , RANDOM
        }

    }

    public static class BoundaryEffect {

        public final BOUNDARY_EFFECT boundaryEffect;
        public final boolean mirrorAbsoluteValueBoundaries;
        public final boolean bounceOnInternalBoundary;

        public BoundaryEffect() {
            this.boundaryEffect = BOUNDARY_EFFECT.STICK;
            this.mirrorAbsoluteValueBoundaries = false;
            this.bounceOnInternalBoundary = false;
        }

        public BoundaryEffect(BOUNDARY_EFFECT boundaryEffect) {
            this.boundaryEffect = boundaryEffect;
            this.mirrorAbsoluteValueBoundaries = false;
            this.bounceOnInternalBoundary = false;
        }

        public BoundaryEffect(
                BOUNDARY_EFFECT boundaryEffect
                , boolean mirrorAbsoluteValueBoundaries) {
            this.boundaryEffect = boundaryEffect;
            this.mirrorAbsoluteValueBoundaries = mirrorAbsoluteValueBoundaries;
            this.bounceOnInternalBoundary = false;
        }

        public BoundaryEffect(
                BOUNDARY_EFFECT boundaryEffect
                , boolean mirrorAbsoluteValueBoundaries
                , boolean bounceOnInternalBoundary) {
            this.boundaryEffect = boundaryEffect;
            this.mirrorAbsoluteValueBoundaries = mirrorAbsoluteValueBoundaries;
            this.bounceOnInternalBoundary = bounceOnInternalBoundary;
        }

        public enum BOUNDARY_EFFECT {
            STICK
            , BOUNCE
            , PERIODIC
            , PERIODIC_REFLECTIVE
        }

    }

}
