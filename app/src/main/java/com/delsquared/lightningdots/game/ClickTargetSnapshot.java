package com.delsquared.lightningdots.game;

import com.delsquared.lightningdots.utilities.PositionVector;

public class ClickTargetSnapshot {

	private final PositionVector XPixels;
    private final PositionVector DXdtPixelsPerMilliPolar;
    private final PositionVector RadiusPixels;

	public ClickTargetSnapshot() {
        XPixels = new PositionVector();
        DXdtPixelsPerMilliPolar = new PositionVector();
        RadiusPixels = new PositionVector();
	}

	public ClickTargetSnapshot(
			PositionVector XPixels
			, PositionVector DXdtPixelsPerMilliPolar
			, PositionVector RadiusPixels
	) {
		this.XPixels = XPixels;
        this.DXdtPixelsPerMilliPolar = DXdtPixelsPerMilliPolar;
        this.RadiusPixels = RadiusPixels;
	}

	public PositionVector getXPixels() { return XPixels; }
    public PositionVector getDXdtPixelsPerMilliPolar() { return DXdtPixelsPerMilliPolar; }
    public PositionVector getRadiusPixels() { return RadiusPixels; }

    /*
    public float getRadiusPixels(Context context) {

		// Initialize the result
		float radiusPixels = 0.0f;

		// Get the resources
		Resources resources = context.getResources();

		// Convert the radius from inches to pixels
		radiusPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, (float) radiusInches, resources.getDisplayMetrics());

		return radiusPixels;

	}
	*/

    public boolean equals(ClickTargetSnapshot otherClickTargetSnapshot) {

        return XPixels.equals(otherClickTargetSnapshot.getXPixels())
                && DXdtPixelsPerMilliPolar.equals(otherClickTargetSnapshot.getDXdtPixelsPerMilliPolar())
                && RadiusPixels.equals(otherClickTargetSnapshot.getRadiusPixels());

    }

}
