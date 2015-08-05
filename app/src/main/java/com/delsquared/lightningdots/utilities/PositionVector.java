package com.delsquared.lightningdots.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PositionVector {
    public final int cardinality;
    private final List<Double> X;
    public PositionVector() {
        cardinality = 0;
        X = new ArrayList<Double>();
    }
    public PositionVector(final double X1) {
        cardinality = 1;
        X = new ArrayList<Double>() {{
            add(X1);
        }};
    }
    public PositionVector(final double X1, final double X2) {
        cardinality = 2;
        X = new ArrayList<Double>() {{
            add(X1);
            add(X2);
        }};
    }
    public PositionVector(final double X1, final double X2, final double X3) {
        cardinality = 3;
        X = new ArrayList<Double>() {{
            add(X1);
            add(X2);
            add(X3);
        }};
    }

    public PositionVector(List<Double> X) {
        cardinality = X.size();
        this.X = X;
    }

    public PositionVector(final PositionVector positionVector) {
        cardinality = positionVector.cardinality;
        X = new ArrayList<>(positionVector.cardinality);
        for (double currentItem : positionVector.getX()) {
            X.add(currentItem);
        }
    }

    public List<Double> getX() {
        return X;
    }

    public double getValue(int index) {
        if (X.size() > index) {
            return X.get(index);
        }
        return 0.0;
    }

    public boolean equals(PositionVector otherPositionVector) {
        return this.cardinality == otherPositionVector.cardinality
                && this.X.equals(otherPositionVector.X);
    }
}