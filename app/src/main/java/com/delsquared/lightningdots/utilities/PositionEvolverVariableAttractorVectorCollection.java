package com.delsquared.lightningdots.utilities;

import com.delsquared.lightningdots.ntuple.NTuple;

import java.util.HashMap;
import java.util.Map;

public class PositionEvolverVariableAttractorVectorCollection {

    private PositionVector attractorVectorSnapToVector;
    private PositionVector attractorVectorMaxToVector;
    private PositionVector attractorVectorGravity;

    public PositionEvolverVariableAttractorVectorCollection() {
        attractorVectorSnapToVector = null;
        attractorVectorMaxToVector = null;
        attractorVectorGravity = null;
    }

    public PositionEvolverVariableAttractorVectorCollection(
            PositionVector attractorVectorSnapToVector
            , PositionVector attractorVectorMaxToVector
            , PositionVector attractorVectorGravity) {
        this.attractorVectorSnapToVector = attractorVectorSnapToVector;
        this.attractorVectorMaxToVector = attractorVectorMaxToVector;
        this.attractorVectorGravity = attractorVectorGravity;
    }

    public PositionVector getAttractorVectorSnapToVector() { return this.attractorVectorSnapToVector; }
    public PositionVector getAttractorVectorMaxToVector() { return this.attractorVectorMaxToVector; }
    public PositionVector getAttractorVectorGravity() { return this.attractorVectorGravity; }

    public void setAttractorVectorSnapToVector(PositionVector attractorVectorSnapToVector) { this.attractorVectorSnapToVector = attractorVectorSnapToVector; }
    public void setAttractorVectorMaxToVector(PositionVector attractorVectorMaxToVector) { this.attractorVectorMaxToVector = attractorVectorMaxToVector; }
    public void setAttractorVectorGravity(PositionVector attractorVectorGravity) { this.attractorVectorGravity = attractorVectorGravity; }
}
