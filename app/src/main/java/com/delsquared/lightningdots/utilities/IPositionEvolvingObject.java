package com.delsquared.lightningdots.utilities;

import java.util.List;

public interface IPositionEvolvingObject extends INamedObject {

    public String getCurrentProfileName();
    public void setCurrentProfile(String profileName);
    public List<String> getListProfileNames();
    public boolean checkProfileTransition(double dt);
    public PositionEvolverFamily getPositionEvolverFamily(String positionEvolverFamilyName);
    public OrderedObjectCollection<PositionEvolverFamily> getCollectionPositionEvolverFamilies();
    public double getVariableValue(String variableName);
    public void setVariableValue(String variableName, double value);
    public void setVariableValue(String variableName, double variableValue, boolean treatAsInitialValue);
    public void randomizeVariableValue(String variableName);
    public double getMass();

}
