package com.delsquared.lightningdots.utilities;

import android.util.Pair;

import com.delsquared.lightningdots.ntuple.NTuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PositionEvolverVariableAttractorHelper<T extends IPositionEvolvingObject> {

    public PositionEvolverVariableAttractorHelper() {

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

            // Get the source names
            String objectName = sourceKey.getNthValue(0);
            String objectProfile = sourceKey.getNthValue(1);
            String positionEvolverFamilyName = sourceKey.getNthValue(2);
            String positionEvolverName = sourceKey.getNthValue(3);

            // Get the source object
            T sourceObject = positionEvolvingObjectContainer.getCollectionObjects().getObject(objectName);

            // Make sure the source object exists and is in the required profile
            if (sourceObject != null
                    && sourceObject.getCurrentProfileName().contentEquals(objectProfile)) {

                // Get the source position evolver family
                PositionEvolverFamily sourcePositionEvolverFamily =
                        sourceObject.getPositionEvolverFamily(positionEvolverFamilyName);

                // Make sure the position evolver family exists
                if (sourcePositionEvolverFamily != null) {

                    // Get the source position evolver and its derivative position evolvers
                    int sourcePositionEvolverIndex = sourcePositionEvolverFamily.getPositionEvolverIndex(positionEvolverName);
                    PositionEvolver sourcePositionEvolver =
                            sourcePositionEvolverFamily.getPositionEvolver(sourcePositionEvolverIndex);
                    PositionEvolver sourcePositionEvolverDX =
                            sourcePositionEvolverFamily.getPositionEvolver(sourcePositionEvolverIndex + 1);
                    PositionEvolver sourcePositionEvolverD2X =
                            sourcePositionEvolverFamily.getPositionEvolver(sourcePositionEvolverIndex + 2);

                    // Make sure the source and derivative position evolvers exist
                    if (sourcePositionEvolver != null
                            && sourcePositionEvolverDX != null) {

                        NTuple sourceKeyDX =
                                PositionEvolverVariableAttractor
                                        .nTupleTypePositionEvolverVariableAttractorKey
                                        .createNTuple(
                                                objectName
                                                , objectProfile
                                                , positionEvolverFamilyName
                                                , sourcePositionEvolverDX.getName()
                                        );

                        // Get the vector collection
                        PositionEvolverVariableAttractorVectorCollection vectorCollectionDX = null;
                        if (resultMap.containsKey(sourceKeyDX)) {
                            vectorCollectionDX = resultMap.get(sourceKeyDX);
                        } else {
                            vectorCollectionDX = new PositionEvolverVariableAttractorVectorCollection();
                            resultMap.put(sourceKeyDX, vectorCollectionDX);
                        }

                        // total mass
                        double totalMassSnapToVector = 0.0;
                        double totalMassMaxToVector = 0.0;

                        // Get the current difference vectors
                        PositionVector totalDifferenceVectorSnapToVector = vectorCollectionDX.getAttractorVectorSnapToVector();
                        PositionVector totalDifferenceVectorMaxToVector = vectorCollectionDX.getAttractorVectorMaxToVector();
                        PositionVector totalGravityForceVector = vectorCollectionDX.getAttractorVectorGravity();

                        // Get the list of attractors for this position evolver
                        List<PositionEvolverVariableAttractor> listPositionEvolverVariableAttractors =
                                currentEntry.getValue();

                        // Loop through the attractors for this source
                        for (PositionEvolverVariableAttractor attractor : listPositionEvolverVariableAttractors) {

                            // Make sure this position evolver has the required derivatives
                            if (attractor.mode == PositionEvolverVariableAttractor.MODE.SNAP_TO_VECTOR
                                    || (sourcePositionEvolverD2X != null)) {

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

                                            // Get the target position evolver
                                            PositionEvolver targetPositionEvolver =
                                                    positionEvolvingObjectContainerHelper.getPositionEvolver(
                                                            attractor.targetObjectName
                                                            , attractor.targetObjectProfileName
                                                            , attractor.positionEvolverFamilyName
                                                            , attractor.positionEvolverName
                                                    );

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
                                    totalMassSnapToVector += attractor.mass;
                                    if (totalDifferenceVectorSnapToVector == null) {
                                        totalDifferenceVectorSnapToVector = differenceVector;
                                    } else {
                                        totalDifferenceVectorSnapToVector = totalDifferenceVectorSnapToVector.add(differenceVector);
                                    }
                                }

                                // Check if this attractor is in the max to vector mode
                                else if (attractor.mode == PositionEvolverVariableAttractor.MODE.MAX_TO_VECTOR) {
                                    totalMassMaxToVector += attractor.mass;
                                    if (totalDifferenceVectorMaxToVector == null) {
                                        totalDifferenceVectorMaxToVector = differenceVector;
                                    } else {
                                        totalDifferenceVectorMaxToVector = totalDifferenceVectorMaxToVector.add(differenceVector);
                                    }
                                }

                                // Check if this attractor is in the gravity mode
                                else if (attractor.mode == PositionEvolverVariableAttractor.MODE.GRAVITY) {
                                    double d2 = differenceVector.dotProduct(differenceVector);
                                    double d3 = Math.pow(d2, 1.5);
                                    if (d3 != 0.0) {
                                        PositionVector forceVector = differenceVector.multiply(attractor.mass / d3);
                                        if (totalGravityForceVector == null) {
                                            totalGravityForceVector = forceVector;
                                        } else {
                                            totalGravityForceVector = totalGravityForceVector.add(forceVector);
                                        }
                                    }
                                }

                                // Check if this attractor is in the gravity field mode
                                else if (attractor.mode == PositionEvolverVariableAttractor.MODE.GRAVITY_FIELD) {
                                    PositionVector forceVector = differenceVector.getUnitVector().multiply(attractor.mass);
                                    if (totalGravityForceVector == null) {
                                        totalGravityForceVector = forceVector;
                                    } else {
                                        totalGravityForceVector = totalGravityForceVector.add(forceVector);
                                    }
                                }
                            }
                        }


                        if (totalDifferenceVectorSnapToVector != null) {
                            if (totalMassSnapToVector != 0.0) {
                                vectorCollectionDX.setAttractorVectorSnapToVector(totalDifferenceVectorSnapToVector.multiply(1.0 / totalMassSnapToVector));
                            }
                        }
                        if (totalDifferenceVectorMaxToVector != null) {
                            if (totalMassMaxToVector != 0.0) {
                                vectorCollectionDX.setAttractorVectorMaxToVector(totalDifferenceVectorMaxToVector.multiply(1.0 / totalMassMaxToVector));
                            }
                        }
                        if (totalGravityForceVector != null) {
                            vectorCollectionDX.setAttractorVectorGravity(totalGravityForceVector);
                        }

                    }
                }
            }
        }

        return resultMap;

    }

}
