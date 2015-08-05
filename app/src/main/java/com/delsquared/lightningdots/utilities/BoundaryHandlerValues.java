package com.delsquared.lightningdots.utilities;

import com.delsquared.lightningdots.ntuple.NTupleType;

public class BoundaryHandlerValues {

    public final double newValue;
    public final boolean bounceValue;

    public BoundaryHandlerValues(
            double newValue
            , boolean bounceValue) {
        this.newValue = newValue;
        this.bounceValue = bounceValue;
    }

    public static final NTupleType nTupleTypeBounceVariableKey =
            NTupleType.DefaultFactory.create(
                    String.class        // Target object name
                    , String.class      // Target object profile name
                    , String.class      // Target variable name
            );

}
