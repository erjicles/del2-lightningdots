package com.delsquared.lightningdots.game;

public class GameTimer {

	private long startTimeMillis;
	private long endTimeMillis;
	private long timeElapsedMillis;
	private long timeRemainingMillis;
	int displayTimeElapsedSeconds;
	int displayTimeRemainingSeconds;
	private long totalTimeMillis; // Time limit
	@SuppressWarnings("unused")
	private long lastPauseStartTimeMillis;
	private long lastPauseEndTimeMillis;
	private long lastUpdateTimeMillis;

	@SuppressWarnings({"unused", "FieldCanBeLocal"})
	private GameTimerType gameTimerType;
	private boolean isRunning;


	public GameTimer() {
		startTimeMillis = 0;
		endTimeMillis = 0;
		timeElapsedMillis = 0;
		timeRemainingMillis = 0;
		displayTimeRemainingSeconds = 0;
		displayTimeElapsedSeconds = 0;
		totalTimeMillis = 0;
		lastPauseStartTimeMillis = 0;
		lastPauseEndTimeMillis = 0;
		lastUpdateTimeMillis = 0;
	}

	public long getEndTimeMillis() {
		return endTimeMillis;
	}

	public void initialize(
			long currentTimeMillis
			, long totalTimeMillis
			, GameTimerType gameTimerType) {

		// Flag the timer as not running
		isRunning = false;

		// Set the timer type
		this.gameTimerType = gameTimerType;

		// Set the start and end times
		startTimeMillis = currentTimeMillis;
		this.totalTimeMillis = totalTimeMillis;
		endTimeMillis = startTimeMillis + this.totalTimeMillis;

		// Reset the elapsed times
		reset(currentTimeMillis);

		lastPauseStartTimeMillis = currentTimeMillis;
		lastPauseEndTimeMillis = 0;

		lastUpdateTimeMillis = currentTimeMillis;
	}

	public void reset(long currentTimeMillis) {

		// Flag the timer as not running
		isRunning = false;

		// Reset the elapsed times
		timeElapsedMillis = 0;
		timeRemainingMillis = totalTimeMillis;
		displayTimeElapsedSeconds = 0;
		displayTimeRemainingSeconds = (int) Math.ceil(totalTimeMillis / 1000.f);
		lastUpdateTimeMillis = currentTimeMillis;

	}

	public void start(long currentTimeMillis) {
		isRunning = true;
		lastPauseEndTimeMillis = currentTimeMillis;
		lastUpdateTimeMillis = lastPauseEndTimeMillis;
		endTimeMillis = currentTimeMillis + totalTimeMillis - timeElapsedMillis;
		processNewTime(currentTimeMillis);
	}

	public void pause(long currentTimeMillis) {
		processNewTime(currentTimeMillis);
		isRunning = false;
		lastPauseStartTimeMillis = currentTimeMillis;
	}

	public long processNewTime(long currentTimeMillis) {

		long timeElapsedSinceLastUpdateMillis = currentTimeMillis - lastUpdateTimeMillis;

		if (isRunning) {

			// Update the time elapsed and time remaining
			timeRemainingMillis = endTimeMillis - currentTimeMillis;
			timeElapsedMillis += timeElapsedSinceLastUpdateMillis;

			// Update the display times
			float timeElapsedSeconds = timeElapsedMillis / 1000.f;
			float timeRemainingSeconds = timeRemainingMillis / 1000.f;
			displayTimeElapsedSeconds = (int) Math.floor(timeElapsedSeconds);
			displayTimeRemainingSeconds = (int) Math.ceil(timeRemainingSeconds);

			// Set the last update time to now
			lastUpdateTimeMillis = currentTimeMillis;

		}

		return timeElapsedSinceLastUpdateMillis;

	}

	public long getTimeElapsedMillis() { return timeElapsedMillis; }

	public int getDisplayTimeRemaining() {
		return displayTimeRemainingSeconds;
	}

	public int getDisplayTimeElapsed() {
		return displayTimeElapsedSeconds;
	}

	public boolean timeLimitExceeded() {
		return timeElapsedMillis >= totalTimeMillis;
	}

	public enum GameTimerType {
		COUNTDOWN
		, STOPWATCH
	}

}
