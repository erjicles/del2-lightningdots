package com.delsquared.lightningdots.game;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Pair;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.ntuple.NTuple;
import com.delsquared.lightningdots.utilities.Polygon;
import com.delsquared.lightningdots.utilities.PolygonHelper;
import com.delsquared.lightningdots.utilities.PositionEvolver;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

public class LevelDefinitionLadderHelper {

    private static final String NODE_NAME_LEVELS = "Levels";
    private static final String NODE_NAME_LEVEL = "Level";
    private static final String NODE_NAME_CLICK_TARGETS = "ClickTargets";
    private static final String NODE_NAME_CLICK_TARGET = "ClickTarget";
    private static final String NODE_NAME_CLICK_TARGET_PROFILE_SCRIPT = "ClickTargetProfileScript";
    private static final String NODE_NAME_CLICK_TARGET_PROFILE = "ClickTargetProfile";
    private static final String NODE_NAME_VARIABLES = "Variables";
    private static final String NODE_NAME_VARIABLE = "Variable";
    private static final String NODE_NAME_RANDOM_CHANGE_EFFECT = "RandomChangeEffect";
    private static final String NODE_NAME_BOUNDARY_EFFECT = "BoundaryEffect";
    private static final String NODE_NAME_TRANSITION_TRIGGERS = "TransitionTriggers";
    private static final String NODE_NAME_TRANSITION_TRIGGER = "TransitionTrigger";
    private static final String NODE_NAME_RANDOM_CHANGE_TRIGGERS = "RandomChangeTriggers";
    private static final String NODE_NAME_RANDOM_CHANGE_TRIGGER = "RandomChangeTrigger";
    private static final String NODE_NAME_SYNC_VARIABLE_TRIGGERS = "SyncVariableTriggers";
    private static final String NODE_NAME_SYNC_VARIABLE_TRIGGER = "SyncVariableTrigger";
    private static final String NODE_NAME_CLICK_TARGET_SETTINGS_SHUFFLES = "ClickTargetSettingsShuffles";
    private static final String NODE_NAME_CLICK_TARGET_SETTINGS_SHUFFLE = "ClickTargetSettingsShuffle";
    private static final String NODE_NAME_CLICK_TARGET_SETTINGS_SHUFFLE_SETTINGS = "ClickTargetSettingsShuffleSettings";
    private static final String NODE_NAME_CLICK_TARGET_SETTINGS_SHUFFLE_SETTING = "ClickTargetSettingsShuffleSetting";


    // <Level> attribute names
    private static final String ATTRIBUTE_NAME_LEVEL_LEVEL = "level";

    // <ClickTarget> attribute names
    private static final String ATTRIBUTE_NAME_CLICKTARGET_NAME = "name";

    // <ClickTargetProfileScript> attribute names
    private static final String ATTRIBUTE_NAME_SCRIPT_TRANSITION_MODE = "scriptTransitionMode";
    private static final String ATTRIBUTE_NAME_SCRIPT_TRANSITION_INTERVAL = "scriptTransitionInterval";
    private static final String ATTRIBUTE_NAME_SCRIPT_CYCLE_DIRECTION = "scriptCycleDirection";
    private static final String ATTRIBUTE_NAME_SCRIPT_INITIAL_CLICK_TARGET_PROFILE = "initialClickTargetProfile";
    private static final String ATTRIBUTE_NAME_SCRIPT_RANDOM_INITIAL_CLICK_TARGET_PROFILE = "randomInitialClickTargetProfile";

    // <ClickTargetProfile> attribute names
    private static final String ATTRIBUTE_NAME_PROFILE_NAME = "name";
    private static final String ATTRIBUTE_NAME_PROFILE_SCRIPT_TRANSITION_VALUE = "scriptTransitionValue";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_SHAPE = "targetShape";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_IS_CLICKABLE = "targetIsClickable";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_VISIBILITY = "targetVisibility";

    // <Variable> attribute names
    private static final String ATTRIBUTE_NAME_VARIABLE_NAME = "name";
    private static final String ATTRIBUTE_NAME_VARIABLE_MINIMUM_VALUE = "minimumValue";
    private static final String ATTRIBUTE_NAME_VARIABLE_INITIAL_VALUE = "initialValue";
    private static final String ATTRIBUTE_NAME_VARIABLE_MAXIMUM_VALUE = "maximumValue";
    private static final String ATTRIBUTE_NAME_VARIABLE_RANDOM_INITIAL_VALUE = "randomInitialValue";
    private static final String ATTRIBUTE_NAME_VARIABLE_RANDOM_INITIAL_SIGN = "randomInitialSign";
    private static final String ATTRIBUTE_NAME_VARIABLE_CAN_CHANGE = "canChange";
    private static final String ATTRIBUTE_NAME_VARIABLE_TRANSITION_CONTINUITY = "transitionContinuity";

    // <RandomChangeEffect> attribute names
    private static final String ATTRIBUTE_NAME_RANDOMCHANGEEFFECT_CAN_RANDOMLY_CHANGE = "canRandomlyChange";
    private static final String ATTRIBUTE_NAME_RANDOMCHANGEEFFECT_RANDOM_CHANGE_VALUE = "randomChangeValue";
    private static final String ATTRIBUTE_NAME_RANDOMCHANGEEFFECT_RANDOM_CHANGE_INTERVAL = "randomChangeInterval";
    private static final String ATTRIBUTE_NAME_RANDOMCHANGEEFFECT_BOUNCE_ON_RANDOM_CHANGE = "bounceOnRandomChange";

    // <BoundaryEffect> attribute names
    private static final String ATTRIBUTE_NAME_BOUNDARYEFFECT_BOUNDARY_EFFECT = "boundaryEffect";
    private static final String ATTRIBUTE_NAME_BOUNDARYEFFECT_MIRROR_ABSOLUTE_VALUE_BOUNDARIES = "mirrorAbsoluteValueBoundaries";
    private static final String ATTRIBUTE_NAME_BOUNDARYEFFECT_BOUNCE_ON_INTERNAL_BOUNDARY = "bounceOnInternalBoundary";

    // <RandomChangeTrigger> attribute names
    private static final String ATTRIBUTE_NAME_RANDOMCHANGETRIGGER_SOURCE_CLICK_TARGET_NAME = "sourceClickTargetName";
    private static final String ATTRIBUTE_NAME_RANDOMCHANGETRIGGER_SOURCE_CLICK_TARGET_PROFILE_NAME = "sourceClickTargetProfileName";
    private static final String ATTRIBUTE_NAME_RANDOMCHANGETRIGGER_SOURCE_VARIABLE = "sourceVariable";
    private static final String ATTRIBUTE_NAME_RANDOMCHANGETRIGGER_TARGET_CLICK_TARGET_NAME = "targetClickTargetName";
    private static final String ATTRIBUTE_NAME_RANDOMCHANGETRIGGER_TARGET_CLICK_TARGET_PROFILE_NAME = "targetClickTargetProfileName";
    private static final String ATTRIBUTE_NAME_RANDOMCHANGETRIGGER_TARGET_VARIABLE = "targetVariable";

    // <TransitionTrigger> attribute names
    private static final String ATTRIBUTE_NAME_TRANSITIONTRIGGER_SOURCE_CLICK_TARGET_NAME = "sourceClickTargetName";
    private static final String ATTRIBUTE_NAME_TRANSITIONTRIGGER_SOURCE_CLICK_TARGET_PROFILE_NAME = "sourceClickTargetProfileName";
    private static final String ATTRIBUTE_NAME_TRANSITIONTRIGGER_TARGET_CLICK_TARGET_NAME = "targetClickTargetName";
    private static final String ATTRIBUTE_NAME_TRANSITIONTRIGGER_TARGET_CLICK_TARGET_PROFILE_NAME = "targetClickTargetProfileName";
    private static final String ATTRIBUTE_NAME_TRANSITIONTRIGGER_RANDOM_TARGET_CLICK_TARGET = "randomTargetClickTarget";
    private static final String ATTRIBUTE_NAME_TRANSITIONTRIGGER_RANDOM_TARGET_CLICK_TARGET_PROFILE = "randomTargetClickTargetProfile";

    // <SyncVariableTrigger> attribute names
    private static final String ATTRIBUTE_NAME_SYNCVARIABLE_SOURCE_CLICK_TARGET_NAME = "sourceClickTargetName";
    private static final String ATTRIBUTE_NAME_SYNCVARIABLE_SOURCE_CLICK_TARGET_PROFILE_NAME = "sourceClickTargetProfileName";
    private static final String ATTRIBUTE_NAME_SYNCVARIABLE_TARGET_CLICK_TARGET_NAME = "targetClickTargetName";
    private static final String ATTRIBUTE_NAME_SYNCVARIABLE_TARGET_CLICK_TARGET_PROFILE_NAME = "targetClickTargetProfileName";
    private static final String ATTRIBUTE_NAME_SYNCVARIABLE_VARIABLE_NAME = "variableName";
    private static final String ATTRIBUTE_NAME_SYNCVARIABLE_MODE = "mode";
    private static final String ATTRIBUTE_NAME_SYNCVARIABLE_VALUE = "value";

    // <ClickTargetSettingsShuffle> attribute names
    private static final String ATTRIBUTE_NAME_CLICKTARGETSETTINGSSHUFFLE_PRESERVE_ORDER = "preserveOrder";

    // <ClickTargetSettingsShuffleSetting> attribute names
    private static final String ATTRIBUTE_NAME_CLICKTARGETSETTINGSSHUFFLESETTING_NAME = "name";


    // Variable names
    private static final ArrayList<String> arrayListVariableNames = new ArrayList<String>() {{
        add("position_horizontal");
        add("position_vertical");
        add("speed");
        add("direction");
        add("dSpeed");
        add("dDirection");
        add("radius");
        add("dRadius");
        add("d2Radius");
        add("rotation");
        add("dRotation");
        add("d2Rotation");
    }};


    public static int getHighestScriptedLevel(Context context) {

        int returnValue = 0;

        try {

            // Get a XmlResourceParser object to read contents of the xml file
            XmlResourceParser xmlResourceParser =
                    context.getResources().getXml(R.xml.level_definitions_ladder);

            // Get the initial parser event
            int currentXmlEvent = xmlResourceParser.getEventType();

            //while haven't reached the end of the XML file
            while (currentXmlEvent != XmlPullParser.END_DOCUMENT)
            {

                // Check if we found the start of a tag
                if (currentXmlEvent == XmlPullParser.START_TAG) {

                    // Check if we found the start of a click target profile script
                    if (xmlResourceParser.getName().contentEquals(NODE_NAME_LEVEL)) {

                        // Get the level for this script
                        int currentLevel = xmlResourceParser.getAttributeIntValue(null, ATTRIBUTE_NAME_LEVEL_LEVEL, 0);

                        if (currentLevel > returnValue) {
                            returnValue = currentLevel;
                        }

                    }

                }

                currentXmlEvent = xmlResourceParser.next();
            }

        } catch (XmlPullParserException|IOException e) {
            int blah = 0;
        }

        return returnValue;

    }

    public static class XMLClickTargetSettingsShuffle {
        public List<String> listXMLClickTargetSettingsShuffleSettingNames;
        public boolean preserveOrder;

        public XMLClickTargetSettingsShuffle(Context context) {
            listXMLClickTargetSettingsShuffleSettingNames = new ArrayList<>();
            preserveOrder = context.getResources().getBoolean(R.bool.game_values_defaultClickTargetSettingsShufflePreserveOrder);
        }

        public LevelDefinitionLadder.ClickTargetSettingsShuffle toClickTargetSettingsShuffle() {

            return new LevelDefinitionLadder.ClickTargetSettingsShuffle(
                    listXMLClickTargetSettingsShuffleSettingNames
                    , preserveOrder
            );

        }
    }

    public static class XMLSyncVariableTrigger {

        public final String sourceClickTargetName;
        public final String sourceClickTargetProfileName;
        public final String targetClickTargetName;
        public final String targetClickTargetProfileName;
        public final String variableName;
        public final LevelDefinitionLadder.SyncVariableTrigger.MODE mode;
        public final double value;

        public XMLSyncVariableTrigger(
                String sourceClickTargetName
                , String sourceClickTargetProfileName
                , String targetClickTargetName
                , String targetClickTargetProfileName
                , String variableName
                , LevelDefinitionLadder.SyncVariableTrigger.MODE mode
                , double value) {
            this.sourceClickTargetName = sourceClickTargetName;
            this.sourceClickTargetProfileName = sourceClickTargetProfileName;
            this.targetClickTargetName = targetClickTargetName;
            this.targetClickTargetProfileName = targetClickTargetProfileName;
            this.variableName = variableName;
            this.mode = mode;
            this.value = value;
        }

        public LevelDefinitionLadder.SyncVariableTrigger toSyncVariableTrigger() {
            return new LevelDefinitionLadder.SyncVariableTrigger(
                    sourceClickTargetName
                    , sourceClickTargetProfileName
                    , targetClickTargetName
                    , targetClickTargetProfileName
                    , variableName
                    , mode
                    , value
            );
        }

    }

    public static class XMLTransitionTrigger {

        public final String sourceClickTargetName;
        public final String sourceClickTargetProfileName;
        public final String targetClickTargetName;
        public final String targetClickTargetProfileName;
        public final boolean randomTargetClickTarget;
        public final boolean randomTargetClickTargetProfile;
        public ArrayList<XMLSyncVariableTrigger> arrayListXMLSyncVariableTriggers;

        public XMLTransitionTrigger(
                String sourceClickTargetName
                , String sourceClickTargetProfileName
                , String targetClickTargetName
                , String targetClickTargetProfileName
                , boolean randomTargetClickTarget
                , boolean randomTargetClickTargetProfile) {
            this.sourceClickTargetName = sourceClickTargetName;
            this.sourceClickTargetProfileName = sourceClickTargetProfileName;
            this.targetClickTargetName = targetClickTargetName;
            this.targetClickTargetProfileName = targetClickTargetProfileName;
            this.randomTargetClickTarget = randomTargetClickTarget;
            this.randomTargetClickTargetProfile = randomTargetClickTargetProfile;
            this.arrayListXMLSyncVariableTriggers = new ArrayList<>();
        }

        public LevelDefinitionLadder.TransitionTrigger toTransitionTrigger() {

            // Convert the XMLSyncVariables to SyncVariables
            ArrayList<LevelDefinitionLadder.SyncVariableTrigger> arrayListSyncVariableTriggers = new ArrayList<>();
            for (XMLSyncVariableTrigger xmlSyncVariableTrigger : arrayListXMLSyncVariableTriggers) {
                LevelDefinitionLadder.SyncVariableTrigger syncVariableTrigger = xmlSyncVariableTrigger.toSyncVariableTrigger();
                arrayListSyncVariableTriggers.add(syncVariableTrigger);
            }

            return new LevelDefinitionLadder.TransitionTrigger(
                    sourceClickTargetName
                    , sourceClickTargetProfileName
                    , targetClickTargetName
                    , targetClickTargetProfileName
                    , randomTargetClickTarget
                    , randomTargetClickTargetProfile
                    , arrayListSyncVariableTriggers
            );
        }
    }

    public static class XMLRandomChangeTrigger {

        public final String sourceClickTargetName;
        public final String sourceClickTargetProfileName;
        public final String sourceVariable;
        public final String targetClickTargetName;
        public final String targetClickTargetProfileName;
        public final String targetVariable;
        public ArrayList<XMLSyncVariableTrigger> arrayListXMLSyncVariableTriggers;

        public XMLRandomChangeTrigger(
                String sourceClickTargetName
                , String sourceClickTargetProfileName
                , String sourceVariable
                , String targetClickTargetName
                , String targetClickTargetProfileName
                , String targetVariable) {
            this.sourceClickTargetName = sourceClickTargetName;
            this.sourceClickTargetProfileName = sourceClickTargetProfileName;
            this.sourceVariable = sourceVariable;
            this.targetClickTargetName = targetClickTargetName;
            this.targetClickTargetProfileName = targetClickTargetProfileName;
            this.targetVariable = targetVariable;
            this.arrayListXMLSyncVariableTriggers = new ArrayList<>();
        }

        public LevelDefinitionLadder.RandomChangeTrigger toRandomChangeTrigger() {

            // Convert the XMLSyncVariables to SyncVariables
            ArrayList<LevelDefinitionLadder.SyncVariableTrigger> arrayListSyncVariableTriggers = new ArrayList<>();
            for (XMLSyncVariableTrigger xmlSyncVariableTrigger : arrayListXMLSyncVariableTriggers) {
                LevelDefinitionLadder.SyncVariableTrigger syncVariableTrigger = xmlSyncVariableTrigger.toSyncVariableTrigger();
                arrayListSyncVariableTriggers.add(syncVariableTrigger);
            }

            return new LevelDefinitionLadder.RandomChangeTrigger(
                    sourceClickTargetName
                    , sourceClickTargetProfileName
                    , sourceVariable
                    , targetClickTargetName
                    , targetClickTargetProfileName
                    , targetVariable
                    , arrayListSyncVariableTriggers
            );
        }

    }

    public static class XMLRandomChangeEffect {

        public boolean canRandomlyChange;
        public double randomChangeValue;
        public PositionEvolver.RandomChangeEffect.RANDOM_CHANGE_INTERVAL randomChangeInterval;
        public boolean bounceOnRandomChange;

        public XMLRandomChangeEffect() {
            this.canRandomlyChange = false;
            this.randomChangeValue = 0.0;
            this.randomChangeInterval = PositionEvolver.RandomChangeEffect.RANDOM_CHANGE_INTERVAL.CONSTANT;
            this.bounceOnRandomChange = false;
        }

        public PositionEvolver.RandomChangeEffect toRandomChangeEffect() {
            return new PositionEvolver.RandomChangeEffect(
                    canRandomlyChange
                    , randomChangeValue
                    , randomChangeInterval
                    , bounceOnRandomChange
            );
        }

    }

    public static class XMLBoundaryEffect {

        public PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT boundaryEffect;
        public boolean mirrorAbsoluteValueBoundaries;
        public boolean bounceOnInternalBoundary;

        public XMLBoundaryEffect() {
            boundaryEffect = PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.STICK;
            mirrorAbsoluteValueBoundaries = false;
            bounceOnInternalBoundary = false;
        }

        public PositionEvolver.BoundaryEffect toBoundaryEffect() {
            return new PositionEvolver.BoundaryEffect(
                    boundaryEffect
                    , mirrorAbsoluteValueBoundaries
                    , bounceOnInternalBoundary
            );
        }

    }

    public static class XMLVariable {

        public String name;
        public double defaultInitialValue;
        public double minimumValue;
        public double initialValue;
        public double maximumValue;
        public boolean usesInitialValueMultipliers;
        public boolean randomInitialValue;
        public boolean randomInitialSign;
        public boolean canChange;
        public ClickTargetProfile.TRANSITION_CONTINUITY transitionContinuity;

        public XMLRandomChangeEffect xmlRandomChangeEffect;
        public XMLBoundaryEffect xmlBoundaryEffect;

        public XMLVariable() {
            this.name = "";
            this.defaultInitialValue = 0.0;
            this.minimumValue = Double.NEGATIVE_INFINITY;
            this.initialValue = 0.0;
            this.maximumValue = Double.POSITIVE_INFINITY;
            this.usesInitialValueMultipliers = false;
            this.randomInitialValue = false;
            this.randomInitialSign = false;
            this.canChange = false;
            this.transitionContinuity = ClickTargetProfile.TRANSITION_CONTINUITY.DEFAULT;

            this.xmlRandomChangeEffect = new XMLRandomChangeEffect();
            this.xmlBoundaryEffect = new XMLBoundaryEffect();
        }

        public ClickTargetProfile.ProfileVariableValues toProfileVariableValues() {

            PositionEvolver.RandomChangeEffect randomChangeEffect = xmlRandomChangeEffect.toRandomChangeEffect();
            PositionEvolver.BoundaryEffect boundaryEffect = xmlBoundaryEffect.toBoundaryEffect();

            double minimumValue = this.minimumValue;
            double initialValue = this.initialValue;
            double maximumValue = this.maximumValue;

            // Check if this variable uses initial value multipliers
            if (usesInitialValueMultipliers) {

                minimumValue *= defaultInitialValue;
                initialValue *= defaultInitialValue;
                maximumValue *= defaultInitialValue;

            }

            return new ClickTargetProfile.ProfileVariableValues(
                    name
                    , minimumValue
                    , initialValue
                    , maximumValue
                    , usesInitialValueMultipliers
                    , randomInitialValue
                    , randomInitialSign
                    , canChange
                    , randomChangeEffect
                    , boundaryEffect
                    , transitionContinuity
            );

        }

    }

   public static class XMLClickTargetProfile {

       public String name;
       public double scriptTransitionValue;
       public String shape;
       public boolean isClickable;
       public ClickTarget.VISIBILITY visibility;

       public HashMap<String, XMLVariable> mapXMLVariables;

       public XMLClickTargetProfile(Context context) {
           name = "";
           scriptTransitionValue =
                   UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultScriptTransitionValue);

           shape = PolygonHelper.CLICK_TARGET_SHAPES.get(0);// Target shape
           isClickable = context.getResources().getBoolean(R.bool.game_values_defaultTargetIsClickable);
           visibility = ClickTarget.VISIBILITY.valueOf(context.getString(R.string.game_values_defaultTargetVisibility));

           mapXMLVariables = new HashMap<>();

           initializeVariables(context);
       }

       public void initializeVariables(Context context) {

           for (String currentVariableName : arrayListVariableNames) {

               // Initialize the new variable
               XMLVariable variable = new XMLVariable();
               variable.name = currentVariableName;

               // Transition continuity
               variable.transitionContinuity =
                       ClickTargetProfile.TRANSITION_CONTINUITY.valueOf(
                        context.getString(R.string.game_values_defaultScriptTransitionContinuity)
                       );

               if (currentVariableName.contentEquals("position_horizontal")) {

                   // Set the Variable values
                   variable.defaultInitialValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultInitialTargetPositionHorizontal);
                   variable.initialValue = variable.defaultInitialValue;
                   variable.minimumValue = 0.0;
                   variable.maximumValue = 0.0;
                   variable.randomInitialValue = context.getResources().getBoolean(R.bool.game_values_defaultRandomInitialTargetPosition);
                   variable.randomInitialSign = false;
                   variable.canChange = context.getResources().getBoolean(R.bool.game_values_defaultCanChangePosition);

                   // Set the RandomChangeEffect values
                   variable.xmlRandomChangeEffect.canRandomlyChange = context.getResources().getBoolean(R.bool.game_values_defaultCanRandomlyChangePosition);
                   variable.xmlRandomChangeEffect.randomChangeValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultProbabilityOfRandomPositionChangePerSecond);
                   variable.xmlRandomChangeEffect.randomChangeInterval =
                           PositionEvolver.RandomChangeEffect.RANDOM_CHANGE_INTERVAL.valueOf(
                                   context.getString(R.string.game_values_defaultRandomChangeIntervalPosition)
                           );

                   // Set the BoundaryEffect values
                   variable.xmlBoundaryEffect.boundaryEffect =
                           PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.valueOf(
                                   context.getString(R.string.game_values_defaultBoundaryEffectPositionHorizontal)
                           );

                   // Add the variable to the map
                   mapXMLVariables.put(currentVariableName, variable);

               }
               else if (currentVariableName.contentEquals("position_vertical")) {

                   // Set the Variable values
                   variable.defaultInitialValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultInitialTargetPositionVertical);
                   variable.initialValue = variable.defaultInitialValue;
                   variable.minimumValue = 0.0;
                   variable.maximumValue = 0.0;
                   variable.randomInitialValue = context.getResources().getBoolean(R.bool.game_values_defaultRandomInitialTargetPosition);
                   variable.randomInitialSign = false;
                   variable.canChange = context.getResources().getBoolean(R.bool.game_values_defaultCanChangePosition);

                   // Set the RandomChangeEffect values
                   variable.xmlRandomChangeEffect.canRandomlyChange = context.getResources().getBoolean(R.bool.game_values_defaultCanRandomlyChangePosition);
                   variable.xmlRandomChangeEffect.randomChangeValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultProbabilityOfRandomPositionChangePerSecond);
                   variable.xmlRandomChangeEffect.randomChangeInterval =
                           PositionEvolver.RandomChangeEffect.RANDOM_CHANGE_INTERVAL.valueOf(
                                   context.getString(R.string.game_values_defaultRandomChangeIntervalPosition)
                           );

                   // Set the BoundaryEffect values
                   variable.xmlBoundaryEffect.boundaryEffect =
                           PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.valueOf(
                                   context.getString(R.string.game_values_defaultBoundaryEffectPositionVertical)
                           );

                   // Add the variable to the map
                   mapXMLVariables.put(currentVariableName, variable);

               }
               else if (currentVariableName.contentEquals("speed")) {

                   // Set the Variable values
                   variable.defaultInitialValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultInitialTargetSpeedInchesPerSecond);
                   variable.initialValue = 1.0;
                   variable.minimumValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultMinimumTargetSpeedMultiplier);
                   variable.maximumValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultMaximumTargetSpeedMultiplier);
                   variable.usesInitialValueMultipliers = true;
                   variable.randomInitialValue = context.getResources().getBoolean(R.bool.game_values_defaultRandomInitialTargetSpeed);
                   variable.randomInitialSign = false;
                   variable.canChange = context.getResources().getBoolean(R.bool.game_values_defaultCanChangeSpeed);

                   // Set the RandomChangeEffect values
                   variable.xmlRandomChangeEffect.canRandomlyChange = context.getResources().getBoolean(R.bool.game_values_defaultCanRandomlyChangeSpeed);
                   variable.xmlRandomChangeEffect.randomChangeValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultProbabilityOfRandomSpeedChangePerSecond);
                   variable.xmlRandomChangeEffect.randomChangeInterval =
                           PositionEvolver.RandomChangeEffect.RANDOM_CHANGE_INTERVAL.valueOf(
                                   context.getString(R.string.game_values_defaultRandomChangeIntervalSpeed)
                           );

                   // Set the BoundaryEffect values
                   variable.xmlBoundaryEffect.boundaryEffect =
                           PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.valueOf(
                                   context.getString(R.string.game_values_defaultBoundaryEffectSpeed)
                           );

                   // Add the variable to the map
                   mapXMLVariables.put(currentVariableName, variable);

               }
               else if (currentVariableName.contentEquals("direction")) {

                   // Set the Variable values
                   variable.defaultInitialValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultInitialTargetDirectionAngleRadians);
                   variable.initialValue = variable.defaultInitialValue;
                   variable.minimumValue = 0.0;
                   variable.maximumValue = 2.0 * Math.PI;
                   variable.randomInitialValue = context.getResources().getBoolean(R.bool.game_values_defaultRandomInitialTargetDirectionAngle);
                   variable.randomInitialSign = false;
                   variable.canChange = context.getResources().getBoolean(R.bool.game_values_defaultCanChangeDirection);

                   // Set the RandomChangeEffect values
                   variable.xmlRandomChangeEffect.canRandomlyChange = context.getResources().getBoolean(R.bool.game_values_defaultCanRandomlyChangeDirection);
                   variable.xmlRandomChangeEffect.randomChangeValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultProbabilityOfRandomDirectionChangePerSecond);
                   variable.xmlRandomChangeEffect.randomChangeInterval =
                           PositionEvolver.RandomChangeEffect.RANDOM_CHANGE_INTERVAL.valueOf(
                                   context.getString(R.string.game_values_defaultRandomChangeIntervalDirection)
                           );

                   // Set the BoundaryEffect values
                   variable.xmlBoundaryEffect.boundaryEffect =
                           PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.valueOf(
                                   context.getString(R.string.game_values_defaultBoundaryEffectDirection)
                           );

                   // Add the variable to the map
                   mapXMLVariables.put(currentVariableName, variable);

               }
               else if (currentVariableName.contentEquals("dSpeed")) {

                   // Set the Variable values
                   variable.defaultInitialValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultInitialTargetSpeedChangeAbsoluteValueInchesPerSecond);
                   variable.initialValue = 1.0;
                   variable.minimumValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultMinimumTargetSpeedChangeAbsoluteValueMultiplier);
                   variable.maximumValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultMaximumTargetSpeedChangeAbsoluteValueMultiplier);
                   variable.usesInitialValueMultipliers = true;
                   variable.randomInitialValue = context.getResources().getBoolean(R.bool.game_values_defaultRandomInitialTargetSpeedChange);
                   variable.randomInitialSign = context.getResources().getBoolean(R.bool.game_values_defaultRandomInitialTargetSpeedChangeSign);
                   variable.canChange = false;

                   // Set the RandomChangeEffect values
                   variable.xmlRandomChangeEffect.canRandomlyChange = context.getResources().getBoolean(R.bool.game_values_defaultCanRandomlyChangeDSpeed);
                   variable.xmlRandomChangeEffect.randomChangeValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultProbabilityOfRandomDSpeedChangePerSecond);
                   variable.xmlRandomChangeEffect.randomChangeInterval =
                           PositionEvolver.RandomChangeEffect.RANDOM_CHANGE_INTERVAL.valueOf(
                                   context.getString(R.string.game_values_defaultRandomChangeIntervalDSpeed)
                           );

                   // Set the BoundaryEffect values
                   variable.xmlBoundaryEffect.boundaryEffect =
                           PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.valueOf(
                                   context.getString(R.string.game_values_defaultBoundaryEffectDSpeed)
                           );
                   variable.xmlBoundaryEffect.mirrorAbsoluteValueBoundaries = true;

                   // Add the variable to the map
                   mapXMLVariables.put(currentVariableName, variable);

               }
               else if (currentVariableName.contentEquals("dDirection")) {

                   // Set the Variable values
                   variable.defaultInitialValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultInitialTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond);
                   variable.initialValue = 1.0;
                   variable.minimumValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultMinimumTargetDirectionAngleChangeAbsoluteValueMultiplier);
                   variable.maximumValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultMaximumTargetDirectionAngleChangeAbsoluteValueMultiplier);
                   variable.usesInitialValueMultipliers = true;
                   variable.randomInitialValue = context.getResources().getBoolean(R.bool.game_values_defaultRandomInitialTargetDirectionAngleChange);
                   variable.randomInitialSign = context.getResources().getBoolean(R.bool.game_values_defaultRandomInitialTargetDirectionAngleChangeSign);
                   variable.canChange = false;

                   // Set the RandomChangeEffect values
                   variable.xmlRandomChangeEffect.canRandomlyChange = context.getResources().getBoolean(R.bool.game_values_defaultCanRandomlyChangeDDirection);
                   variable.xmlRandomChangeEffect.randomChangeValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultProbabilityOfRandomDDirectionChangePerSecond);
                   variable.xmlRandomChangeEffect.randomChangeInterval =
                           PositionEvolver.RandomChangeEffect.RANDOM_CHANGE_INTERVAL.valueOf(
                                   context.getString(R.string.game_values_defaultRandomChangeIntervalDDirection)
                           );

                   // Set the BoundaryEffect values
                   variable.xmlBoundaryEffect.boundaryEffect =
                           PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.valueOf(
                                   context.getString(R.string.game_values_defaultBoundaryEffectDDirection)
                           );
                   variable.xmlBoundaryEffect.mirrorAbsoluteValueBoundaries = true;

                   // Add the variable to the map
                   mapXMLVariables.put(currentVariableName, variable);

               }
               else if (currentVariableName.contentEquals("radius")) {

                   // Set the Variable values
                   variable.defaultInitialValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultInitialTargetRadiusInches);
                   variable.initialValue = 1.0;
                   variable.minimumValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultMinimumTargetRadiusInchesMultiplier);
                   variable.maximumValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultMaximumTargetRadiusInchesMultiplier);
                   variable.usesInitialValueMultipliers = true;
                   variable.randomInitialValue = context.getResources().getBoolean(R.bool.game_values_defaultRandomInitialTargetRadius);
                   variable.randomInitialSign = false;
                   variable.canChange = context.getResources().getBoolean(R.bool.game_values_defaultCanChangeRadius);

                   // Set the RandomChangeEffect values
                   variable.xmlRandomChangeEffect.canRandomlyChange = context.getResources().getBoolean(R.bool.game_values_defaultCanRandomlyChangeRadius);
                   variable.xmlRandomChangeEffect.randomChangeValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultProbabilityOfRandomRadiusChangePerSecond);
                   variable.xmlRandomChangeEffect.randomChangeInterval =
                           PositionEvolver.RandomChangeEffect.RANDOM_CHANGE_INTERVAL.valueOf(
                                   context.getString(R.string.game_values_defaultRandomChangeIntervalRadius)
                           );

                   // Set the BoundaryEffect values
                   variable.xmlBoundaryEffect.boundaryEffect =
                           PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.valueOf(
                                   context.getString(R.string.game_values_defaultBoundaryEffectRadius)
                           );

                   // Add the variable to the map
                   mapXMLVariables.put(currentVariableName, variable);

               }
               else if (currentVariableName.contentEquals("dRadius")) {

                   // Set the Variable values
                   variable.defaultInitialValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultInitialTargetDRadiusAbsoluteValueInchesPerSecond);
                   variable.initialValue = 1.0;
                   variable.minimumValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultMinimumTargetDRadiusAbsoluteValueMultiplier);
                   variable.maximumValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultMaximumTargetDRadiusAbsoluteValueMultiplier);
                   variable.usesInitialValueMultipliers = true;
                   variable.randomInitialValue = context.getResources().getBoolean(R.bool.game_values_defaultRandomInitialTargetDRadius);
                   variable.randomInitialSign = context.getResources().getBoolean(R.bool.game_values_defaultRandomInitialTargetDRadiusSign);
                   variable.canChange = context.getResources().getBoolean(R.bool.game_values_defaultCanChangeDRadius);

                   // Set the RandomChangeEffect values
                   variable.xmlRandomChangeEffect.canRandomlyChange = context.getResources().getBoolean(R.bool.game_values_defaultCanRandomlyChangeDRadius);
                   variable.xmlRandomChangeEffect.randomChangeValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultProbabilityOfRandomDRadiusChangePerSecond);
                   variable.xmlRandomChangeEffect.randomChangeInterval =
                           PositionEvolver.RandomChangeEffect.RANDOM_CHANGE_INTERVAL.valueOf(
                                   context.getString(R.string.game_values_defaultRandomChangeIntervalDRadius)
                           );

                   // Set the BoundaryEffect values
                   variable.xmlBoundaryEffect.boundaryEffect =
                           PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.valueOf(
                                   context.getString(R.string.game_values_defaultBoundaryEffectDRadius)
                           );
                   variable.xmlBoundaryEffect.mirrorAbsoluteValueBoundaries = true;

                   // Add the variable to the map
                   mapXMLVariables.put(currentVariableName, variable);

               }
               else if (currentVariableName.contentEquals("d2Radius")) {

                   // Set the Variable values
                   variable.defaultInitialValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultInitialTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond);
                   variable.initialValue = 1.0;
                   variable.minimumValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultMinimumTargetDRadiusChangeAbsoluteValueMultiplier);
                   variable.maximumValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultMaximumTargetDRadiusChangeAbsoluteValueMultiplier);
                   variable.usesInitialValueMultipliers = true;
                   variable.randomInitialValue = context.getResources().getBoolean(R.bool.game_values_defaultRandomInitialTargetDRadiusChange);
                   variable.randomInitialSign = context.getResources().getBoolean(R.bool.game_values_defaultRandomInitialTargetDRadiusChangeSign);
                   variable.canChange = false;

                   // Set the RandomChangeEffect values
                   variable.xmlRandomChangeEffect.canRandomlyChange = context.getResources().getBoolean(R.bool.game_values_defaultCanRandomlyChangeD2Radius);
                   variable.xmlRandomChangeEffect.randomChangeValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultProbabilityOfRandomD2RadiusChangePerSecond);
                   variable.xmlRandomChangeEffect.randomChangeInterval =
                           PositionEvolver.RandomChangeEffect.RANDOM_CHANGE_INTERVAL.valueOf(
                                   context.getString(R.string.game_values_defaultRandomChangeIntervalD2Radius)
                           );

                   // Set the BoundaryEffect values
                   variable.xmlBoundaryEffect.boundaryEffect =
                           PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.valueOf(
                                   context.getString(R.string.game_values_defaultBoundaryEffectD2Radius)
                           );
                   variable.xmlBoundaryEffect.mirrorAbsoluteValueBoundaries = true;

                   // Add the variable to the map
                   mapXMLVariables.put(currentVariableName, variable);

               }
               else if (currentVariableName.contentEquals("rotation")) {

                   // Set the Variable values
                   variable.defaultInitialValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultInitialTargetRotationAngleRadians);
                   variable.initialValue = variable.defaultInitialValue;
                   variable.minimumValue = 0.0;
                   variable.maximumValue = 2.0 * Math.PI;
                   variable.randomInitialValue = context.getResources().getBoolean(R.bool.game_values_defaultRandomInitialTargetRotationAngle);
                   variable.randomInitialSign = false;
                   variable.canChange = context.getResources().getBoolean(R.bool.game_values_defaultCanChangeRotation);

                   // Set the RandomChangeEffect values
                   variable.xmlRandomChangeEffect.canRandomlyChange = context.getResources().getBoolean(R.bool.game_values_defaultCanRandomlyChangeRotation);
                   variable.xmlRandomChangeEffect.randomChangeValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultProbabilityOfRandomRotationChangePerSecond);
                   variable.xmlRandomChangeEffect.randomChangeInterval =
                           PositionEvolver.RandomChangeEffect.RANDOM_CHANGE_INTERVAL.valueOf(
                                   context.getString(R.string.game_values_defaultRandomChangeIntervalRotation)
                           );

                   // Set the BoundaryEffect values
                   variable.xmlBoundaryEffect.boundaryEffect =
                           PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.valueOf(
                                   context.getString(R.string.game_values_defaultBoundaryEffectRotation)
                           );

                   // Add the variable to the map
                   mapXMLVariables.put(currentVariableName, variable);

               }
               else if (currentVariableName.contentEquals("dRotation")) {

                   // Set the Variable values
                   variable.defaultInitialValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultInitialTargetDRotationAbsoluteValueRadiansPerSecond);
                   variable.initialValue = 1.0;
                   variable.minimumValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultMinimumTargetDRotationAbsoluteValueMultiplier);
                   variable.maximumValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultMaximumTargetDRotationAbsoluteValueMultiplier);
                   variable.usesInitialValueMultipliers = true;
                   variable.randomInitialValue = context.getResources().getBoolean(R.bool.game_values_defaultRandomInitialTargetDRotation);
                   variable.randomInitialSign = context.getResources().getBoolean(R.bool.game_values_defaultRandomInitialTargetDRotationSign);
                   variable.canChange = context.getResources().getBoolean(R.bool.game_values_defaultCanChangeDRotation);

                   // Set the RandomChangeEffect values
                   variable.xmlRandomChangeEffect.canRandomlyChange = context.getResources().getBoolean(R.bool.game_values_defaultCanRandomlyChangeDRotation);
                   variable.xmlRandomChangeEffect.randomChangeValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultProbabilityOfRandomDRotationChangePerSecond);
                   variable.xmlRandomChangeEffect.randomChangeInterval =
                           PositionEvolver.RandomChangeEffect.RANDOM_CHANGE_INTERVAL.valueOf(
                                   context.getString(R.string.game_values_defaultRandomChangeIntervalDRotation)
                           );

                   // Set the BoundaryEffect values
                   variable.xmlBoundaryEffect.boundaryEffect =
                           PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.valueOf(
                                   context.getString(R.string.game_values_defaultBoundaryEffectDRotation)
                           );
                   variable.xmlBoundaryEffect.mirrorAbsoluteValueBoundaries = true;

                   // Add the variable to the map
                   mapXMLVariables.put(currentVariableName, variable);

               }
               else if (currentVariableName.contentEquals("d2Rotation")) {

                   // Set the Variable values
                   variable.defaultInitialValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultInitialTargetD2RotationAbsoluteValueRadiansPerSecondPerSecond);
                   variable.initialValue = 1.0;
                   variable.minimumValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultMinimumTargetD2RotationAbsoluteValueMultiplier);
                   variable.maximumValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultMaximumTargetD2RotationAbsoluteValueMultiplier);
                   variable.usesInitialValueMultipliers = true;
                   variable.randomInitialValue = context.getResources().getBoolean(R.bool.game_values_defaultRandomInitialTargetD2Rotation);
                   variable.randomInitialSign = context.getResources().getBoolean(R.bool.game_values_defaultRandomInitialTargetD2RotationSign);
                   variable.canChange = false;

                   // Set the RandomChangeEffect values
                   variable.xmlRandomChangeEffect.canRandomlyChange = context.getResources().getBoolean(R.bool.game_values_defaultCanRandomlyChangeD2Rotation);
                   variable.xmlRandomChangeEffect.randomChangeValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultProbabilityOfRandomD2RotationChangePerSecond);
                   variable.xmlRandomChangeEffect.randomChangeInterval =
                           PositionEvolver.RandomChangeEffect.RANDOM_CHANGE_INTERVAL.valueOf(
                                   context.getString(R.string.game_values_defaultRandomChangeIntervalD2Rotation)
                           );

                   // Set the BoundaryEffect values
                   variable.xmlBoundaryEffect.boundaryEffect =
                           PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.valueOf(
                                   context.getString(R.string.game_values_defaultBoundaryEffectD2Rotation)
                           );
                   variable.xmlBoundaryEffect.mirrorAbsoluteValueBoundaries = true;

                   // Add the variable to the map
                   mapXMLVariables.put(currentVariableName, variable);

               }

           }

       }

       public ClickTargetProfile toClickTargetProfile() {

           // Convert the XMLVariables to ProfileVariableValues objects
           HashMap<String, ClickTargetProfile.ProfileVariableValues> mapProfileVariableValues = new HashMap<>();

           // Loop through the XMLVariables
           Iterator XMLVariableIterator = mapXMLVariables.entrySet().iterator();
           while (XMLVariableIterator.hasNext()) {

               // Get the current XMLVariable pair
               Map.Entry<String, XMLVariable> currentXMLVariablePair = (Map.Entry) XMLVariableIterator.next();
               String currentVariableName = currentXMLVariablePair.getKey();
               XMLVariable currentXMLVariable = currentXMLVariablePair.getValue();

               // Convert the XMLVariable to a ProfileVariableValues object
               ClickTargetProfile.ProfileVariableValues currentProfileVariableValues = currentXMLVariable.toProfileVariableValues();

               // Add the ProfileVariableValues object to the map
               mapProfileVariableValues.put(currentVariableName, currentProfileVariableValues);

           }


           // Create the ClickTargetProfile object
           ClickTargetProfile clickTargetProfile = new ClickTargetProfile(
                   this.name
                   , this.scriptTransitionValue
                   , this.shape
                   , this.isClickable
                   , this.visibility
                   , mapProfileVariableValues
           );

           return clickTargetProfile;

       }

   }

    public static class XMLClickTargetProfileScript {

        public ClickTargetProfileScript.SCRIPT_TRANSITION_MODE scriptTransitionMode;
        public ClickTargetProfileScript.SCRIPT_TRANSITION_INTERVAL scriptTransitionInterval;
        public ClickTargetProfileScript.SCRIPT_CYCLE_DIRECTION scriptCycleDirection;
        public String initialClickTargetProfileName;
        public boolean randomInitialClickTargetProfile;

        public List<String> listClickTargetProfileNames;
        public Map<String, XMLClickTargetProfile> mapXMLClickTargetProfiles;

        public XMLClickTargetProfileScript(Context context) {

            scriptTransitionMode =
                    ClickTargetProfileScript.SCRIPT_TRANSITION_MODE.valueOf(
                        context.getString(R.string.game_values_defaultScriptTransitionMode)
                    );
            scriptTransitionInterval =
                    ClickTargetProfileScript.SCRIPT_TRANSITION_INTERVAL.valueOf(
                        context.getString(R.string.game_values_defaultScriptTransitionInterval)
                    );
            scriptCycleDirection =
                    ClickTargetProfileScript.SCRIPT_CYCLE_DIRECTION.valueOf(
                        context.getString(R.string.game_values_defaultScriptCycleDirection)
                    );
            initialClickTargetProfileName = "";
            randomInitialClickTargetProfile = context.getResources().getBoolean(R.bool.game_values_defaultRandomInitialClickTargetProfile);

            listClickTargetProfileNames = new ArrayList<>();
            mapXMLClickTargetProfiles = new HashMap<>();

        }

        private void validateInitialClickTargetProfileName() {

            // Check if the map of click target profiles does not contain the initial name
            if (!mapXMLClickTargetProfiles.containsKey(initialClickTargetProfileName)) {

                // Reinitialize the initial name to blank
                initialClickTargetProfileName = "";

                // Check if there are names
                if (listClickTargetProfileNames.size() > 0) {

                    // Set the initial profile to the first name
                    initialClickTargetProfileName = listClickTargetProfileNames.get(0);

                }

                // Check if the map of click target profiles still does not contain the initial name
                if (!mapXMLClickTargetProfiles.containsKey(initialClickTargetProfileName)) {

                    // Reinitialize the initial name to blank
                    initialClickTargetProfileName = "";

                    // Loop through the click target profiles
                    Iterator xmlClickTargetProfilesIterator = mapXMLClickTargetProfiles.entrySet().iterator();
                    while (xmlClickTargetProfilesIterator.hasNext()) {

                        // Get the current XMLClickTargetProfile
                        Map.Entry<String, XMLClickTargetProfile> currentXMLClickTargetProfilePair = (Map.Entry) xmlClickTargetProfilesIterator.next();
                        String currentXMLClickTargetProfileName = currentXMLClickTargetProfilePair.getKey();

                        // Set the initial name to this click target profile's name
                        initialClickTargetProfileName = currentXMLClickTargetProfileName;

                        // Break out of the loop
                        break;
                    }

                }

            }
        }

        public ClickTargetProfileScript toClickTargetProfileScript() {

            // First validate the initial click target profile name
            validateInitialClickTargetProfileName();

            // Convert the XMLClickTargetProfiles to ClickTargetProfiles
            ArrayList<String> arrayListClickTargetProfileNames = new ArrayList<>();
            HashMap<String, ClickTargetProfile> mapClickTargetProfiles = new HashMap<>();

            // Loop through the XMLClickTargetProfiles
            Iterator xmlClickTargetProfilesIterator = this.mapXMLClickTargetProfiles.entrySet().iterator();
            while (xmlClickTargetProfilesIterator.hasNext()) {

                // Get the current XMLClickTargetProfile
                Map.Entry<String, XMLClickTargetProfile> currentXMLClickTargetProfilePair = (Map.Entry) xmlClickTargetProfilesIterator.next();
                String currentXMLClickTargetProfileName = currentXMLClickTargetProfilePair.getKey();
                XMLClickTargetProfile currentXMLClickTargetProfile = currentXMLClickTargetProfilePair.getValue();

                // Convert the XMLClickTargetProfile to a ClickTargetProfile
                ClickTargetProfile currentClickTargetProfile = currentXMLClickTargetProfile.toClickTargetProfile();

                // Add the ClickTargetProfile name to the name list
                arrayListClickTargetProfileNames.add(currentXMLClickTargetProfileName);

                // Add the ClickTargetProfile to the map
                mapClickTargetProfiles.put(currentXMLClickTargetProfileName, currentClickTargetProfile);

            }

            // Create the ClickTargetProfileScript object
            ClickTargetProfileScript clickTargetProfileScript = new ClickTargetProfileScript(
                    this.scriptTransitionMode
                    , this.scriptTransitionInterval
                    , this.scriptCycleDirection
                    , this.initialClickTargetProfileName
                    , this.randomInitialClickTargetProfile
                    , arrayListClickTargetProfileNames
                    , mapClickTargetProfiles
            );

            return clickTargetProfileScript;

        }
    }

    public static class XMLClickTarget {

        public String name;
        public XMLClickTargetProfileScript xmlClickTargetProfileScript;

        public XMLClickTarget(Context context) {
            this.name = "";
            this.xmlClickTargetProfileScript = new XMLClickTargetProfileScript(context);
        }

        public ClickTargetDefinition toClickTargetDefinition() {

            // Convert the XMLClickTargetProfileScript to ClickTargetProfileScript
            ClickTargetProfileScript clickTargetProfileScript = xmlClickTargetProfileScript.toClickTargetProfileScript();

            // Create the ClickTargetDefinition object
            ClickTargetDefinition clickTargetDefinition = new ClickTargetDefinition(
                    this.name
                    , clickTargetProfileScript
            );

            return clickTargetDefinition;

        }
    }

    public static class XMLLevel {

        public int level;

        public List<String> listXMLClickTargetNames;
        public Map<String, XMLClickTarget> mapXMLClickTargets;
        public List<XMLTransitionTrigger> listXMLTransitionTriggers;
        public List<XMLRandomChangeTrigger> listXMLRandomChangeTriggers;
        public List<XMLClickTargetSettingsShuffle> listXMLClickTargetSettingsShuffles;

        public XMLLevel() {
            this.level = 1;
            this.listXMLClickTargetNames = new ArrayList<>();
            this.mapXMLClickTargets = new HashMap<>();
            this.listXMLTransitionTriggers = new ArrayList<>();
            this.listXMLRandomChangeTriggers = new ArrayList<>();
            this.listXMLClickTargetSettingsShuffles = new ArrayList<>();
        }

        public LevelDefinitionLadder toLevelDefinitionLadder() {

            // Convert the XMLClickTargets to ClickTargetDefinition objects
            Map<String, ClickTargetDefinition> mapClickTargetDefinitions = new HashMap<>();
            for (String currentClickTargetName : listXMLClickTargetNames) {

                // Get the current XMLClickTarget
                XMLClickTarget currentXMLClickTarget = mapXMLClickTargets.get(currentClickTargetName);

                // Convert the current XMLClickTarget to a ClickTargetDefinition
                ClickTargetDefinition currentClickTargetDefinition = currentXMLClickTarget.toClickTargetDefinition();

                // Add the ClickTarget to the map
                mapClickTargetDefinitions.put(currentClickTargetName, currentClickTargetDefinition);

            }


            // Convert the XMLTransitionTriggers to TransitionTriggers
            Map<NTuple, List<LevelDefinitionLadder.TransitionTrigger>> mapTransitionTriggers = new HashMap<>();

            // Loop through the XMLTransitionTriggers
            for (XMLTransitionTrigger xmlTransitionTrigger : listXMLTransitionTriggers) {

                // Create the TransitionTrigger object
                LevelDefinitionLadder.TransitionTrigger transitionTrigger = xmlTransitionTrigger.toTransitionTrigger();

                // Create the TransitionTrigger NTuple key object
                NTuple transitionTriggerKey = LevelDefinitionLadder.nTupleTypeTransitionTriggerKey.createNTuple(
                        transitionTrigger.sourceClickTargetName
                        , transitionTrigger.sourceClickTargetProfileName
                );

                // Check if the key is already in the map
                if (mapTransitionTriggers.containsKey(transitionTriggerKey)) {

                    // Add the TransitionTrigger to the list for this key
                    mapTransitionTriggers.get(transitionTriggerKey).add(transitionTrigger);

                } else {

                    // Create the transition trigger list for this key
                    ArrayList<LevelDefinitionLadder.TransitionTrigger> arrayListTransitionTriggers = new ArrayList<>();

                    // Add the transition trigger to the list
                    arrayListTransitionTriggers.add(transitionTrigger);

                    // Add the list to the map
                    mapTransitionTriggers.put(transitionTriggerKey, arrayListTransitionTriggers);
                }

            }


            // Convert the XMLRandomChangeTriggers to RandomChangeTriggers
            Map<NTuple, List<LevelDefinitionLadder.RandomChangeTrigger>> mapRandomChangeTriggers = new HashMap<>();

            // Loop through the XMLRandomChangeTriggers
            for (XMLRandomChangeTrigger xmlRandomChangeTrigger : listXMLRandomChangeTriggers) {

                // Convert the XMLRandomChangeTrigger to RandomChangeTrigger
                LevelDefinitionLadder.RandomChangeTrigger currentRandomChangeTrigger = xmlRandomChangeTrigger.toRandomChangeTrigger();

                // Create the RandomChangeTrigger NTuple key object
                NTuple randomChangeTriggerKey = LevelDefinitionLadder.nTupleTypeRandomChangeTriggerKey.createNTuple(
                        currentRandomChangeTrigger.sourceClickTargetName
                        , currentRandomChangeTrigger.sourceClickTargetProfileName
                        , currentRandomChangeTrigger.sourceVariable
                );

                // Check if the key is already in the map
                if (mapRandomChangeTriggers.containsKey(randomChangeTriggerKey)) {

                    // Add the RandomChangeTrigger to the list for this key
                    mapRandomChangeTriggers.get(randomChangeTriggerKey).add(currentRandomChangeTrigger);

                } else {

                    // Create the random change trigger list for this key
                    ArrayList<LevelDefinitionLadder.RandomChangeTrigger> arrayListRandomChangeTrigger = new ArrayList<>();

                    // Add the transition trigger to the list
                    arrayListRandomChangeTrigger.add(currentRandomChangeTrigger);

                    // Add the list to the map
                    mapRandomChangeTriggers.put(randomChangeTriggerKey, arrayListRandomChangeTrigger);
                }

            }

            // Convert the XMLClickTargetSettingsShuffles to ClickTargetSettingsShuffles
            List<LevelDefinitionLadder.ClickTargetSettingsShuffle> listClickTargetSettingsShuffles = new ArrayList<>();
            for (XMLClickTargetSettingsShuffle xmlClickTargetSettingsShuffle : listXMLClickTargetSettingsShuffles) {

                // Create the ClickTargetSettingsShuffle
                LevelDefinitionLadder.ClickTargetSettingsShuffle clickTargetSettingsShuffle =
                        xmlClickTargetSettingsShuffle.toClickTargetSettingsShuffle();

                // Add the ClickTargetSettingsShuffle to the list
                listClickTargetSettingsShuffles.add(clickTargetSettingsShuffle);

            }


            // Create the LevelDefinitionLadder
            LevelDefinitionLadder levelDefinitionLadder = new LevelDefinitionLadder(
                    this.level
                    , listXMLClickTargetNames
                    , mapClickTargetDefinitions
                    , mapTransitionTriggers
                    , mapRandomChangeTriggers
                    , listClickTargetSettingsShuffles
            );

            return levelDefinitionLadder;

        }
    }

    public static LevelDefinitionLadder getLevelDefinitionLadder(
            Context context
            , int gameLevel) {

        // Initialize the XMLLevel
        XMLLevel xmlLevel = new XMLLevel();

        try {

            // Initialize the flag on if we found the level
            boolean foundLevel = false;

            // Initialize trackers
            String currentClickTargetName = null;
            String currentClickTargetProfileName = null;
            String currentVariableName = null;
            int currentTransitionTriggerIndex = -1;
            int currentRandomChangeTriggerIndex = -1;
            int currentXMLClickTargetSettingsShuffleIndex = -1;

            // Get a XmlResourceParser object to read contents of the xml file
            XmlResourceParser xmlResourceParser =
                    context.getResources().getXml(R.xml.level_definitions_ladder);

            // Get the initial parser event
            int currentXmlEvent = xmlResourceParser.getEventType();

            //while haven't reached the end of the XML file
            while (currentXmlEvent != XmlPullParser.END_DOCUMENT)
            {

                // Get the type of the next event
                currentXmlEvent = xmlResourceParser.next();

                // Check if we found the start of a tag
                if (currentXmlEvent == XmlPullParser.START_TAG) {

                    // Check if we found the start of a level
                    if (xmlResourceParser.getName().contentEquals(NODE_NAME_LEVEL)) {

                        // Get the level for this level
                        int currentLevel = xmlResourceParser.getAttributeIntValue(null, ATTRIBUTE_NAME_LEVEL_LEVEL, 0);

                        // Check if the level is the one we are looking for
                        if (currentLevel == gameLevel) {

                            // Flag the level as found
                            foundLevel = true;

                            xmlLevel.level = gameLevel;

                            // Reset the trackers
                            currentClickTargetName = null;
                            currentClickTargetProfileName = null;
                            currentVariableName = null;

                        }

                        // Check if we found the level
                    } else if (foundLevel) {

                        // Initialize the current XML Objects to null
                        XMLClickTarget currentXMLClickTarget = null;
                        XMLClickTargetProfile currentXMLClickTargetProfile = null;
                        XMLVariable currentXMLVariable = null;

                        XMLRandomChangeTrigger currentXMLRandomChangeTrigger = null;
                        XMLTransitionTrigger currentXMLTransitionTrigger = null;

                        XMLClickTargetSettingsShuffle currentXMLClickTargetSettingsShuffle = null;

                        // Get the current XML Objects
                        if (currentClickTargetName != null
                                && xmlLevel.mapXMLClickTargets.containsKey(currentClickTargetName)) {
                            currentXMLClickTarget = xmlLevel.mapXMLClickTargets.get(currentClickTargetName);
                        }
                        if (currentClickTargetProfileName != null
                                && currentXMLClickTarget != null
                                && currentXMLClickTarget
                                    .xmlClickTargetProfileScript
                                    .mapXMLClickTargetProfiles.containsKey(currentClickTargetProfileName)) {
                            currentXMLClickTargetProfile =
                                    currentXMLClickTarget
                                            .xmlClickTargetProfileScript
                                            .mapXMLClickTargetProfiles.get(currentClickTargetProfileName);
                        }
                        if (currentVariableName != null
                                && currentXMLClickTargetProfile != null
                                && currentXMLClickTargetProfile
                                    .mapXMLVariables.containsKey(currentVariableName)) {
                            currentXMLVariable =
                                    currentXMLClickTargetProfile
                                            .mapXMLVariables.get(currentVariableName);
                        }
                        if (currentRandomChangeTriggerIndex >= 0) {
                            currentXMLRandomChangeTrigger =
                                    xmlLevel.listXMLRandomChangeTriggers.get(currentRandomChangeTriggerIndex);
                        }
                        if (currentTransitionTriggerIndex >= 0) {
                            currentXMLTransitionTrigger =
                                    xmlLevel.listXMLTransitionTriggers.get(currentTransitionTriggerIndex);
                        }

                        if (currentXMLClickTargetSettingsShuffleIndex >= 0) {
                            currentXMLClickTargetSettingsShuffle =
                                    xmlLevel.listXMLClickTargetSettingsShuffles.get(currentXMLClickTargetSettingsShuffleIndex);
                        }

                        // -------------------- BEGIN Creating XML Objects -------------------- //

                        // Check if this is the start of a <ClickTarget> tag
                        if (xmlResourceParser.getName().contentEquals(NODE_NAME_CLICK_TARGET)) {

                            // Get the attributes
                            String currentXMLClickTargetName =
                                    (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_CLICKTARGET_NAME) == null) ?
                                    ""
                                    : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_CLICKTARGET_NAME);

                            // Create the XMLClickTarget object
                            XMLClickTarget xmlClickTarget = new XMLClickTarget(context);
                            xmlClickTarget.name = currentXMLClickTargetName;

                            // Add it to the level
                            xmlLevel.listXMLClickTargetNames.add(currentXMLClickTargetName);
                            xmlLevel.mapXMLClickTargets.put(currentXMLClickTargetName, xmlClickTarget);

                            // Set the trackers
                            currentClickTargetName = currentXMLClickTargetName;
                            currentClickTargetProfileName = null;
                            currentVariableName = null;

                        }

                        // Check if this is the start of a <ClickTargetProfileScript> tag
                        else if (xmlResourceParser.getName().contentEquals(NODE_NAME_CLICK_TARGET_PROFILE_SCRIPT)
                                && currentXMLClickTarget != null) {

                            // Get the attributes
                            ClickTargetProfileScript.SCRIPT_TRANSITION_MODE scriptTransitionMode =
                                    UtilityFunctions.getEnumValue(
                                            xmlResourceParser
                                            , ATTRIBUTE_NAME_SCRIPT_TRANSITION_MODE
                                            , ClickTargetProfileScript.SCRIPT_TRANSITION_MODE.class
                                            , currentXMLClickTarget.xmlClickTargetProfileScript.scriptTransitionMode
                                    );
                            ClickTargetProfileScript.SCRIPT_TRANSITION_INTERVAL scriptTransitionInterval =
                                    UtilityFunctions.getEnumValue(
                                            xmlResourceParser
                                            , ATTRIBUTE_NAME_SCRIPT_TRANSITION_INTERVAL
                                            , ClickTargetProfileScript.SCRIPT_TRANSITION_INTERVAL.class
                                            , currentXMLClickTarget.xmlClickTargetProfileScript.scriptTransitionInterval
                                    );
                            ClickTargetProfileScript.SCRIPT_CYCLE_DIRECTION scriptCycleDirection =
                                    UtilityFunctions.getEnumValue(
                                            xmlResourceParser
                                            , ATTRIBUTE_NAME_SCRIPT_CYCLE_DIRECTION
                                            , ClickTargetProfileScript.SCRIPT_CYCLE_DIRECTION.class
                                            , currentXMLClickTarget.xmlClickTargetProfileScript.scriptCycleDirection
                                    );
                            String initialClickTargetProfileName =
                                    (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_SCRIPT_INITIAL_CLICK_TARGET_PROFILE) == null) ?
                                            ""
                                            : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_SCRIPT_INITIAL_CLICK_TARGET_PROFILE);
                            boolean randomInitialClickTargetProfile =
                                    xmlResourceParser.getAttributeBooleanValue(
                                            null
                                            , ATTRIBUTE_NAME_SCRIPT_RANDOM_INITIAL_CLICK_TARGET_PROFILE
                                            , currentXMLClickTarget.xmlClickTargetProfileScript.randomInitialClickTargetProfile
                                    );


                            // Set the attributes
                            currentXMLClickTarget.xmlClickTargetProfileScript.scriptTransitionMode = scriptTransitionMode;
                            currentXMLClickTarget.xmlClickTargetProfileScript.scriptTransitionInterval = scriptTransitionInterval;
                            currentXMLClickTarget.xmlClickTargetProfileScript.scriptCycleDirection = scriptCycleDirection;
                            currentXMLClickTarget.xmlClickTargetProfileScript.initialClickTargetProfileName = initialClickTargetProfileName;
                            currentXMLClickTarget.xmlClickTargetProfileScript.randomInitialClickTargetProfile = randomInitialClickTargetProfile;

                        }

                        // Check if this is the start of a <ClickTargetProfile> tag
                        else if (xmlResourceParser.getName().contentEquals(NODE_NAME_CLICK_TARGET_PROFILE)
                                && currentXMLClickTarget != null) {

                            // Create the XMLClickTargetProfile object
                            XMLClickTargetProfile xmlClickTargetProfile = new XMLClickTargetProfile(context);

                            // Get the attributes
                            String name =
                                    (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_NAME) == null) ?
                                            ""
                                            : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_NAME);
                            double scriptTransitionValue =
                                    (double) xmlResourceParser.getAttributeFloatValue(
                                            null
                                            , ATTRIBUTE_NAME_PROFILE_SCRIPT_TRANSITION_VALUE
                                            , (float) xmlClickTargetProfile.scriptTransitionValue
                                    );
                            String shape =
                                    (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TARGET_SHAPE) == null) ?
                                            xmlClickTargetProfile.shape
                                            : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TARGET_SHAPE);
                            boolean isClickable =
                                    xmlResourceParser.getAttributeBooleanValue(
                                            null
                                            , ATTRIBUTE_NAME_PROFILE_TARGET_IS_CLICKABLE
                                            , xmlClickTargetProfile.isClickable
                                    );
                            ClickTarget.VISIBILITY visibility =
                                    UtilityFunctions.getEnumValue(
                                            xmlResourceParser
                                            , ATTRIBUTE_NAME_PROFILE_TARGET_VISIBILITY
                                            , ClickTarget.VISIBILITY.class
                                            , xmlClickTargetProfile.visibility
                                    );

                            // Set the attributes
                            xmlClickTargetProfile.name = name;
                            xmlClickTargetProfile.scriptTransitionValue = scriptTransitionValue;
                            xmlClickTargetProfile.shape = shape;
                            xmlClickTargetProfile.isClickable = isClickable;
                            xmlClickTargetProfile.visibility = visibility;

                            // Add it to the ClickTargetProfileScript
                            currentXMLClickTarget.xmlClickTargetProfileScript.listClickTargetProfileNames.add(name);
                            currentXMLClickTarget.xmlClickTargetProfileScript.mapXMLClickTargetProfiles.put(name, xmlClickTargetProfile);

                            // Set the trackers
                            currentClickTargetProfileName = name;
                            currentVariableName = null;

                        }

                        // Check if this is the start of a <Variable> tag
                        else if (xmlResourceParser.getName().contentEquals(NODE_NAME_VARIABLE)
                                && currentXMLClickTargetProfile != null) {

                            // Get the variable name
                            String name = xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_VARIABLE_NAME);

                            // Check if it's a variable in the XMLClickTargetProfile
                            boolean isValidVariableName = currentXMLClickTargetProfile.mapXMLVariables.containsKey(name);

                            // Proceed only if it's a valid variable name
                            if (isValidVariableName) {

                                // Get the XMLVariable object
                                currentXMLVariable =
                                       currentXMLClickTargetProfile
                                               .mapXMLVariables.get(name);

                                // Get the attributes
                                double initialValue =
                                        xmlResourceParser.getAttributeFloatValue(
                                                null
                                                , ATTRIBUTE_NAME_VARIABLE_INITIAL_VALUE
                                                , (float) currentXMLVariable.initialValue
                                        );
                                double minimumValue =
                                        xmlResourceParser.getAttributeFloatValue(
                                                null
                                                , ATTRIBUTE_NAME_VARIABLE_MINIMUM_VALUE
                                                , (float) currentXMLVariable.minimumValue
                                        );
                                double maximumValue =
                                        xmlResourceParser.getAttributeFloatValue(
                                                null
                                                , ATTRIBUTE_NAME_VARIABLE_MAXIMUM_VALUE
                                                , (float) currentXMLVariable.maximumValue
                                        );
                                boolean randomInitialValue =
                                        xmlResourceParser.getAttributeBooleanValue(
                                                null
                                                , ATTRIBUTE_NAME_VARIABLE_RANDOM_INITIAL_VALUE
                                                , currentXMLVariable.randomInitialValue
                                        );
                                boolean randomInitialSign =
                                        xmlResourceParser.getAttributeBooleanValue(
                                                null
                                                , ATTRIBUTE_NAME_VARIABLE_RANDOM_INITIAL_SIGN
                                                , currentXMLVariable.randomInitialSign
                                        );
                                boolean canChange =
                                        xmlResourceParser.getAttributeBooleanValue(
                                                null
                                                , ATTRIBUTE_NAME_VARIABLE_CAN_CHANGE
                                                , currentXMLVariable.canChange
                                        );
                                ClickTargetProfile.TRANSITION_CONTINUITY transitionContinuity =
                                        UtilityFunctions.getEnumValue(
                                                xmlResourceParser
                                                , ATTRIBUTE_NAME_VARIABLE_TRANSITION_CONTINUITY
                                                , ClickTargetProfile.TRANSITION_CONTINUITY.class
                                                , currentXMLVariable.transitionContinuity
                                        );

                                // Normalize the min and max values
                                if (minimumValue > initialValue) {
                                    minimumValue = initialValue;
                                }
                                if (maximumValue < initialValue) {
                                    maximumValue = initialValue;
                                }

                                // Set the attributes
                                currentXMLVariable.initialValue = initialValue;
                                currentXMLVariable.minimumValue = minimumValue;
                                currentXMLVariable.maximumValue = maximumValue;
                                currentXMLVariable.randomInitialValue = randomInitialValue;
                                currentXMLVariable.randomInitialSign = randomInitialSign;
                                currentXMLVariable.canChange = canChange;
                                currentXMLVariable.transitionContinuity = transitionContinuity;

                                // Set the tracker
                                currentVariableName = name;

                            }

                        }

                        // Check if this is the start of a <RandomChangeEffect> tag
                        else if (xmlResourceParser.getName().contentEquals(NODE_NAME_RANDOM_CHANGE_EFFECT)
                                && currentXMLVariable != null) {

                            // Get the attributes
                            boolean canRandomlyChange =
                                    xmlResourceParser.getAttributeBooleanValue(
                                            null
                                            , ATTRIBUTE_NAME_RANDOMCHANGEEFFECT_CAN_RANDOMLY_CHANGE
                                            , currentXMLVariable.xmlRandomChangeEffect.canRandomlyChange
                                    );
                            double randomChangeValue =
                                    xmlResourceParser.getAttributeFloatValue(
                                            null
                                            , ATTRIBUTE_NAME_RANDOMCHANGEEFFECT_RANDOM_CHANGE_VALUE
                                            , (float) currentXMLVariable.xmlRandomChangeEffect.randomChangeValue
                                    );
                            PositionEvolver.RandomChangeEffect.RANDOM_CHANGE_INTERVAL randomChangeInterval =
                                    UtilityFunctions.getEnumValue(
                                            xmlResourceParser
                                            , ATTRIBUTE_NAME_RANDOMCHANGEEFFECT_RANDOM_CHANGE_INTERVAL
                                            , PositionEvolver.RandomChangeEffect.RANDOM_CHANGE_INTERVAL.class
                                            , currentXMLVariable.xmlRandomChangeEffect.randomChangeInterval
                                    );
                            boolean bounceOnRandomChange =
                                    xmlResourceParser.getAttributeBooleanValue(
                                            null
                                            , ATTRIBUTE_NAME_RANDOMCHANGEEFFECT_BOUNCE_ON_RANDOM_CHANGE
                                            , currentXMLVariable.xmlRandomChangeEffect.bounceOnRandomChange
                                    );

                            // Set the attributes
                            currentXMLVariable.xmlRandomChangeEffect.canRandomlyChange = canRandomlyChange;
                            currentXMLVariable.xmlRandomChangeEffect.randomChangeValue = randomChangeValue;
                            currentXMLVariable.xmlRandomChangeEffect.randomChangeInterval = randomChangeInterval;
                            currentXMLVariable.xmlRandomChangeEffect.bounceOnRandomChange = bounceOnRandomChange;

                        }

                        // Check if this is the start of a <BoundaryEffect> tag
                        else if (xmlResourceParser.getName().contentEquals(NODE_NAME_BOUNDARY_EFFECT)
                                && currentXMLVariable != null) {

                            // Get the attributes
                            PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT boundaryEffect =
                                    UtilityFunctions.getEnumValue(
                                            xmlResourceParser
                                            , ATTRIBUTE_NAME_BOUNDARYEFFECT_BOUNDARY_EFFECT
                                            , PositionEvolver.BoundaryEffect.BOUNDARY_EFFECT.class
                                            , currentXMLVariable.xmlBoundaryEffect.boundaryEffect
                                    );
                            boolean mirrorAbsoluteValueBoundaries =
                                    xmlResourceParser.getAttributeBooleanValue(
                                            null
                                            , ATTRIBUTE_NAME_BOUNDARYEFFECT_MIRROR_ABSOLUTE_VALUE_BOUNDARIES
                                            , currentXMLVariable.xmlBoundaryEffect.mirrorAbsoluteValueBoundaries
                                    );
                            boolean bounceOnInternalBoundary =
                                    xmlResourceParser.getAttributeBooleanValue(
                                            null
                                            , ATTRIBUTE_NAME_BOUNDARYEFFECT_BOUNCE_ON_INTERNAL_BOUNDARY
                                            , currentXMLVariable.xmlBoundaryEffect.bounceOnInternalBoundary
                                    );

                            // Set the attributes
                            currentXMLVariable.xmlBoundaryEffect.boundaryEffect = boundaryEffect;
                            currentXMLVariable.xmlBoundaryEffect.mirrorAbsoluteValueBoundaries = mirrorAbsoluteValueBoundaries;
                            currentXMLVariable.xmlBoundaryEffect.bounceOnInternalBoundary = bounceOnInternalBoundary;

                        }

                        // Check if this is the start of a <TransitionTrigger> tag
                        else if (xmlResourceParser.getName().contentEquals(NODE_NAME_TRANSITION_TRIGGER)) {

                            // Get the attributes
                            String sourceClickTargetName =
                                    (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_TRANSITIONTRIGGER_SOURCE_CLICK_TARGET_NAME) == null) ?
                                            ""
                                            : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_TRANSITIONTRIGGER_SOURCE_CLICK_TARGET_NAME);
                            String sourceClickTargetProfileName =
                                    (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_TRANSITIONTRIGGER_SOURCE_CLICK_TARGET_PROFILE_NAME) == null) ?
                                            ""
                                            : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_TRANSITIONTRIGGER_SOURCE_CLICK_TARGET_PROFILE_NAME);
                            String targetClickTargetName =
                                    (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_TRANSITIONTRIGGER_TARGET_CLICK_TARGET_NAME) == null) ?
                                            ""
                                            : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_TRANSITIONTRIGGER_TARGET_CLICK_TARGET_NAME);
                            String targetClickTargetProfileName =
                                    (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_TRANSITIONTRIGGER_TARGET_CLICK_TARGET_PROFILE_NAME) == null) ?
                                            ""
                                            : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_TRANSITIONTRIGGER_TARGET_CLICK_TARGET_PROFILE_NAME);
                            boolean randomTargetClickTarget =
                                    xmlResourceParser.getAttributeBooleanValue(
                                            null
                                            , ATTRIBUTE_NAME_TRANSITIONTRIGGER_RANDOM_TARGET_CLICK_TARGET
                                            , context.getResources().getBoolean(R.bool.game_values_defaultTransitionTriggerRandomTargetClickTarget)
                                    );
                            boolean randomTargetClickTargetProfile =
                                    xmlResourceParser.getAttributeBooleanValue(
                                            null
                                            , ATTRIBUTE_NAME_TRANSITIONTRIGGER_RANDOM_TARGET_CLICK_TARGET_PROFILE
                                            , context.getResources().getBoolean(R.bool.game_values_defaultTransitionTriggerRandomTargetClickTargetProfile)
                                    );

                            // Check if the names are not the same
                            if (!(
                                    sourceClickTargetName.contentEquals(targetClickTargetName)
                                            && sourceClickTargetProfileName.contentEquals(targetClickTargetProfileName))) {

                                // Create the XMLTransitionTrigger object
                                XMLTransitionTrigger xmlTransitionTrigger = new XMLTransitionTrigger(
                                        sourceClickTargetName
                                        , sourceClickTargetProfileName
                                        , targetClickTargetName
                                        , targetClickTargetProfileName
                                        , randomTargetClickTarget
                                        , randomTargetClickTargetProfile
                                );

                                // Add the XMLTransitionTrigger object to the transition trigger array list
                                xmlLevel.listXMLTransitionTriggers.add(xmlTransitionTrigger);

                                // Set the trackers
                                currentTransitionTriggerIndex = xmlLevel.listXMLTransitionTriggers.indexOf(xmlTransitionTrigger);
                                currentRandomChangeTriggerIndex = -1;
                                currentXMLClickTargetSettingsShuffleIndex = -1;

                            }

                        }

                        // Check if this is the start of a <RandomChangeTrigger> tag
                        else if (xmlResourceParser.getName().contentEquals(NODE_NAME_RANDOM_CHANGE_TRIGGER)) {

                            // Get the attributes
                            String sourceClickTargetName =
                                    (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_RANDOMCHANGETRIGGER_SOURCE_CLICK_TARGET_NAME) == null) ?
                                            ""
                                            : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_RANDOMCHANGETRIGGER_SOURCE_CLICK_TARGET_NAME);
                            String sourceClickTargetProfileName =
                                    (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_RANDOMCHANGETRIGGER_SOURCE_CLICK_TARGET_PROFILE_NAME) == null) ?
                                            ""
                                            : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_RANDOMCHANGETRIGGER_SOURCE_CLICK_TARGET_PROFILE_NAME);
                            String sourceVariable =
                                    (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_RANDOMCHANGETRIGGER_SOURCE_VARIABLE) == null) ?
                                            ""
                                            : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_RANDOMCHANGETRIGGER_SOURCE_VARIABLE);
                            String targetClickTargetName =
                                    (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_RANDOMCHANGETRIGGER_TARGET_CLICK_TARGET_NAME) == null) ?
                                            ""
                                            : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_RANDOMCHANGETRIGGER_TARGET_CLICK_TARGET_NAME);
                            String targetClickTargetProfileName =
                                    (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_RANDOMCHANGETRIGGER_TARGET_CLICK_TARGET_PROFILE_NAME) == null) ?
                                            ""
                                            : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_RANDOMCHANGETRIGGER_TARGET_CLICK_TARGET_PROFILE_NAME);
                            String targetVariable =
                                    (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_RANDOMCHANGETRIGGER_TARGET_VARIABLE) == null) ?
                                            ""
                                            : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_RANDOMCHANGETRIGGER_TARGET_VARIABLE);

                            // Make sure the names aren't all the same
                            // and they are in the variable names list
                            if (!(
                                    sourceVariable.contentEquals(targetVariable)
                                            && sourceClickTargetName.contentEquals(targetClickTargetName)
                                            && sourceClickTargetProfileName.contentEquals(targetClickTargetProfileName))
                                && arrayListVariableNames.contains(sourceVariable)
                                && arrayListVariableNames.contains(targetVariable)) {

                                // Create the XMLRandomChangeTrigger object
                                XMLRandomChangeTrigger xmlRandomChangeTrigger = new XMLRandomChangeTrigger(
                                        sourceClickTargetName
                                        , sourceClickTargetProfileName
                                        , sourceVariable
                                        , targetClickTargetName
                                        , targetClickTargetProfileName
                                        , targetVariable
                                );

                                // Add the XMLRandomChangeTrigger to the list
                                xmlLevel.listXMLRandomChangeTriggers.add(xmlRandomChangeTrigger);

                                // Set the trackers
                                currentTransitionTriggerIndex = -1;
                                currentRandomChangeTriggerIndex = xmlLevel.listXMLRandomChangeTriggers.indexOf(xmlRandomChangeTrigger);
                                currentXMLClickTargetSettingsShuffleIndex = -1;

                            }

                        }

                        // Check if this is the start of a <SyncVariableTrigger> tag
                        else if (xmlResourceParser.getName().contentEquals(NODE_NAME_SYNC_VARIABLE_TRIGGER)) {

                            // Get the attributes
                            String sourceClickTargetName =
                                    (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_SYNCVARIABLE_SOURCE_CLICK_TARGET_NAME) == null) ?
                                            ""
                                            : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_SYNCVARIABLE_SOURCE_CLICK_TARGET_NAME);
                            String sourceClickTargetProfileName =
                                    (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_SYNCVARIABLE_SOURCE_CLICK_TARGET_PROFILE_NAME) == null) ?
                                            ""
                                            : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_SYNCVARIABLE_SOURCE_CLICK_TARGET_PROFILE_NAME);
                            String targetClickTargetName =
                                    (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_SYNCVARIABLE_TARGET_CLICK_TARGET_NAME) == null) ?
                                            ""
                                            : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_SYNCVARIABLE_TARGET_CLICK_TARGET_NAME);
                            String targetClickTargetProfileName =
                                    (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_SYNCVARIABLE_TARGET_CLICK_TARGET_PROFILE_NAME) == null) ?
                                            ""
                                            : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_SYNCVARIABLE_TARGET_CLICK_TARGET_PROFILE_NAME);
                            String variableName =
                                    (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_SYNCVARIABLE_VARIABLE_NAME) == null) ?
                                            ""
                                            : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_SYNCVARIABLE_VARIABLE_NAME);
                            LevelDefinitionLadder.SyncVariableTrigger.MODE mode =
                                    UtilityFunctions.getEnumValue(
                                            xmlResourceParser
                                            , ATTRIBUTE_NAME_SYNCVARIABLE_MODE
                                            , LevelDefinitionLadder.SyncVariableTrigger.MODE.class
                                            , LevelDefinitionLadder.SyncVariableTrigger.MODE.valueOf(
                                                    context.getString(R.string.game_values_defaultSyncVariableMode)
                                            )
                                    );
                            double value =
                                    xmlResourceParser.getAttributeFloatValue(
                                            null
                                            , ATTRIBUTE_NAME_SYNCVARIABLE_VALUE
                                            , (float) UtilityFunctions.getResourceFloatValue(
                                                    context
                                                    , R.dimen.game_values_defaultSyncVariableValue)
                                    );

                            boolean isSameNames = sourceClickTargetName.contentEquals(targetClickTargetName)
                                    && sourceClickTargetProfileName.contentEquals(targetClickTargetProfileName);
                            boolean isValidVariableName = arrayListVariableNames.contains(variableName);

                            // Check if the names are not the same
                            if (!isSameNames && isValidVariableName) {

                                // Create the XMLSyncVariable object
                                XMLSyncVariableTrigger xmlSyncVariableTrigger = new XMLSyncVariableTrigger(
                                        sourceClickTargetName
                                        , sourceClickTargetProfileName
                                        , targetClickTargetName
                                        , targetClickTargetProfileName
                                        , variableName
                                        , mode
                                        , value
                                );

                                // Add the XMLSyncVariableTrigger object to the transition trigger array list
                                if (currentXMLTransitionTrigger != null) {
                                    currentXMLTransitionTrigger.arrayListXMLSyncVariableTriggers.add(xmlSyncVariableTrigger);
                                } else if (currentXMLRandomChangeTrigger != null) {
                                    currentXMLRandomChangeTrigger.arrayListXMLSyncVariableTriggers.add(xmlSyncVariableTrigger);
                                }

                            }

                        }

                        // Check if this is a <ClickTargetSettingsShuffle> tag
                        else if (xmlResourceParser.getName().contentEquals(NODE_NAME_CLICK_TARGET_SETTINGS_SHUFFLE)) {

                            // Get the attributes
                            boolean preserveOrder =
                                    xmlResourceParser.getAttributeBooleanValue(
                                            null
                                            , ATTRIBUTE_NAME_CLICKTARGETSETTINGSSHUFFLE_PRESERVE_ORDER
                                            , context.getResources().getBoolean(R.bool.game_values_defaultClickTargetSettingsShufflePreserveOrder)
                                    );

                            // Create the XMLClickTargetSettingsShuffle object
                            XMLClickTargetSettingsShuffle xmlClickTargetSettingsShuffle = new XMLClickTargetSettingsShuffle(context);

                            // Set the attributes
                            xmlClickTargetSettingsShuffle.preserveOrder = preserveOrder;

                            // Add the object to the list
                            xmlLevel.listXMLClickTargetSettingsShuffles.add(xmlClickTargetSettingsShuffle);

                            // Set the current XMLClickTargetSettingsShuffle index
                            currentTransitionTriggerIndex = -1;
                            currentRandomChangeTriggerIndex = -1;
                            currentXMLClickTargetSettingsShuffleIndex = xmlLevel.listXMLClickTargetSettingsShuffles.indexOf(xmlClickTargetSettingsShuffle);

                        }

                        // Check if this is a <ClickTargetSettingsShuffleSetting> tag
                        else if (xmlResourceParser.getName().contentEquals(NODE_NAME_CLICK_TARGET_SETTINGS_SHUFFLE_SETTING)
                                && currentXMLClickTargetSettingsShuffle != null) {

                            // Get the attributes
                            String name =
                                    (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_CLICKTARGETSETTINGSSHUFFLESETTING_NAME) == null) ?
                                            ""
                                            : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_CLICKTARGETSETTINGSSHUFFLESETTING_NAME);

                            // Add the setting to the existing shuffle setting name list
                            currentXMLClickTargetSettingsShuffle.listXMLClickTargetSettingsShuffleSettingNames.add(name);

                        }

                        // -------------------- END Creating XML Objects -------------------- //

                    }

                } else if (currentXmlEvent == XmlPullParser.END_TAG) {

                    // Check if we found the level
                    if (foundLevel) {

                        // Initialize the current XML Objects to null
                        XMLClickTarget currentXMLClickTarget = null;
                        XMLClickTargetProfile currentXMLClickTargetProfile = null;
                        XMLVariable currentXMLVariable = null;

                        // Get the current XML Objects
                        if (currentClickTargetName != null
                                && xmlLevel.mapXMLClickTargets.containsKey(currentClickTargetName)) {
                            currentXMLClickTarget = xmlLevel.mapXMLClickTargets.get(currentClickTargetName);
                        }
                        if (currentClickTargetProfileName != null
                                && currentXMLClickTarget != null
                                && currentXMLClickTarget
                                .xmlClickTargetProfileScript
                                .mapXMLClickTargetProfiles.containsKey(currentClickTargetProfileName)) {
                            currentXMLClickTargetProfile =
                                    currentXMLClickTarget
                                            .xmlClickTargetProfileScript
                                            .mapXMLClickTargetProfiles.get(currentClickTargetProfileName);
                        }
                        if (currentVariableName != null
                                && currentXMLClickTargetProfile != null
                                && currentXMLClickTargetProfile
                                .mapXMLVariables.containsKey(currentVariableName)) {
                            currentXMLVariable =
                                    currentXMLClickTargetProfile
                                            .mapXMLVariables.get(currentVariableName);
                        }

                        // Check if this is the end of a <Level> tag
                        if (xmlResourceParser.getName().contentEquals(NODE_NAME_LEVEL)) {

                            // Break out of the while loop, we have finished with this level
                            break;

                        }

                        // Check if this is the end of a <ClickTarget> tag
                        else if (xmlResourceParser.getName().contentEquals(NODE_NAME_CLICK_TARGET)) {



                        }

                        // Check if this is the end of a <ClickTargetProfileScript> tag
                        else if (xmlResourceParser.getName().contentEquals(NODE_NAME_CLICK_TARGET_PROFILE_SCRIPT)
                                && currentXMLClickTarget != null) {



                        }

                        // Check if this is the end of a <ClickTargetProfile> tag
                        else if (xmlResourceParser.getName().contentEquals(NODE_NAME_CLICK_TARGET_PROFILE)) {

                        }

                        // Check if this is the end of a <Variable> tag
                        else if (xmlResourceParser.getName().contentEquals(NODE_NAME_VARIABLE)) {

                        }

                        // Check if this is the end of a <RandomChangeEffect> tag
                        else if (xmlResourceParser.getName().contentEquals(NODE_NAME_RANDOM_CHANGE_EFFECT)) {

                        }

                        // Check if this is the end of a <BoundaryEffect> tag
                        else if (xmlResourceParser.getName().contentEquals(NODE_NAME_BOUNDARY_EFFECT)) {

                        }

                        // Check if this is the end of a <TransitionTrigger> tag
                        else if (xmlResourceParser.getName().contentEquals(NODE_NAME_TRANSITION_TRIGGER)) {

                        }

                        // Check if this is the end of a <RandomChangeTrigger> tag
                        else if (xmlResourceParser.getName().contentEquals(NODE_NAME_RANDOM_CHANGE_TRIGGER)) {

                        }

                    }

                }

            }

            //Release resources associated with the parser
            xmlResourceParser.close();

        } catch (XmlPullParserException|IOException e) {
            int blah = 0;
        }

        // -------------------- BEGIN Perform Validations -------------------- //
        // -------------------- END Perform Validations -------------------- //

        return xmlLevel.toLevelDefinitionLadder();

    }

}
