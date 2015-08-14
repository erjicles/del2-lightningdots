package com.delsquared.lightningdots.utilities;

public class PositionEvolverVariableAttractorVariable implements INamedObject {
    public final String name;
    public final double initialFixedValue;
    public final boolean isPercent;
    private double value;

    public PositionEvolverVariableAttractorVariable(
            String name
            , double initialFixedValue
            , boolean isPercent) {
        this.name = name;
        this.initialFixedValue = initialFixedValue;
        this.isPercent = isPercent;
        this.value = initialFixedValue;
    }

    public String getName() { return this.name; }
    public double getValue() { return this.value; }
    public void setValue(double value) { this.value = value; }
    public void resetValue(double minimumValue, double maximumValue) {
        if (isPercent) {
            value = minimumValue + (initialFixedValue * (maximumValue - minimumValue));
        } else {
            value = initialFixedValue;
        }
    }

}
