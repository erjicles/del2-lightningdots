package com.delsquared.lightningdots.game;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.delsquared.lightningdots.utilities.Polygon;
import com.delsquared.lightningdots.utilities.PositionEvolver;
import com.delsquared.lightningdots.utilities.PositionVector;

import java.util.ArrayList;

public class ClickTargetSnapshot {

	private final PositionVector XPixels;
    private final PositionVector DXdtPixelsPerMilliPolar;
    private final PositionVector RadiusPixels;
    private final ArrayList<PositionVector> arrayListCenterPoints;
    private final Polygon polygonTargetShape;

	public ClickTargetSnapshot() {
        XPixels = new PositionVector();
        DXdtPixelsPerMilliPolar = new PositionVector();
        RadiusPixels = new PositionVector();
        arrayListCenterPoints = new ArrayList<>();
        polygonTargetShape = null;
	}

	public ClickTargetSnapshot(
			PositionVector XPixels
			, PositionVector DXdtPixelsPerMilliPolar
			, PositionVector RadiusPixels
            , ArrayList<PositionVector> arrayListCenterPoints
            , Polygon polygonTargetShape
	) {
		this.XPixels = XPixels;
        this.DXdtPixelsPerMilliPolar = DXdtPixelsPerMilliPolar;
        this.RadiusPixels = RadiusPixels;
        this.arrayListCenterPoints = arrayListCenterPoints;
        this.polygonTargetShape = polygonTargetShape;
	}

	public PositionVector getXPixels() { return XPixels; }
    public PositionVector getDXdtPixelsPerMilliPolar() { return DXdtPixelsPerMilliPolar; }
    public PositionVector getRadiusPixels() { return RadiusPixels; }
    public ArrayList<PositionVector> getArrayListCenterPoints() { return this.arrayListCenterPoints; }
    public Polygon getPolygonTargetShape() { return polygonTargetShape; }

    public boolean equals(ClickTargetSnapshot otherClickTargetSnapshot) {

        return XPixels.equals(otherClickTargetSnapshot.getXPixels())
                && DXdtPixelsPerMilliPolar.equals(otherClickTargetSnapshot.getDXdtPixelsPerMilliPolar())
                && RadiusPixels.equals(otherClickTargetSnapshot.getRadiusPixels())
                && arrayListCenterPoints.equals(otherClickTargetSnapshot.getArrayListCenterPoints())
                && polygonTargetShape.equals(otherClickTargetSnapshot.getPolygonTargetShape());

    }

    public void draw(Canvas canvas, Paint paintTarget) {

        // Loop through the center points to draw
        for (PositionVector currentPoint : arrayListCenterPoints) {

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
                        (float) currentPoint.X1
                        , (float) currentPoint.X2
                        , (float) RadiusPixels.X1
                        , paintTarget);

            }

        }

    }

    public boolean pointIsInsideTarget(double pointX, double pointY) {

        boolean returnValue = false;

        // Get the list of center points
        ArrayList<PositionVector> arrayListCenterPoints = getArrayListCenterPoints();

        // Loop through the center points
        for (PositionVector currentCenterPoint : arrayListCenterPoints) {

            // Check if the target is a polygon
            if (polygonTargetShape != null) {

                // Translate the polygon to the current center point
                Polygon currentPolygon = polygonTargetShape.duplicate();
                currentPolygon.setCenterPoint(currentCenterPoint);

                // Check if the point is inside the polygon
                returnValue = currentPolygon.pointIsInsidePolygon(pointX, pointY);

            } else { // The target is a circle

                double dx = currentCenterPoint.X1 - pointX;
                double dy = currentCenterPoint.X2 - pointY;

                returnValue = (dx * dx) + (dy * dy) <= (RadiusPixels.X1 * RadiusPixels.X1);

            }

            if (returnValue) break;

        }

        return returnValue;
    }

}
