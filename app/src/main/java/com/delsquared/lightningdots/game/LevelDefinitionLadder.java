package com.delsquared.lightningdots.game;

import com.delsquared.lightningdots.ntuple.NTuple;
import com.delsquared.lightningdots.ntuple.NTupleType;
import com.delsquared.lightningdots.utilities.PositionEvolverVariableAttractor;
import com.delsquared.lightningdots.utilities.RandomChangeTrigger;
import com.delsquared.lightningdots.utilities.SyncVariableTrigger;
import com.delsquared.lightningdots.utilities.TransitionTrigger;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelDefinitionLadder {

    public final int level;
    public final List<String> listClickTargetDefinitionNames;
    public final Map<String, ClickTargetDefinition> mapClickTargetDefinitions;
    public final Map<NTuple, List<TransitionTrigger>> mapTransitionTriggers;
    public final Map<NTuple, List<RandomChangeTrigger>> mapRandomChangeTriggers;
    public final Map<NTuple, List<PositionEvolverVariableAttractor>> mapPositionEvolverVariableAttractors;
    public final List<ClickTargetSettingsShuffle> listClickTargetSettingsShuffles;

    public LevelDefinitionLadder() {
        level = 1;
        listClickTargetDefinitionNames = new ArrayList<>();
        mapClickTargetDefinitions = new HashMap<>();
        mapTransitionTriggers = new HashMap<>();
        mapRandomChangeTriggers = new HashMap<>();
        mapPositionEvolverVariableAttractors = new HashMap<>();
        listClickTargetSettingsShuffles = new ArrayList<>();
    }

    public LevelDefinitionLadder(
            int level
            , List<String> listClickTargetDefinitionNames
            , Map<String, ClickTargetDefinition> mapClickTargetDefinitions
            , Map<NTuple, List<TransitionTrigger>> mapTransitionTriggers
            , Map<NTuple, List<RandomChangeTrigger>> mapRandomChangeTriggers
            , Map<NTuple, List<PositionEvolverVariableAttractor>> mapPositionEvolverVariableAttractors
            , List<ClickTargetSettingsShuffle> listClickTargetSettingsShuffles) {
        this.level = level;
        this.listClickTargetDefinitionNames = listClickTargetDefinitionNames;
        this.mapClickTargetDefinitions = mapClickTargetDefinitions;
        this.mapTransitionTriggers = mapTransitionTriggers;
        this.mapRandomChangeTriggers = mapRandomChangeTriggers;
        this.mapPositionEvolverVariableAttractors = mapPositionEvolverVariableAttractors;
        this.listClickTargetSettingsShuffles = listClickTargetSettingsShuffles;

        // Perform any ClickTarget settings shuffles
        performClickTargetSettingsShuffles();

    }

    public void performClickTargetSettingsShuffles() {

        // Loop through the ClickTargetSettingsShuffles
        for (ClickTargetSettingsShuffle clickTargetSettingsShuffle : listClickTargetSettingsShuffles) {

            // Get the settings
            List<String> listShuffledClickTargetProfileNames = new ArrayList<>();
            for (String clickTargetName : listClickTargetDefinitionNames) {

                // Get the click target definition
                ClickTargetDefinition clickTargetDefinition = mapClickTargetDefinitions.get(clickTargetName);

                // Add the initial click target profile name
                listShuffledClickTargetProfileNames.add(clickTargetDefinition.clickTargetProfileScript.getInitialClickTargetProfileName());

            }

            // Check if we should preserve order of the settings
            if (clickTargetSettingsShuffle.preserveOrder) {

                // Get a random rotation value for the list
                int rotationAmount = UtilityFunctions.generateRandomIndex(0, listShuffledClickTargetProfileNames.size() - 1);

                // Perform rotation
                Collections.rotate(listShuffledClickTargetProfileNames, rotationAmount);

            }

            // We should truly shuffle
            else {

                // Perform shuffle
                Collections.shuffle(listShuffledClickTargetProfileNames, UtilityFunctions.randomizer);

            }

            // Loop through the settings
            for (String clickTargetSettingsShuffleSettingName : clickTargetSettingsShuffle.listClickTargetSettingsShuffleSettingNames) {

                // Check if the setting is the initial click target profile name
                if (clickTargetSettingsShuffleSettingName.contentEquals(ClickTargetSettingsShuffle.SETTING_NAME_INITIAL_CLICK_TARGET_PROFILE_NAME)) {

                    // Set the new initial click target profiles
                    for (int currentIndex = 0; currentIndex < listClickTargetDefinitionNames.size(); currentIndex++) {

                        // Get the current ClickTargetDefinition name
                        String clickTargetName = listClickTargetDefinitionNames.get(currentIndex);

                        // Get the current ClickTargetDefinition
                        ClickTargetDefinition clickTargetDefinition = mapClickTargetDefinitions.get(clickTargetName);

                        // Set the setting
                        clickTargetDefinition.clickTargetProfileScript.reinitializeInitialClickTargetProfileName(
                                listShuffledClickTargetProfileNames.get(currentIndex)
                        );

                    }

                }

            }

        }

    }

    public static class ClickTargetSettingsShuffle {

        public static final String SETTING_NAME_INITIAL_CLICK_TARGET_PROFILE_NAME = "initialClickTargetProfileName";

        public final List<String> listClickTargetSettingsShuffleSettingNames;
        private final boolean preserveOrder;

        public ClickTargetSettingsShuffle() {
            listClickTargetSettingsShuffleSettingNames = new ArrayList<>();
            preserveOrder = false;
        }

        public ClickTargetSettingsShuffle(
                List<String> listClickTargetSettingsShuffleSettingNames
                , boolean preserveOrder) {
            this.listClickTargetSettingsShuffleSettingNames = listClickTargetSettingsShuffleSettingNames;
            this.preserveOrder = preserveOrder;
        }

    }

}
