package com.delsquared.lightningdots.game;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.delsquared.lightningdots.utilities.PositionEvolver;
import com.delsquared.lightningdots.utilities.PositionVector;

import java.util.ArrayList;

public class ClickTargetSnapshot {

	private final PositionVector XPixels;
    private final PositionVector DXdtPixelsPerMilliPolar;
    private final PositionVector RadiusPixels;
    private final ArrayList<PositionVector> arrayListSecondaryPoints;

	public ClickTargetSnapshot() {
        XPixels = new PositionVector();
        DXdtPixelsPerMilliPolar = new PositionVector();
        RadiusPixels = new PositionVector();
        arrayListSecondaryPoints = new ArrayList<>();
	}

	public ClickTargetSnapshot(
			PositionVector XPixels
			, PositionVector DXdtPixelsPerMilliPolar
			, PositionVector RadiusPixels
            , ArrayList<PositionVector> arrayListSecondaryPoints
	) {
		this.XPixels = XPixels;
        this.DXdtPixelsPerMilliPolar = DXdtPixelsPerMilliPolar;
        this.RadiusPixels = RadiusPixels;
        this.arrayListSecondaryPoints = arrayListSecondaryPoints;
	}

	public PositionVector getXPixels() { return XPixels; }
    public PositionVector getDXdtPixelsPerMilliPolar() { return DXdtPixelsPerMilliPolar; }
    public PositionVector getRadiusPixels() { return RadiusPixels; }
    public ArrayList<PositionVector> getArrayListSecondaryPoints() { return this.arrayListSecondaryPoints; }

    public boolean equals(ClickTargetSnapshot otherClickTargetSnapshot) {

        return XPixels.equals(otherClickTargetSnapshot.getXPixels())
                && DXdtPixelsPerMilliPolar.equals(otherClickTargetSnapshot.getDXdtPixelsPerMilliPolar())
                && RadiusPixels.equals(otherClickTargetSnapshot.getRadiusPixels())
                && arrayListSecondaryPoints.equals(otherClickTargetSnapshot.getArrayListSecondaryPoints());

    }

    public void draw(Canvas canvas, Paint paintTarget) {

        // Draw primary target
        canvas.drawCircle(
                (float) XPixels.X1
                , (float) XPixels.X2
                , (float) RadiusPixels.X1
                , paintTarget);

        // Loop through the secondary points to draw
        for (PositionVector currentPoint : arrayListSecondaryPoints) {

            // Draw the current point
            canvas.drawCircle(
                    (float) currentPoint.X1
                    , (float) currentPoint.X2
                    , (float) RadiusPixels.X1
                    , paintTarget);

        }

    }

}
