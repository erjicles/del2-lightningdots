package com.delsquared.lightningdots.game;


import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import com.delsquared.lightningdots.utilities.PositionEvolver;
import com.delsquared.lightningdots.utilities.PositionVector;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

public class ClickTarget {

    private ClickTargetProfileScript clickTargetProfileScript;
    private PositionEvolver positionEvolverXPixels;
	private PositionEvolver positionEvolverRadiusPixels;

    public ClickTarget() {

        clickTargetProfileScript = new ClickTargetProfileScript();

        positionEvolverXPixels = new PositionEvolver(
                new PositionVector(0.0, 0.0, 0.0)
                , PositionEvolver.MODE.CARTESIAN_2D
                , true
                , true
                , true
                , null
                , 0.0
                , 0.0
                , 0.0
                , 0.0
                , 0.0
                , 0.0
                , false
                , false
                , false
                , false
                , 0.0
                , false
                , false
                , false
                , 0.0
                , 0.0
                , 0.0
                , false
                , false
                , false
                , false
                , false
                , false
                , false
                , PositionEvolver.BOUNDARY_EFFECT.STICK
                , PositionEvolver.BOUNDARY_EFFECT.STICK
                , PositionEvolver.BOUNDARY_EFFECT.STICK);
        positionEvolverRadiusPixels = new PositionEvolver(
                new PositionVector(0.0, 0.0, 0.0)
                , PositionEvolver.MODE.ONE_DIMENSION
                , true
                , true
                , true
                , null
                , 0.0
                , 0.0
                , 0.0
                , 0.0
                , 0.0
                , 0.0
                , false
                , false
                , false
                , false
                , 0.0
                , false
                , false
                , false
                , 0.0
                , 0.0
                , 0.0
                , false
                , false
                , false
                , false
                , false
                , false
                , false
                , PositionEvolver.BOUNDARY_EFFECT.STICK
                , PositionEvolver.BOUNDARY_EFFECT.STICK
                , PositionEvolver.BOUNDARY_EFFECT.STICK);
    }

	public ClickTarget(
            Context context
            , ClickTargetProfileScript clickTargetProfileScript
            , double minimumXPixels
            , double maximumXPixels
            , double minimumYPixels
            , double maximumYPixels)
	{
        this.clickTargetProfileScript = clickTargetProfileScript;

        // Initialize the click target to its current profile
        transitionClickTargetProfile(
                context
                , minimumXPixels
                , maximumXPixels
                , minimumYPixels
                , maximumYPixels
                , true);
	}

    public PositionVector getXPixels() {
        if (positionEvolverXPixels == null)
            return null;
        return positionEvolverXPixels.getX();
    }
    public double getRadiusPixels() {
        if (positionEvolverRadiusPixels == null)
            return 0.0;
        return positionEvolverRadiusPixels.getX().X1;
    }

    public void setBoundsPixels(
            double minimumX
            , double minimumY
            , double maximumX
            , double maximumY) {
        positionEvolverXPixels.setBoundaryValues(
                minimumX
                , minimumY
                , 0.0
                , maximumX
                , maximumY
                , 0.0);
    }

	public void processElapsedTimeMillis(
			Context context
			, long timeElapsedSinceLastUpdateMillis
			, double minimumXPixels
			, double minimumYPixels
			, double maximumXPixels
			, double maximumYPixels) {

		// Get the resources
		Resources resources = context.getResources();

		double timeElapsedSinceLastUpdateSeconds = timeElapsedSinceLastUpdateMillis / 1000.0;

        if (positionEvolverXPixels != null) {

            // Set the new bounds
            setBoundsPixels(
                    minimumXPixels
                    , minimumYPixels
                    , maximumXPixels
                    , maximumYPixels);

            // Evolve the position and radius
            positionEvolverXPixels.evolveTime(timeElapsedSinceLastUpdateSeconds, true, true, true);
            positionEvolverRadiusPixels.evolveTime(timeElapsedSinceLastUpdateSeconds, true, true, true);

        }

        // Process the click target profile transition
        boolean transitionPerformed = clickTargetProfileScript.processTransition(timeElapsedSinceLastUpdateMillis);
        if (transitionPerformed) {

            // Transition the click target to the new profile
            transitionClickTargetProfile(
                    context
                    , minimumXPixels
                    , maximumXPixels
                    , minimumYPixels
                    , maximumYPixels
                    , false);
        }

	}

	public ClickTargetSnapshot getClickTargetSnapshot() {
        PositionVector targetX = new PositionVector();
        PositionVector targetDX = new PositionVector();
        PositionVector targetRadiusX = new PositionVector();
        if (positionEvolverXPixels != null) {
            targetX = positionEvolverXPixels.getX();
            targetDX = positionEvolverXPixels.getDXdt();
        }
        if (positionEvolverRadiusPixels != null) {
            targetRadiusX = positionEvolverRadiusPixels.getX();
        }

		return new ClickTargetSnapshot(
                targetX
                , targetDX
                , targetRadiusX);
	}

    public void transitionClickTargetProfile(
            Context context
            , double minimumPixelsX
            , double maximumPixelsX
            , double minimumPixelsY
            , double maximumPixelsY
            , boolean initializeClickTarget) {

        ClickTargetProfile clickTargetProfile = clickTargetProfileScript.getCurrentClickTargetProfile();

        if (clickTargetProfile == null) {
            return;
        }

        double width = maximumPixelsX - minimumPixelsX;
        double height = maximumPixelsY - minimumPixelsY;

        // Determine continuity
        boolean continuousX = false;
        boolean continuousSpeed = false;
        boolean continuousDirection = false;
        boolean continuousSpeedChange = false;
        boolean continuousDirectionChange = false;
        boolean continuousRadius = false;
        boolean continuousDRadius = false;
        boolean continuousD2Radius = false;

        // If we are not initializing the click target, then determine the continuity of the values
        // (if we are initializing, then all need to be discontinuously initialized)
        if (initializeClickTarget == false) {

            switch (clickTargetProfile.transitionContinuityPosition) {
                case CONTINUOUS:
                    continuousX = true;
                    break;
                case DISCONTINUOUS:
                    continuousX = false;
                    break;
                default:
                    continuousX = clickTargetProfile.canChangePosition;

            }
            switch (clickTargetProfile.transitionContinuitySpeed) {
                case CONTINUOUS:
                    continuousSpeed = true;
                    break;
                case DISCONTINUOUS:
                    continuousSpeed = false;
                    break;
                default:
                    continuousSpeed = clickTargetProfile.canChangeSpeed;
            }
            switch (clickTargetProfile.transitionContinuityDirection) {
                case CONTINUOUS:
                    continuousDirection = true;
                    break;
                case DISCONTINUOUS:
                    continuousDirection = false;
                    break;
                default:
                    continuousDirection = clickTargetProfile.canChangeDirection;
            }
            switch (clickTargetProfile.transitionContinuitySpeedChange) {
                case CONTINUOUS:
                    continuousSpeedChange = true;
                    break;
                case DISCONTINUOUS:
                    continuousSpeedChange = false;
                    break;
            }
            switch (clickTargetProfile.transitionContinuityDirectionChange) {
                case CONTINUOUS:
                    continuousDirectionChange = true;
                    break;
                case DISCONTINUOUS:
                    continuousDirectionChange = false;
                    break;
            }
            switch (clickTargetProfile.transitionContinuityRadius) {
                case CONTINUOUS:
                    continuousRadius = true;
                    break;
                case DISCONTINUOUS:
                    continuousRadius = false;
                    break;
                default:
                    continuousRadius = clickTargetProfile.canChangeRadius;
            }
            switch (clickTargetProfile.transitionContinuityDRadius) {
                case CONTINUOUS:
                    continuousDRadius = true;
                    break;
                case DISCONTINUOUS:
                    continuousDRadius = false;
                    break;
                default:
                    continuousDRadius = clickTargetProfile.canChangeDRadius;
            }
            switch (clickTargetProfile.transitionContinuityDRadiusChange) {
                case CONTINUOUS:
                    continuousD2Radius = true;
                    break;
                case DISCONTINUOUS:
                    continuousD2Radius = false;
                    break;
            }

        }

        // Convert values from inches to pixels
        // Radius
        double initialTargetRadiusPixels =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) clickTargetProfile.initialTargetRadiusInches
                        , context.getResources().getDisplayMetrics());
        double minimumTargetRadiusPixels =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) clickTargetProfile.minimumTargetRadiusInches
                        , context.getResources().getDisplayMetrics());
        double maximumTargetRadiusPixels =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) clickTargetProfile.maximumTargetRadiusInches
                        , context.getResources().getDisplayMetrics());

        // Speed and Speed Change
        double initialTargetSpeedPixelsPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) clickTargetProfile.initialTargetSpeedInchesPerSecond
                        , context.getResources().getDisplayMetrics());
        double minimumTargetSpeedPixelsPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) clickTargetProfile.minimumTargetSpeedInchesPerSecond
                        , context.getResources().getDisplayMetrics());
        double maximumTargetSpeedPixelsPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) clickTargetProfile.maximumTargetSpeedInchesPerSecond
                        , context.getResources().getDisplayMetrics());
        double initialTargetSpeedChangeAbsoluteValuePixelsPerSecondPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) clickTargetProfile.initialTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond
                        , context.getResources().getDisplayMetrics());
        double minimumTargetSpeedChangeAbsoluteValuePixelsPerSecondPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) clickTargetProfile.minimumTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond
                        , context.getResources().getDisplayMetrics());
        double maximumTargetSpeedChangeAbsoluteValuePixelsPerSecondPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) clickTargetProfile.maximumTargetSpeedChangeAbsoluteValueInchesPerSecondPerSecond
                        , context.getResources().getDisplayMetrics());

        // DRadius and DRadius Change
        double initialTargetDRadiusAbsoluteValuePixelsPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) clickTargetProfile.initialTargetDRadiusAbsoluteValueInchesPerSecond
                        , context.getResources().getDisplayMetrics());
        double minimumTargetDRadiusAbsoluteValuePixelsPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) clickTargetProfile.minimumTargetDRadiusAbsoluteValueInchesPerSecond
                        , context.getResources().getDisplayMetrics());
        double maximumTargetDRadiusAbsoluteValuePixelsPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) clickTargetProfile.maximumTargetDRadiusAbsoluteValueInchesPerSecond
                        , context.getResources().getDisplayMetrics());
        double initialTargetDRadiusChangeAbsoluteValuePixelsPerSecondPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) clickTargetProfile.initialTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond
                        , context.getResources().getDisplayMetrics());
        double minimumTargetDRadiusChangeAbsoluteValuePixelsPerSecondPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) clickTargetProfile.minimumTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond
                        , context.getResources().getDisplayMetrics());
        double maximumTargetDRadiusChangeAbsoluteValuePixelsPerSecondPerSecond =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_IN
                        , (float) clickTargetProfile.maximumTargetDRadiusChangeAbsoluteValueInchesPerSecondPerSecond
                        , context.getResources().getDisplayMetrics());

        double targetSpeedChangePixelsPerSecondPerSecond = 0.0;
        double targetDirectionAngleChangeRadiansPerSecond = 0.0;
        // Speed Change
        if (!initializeClickTarget && continuousSpeedChange == true) {
            if (positionEvolverXPixels != null
                    && positionEvolverXPixels.getPositionEvolverDXdt() != null
                    && positionEvolverXPixels.getPositionEvolverDXdt().getPositionEvolverDXdt() != null) {

                PositionEvolver positionEvolverD2Xdt2Pixels = positionEvolverXPixels.getPositionEvolverDXdt().getPositionEvolverDXdt();
                targetSpeedChangePixelsPerSecondPerSecond = positionEvolverD2Xdt2Pixels.getX().X1;

            }
        } else {
            if (clickTargetProfile.canChangeSpeed) {

                if (clickTargetProfile.randomInitialTargetSpeedChange) {
                    targetSpeedChangePixelsPerSecondPerSecond =
                            UtilityFunctions.generateRandomValue(
                                    minimumTargetSpeedChangeAbsoluteValuePixelsPerSecondPerSecond
                                    , maximumTargetSpeedChangeAbsoluteValuePixelsPerSecondPerSecond
                                    , true);
                } else {
                    targetSpeedChangePixelsPerSecondPerSecond = initialTargetSpeedChangeAbsoluteValuePixelsPerSecondPerSecond;
                    if (clickTargetProfile.randomInitialTargetSpeedChangeSign) {
                        targetSpeedChangePixelsPerSecondPerSecond *= UtilityFunctions.getRandomSign();
                    }
                }

            }
        }
        // Direction Angle Change
        if (!initializeClickTarget && continuousDirectionChange) {
            if (positionEvolverXPixels != null
                    && positionEvolverXPixels.getPositionEvolverDXdt() != null
                    && positionEvolverXPixels.getPositionEvolverDXdt().getPositionEvolverDXdt() != null) {

                PositionEvolver positionEvolverD2Xdt2Pixels = positionEvolverXPixels.getPositionEvolverDXdt().getPositionEvolverDXdt();
                targetDirectionAngleChangeRadiansPerSecond = positionEvolverD2Xdt2Pixels.getX().X2;

            }
        } else {
            if (clickTargetProfile.canChangeDirection) {

                if (clickTargetProfile.randomInitialTargetDirectionAngleChange) {
                    targetDirectionAngleChangeRadiansPerSecond =
                            UtilityFunctions.generateRandomValue(
                                    clickTargetProfile.minimumTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond
                                    , clickTargetProfile.maximumTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond
                                    , true);
                } else {
                    targetDirectionAngleChangeRadiansPerSecond = clickTargetProfile.initialTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond;
                    if (clickTargetProfile.randomInitialTargetDirectionAngleChangeSign) {
                        targetDirectionAngleChangeRadiansPerSecond *= UtilityFunctions.getRandomSign();
                    }
                }

            }
        }

        PositionEvolver targetD2Xdt2Pixels = new PositionEvolver(

                // Initial position and coordinate system
                new PositionVector(
                        targetSpeedChangePixelsPerSecondPerSecond
                        , targetDirectionAngleChangeRadiansPerSecond
                        , 0.0)
                , PositionEvolver.MODE.POLAR_2D

                // Is constant X1, X2, X3
                , true
                , true
                , true

                // Position evolver d2Xdt2
                , null

                // Minimum X1, X2, X3
                , minimumTargetSpeedChangeAbsoluteValuePixelsPerSecondPerSecond
                , clickTargetProfile.minimumTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond
                , 0.0

                // Maximum X1, X2, X3
                , maximumTargetSpeedChangeAbsoluteValuePixelsPerSecondPerSecond
                , clickTargetProfile.maximumTargetDirectionAngleChangeAbsoluteValueRadiansPerSecond
                , 0.0

                // Mirror absolute value boundaries
                , true
                , true
                , false

                // Can randomly change position
                , false
                , 0.0

                // Can randomly change X1, X2, X3
                , clickTargetProfile.canRandomlyChangeDSpeed
                , clickTargetProfile.canRandomlyChangeDDirection
                , false

                // Probability of random change X1, X2, X3
                , (clickTargetProfile.canRandomlyChangeDSpeed) ? clickTargetProfile.probabilityOfRandomDSpeedChangePerSecond : 0.0
                , (clickTargetProfile.canRandomlyChangeDDirection) ? clickTargetProfile.probabilityOfRandomDDirectionChangePerSecond : 0.0
                , 0.0

                // Tie random new X1, X2, X3 to random new DX1, DX2, DX3
                , false
                , false
                , false
                , false
                , false
                , false
                , false

                // Bounces on boundary value
                , clickTargetProfile.boundaryEffectDSpeed
                , clickTargetProfile.boundaryEffectDDirection
                , PositionEvolver.BOUNDARY_EFFECT.STICK
        );

        double targetSpeedPixelsPerSecond = 0.0;
        double targetDirectionAngleRadians = 0.0;
        // Speed
        if (!initializeClickTarget && continuousSpeed) {
            if (positionEvolverXPixels != null
                    && positionEvolverXPixels.getPositionEvolverDXdt() != null) {
                PositionEvolver positionEvolverDXdtPixels = positionEvolverXPixels.getPositionEvolverDXdt();
                targetSpeedPixelsPerSecond = positionEvolverDXdtPixels.getX().X1;
            }
        } else {
            if (clickTargetProfile.canChangePosition) {
                if (clickTargetProfile.randomInitialTargetSpeed) {
                    targetSpeedPixelsPerSecond =
                            UtilityFunctions.generateRandomValue(
                                    minimumTargetSpeedPixelsPerSecond
                                    , maximumTargetSpeedPixelsPerSecond
                                    , false);
                } else {
                    targetSpeedPixelsPerSecond = initialTargetSpeedPixelsPerSecond;
                }
            }
        }
        // Direction Angle
        if (!initializeClickTarget && continuousDirection) {
            if (positionEvolverXPixels != null
                    && positionEvolverXPixels.getPositionEvolverDXdt() != null) {
                PositionEvolver positionEvolverDXdtPixels = positionEvolverXPixels.getPositionEvolverDXdt();
                targetDirectionAngleRadians = positionEvolverDXdtPixels.getX().X2;
            }
        } else {
            if (clickTargetProfile.canChangePosition) {
                if (clickTargetProfile.randomInitialTargetDirectionAngle) {
                    targetDirectionAngleRadians =
                            UtilityFunctions.generateRandomValue(
                                    0.0
                                    , 2.0 * Math.PI
                                    , false);
                } else {
                    targetDirectionAngleRadians = clickTargetProfile.initialTargetDirectionAngleRadians;
                }
            }
        }

        PositionEvolver targetDXdtPixels = new PositionEvolver(

                // Initial position and coordinate system
                new PositionVector(
                        targetSpeedPixelsPerSecond
                        , targetDirectionAngleRadians
                        , 0.0)
                , PositionEvolver.MODE.POLAR_2D

                // X1, X2, X3 are constant
                , !clickTargetProfile.canChangeSpeed
                , !clickTargetProfile.canChangeDirection
                , true

                // dXdt Position Evolver
                , targetD2Xdt2Pixels

                // Min X1, X2, X3
                , minimumTargetSpeedPixelsPerSecond
                , 0.0
                , 0.0

                // Max X1, X2, X3
                , maximumTargetSpeedPixelsPerSecond
                , Math.PI * 2.0
                , 0.0

                // Mirror absolute value boundaries
                , false
                , false
                , false

                // Random position changes
                , false
                , 0.0

                // Random changes in speed and direction
                , clickTargetProfile.canRandomlyChangeSpeed
                , clickTargetProfile.canRandomlyChangeDirection
                , false

                // Probabilities of speed/direction change
                , clickTargetProfile.probabilityOfRandomSpeedChangePerSecond
                , clickTargetProfile.probabilityOfRandomDirectionChangePerSecond
                , 0.0

                // Tie new random X1, X2, X3 to new random DX1, DX2, DX3
                , clickTargetProfile.tieRandomSpeedChangeToRandomDDirectionChange
                , clickTargetProfile.tieRandomDirectionChangeToRandomSpeedChange
                , clickTargetProfile.tieRandomSpeedChangeToRandomDSpeedChange
                , clickTargetProfile.tieRandomSpeedChangeToRandomDDirectionChange
                , clickTargetProfile.tieRandomDirectionChangeToRandomDSpeedChange
                , clickTargetProfile.tieRandomDirectionChangeToRandomDDirectionChange
                , false

                // Bounces on boundary value
                , clickTargetProfile.boundaryEffectSpeed
                , clickTargetProfile.boundaryEffectDirection
                , PositionEvolver.BOUNDARY_EFFECT.STICK
        );

        double XPixels = 0.0;
        double YPixels = 0.0;
        if (!initializeClickTarget && continuousX) {
            if (positionEvolverXPixels != null) {
                XPixels = positionEvolverXPixels.getX().X1;
                YPixels = positionEvolverXPixels.getX().X2;
            }
        } else {
            XPixels = minimumPixelsX + (width / 2.0);
            YPixels = minimumPixelsY + (height / 2.0);
        }

        PositionEvolver targetXPixels = new PositionEvolver(
                // Initial position and coordinate system
                new PositionVector(
                        XPixels
                        , YPixels
                        , 0.0)
                , PositionEvolver.MODE.CARTESIAN_2D

                // X1, X2, X3 are constant
                , !clickTargetProfile.canChangePosition
                , !clickTargetProfile.canChangePosition
                , true

                // dXdt Position evolver
                , targetDXdtPixels

                // Minimum X1, X2, X3
                , minimumPixelsX
                , minimumPixelsY
                , 0.0

                // Maximum X1, X2, X3
                , maximumPixelsX
                , maximumPixelsY
                , 0.0

                // Mirror absolute value boundaries
                , false
                , false
                , false

                // Can randomly change position
                , clickTargetProfile.canRandomlyChangePosition
                , clickTargetProfile.probabilityOfRandomPositionChangePerSecond

                // Can randomly change X1, X2, X3
                , false
                , false
                , false

                // Probability of random change X1, X2, X3
                , 0.0
                , 0.0
                , 0.0

                // Tie new random X1, X2, X3 to new random DX1, DX2, DX3
                , false
                , false
                , clickTargetProfile.tieRandomPositionChangeToRandomSpeedChange
                , clickTargetProfile.tieRandomPositionChangeToRandomDirectionChange
                , clickTargetProfile.tieRandomPositionChangeToRandomSpeedChange
                , clickTargetProfile.tieRandomPositionChangeToRandomDirectionChange
                , false

                // Bounces on boundary value
                , clickTargetProfile.boundaryEffectPositionHorizontal
                , clickTargetProfile.boundaryEffectPositionVertical
                , PositionEvolver.BOUNDARY_EFFECT.STICK
        );

        double targetDRadiusChangePixels = 0.0;
        // D2Radius
        if (!initializeClickTarget && continuousD2Radius) {
            if (positionEvolverRadiusPixels != null
                    && positionEvolverRadiusPixels.getPositionEvolverDXdt() != null
                    && positionEvolverRadiusPixels.getPositionEvolverDXdt().getPositionEvolverDXdt() != null) {
                PositionEvolver positionEvolverDRadiusChangePixels = positionEvolverRadiusPixels.getPositionEvolverDXdt().getPositionEvolverDXdt();
                targetDRadiusChangePixels = positionEvolverDRadiusChangePixels.getX().X1;
            }
        } else {
            if (clickTargetProfile.canChangeDRadius) {
                if (clickTargetProfile.randomInitialTargetDRadiusChange) {
                    targetDRadiusChangePixels = UtilityFunctions.generateRandomValue(
                            minimumTargetDRadiusChangeAbsoluteValuePixelsPerSecondPerSecond
                            , maximumTargetDRadiusChangeAbsoluteValuePixelsPerSecondPerSecond
                            , true);
                } else {
                    targetDRadiusChangePixels = initialTargetDRadiusChangeAbsoluteValuePixelsPerSecondPerSecond;
                    if (clickTargetProfile.randomInitialTargetDRadiusChangeSign) {
                        targetDRadiusChangePixels *= UtilityFunctions.getRandomSign();
                    }
                }
            }
        }

        PositionEvolver targetD2RadiusDt2Pixels = new PositionEvolver(

                // Initial position and coordinate system
                new PositionVector(
                        targetDRadiusChangePixels
                        , 0.0
                        , 0.0)
                , PositionEvolver.MODE.ONE_DIMENSION

                // Is constant X1, X2, X3
                , true
                , true
                , true

                // PositionEvolver dXdt
                , null

                // Minimum X1, X2, X3
                , minimumTargetDRadiusChangeAbsoluteValuePixelsPerSecondPerSecond
                , 0.0
                , 0.0

                // Maximum X1, X2, X3
                , maximumTargetDRadiusChangeAbsoluteValuePixelsPerSecondPerSecond
                , 0.0
                , 0.0

                // Mirror absolute value boundaries
                , true
                , false
                , false

                // Can randomly change position
                , clickTargetProfile.canRandomlyChangeD2Radius
                , (clickTargetProfile.canRandomlyChangeD2Radius) ? clickTargetProfile.probabilityOfRandomD2RadiusChangePerSecond : 0.0

                // Can change X1, X2, X3
                , false
                , false
                , false

                // Probability of change X1, X2, X3
                , 0.0
                , 0.0
                , 0.0

                // Tie random new X1, X2, X3 to new random DX1, DX2, DX3
                , false
                , false
                , false
                , false
                , false
                , false
                , false

                // Bounces on boundary
                , clickTargetProfile.boundaryEffectD2Radius
                , PositionEvolver.BOUNDARY_EFFECT.STICK
                , PositionEvolver.BOUNDARY_EFFECT.STICK
        );

        double targetDRadiusPixels = 0.0;
        // DRadius
        if (!initializeClickTarget && continuousDRadius) {
            if (positionEvolverRadiusPixels != null
                    && positionEvolverRadiusPixels.getPositionEvolverDXdt() != null) {
                PositionEvolver positionEvolverDRadiusPixels = positionEvolverRadiusPixels.getPositionEvolverDXdt();
                targetDRadiusPixels = positionEvolverDRadiusPixels.getX().X1;
            }
        } else {
            if (clickTargetProfile.canChangeRadius) {
                if (clickTargetProfile.randomInitialTargetDRadius) {
                    targetDRadiusPixels = UtilityFunctions.generateRandomValue(
                            minimumTargetDRadiusAbsoluteValuePixelsPerSecond
                            , maximumTargetDRadiusAbsoluteValuePixelsPerSecond
                            , true);
                } else {
                    targetDRadiusPixels = initialTargetDRadiusAbsoluteValuePixelsPerSecond;
                    if (clickTargetProfile.randomInitialTargetDRadiusSign) {
                        targetDRadiusPixels *= UtilityFunctions.getRandomSign();
                    }
                }
            }
        }

        PositionEvolver targetDRadiusDtPixels = new PositionEvolver(

                // Initial position and coordinate system
                new PositionVector(
                        targetDRadiusPixels
                        , 0.0
                        , 0.0)
                , PositionEvolver.MODE.ONE_DIMENSION

                // Is constant X1, X2, X3
                , !clickTargetProfile.canChangeDRadius
                , true
                , true

                // PositionEvolver dXdt
                , targetD2RadiusDt2Pixels

                // Minimum X1, X2, X3
                , minimumTargetDRadiusAbsoluteValuePixelsPerSecond
                , 0.0
                , 0.0

                // Maximum X1, X2, X3
                , maximumTargetDRadiusAbsoluteValuePixelsPerSecond
                , 0.0
                , 0.0

                // Mirror absolute value boundaries
                , true
                , false
                ,false

                // Can randomly change position
                , clickTargetProfile.canRandomlyChangeDRadius
                , clickTargetProfile.probabilityOfRandomDRadiusChangePerSecond

                // Can change X1, X2, X3
                , false
                , false
                , false

                // Probability of change X1, X2, X3
                , 0.0
                , 0.0
                , 0.0

                // Tie new random X1, X2, X3 to new random DX1, DX2, DX3
                , false
                , false
                , clickTargetProfile.tieRandomDRadiusChangeToRandomD2RadiusChange
                , false
                , false
                , false
                , false

                // Bounces on boundary
                , clickTargetProfile.boundaryEffectDRadius
                , PositionEvolver.BOUNDARY_EFFECT.STICK
                , PositionEvolver.BOUNDARY_EFFECT.STICK
        );


        double radiusPixels = 0.0;
        // Radius
        if (!initializeClickTarget && continuousRadius) {
            if (positionEvolverRadiusPixels != null) {
                radiusPixels = positionEvolverRadiusPixels.getX().X1;
            }
        } else {
            if (clickTargetProfile.randomInitialTargetRadius) {
                radiusPixels = UtilityFunctions.generateRandomValue(
                        minimumTargetRadiusPixels
                        , maximumTargetRadiusPixels
                        , false);
            } else {
                radiusPixels = initialTargetRadiusPixels;
            }
        }

        PositionEvolver targetRadiusPixels = new PositionEvolver(

                // Initial position and coordinate system
                new PositionVector(
                        radiusPixels
                        , 0.0
                        , 0.0)
                , PositionEvolver.MODE.ONE_DIMENSION

                // X1, X2, X3 are constant
                , !clickTargetProfile.canChangeRadius
                , true
                , true

                // Position evolver DXdt
                , targetDRadiusDtPixels

                // Minimum X1, X2, X3
                , minimumTargetRadiusPixels
                , 0.0
                , 0.0

                // Maximum X1, X2, X3
                , maximumTargetRadiusPixels
                , 0.0
                , 0.0

                // Mirror absolute value boundaries
                , false
                , false
                , false

                // Can randomly change position
                , clickTargetProfile.canRandomlyChangeRadius
                , clickTargetProfile.probabilityOfRandomRadiusChangePerSecond

                // Can randomly change X1, X2, X3
                , false
                , false
                , false

                // Probability of change X1, X2, X3
                , 0.0
                , 0.0
                , 0.0

                // Tie new random X1, X2, X3 to new random DX1, DX2, DX3
                , false
                ,false
                , clickTargetProfile.tieRandomRadiusChangeToRandomDRadiusChange
                , false
                , false
                , false
                , false

                // Bounces on boundary
                , clickTargetProfile.boundaryEffectRadius
                , PositionEvolver.BOUNDARY_EFFECT.STICK
                , PositionEvolver.BOUNDARY_EFFECT.STICK
        );

        positionEvolverXPixels = targetXPixels;
        positionEvolverRadiusPixels = targetRadiusPixels;

    }

}
