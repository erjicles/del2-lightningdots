package com.delsquared.lightningdots.game;

import android.graphics.RectF;

public class UserClick {

	private final int xPixels;
	private final int yPixels;
	private final int radiusPixels;
	private final String paintString;
	private final RectF boundingRectangle;
	private final boolean isInsideTarget;

	public UserClick(
			int xPixels
			, int yPixels
			, int radiusPixels
			, String paintString
			, boolean isInsideTarget) {
		this.xPixels = xPixels;
		this.yPixels = yPixels;
		this.radiusPixels = radiusPixels;
		this.paintString = paintString;
		this.isInsideTarget = isInsideTarget;
		this.boundingRectangle =
				new RectF(
						xPixels - radiusPixels
						, yPixels - radiusPixels
						, xPixels + radiusPixels
						, yPixels + radiusPixels);
	}

	public int getXPixels() { return xPixels; }
	public int getYPixels() { return yPixels; }
	public int getRadiusPixels() { return radiusPixels; }
	public String getPaintString() { return paintString; }
	public RectF getBoundingRectangle() { return boundingRectangle; }
	public boolean getIsInsideTarget() { return isInsideTarget; }

}
