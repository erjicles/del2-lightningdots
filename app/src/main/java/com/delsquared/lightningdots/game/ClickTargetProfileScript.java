package com.delsquared.lightningdots.game;

import java.util.ArrayList;

public class ClickTargetProfileScript {

    private SCRIPT_TRANSITION_MODE scriptTransitionMode;
    private SCRIPT_TRANSITION_INTERVAL scriptTransitionInterval;
    private SCRIPT_CYCLE_DIRECTION scriptCycleDirection;
    private ArrayList<Double> arrayListTransitionValue;
    private ArrayList<ClickTargetProfile> arrayListClickTargetProfile;
    private int currentClickTargetProfileIndex = -1;
    private long totalTimeElapsedSinceLastTransitionMillis = 0;

    public ClickTargetProfileScript() {
        scriptTransitionMode = SCRIPT_TRANSITION_MODE.CONSTANT;
        scriptTransitionInterval = SCRIPT_TRANSITION_INTERVAL.CONSTANT;
        scriptCycleDirection = SCRIPT_CYCLE_DIRECTION.INCREASING;
        arrayListTransitionValue = new ArrayList<Double>();
        arrayListClickTargetProfile = new ArrayList<ClickTargetProfile>();
    }

    public ClickTargetProfileScript(
            SCRIPT_TRANSITION_MODE scriptTransitionMode
            , SCRIPT_TRANSITION_INTERVAL scriptTransitionInterval
            , SCRIPT_CYCLE_DIRECTION scriptCycleDirection
            , ArrayList<Double> arrayListTransitionValue
            , ArrayList<ClickTargetProfile> arrayListClickTargetProfile
            , int currentClickTargetProfileIndex) {
        this.scriptTransitionMode = scriptTransitionMode;
        this.scriptTransitionInterval = scriptTransitionInterval;
        this.scriptCycleDirection = scriptCycleDirection;
        this.arrayListTransitionValue = arrayListTransitionValue;
        this.arrayListClickTargetProfile = arrayListClickTargetProfile;
        this.currentClickTargetProfileIndex = currentClickTargetProfileIndex;
    }

    public int getCurrentClickTargetProfileIndex() {
        return this.currentClickTargetProfileIndex;
    }

    public ClickTargetProfile getCurrentClickTargetProfile() {
        try {
            if (currentClickTargetProfileIndex > -1) {
                return arrayListClickTargetProfile.get(currentClickTargetProfileIndex);
            }
        } catch (Exception e) {

        }

        return null;
    }

    public SCRIPT_TRANSITION_MODE getScriptTransitionMode() {
        return this.scriptTransitionMode;
    }

    public SCRIPT_TRANSITION_INTERVAL getScriptTransitionInterval() {
        return this.scriptTransitionInterval;
    }

    public SCRIPT_CYCLE_DIRECTION getScriptCycleDirection() {
        return this.scriptCycleDirection;
    }

    public void setCurrentClickTargetProfileIndex(int newIndex) {
        this.currentClickTargetProfileIndex = newIndex;
    }

    public boolean processTransition(long timeElapsedSinceLastUpdateMillis) {

        if (arrayListClickTargetProfile.size() == 0) {
            return false;
        }

        boolean performTransition = false;

        // Add the time elapsed to the total time elapsed since the last transition
        totalTimeElapsedSinceLastTransitionMillis += timeElapsedSinceLastUpdateMillis;

        // Make sure the mode isn't constant
        if (scriptTransitionMode != SCRIPT_TRANSITION_MODE.CONSTANT &&
                scriptTransitionInterval != SCRIPT_TRANSITION_INTERVAL.CONSTANT) {

            double currentTransitionValue = arrayListTransitionValue.get(currentClickTargetProfileIndex);

            switch (scriptTransitionInterval) {

                case REGULAR:
                    if (totalTimeElapsedSinceLastTransitionMillis >= currentTransitionValue) {
                        performTransition = true;
                    }
                    break;

                case RANDOM:
                    double timeElapsedSinceLastUpdateSeconds = (double) timeElapsedSinceLastUpdateMillis / 1000.0;
                    double probabilityThreshold =
                            1.0 - Math.pow(1.0 - currentTransitionValue, timeElapsedSinceLastUpdateSeconds);
                    double transitionCheck = Math.random();
                    if (transitionCheck < probabilityThreshold) {
                        performTransition = true;
                    }
                    break;

                default:
            }

            // Check if we should perform a transition
            if (performTransition == true) {

                // Reset the total time elapsed since the last transition
                totalTimeElapsedSinceLastTransitionMillis = 0;

                switch (scriptTransitionMode) {

                    case CYCLE:
                        switch (scriptCycleDirection) {

                            case INCREASING:
                                // Check if we have reached the last index
                                if (currentClickTargetProfileIndex >= arrayListClickTargetProfile.size() - 1) {
                                    // Reset to index 0
                                    currentClickTargetProfileIndex = 0;
                                } else {
                                    // Increment the index
                                    currentClickTargetProfileIndex++;
                                }
                                break;

                            case DECREASING:
                                // Check if we have reached the min index
                                if (currentClickTargetProfileIndex <= 0) {
                                    // Reset index to max
                                    currentClickTargetProfileIndex = arrayListClickTargetProfile.size() - 1;
                                } else {
                                    // Decrement the index
                                    currentClickTargetProfileIndex--;
                                }
                                break;
                        }

                        break;

                    case RANDOM:
                        double randomValue = Math.random() * (double)arrayListClickTargetProfile.size();
                        int newIndex = (int) Math.floor(randomValue);
                        if (newIndex == arrayListClickTargetProfile.size()) {
                            newIndex = arrayListClickTargetProfile.size() - 1;
                        }
                        currentClickTargetProfileIndex = newIndex;
                        break;

                }

            }

        }

        return performTransition;

    }

    public enum SCRIPT_TRANSITION_MODE {
        CONSTANT
        , CYCLE
        , RANDOM
    }

    public enum SCRIPT_TRANSITION_INTERVAL{
        CONSTANT
        , REGULAR
        , RANDOM
    }

    public enum SCRIPT_CYCLE_DIRECTION {
        INCREASING
        , DECREASING
    }
}
