package com.delsquared.lightningdots.game;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.TypedValue;

import com.delsquared.lightningdots.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class ClickTargetProfileScriptHelper {

    private static final String NODE_NAME_CLICK_TARGET_PROFILE_SCRIPT = "ClickTargetProfileScript";
    private static final String NODE_NAME_CLICK_TARGET_PROFILE = "ClickTargetProfile";

    private static final String ATTRIBUTE_NAME_SCRIPT_LEVEL = "level";
    private static final String ATTRIBUTE_NAME_SCRIPT_INITIAL_CLICK_TARGET_PROFILE = "initialClickTargetProfile";
    private static final String ATTRIBUTE_NAME_SCRIPT_TRANSITION_MODE = "scriptTransitionMode";
    private static final String ATTRIBUTE_NAME_SCRIPT_TRANSITION_INTERVAL = "scriptTransitionInterval";
    private static final String ATTRIBUTE_NAME_SCRIPT_CYCLE_DIRECTION = "scriptCycleDirection";

    private static final String ATTRIBUTE_NAME_PROFILE_TRANSITION_VALUE = "scriptTransitionValue";
    private static final String ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_POSITION = "scriptTransitionContinuousPosition";
    private static final String ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_SPEED = "scriptTransitionContinuousSpeed";
    private static final String ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_DIRECTION = "scriptTransitionContinuousDirection";
    private static final String ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_SPEED_CHANGE = "scriptTransitionContinuousSpeedChange";
    private static final String ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_DIRECTION_CHANGE = "scriptTransitionContinuousDirectionChange";
    private static final String ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_RADIUS = "scriptTransitionContinuousRadius";
    private static final String ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_DRADIUS = "scriptTransitionContinuousDRadius";
    private static final String ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_DRADIUS_CHANGE = "scriptTransitionContinuousDRadiusChange";

    private static final String ATTRIBUTE_NAME_PROFILE_INITIAL_TARGET_SPEED_MULTIPLIER = "initialTargetSpeedMultiplier";
    private static final String ATTRIBUTE_NAME_PROFILE_MAXIMUM_TARGET_SPEED_MULTIPLIER = "maximumTargetSpeedMultiplier";
    private static final String ATTRIBUTE_NAME_PROFILE_MAXIMUM_TARGET_SPEED_CHANGE_MULTIPLIER = "maximumTargetSpeedChangeMultiplier";
    private static final String ATTRIBUTE_NAME_PROFILE_MAXIMUM_TARGET_DIRECTION_ANGLE_CHANGE_MULTIPLIER = "maximumTargetDirectionAngleChangeMultiplier";

    private static final String ATTRIBUTE_NAME_PROFILE_INITIAL_TARGET_RADIUS_MULTIPLIER = "initialTargetRadiusMultiplier";
    private static final String ATTRIBUTE_NAME_PROFILE_MINIMUM_TARGET_RADIUS_MULTIPLIER = "minimumTargetRadiusMultiplier";
    private static final String ATTRIBUTE_NAME_PROFILE_MAXIMUM_TARGET_DRADIUS_MULTIPLIER = "maximumTargetDRadiusMultiplier";
    private static final String ATTRIBUTE_NAME_PROFILE_MAXIMUM_TARGET_DRADIUS_CHANGE_MULTIPLIER = "maximumTargetDRadiusChangeMultiplier";

    private static final String ATTRIBUTE_NAME_PROFILE_PROBABILITY_OF_RANDOM_POSITION_CHANGE_PER_SECOND = "probabilityOfRandomPositionChangePerSecond";
    private static final String ATTRIBUTE_NAME_PROFILE_PROBABILITY_OF_RANDOM_SPEED_CHANGE_PER_SECOND = "probabilityOfRandomSpeedChangePerSecond";
    private static final String ATTRIBUTE_NAME_PROFILE_PROBABILITY_OF_RANDOM_DIRECTION_CHANGE_PER_SECOND = "probabilityOfRandomDirectionChangePerSecond";
    private static final String ATTRIBUTE_NAME_PROFILE_PROBABILITY_OF_RANDOM_RADIUS_CHANGE_PER_SECOND = "probabilityOfRandomRadiusChangePerSecond";
    private static final String ATTRIBUTE_NAME_PROFILE_PROBABILITY_OF_RANDOM_DRADIUS_CHANGE_PER_SECOND = "probabilityOfRandomDRadiusChangePerSecond";

    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_CAN_CHANGE_POSITION = "targetCanChangePosition";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_CAN_CHANGE_SPEED = "targetCanChangeSpeed";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_CAN_CHANGE_DIRECTION = "targetCanChangeDirection";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_CAN_CHANGE_RADIUS = "targetCanChangeRadius";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_CAN_CHANGE_DRADIUS = "targetCanChangeDRadius";

    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_CAN_RANDOMLY_CHANGE_POSITION = "targetCanRandomlyChangePosition";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_CAN_RANDOMLY_CHANGE_SPEED = "targetCanRandomlyChangeSpeed";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_CAN_RANDOMLY_CHANGE_DIRECTION = "targetCanRandomlyChangeDirection";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_CAN_RANDOMLY_CHANGE_RADIUS = "targetCanRandomlyChangeRadius";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_CAN_RANDOMLY_CHANGE_DRADIUS = "targetCanRandomlyChangeDRadius";

    private static final ArrayList<String> scriptTransitionContinuityValues = new ArrayList<String>(){{
            add("CONTINUOUS");
            add("DISCONTINUOUS");
            add("DEFAULT");
    }};

    private static final ArrayList<String> scriptTransitionModeValues = new ArrayList<String>(){{
            add("CONSTANT");
            add("CYCLE");
            add("RANDOM");
    }};
    public static final ArrayList<String> scriptTransitionIntervalValues = new ArrayList<String>() {{
            add("CONSTANT");
            add("REGULAR");
            add("RANDOM");
    }};
    public static final ArrayList<String> scriptCycleDirectionValues = new ArrayList<String>() {{
            add("INCREASING");
            add("DECREASING");
    }};

    public static int getHighestScriptedLevel(
            Context context
            , Game.GameType gameType) {

        int returnValue = 0;

        try {

            // Get a XmlResourceParser object to read contents of the xml file
            XmlResourceParser xmlResourceParser =
                    context.getResources().getXml(R.xml.click_target_profile_scripts);

            // Get the initial parser event
            int currentXmlEvent = xmlResourceParser.getEventType();

            //while haven't reached the end of the XML file
            while (currentXmlEvent != XmlPullParser.END_DOCUMENT)
            {

                // Check if we found the start of a tag
                if (currentXmlEvent == XmlPullParser.START_TAG) {

                    // Check if we found the start of a click target profile script
                    if (xmlResourceParser.getName().contentEquals(NODE_NAME_CLICK_TARGET_PROFILE_SCRIPT)) {

                        // Get the level for this script
                        int currentLevel = xmlResourceParser.getAttributeIntValue(null, ATTRIBUTE_NAME_SCRIPT_LEVEL, 0);

                        if (currentLevel > returnValue) {
                            returnValue = currentLevel;
                        }

                    }

                }

                currentXmlEvent = xmlResourceParser.next();
            }

        } catch (XmlPullParserException e) {
            int blah = 0;
        } catch (IOException e) {
            int blah = 0;
        } catch (Exception e) {
            int blah = 0;
        }

        return returnValue;

    }

    public static ClickTargetProfileScript getClickTargetProfileScript(
            Context context
            , Game.GameType gameType
            , int gameLevel) {

        // Initialize the typed value
        TypedValue typedValueResourceHelper = new TypedValue();

        // Radius
        context.getResources().getValue(R.dimen.game_values_defaultInitialTargetRadiusInches, typedValueResourceHelper, true);
        float defaultInitialTargetRadiusInches = typedValueResourceHelper.getFloat();
        context.getResources().getValue(R.dimen.game_values_defaultMinimumTargetRadiusInchesMultiplier, typedValueResourceHelper, true);
        float defaultMinimumTargetRadiusInchesMultiplier = typedValueResourceHelper.getFloat();

        // Speed
        context.getResources().getValue(R.dimen.game_values_defaultInitialTargetSpeedInchesPerSecond, typedValueResourceHelper, true);
        float defaultInitialTargetSpeedInchesPerSecond = typedValueResourceHelper.getFloat();
        context.getResources().getValue(R.dimen.game_values_defaultMaximumTargetSpeedMultiplier, typedValueResourceHelper, true);
        float defaultMaximumTargetSpeedMultiplier = typedValueResourceHelper.getFloat();

        // Speed change
        context.getResources().getValue(R.dimen.game_values_defaultMaximumTargetSpeedChangeInchesPerSecondPerSecond, typedValueResourceHelper, true);
        float defaultMaximumTargetSpeedChangeInchesPerSecondPerSecond = typedValueResourceHelper.getFloat();

        // Radius change
        context.getResources().getValue(R.dimen.game_values_defaultMaximumTargetDRadiusInchesPerSecond, typedValueResourceHelper, true);
        float defaultMaximumTargetDRadiusInchesPerSecond = typedValueResourceHelper.getFloat();

        // Radius acceleration
        context.getResources().getValue(R.dimen.game_values_defaultMaximumTargetDRadiusChangeInchesPerSecondPerSecond, typedValueResourceHelper, true);
        float defaultMaximumTargetDRadiusChangeInchesPerSecondPerSecond = typedValueResourceHelper.getFloat();

        // Direction angle change
        context.getResources().getValue(R.dimen.game_values_defaultMaximumTargetDirectionAngleChangeRadiansPerSecond, typedValueResourceHelper, true);
        float defaultMaximumTargetDirectionAngleChangeRadiansPerSecond = typedValueResourceHelper.getFloat();

        // Random change probabilities
        context.getResources().getValue(R.dimen.game_values_defaultProbabilityOfRandomPositionChangePerSecond, typedValueResourceHelper, true);
        float defaultProbabilityOfRandomPositionChangePerSecond = typedValueResourceHelper.getFloat();
        context.getResources().getValue(R.dimen.game_values_defaultProbabilityOfRandomDirectionChangePerSecond, typedValueResourceHelper, true);
        float defaultProbabilityOfRandomDirectionChangePerSecond = typedValueResourceHelper.getFloat();
        context.getResources().getValue(R.dimen.game_values_defaultProbabilityOfRandomRadiusChangePerSecond, typedValueResourceHelper, true);
        float defaultProbabilityOfRandomRadiusChangePerSecond = typedValueResourceHelper.getFloat();
        context.getResources().getValue(R.dimen.game_values_defaultProbabilityOfRandomDRadiusChangePerSecond, typedValueResourceHelper, true);
        float defaultProbabilityOfRandomDRadiusChangePerSecond = typedValueResourceHelper.getFloat();
        context.getResources().getValue(R.dimen.game_values_defaultProbabilityOfRandomSpeedChangePerSecond, typedValueResourceHelper, true);
        float defaultProbabilityOfRandomSpeedChangePerSecond = typedValueResourceHelper.getFloat();

        // Can do things
        boolean defaultCanChangePosition = context.getResources().getBoolean(R.bool.game_values_defaultCanChangePosition);
        boolean defaultCanChangeSpeed = context.getResources().getBoolean(R.bool.game_values_defaultCanChangeSpeed);
        boolean defaultCanChangeDirection = context.getResources().getBoolean(R.bool.game_values_defaultCanChangeDirection);
        boolean defaultCanChangeRadius = context.getResources().getBoolean(R.bool.game_values_defaultCanChangeRadius);
        boolean defaultCanChangeDRadius = context.getResources().getBoolean(R.bool.game_values_defaultCanChangeDRadius);

        // Can randomly do things
        boolean defaultCanRandomlyChangePosition = context.getResources().getBoolean(R.bool.game_values_defaultCanRandomlyChangePosition);
        boolean defaultCanRandomlyChangeSpeed = context.getResources().getBoolean(R.bool.game_values_defaultCanRandomlyChangeSpeed);
        boolean defaultCanRandomlyChangeDirection = context.getResources().getBoolean(R.bool.game_values_defaultCanRandomlyChangeDirection);
        boolean defaultCanRandomlyChangeRadius = context.getResources().getBoolean(R.bool.game_values_defaultCanRandomlyChangeRadius);
        boolean defaultCanRandomlyChangeDRadius = context.getResources().getBoolean(R.bool.game_values_defaultCanRandomlyChangeDRadius);

        // Transition continuity
        String defaultScriptTransitionContinuity = context.getString(R.string.game_values_defaultScriptTransitionContinuity);

        // Transitions
        String defaultScriptTransitionMode = context.getString(R.string.game_values_defaultScriptTransitionMode);
        String defaultScriptTransitionInterval = context.getString(R.string.game_values_defaultScriptTransitionInterval);
        String defaultScriptCycleDirection = context.getString(R.string.game_values_defaultScriptCycleDirection);
        context.getResources().getValue(R.dimen.game_values_defaultScriptTransitionValue, typedValueResourceHelper, true);
        float defaultScriptTransitionValue = typedValueResourceHelper.getFloat();

        // Initialize the variables
        ClickTargetProfileScript resultClickTargetProfileScript = null;
        ArrayList<Double> arrayListTransitionValue = new ArrayList<Double>();
        ArrayList<ClickTargetProfile> arrayListClickTargetProfile = new ArrayList<ClickTargetProfile>();
        boolean foundCurrentLevelClickTargetProfileScript = false;
        int initialClickTargetProfile = 0;
        String scriptTransitionModeString = defaultScriptTransitionMode;
        String scriptTransitionIntervalString = defaultScriptTransitionInterval;
        String scriptCycleDirectionString = defaultScriptCycleDirection;

        try {

            // Get a XmlResourceParser object to read contents of the xml file
            XmlResourceParser xmlResourceParser =
                    context.getResources().getXml(R.xml.click_target_profile_scripts);

            // Get the initial parser event
            int currentXmlEvent = xmlResourceParser.getEventType();

            //while haven't reached the end of the XML file
            while (currentXmlEvent != XmlPullParser.END_DOCUMENT)
            {

                // Check if we found the start of a tag
                if (currentXmlEvent == XmlPullParser.START_TAG) {

                    // Check if we found the start of a click target profile script
                    if (xmlResourceParser.getName().contentEquals(NODE_NAME_CLICK_TARGET_PROFILE_SCRIPT)) {

                        // Get the level for this script
                        int currentLevel = xmlResourceParser.getAttributeIntValue(null, ATTRIBUTE_NAME_SCRIPT_LEVEL, 0);

                        // Check if the level is the one we are looking for
                        if (currentLevel == gameLevel) {

                            // Flag the level as found
                            foundCurrentLevelClickTargetProfileScript = true;

                            initialClickTargetProfile =
                                    xmlResourceParser.getAttributeIntValue(
                                            null
                                            , ATTRIBUTE_NAME_SCRIPT_INITIAL_CLICK_TARGET_PROFILE
                                            , 0);

                            // Get the values for the transitions
                            scriptTransitionModeString =
                                    (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_SCRIPT_TRANSITION_MODE) == null) ?
                                        defaultScriptTransitionMode
                                        : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_SCRIPT_TRANSITION_MODE);
                            scriptTransitionIntervalString =
                                    (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_SCRIPT_TRANSITION_INTERVAL) == null) ?
                                        defaultScriptTransitionInterval
                                        : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_SCRIPT_TRANSITION_INTERVAL);
                            scriptCycleDirectionString =
                                    (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_SCRIPT_CYCLE_DIRECTION) == null) ?
                                        defaultScriptCycleDirection
                                        : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_SCRIPT_CYCLE_DIRECTION);
                        }

                        // Check if this is the start of a click target profile tag, and we are on the correct level
                    } else if (xmlResourceParser.getName().contentEquals(NODE_NAME_CLICK_TARGET_PROFILE)
                            && foundCurrentLevelClickTargetProfileScript == true) {

                        // Get radius attributes
                        double initialTargetRadiusInches =
                                (double) defaultInitialTargetRadiusInches
                                        * (double) xmlResourceParser.getAttributeFloatValue(
                                                null
                                                , ATTRIBUTE_NAME_PROFILE_INITIAL_TARGET_RADIUS_MULTIPLIER
                                                , 1.0f);
                        double minimumTargetRadiusInches =
                                (double) defaultInitialTargetRadiusInches
                                        * (double) xmlResourceParser.getAttributeFloatValue(
                                                null
                                                , ATTRIBUTE_NAME_PROFILE_MINIMUM_TARGET_RADIUS_MULTIPLIER
                                                , defaultMinimumTargetRadiusInchesMultiplier);


                        // Get speed attributes
                        double initialTargetSpeedInchesPerSecond =
                                (double) defaultInitialTargetSpeedInchesPerSecond
                                        * (double) xmlResourceParser.getAttributeFloatValue(
                                                null
                                                , ATTRIBUTE_NAME_PROFILE_INITIAL_TARGET_SPEED_MULTIPLIER
                                                , 1.0f);
                        double maximumTargetSpeedInchesPerSecond =
                                (double) defaultInitialTargetSpeedInchesPerSecond
                                        * (double) xmlResourceParser.getAttributeFloatValue(
                                                null
                                                , ATTRIBUTE_NAME_PROFILE_MAXIMUM_TARGET_SPEED_MULTIPLIER
                                                , defaultMaximumTargetSpeedMultiplier);

                        // Get speed change attributes
                        double maximumTargetSpeedChangeInchesPerSecondPerSecond =
                                (double) defaultMaximumTargetSpeedChangeInchesPerSecondPerSecond
                                        * (double) xmlResourceParser.getAttributeFloatValue(
                                                null
                                                , ATTRIBUTE_NAME_PROFILE_MAXIMUM_TARGET_SPEED_CHANGE_MULTIPLIER
                                                , 1.0f);

                        // Get the DRadius attributes
                        double maximumTargetDRadiusInchesPerSecond =
                                (double) defaultMaximumTargetDRadiusInchesPerSecond
                                        * (double) xmlResourceParser.getAttributeFloatValue(
                                                null
                                                , ATTRIBUTE_NAME_PROFILE_MAXIMUM_TARGET_DRADIUS_MULTIPLIER
                                                , 1.0f);

                        // Get the DRadius Change attributes
                        double maximumTargetDRadiusChangeInchesPerSecondPerSecond =
                                (double) defaultMaximumTargetDRadiusChangeInchesPerSecondPerSecond
                                        * (double) xmlResourceParser.getAttributeFloatValue(
                                                null
                                                , ATTRIBUTE_NAME_PROFILE_MAXIMUM_TARGET_DRADIUS_CHANGE_MULTIPLIER
                                                , 1.0f);

                        // Get the direction angle change attributes
                        double maximumTargetDirectionAngleChangeRadiansPerSecond =
                                (double) defaultMaximumTargetDirectionAngleChangeRadiansPerSecond
                                        * (double) xmlResourceParser.getAttributeFloatValue(
                                            null
                                            , ATTRIBUTE_NAME_PROFILE_MAXIMUM_TARGET_DIRECTION_ANGLE_CHANGE_MULTIPLIER
                                            , 1.0f);

                        // Get the probability attributes
                        double probabilityOfRandomPositionChangePerSecond =
                                (double) xmlResourceParser.getAttributeFloatValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_PROBABILITY_OF_RANDOM_POSITION_CHANGE_PER_SECOND
                                        , defaultProbabilityOfRandomPositionChangePerSecond);
                        double probabilityOfRandomDirectionChangePerSecond =
                                (double) xmlResourceParser.getAttributeFloatValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_PROBABILITY_OF_RANDOM_DIRECTION_CHANGE_PER_SECOND
                                        , defaultProbabilityOfRandomDirectionChangePerSecond);
                        double probabilityOfRandomRadiusChangePerSecond =
                                (double) xmlResourceParser.getAttributeFloatValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_PROBABILITY_OF_RANDOM_RADIUS_CHANGE_PER_SECOND
                                        , defaultProbabilityOfRandomRadiusChangePerSecond);
                        double probabilityOfRandomDRadiusChangePerSecond =
                                (double) xmlResourceParser.getAttributeFloatValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_PROBABILITY_OF_RANDOM_DRADIUS_CHANGE_PER_SECOND
                                        , defaultProbabilityOfRandomDRadiusChangePerSecond);
                        double probabilityOfRandomSpeedChangePerSecond =
                                (double) xmlResourceParser.getAttributeFloatValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_PROBABILITY_OF_RANDOM_SPEED_CHANGE_PER_SECOND
                                        , defaultProbabilityOfRandomSpeedChangePerSecond);

                        boolean canChangePosition =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TARGET_CAN_CHANGE_POSITION
                                        , defaultCanChangePosition);
                        boolean canChangeSpeed =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TARGET_CAN_CHANGE_SPEED
                                        , defaultCanChangeSpeed);
                        boolean canChangeDirection =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TARGET_CAN_CHANGE_DIRECTION
                                        , defaultCanChangeDirection);
                        boolean canChangeRadius =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TARGET_CAN_CHANGE_RADIUS
                                        , defaultCanChangeRadius);
                        boolean canChangeDRadius =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TARGET_CAN_CHANGE_DRADIUS
                                        , defaultCanChangeDRadius);

                        boolean canRandomlyChangePosition =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TARGET_CAN_RANDOMLY_CHANGE_POSITION
                                        , defaultCanRandomlyChangePosition);
                        boolean canRandomlyChangeSpeed =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TARGET_CAN_RANDOMLY_CHANGE_SPEED
                                        , defaultCanRandomlyChangeSpeed);
                        boolean canRandomlyChangeDirection =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TARGET_CAN_RANDOMLY_CHANGE_DIRECTION
                                        , defaultCanRandomlyChangeDirection);
                        boolean canRandomlyChangeRadius =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TARGET_CAN_RANDOMLY_CHANGE_RADIUS
                                        , defaultCanRandomlyChangeRadius);
                        boolean canRandomlyChangeDRadius =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TARGET_CAN_RANDOMLY_CHANGE_DRADIUS
                                        , defaultCanRandomlyChangeDRadius);

                        // Get the transition value
                        double scriptTransitionValue =
                                (double) xmlResourceParser.getAttributeFloatValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TRANSITION_VALUE
                                        , defaultScriptTransitionValue);

                        // Get the transition continuity values
                        String transitionContinuityPositionString =
                                (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_POSITION) == null) ?
                                    defaultScriptTransitionContinuity
                                    : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_POSITION);
                        String transitionContinuitySpeedString =
                                (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_SPEED) == null) ?
                                    defaultScriptTransitionContinuity
                                    : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_SPEED);
                        String transitionContinuityDirectionString =
                                (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_DIRECTION) == null) ?
                                    defaultScriptTransitionContinuity
                                    : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_DIRECTION);
                        String transitionContinuitySpeedChangeString =
                                (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_SPEED_CHANGE) == null) ?
                                    defaultScriptTransitionContinuity
                                    : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_SPEED_CHANGE);
                        String transitionContinuityDirectionChangeString =
                                (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_DIRECTION_CHANGE) == null) ?
                                    defaultScriptTransitionContinuity
                                    : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_DIRECTION_CHANGE);
                        String transitionContinuityRadiusString =
                                (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_RADIUS) == null) ?
                                    defaultScriptTransitionContinuity
                                    : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_RADIUS);
                        String transitionContinuityDRadiusString =
                                (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_DRADIUS) == null) ?
                                    defaultScriptTransitionContinuity
                                    : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_DRADIUS);
                        String transitionContinuityDRadiusChangeString =
                                (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_DRADIUS_CHANGE) == null) ?
                                    defaultScriptTransitionContinuity
                                    : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_DRADIUS_CHANGE);

                        ClickTargetProfile.TRANSITION_CONTINUITY transitionContinuityPosition =
                                ClickTargetProfile.TRANSITION_CONTINUITY.values()[
                                        scriptTransitionContinuityValues.indexOf(transitionContinuityPositionString)];
                        ClickTargetProfile.TRANSITION_CONTINUITY transitionContinuitySpeed =
                                ClickTargetProfile.TRANSITION_CONTINUITY.values()[
                                        scriptTransitionContinuityValues.indexOf(transitionContinuitySpeedString)];
                        ClickTargetProfile.TRANSITION_CONTINUITY transitionContinuityDirection =
                                ClickTargetProfile.TRANSITION_CONTINUITY.values()[
                                        scriptTransitionContinuityValues.indexOf(transitionContinuityDirectionString)];
                        ClickTargetProfile.TRANSITION_CONTINUITY transitionContinuitySpeedChange =
                                ClickTargetProfile.TRANSITION_CONTINUITY.values()[
                                        scriptTransitionContinuityValues.indexOf(transitionContinuitySpeedChangeString)];
                        ClickTargetProfile.TRANSITION_CONTINUITY transitionContinuityDirectionChange =
                                ClickTargetProfile.TRANSITION_CONTINUITY.values()[
                                        scriptTransitionContinuityValues.indexOf(transitionContinuityDirectionChangeString)];
                        ClickTargetProfile.TRANSITION_CONTINUITY transitionContinuityRadius =
                                ClickTargetProfile.TRANSITION_CONTINUITY.values()[
                                        scriptTransitionContinuityValues.indexOf(transitionContinuityRadiusString)];
                        ClickTargetProfile.TRANSITION_CONTINUITY transitionContinuityDRadius =
                                ClickTargetProfile.TRANSITION_CONTINUITY.values()[
                                        scriptTransitionContinuityValues.indexOf(transitionContinuityDRadiusString)];
                        ClickTargetProfile.TRANSITION_CONTINUITY transitionContinuityDRadiusChange =
                                ClickTargetProfile.TRANSITION_CONTINUITY.values()[
                                        scriptTransitionContinuityValues.indexOf(transitionContinuityDRadiusChangeString)];


                        // Create a new click target profile
                        ClickTargetProfile currentClickTargetProfile = new ClickTargetProfile(
                                 initialTargetRadiusInches
                                , minimumTargetRadiusInches
                                , initialTargetSpeedInchesPerSecond
                                , maximumTargetSpeedInchesPerSecond
                                , maximumTargetSpeedChangeInchesPerSecondPerSecond
                                , maximumTargetDRadiusInchesPerSecond
                                , maximumTargetDRadiusChangeInchesPerSecondPerSecond
                                , maximumTargetDirectionAngleChangeRadiansPerSecond

                                , probabilityOfRandomPositionChangePerSecond
                                , probabilityOfRandomDirectionChangePerSecond
                                , probabilityOfRandomRadiusChangePerSecond
                                , probabilityOfRandomDRadiusChangePerSecond
                                , probabilityOfRandomSpeedChangePerSecond

                                , canChangePosition
                                , canChangeSpeed
                                , canChangeDirection
                                , canChangeRadius
                                , canChangeDRadius

                                , canRandomlyChangePosition
                                , canRandomlyChangeSpeed
                                , canRandomlyChangeDirection
                                , canRandomlyChangeRadius
                                , canRandomlyChangeDRadius

                                , transitionContinuityPosition
                                , transitionContinuitySpeed
                                , transitionContinuityDirection
                                , transitionContinuitySpeedChange
                                , transitionContinuityDirectionChange
                                , transitionContinuityRadius
                                , transitionContinuityDRadius
                                , transitionContinuityDRadiusChange);

                        // Add the current click target profile to the click target profile list
                        arrayListTransitionValue.add(scriptTransitionValue);
                        arrayListClickTargetProfile.add(currentClickTargetProfile);

                    }

                } else if (currentXmlEvent == XmlPullParser.END_TAG) {

                    // Check if this is the end tag for a click target profile script
                    // Check if we found the start of a click target profile script
                    if (xmlResourceParser.getName().contentEquals(NODE_NAME_CLICK_TARGET_PROFILE_SCRIPT)) {

                        // Check if we already found the game level, and this is the closing tag for its script
                        if (foundCurrentLevelClickTargetProfileScript == true) {

                            // Break out of the while loop
                            break;

                        }

                    }

                }

                // Get the type of the next event
                currentXmlEvent = xmlResourceParser.next();

            }

            //Release resources associated with the parser
            xmlResourceParser.close();

        } catch (XmlPullParserException e) {
            int blah = 0;
        } catch (IOException e) {
            int blah = 0;
        } catch (Exception e) {
            int blah = 0;
        }

        ClickTargetProfileScript.SCRIPT_TRANSITION_MODE scriptTransitionMode =
                ClickTargetProfileScript.SCRIPT_TRANSITION_MODE.values()[
                scriptTransitionModeValues.indexOf(scriptTransitionModeString)];
        ClickTargetProfileScript.SCRIPT_TRANSITION_INTERVAL scriptTransitionInterval =
                ClickTargetProfileScript.SCRIPT_TRANSITION_INTERVAL.values()[
                        scriptTransitionIntervalValues.indexOf(scriptTransitionIntervalString)];
        ClickTargetProfileScript.SCRIPT_CYCLE_DIRECTION scriptCycleDirection =
                ClickTargetProfileScript.SCRIPT_CYCLE_DIRECTION.values()[
                        scriptCycleDirectionValues.indexOf(scriptCycleDirectionString)];

        resultClickTargetProfileScript = new ClickTargetProfileScript(
                scriptTransitionMode
                , scriptTransitionInterval
                , scriptCycleDirection
                , arrayListTransitionValue
                , arrayListClickTargetProfile
                , initialClickTargetProfile);

        return resultClickTargetProfileScript;

    }

}
