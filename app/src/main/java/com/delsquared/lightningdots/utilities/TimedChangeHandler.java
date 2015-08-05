package com.delsquared.lightningdots.utilities;

public class TimedChangeHandler {

    public final boolean canRandomlyChange;
    public final double value;
    public final INTERVAL interval;
    public final boolean bounceOnRandomChange;

    private double timeElapsedSinceLastChange = 0.0;

    public TimedChangeHandler() {
        canRandomlyChange = false;
        value = 0.0;
        interval = INTERVAL.NONE;
        bounceOnRandomChange = false;
        this.timeElapsedSinceLastChange = 0.0;
    }

    public TimedChangeHandler(
            boolean canRandomlyChange
            , double value
            , INTERVAL interval) {
        this.canRandomlyChange = canRandomlyChange;
        this.value = value;
        this.interval = interval;
        this.bounceOnRandomChange = false;
        this.timeElapsedSinceLastChange = 0.0;
    }

    public TimedChangeHandler(
            boolean canRandomlyChange
            , double value
            , INTERVAL interval
            , boolean bounceOnRandomChange) {
        this.canRandomlyChange = canRandomlyChange;
        this.value = value;
        this.interval = interval;
        this.bounceOnRandomChange = bounceOnRandomChange;
        this.timeElapsedSinceLastChange = 0.0;
    }

    public boolean checkTimedChange(double dt) {

        timeElapsedSinceLastChange += dt;

        // No change if it can't randomly change
        if (!canRandomlyChange) {
            return false;
        }

        // Initialize the result
        boolean shouldRandomlyChange = false;

        // Check if it should change randomly in regular intervals
        if (interval == INTERVAL.FIXED) {

            // Check if the time since the last random change has exceeded the interval
            if (timeElapsedSinceLastChange >= value) {

                // Set the random change flag to true
                shouldRandomlyChange = true;

                // Reset the timer
                timeElapsedSinceLastChange = 0.0;

            }

        }

        // It should change randomly in random intervals
        else if (interval == INTERVAL.RANDOM) {

            // Generate random value
            double probabilityThreshold = 1.0 - Math.pow(1.0 - value, dt);
            double changeCheck = UtilityFunctions.generateRandomValue(0.0, 1.0, false);

            // Check if it should randomly change
            if (changeCheck < probabilityThreshold) {

                // Set the random change flag
                shouldRandomlyChange = true;

            }

        }

        return shouldRandomlyChange;

    }

    public enum INTERVAL {
        NONE
        , FIXED
        , RANDOM
    }

}
