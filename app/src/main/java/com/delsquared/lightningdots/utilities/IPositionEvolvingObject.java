package com.delsquared.lightningdots.utilities;

import java.util.List;

public interface IPositionEvolvingObject extends INamedObject {

    String getCurrentProfileName();
    void setCurrentProfile(String profileName);
    List<String> getListProfileNames();
    boolean checkProfileTransition(double dt);
    PositionEvolverFamily getPositionEvolverFamily(String positionEvolverFamilyName);
    OrderedObjectCollection<PositionEvolverFamily> getCollectionPositionEvolverFamilies();
    double getVariableValue(String variableName);
    @SuppressWarnings("unused")
    double getOldVariableValue(String variableName);
    void setVariableValue(String variableName, double value);
    void setVariableValue(String variableName, double variableValue, boolean treatAsInitialValue);
    void randomizeVariableValue(String variableName);
    double getMass();

}
