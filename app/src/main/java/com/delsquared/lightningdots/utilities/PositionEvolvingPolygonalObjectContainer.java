package com.delsquared.lightningdots.utilities;

import com.delsquared.lightningdots.ntuple.NTuple;

import java.util.List;
import java.util.Map;

public class PositionEvolvingPolygonalObjectContainer<T extends IPositionEvolvingPolygonalObject> extends PositionEvolvingObjectContainer<T> {

    public PositionEvolvingPolygonalObjectContainer() {
        super();
    }

    public PositionEvolvingPolygonalObjectContainer(
            OrderedObjectCollection<T> collectionObjects
            , Map<NTuple, List<TransitionTrigger>> mapTransitionTriggers
            , Map<NTuple, List<RandomChangeTrigger>> mapRandomChangeTriggers
            , Map<NTuple, List<PositionEvolverVariableAttractor>> mapPositionEvolverVariableAttractors) {
        super(
                collectionObjects
                , mapTransitionTriggers
                , mapRandomChangeTriggers
                , mapPositionEvolverVariableAttractors);
    }

}
