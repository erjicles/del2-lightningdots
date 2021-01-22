package com.delsquared.lightningdots.utilities;

public interface IPositionEvolvingPolygonalObject extends IPositionEvolvingObject {
    Polygon getPolygon();
    PositionEvolver getPositionEvolverX();
    PositionEvolver getPositionEvolverRadius();
    PositionEvolver getPositionEvolverRotation();
}
