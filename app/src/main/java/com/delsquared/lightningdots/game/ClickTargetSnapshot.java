package com.delsquared.lightningdots.game;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.delsquared.lightningdots.utilities.Polygon;
import com.delsquared.lightningdots.utilities.PositionEvolver;
import com.delsquared.lightningdots.utilities.PositionVector;

import java.util.ArrayList;
import java.util.List;

public class ClickTargetSnapshot {

    private final String name;
	private final PositionVector XPixels;
    private final PositionVector DXdtPixelsPerSecondPolar;
    private final PositionVector D2Xdt2PixelsPolar;
    private final PositionVector RadiusPixels;
    private final PositionVector DRadiusPixels;
    private final PositionVector D2RadiusPixels;
    private final PositionVector RotationRadians;
    private final PositionVector DRotationRadians;
    private final PositionVector D2RotationRadians;
    private final List<PositionVector> listCenterPoints;
    private final Polygon polygonTargetShape;
    private final boolean isClickable;
    private final ClickTarget.VISIBILITY visibility;

	public ClickTargetSnapshot() {
        name = "";
        XPixels = new PositionVector();
        DXdtPixelsPerSecondPolar = new PositionVector();
        D2Xdt2PixelsPolar = new PositionVector();
        RadiusPixels = new PositionVector();
        DRadiusPixels = new PositionVector();
        D2RadiusPixels = new PositionVector();
        RotationRadians = new PositionVector();
        DRotationRadians = new PositionVector();
        D2RotationRadians = new PositionVector();
        listCenterPoints = new ArrayList<>();
        polygonTargetShape = null;
        isClickable = true;
        visibility = ClickTarget.VISIBILITY.VISIBLE;
	}

	public ClickTargetSnapshot(
            String name
			, PositionVector XPixels
			, PositionVector DXdtPixelsPerSecondPolar
            , PositionVector D2Xdt2PixelsPolar
			, PositionVector RadiusPixels
            , PositionVector DRadiusPixels
            , PositionVector D2RadiusPixels
            , PositionVector RotationRadians
            , PositionVector DRotationRadians
            , PositionVector D2RotationRadians
            , List<PositionVector> listCenterPoints
            , Polygon polygonTargetShape
            , boolean isClickable
            , ClickTarget.VISIBILITY visibility
	) {
        this.name = name;
		this.XPixels = XPixels;
        this.DXdtPixelsPerSecondPolar = DXdtPixelsPerSecondPolar;
        this.D2Xdt2PixelsPolar = D2Xdt2PixelsPolar;
        this.RadiusPixels = RadiusPixels;
        this.DRadiusPixels = DRadiusPixels;
        this.D2RadiusPixels = D2RadiusPixels;
        this.RotationRadians = RotationRadians;
        this.DRotationRadians = DRotationRadians;
        this.D2RotationRadians = D2RotationRadians;
        this.listCenterPoints = listCenterPoints;
        this.polygonTargetShape = polygonTargetShape;
        this.isClickable = isClickable;
        this.visibility = visibility;
	}

    public String getName() { return name; }
	public PositionVector getXPixels() { return XPixels; }
    public PositionVector getDXdtPixelsPerSecondPolar() { return DXdtPixelsPerSecondPolar; }
    public PositionVector getD2Xdt2PixelsPolar() { return D2Xdt2PixelsPolar; }
    public PositionVector getRadiusPixels() { return RadiusPixels; }
    public PositionVector getDRadiusPixels() { return DRadiusPixels; }
    public PositionVector getD2RadiusPixels() { return D2RadiusPixels; }
    public PositionVector getRotationRadians() { return RotationRadians; }
    public PositionVector getDRotationRadians() { return DRotationRadians; }
    public PositionVector getD2RotationRadians() { return D2RotationRadians; }
    public List<PositionVector> getListCenterPoints() { return this.listCenterPoints; }
    public Polygon getPolygonTargetShape() { return polygonTargetShape; }
    public boolean getIsClickable() { return isClickable; }
    public ClickTarget.VISIBILITY getVisibility() { return visibility; }

    public boolean equals(ClickTargetSnapshot otherClickTargetSnapshot) {

        return XPixels.equals(otherClickTargetSnapshot.getXPixels())
                && DXdtPixelsPerSecondPolar.equals(otherClickTargetSnapshot.getDXdtPixelsPerSecondPolar())
                && D2Xdt2PixelsPolar.equals(otherClickTargetSnapshot.getD2Xdt2PixelsPolar())
                && RadiusPixels.equals(otherClickTargetSnapshot.getRadiusPixels())
                && DRadiusPixels.equals(otherClickTargetSnapshot.getDRadiusPixels())
                && D2RadiusPixels.equals(otherClickTargetSnapshot.getD2RadiusPixels())
                && RotationRadians.equals(otherClickTargetSnapshot.getRotationRadians())
                && DRotationRadians.equals(otherClickTargetSnapshot.getDRotationRadians())
                && D2RotationRadians.equals(otherClickTargetSnapshot.getD2RotationRadians())
                && listCenterPoints.equals(otherClickTargetSnapshot.getListCenterPoints())
                && polygonTargetShape.equals(otherClickTargetSnapshot.getPolygonTargetShape())
                && isClickable == otherClickTargetSnapshot.getIsClickable()
                && visibility == otherClickTargetSnapshot.getVisibility();

    }

    public void draw(Canvas canvas, Paint paintTarget) {

        // Loop through the center points to draw
        for (PositionVector currentPoint : listCenterPoints) {

            // Check if the target is a polygon
            if (polygonTargetShape != null) {

                // Duplicate and translate the polygon
                Polygon currentPolygon = polygonTargetShape.duplicate();
                currentPolygon.setCenterPoint(currentPoint);

                // Draw the current polygon
                currentPolygon.draw(canvas, paintTarget);

            } else { // The target is a circle

                // Draw the current point
                canvas.drawCircle(
                        (float) currentPoint.getValue(0)
                        , (float) currentPoint.getValue(1)
                        , (float) RadiusPixels.getValue(0)
                        , paintTarget);

            }

        }

    }

    public boolean pointIsInsideTarget(double pointX, double pointY) {

        boolean returnValue = false;

        // Get the list of center points
        List<PositionVector> listCenterPoints = new ArrayList<>(getListCenterPoints());

        // Loop through the center points
        for (PositionVector currentCenterPoint : listCenterPoints) {

            // Check if the target is a polygon
            if (polygonTargetShape != null) {

                // Translate the polygon to the current center point
                Polygon currentPolygon = polygonTargetShape.duplicate();
                currentPolygon.setCenterPoint(currentCenterPoint);

                // Check if the point is inside the polygon
                returnValue = currentPolygon.pointIsInsidePolygon(pointX, pointY);

            } else { // The target is a circle

                double dx = currentCenterPoint.getValue(0) - pointX;
                double dy = currentCenterPoint.getValue(1) - pointY;

                returnValue = (dx * dx) + (dy * dy) <= (RadiusPixels.getValue(0) * RadiusPixels.getValue(0));

            }

            if (returnValue) break;

        }

        return returnValue;
    }

}
