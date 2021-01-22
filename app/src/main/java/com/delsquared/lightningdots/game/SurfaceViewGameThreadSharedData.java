package com.delsquared.lightningdots.game;

public class SurfaceViewGameThreadSharedData {

	private final int canvasWidth;
	private final int canvasHeight;

	private final double currentAverageTimeBetweenFrames;

	public SurfaceViewGameThreadSharedData() {
		canvasWidth = 1;
		canvasHeight = 1;
		currentAverageTimeBetweenFrames = 0.0;
	}

	public SurfaceViewGameThreadSharedData(
			int canvasWidth
			, int canvasHeight
			, double currentAverageTimeBetweenFrames
	) {
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
		this.currentAverageTimeBetweenFrames = currentAverageTimeBetweenFrames;
	}

	public int getCanvasWidth() { return canvasWidth; }
	public int getCanvasHeight() { return canvasHeight; }
	@SuppressWarnings("unused")
	public double getCurrentAverageTimeBetweenFrames() { return currentAverageTimeBetweenFrames; }

}
