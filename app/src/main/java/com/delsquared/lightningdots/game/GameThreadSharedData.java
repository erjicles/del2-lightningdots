package com.delsquared.lightningdots.game;

public class GameThreadSharedData {

	private final long currentTimeMillis;

	public GameThreadSharedData() {
		currentTimeMillis = 0;
	}

	public GameThreadSharedData(
			long currentTimeMillis
	) {
		this.currentTimeMillis = currentTimeMillis;
	}

	public long getCurrentTimeMillis() { return currentTimeMillis; }

}
