package com.delsquared.lightningdots.game;

import com.delsquared.lightningdots.utilities.UtilityFunctions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClickTargetProfileScript {

    private final SCRIPT_TRANSITION_MODE scriptTransitionMode;
    private final SCRIPT_TRANSITION_INTERVAL scriptTransitionInterval;
    private final SCRIPT_CYCLE_DIRECTION scriptCycleDirection;
    private final String initialClickTargetProfileName;
    private final boolean randomInitialClickTargetProfile;
    private final List<String> listClickTargetNames;
    private final Map<String, ClickTargetProfile> mapClickTargetProfiles;
    private String currentClickTargetProfileName = "";
    private double totalTimeElapsedSinceLastTransitionSeconds = 0.0;

    public ClickTargetProfileScript() {
        scriptTransitionMode = SCRIPT_TRANSITION_MODE.CONSTANT;
        scriptTransitionInterval = SCRIPT_TRANSITION_INTERVAL.CONSTANT;
        scriptCycleDirection = SCRIPT_CYCLE_DIRECTION.INCREASING;
        initialClickTargetProfileName = "";
        randomInitialClickTargetProfile = false;
        listClickTargetNames = new ArrayList<>();
        mapClickTargetProfiles = new LinkedHashMap<>();
    }

    public ClickTargetProfileScript(
            SCRIPT_TRANSITION_MODE scriptTransitionMode
            , SCRIPT_TRANSITION_INTERVAL scriptTransitionInterval
            , SCRIPT_CYCLE_DIRECTION scriptCycleDirection
            , String initialClickTargetProfileName
            , boolean randomInitialClickTargetProfile
            , List<String> listClickTargetNames
            , Map<String, ClickTargetProfile> mapClickTargetProfiles) {
        this.scriptTransitionMode = scriptTransitionMode;
        this.scriptTransitionInterval = scriptTransitionInterval;
        this.scriptCycleDirection = scriptCycleDirection;
        this.initialClickTargetProfileName = initialClickTargetProfileName;
        this.randomInitialClickTargetProfile = randomInitialClickTargetProfile;
        this.listClickTargetNames = listClickTargetNames;
        this.mapClickTargetProfiles = mapClickTargetProfiles;

        // Check if we should randomize the initial click target
        if (randomInitialClickTargetProfile) {

            // Select a random click target profile
            randomizeClickTargetProfile();

            // Set the random click target profile as the initial
            initialClickTargetProfileName = currentClickTargetProfileName;

        }

        // Check if the initial click target profile name is not in the list
        if (!mapClickTargetProfiles.containsKey(initialClickTargetProfileName)) {
            // Set the initial click target profile name to the first name in the list
            if (listClickTargetNames.size() > 0) {
                initialClickTargetProfileName = listClickTargetNames.get(0);
            }
        }
        currentClickTargetProfileName = initialClickTargetProfileName;
    }

    private void resetCurrentClickTargetProfileName() {
        if (!mapClickTargetProfiles.containsKey(currentClickTargetProfileName)) {
            if (listClickTargetNames.size() > 0) {
                currentClickTargetProfileName = listClickTargetNames.get(0);
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

        // Set the profile name
        if (mapClickTargetProfiles.containsKey(newName)) {
            this.currentClickTargetProfileName = newName;
        }

        // Reset the transition clock
        resetTransitionClock();

    }

    public void randomizeClickTargetProfile() {

        // Check if there are profiles
        if (mapClickTargetProfiles.size() > 0) {

            // Get the random index
            int randomIndex = UtilityFunctions.generateRandomIndex(0, listClickTargetNames.size() - 1);

            // Set the new current profile
            setCurrentClickTargetProfileName(listClickTargetNames.get(randomIndex));

        }

    }

    public void resetTransitionClock() {
        totalTimeElapsedSinceLastTransitionSeconds = 0.0;
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
                    double transitionCheck = UtilityFunctions.generateRandomValue(0.0, 1.0, false);
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
                int currentClickTargetNameIndex = listClickTargetNames.indexOf(currentClickTargetProfileName);

                switch (scriptTransitionMode) {

                    case CYCLE:
                        switch (scriptCycleDirection) {

                            case INCREASING:
                                // Check if we have reached the last index
                                if (currentClickTargetNameIndex >= listClickTargetNames.size() - 1) {
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
                                    currentClickTargetNameIndex = listClickTargetNames.size() - 1;
                                } else {
                                    // Decrement the index
                                    currentClickTargetNameIndex--;
                                }
                                break;
                        }

                        break;

                    case RANDOM:
                        int newIndex = UtilityFunctions.generateRandomIndex(0, listClickTargetNames.size());
                        if (newIndex == listClickTargetNames.size()) {
                            newIndex = listClickTargetNames.size() - 1;
                        }
                        currentClickTargetNameIndex = newIndex;
                        break;

                }

                // Set the new ClickTargetProfile name
                currentClickTargetProfileName = listClickTargetNames.get(currentClickTargetNameIndex);

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
