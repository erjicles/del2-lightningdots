package com.delsquared.lightningdots.utilities;

public class PositionVector {
    public final double X1;
    public final double X2;
    public final double X3;
    public PositionVector() {
        X1 = 0.0;
        X2 = 0.0;
        X3 = 0.0;
    }
    public PositionVector(double X1, double X2, double X3) {
        this.X1 = X1;
        this.X2 = X2;
        this.X3 = X3;
    }

    public boolean equals(PositionVector otherPositionVector) {
        return X1 == otherPositionVector.X1
                && X2 == otherPositionVector.X2
                && X3 == otherPositionVector.X3;
    }
}