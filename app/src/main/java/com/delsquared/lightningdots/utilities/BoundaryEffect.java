package com.delsquared.lightningdots.utilities;

public class BoundaryEffect {

    public final TYPE boundaryEffect;
    public final boolean mirrorAbsoluteValueBoundaries;
    public final boolean bounceOnInternalBoundary;

    public BoundaryEffect() {
        this.boundaryEffect = TYPE.STICK;
        this.mirrorAbsoluteValueBoundaries = false;
        this.bounceOnInternalBoundary = false;
    }

    public BoundaryEffect(TYPE boundaryEffect) {
        this.boundaryEffect = boundaryEffect;
        this.mirrorAbsoluteValueBoundaries = false;
        this.bounceOnInternalBoundary = false;
    }

    public BoundaryEffect(
            TYPE boundaryEffect
            , boolean mirrorAbsoluteValueBoundaries) {
        this.boundaryEffect = boundaryEffect;
        this.mirrorAbsoluteValueBoundaries = mirrorAbsoluteValueBoundaries;
        this.bounceOnInternalBoundary = false;
    }

    public BoundaryEffect(
            TYPE boundaryEffect
            , boolean mirrorAbsoluteValueBoundaries
            , boolean bounceOnInternalBoundary) {
        this.boundaryEffect = boundaryEffect;
        this.mirrorAbsoluteValueBoundaries = mirrorAbsoluteValueBoundaries;
        this.bounceOnInternalBoundary = bounceOnInternalBoundary;
    }

    public enum TYPE {
        STICK
        , BOUNCE
        , PERIODIC
        , PERIODIC_REFLECTIVE
    }

}
