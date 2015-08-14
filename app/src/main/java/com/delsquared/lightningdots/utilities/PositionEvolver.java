package com.delsquared.lightningdots.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PositionEvolver implements INamedObject {

    private String name;
    private OrderedObjectCollection<PositionEvolverVariable> collectionPositionEvolverVariables;
    private MODE mode = MODE.CARTESIAN;
    private TimedChangeHandler timedChangeHandler;

    public PositionEvolver(
            String name
            , List<PositionEvolverVariable> listPositionEvolverVariables
            , MODE mode
            , TimedChangeHandler timedChangeHandler) {
        this.name = name;
        this.collectionPositionEvolverVariables = new OrderedObjectCollection<PositionEvolverVariable>(listPositionEvolverVariables);
        this.mode = mode;
        this.timedChangeHandler = timedChangeHandler;
    }

    public String getName() { return this.name; }
    public OrderedObjectCollection<PositionEvolverVariable> getCollectionPositionEvolverVariables() { return this.collectionPositionEvolverVariables; }
    public MODE getMode() { return this. mode; }
    public TimedChangeHandler getTimedChangeHandler() { return this.timedChangeHandler; }
    public int getCardinality() { return collectionPositionEvolverVariables.size(); }

    public double getVariableValue(String variableName) {
        PositionEvolverVariable positionEvolverVariable = collectionPositionEvolverVariables.getObject(variableName);
        if (positionEvolverVariable != null) {
            return positionEvolverVariable.getValue();
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
        for (PositionEvolverVariable currentVariable : collectionPositionEvolverVariables) {
            resultX.add(currentVariable.getValue());
        }
        return new PositionVector(resultX);
    }

    public PositionVector getX(MODE convertToMode) {
        int cardinality = getCardinality();
        if (this.mode == convertToMode) {
            return getX();
        }
        else if (this.mode == MODE.SPHERICAL && cardinality == 2
                && convertToMode == MODE.CARTESIAN) {
            double X1 = collectionPositionEvolverVariables.getObject(0).getValue();
            double X2 = collectionPositionEvolverVariables.getObject(1).getValue();
            return new PositionVector(
                    X1 * Math.cos(X2)
                    , X1 * Math.sin(X2)
            );
        }
        else if (this.mode == MODE.SPHERICAL && cardinality == 3
                && convertToMode == MODE.CARTESIAN) {
            double X1 = collectionPositionEvolverVariables.getObject(0).getValue();
            double X2 = collectionPositionEvolverVariables.getObject(1).getValue();
            double X3 = collectionPositionEvolverVariables.getObject(2).getValue();
            double sinX3 = Math.sin(X3);
            return new PositionVector(
                    X1 * Math.cos(X2) * sinX3
                    , X1 * Math.sin(X2) * sinX3
                    , X1 * Math.cos(X3)
            );
        }
        return getX();
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
            MODE mode
            , List<Boolean> bounceVariables) {

        int cardinality = getCardinality();

        // Check if the modes are the same
        if (this.mode == mode) {

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
        else if (this.mode == MODE.SPHERICAL && cardinality == 2 && mode == MODE.CARTESIAN) {

            // Get the bounce flags
            boolean bounceX = bounceVariables.get(0);
            boolean bounceY = bounceVariables.get(1);

            // Get the direction variable
            PositionEvolverVariable variableDirection = collectionPositionEvolverVariables.getObject(1);

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
        CARTESIAN
        , SPHERICAL
    }

}
