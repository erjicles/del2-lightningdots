package com.delsquared.lightningdots.utilities;

import android.content.Context;

import com.delsquared.lightningdots.ntuple.NTuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PositionEvolvingObjectContainerEvolverHelper<T extends IPositionEvolvingObject> {

    Context context;

    public PositionEvolvingObjectContainerEvolverHelper(Context context) {
        this.context = context;
    }

    public void evolveTime(double dt, PositionEvolvingObjectContainer<T> positionEvolvingObjectContainer) {

        // Don't do anything if dt = 0
        if (dt == 0.0) {
            return;
        }

        // Create the helper
        PositionEvolvingObjectContainerHelper<T> positionEvolvingObjectContainerHelper =
                new PositionEvolvingObjectContainerHelper(positionEvolvingObjectContainer);

        // -------------------- BEGIN Check for RandomChangeEvents and TransitionEvents -------------------- //

        // Initialize the list of unprocessed RandomChangeEvents
        List<RandomChangeEvent> listRandomChangeEventsUnprocessed = new ArrayList<>();

        // Initialize the list of unprocessed TransitionEvents
        List<TransitionEvent> listTransitionEventsUnprocessed = new ArrayList<>();

        // Loop through the objects
        for (T currentObject : positionEvolvingObjectContainer.getCollectionObjects()) {

            // Check the current object for a profile transition
            boolean performProfileTransition = currentObject.checkProfileTransition(dt);

            // Check if we should perform a transition
            if (performProfileTransition) {

                // Create the transition event
                TransitionEvent transitionEvent = new TransitionEvent(
                        currentObject.getName()
                        , currentObject.getCurrentProfileName()
                );

                // Add the transition event to the list
                listTransitionEventsUnprocessed.add(transitionEvent);

            }

            // There is no profile transition
            // Check for random change effects
            else {

                // Loop through the position evolver families in the object
                for (PositionEvolverFamily positionEvolverFamily : currentObject.getCollectionPositionEvolverFamilies()) {

                    // Loop through the position evolvers in the position evolver family
                    for (PositionEvolver currentPositionEvolver : positionEvolverFamily.getCollectionPositionEvolvers()) {

                        // Check for global random change
                        boolean performGlobalRandomChange = currentPositionEvolver.checkTimedChange(dt);

                        // Loop through the PositionEvolverVariables in the position evolver
                        for (PositionEvolverVariable currentPositionEvolverVariable : currentPositionEvolver.getCollectionPositionEvolverVariables()) {
                            boolean performRandomChange = false;
                            if (!performGlobalRandomChange) {
                                performRandomChange = currentPositionEvolverVariable.checkTimedChange(dt);
                            }

                            // Check if this variable will randomly change
                            if (performGlobalRandomChange || performRandomChange) {
                                RandomChangeEvent randomChangeEvent = new RandomChangeEvent(
                                        currentObject.getName()
                                        , currentObject.getCurrentProfileName()
                                        , currentPositionEvolverVariable.getName()
                                );
                                listRandomChangeEventsUnprocessed.add(randomChangeEvent);
                            }

                        }

                    }

                }

            }

        }
        // -------------------- END Check for RandomChangeEvents and TransitionEvents -------------------- //

        // Initialize the list of SyncVariableTriggers already processed
        // TransitionTriggers and RandomChangeTriggers contribute to this
        List<SyncVariableTrigger> listGeneratedSyncVariableTriggers = new ArrayList<>();

        // -------------------- BEGIN Create the map of generated RandomChangeEvents -------------------- //

        // Initialize the map of generated random changes
        Map<NTuple, Boolean> mapGeneratedRandomChanges = new HashMap<>();

        // Initialize the map of RandomChangeTriggers already processed
        Map<NTuple, Boolean> mapRandomChangeEventsProcessed = new HashMap<>();

        // Process while there are still unprocessed RandomChangeEvents
        while (listRandomChangeEventsUnprocessed.size() > 0) {

            // Get the first RandomChangeEvent in the list
            RandomChangeEvent firstRandomChangeEvent = listRandomChangeEventsUnprocessed.get(0);

            // Get the RandomChangeTrigger key
            NTuple randomChangeTriggerKey = firstRandomChangeEvent.toRandomChangeTriggerKey();

            // Remove this RandomChangeEvent from the unprocessed list
            listRandomChangeEventsUnprocessed.remove(0);

            // Check if we have not yet processed this RandomChangeEvent
            if (!mapRandomChangeEventsProcessed.containsKey(randomChangeTriggerKey)) {

                // Add this random change event to the map of processed random change events
                mapRandomChangeEventsProcessed.put(randomChangeTriggerKey, true);

                // -------------------- BEGIN Add this random change to the map of generated random changes -------------------- //

                // Get the key for this random change
                NTuple randomChangeEventKey = RandomChangeEvent.nTupleTypeRandomChangeTriggerKey.createNTuple(
                        firstRandomChangeEvent.objectName
                        , firstRandomChangeEvent.objectProfileName
                        , firstRandomChangeEvent.variableName
                );

                // Add the variable to the map
                mapGeneratedRandomChanges.put(randomChangeEventKey, true);

                // -------------------- END Add this random change to the map of generated random changes -------------------- //

                // Check if there are any triggers associated with this random change event
                if (positionEvolvingObjectContainer.getMapRandomChangeTriggers().containsKey(randomChangeTriggerKey)) {

                    // Get the list of associated triggers
                    List<RandomChangeTrigger> listRandomChangeTriggers =
                            positionEvolvingObjectContainer.getMapRandomChangeTriggers().get(randomChangeTriggerKey);

                    // Loop through the associated triggers
                    for (RandomChangeTrigger randomChangeTrigger : listRandomChangeTriggers) {

                        // Convert the trigger to a RandomChangeEvent
                        RandomChangeEvent randomChangeEvent =
                                randomChangeTrigger.toRandomChangeEvent();

                        // Add the new RandomChangeEvent to the list of RandomChangeEvents
                        listRandomChangeEventsUnprocessed.add(randomChangeEvent);

                        // Loop through the SyncVariableTriggers associated with this RandomChangeTrigger
                        for (SyncVariableTrigger syncVariableTrigger : randomChangeTrigger.listSyncVariableTriggers) {

                            // Add the sync variable trigger to the list
                            listGeneratedSyncVariableTriggers.add(syncVariableTrigger);

                        }

                    }

                }

            }

        }

        // -------------------- END Create the map of generated RandomChangeEvents -------------------- //


        // -------------------- BEGIN Create the map of generated TransitionEvents -------------------- //

        // Initialize the map of generated transitions
        Map<String, String> mapGeneratedTransitions = new HashMap<>();

        // Initialize the map of TransitionEvents already processed
        Map<NTuple, Boolean> mapTransitionEventsProcessed = new HashMap<>();

        // Process while there are still unprocessed TransitionEvents
        while (listTransitionEventsUnprocessed.size() > 0) {

            // Get the first TransitionEvent in the list
            TransitionEvent firstTransitionEvent = listTransitionEventsUnprocessed.get(0);

            // Get the TransitionTrigger key
            NTuple transitionTriggerKey = firstTransitionEvent.toTransitionTriggerKey();

            // Remove this TransitionEvent from the unprocessed list
            listTransitionEventsUnprocessed.remove(0);

            // Check if we have not yet processed this transition event
            if (!mapTransitionEventsProcessed.containsKey(transitionTriggerKey)) {

                // Add this transition event to the map of processed transition events
                mapTransitionEventsProcessed.put(transitionTriggerKey, true);

                // Add this transition event to the map of generated transition events
                mapGeneratedTransitions.put(firstTransitionEvent.objectName, firstTransitionEvent.objectProfileName);

                // Check if there are any triggers associated with this transition event
                if (positionEvolvingObjectContainer.getMapTransitionTriggers().containsKey(transitionTriggerKey)) {

                    // Get the list of associated triggers
                    List<TransitionTrigger> listTransitionTriggers =
                            positionEvolvingObjectContainer.getMapTransitionTriggers().get(transitionTriggerKey);

                    // Loop through the associated triggers
                    for (TransitionTrigger transitionTrigger : listTransitionTriggers) {

                        // Initialize the target object name and profile
                        String targetObjectName = transitionTrigger.targetObjectName;
                        String targetObjectProfileName = transitionTrigger.targetObjectProfileName;

                        // Check if the transition trigger should randomize the target object
                        if (transitionTrigger.randomTargetObject
                                && positionEvolvingObjectContainer.getCollectionObjects().size() > 1) {

                            // Get the index of the source object for the transition trigger
                            int sourceObjectIndex =
                                    positionEvolvingObjectContainer
                                            .getCollectionObjects()
                                            .getObjectIndex(transitionTrigger.sourceObjectName);

                            // Generate a random index
                            int randomIndex =
                                    UtilityFunctions.generateRandomIndex(
                                            0
                                            , positionEvolvingObjectContainer.getCollectionObjects().size() - 2);

                            // Shift the random index up to ensure that we never select the source
                            if (randomIndex >= sourceObjectIndex) {
                                randomIndex++;
                            }

                            // Set the name of the target
                            targetObjectName = positionEvolvingObjectContainer
                                    .getCollectionObjects()
                                    .getObjectName(randomIndex);

                        }

                        // Check if the transition trigger should randomize the target object profile
                        if (transitionTrigger.randomTargetObjectProfile) {

                            // Get the object
                            T object =
                                    positionEvolvingObjectContainer
                                            .getCollectionObjects()
                                            .getObject(targetObjectName);

                            // Get the list of profile names
                            List<String> listObjectProfileNames =
                                    object.getListProfileNames();

                            // Get a random index
                            int randomIndex =
                                    UtilityFunctions.generateRandomIndex(
                                            0
                                            , listObjectProfileNames.size() - 1
                                    );

                            // Set the name of the target
                            targetObjectProfileName = listObjectProfileNames.get(randomIndex);

                        }

                        // Convert the trigger to a TransitionEvent
                        TransitionEvent transitionEvent = new TransitionEvent(
                                targetObjectName
                                , targetObjectProfileName
                        );

                        // Add the new TransitionEvent to the list of TransitionEvents
                        listTransitionEventsUnprocessed.add(transitionEvent);

                        // Loop through the SyncVariableTriggers associated with this TransitionTrigger
                        for (SyncVariableTrigger syncVariableTrigger : transitionTrigger.listSyncVariableTriggers) {

                            // Add the sync variable trigger to the list
                            listGeneratedSyncVariableTriggers.add(syncVariableTrigger);

                        }

                    }

                }

            }

        }

        // -------------------- END Create the map of generated ClickTargetProfileTransitionEvents -------------------- //


        // -------------------- BEGIN Process attractors -------------------- //
        PositionEvolverVariableAttractorHelper<T> positionEvolverVariableAttractorHelper =
                new PositionEvolverVariableAttractorHelper<>(context);
        Map<NTuple, PositionEvolverVariableAttractorVectorCollection> mapAttractorVectorCollections =
                positionEvolverVariableAttractorHelper.getAttractorVectorCollections(positionEvolvingObjectContainer);
        // -------------------- END Process attractors -------------------- //


        // -------------------- BEGIN Evolve the objects -------------------- //

        // Loop through the objects
        for (T currentObject : positionEvolvingObjectContainer.getCollectionObjects()) {

            // Get the object name
            String currentObjectName = currentObject.getName();

            // Get the object profile name
            String currentObjectProfileName = currentObject.getCurrentProfileName();

            // Check for profile transition
            String targetObjectProfileName = null;
            if (mapGeneratedTransitions.containsKey(currentObjectName)) {
                targetObjectProfileName = mapGeneratedTransitions.get(currentObjectName);
            }
            if (targetObjectProfileName != null) {
                currentObject.setCurrentProfile(targetObjectProfileName);
            }

            // No profile transition
            else {

                // Loop through the PositionEvolverFamilies
                for (PositionEvolverFamily positionEvolverFamily : currentObject.getCollectionPositionEvolverFamilies()) {

                    OrderedObjectCollection<PositionEvolver> collectionPositionEvolvers =
                            positionEvolverFamily.getCollectionPositionEvolvers();

                    // Loop through the PositionEvolvers
                    // Evolve derivatives first
                    for (
                            int currentPositionEvolverIndex = collectionPositionEvolvers.size() - 1;
                            currentPositionEvolverIndex >= 0;
                            currentPositionEvolverIndex--) {

                        // Get the current position evolver and its derivative
                        PositionEvolver positionEvolver = collectionPositionEvolvers.getObject(currentPositionEvolverIndex);
                        PositionEvolver positionEvolverDX = collectionPositionEvolvers.getObject(currentPositionEvolverIndex + 1);
                        PositionEvolver sourcePositionEvolverD2X = collectionPositionEvolvers.getObject(currentPositionEvolverIndex + 2);

                        // Make sure the position evolver exists
                        if (positionEvolver != null) {

                            // Get the variable collection
                            OrderedObjectCollection<PositionEvolverVariable> collectionPositionEvolverVariables =
                                    positionEvolver.getCollectionPositionEvolverVariables();

                            // Initialize the list of bounce variables
                            List<Boolean> listBounceVariables = new ArrayList<>();

                            // Get the DX position vector as the average of its old and new values
                            PositionVector positionVectorDX = null;
                            if (positionEvolverDX != null) {
                                PositionVector currentDX = positionEvolverDX.getX(positionEvolver.getCoordinateSystemType());
                                PositionVector oldDX = positionEvolverDX.getOldX(positionEvolver.getCoordinateSystemType());
                                positionVectorDX = currentDX.add(oldDX).divide(2.0);
                            }

                            // Loop through the PositionEvolverVariables
                            for (
                                    int currentPositionEvolverVariableIndex = 0;
                                    currentPositionEvolverVariableIndex < collectionPositionEvolverVariables.size();
                                    currentPositionEvolverVariableIndex++) {

                                // Get the position evolver variable
                                PositionEvolverVariable positionEvolverVariable =
                                        collectionPositionEvolverVariables.getObject(currentPositionEvolverVariableIndex);

                                // Check if this variable is randomly changing
                                NTuple randomChangeEventKey = RandomChangeEvent.nTupleTypeRandomChangeTriggerKey.createNTuple(
                                        currentObjectName
                                        , currentObjectProfileName
                                        , positionEvolverVariable.getName()
                                );
                                if (mapGeneratedRandomChanges.containsKey(randomChangeEventKey)) {
                                    positionEvolverVariable.randomizeValue();
                                }

                                // This variable is not randomly changing
                                // Make sure the position evolver DX exists
                                else if (positionEvolverDX != null) {

                                    // Get the dx for the current variable
                                    double dxdt = positionVectorDX.getValue(currentPositionEvolverVariableIndex);

                                    // Check if we should evolve the variable
                                    if (positionEvolverVariable.getCanChange()) {

                                        // Get the change in value
                                        double dx = dxdt * dt;

                                        // Change the variable value
                                        positionEvolverVariable.moveValue(dx);

                                    }

                                    // -------------------- BEGIN Check boundaries -------------------- //

                                    // Get if the variable is increasing or decreasing
                                    int variableDirection = 0;
                                    if (dxdt > 0.0) {
                                        variableDirection = 1;
                                    } else if (dxdt < 0.0) {
                                        variableDirection = -1;
                                    }

                                    // Get the boundary reached
                                    int boundaryReached = positionEvolverVariable.checkBoundaryReached(variableDirection);

                                    // Check if we actually reached a boundary
                                    if (boundaryReached != 0) {

                                        // Get the boundary handler values
                                        BoundaryHandlerValues boundaryHandlerValues =
                                                positionEvolverVariable.processBoundary(boundaryReached);
                                        double newValue = boundaryHandlerValues.newValue;

                                        // Add the bounce value to the bounce variables list
                                        listBounceVariables.add(boundaryHandlerValues.bounceValue);

                                        // Set the new value
                                        positionEvolverVariable.setValue(newValue);

                                    } else {
                                        listBounceVariables.add(false);
                                    }

                                    // -------------------- END Check boundaries -------------------- //

                                }

                            }

                            // Process bounce and attractors
                            if (positionEvolverDX != null) {

                                // Process bounce if necessary
                                if (listBounceVariables.contains(true)) {
                                    positionEvolverDX.bounce(positionEvolver.getCoordinateSystemType(), listBounceVariables);
                                }

                                // -------------------- BEGIN Process attractor vector -------------------- //

                                // Get the attractor key
                                NTuple attractorKey =
                                        PositionEvolverVariableAttractor
                                                .nTupleTypePositionEvolverVariableAttractorKey
                                                .createNTuple(
                                                        currentObjectName
                                                        , currentObjectProfileName
                                                        , positionEvolverFamily.getName()
                                                        , positionEvolver.getName()
                                                );

                                // Check if there is an attractor vector collection
                                if (mapAttractorVectorCollections.containsKey(attractorKey)) {

                                    // Get the attractor vector collection
                                    PositionEvolverVariableAttractorVectorCollection attractorVectorCollection =
                                            mapAttractorVectorCollections.get(attractorKey);

                                    PositionVector attractorVectorSnapToVector = attractorVectorCollection.getAttractorVectorSnapToVector();
                                    PositionVector attractorVectorMaxToVector = attractorVectorCollection.getAttractorVectorMaxToVector();
                                    PositionVector attractorVectorGravity = attractorVectorCollection.getAttractorVectorGravity();

                                    if (attractorVectorSnapToVector != null) {
                                        double theta = Math.atan2(attractorVectorSnapToVector.getValue(1), attractorVectorSnapToVector.getValue(0));
                                        positionEvolver.getCollectionPositionEvolverVariables().getObject(1).setValue(theta);
                                    } else if (attractorVectorMaxToVector != null) {
                                        double phi = positionEvolver.getCollectionPositionEvolverVariables().getObject(1).getValue();
                                        double theta = Math.atan2(attractorVectorMaxToVector.getValue(1), attractorVectorMaxToVector.getValue(0));
                                        double delta1 = theta - phi;
                                        double delta2 = delta1;
                                        if (delta1 > 0.0) {
                                            delta2 = (-2.0 * Math.PI) + delta1;
                                        } else {
                                            delta2 = (2.0 * Math.PI) + delta1;
                                        }
                                        double deltaToUse = delta1;
                                        double absDelta1 = Math.abs(delta1);
                                        double absDelta2 = Math.abs(delta2);
                                        if (absDelta1 < absDelta2) {
                                            deltaToUse = delta1;
                                        } else {
                                            deltaToUse = delta2;
                                        }
                                        if (deltaToUse > 0) {
                                            positionEvolverDX.getCollectionPositionEvolverVariables()
                                                    .getObject(1)
                                                    .setValue(
                                                            positionEvolverDX.getCollectionPositionEvolverVariables()
                                                                    .getObject(1)
                                                                    .getMaximumValue()
                                                    );
                                        } else {
                                            positionEvolverDX.getCollectionPositionEvolverVariables()
                                                    .getObject(1)
                                                    .setValue(
                                                            -1.0 * positionEvolverDX.getCollectionPositionEvolverVariables()
                                                                    .getObject(1)
                                                                    .getMaximumValue()
                                                    );
                                        }
                                    } else if (attractorVectorGravity != null) {
                                        double theta = Math.atan2(attractorVectorGravity.getValue(1), attractorVectorGravity.getValue(0));
                                        double delta = positionEvolver.getCollectionPositionEvolverVariables().getObject(1).getValue() - theta;
                                        double magnitude = attractorVectorGravity.getMagnitude();
                                        double ds = magnitude * Math.cos(delta);
                                        double s = positionEvolver.getCollectionPositionEvolverVariables().getObject(0).getValue();
                                        double dPhi = 0.0;
                                        if (s != 0.0) {
                                            dPhi = -1.0 * magnitude * Math.sin(delta) / s;
                                        }
                                        positionEvolverDX.getCollectionPositionEvolverVariables().getObject(0).setValue(
                                                ds
                                        );
                                        positionEvolverDX.getCollectionPositionEvolverVariables().getObject(1).setValue(
                                                dPhi
                                        );
                                    }

                                }

                                // -------------------- END Process attractor vector -------------------- //

                            }

                        }

                    }

                }

            }

        }

        // -------------------- END Evolve the click targets -------------------- //


        // -------------------- BEGIN Process sync variable triggers -------------------- //

        // Loop through the list of generated sync variable triggers
        for (SyncVariableTrigger syncVariableTrigger : listGeneratedSyncVariableTriggers) {

            // Check the mode
            if (syncVariableTrigger.mode == SyncVariableTrigger.MODE.SNAP_TO_TARGET) {

                // Get the source and target objects
                T sourceObject = positionEvolvingObjectContainerHelper.getObject(syncVariableTrigger.sourceObjectName);
                T targetObject = positionEvolvingObjectContainerHelper.getObject(syncVariableTrigger.targetObjectName);

                // Check if the source and target objects exist
                if (sourceObject != null
                        && targetObject != null) {

                    // Get the source and target profiles
                    String sourceObjectProfileName = sourceObject.getCurrentProfileName();
                    String targetObjectProfileName = targetObject.getCurrentProfileName();

                    // Make sure the source and target ClickTargets are using the required ClickTargetProfiles
                    if (sourceObjectProfileName.contentEquals(syncVariableTrigger.sourceObjectProfileName)
                            && targetObjectProfileName.contentEquals(syncVariableTrigger.targetObjectProfileName)) {

                        // Get the source object value for the variable
                        double sourceVariableValue = sourceObject.getVariableValue(syncVariableTrigger.variableName);

                        // Set the target click target value
                        targetObject.setVariableValue(syncVariableTrigger.variableName, sourceVariableValue);

                    }

                }

            }

            // The mode is RANDOMIZE or LITERAL_VALUE
            else {

                T targetObject = positionEvolvingObjectContainerHelper.getObject(syncVariableTrigger.targetObjectName);

                // Get the target click target
                if (targetObject != null) {

                    // Get the profile
                    String targetObjectProfileName = targetObject.getCurrentProfileName();

                    if (targetObjectProfileName.contentEquals(syncVariableTrigger.targetObjectProfileName)) {

                        // Check the mode
                        if (syncVariableTrigger.mode == SyncVariableTrigger.MODE.LITERAL_VALUE) {

                            // Set the value
                            targetObject.setVariableValue(
                                    syncVariableTrigger.variableName
                                    , syncVariableTrigger.getValue(), true
                            );

                        } else if (syncVariableTrigger.mode == SyncVariableTrigger.MODE.RANDOMIZE) {

                            // Randomize the value
                            targetObject.randomizeVariableValue(syncVariableTrigger.variableName);

                        }

                    }

                }

            }

        }

        // -------------------- END Process sync variable triggers -------------------- //

    }

}
