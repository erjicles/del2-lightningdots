package com.delsquared.lightningdots.utilities;

import java.util.ArrayList;
import java.util.List;

public class PositionVectorHelper {

    /***
     * Returns the difference vector pointing from p1 to p2
     * @param p1
     * @param p2
     * @return
     */
    public static PositionVector GetDifferenceVector(PositionVector p1, PositionVector p2) {
        return p2.add(p1.multiply(-1.0));

    }

}
