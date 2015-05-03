package com.delsquared.lightningdots.game;

import android.content.Context;
import android.content.res.XmlResourceParser;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.utilities.PositionEvolver;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

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

    private static final String ATTRIBUTE_NAME_PROFILE_INITIAL_TARGET_RADIUS_MULTIPLIER = "initialTargetRadiusMultiplier";
    private static final String ATTRIBUTE_NAME_PROFILE_MINIMUM_TARGET_RADIUS_MULTIPLIER = "minimumTargetRadiusMultiplier";
    private static final String ATTRIBUTE_NAME_PROFILE_MAXIMUM_TARGET_RADIUS_MULTIPLIER = "maximumTargetRadiusMultiplier";
    private static final String ATTRIBUTE_NAME_PROFILE_RANDOM_INITIAL_TARGET_RADIUS = "randomInitialTargetRadius";

    private static final String ATTRIBUTE_NAME_PROFILE_INITIAL_TARGET_SPEED_MULTIPLIER = "initialTargetSpeedMultiplier";
    private static final String ATTRIBUTE_NAME_PROFILE_MINIMUM_TARGET_SPEED_MULTIPLIER = "minimumTargetSpeedMultiplier";
    private static final String ATTRIBUTE_NAME_PROFILE_MAXIMUM_TARGET_SPEED_MULTIPLIER = "maximumTargetSpeedMultiplier";
    private static final String ATTRIBUTE_NAME_PROFILE_RANDOM_INITIAL_TARGET_SPEED = "randomInitialTargetSpeed";
    private static final String ATTRIBUTE_NAME_PROFILE_INITIAL_TARGET_SPEED_CHANGE_ABSOLUTE_VALUE_MULTIPLIER = "initialTargetSpeedChangeAbsoluteValueMultiplier";
    private static final String ATTRIBUTE_NAME_PROFILE_MINIMUM_TARGET_SPEED_CHANGE_ABSOLUTE_VALUE_MULTIPLIER = "minimumTargetSpeedChangeAbsoluteValueMultiplier";
    private static final String ATTRIBUTE_NAME_PROFILE_MAXIMUM_TARGET_SPEED_CHANGE_ABSOLUTE_VALUE_MULTIPLIER = "maximumTargetSpeedChangeAbsoluteValueMultiplier";
    private static final String ATTRIBUTE_NAME_PROFILE_RANDOM_INITIAL_TARGET_SPEED_CHANGE = "randomInitialTargetSpeedChange";
    private static final String ATTRIBUTE_NAME_PROFILE_RANDOM_INITIAL_TARGET_SPEED_CHANGE_SIGN = "randomInitialTargetSpeedChangeSign";

    private static final String ATTRIBUTE_NAME_PROFILE_INITIAL_TARGET_DRADIUS_ABSOLUTE_VALUE_MULTIPLIER = "initialTargetDRadiusAbsoluteValueMultiplier";
    private static final String ATTRIBUTE_NAME_PROFILE_MINIMUM_TARGET_DRADIUS_ABSOLUTE_VALUE_MULTIPLIER = "minimumTargetDRadiusAbsoluteValueMultiplier";
    private static final String ATTRIBUTE_NAME_PROFILE_MAXIMUM_TARGET_DRADIUS_ABSOLUTE_VALUE_MULTIPLIER = "maximumTargetDRadiusAbsoluteValueMultiplier";
    private static final String ATTRIBUTE_NAME_PROFILE_RANDOM_INITIAL_TARGET_DRADIUS = "randomInitialTargetDRadius";
    private static final String ATTRIBUTE_NAME_PROFILE_RANDOM_INITIAL_TARGET_DRADIUS_SIGN = "randomInitialTargetDRadiusSign";
    private static final String ATTRIBUTE_NAME_PROFILE_INITIAL_TARGET_DRADIUS_CHANGE_ABSOLUTE_VALUE_MULTIPLIER = "initialTargetDRadiusChangeAbsoluteValueMultiplier";
    private static final String ATTRIBUTE_NAME_PROFILE_MINIMUM_TARGET_DRADIUS_CHANGE_ABSOLUTE_VALUE_MULTIPLIER = "minimumTargetDRadiusChangeAbsoluteValueMultiplier";
    private static final String ATTRIBUTE_NAME_PROFILE_MAXIMUM_TARGET_DRADIUS_CHANGE_ABSOLUTE_VALUE_MULTIPLIER = "maximumTargetDRadiusChangeAbsoluteValueMultiplier";
    private static final String ATTRIBUTE_NAME_PROFILE_RANDOM_INITIAL_TARGET_DRADIUS_CHANGE = "randomInitialTargetDRadiusChange";
    private static final String ATTRIBUTE_NAME_PROFILE_RANDOM_INITIAL_TARGET_DRADIUS_CHANGE_SIGN = "randomInitialTargetDRadiusChangeSign";

    private static final String ATTRIBUTE_NAME_PROFILE_INITIAL_TARGET_DIRECTION_ANGLE_RADIANS = "initialTargetDirectionAngleRadians";
    private static final String ATTRIBUTE_NAME_PROFILE_RANDOM_INITIAL_TARGET_DIRECTION_ANGLE = "randomInitialTargetDirectionAngle";

    private static final String ATTRIBUTE_NAME_PROFILE_INITIAL_TARGET_DIRECTION_ANGLE_CHANGE_ABSOLUTE_VALUE_MULTIPLIER = "initialTargetDirectionAngleChangeAbsoluteValueMultiplier";
    private static final String ATTRIBUTE_NAME_PROFILE_MINIMUM_TARGET_DIRECTION_ANGLE_CHANGE_ABSOLUTE_VALUE_MULTIPLIER = "minimumTargetDirectionAngleChangeAbsoluteValueMultiplier";
    private static final String ATTRIBUTE_NAME_PROFILE_MAXIMUM_TARGET_DIRECTION_ANGLE_CHANGE_ABSOLUTE_VALUE_MULTIPLIER = "maximumTargetDirectionAngleChangeAbsoluteValueMultiplier";
    private static final String ATTRIBUTE_NAME_PROFILE_RANDOM_INITIAL_TARGET_DIRECTION_ANGLE_CHANGE = "randomInitialTargetDirectionAngleChange";
    private static final String ATTRIBUTE_NAME_PROFILE_RANDOM_INITIAL_TARGET_DIRECTION_ANGLE_CHANGE_SIGN = "randomInitialTargetDirectionAngleChangeSign";

    private static final String ATTRIBUTE_NAME_PROFILE_PROBABILITY_OF_RANDOM_POSITION_CHANGE_PER_SECOND = "probabilityOfRandomPositionChangePerSecond";
    private static final String ATTRIBUTE_NAME_PROFILE_PROBABILITY_OF_RANDOM_SPEED_CHANGE_PER_SECOND = "probabilityOfRandomSpeedChangePerSecond";
    private static final String ATTRIBUTE_NAME_PROFILE_PROBABILITY_OF_RANDOM_DIRECTION_CHANGE_PER_SECOND = "probabilityOfRandomDirectionChangePerSecond";
    private static final String ATTRIBUTE_NAME_PROFILE_PROBABILITY_OF_RANDOM_DSPEED_CHANGE_PER_SECOND = "probabilityOfRandomDSpeedChangePerSecond";
    private static final String ATTRIBUTE_NAME_PROFILE_PROBABILITY_OF_RANDOM_DDIRECTION_CHANGE_PER_SECOND = "probabilityOfRandomDDirectionChangePerSecond";
    private static final String ATTRIBUTE_NAME_PROFILE_PROBABILITY_OF_RANDOM_RADIUS_CHANGE_PER_SECOND = "probabilityOfRandomRadiusChangePerSecond";
    private static final String ATTRIBUTE_NAME_PROFILE_PROBABILITY_OF_RANDOM_DRADIUS_CHANGE_PER_SECOND = "probabilityOfRandomDRadiusChangePerSecond";
    private static final String ATTRIBUTE_NAME_PROFILE_PROBABILITY_OF_RANDOM_D2RADIUS_CHANGE_PER_SECOND = "probabilityOfRandomD2RadiusChangePerSecond";

    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_CAN_CHANGE_POSITION = "targetCanChangePosition";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_CAN_CHANGE_SPEED = "targetCanChangeSpeed";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_CAN_CHANGE_DIRECTION = "targetCanChangeDirection";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_CAN_CHANGE_RADIUS = "targetCanChangeRadius";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_CAN_CHANGE_DRADIUS = "targetCanChangeDRadius";

    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_CAN_RANDOMLY_CHANGE_POSITION = "targetCanRandomlyChangePosition";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_CAN_RANDOMLY_CHANGE_SPEED = "targetCanRandomlyChangeSpeed";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_CAN_RANDOMLY_CHANGE_DIRECTION = "targetCanRandomlyChangeDirection";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_CAN_RANDOMLY_CHANGE_DSPEED = "targetCanRandomlyChangeDSpeed";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_CAN_RANDOMLY_CHANGE_DDIRECTION = "targetCanRandomlyChangeDDirection";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_CAN_RANDOMLY_CHANGE_RADIUS = "targetCanRandomlyChangeRadius";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_CAN_RANDOMLY_CHANGE_DRADIUS = "targetCanRandomlyChangeDRadius";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_CAN_RANDOMLY_CHANGE_D2RADIUS = "targetCanRandomlyChangeD2Radius";


    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_POSITION_CHANGE_TO_RANDOM_SPEED_CHANGE = "targetTieRandomPositionChangeToRandomSpeedChange";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_POSITION_CHANGE_TO_RANDOM_DIRECTION_CHANGE = "targetTieRandomPositionChangeToRandomDirectionChange";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_SPEED_CHANGE_TO_RANDOM_DIRECTION_CHANGE = "targetTieRandomSpeedChangeToRandomDirectionChange";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_SPEED_CHANGE_TO_RANDOM_DSPEED_CHANGE = "targetTieRandomSpeedChangeToRandomDSpeedChange";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_SPEED_CHANGE_TO_RANDOM_DDIRECTION_CHANGE = "targetTieRandomSpeedChangeToRandomDDirectionChange";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_DIRECTION_CHANGE_TO_RANDOM_SPEED_CHANGE = "targetTieRandomDirectionChangeToRandomSpeedChange";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_DIRECTION_CHANGE_TO_RANDOM_DSPEED_CHANGE = "targetTieRandomDirectionChangeToRandomDSpeedChange";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_DIRECTION_CHANGE_TO_RANDOM_DDIRECTION_CHANGE = "targetTieRandomDirectionChangeToRandomDDirectionChange";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_RADIUS_CHANGE_TO_RANDOM_DRADIUS_CHANGE = "targetTieRandomRadiusChangeToRandomDRadiusChange";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_DRADIUS_CHANGE_TO_RANDOM_D2RADIUS_CHANGE = "targetTieRandomDRadiusChangeToRandomD2RadiusChange";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_RADIUS_CHANGE_TO_RANDOM_POSITION_CHANGE = "targetTieRandomRadiusChangeToRandomPositionChange";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_RADIUS_CHANGE_TO_RANDOM_SPEED_CHANGE = "targetTieRandomRadiusChangeToRandomSpeedChange";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_RADIUS_CHANGE_TO_RANDOM_DIRECTION_CHANGE = "targetTieRandomRadiusChangeToRandomDirectionChange";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_DRADIUS_CHANGE_TO_RANDOM_SPEED_CHANGE = "targetTieRandomDRadiusChangeToRandomSpeedChange";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_DRADIUS_CHANGE_TO_RANDOM_DIRECTION_CHANGE = "targetTieRandomDRadiusChangeToRandomDirectionChange";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_DRADIUS_CHANGE_TO_RANDOM_DSPEED_CHANGE = "targetTieRandomDRadiusChangeToRandomDSpeedChange";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_DRADIUS_CHANGE_TO_RANDOM_DDIRECTION_CHANGE = "targetTieRandomDRadiusChangeToRandomDDirectionChange";

    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_BOUNDARY_EFFECT_POSITION_HORIZONTAL = "targetBoundaryEffectPositionHorizontal";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_BOUNDARY_EFFECT_POSITION_VERTICAL = "targetBoundaryEffectPositionVertical";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_BOUNDARY_EFFECT_SPEED = "targetBoundaryEffectSpeed";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_BOUNDARY_EFFECT_DIRECTION = "targetBoundaryEffectDirection";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_BOUNDARY_EFFECT_DSPEED = "targetBoundaryEffectDSpeed";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_BOUNDARY_EFFECT_DDIRECTION = "targetBoundaryEffectDDirection";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_BOUNDARY_EFFECT_RADIUS = "targetBoundaryEffectRadius";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_BOUNDARY_EFFECT_DRADIUS = "targetBoundaryEffectDRadius";
    private static final String ATTRIBUTE_NAME_PROFILE_TARGET_BOUNDARY_EFFECT_D2RADIUS = "targetBoundaryEffectD2Radius";

    private static final String ATTRIBUTE_NAME_PROFILE_TRANSITION_VALUE = "scriptTransitionValue";
    private static final String ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_POSITION = "scriptTransitionContinuousPosition";
    private static final String ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_SPEED = "scriptTransitionContinuousSpeed";
    private static final String ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_DIRECTION = "scriptTransitionContinuousDirection";
    private static final String ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_SPEED_CHANGE = "scriptTransitionContinuousSpeedChange";
    private static final String ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_DIRECTION_CHANGE = "scriptTransitionContinuousDirectionChange";
    private static final String ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_RADIUS = "scriptTransitionContinuousRadius";
    private static final String ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_DRADIUS = "scriptTransitionContinuousDRadius";
    private static final String ATTRIBUTE_NAME_PROFILE_TRANSITION_CONTINUOUS_DRADIUS_CHANGE = "scriptTransitionContinuousDRadiusChange";

    private static final ArrayList<String> boundaryEffectValues = new ArrayList<String>(){{
            add("STICK");
            add("BOUNCE");
            add("PERIODIC");
            add("PERIODIC_REFLECTIVE");
    }};

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

        // Radius
        float defaultInitialTargetRadiusInches = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultInitialTargetRadiusInches);
        float defaultMinimumTargetRadiusInchesMultiplier = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultMinimumTargetRadiusInchesMultiplier);
        float defaultMaximumTargetRadiusInchesMultiplier = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultMaximumTargetRadiusInchesMultiplier);
        boolean defaultRandomInitialTargetRadius = context.getResources().getBoolean(R.bool.game_values_defaultRandomInitialTargetRadius);

        // Speed
        float defaultInitialTargetSpeedInchesPerSecond = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultInitialTargetSpeedInchesPerSecond);
        float defaultMinimumTargetSpeedMultiplier = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultMinimumTargetSpeedMultiplier);
        float defaultMaximumTargetSpeedMultiplier = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultMaximumTargetSpeedMultiplier);
        boolean defaultRandomInitialTargetSpeed = context.getResources().getBoolean(R.bool.game_values_defaultRandomInitialTargetSpeed);
        float defaultInitialTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultInitialTargetSpeedChangeAbsoluteValueInchesPerSecond);
        float defaultMinimumTargetSpeedChangeAbsoluteValueMultiplier = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultMinimumTargetSpeedChangeAbsoluteValueMultiplier);
        float defaultMaximumTargetSpeedChangeAbsoluteValueMultiplier = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultMaximumTargetSpeedChangeAbsoluteValueMultiplier);
        boolean defaultRandomInitialTargetSpeedChange = context.getResources().getBoolean(R.bool.game_values_defaultRandomInitialTargetSpeedChange);
        boolean defaultRandomInitialTargetSpeedChangeSign = context.getResources().getBoolean(R.bool.game_values_defaultRandomInitialTargetSpeedChangeSign);

        // Radius change
        float defaultInitialTargetDRadiusAbsoluteValueInchesPerSecond = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultInitialTargetDRadiusAbsoluteValueInchesPerSecond);
        float defaultMinimumTargetDRadiusAbsoluteValueMultiplier = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultMinimumTargetDRadiusAbsoluteValueMultiplier);
        float defaultMaximumTargetDRadiusAbsoluteValueMultiplier = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultMaximumTargetDRadiusAbsoluteValueMultiplier);
        boolean defaultRandomInitialTargetDRadius = context.getResources().getBoolean(R.bool.game_values_defaultRandomInitialTargetDRadius);
        boolean defaultRandomInitialTargetDRadiusSign = context.getResources().getBoolean(R.bool.game_values_defaultRandomInitialTargetDRadiusSign);
        float defaultInitialTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultInitialTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond);
        float defaultMinimumTargetDRadiusChangeAbsoluteValueMultiplier = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultMinimumTargetDRadiusChangeAbsoluteValueMultiplier);
        float defaultMaximumTargetDRadiusChangeAbsoluteValueMultiplier = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultMaximumTargetDRadiusChangeAbsoluteValueMultiplier);
        boolean defaultRandomInitialTargetDRadiusChange = context.getResources().getBoolean(R.bool.game_values_defaultRandomInitialTargetDRadiusChange);
        boolean defaultRandomInitialTargetDRadiusChangeSign = context.getResources().getBoolean(R.bool.game_values_defaultRandomInitialTargetDRadiusChangeSign);

        // Direction angle
        float defaultInitialTargetDirectionAngleRadians = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultInitialTargetDirectionAngleRadians);
        boolean defaultRandomInitialTargetDirectionAngle = context.getResources().getBoolean(R.bool.game_values_defaultRandomInitialTargetDirectionAngle);

        // Direction angle change
        float defaultInitialTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultInitialTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond);
        float defaultMinimumTargetDirectionAngleChangeAbsoluteValueMultiplier = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultMinimumTargetDirectionAngleChangeAbsoluteValueMultiplier);
        float defaultMaximumTargetDirectionAngleChangeAbsoluteValueMultiplier = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultMaximumTargetDirectionAngleChangeAbsoluteValueMultiplier);
        boolean defaultRandomInitialTargetDirectionAngleChange = context.getResources().getBoolean(R.bool.game_values_defaultRandomInitialTargetDirectionAngleChange);
        boolean defaultRandomInitialTargetDirectionAngleChangeSign = context.getResources().getBoolean(R.bool.game_values_defaultRandomInitialTargetDirectionAngleChangeSign);

        // Random change probabilities
        float defaultProbabilityOfRandomPositionChangePerSecond = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultProbabilityOfRandomPositionChangePerSecond);
        float defaultProbabilityOfRandomSpeedChangePerSecond = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultProbabilityOfRandomSpeedChangePerSecond);
        float defaultProbabilityOfRandomDirectionChangePerSecond = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultProbabilityOfRandomDirectionChangePerSecond);
        float defaultProbabilityOfRandomDSpeedChangePerSecond = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultProbabilityOfRandomDSpeedChangePerSecond);
        float defaultProbabilityOfRandomDDirectionChangePerSecond = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultProbabilityOfRandomDDirectionChangePerSecond);
        float defaultProbabilityOfRandomRadiusChangePerSecond = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultProbabilityOfRandomRadiusChangePerSecond);
        float defaultProbabilityOfRandomDRadiusChangePerSecond = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultProbabilityOfRandomDRadiusChangePerSecond);
        float defaultProbabilityOfRandomD2RadiusChangePerSecond = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultProbabilityOfRandomD2RadiusChangePerSecond);

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
        boolean defaultCanRandomlyChangeDSpeed = context.getResources().getBoolean(R.bool.game_values_defaultCanRandomlyChangeDSpeed);
        boolean defaultCanRandomlyChangeDDirection = context.getResources().getBoolean(R.bool.game_values_defaultCanRandomlyChangeDDirection);
        boolean defaultCanRandomlyChangeRadius = context.getResources().getBoolean(R.bool.game_values_defaultCanRandomlyChangeRadius);
        boolean defaultCanRandomlyChangeDRadius = context.getResources().getBoolean(R.bool.game_values_defaultCanRandomlyChangeDRadius);
        boolean defaultCanRandomlyChangeD2Radius = context.getResources().getBoolean(R.bool.game_values_defaultCanRandomlyChangeD2Radius);

        // Tie random changes to other random changes
        boolean defaultTieRandomPositionChangeToRandomSpeedChange = context.getResources().getBoolean(R.bool.game_values_defaultTieRandomPositionChangeToRandomSpeedChange);
        boolean defaultTieRandomPositionChangeToRandomDirectionChange = context.getResources().getBoolean(R.bool.game_values_defaultTieRandomPositionChangeToRandomDirectionChange);
        boolean defaultTieRandomSpeedChangeToRandomDirectionChange = context.getResources().getBoolean(R.bool.game_values_defaultTieRandomSpeedChangeToRandomDirectionChange);
        boolean defaultTieRandomSpeedChangeToRandomDSpeedChange = context.getResources().getBoolean(R.bool.game_values_defaultTieRandomSpeedChangeToRandomDSpeedChange);
        boolean defaultTieRandomSpeedChangeToRandomDDirectionChange = context.getResources().getBoolean(R.bool.game_values_defaultTieRandomSpeedChangeToRandomDDirectionChange);
        boolean defaultTieRandomDirectionChangeToRandomSpeedChange = context.getResources().getBoolean(R.bool.game_values_defaultTieRandomDirectionChangeToRandomSpeedChange);
        boolean defaultTieRandomDirectionChangeToRandomDSpeedChange = context.getResources().getBoolean(R.bool.game_values_defaultTieRandomDirectionChangeToRandomDSpeedChange);
        boolean defaultTieRandomDirectionChangeToRandomDDirectionChange = context.getResources().getBoolean(R.bool.game_values_defaultTieRandomDirectionChangeToRandomDDirectionChange);
        boolean defaultTieRandomRadiusChangeToRandomDRadiusChange = context.getResources().getBoolean(R.bool.game_values_defaultTieRandomRadiusChangeToRandomDRadiusChange);
        boolean defaultTieRandomDRadiusChangeToRandomD2RadiusChange = context.getResources().getBoolean(R.bool.game_values_defaultTieRandomDRadiusChangeToRandomD2RadiusChange);
        boolean defaultTieRandomRadiusChangeToRandomPositionChange = context.getResources().getBoolean(R.bool.game_values_defaultTieRandomRadiusChangeToRandomPositionChange);
        boolean defaultTieRandomRadiusChangeToRandomSpeedChange = context.getResources().getBoolean(R.bool.game_values_defaultTieRandomRadiusChangeToRandomSpeedChange);
        boolean defaultTieRandomRadiusChangeToRandomDirectionChange = context.getResources().getBoolean(R.bool.game_values_defaultTieRandomRadiusChangeToRandomDirectionChange);
        boolean defaultTieRandomDRadiusChangeToRandomSpeedChange = context.getResources().getBoolean(R.bool.game_values_defaultTieRandomDRadiusChangeToRandomSpeedChange);
        boolean defaultTieRandomDRadiusChangeToRandomDirectionChange = context.getResources().getBoolean(R.bool.game_values_defaultTieRandomDRadiusChangeToRandomDirectionChange);
        boolean defaultTieRandomDRadiusChangeToRandomDSpeedChange = context.getResources().getBoolean(R.bool.game_values_defaultTieRandomDRadiusChangeToRandomDSpeedChange);
        boolean defaultTieRandomDRadiusChangeToRandomDDirectionChange = context.getResources().getBoolean(R.bool.game_values_defaultTieRandomDRadiusChangeToRandomDDirectionChange);

        // Boundary effects
        String defaultBoundaryEffectPositionHorizontalString = context.getString(R.string.game_values_defaultBoundaryEffectPositionHorizontal);
        String defaultBoundaryEffectPositionVerticalString = context.getString(R.string.game_values_defaultBoundaryEffectPositionVertical);
        String defaultBoundaryEffectSpeedString = context.getString(R.string.game_values_defaultBoundaryEffectSpeed);
        String defaultBoundaryEffectDirectionString = context.getString(R.string.game_values_defaultBoundaryEffectDirection);
        String defaultBoundaryEffectDSpeedString = context.getString(R.string.game_values_defaultBoundaryEffectDSpeed);
        String defaultBoundaryEffectDDirectionString = context.getString(R.string.game_values_defaultBoundaryEffectDDirection);
        String defaultBoundaryEffectRadiusString = context.getString(R.string.game_values_defaultBoundaryEffectRadius);
        String defaultBoundaryEffectDRadiusString = context.getString(R.string.game_values_defaultBoundaryEffectDRadius);
        String defaultBoundaryEffectD2RadiusString = context.getString(R.string.game_values_defaultBoundaryEffectD2Radius);

        // Transition continuity
        String defaultScriptTransitionContinuity = context.getString(R.string.game_values_defaultScriptTransitionContinuity);

        // Transitions
        String defaultScriptTransitionMode = context.getString(R.string.game_values_defaultScriptTransitionMode);
        String defaultScriptTransitionInterval = context.getString(R.string.game_values_defaultScriptTransitionInterval);
        String defaultScriptCycleDirection = context.getString(R.string.game_values_defaultScriptCycleDirection);
        float defaultScriptTransitionValue = UtilityFunctions.getResourceFloatValue(context, R.dimen.game_values_defaultScriptTransitionValue);

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
                            && foundCurrentLevelClickTargetProfileScript) {

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
                        double maximumTargetRadiusInches =
                                (double) defaultInitialTargetRadiusInches
                                        * (double) xmlResourceParser.getAttributeFloatValue(
                                                null
                                                , ATTRIBUTE_NAME_PROFILE_MAXIMUM_TARGET_RADIUS_MULTIPLIER
                                                , defaultMaximumTargetRadiusInchesMultiplier);
                        boolean randomInitialTargetRadius =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_RANDOM_INITIAL_TARGET_RADIUS
                                        , defaultRandomInitialTargetRadius);

                        // Make sure the minimum and maximum values are valid
                        if (minimumTargetRadiusInches > initialTargetRadiusInches) minimumTargetRadiusInches = initialTargetRadiusInches * defaultMinimumTargetRadiusInchesMultiplier;
                        if (maximumTargetRadiusInches < initialTargetRadiusInches) maximumTargetRadiusInches = initialTargetRadiusInches * defaultMaximumTargetRadiusInchesMultiplier;

                        // Get speed attributes
                        double initialTargetSpeedInchesPerSecond =
                                (double) defaultInitialTargetSpeedInchesPerSecond
                                        * (double) xmlResourceParser.getAttributeFloatValue(
                                                null
                                                , ATTRIBUTE_NAME_PROFILE_INITIAL_TARGET_SPEED_MULTIPLIER
                                                , 1.0f);
                        double minimumTargetSpeedInchesPerSecond =
                                (double) defaultInitialTargetSpeedInchesPerSecond
                                        * (double) xmlResourceParser.getAttributeFloatValue(
                                                null
                                                , ATTRIBUTE_NAME_PROFILE_MINIMUM_TARGET_SPEED_MULTIPLIER
                                                , defaultMinimumTargetSpeedMultiplier);
                        double maximumTargetSpeedInchesPerSecond =
                                (double) defaultInitialTargetSpeedInchesPerSecond
                                        * (double) xmlResourceParser.getAttributeFloatValue(
                                                null
                                                , ATTRIBUTE_NAME_PROFILE_MAXIMUM_TARGET_SPEED_MULTIPLIER
                                                , defaultMaximumTargetSpeedMultiplier);
                        boolean randomInitialTargetSpeed =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_RANDOM_INITIAL_TARGET_SPEED
                                        , defaultRandomInitialTargetSpeed);

                        // Make sure the minimum and maximum values are valid
                        if (minimumTargetSpeedInchesPerSecond > initialTargetSpeedInchesPerSecond) minimumTargetSpeedInchesPerSecond = initialTargetSpeedInchesPerSecond * defaultMinimumTargetSpeedMultiplier;
                        if (maximumTargetSpeedInchesPerSecond < initialTargetSpeedInchesPerSecond) maximumTargetSpeedInchesPerSecond = initialTargetSpeedInchesPerSecond * defaultMaximumTargetSpeedMultiplier;

                        // Get speed change attributes
                        double initialTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond =
                                (double) defaultInitialTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond
                                        * (double) xmlResourceParser.getAttributeFloatValue(
                                                null
                                                , ATTRIBUTE_NAME_PROFILE_INITIAL_TARGET_SPEED_CHANGE_ABSOLUTE_VALUE_MULTIPLIER
                                                , 1.0f);
                        double minimumTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond =
                                (double) defaultInitialTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond
                                        * (double) xmlResourceParser.getAttributeFloatValue(
                                                null
                                                , ATTRIBUTE_NAME_PROFILE_MINIMUM_TARGET_SPEED_CHANGE_ABSOLUTE_VALUE_MULTIPLIER
                                                , defaultMinimumTargetSpeedChangeAbsoluteValueMultiplier);
                        double maximumTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond =
                                (double) defaultInitialTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond
                                        * (double) xmlResourceParser.getAttributeFloatValue(
                                                null
                                                , ATTRIBUTE_NAME_PROFILE_MAXIMUM_TARGET_SPEED_CHANGE_ABSOLUTE_VALUE_MULTIPLIER
                                                , defaultMaximumTargetSpeedChangeAbsoluteValueMultiplier);
                        boolean randomInitialTargetSpeedChange =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_RANDOM_INITIAL_TARGET_SPEED_CHANGE
                                        , defaultRandomInitialTargetSpeedChange);
                        boolean randomInitialTargetSpeedChangeSign =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_RANDOM_INITIAL_TARGET_SPEED_CHANGE_SIGN
                                        , defaultRandomInitialTargetSpeedChangeSign);

                        // Make sure the minimum and maximum values are valid
                        if (minimumTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond > initialTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond) minimumTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond = initialTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond * defaultMinimumTargetSpeedChangeAbsoluteValueMultiplier;
                        if (maximumTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond < initialTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond) maximumTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond = initialTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond * defaultMaximumTargetSpeedChangeAbsoluteValueMultiplier;

                        // Get the DRadius attributes
                        double initialTargetDRadiusAbsoluteValueInchesPerSecond =
                                (double) defaultInitialTargetDRadiusAbsoluteValueInchesPerSecond
                                        * (double) xmlResourceParser.getAttributeFloatValue(
                                                null
                                                , ATTRIBUTE_NAME_PROFILE_INITIAL_TARGET_DRADIUS_ABSOLUTE_VALUE_MULTIPLIER
                                                , 1.0f);
                        double minimumTargetDRadiusAbsoluteValueInchesPerSecond =
                                (double) defaultInitialTargetDRadiusAbsoluteValueInchesPerSecond
                                        * (double) xmlResourceParser.getAttributeFloatValue(
                                                null
                                                , ATTRIBUTE_NAME_PROFILE_MINIMUM_TARGET_DRADIUS_ABSOLUTE_VALUE_MULTIPLIER
                                                , defaultMinimumTargetDRadiusAbsoluteValueMultiplier);
                        double maximumTargetDRadiusAbsoluteValueInchesPerSecond =
                                (double) defaultInitialTargetDRadiusAbsoluteValueInchesPerSecond
                                        * (double) xmlResourceParser.getAttributeFloatValue(
                                                null
                                                , ATTRIBUTE_NAME_PROFILE_MAXIMUM_TARGET_DRADIUS_ABSOLUTE_VALUE_MULTIPLIER
                                                , defaultMaximumTargetDRadiusAbsoluteValueMultiplier);
                        boolean randomInitialTargetDRadius =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_RANDOM_INITIAL_TARGET_DRADIUS
                                        , defaultRandomInitialTargetDRadius);
                        boolean randomInitialTargetDRadiusSign =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_RANDOM_INITIAL_TARGET_DRADIUS_SIGN
                                        , defaultRandomInitialTargetDRadiusSign);

                        // Make sure the minimum and maximum values are valid
                        if (minimumTargetDRadiusAbsoluteValueInchesPerSecond > initialTargetDRadiusAbsoluteValueInchesPerSecond) minimumTargetDRadiusAbsoluteValueInchesPerSecond = initialTargetDRadiusAbsoluteValueInchesPerSecond * defaultMinimumTargetDRadiusAbsoluteValueMultiplier;
                        if (maximumTargetDRadiusAbsoluteValueInchesPerSecond < initialTargetDRadiusAbsoluteValueInchesPerSecond) maximumTargetDRadiusAbsoluteValueInchesPerSecond = initialTargetDRadiusAbsoluteValueInchesPerSecond * defaultMaximumTargetDRadiusAbsoluteValueMultiplier;

                        // Get the DRadius Change attributes
                        double initialTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond =
                                (double) defaultInitialTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond
                                        * (double) xmlResourceParser.getAttributeFloatValue(
                                                null
                                                , ATTRIBUTE_NAME_PROFILE_INITIAL_TARGET_DRADIUS_CHANGE_ABSOLUTE_VALUE_MULTIPLIER
                                                , 1.0f);
                        double minimumTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond =
                                (double) defaultInitialTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond
                                        * (double) xmlResourceParser.getAttributeFloatValue(
                                                null
                                                , ATTRIBUTE_NAME_PROFILE_MINIMUM_TARGET_DRADIUS_CHANGE_ABSOLUTE_VALUE_MULTIPLIER
                                                , defaultMinimumTargetDRadiusChangeAbsoluteValueMultiplier);
                        double maximumTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond =
                                (double) defaultInitialTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond
                                        * (double) xmlResourceParser.getAttributeFloatValue(
                                                null
                                                , ATTRIBUTE_NAME_PROFILE_MAXIMUM_TARGET_DRADIUS_CHANGE_ABSOLUTE_VALUE_MULTIPLIER
                                                , defaultMaximumTargetDRadiusChangeAbsoluteValueMultiplier);
                        boolean randomInitialTargetDRadiusChange =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_RANDOM_INITIAL_TARGET_DRADIUS_CHANGE
                                        , defaultRandomInitialTargetDRadiusChange);
                        boolean randomInitialTargetDRadiusChangeSign =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_RANDOM_INITIAL_TARGET_DRADIUS_CHANGE_SIGN
                                        , defaultRandomInitialTargetDRadiusChangeSign);

                        // Make sure the minimum and maximum values are valid
                        if (minimumTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond > initialTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond) minimumTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond = initialTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond * defaultMinimumTargetDRadiusChangeAbsoluteValueMultiplier;
                        if (maximumTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond < initialTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond) maximumTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond = initialTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond * defaultMaximumTargetDRadiusChangeAbsoluteValueMultiplier;


                        // Direction angle
                        double initialTargetDirectionAngleRadians =
                                (double) xmlResourceParser.getAttributeFloatValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_INITIAL_TARGET_DIRECTION_ANGLE_RADIANS
                                        , defaultInitialTargetDirectionAngleRadians);
                        boolean randomInitialTargetDirectionAngle =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_RANDOM_INITIAL_TARGET_DIRECTION_ANGLE
                                        , defaultRandomInitialTargetDirectionAngle);

                        // Direction angle change
                        double initialTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond =
                                defaultInitialTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond
                                        * (double) xmlResourceParser.getAttributeFloatValue(
                                                null
                                                , ATTRIBUTE_NAME_PROFILE_INITIAL_TARGET_DIRECTION_ANGLE_CHANGE_ABSOLUTE_VALUE_MULTIPLIER
                                                , 1.0f);
                        double minimumTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond =
                                defaultInitialTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond
                                        * (double) xmlResourceParser.getAttributeFloatValue(
                                                null
                                                , ATTRIBUTE_NAME_PROFILE_MINIMUM_TARGET_DIRECTION_ANGLE_CHANGE_ABSOLUTE_VALUE_MULTIPLIER
                                                , defaultMinimumTargetDirectionAngleChangeAbsoluteValueMultiplier);
                        double maximumTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond =
                                defaultInitialTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond
                                        * (double) xmlResourceParser.getAttributeFloatValue(
                                                null
                                                , ATTRIBUTE_NAME_PROFILE_MAXIMUM_TARGET_DIRECTION_ANGLE_CHANGE_ABSOLUTE_VALUE_MULTIPLIER
                                                , defaultMaximumTargetDirectionAngleChangeAbsoluteValueMultiplier);
                        boolean randomInitialTargetDirectionAngleChange =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_RANDOM_INITIAL_TARGET_DIRECTION_ANGLE_CHANGE
                                        , defaultRandomInitialTargetDirectionAngleChange);
                        boolean randomInitialTargetDirectionAngleChangeSign =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_RANDOM_INITIAL_TARGET_DIRECTION_ANGLE_CHANGE_SIGN
                                        , defaultRandomInitialTargetDirectionAngleChangeSign);

                        // Make sure the minimum and maximum values are valid
                        if (minimumTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond > initialTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond) minimumTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond = initialTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond * defaultMinimumTargetDirectionAngleChangeAbsoluteValueMultiplier;
                        if (maximumTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond < initialTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond) maximumTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond = initialTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond * defaultMaximumTargetDirectionAngleChangeAbsoluteValueMultiplier;

                        // Get the probability attributes
                        double probabilityOfRandomPositionChangePerSecond =
                                (double) xmlResourceParser.getAttributeFloatValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_PROBABILITY_OF_RANDOM_POSITION_CHANGE_PER_SECOND
                                        , defaultProbabilityOfRandomPositionChangePerSecond);
                        double probabilityOfRandomSpeedChangePerSecond =
                                (double) xmlResourceParser.getAttributeFloatValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_PROBABILITY_OF_RANDOM_SPEED_CHANGE_PER_SECOND
                                        , defaultProbabilityOfRandomSpeedChangePerSecond);
                        double probabilityOfRandomDirectionChangePerSecond =
                                (double) xmlResourceParser.getAttributeFloatValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_PROBABILITY_OF_RANDOM_DIRECTION_CHANGE_PER_SECOND
                                        , defaultProbabilityOfRandomDirectionChangePerSecond);
                        double probabilityOfRandomDSpeedChangePerSecond =
                                (double) xmlResourceParser.getAttributeFloatValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_PROBABILITY_OF_RANDOM_DSPEED_CHANGE_PER_SECOND
                                        , defaultProbabilityOfRandomDSpeedChangePerSecond);
                        double probabilityOfRandomDDirectionChangePerSecond =
                                (double) xmlResourceParser.getAttributeFloatValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_PROBABILITY_OF_RANDOM_DDIRECTION_CHANGE_PER_SECOND
                                        , defaultProbabilityOfRandomDDirectionChangePerSecond);
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
                        double probabilityOfRandomD2RadiusChangePerSecond =
                                (double) xmlResourceParser.getAttributeFloatValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_PROBABILITY_OF_RANDOM_D2RADIUS_CHANGE_PER_SECOND
                                        , defaultProbabilityOfRandomD2RadiusChangePerSecond);


                        // Value changes
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


                        // Random value changes
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
                        boolean canRandomlyChangeDSpeed =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TARGET_CAN_RANDOMLY_CHANGE_DSPEED
                                        , canChangeSpeed);
                        boolean canRandomlyChangeDDirection =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TARGET_CAN_RANDOMLY_CHANGE_DDIRECTION
                                        , canChangeDirection);
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
                        boolean canRandomlyChangeD2Radius =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TARGET_CAN_RANDOMLY_CHANGE_D2RADIUS
                                        , canChangeDRadius);

                        // Tie random changes to other random changes
                        boolean tieRandomPositionChangeToRandomSpeedChange =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_POSITION_CHANGE_TO_RANDOM_SPEED_CHANGE
                                        , defaultTieRandomPositionChangeToRandomSpeedChange);
                        boolean tieRandomPositionChangeToRandomDirectionChange =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_POSITION_CHANGE_TO_RANDOM_DIRECTION_CHANGE
                                        , defaultTieRandomPositionChangeToRandomDirectionChange);
                        boolean tieRandomSpeedChangeToRandomDirectionChange =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_SPEED_CHANGE_TO_RANDOM_DIRECTION_CHANGE
                                        , defaultTieRandomSpeedChangeToRandomDirectionChange);
                        boolean tieRandomSpeedChangeToRandomDSpeedChange =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_SPEED_CHANGE_TO_RANDOM_DSPEED_CHANGE
                                        , defaultTieRandomSpeedChangeToRandomDSpeedChange);
                        boolean tieRandomSpeedChangeToRandomDDirectionChange =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_SPEED_CHANGE_TO_RANDOM_DDIRECTION_CHANGE
                                        , defaultTieRandomSpeedChangeToRandomDDirectionChange);
                        boolean tieRandomDirectionChangeToRandomSpeedChange =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_DIRECTION_CHANGE_TO_RANDOM_SPEED_CHANGE
                                        , defaultTieRandomDirectionChangeToRandomSpeedChange);
                        boolean tieRandomDirectionChangeToRandomDSpeedChange =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_DIRECTION_CHANGE_TO_RANDOM_DSPEED_CHANGE
                                        , defaultTieRandomDirectionChangeToRandomDSpeedChange);
                        boolean tieRandomDirectionChangeToRandomDDirectionChange =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_DIRECTION_CHANGE_TO_RANDOM_DDIRECTION_CHANGE
                                        , defaultTieRandomDirectionChangeToRandomDDirectionChange);
                        boolean tieRandomRadiusChangeToRandomDRadiusChange =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_RADIUS_CHANGE_TO_RANDOM_DRADIUS_CHANGE
                                        , defaultTieRandomRadiusChangeToRandomDRadiusChange);
                        boolean tieRandomDRadiusChangeToRandomD2RadiusChange =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_DRADIUS_CHANGE_TO_RANDOM_D2RADIUS_CHANGE
                                        , defaultTieRandomDRadiusChangeToRandomD2RadiusChange);
                        boolean tieRandomRadiusChangeToRandomPositionChange =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_RADIUS_CHANGE_TO_RANDOM_POSITION_CHANGE
                                        , defaultTieRandomRadiusChangeToRandomPositionChange);
                        boolean tieRandomRadiusChangeToRandomSpeedChange =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_RADIUS_CHANGE_TO_RANDOM_SPEED_CHANGE
                                        , defaultTieRandomRadiusChangeToRandomSpeedChange);
                        boolean tieRandomRadiusChangeToRandomDirectionChange =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_RADIUS_CHANGE_TO_RANDOM_DIRECTION_CHANGE
                                        , defaultTieRandomRadiusChangeToRandomDirectionChange);
                        boolean tieRandomDRadiusChangeToRandomSpeedChange =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_DRADIUS_CHANGE_TO_RANDOM_SPEED_CHANGE
                                        , defaultTieRandomDRadiusChangeToRandomSpeedChange);
                        boolean tieRandomDRadiusChangeToRandomDirectionChange =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_DRADIUS_CHANGE_TO_RANDOM_DIRECTION_CHANGE
                                        , defaultTieRandomDRadiusChangeToRandomDirectionChange);
                        boolean tieRandomDRadiusChangeToRandomDSpeedChange =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_DRADIUS_CHANGE_TO_RANDOM_DSPEED_CHANGE
                                        , defaultTieRandomDRadiusChangeToRandomDSpeedChange);
                        boolean tieRandomDRadiusChangeToRandomDDirectionChange =
                                xmlResourceParser.getAttributeBooleanValue(
                                        null
                                        , ATTRIBUTE_NAME_PROFILE_TARGET_TIE_RANDOM_DRADIUS_CHANGE_TO_RANDOM_DDIRECTION_CHANGE
                                        , defaultTieRandomDRadiusChangeToRandomDDirectionChange);

                        // Get the boundary effect values
                        String boundaryEffectPositionHorizontalString =
                                (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TARGET_BOUNDARY_EFFECT_POSITION_HORIZONTAL) == null) ?
                                        defaultBoundaryEffectPositionHorizontalString
                                        : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TARGET_BOUNDARY_EFFECT_POSITION_HORIZONTAL);
                        String boundaryEffectPositionVerticalString =
                                (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TARGET_BOUNDARY_EFFECT_POSITION_VERTICAL) == null) ?
                                        defaultBoundaryEffectPositionVerticalString
                                        : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TARGET_BOUNDARY_EFFECT_POSITION_VERTICAL);
                        String boundaryEffectSpeedString =
                                (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TARGET_BOUNDARY_EFFECT_SPEED) == null) ?
                                        defaultBoundaryEffectSpeedString
                                        : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TARGET_BOUNDARY_EFFECT_SPEED);
                        String boundaryEffectDirectionString =
                                (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TARGET_BOUNDARY_EFFECT_DIRECTION) == null) ?
                                        defaultBoundaryEffectDirectionString
                                        : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TARGET_BOUNDARY_EFFECT_DIRECTION);
                        String boundaryEffectDSpeedString =
                                (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TARGET_BOUNDARY_EFFECT_DSPEED) == null) ?
                                        defaultBoundaryEffectDSpeedString
                                        : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TARGET_BOUNDARY_EFFECT_DSPEED);
                        String boundaryEffectDDirectionString =
                                (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TARGET_BOUNDARY_EFFECT_DDIRECTION) == null) ?
                                        defaultBoundaryEffectDDirectionString
                                        : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TARGET_BOUNDARY_EFFECT_DDIRECTION);
                        String boundaryEffectRadiusString =
                                (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TARGET_BOUNDARY_EFFECT_RADIUS) == null) ?
                                        defaultBoundaryEffectRadiusString
                                        : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TARGET_BOUNDARY_EFFECT_RADIUS);
                        String boundaryEffectDRadiusString =
                                (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TARGET_BOUNDARY_EFFECT_DRADIUS) == null) ?
                                        defaultBoundaryEffectDRadiusString
                                        : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TARGET_BOUNDARY_EFFECT_DRADIUS);
                        String boundaryEffectD2RadiusString =
                                (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TARGET_BOUNDARY_EFFECT_D2RADIUS) == null) ?
                                        defaultBoundaryEffectD2RadiusString
                                        : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_PROFILE_TARGET_BOUNDARY_EFFECT_D2RADIUS);

                        PositionEvolver.BOUNDARY_EFFECT boundaryEffectPositionHorizontal =
                                PositionEvolver.BOUNDARY_EFFECT.values()[
                                        boundaryEffectValues.indexOf(boundaryEffectPositionHorizontalString)];
                        PositionEvolver.BOUNDARY_EFFECT boundaryEffectPositionVertical =
                                PositionEvolver.BOUNDARY_EFFECT.values()[
                                        boundaryEffectValues.indexOf(boundaryEffectPositionVerticalString)];
                        PositionEvolver.BOUNDARY_EFFECT boundaryEffectSpeed =
                                PositionEvolver.BOUNDARY_EFFECT.values()[
                                        boundaryEffectValues.indexOf(boundaryEffectSpeedString)];
                        PositionEvolver.BOUNDARY_EFFECT boundaryEffectDirection =
                                PositionEvolver.BOUNDARY_EFFECT.values()[
                                        boundaryEffectValues.indexOf(boundaryEffectDirectionString)];
                        PositionEvolver.BOUNDARY_EFFECT boundaryEffectDSpeed =
                                PositionEvolver.BOUNDARY_EFFECT.values()[
                                        boundaryEffectValues.indexOf(boundaryEffectDSpeedString)];
                        PositionEvolver.BOUNDARY_EFFECT boundaryEffectDDirection =
                                PositionEvolver.BOUNDARY_EFFECT.values()[
                                        boundaryEffectValues.indexOf(boundaryEffectDDirectionString)];
                        PositionEvolver.BOUNDARY_EFFECT boundaryEffectRadius =
                                PositionEvolver.BOUNDARY_EFFECT.values()[
                                        boundaryEffectValues.indexOf(boundaryEffectRadiusString)];
                        PositionEvolver.BOUNDARY_EFFECT boundaryEffectDRadius =
                                PositionEvolver.BOUNDARY_EFFECT.values()[
                                        boundaryEffectValues.indexOf(boundaryEffectDRadiusString)];
                        PositionEvolver.BOUNDARY_EFFECT boundaryEffectD2Radius =
                                PositionEvolver.BOUNDARY_EFFECT.values()[
                                        boundaryEffectValues.indexOf(boundaryEffectD2RadiusString)];


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
                                // Target radius values
                                minimumTargetRadiusInches
                                , initialTargetRadiusInches
                                , maximumTargetRadiusInches
                                , randomInitialTargetRadius

                                // Target speed values
                                , minimumTargetSpeedInchesPerSecond
                                , initialTargetSpeedInchesPerSecond
                                , maximumTargetSpeedInchesPerSecond
                                , randomInitialTargetSpeed
                                , minimumTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond
                                , initialTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond
                                , maximumTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond
                                , randomInitialTargetSpeedChange
                                , randomInitialTargetSpeedChangeSign

                                // Target radius change values
                                , minimumTargetDRadiusAbsoluteValueInchesPerSecond
                                , initialTargetDRadiusAbsoluteValueInchesPerSecond
                                , maximumTargetDRadiusAbsoluteValueInchesPerSecond
                                , randomInitialTargetDRadius
                                , randomInitialTargetDRadiusSign
                                , minimumTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond
                                , initialTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond
                                , maximumTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond
                                , randomInitialTargetDRadiusChange
                                , randomInitialTargetDRadiusChangeSign

                                // Target direction values
                                , initialTargetDirectionAngleRadians
                                , randomInitialTargetDirectionAngle

                                // Target direction change values
                                , minimumTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond
                                , initialTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond
                                , maximumTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond
                                , randomInitialTargetDirectionAngleChange
                                , randomInitialTargetDirectionAngleChangeSign

                                // Random change values
                                , probabilityOfRandomPositionChangePerSecond
                                , probabilityOfRandomSpeedChangePerSecond
                                , probabilityOfRandomDirectionChangePerSecond
                                , probabilityOfRandomDSpeedChangePerSecond
                                , probabilityOfRandomDDirectionChangePerSecond
                                , probabilityOfRandomRadiusChangePerSecond
                                , probabilityOfRandomDRadiusChangePerSecond
                                , probabilityOfRandomD2RadiusChangePerSecond

                                // Variable change values
                                , canChangePosition
                                , canChangeSpeed
                                , canChangeDirection
                                , canChangeRadius
                                , canChangeDRadius

                                // Random change booleans
                                , canRandomlyChangePosition
                                , canRandomlyChangeSpeed
                                , canRandomlyChangeDirection
                                , canRandomlyChangeDSpeed
                                , canRandomlyChangeDDirection
                                , canRandomlyChangeRadius
                                , canRandomlyChangeDRadius
                                , canRandomlyChangeD2Radius

                                // Tie random changes to other random changes
                                , tieRandomPositionChangeToRandomSpeedChange
                                , tieRandomPositionChangeToRandomDirectionChange
                                , tieRandomSpeedChangeToRandomDirectionChange
                                , tieRandomSpeedChangeToRandomDSpeedChange
                                , tieRandomSpeedChangeToRandomDDirectionChange
                                , tieRandomDirectionChangeToRandomSpeedChange
                                , tieRandomDirectionChangeToRandomDSpeedChange
                                , tieRandomDirectionChangeToRandomDDirectionChange
                                , tieRandomRadiusChangeToRandomDRadiusChange
                                , tieRandomDRadiusChangeToRandomD2RadiusChange
                                , tieRandomRadiusChangeToRandomPositionChange
                                , tieRandomRadiusChangeToRandomSpeedChange
                                , tieRandomRadiusChangeToRandomDirectionChange
                                , tieRandomDRadiusChangeToRandomSpeedChange
                                , tieRandomDRadiusChangeToRandomDirectionChange
                                , tieRandomDRadiusChangeToRandomDSpeedChange
                                , tieRandomDRadiusChangeToRandomDDirectionChange

                                // Boundary effect values
                                , boundaryEffectPositionHorizontal
                                , boundaryEffectPositionVertical
                                , boundaryEffectSpeed
                                , boundaryEffectDirection
                                , boundaryEffectDSpeed
                                , boundaryEffectDDirection
                                , boundaryEffectRadius
                                , boundaryEffectDRadius
                                , boundaryEffectD2Radius

                                // Script transition values
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
                        if (foundCurrentLevelClickTargetProfileScript) {

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
