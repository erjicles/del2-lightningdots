package com.delsquared.lightningdots.utilities;

import java.util.ArrayList;
import java.util.List;

public class PositionVectorHelper {

    @SuppressWarnings("ConstantConditions")
    public static PositionVector toCoordinateSystem(
            PositionVector sourcePositionVector
            , CoordinateSystemType targetCoordinateSystemType) {

        if (sourcePositionVector == null) {
            return new PositionVector();
        }

        // Return the source vector if there is no coordinate change
        if (sourcePositionVector.coordinateSystemType == targetCoordinateSystemType) {
            return sourcePositionVector;
        }

        // Get the cardinality
        int cardinality = sourcePositionVector.cardinality;

        // Initialize the value list
        List<Double> valueList = new ArrayList<>(cardinality);

        // Check if we are converting from spherical to cartesian
        // phi1 -> [0, 2pi)
        // phiN(!= 1) -> [0, PI)
        // e.g.:
        // x1 = r cos(phi1) sin(phi2) sin(phi3)
        // x2 = r sin(phi1) sin(phi2) sin(phi3)
        // x3 = r           cos(phi2) sin(phi3)
        // x4 = r                     cos(phi3)
        if (sourcePositionVector.coordinateSystemType == CoordinateSystemType.SPHERICAL
                && targetCoordinateSystemType == CoordinateSystemType.CARTESIAN) {

            // Get the radius
            double radius = sourcePositionVector.getValue(0);

            // Loop through the variables
            for (int currentVariableIndex = 0; currentVariableIndex < cardinality; currentVariableIndex++) {

                // Initialize the value to the radius
                double currentValue = radius;

                // Get the first angle index
                int firstAngleIndex = currentVariableIndex;
                if (currentVariableIndex == 0) {
                    firstAngleIndex = 1;
                }

                // Loop through the angles
                for (int currentAngleIndex = firstAngleIndex; currentAngleIndex < cardinality; currentAngleIndex++) {
                    if (currentVariableIndex == 0
                            && firstAngleIndex == 1
                            && currentAngleIndex == 1) {
                        currentValue *= Math.cos(sourcePositionVector.getValue(currentAngleIndex));
                    } else if (currentVariableIndex == 1
                            && currentAngleIndex == 1) {
                        currentValue *= Math.sin(sourcePositionVector.getValue(currentAngleIndex));
                    } else if (currentAngleIndex == firstAngleIndex) {
                        currentValue *= Math.cos(sourcePositionVector.getValue(currentAngleIndex));
                    } else {
                        currentValue *= Math.sin(sourcePositionVector.getValue(currentAngleIndex));
                    }
                }

                // Add the current value to the value list
                valueList.add(currentValue);

            }

        }

        // Check if we are converting from cartesian to spherical
        // e.g.:
        // x1 = r    = sqrt(x^2 + y^2 + z^2 + w^2)
        // x2 = phi1 = arccos(x / sqrt(x^2 + y^2)
        // x3 = phi2 = arccos(z / sqrt(x^2 + y^2 + z^2)
        // x4 = phi3 = arccos(w / sqrt(x^2 + y^2 + z^2 + w^2)
        // If x(k) != 0 for some k but all of x(k+1),...,x(n) = 0,
        // then phi(k) = 0 when x(k) > 0 and phi(k) = pi when x(k) < 0
        // If all of x(k), x(k+1), ... , x(n) = 0, then phi(k) = 0
        else if (sourcePositionVector.coordinateSystemType == CoordinateSystemType.CARTESIAN
                && targetCoordinateSystemType == CoordinateSystemType.SPHERICAL) {

            // Initialize the progressive squared magnitude
            double progressiveMagnitudeSquared = 0.0;

            // Trackers for special cases
            int firstSubsequentZeroValueIndex = -1;
            int lastNonZeroValueIndex = -1;
            boolean allSubsequentValuesZero = false;
            double lastNonZeroValue = 0.0;

            // Loop through the variables to set the angles
            for (int currentVariableIndex = 0; currentVariableIndex < cardinality; currentVariableIndex++) {

                // Initialize the value
                double currentValue = 0.0;

                // Get the current variable value
                double currentVariableValue = sourcePositionVector.getValue(currentVariableIndex);

                // Add the new variable to the progressive squared magnitude
                progressiveMagnitudeSquared += (currentVariableValue * currentVariableValue);

                // Handle the azimuthal angle
                if (currentVariableIndex == 1) {
                    double previousVariableValue = sourcePositionVector.getValue(0);
                    double aCosPreviousVariableValue = Math.acos(previousVariableValue / Math.sqrt(progressiveMagnitudeSquared));
                    if (currentVariableValue >= 0.0) {
                        currentValue = aCosPreviousVariableValue;
                    } else {
                        currentValue = (2.0 * Math.PI) - aCosPreviousVariableValue;
                    }
                }

                // Handle other angles
                else if (currentVariableIndex > 1) {
                    currentValue = Math.acos(currentValue / Math.sqrt(progressiveMagnitudeSquared));
                }

                // Set special case trackers
                if (currentVariableValue == 0.0) {
                    if (!allSubsequentValuesZero) {
                        firstSubsequentZeroValueIndex = currentVariableIndex;
                        allSubsequentValuesZero = true;
                    }
                } else {
                    lastNonZeroValueIndex = currentVariableIndex;
                    allSubsequentValuesZero = false;
                    lastNonZeroValue = currentVariableValue;
                }

                // Add the value to the list
                valueList.add(currentValue);

            }

            // Set the radius
            valueList.add(0, Math.sqrt(progressiveMagnitudeSquared));

            // Handle special case for angle phi(k) where all x(k+1), ..., x(n) = 0
            if (allSubsequentValuesZero && lastNonZeroValueIndex != -1) {
                if (lastNonZeroValue < 0) {
                    valueList.set(lastNonZeroValueIndex, Math.PI);
                } else {
                    valueList.set(lastNonZeroValueIndex, 0.0);
                }
            }

            // Handle special case for angle phi(k) where all of x(k), x(k+1), ..., x(n) = 0
            if (allSubsequentValuesZero && firstSubsequentZeroValueIndex != -1) {
                for (int currentVariableIndex = firstSubsequentZeroValueIndex; currentVariableIndex < cardinality; currentVariableIndex++) {
                    valueList.set(currentVariableIndex, 0.0);
                }
            }



        }

        return new PositionVector(valueList, targetCoordinateSystemType);

    }

}
