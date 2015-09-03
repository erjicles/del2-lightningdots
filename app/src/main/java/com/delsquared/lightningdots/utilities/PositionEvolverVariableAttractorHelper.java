package com.delsquared.lightningdots.utilities;

import android.content.Context;
import android.util.TypedValue;

import com.delsquared.lightningdots.ntuple.NTuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PositionEvolverVariableAttractorHelper<T extends IPositionEvolvingObject> {

    public final double pixelsPerInch;
    public final double pixels3PerInch3;

    public PositionEvolverVariableAttractorHelper(Context context) {

        // Get the pixels per inch
        pixelsPerInch = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_IN
                , (float) 1.0
                , context.getResources().getDisplayMetrics()
        );
        pixels3PerInch3 = pixelsPerInch * pixelsPerInch * pixelsPerInch;
    }

    public Map<NTuple, PositionEvolverVariableAttractorVectorCollection> getAttractorVectorCollections(
            PositionEvolvingObjectContainer<T> positionEvolvingObjectContainer) {

        // Initialize the result map
        Map<NTuple, PositionEvolverVariableAttractorVectorCollection> resultMap = new HashMap<>();

        // Initialize the helper
        PositionEvolvingObjectContainerHelper<T> positionEvolvingObjectContainerHelper =
                new PositionEvolvingObjectContainerHelper(positionEvolvingObjectContainer);

        // Get the map of attractors
        Map<NTuple, List<PositionEvolverVariableAttractor>> mapPositionEvolverVariableAttractors =
                positionEvolvingObjectContainer.getMapPositionEvolverVariableAttractors();

        // If there are no attractors, exit
        if (mapPositionEvolverVariableAttractors.size() == 0) {
            return resultMap;
        }

        // Loop through the attractors
        Iterator attractorIterator = mapPositionEvolverVariableAttractors.entrySet().iterator();
        while (attractorIterator.hasNext()) {

            // Get the current source key
            Map.Entry<NTuple, List<PositionEvolverVariableAttractor>> currentEntry =
                    (Map.Entry) attractorIterator.next();
            NTuple sourceKey = currentEntry.getKey();

            // Get the current attractor list
            List<PositionEvolverVariableAttractor> sourceAttractorList = currentEntry.getValue();

            // Get the source names
            String sourceObjectName = sourceKey.getNthValue(0);
            String sourceObjectProfile = sourceKey.getNthValue(1);
            String sourcePositionEvolverFamilyName = sourceKey.getNthValue(2);
            String sourcePositionEvolverName = sourceKey.getNthValue(3);

            // Get the source objects
            T sourceObject = positionEvolvingObjectContainerHelper
                    .getObject(sourceObjectName, sourceObjectProfile);
            PositionEvolverFamily sourcePositionEvolverFamily = null;
            PositionEvolver sourcePositionEvolver = null;
            PositionEvolver sourcePositionEvolverDX = null;
            PositionEvolver sourcePositionEvolverD2X = null;
            if (sourceObject != null) {
                sourcePositionEvolverFamily =
                        sourceObject
                                .getCollectionPositionEvolverFamilies()
                                .getObject(sourcePositionEvolverFamilyName
                                );
                if (sourcePositionEvolverFamily != null) {
                    int sourcePositionEvolverIndex =
                            sourcePositionEvolverFamily
                                    .getPositionEvolverIndex(sourcePositionEvolverName);
                    sourcePositionEvolver =
                            sourcePositionEvolverFamily
                                    .getPositionEvolver(sourcePositionEvolverIndex);
                    sourcePositionEvolverDX =
                            sourcePositionEvolverFamily
                                    .getPositionEvolver(sourcePositionEvolverIndex + 1);
                    sourcePositionEvolverD2X =
                            sourcePositionEvolverFamily
                                    .getPositionEvolver(sourcePositionEvolverIndex + 2);
                }
            }

            // Make sure the source objects exist
            // also make sure the attractor list exists and has entries
            if (sourceObject != null
                    && sourcePositionEvolverFamily != null
                    && sourcePositionEvolver != null
                    && sourcePositionEvolverDX != null
                    && sourceAttractorList != null
                    && sourceAttractorList.size() > 0) {

                // Get the list of center points
                // This is used so that the source and target two closest points are only counted
                //List<PositionVector> listSourceCenterPoints = sourcePositionEvolver.get

                // Create the source DX key
                NTuple sourceKeyDX =
                        PositionEvolverVariableAttractor
                                .nTupleTypePositionEvolverVariableAttractorKey
                                .createNTuple(
                                        sourceObjectName
                                        , sourceObjectProfile
                                        , sourcePositionEvolverFamilyName
                                        , sourcePositionEvolverDX.getName()
                                );

                // Get or create the attractor vector collection
                PositionEvolverVariableAttractorVectorCollection sourceVectorCollectionDX = null;
                if (resultMap.containsKey(sourceKeyDX)) {
                    sourceVectorCollectionDX = resultMap.get(sourceKeyDX);
                } else {
                    sourceVectorCollectionDX = new PositionEvolverVariableAttractorVectorCollection();
                    resultMap.put(sourceKeyDX, sourceVectorCollectionDX);
                }

                // Initialize the total mass
                double totalMassSnapToVector = 0.0;
                double totalMassMaxToVector = 0.0;

                // Get the current difference vectors
                PositionVector totalDifferenceVectorSnapToVector = sourceVectorCollectionDX.getAttractorVectorSnapToVector();
                PositionVector totalDifferenceVectorMaxToVector = sourceVectorCollectionDX.getAttractorVectorMaxToVector();
                PositionVector totalGravityAccelerationVector = sourceVectorCollectionDX.getAttractorVectorGravity();

                // Loop through the attractors for this source
                for (PositionEvolverVariableAttractor attractor : sourceAttractorList) {

                    // Make sure this position evolver has the required derivatives
                    if (attractor.mode == PositionEvolverVariableAttractor.MODE.SNAP_TO_VECTOR
                            || (sourcePositionEvolverD2X != null)) {

                        // Get the target object
                        T targetObject = null;
                        PositionEvolverFamily targetPositionEvolverFamily = null;
                        PositionEvolver targetPositionEvolver = null;

                        // Initialize the target mass
                        double targetMass = attractor.mass;

                        if (attractor.type == PositionEvolverVariableAttractor.TYPE.OTHER_OBJECT) {
                            targetObject = positionEvolvingObjectContainerHelper
                                    .getObject(attractor.targetObjectName, attractor.targetObjectProfileName);
                            if (targetObject != null) {
                                targetPositionEvolverFamily =
                                        targetObject.getPositionEvolverFamily(attractor.positionEvolverFamilyName);
                                if (targetPositionEvolverFamily != null) {
                                    targetPositionEvolver =
                                            targetPositionEvolverFamily.getPositionEvolver(attractor.positionEvolverName);
                                }

                                // Get the target mass
                                targetMass = targetObject.getMass();
                            }
                        }

                        if (attractor.mode == PositionEvolverVariableAttractor.MODE.SNAP_TO_VECTOR) {
                            totalMassSnapToVector += targetMass;
                        } else if (attractor.mode == PositionEvolverVariableAttractor.MODE.MAX_TO_VECTOR) {
                            totalMassMaxToVector += targetMass;
                        }

                        // Initialize the list of differences between the source variables and the target variables
                        List<Double> listVariableDifferences = new ArrayList<>();

                        // Loop through the source position evolver variables
                        for (PositionEvolverVariable sourcePositionEvolverVariable
                                : sourcePositionEvolver.getCollectionPositionEvolverVariables()) {

                            // Get the variable name
                            String variableName = sourcePositionEvolverVariable.getName();

                            // Get the attractor variable
                            PositionEvolverVariableAttractorVariable attractorVariable =
                                    attractor.collectionAttractorVariables.getObject(variableName);

                            // Make sure the attractor variable exists
                            if (attractorVariable != null) {

                                // Initialize the difference
                                double difference = 0.0;

                                // Check if the attractor is just a fixed value
                                if (attractor.type == PositionEvolverVariableAttractor.TYPE.FIXED_VALUE) {

                                    // Get the target value
                                    double attractorValue = attractorVariable.getValue();
                                    if (attractorVariable.isPercent) {
                                        attractorValue = UtilityFunctions.getRangeValue(
                                                attractorValue
                                                , sourcePositionEvolverVariable.getMinimumValue()
                                                , sourcePositionEvolverVariable.getMaximumValue()
                                        );
                                    }

                                    // Get the difference
                                    difference = attractorValue - sourcePositionEvolverVariable.getValue();

                                }
                                // Check if the attractor is to another object
                                else if (attractor.type == PositionEvolverVariableAttractor.TYPE.OTHER_OBJECT) {

                                    // Make sure the target position evolver exists
                                    if (targetPositionEvolver != null) {

                                        // Get the target position evolver variable
                                        PositionEvolverVariable targetPositionEvolverVariable =
                                                targetPositionEvolver
                                                        .getCollectionPositionEvolverVariables()
                                                        .getObject(variableName);

                                        // Make sure the target position evolver variable exists
                                        if (targetPositionEvolverVariable != null) {

                                            // Get the difference
                                            difference = targetPositionEvolverVariable.getValue() - sourcePositionEvolverVariable.getValue();

                                        }

                                    }

                                }

                                // Add the difference to the difference list
                                listVariableDifferences.add(difference);
                            }

                            // The attractor variable does not exist, set the difference to 0
                            else {
                                listVariableDifferences.add(0.0);
                            }

                        }

                        // Convert the difference list to a difference vector
                        PositionVector differenceVector = new PositionVector(listVariableDifferences);

                        // Check if this attractor is a repeller
                        if (attractor.isRepeller) {
                            // Reverse the vector
                            differenceVector = differenceVector.multiply(-1.0);
                        }

                        // Check if this attractor is in the snap to vector mode
                        if (attractor.mode == PositionEvolverVariableAttractor.MODE.SNAP_TO_VECTOR) {

                            // Get the weighted difference vector
                            PositionVector weightedDifferenceVector = differenceVector.multiply(targetMass);

                            if (totalDifferenceVectorSnapToVector == null) {
                                totalDifferenceVectorSnapToVector = weightedDifferenceVector;
                            } else {
                                totalDifferenceVectorSnapToVector = totalDifferenceVectorSnapToVector.add(weightedDifferenceVector);
                            }
                        }

                        // Check if this attractor is in the max to vector mode
                        else if (attractor.mode == PositionEvolverVariableAttractor.MODE.MAX_TO_VECTOR) {

                            // Get the weighted difference vector
                            PositionVector weightedDifferenceVector = differenceVector.multiply(targetMass);

                            if (totalDifferenceVectorMaxToVector == null) {
                                totalDifferenceVectorMaxToVector = weightedDifferenceVector;
                            } else {
                                totalDifferenceVectorMaxToVector = totalDifferenceVectorMaxToVector.add(weightedDifferenceVector);
                            }
                        }

                        // Check if this attractor is in the gravity mode
                        else if (attractor.mode == PositionEvolverVariableAttractor.MODE.GRAVITY) {

                            // Get the magnitude squared and cubed of the difference vector
                            double d2 = differenceVector.dotProduct(differenceVector);
                            double d3 = Math.pow(d2, 1.5);

                            // Make sure there is a difference
                            if (d3 > 0.0) {

                                // Get the acceleration vector
                                // a = GM/r^2 rhat = GM/r^3 r, M = target mass
                                PositionVector accelerationVector =
                                        differenceVector
                                                .multiply(pixels3PerInch3)
                                                .multiply(targetMass)
                                                .divide(d3);
                                if (totalGravityAccelerationVector == null) {
                                    totalGravityAccelerationVector = accelerationVector;
                                } else {
                                    totalGravityAccelerationVector = totalGravityAccelerationVector.add(accelerationVector);
                                }
                            }
                        }

                        // Check if this attractor is in the gravity field mode
                        else if (attractor.mode == PositionEvolverVariableAttractor.MODE.GRAVITY_FIELD) {

                            // Get the acceleration vector
                            // a = g   g = target mass
                            PositionVector accelerationVector =
                                    differenceVector.getUnitVector().multiply(pixelsPerInch).multiply(targetMass);
                            if (totalGravityAccelerationVector == null) {
                                totalGravityAccelerationVector = accelerationVector;
                            } else {
                                totalGravityAccelerationVector = totalGravityAccelerationVector.add(accelerationVector);
                            }
                        }
                    }
                }


                if (totalDifferenceVectorSnapToVector != null) {
                    if (totalMassSnapToVector != 0.0) {
                        sourceVectorCollectionDX.setAttractorVectorSnapToVector(totalDifferenceVectorSnapToVector.divide(totalMassSnapToVector));
                    }
                }
                if (totalDifferenceVectorMaxToVector != null) {
                    if (totalMassMaxToVector != 0.0) {
                        sourceVectorCollectionDX.setAttractorVectorMaxToVector(totalDifferenceVectorMaxToVector.divide(totalMassMaxToVector));
                    }
                }
                if (totalGravityAccelerationVector != null) {
                    sourceVectorCollectionDX.setAttractorVectorGravity(totalGravityAccelerationVector);
                }
            }
        }

        return resultMap;

    }

}
