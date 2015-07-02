package com.delsquared.lightningdots.game;

import java.util.ArrayList;
import java.util.HashMap;

public class ClickTargetProfileScript {

    private final SCRIPT_TRANSITION_MODE scriptTransitionMode;
    private final SCRIPT_TRANSITION_INTERVAL scriptTransitionInterval;
    private final SCRIPT_CYCLE_DIRECTION scriptCycleDirection;
    private final String initialClickTargetProfileName;
    private final ArrayList<String> arrayListClickTargetNames;
    private final HashMap<String, ClickTargetProfile> mapClickTargetProfiles;
    private String currentClickTargetProfileName = "";
    private double totalTimeElapsedSinceLastTransitionSeconds = 0.0;

    public ClickTargetProfileScript() {
        scriptTransitionMode = SCRIPT_TRANSITION_MODE.CONSTANT;
        scriptTransitionInterval = SCRIPT_TRANSITION_INTERVAL.CONSTANT;
        scriptCycleDirection = SCRIPT_CYCLE_DIRECTION.INCREASING;
        initialClickTargetProfileName = "";
        arrayListClickTargetNames = new ArrayList<>();
        mapClickTargetProfiles = new HashMap<>();
    }

    public ClickTargetProfileScript(
            SCRIPT_TRANSITION_MODE scriptTransitionMode
            , SCRIPT_TRANSITION_INTERVAL scriptTransitionInterval
            , SCRIPT_CYCLE_DIRECTION scriptCycleDirection
            , String initialClickTargetProfileName
            , ArrayList<String> arrayListClickTargetNames
            , HashMap<String, ClickTargetProfile> mapClickTargetProfiles) {
        this.scriptTransitionMode = scriptTransitionMode;
        this.scriptTransitionInterval = scriptTransitionInterval;
        this.scriptCycleDirection = scriptCycleDirection;
        this.initialClickTargetProfileName = initialClickTargetProfileName;
        this.arrayListClickTargetNames = arrayListClickTargetNames;
        this.mapClickTargetProfiles = mapClickTargetProfiles;

        // Check if the initial click target profile name is not in the list
        if (!mapClickTargetProfiles.containsKey(initialClickTargetProfileName)) {
            // Set the initial click target profile name to the first name in the list
            if (arrayListClickTargetNames.size() > 0) {
                initialClickTargetProfileName = arrayListClickTargetNames.get(0);
            }
        }
        currentClickTargetProfileName = initialClickTargetProfileName;
    }

    private void resetCurrentClickTargetProfileName() {
        if (!mapClickTargetProfiles.containsKey(currentClickTargetProfileName)) {
            if (arrayListClickTargetNames.size() > 0) {
                currentClickTargetProfileName = arrayListClickTargetNames.get(0);
            }
        }
    }

    public ClickTargetProfile getCurrentClickTargetProfile() {
        if (mapClickTargetProfiles.containsKey(currentClickTargetProfileName)) {
            return mapClickTargetProfiles.get(currentClickTargetProfileName);
        } else {
            resetCurrentClickTargetProfileName();
            if (mapClickTargetProfiles.containsKey(currentClickTargetProfileName)) {
                return mapClickTargetProfiles.get(currentClickTargetProfileName);
            }
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

    public void setCurrentClickTargetProfileName(String newName) {
        if (mapClickTargetProfiles.containsKey(newName)) {
            this.currentClickTargetProfileName = newName;
        }
    }

    public boolean processTransition(double timeElapsedSinceLastUpdateSeconds) {

        // Check if the map does not contain the current click target name
        if (!mapClickTargetProfiles.containsKey(currentClickTargetProfileName)) {
            return false;
        }

        // Get the current ClickTargetProfile
        ClickTargetProfile currentClickTargetProfile = mapClickTargetProfiles.get(currentClickTargetProfileName);

        // Check if the current click target is null
        if (currentClickTargetProfile == null) {
            return false;
        }

        // Initialize the flag for performing a transition
        boolean performTransition = false;

        // Add the time elapsed to the total time elapsed since the last transition
        totalTimeElapsedSinceLastTransitionSeconds += timeElapsedSinceLastUpdateSeconds;

        // Make sure the mode isn't constant
        if (scriptTransitionMode != SCRIPT_TRANSITION_MODE.CONSTANT &&
                scriptTransitionInterval != SCRIPT_TRANSITION_INTERVAL.CONSTANT) {

            // Get the transition value for the current click target
            double currentTransitionValue = currentClickTargetProfile.scriptTransitionValue;

            switch (scriptTransitionInterval) {

                case REGULAR:
                    if (totalTimeElapsedSinceLastTransitionSeconds >= currentTransitionValue) {
                        performTransition = true;
                    }
                    break;

                case RANDOM:
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
                totalTimeElapsedSinceLastTransitionSeconds = 0.0;

                // Get the index of the current click target name
                int currentClickTargetNameIndex = arrayListClickTargetNames.indexOf(currentClickTargetProfileName);

                switch (scriptTransitionMode) {

                    case CYCLE:
                        switch (scriptCycleDirection) {

                            case INCREASING:
                                // Check if we have reached the last index
                                if (currentClickTargetNameIndex >= arrayListClickTargetNames.size() - 1) {
                                    currentClickTargetNameIndex = 0;
                                } else {
                                    // Increment the index
                                    currentClickTargetNameIndex++;
                                }
                                break;

                            case DECREASING:
                                // Check if we have reached the min index
                                if (currentClickTargetNameIndex <= 0) {
                                    // Reset index to max
                                    currentClickTargetNameIndex = arrayListClickTargetNames.size() - 1;
                                } else {
                                    // Decrement the index
                                    currentClickTargetNameIndex--;
                                }
                                break;
                        }

                        break;

                    case RANDOM:
                        double randomValue = Math.random() * (double)arrayListClickTargetNames.size();
                        int newIndex = (int) Math.floor(randomValue);
                        if (newIndex == arrayListClickTargetNames.size()) {
                            newIndex = arrayListClickTargetNames.size() - 1;
                        }
                        currentClickTargetNameIndex = newIndex;
                        break;

                }

                // Set the new ClickTargetProfile name
                currentClickTargetProfileName = arrayListClickTargetNames.get(currentClickTargetNameIndex);

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
