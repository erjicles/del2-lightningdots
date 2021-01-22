package com.delsquared.lightningdots.utilities;

public class BoundaryEffect {

    public final BoundaryType boundaryType;
    public final boolean mirrorAbsoluteValueBoundaries;

    public BoundaryEffect() {
        this.boundaryType = BoundaryType.HARD;
        this.mirrorAbsoluteValueBoundaries = false;
    }

    public BoundaryEffect(
            BoundaryType boundaryType
            , boolean mirrorAbsoluteValueBoundaries) {
        this.boundaryType = boundaryType;
        this.mirrorAbsoluteValueBoundaries = mirrorAbsoluteValueBoundaries;
    }

}
