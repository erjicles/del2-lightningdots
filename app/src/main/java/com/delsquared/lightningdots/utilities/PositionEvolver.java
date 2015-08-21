package com.delsquared.lightningdots.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PositionEvolver implements INamedObject {

    private String name;
    private OrderedObjectCollection<PositionEvolverVariable> collectionPositionEvolverVariables;
    private CoordinateSystemType coordinateSystemType = CoordinateSystemType.CARTESIAN;
    private TimedChangeHandler timedChangeHandler;

    public PositionEvolver(
            String name
            , List<PositionEvolverVariable> listPositionEvolverVariables
            , CoordinateSystemType coordinateSystemType
            , TimedChangeHandler timedChangeHandler) {
        this.name = name;
        this.collectionPositionEvolverVariables = new OrderedObjectCollection<PositionEvolverVariable>(listPositionEvolverVariables);
        this.coordinateSystemType = coordinateSystemType;
        this.timedChangeHandler = timedChangeHandler;
    }

    public String getName() { return this.name; }
    public OrderedObjectCollection<PositionEvolverVariable> getCollectionPositionEvolverVariables() { return this.collectionPositionEvolverVariables; }
    public CoordinateSystemType getCoordinateSystemType() { return this.coordinateSystemType; }
    public TimedChangeHandler getTimedChangeHandler() { return this.timedChangeHandler; }
    public int getCardinality() { return collectionPositionEvolverVariables.size(); }

    public double getVariableValue(String variableName) {
        PositionEvolverVariable positionEvolverVariable = collectionPositionEvolverVariables.getObject(variableName);
        if (positionEvolverVariable != null) {
            return positionEvolverVariable.getValue();
        }
        return 0.0;
    }

    public double getOldVariableValue(String variableName) {
        PositionEvolverVariable positionEvolverVariable = collectionPositionEvolverVariables.getObject(variableName);
        if (positionEvolverVariable != null) {
            return positionEvolverVariable.getOldValue();
        }
        return 0.0;
    }

    public void setVariableValue(String variableName, double variableValue) {
        setVariableValue(variableName, variableValue, false);
    }

    public void setVariableValue(String variableName, double variableValue, boolean treatAsInitialValue) {
        // Get the PositionEvolverVariable
        PositionEvolverVariable positionEvolverVariable = collectionPositionEvolverVariables.getObject(variableName);
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
        PositionEvolverVariable positionEvolverVariable = collectionPositionEvolverVariables.getObject(variableName);
        if (positionEvolverVariable != null) {
            // Generate random value
            positionEvolverVariable.randomizeValue();
        }
    }

    public PositionVector getX() {
        List<Double> resultX = new ArrayList<>();
        for (int currentVariableIndex = 0; currentVariableIndex < collectionPositionEvolverVariables.size(); currentVariableIndex++) {
            PositionEvolverVariable currentVariable = collectionPositionEvolverVariables.getObject(currentVariableIndex);
            double currentValue = currentVariable.getValue();
            resultX.add(currentValue);
        }
        return new PositionVector(resultX, coordinateSystemType);
    }

    public PositionVector getX(CoordinateSystemType convertToCoordinateSystemType) {
        return PositionVectorHelper.toCoordinateSystem(getX(), convertToCoordinateSystemType);
    }

    public PositionVector getOldX() {
        List<Double> resultX = new ArrayList<>();
        for (int currentVariableIndex = 0; currentVariableIndex < collectionPositionEvolverVariables.size(); currentVariableIndex++) {
            PositionEvolverVariable currentVariable = collectionPositionEvolverVariables.getObject(currentVariableIndex);
            double currentValue = currentVariable.getOldValue();
            resultX.add(currentValue);
        }
        return new PositionVector(resultX, coordinateSystemType);
    }

    public PositionVector getOldX(CoordinateSystemType convertToCoordinateSystemType) {
        return PositionVectorHelper.toCoordinateSystem(getOldX(), convertToCoordinateSystemType);
    }

    public double getMinimumValue(String variableName) {
        // Get the PositionEvolverVariable
        PositionEvolverVariable positionEvolverVariable = collectionPositionEvolverVariables.getObject(variableName);
        if (positionEvolverVariable != null) {
            // Get the boundary value
            return positionEvolverVariable.getMinimumValue();
        }
        return 0.0;
    }

    public double getMaximumValue(String variableName) {
        // Get the PositionEvolverVariable
        PositionEvolverVariable positionEvolverVariable = collectionPositionEvolverVariables.getObject(variableName);
        if (positionEvolverVariable != null) {
            // Get the boundary value
            return positionEvolverVariable.getMaximumValue();
        }
        return 0.0;
    }

    public BoundaryEffect getBoundaryEffect(String variableName) {
        // Get the PositionEvolverVariable
        PositionEvolverVariable positionEvolverVariable = collectionPositionEvolverVariables.getObject(variableName);
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
        PositionEvolverVariable positionEvolverVariable = collectionPositionEvolverVariables.getObject(variableName);
        if (positionEvolverVariable != null) {
            // Set the boundary value
            positionEvolverVariable.setBoundaryValues(minimumValue, maximumValue);
        }
    }

    public boolean checkTimedChange(double dt) { return timedChangeHandler.checkTimedChange(dt); }

    public void bounce(
            CoordinateSystemType coordinateSystemType
            , List<Boolean> bounceVariables) {

        int cardinality = getCardinality();

        // Check if the modes are the same
        if (this.coordinateSystemType == coordinateSystemType) {

            // Loop through the variables
            for (int currentVariableIndex = 0; currentVariableIndex < collectionPositionEvolverVariables.size(); currentVariableIndex++) {

                // Get the current variable
                PositionEvolverVariable currentVariable = collectionPositionEvolverVariables.getObject(currentVariableIndex);

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
        else if (this.coordinateSystemType == CoordinateSystemType.SPHERICAL && cardinality == 2 && coordinateSystemType == CoordinateSystemType.CARTESIAN) {

            // Get the bounce flags
            boolean bounceX = bounceVariables.get(0);
            boolean bounceY = bounceVariables.get(1);

            // Get the direction variable
            PositionEvolverVariable variableDirection = collectionPositionEvolverVariables.getObject(1);

            // Check if we are bouncing horizontally
            if (bounceX) {

                // Bounce horizontally
                variableDirection.reinitializeValue(
                        Math.PI - variableDirection.getValue()
                );

            }

            // Check if we are bounding vertically
            if (bounceY) {

                // Bounce vertically
                variableDirection.reinitializeValue(
                        variableDirection.getValue() * -1.0
                );

            }

        }

    }

}
