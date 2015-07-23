package com.delsquared.lightningdots.game;

import com.delsquared.lightningdots.ntuple.NTuple;
import com.delsquared.lightningdots.ntuple.NTupleType;
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
    public final List<ClickTargetSettingsShuffle> listClickTargetSettingsShuffles;

    public LevelDefinitionLadder() {
        level = 1;
        listClickTargetDefinitionNames = new ArrayList<>();
        mapClickTargetDefinitions = new HashMap<>();
        mapTransitionTriggers = new HashMap<>();
        mapRandomChangeTriggers = new HashMap<>();
        listClickTargetSettingsShuffles = new ArrayList<>();
    }

    public LevelDefinitionLadder(
            int level
            , List<String> listClickTargetDefinitionNames
            , Map<String, ClickTargetDefinition> mapClickTargetDefinitions
            , Map<NTuple, List<TransitionTrigger>> mapTransitionTriggers
            , Map<NTuple, List<RandomChangeTrigger>> mapRandomChangeTriggers
            , List<ClickTargetSettingsShuffle> listClickTargetSettingsShuffles) {
        this.level = level;
        this.listClickTargetDefinitionNames = listClickTargetDefinitionNames;
        this.mapClickTargetDefinitions = mapClickTargetDefinitions;
        this.mapTransitionTriggers = mapTransitionTriggers;
        this.mapRandomChangeTriggers = mapRandomChangeTriggers;
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

    public static class TransitionTrigger {

        public final String sourceClickTargetName;
        public final String sourceClickTargetProfileName;
        public final String targetClickTargetName;
        public final String targetClickTargetProfileName;
        public final boolean randomTargetClickTarget;
        public final boolean randomTargetClickTargetProfile;
        public final List<SyncVariableTrigger> listSyncVariableTriggers;

        public TransitionTrigger(
                String sourceClickTargetName
                , String sourceClickTargetProfileName
                , String targetClickTargetName
                , String targetClickTargetProfileName
                , boolean randomTargetClickTarget
                , boolean randomTargetClickTargetProfile
                , List<SyncVariableTrigger> listSyncVariableTriggers) {
            this.sourceClickTargetName = sourceClickTargetName;
            this.sourceClickTargetProfileName = sourceClickTargetProfileName;
            this.targetClickTargetName = targetClickTargetName;
            this.targetClickTargetProfileName = targetClickTargetProfileName;
            this.randomTargetClickTarget = randomTargetClickTarget;
            this.randomTargetClickTargetProfile = randomTargetClickTargetProfile;
            this.listSyncVariableTriggers = listSyncVariableTriggers;
        }

        public ClickTarget.ClickTargetProfileTransitionEvent toTransitionEvent() {
            return new ClickTarget.ClickTargetProfileTransitionEvent(
                    targetClickTargetName
                    , targetClickTargetProfileName
            );
        }

    }

    public static class RandomChangeTrigger {

        public final String sourceClickTargetName;
        public final String sourceClickTargetProfileName;
        public final String sourceVariable;
        public final String targetClickTargetName;
        public final String targetClickTargetProfileName;
        public final String targetVariable;
        public final List<SyncVariableTrigger> listSyncVariableTriggers;

        public RandomChangeTrigger(
                String sourceClickTargetName
                , String sourceClickTargetProfileName
                , String sourceVariable
                , String targetClickTargetName
                , String targetClickTargetProfileName
                , String targetVariable
                , List<SyncVariableTrigger> listSyncVariableTriggers) {
            this.sourceClickTargetName = sourceClickTargetName;
            this.sourceClickTargetProfileName = sourceClickTargetProfileName;
            this.sourceVariable = sourceVariable;
            this.targetClickTargetName = targetClickTargetName;
            this.targetClickTargetProfileName = targetClickTargetProfileName;
            this.targetVariable = targetVariable;
            this.listSyncVariableTriggers = listSyncVariableTriggers;
        }

        public ClickTarget.RandomChangeEvent toRandomChangeEvent() {
            return new ClickTarget.RandomChangeEvent(
                    targetClickTargetName
                    , targetClickTargetProfileName
                    , targetVariable
            );
        }

    }

    public static class SyncVariableTrigger {

        public final String sourceClickTargetName;
        public final String sourceClickTargetProfileName;
        public final String targetClickTargetName;
        public final String targetClickTargetProfileName;
        public final String variableName;
        public final MODE mode;
        public final double value;

        public SyncVariableTrigger(
                String sourceClickTargetName
                , String sourceClickTargetProfileName
                , String targetClickTargetName
                , String targetClickTargetProfileName
                , String variableName
                , MODE mode
                , double value) {
            this.sourceClickTargetName = sourceClickTargetName;
            this.sourceClickTargetProfileName = sourceClickTargetProfileName;
            this.targetClickTargetName = targetClickTargetName;
            this.targetClickTargetProfileName = targetClickTargetProfileName;
            this.variableName = variableName;
            this.mode = mode;
            this.value = value;
        }

        public NTuple getKey() {
            return nTupleTypeSyncVariableTriggerKey.createNTuple(
                    targetClickTargetName
                    , targetClickTargetProfileName
                    , value
            );
        }

        public enum MODE {
            SNAP_TO_TARGET
            , RANDOMIZE
            , LITERAL_VALUE
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

    public static final NTupleType nTupleTypeTransitionTriggerKey =
            NTupleType.DefaultFactory.create(
                    String.class
                    , String.class
            );
    public static final NTupleType nTupleTypeRandomChangeTriggerKey =
            NTupleType.DefaultFactory.create(
                    String.class
                    , String.class
                    , String.class
            );

    public static final NTupleType nTupleTypeSyncVariableTriggerKey =
            NTupleType.DefaultFactory.create(
                    String.class
                    , String.class
                    , String.class
            );
}
