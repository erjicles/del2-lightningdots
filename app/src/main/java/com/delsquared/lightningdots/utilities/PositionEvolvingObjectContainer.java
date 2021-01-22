package com.delsquared.lightningdots.utilities;

import com.delsquared.lightningdots.ntuple.NTuple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PositionEvolvingObjectContainer<T extends IPositionEvolvingObject> {

    // Ordered collection of objects
    protected final OrderedObjectCollection<T> collectionObjects;

    // Maps for triggers of various kinds
    protected final Map<NTuple, List<TransitionTrigger>> mapTransitionTriggers;
    protected final Map<NTuple, List<RandomChangeTrigger>> mapRandomChangeTriggers;
    protected final Map<NTuple, List<PositionEvolverVariableAttractor>> mapPositionEvolverVariableAttractors;

    public PositionEvolvingObjectContainer() {
        this.collectionObjects = new OrderedObjectCollection<>();
        this.mapTransitionTriggers = new HashMap<>();
        this.mapRandomChangeTriggers = new HashMap<>();
        this.mapPositionEvolverVariableAttractors = new HashMap<>();
    }

    public PositionEvolvingObjectContainer(
            OrderedObjectCollection<T> collectionObjects
            , Map<NTuple, List<TransitionTrigger>> mapTransitionTriggers
            , Map<NTuple, List<RandomChangeTrigger>> mapRandomChangeTriggers
            , Map<NTuple, List<PositionEvolverVariableAttractor>> mapPositionEvolverVariableAttractors) {
        this.collectionObjects = collectionObjects;
        this.mapTransitionTriggers = mapTransitionTriggers;
        this.mapRandomChangeTriggers = mapRandomChangeTriggers;
        this.mapPositionEvolverVariableAttractors = mapPositionEvolverVariableAttractors;
    }

    public OrderedObjectCollection<T> getCollectionObjects() { return collectionObjects; }
    public Map<NTuple, List<TransitionTrigger>> getMapTransitionTriggers() { return mapTransitionTriggers; }
    public Map<NTuple, List<RandomChangeTrigger>> getMapRandomChangeTriggers() { return mapRandomChangeTriggers; }
    public Map<NTuple, List<PositionEvolverVariableAttractor>> getMapPositionEvolverVariableAttractors() { return mapPositionEvolverVariableAttractors; }

}
