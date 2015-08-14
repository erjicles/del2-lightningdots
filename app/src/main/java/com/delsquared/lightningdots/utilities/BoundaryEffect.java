package com.delsquared.lightningdots.utilities;

public class BoundaryEffect {

    public final BoundaryType boundaryType;
    public final boolean mirrorAbsoluteValueBoundaries;
    public final boolean bounceOnInternalBoundary;

    public BoundaryEffect() {
        this.boundaryType = BoundaryType.HARD;
        this.mirrorAbsoluteValueBoundaries = false;
        this.bounceOnInternalBoundary = false;
    }

    public BoundaryEffect(BoundaryType boundaryType) {
        this.boundaryType = boundaryType;
        this.mirrorAbsoluteValueBoundaries = false;
        this.bounceOnInternalBoundary = false;
    }

    public BoundaryEffect(
            BoundaryType boundaryType
            , boolean mirrorAbsoluteValueBoundaries) {
        this.boundaryType = boundaryType;
        this.mirrorAbsoluteValueBoundaries = mirrorAbsoluteValueBoundaries;
        this.bounceOnInternalBoundary = false;
    }

    public BoundaryEffect(
            BoundaryType boundaryType
            , boolean mirrorAbsoluteValueBoundaries
            , boolean bounceOnInternalBoundary) {
        this.boundaryType = boundaryType;
        this.mirrorAbsoluteValueBoundaries = mirrorAbsoluteValueBoundaries;
        this.bounceOnInternalBoundary = bounceOnInternalBoundary;
    }

}
