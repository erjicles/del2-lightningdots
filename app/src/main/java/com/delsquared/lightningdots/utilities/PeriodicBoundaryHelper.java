package com.delsquared.lightningdots.utilities;

import java.util.ArrayList;
import java.util.List;

public class PeriodicBoundaryHelper {

    @SuppressWarnings("unused")
    public static double getEquivalentRangeValue(
            double value
            , double minimumValue
            , double maximumValue
            , BoundaryEffect boundaryEffect) {
        return value;
    }

    @SuppressWarnings("unused")
    public List<Double> getListPeriodicValues(
            double value
            , double minimumValue
            , double maximumValue
            , BoundaryEffect boundaryEffect) {

        // Initialize the result
        List<Double> listPeriodicValues = new ArrayList<>();

        // Add the equivalent value in the range
        double equivalentRangeValue = getEquivalentRangeValue(value, minimumValue, maximumValue, boundaryEffect);
        listPeriodicValues.add(equivalentRangeValue);

        // Check if the boundary type is periodic
        if (boundaryEffect.boundaryType == BoundaryType.PERIODIC) {
            // Add the points for periodic boundaries
            double range = maximumValue - minimumValue;
            listPeriodicValues.add(range + value);
            listPeriodicValues.add(value - range);
        }

        // Check if the boundary type is periodic reflective
        else if (boundaryEffect.boundaryType == BoundaryType.PERIODIC_REFLECTIVE) {
            // Add the points for periodic reflective boundaries
            listPeriodicValues.add((2.0 * minimumValue) - value);
            listPeriodicValues.add((2.0 * maximumValue) - value);
        }

        return listPeriodicValues;

    }

}
