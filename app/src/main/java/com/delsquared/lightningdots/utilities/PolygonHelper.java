package com.delsquared.lightningdots.utilities;

import android.content.Context;
import android.content.res.XmlResourceParser;

import com.delsquared.lightningdots.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class PolygonHelper {
    private static final String CLASS_NAME = PolygonHelper.class.getSimpleName();

    public static final String NODE_NAME_CLICK_TARGET_SHAPE = "ClickTargetShape";
    public static final String ATTRIBUTE_NAME_SHAPE_NAME = "name";

    public static final String NODE_NAME_VERTEX = "Vertex";
    public static final String ATTRIBUTE_NAME_VERTEX_X = "x";
    public static final String ATTRIBUTE_NAME_VERTEX_Y = "y";

    public static final ArrayList<String> CLICK_TARGET_SHAPES = new ArrayList<String>() {{
        add("CIRCLE");
        add("SQUARE");
        add("TRIANGLE_EQUILATERAL");
        add("STAR_5_POINTS");
        add("RHOMBUS_45");
    }};

    public static Polygon getPolygon(
            Context context
            , String targetShapeName) {
        String methodName = CLASS_NAME + ".getPolygon";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Check if it's a circle
        if (targetShapeName.contentEquals(CLICK_TARGET_SHAPES.get(0))) {
            return null;
        }

        // Initialize the list of vertices
        ArrayList<PositionVector> arrayListVertices = new ArrayList<>();

        // Initialize the flag for if we found the shape
        boolean foundTargetShape = false;

        try {

            // Get a XmlResourceParser object to read contents of the xml file
            XmlResourceParser xmlResourceParser =
                    context.getResources().getXml(R.xml.click_target_shape_definitions);

            // Get the initial parser event
            int currentXmlEvent = xmlResourceParser.getEventType();

            //while haven't reached the end of the XML file
            while (currentXmlEvent != XmlPullParser.END_DOCUMENT)
            {

                // Check if we found the start of a tag
                if (currentXmlEvent == XmlPullParser.START_TAG) {

                    // Check if we found the start of a click target profile script
                    if (xmlResourceParser.getName().contentEquals(NODE_NAME_CLICK_TARGET_SHAPE)) {

                        // Get the shape name of the current shape
                        String currentShapeName =
                                (xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_SHAPE_NAME) == null) ?
                                        ""
                                        : xmlResourceParser.getAttributeValue(null, ATTRIBUTE_NAME_SHAPE_NAME);

                        // Check if the current shape is the one we are looking for
                        if (currentShapeName.contentEquals(targetShapeName)) {

                            // Flag the shape as found
                            foundTargetShape = true;

                        }

                    }

                    // Check if we found the start of a vertex for our target shape
                    if (xmlResourceParser.getName().contentEquals(NODE_NAME_VERTEX)
                            && foundTargetShape) {

                        // Get the X and Y values for this vertex
                        double X =
                                xmlResourceParser.getAttributeFloatValue(
                                        null
                                        , ATTRIBUTE_NAME_VERTEX_X
                                        , 0.0f);
                        double Y =
                                xmlResourceParser.getAttributeFloatValue(
                                        null
                                        , ATTRIBUTE_NAME_VERTEX_Y
                                        , 0.0f);

                        // Add the vertex to the polygon
                        //noinspection SuspiciousNameCombination
                        arrayListVertices.add(new PositionVector(X, Y));

                    }

                } else if (currentXmlEvent == XmlPullParser.END_TAG) {

                    // Check if this is the end tag for a click target shape
                    if (xmlResourceParser.getName().contentEquals(NODE_NAME_CLICK_TARGET_SHAPE)) {

                        // Check if we already found the shape, and this is the closing tag for its script
                        if (foundTargetShape) {

                            // Break out of the while loop
                            break;

                        }

                    }

                }

                // Get the type of the next event
                currentXmlEvent = xmlResourceParser.next();

            }

            //Release resources associated with the parser
            xmlResourceParser.close();

        } catch (XmlPullParserException | IOException e) {
            UtilityFunctions.logError(methodName, "Exception parsing xml", e);
        }

        return new Polygon(arrayListVertices);

    }
}
