package com.delsquared.lightningdots.utilities;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.ArrayList;

public class Polygon {

    // The ordered list of vertices defining the edges of the polygon
    // The last vertex should be identical to the first vertex
    // The most distant vertex from the origin should be on the unit ball
    private final ArrayList<PositionVector> arrayListUnitVertices;

    // The current location of the center of the polygon
    private double centerX;
    private double centerY;

    // The current radius of the bounding ball
    private double radius = 1.0;

    // The current rotation angle
    private double rotationAngle;
    private double rotationAngleSine;
    private double rotationAngleCosine = 1.0;

    // The unrotated unit vertices scaled by the radius
    private ArrayList<PositionVector> arrayListScaledVertices;

    private ArrayList<PositionVector> arrayListCurrentVertices;
    private Path currentPath;

    public Polygon() {
        arrayListUnitVertices = new ArrayList<>();
        arrayListScaledVertices = new ArrayList<>();
        arrayListCurrentVertices = new ArrayList<>();
        currentPath = new Path();
    }

    public Polygon(ArrayList<PositionVector> arrayListUnitVertices) {
        this.arrayListUnitVertices = arrayListUnitVertices;
        this.arrayListScaledVertices = arrayListUnitVertices;
        this.arrayListCurrentVertices = arrayListUnitVertices;
        currentPath = new Path();
    }

    public void setCenterPoint(PositionVector centerPoint) {
        setProperties(
                centerPoint
                , radius
                , rotationAngle
        );
    }

    public void setProperties(
            PositionVector centerPosition
            , double radius
            , double rotationAngle) {

        // Determine which calculations are needed
        boolean recalculateCenter = centerX != centerPosition.X1 || centerY != centerPosition.X2;
        boolean recalculateRadius = this.radius != radius;
        boolean recalculateRotationAngle = this.rotationAngle != rotationAngle;


        // Check if we don't need to do anything
        if (!recalculateCenter
                && !recalculateRadius
                && !recalculateRotationAngle) {
            return;
        }

        // Check if we need to change the radius
        if (recalculateRadius) {

            // Set the new radius
            this.radius = radius;

            // Calculate the new scaled vertices
            arrayListScaledVertices = new ArrayList<>();
            for (PositionVector currentUnitVertex : arrayListUnitVertices) {
                PositionVector currentVertex = new PositionVector(
                        this.radius * currentUnitVertex.X1
                        , this.radius * currentUnitVertex.X2
                        , this.radius * currentUnitVertex.X3
                );
                arrayListScaledVertices.add(currentVertex);
            }
        }

        // Check if we need to recalculate the rotation angle
        if (recalculateRotationAngle) {
            setRotationAngle(rotationAngle);
        }

        // Check if we are only translating the center
        if (recalculateCenter
                && !recalculateRadius
                && !recalculateRotationAngle) {

            double dx = centerPosition.X1 - centerX;
            double dy = centerPosition.X2 - centerY;

            // Calculate the new vertices and store them in a temporary list
            ArrayList<PositionVector> temporaryVertices = new ArrayList<>();
            for (PositionVector currentVertex : arrayListCurrentVertices) {
                PositionVector newVertex = new PositionVector(
                        currentVertex.X1 + dx
                        , currentVertex.X2 + dy
                        , 0.0
                );
                temporaryVertices.add(newVertex);
            }

            // Set the temporary list as the new vertex list
            arrayListCurrentVertices = temporaryVertices;

            // Set the new center position
            this.centerX = centerPosition.X1;
            this.centerY = centerPosition.X2;

        } else {

            if (recalculateCenter) {
                this.centerX = centerPosition.X1;
                this.centerY = centerPosition.X2;
            }

            // Recalculate the vertices
            recalculateCurrentVertices();

        }

        // Recalculate the path
        recalculatePath();

    }

    private void setRotationAngle(double rotationAngle) {
        this.rotationAngle = rotationAngle;
        rotationAngleSine = Math.sin(rotationAngle);
        rotationAngleCosine = Math.cos(rotationAngle);
    }

    private void recalculateCurrentVertices() {

        // First recalculate the vertices, accounting for radius and rotation
        arrayListCurrentVertices = new ArrayList<>();
        for (PositionVector currentScaledVertex : arrayListScaledVertices) {
            PositionVector currentVertex = new PositionVector(
                    centerX + ((currentScaledVertex.X1 * rotationAngleCosine) - (currentScaledVertex.X2 * rotationAngleSine))
                    , centerY + ((currentScaledVertex.X1 * rotationAngleSine) + (currentScaledVertex.X2 * rotationAngleCosine))
                    , 0.0
            );
            arrayListCurrentVertices.add(currentVertex);
        }

    }

    private void recalculatePath() {

        if (currentPath == null)
            currentPath = new Path();

        // Reset the path
        currentPath.reset();

        // Initialize the vertex counter
        int currentVertexIndex = 0;

        for (PositionVector currentVertex : arrayListCurrentVertices) {

            // Check if this is the first vertex
            if (currentVertexIndex == 0) {

                // Move the path to the current vertex
                currentPath.moveTo(
                        (float) currentVertex.X1
                        , (float) currentVertex.X2
                );

            } else {

                // Create line to the current vertex
                currentPath.lineTo(
                        (float) currentVertex.X1
                        , (float) currentVertex.X2
                );

            }

            // Increment the current vertex index
            ++currentVertexIndex;
        }
    }

    public void draw(Canvas canvas, Paint paint) {

        // Draw the polygon
        canvas.drawPath(currentPath, paint);

    }

    public boolean pointIsInsidePolygon(double pointX, double pointY) {

        // First check if the point is outside the bounding circle
        double dx = pointX - centerX;
        double dy = pointY - centerY;
        if ((dx*dx) + (dy*dy) > (radius * radius)) {
            return false;
        }

        // The point is inside the bounding circle, check if it's inside the polygon
        int windingNumber = UtilityFunctions.wn_PnPoly(new PositionVector(pointX, pointY), arrayListCurrentVertices);
        return windingNumber != 0;
    }

    public Polygon duplicate() {

        // Initialize the new polygon
        Polygon duplicatePolygon = new Polygon(
                arrayListUnitVertices
        );

        // Set the polygon's properties
        duplicatePolygon.setProperties(
                new PositionVector(centerX, centerY, 0.0)
                , radius
                , rotationAngle
        );

        return duplicatePolygon;
    }

    public boolean equals(Polygon otherPolygon) {
        return arrayListUnitVertices.equals(otherPolygon.arrayListUnitVertices)
                && centerX == otherPolygon.centerX
                && centerY == otherPolygon.centerY
                && radius == otherPolygon.radius
                && rotationAngle == otherPolygon.rotationAngle;
    }

}
