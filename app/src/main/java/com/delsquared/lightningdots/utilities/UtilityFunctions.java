package com.delsquared.lightningdots.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.TypedValue;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Random;

public class UtilityFunctions {

    public static boolean getHasInternetConnection(Context context) {

        // Initialize the result
        boolean isConnected = false;

        try {

            ConnectivityManager cm =
                    (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

        } catch (Exception e) {

            // Reset the connected flag

        }

        return isConnected;

    }

    public static void sendEventTracker(
            Activity activity,
            String category
            , String action
            , String label
            , long value) {

        try {

            // Get tracker.
            Tracker t = ((LightningDotsApplication) activity.getApplication()).getTracker(
                    LightningDotsApplication.TrackerName.APP_TRACKER);

            // Build the event
            HitBuilders.EventBuilder theEvent = new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action)
                    .setLabel(label)
                    .setValue(value);

            // Build and send an Event.
            t.send(theEvent.build());

        } catch (Exception e) {
            LightningDotsApplication.logDebugErrorMessage("Exception encountered: " + e.getMessage());
        }

    }

    public static void registerScreenView(Activity activity, String screenName) {

        try {

            // Get the app tracker.
            Tracker appTracker = ((LightningDotsApplication) activity.getApplication()).getTracker(
                    LightningDotsApplication.TrackerName.APP_TRACKER);

            // Get the global tracker
            Tracker globalTracker = ((LightningDotsApplication) activity.getApplication()).getTracker(
                    LightningDotsApplication.TrackerName.GLOBAL_TRACKER);

            // Set screen name.
            // Where path is a String representing the screen name.
            appTracker.setScreenName(screenName);
            globalTracker.setScreenName(screenName);

            // Send a screen view.
            appTracker.send(new HitBuilders.ScreenViewBuilder().build());
            globalTracker.send(new HitBuilders.ScreenViewBuilder().build());

        } catch (Exception e) {
            LightningDotsApplication.logDebugErrorMessage("Exception encountered: " + e.getMessage());
        }

    }

    public static float getResourceFloatValue(Context context, int resourceID) {
        float resultValue;

        // Get the value
        try {
            TypedValue typedValueResourceHelper = new TypedValue();
            context.getResources().getValue(resourceID, typedValueResourceHelper, true);
            resultValue = typedValueResourceHelper.getFloat();
        } catch (Exception e) {
            resultValue = 0.0f;
        }

        return resultValue;
    }

    public static final Random randomizer = new Random();

    public static int generateRandomIndex(int minIndex, int maxIndex) {

        return generateRandomIndex(minIndex, maxIndex, randomizer);

    }

    public static int generateRandomIndex(int minIndex, int maxIndex, Random randomizer) {

        // Get the range
        int numberOfPossibleIndices = maxIndex - minIndex + 1;

        // Get the unshifted random index
        int randomNumber = randomizer.nextInt(numberOfPossibleIndices);

        // Set the result by shifting the random index to our specified range
        return minIndex + randomNumber;

    }

    public static double generateRandomValue(double minimumValue, double maximumValue, boolean mirrorAbsoluteValue) {

        return generateRandomValue(minimumValue, maximumValue, mirrorAbsoluteValue, randomizer);

    }

    public static double generateRandomValue(double minimumValue, double maximumValue, boolean mirrorAbsoluteValue, Random randomizer) {

        // Initialize the result
        double resultValue;

        // Get the random value
        resultValue = getRangeValue(randomizer.nextDouble(), minimumValue, maximumValue);

        if (mirrorAbsoluteValue) {
            resultValue *= getRandomSign();
        }

        return resultValue;

    }

    public static double getRandomSign() {

        return getRandomSign(randomizer);

    }
    public static double getRandomSign(Random randomizer) {

        return (randomizer.nextBoolean()) ? 1.0 : -1.0;

    }

    public static double getRangeValue(double percent, double minimumValue, double maximumValue) {
        return minimumValue + (percent * (maximumValue - minimumValue));
    }

    // isLeft(): tests if a point is Left|On|Right of an infinite line.
    //    Input:  three points P0, P1, and P2
    //    Return: >0 for P2 left of the line through P0 and P1
    //            =0 for P2  on the line
    //            <0 for P2  right of the line
    //    See: Algorithm 1 "Area of Triangles and Polygons"
    public static double pointIsLeftOfEdge(PositionVector P0, PositionVector P1, PositionVector P2) {
        return ( (P1.getValue(0) - P0.getValue(0)) * (P2.getValue(1) - P0.getValue(1)) )
                - ( (P2.getValue(0) -  P0.getValue(0)) * (P1.getValue(1) - P0.getValue(1)) );
    }

    // wn_PnPoly(): winding number test for a point in a polygon
    //      Input:   P = a point,
    //               V[] = vertex points of a polygon V[n+1] with V[n]=V[0]
    //      Return:  wn = the winding number (=0 only when P is outside)
    // http://geomalgorithms.com/a03-_inclusion.html
    public static int wn_PnPoly(PositionVector P, ArrayList<PositionVector> V)
    {

        // the  winding number counter
        int    wn = 0;

        // loop through all edges of the polygon
        for (int i = 0; i < V.size() - 1; i++) {    // edge from V[i] to  V[i+1]
            PositionVector V1 = V.get(i);
            PositionVector V2 = V.get(i+1);
            if (V1.getValue(1) <= P.getValue(1)) {                    // start y <= P.y
                if (V2.getValue(1) > P.getValue(1)) {                 // an upward crossing
                    if (pointIsLeftOfEdge(V1, V2, P) > 0) {    // P left of  edge
                        ++wn;                       // have  a valid up intersect
                    }
                }
            }
            else {                                  // start y > P.y (no test needed)
                if (V2.getValue(1) <= P.getValue(1)) {                // a downward crossing
                    if (pointIsLeftOfEdge(V1, V2, P) < 0) {    // P right of  edge
                        --wn;                       // have  a valid down intersect
                    }
                }
            }
        }

        return wn;
    }

    public static <T extends Enum<T>> T getEnumValue(
            String enumValueName
            , final Class<T> enumClass
            , T defaultValue) {

        // Initialize the result
        T resultValue = defaultValue;

        try {
            resultValue = Enum.valueOf(
                    enumClass
                    , enumValueName
            );
        } catch (Exception e) {
            LightningDotsApplication.logDebugErrorMessage("Exception encountered: " + e.getMessage());
        }
         return resultValue;
    }

    public static <T extends Enum<T>> T getEnumValue(
            XmlResourceParser xmlResourceParser
            , String attributeName
            , final Class<T> enumClass
            , T defaultValue) {

        // Initialize the result
        T resultValue = defaultValue;

        if (xmlResourceParser.getAttributeValue(null, attributeName) != null) {

            resultValue = getEnumValue(
                    xmlResourceParser.getAttributeValue(null, attributeName)
                    , enumClass
                    , defaultValue);

        }

        return resultValue;

    }

}
