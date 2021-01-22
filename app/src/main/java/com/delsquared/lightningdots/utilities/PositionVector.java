package com.delsquared.lightningdots.utilities;

import java.util.ArrayList;
import java.util.List;

public class PositionVector {
    public final int cardinality;
    private final List<Double> X;
    public final CoordinateSystemType coordinateSystemType;
    public PositionVector() {
        cardinality = 0;
        X = new ArrayList<>();
        coordinateSystemType = CoordinateSystemType.CARTESIAN;
    }
    @SuppressWarnings("unused")
    public PositionVector(final double X1) {
        cardinality = 1;
        X = new ArrayList<Double>() {{
            add(X1);
        }};
        coordinateSystemType = CoordinateSystemType.CARTESIAN;
    }
    @SuppressWarnings("unused")
    public PositionVector(final double X1, CoordinateSystemType coordinateSystemType) {
        cardinality = 1;
        X = new ArrayList<Double>() {{
            add(X1);
        }};
        this.coordinateSystemType = coordinateSystemType;
    }
    public PositionVector(final double X1, final double X2) {
        cardinality = 2;
        X = new ArrayList<Double>() {{
            add(X1);
            add(X2);
        }};
        coordinateSystemType = CoordinateSystemType.CARTESIAN;
    }
    @SuppressWarnings("unused")
    public PositionVector(final double X1, final double X2, CoordinateSystemType coordinateSystemType) {
        cardinality = 2;
        X = new ArrayList<Double>() {{
            add(X1);
            add(X2);
        }};
        this.coordinateSystemType = coordinateSystemType;
    }
    public PositionVector(final double X1, final double X2, final double X3) {
        cardinality = 3;
        X = new ArrayList<Double>() {{
            add(X1);
            add(X2);
            add(X3);
        }};
        coordinateSystemType = CoordinateSystemType.CARTESIAN;
    }
    @SuppressWarnings("unused")
    public PositionVector(final double X1, final double X2, final double X3, CoordinateSystemType coordinateSystemType) {
        cardinality = 3;
        X = new ArrayList<Double>() {{
            add(X1);
            add(X2);
            add(X3);
        }};
        this.coordinateSystemType = coordinateSystemType;
    }
    public PositionVector(List<Double> X) {
        if (X == null) {
            cardinality = 0;
            this.X = new ArrayList<>();
        } else {
            cardinality = X.size();
            this.X = X;
        }
        coordinateSystemType = CoordinateSystemType.CARTESIAN;
    }
    public PositionVector(List<Double> X, CoordinateSystemType coordinateSystemType) {
        cardinality = X.size();
        this.X = X;
        this.coordinateSystemType = coordinateSystemType;
    }
    public PositionVector(final PositionVector positionVector) {
        if (positionVector == null) {
            cardinality = 0;
            X = new ArrayList<>();
            coordinateSystemType = CoordinateSystemType.CARTESIAN;
        } else {
            cardinality = positionVector.cardinality;
            X = new ArrayList<>(positionVector.cardinality);
            X.addAll(positionVector.getX());
            coordinateSystemType = positionVector.coordinateSystemType;
        }
    }
    @SuppressWarnings("unused")
    public PositionVector(int cardinality) {
        this.cardinality = cardinality;
        X = new ArrayList<>();
        for (int i = 0; i < cardinality; i++) {
            X.add(0.0);
        }
        coordinateSystemType = CoordinateSystemType.CARTESIAN;
    }
    @SuppressWarnings("unused")
    public PositionVector(int cardinality, CoordinateSystemType coordinateSystemType) {
        this.cardinality = cardinality;
        X = new ArrayList<>();
        for (int i = 0; i < cardinality; i++) {
            X.add(0.0);
        }
        this.coordinateSystemType = coordinateSystemType;
    }

    public List<Double> getX() {
        return X;
    }

    public double getValue(int index) {
        if (index >= 0 && index < X.size()) {
            return X.get(index);
        }
        return 0.0;
    }

    public boolean equals(PositionVector otherPositionVector) {
        if (otherPositionVector == null) {
            return false;
        }
        return this.cardinality == otherPositionVector.cardinality
                && this.X.equals(otherPositionVector.X);
    }

    public PositionVector add(PositionVector otherVector) {
        if (otherVector == null) {
            return new PositionVector(this);
        }
        List<Double> valueList = new ArrayList<>();
        for (int currentIndex = 0; currentIndex < Math.min(cardinality, otherVector.cardinality); currentIndex++) {
            valueList.add(getValue(currentIndex) + otherVector.getValue(currentIndex));
        }
        return new PositionVector(valueList);
    }

    @SuppressWarnings("unused")
    public PositionVector subtract(PositionVector otherVector) {
        if (otherVector == null) {
            return new PositionVector(this);
        }
        List<Double> valueList = new ArrayList<>();
        for (int currentIndex = 0; currentIndex < Math.min(cardinality, otherVector.cardinality); currentIndex++) {
            valueList.add(getValue(currentIndex) - otherVector.getValue(currentIndex));
        }
        return new PositionVector(valueList);
    }

    public PositionVector multiply(double scalar) {
        List<Double> valueList = new ArrayList<>();
        for (int currentIndex = 0; currentIndex < cardinality; currentIndex++) {
            valueList.add(getValue(currentIndex) * scalar);
        }
        return new PositionVector(valueList);
    }

    public PositionVector divide(double scalar) {
        if (scalar == 0.0) {
            return new PositionVector(this);
        }
        List<Double> valueList = new ArrayList<>();
        for (int currentIndex = 0; currentIndex < cardinality; currentIndex++) {
            valueList.add(getValue(currentIndex) / scalar);
        }
        return new PositionVector(valueList);
    }

    public double dotProduct(PositionVector otherVector) {
        if (otherVector == null) {
            return 0.0;
        }
        double resultValue = 0.0;
        List<Double> valueList = new ArrayList<>();
        for (int currentIndex = 0; currentIndex < Math.min(cardinality, otherVector.cardinality); currentIndex++) {
            valueList.add(getValue(currentIndex) * otherVector.getValue(currentIndex));
        }
        for (double value : valueList) {
            resultValue += value;
        }
        return resultValue;
    }

    public double getMagnitude() {
        return Math.sqrt(dotProduct(this));
    }

    public PositionVector getUnitVector() {
        return divide(getMagnitude());
    }
}