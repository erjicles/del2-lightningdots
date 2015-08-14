package com.delsquared.lightningdots.utilities;

public class PositionEvolvingPolygonalObjectContainerHelper<T extends IPositionEvolvingPolygonalObject> {

    public PositionEvolvingPolygonalObjectContainerHelper() {
    }

    public void evolveTime(double dt, PositionEvolvingObjectContainer<T> positionEvolvingPolygonalObjectContainer) {

        // Don't do anything if dt = 0
        if (dt == 0.0) {
            return;
        }

        // Create the helper
        PositionEvolvingObjectContainerEvolverHelper<T> positionEvolvingObjectContainerEvolverHelper =
                new PositionEvolvingObjectContainerEvolverHelper<>();

        // Evolve the time
        positionEvolvingObjectContainerEvolverHelper.evolveTime(dt, positionEvolvingPolygonalObjectContainer);

        // Update the polygons
        for (T currentObject : positionEvolvingPolygonalObjectContainer.getCollectionObjects()) {

            // Get the polygon
            Polygon polygonTargetShape = currentObject.getPolygon();

            // Update the polygon
            if (polygonTargetShape != null) {

                // Get the position evolvers
                PositionEvolver positionEvolverX = currentObject.getPositionEvolverX();
                PositionEvolver positionEvolverRadius = currentObject.getPositionEvolverRadius();
                PositionEvolver positionEvolverRotation = currentObject.getPositionEvolverRotation();

                if (positionEvolverX != null
                        && positionEvolverRadius != null
                        && positionEvolverRotation != null) {

                    polygonTargetShape.setProperties(
                            new PositionVector(positionEvolverX.getX())
                            , positionEvolverRadius.getX().getValue(0)
                            , positionEvolverRotation.getX().getValue(0)
                    );

                }

            }

        }

    }

}
