package com.delsquared.lightningdots.utilities;

public class PositionEvolvingObjectContainerHelper<T extends IPositionEvolvingObject> {

    private PositionEvolvingObjectContainer<T> positionEvolvingObjectContainer;

    public PositionEvolvingObjectContainerHelper(PositionEvolvingObjectContainer<T> positionEvolvingObjectContainer) {
        this.positionEvolvingObjectContainer = positionEvolvingObjectContainer;
    }

    public T getObject(String objectName) {
        return positionEvolvingObjectContainer.getCollectionObjects().getObject(objectName);
    }

    public T getObject(String objectName, String objectProfileName) {
        T object = positionEvolvingObjectContainer.getCollectionObjects().getObject(objectName);
        if (object != null) {
            if (object.getCurrentProfileName().contentEquals(objectProfileName)) {
                return object;
            }
        }
        return null;
    }

    public PositionEvolverFamily getPositionEvolverFamily(String objectName, String positionEvolverFamilyName) {
        T object = getObject(objectName);
        if (object != null) {
            return object.getCollectionPositionEvolverFamilies().getObject(positionEvolverFamilyName);
        }
        return null;
    }

    public PositionEvolverFamily getPositionEvolverFamily(String objectName, String objectProfileName, String positionEvolverFamilyName) {
        T object = getObject(objectName, objectProfileName);
        if (object != null) {
            return object.getCollectionPositionEvolverFamilies().getObject(positionEvolverFamilyName);
        }
        return null;
    }

    public PositionEvolver getPositionEvolver(String objectName, String positionEvolverFamilyName, String positionEvolverName) {
        PositionEvolverFamily positionEvolverFamily = getPositionEvolverFamily(objectName, positionEvolverFamilyName);
        if (positionEvolverFamily != null) {
            return positionEvolverFamily.getCollectionPositionEvolvers().getObject(positionEvolverName);
        }
        return null;
    }

    public PositionEvolver getPositionEvolver(String objectName, String objectProfileName, String positionEvolverFamilyName, String positionEvolverName) {
        PositionEvolverFamily positionEvolverFamily = getPositionEvolverFamily(objectName, objectProfileName, positionEvolverFamilyName);
        if (positionEvolverFamily != null) {
            return positionEvolverFamily.getCollectionPositionEvolvers().getObject(positionEvolverName);
        }
        return null;
    }

    public PositionEvolverVariable getPositionEvolverVariable(String objectName, String positionEvolverFamilyName, String positionEvolverName, String variableName) {
        PositionEvolver positionEvolver = getPositionEvolver(objectName, positionEvolverFamilyName, positionEvolverName);
        if (positionEvolver != null) {
            return positionEvolver.getCollectionPositionEvolverVariables().getObject(variableName);
        }
        return null;
    }

    public PositionEvolverVariable getPositionEvolverVariable(String objectName, String objectProfileName, String positionEvolverFamilyName, String positionEvolverName, String variableName) {
        PositionEvolver positionEvolver = getPositionEvolver(objectName, objectProfileName, positionEvolverFamilyName, positionEvolverName);
        if (positionEvolver != null) {
            return positionEvolver.getCollectionPositionEvolverVariables().getObject(variableName);
        }
        return null;
    }

}
