package com.delsquared.lightningdots.utilities;

public class PositionEvolverFamily implements INamedObject {

    private final String name;

    private final OrderedObjectCollection<PositionEvolver> collectionPositionEvolvers;

    @SuppressWarnings("unused")
    public PositionEvolverFamily() {
        name = "";
        collectionPositionEvolvers = new OrderedObjectCollection<>();
    }

    public PositionEvolverFamily(
            String name
            , OrderedObjectCollection<PositionEvolver> collectionPositionEvolvers) {
        this.name = name;
        this.collectionPositionEvolvers = collectionPositionEvolvers;
    }

    public String getName() { return name; }
    public OrderedObjectCollection<PositionEvolver> getCollectionPositionEvolvers() { return collectionPositionEvolvers; }

    public PositionEvolver getPositionEvolver(String positionEvolverName) {
        return collectionPositionEvolvers.getObject(positionEvolverName);
    }
    public PositionEvolver getPositionEvolver(int index) {
        return collectionPositionEvolvers.getObject(index);
    }

    public int getPositionEvolverIndex(String positionEvolverName) {
        return collectionPositionEvolvers.getObjectIndex(positionEvolverName);
    }
    @SuppressWarnings("unused")
    public String getPositionEvolverName(int index) {
        return collectionPositionEvolvers.getObjectName(index);
    }

}
