package com.delsquared.lightningdots.utilities;

public interface IPositionEvolvingPolygonalObject extends IPositionEvolvingObject {
    public Polygon getPolygon();
    public PositionEvolver getPositionEvolverX();
    public PositionEvolver getPositionEvolverRadius();
    public PositionEvolver getPositionEvolverRotation();
}
