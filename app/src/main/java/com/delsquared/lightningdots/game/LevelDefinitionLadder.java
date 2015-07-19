package com.delsquared.lightningdots.game;

import com.delsquared.lightningdots.ntuple.NTuple;
import com.delsquared.lightningdots.ntuple.NTupleType;

import java.util.ArrayList;
import java.util.HashMap;

public class LevelDefinitionLadder {

    public final int level;
    public final HashMap<String, ClickTargetDefinition> mapClickTargetDefinitions;
    public final HashMap<NTuple, ArrayList<TransitionTrigger>> mapTransitionTriggers;
    public final HashMap<NTuple, ArrayList<RandomChangeTrigger>> mapRandomChangeTriggers;

    public LevelDefinitionLadder() {
        level = 1;
        mapClickTargetDefinitions = new HashMap<>();
        mapTransitionTriggers = new HashMap<>();
        mapRandomChangeTriggers = new HashMap<>();
    }

    public LevelDefinitionLadder(
            int level
            , HashMap<String, ClickTargetDefinition> mapClickTargetDefinitions
            , HashMap<NTuple, ArrayList<TransitionTrigger>> mapTransitionTriggers
            , HashMap<NTuple, ArrayList<RandomChangeTrigger>> mapRandomChangeTriggers) {
        this.level = level;
        this.mapClickTargetDefinitions = mapClickTargetDefinitions;
        this.mapTransitionTriggers = mapTransitionTriggers;
        this.mapRandomChangeTriggers = mapRandomChangeTriggers;
    }

    public static class TransitionTrigger {

        public final String sourceClickTargetName;
        public final String sourceClickTargetProfileName;
        public final String targetClickTargetName;
        public final String targetClickTargetProfileName;
        public final ArrayList<SyncVariableTrigger> arrayListSyncVariableTriggers;

        public TransitionTrigger(
                String sourceClickTargetName
                , String sourceClickTargetProfileName
                , String targetClickTargetName
                , String targetClickTargetProfileName
                , ArrayList<SyncVariableTrigger> arrayListSyncVariableTriggers) {
            this.sourceClickTargetName = sourceClickTargetName;
            this.sourceClickTargetProfileName = sourceClickTargetProfileName;
            this.targetClickTargetName = targetClickTargetName;
            this.targetClickTargetProfileName = targetClickTargetProfileName;
            this.arrayListSyncVariableTriggers = arrayListSyncVariableTriggers;
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
        public final ArrayList<SyncVariableTrigger> arrayListSyncVariableTriggers;

        public RandomChangeTrigger(
                String sourceClickTargetName
                , String sourceClickTargetProfileName
                , String sourceVariable
                , String targetClickTargetName
                , String targetClickTargetProfileName
                , String targetVariable
                , ArrayList<SyncVariableTrigger> arrayListSyncVariableTriggers) {
            this.sourceClickTargetName = sourceClickTargetName;
            this.sourceClickTargetProfileName = sourceClickTargetProfileName;
            this.sourceVariable = sourceVariable;
            this.targetClickTargetName = targetClickTargetName;
            this.targetClickTargetProfileName = targetClickTargetProfileName;
            this.targetVariable = targetVariable;
            this.arrayListSyncVariableTriggers = arrayListSyncVariableTriggers;
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
