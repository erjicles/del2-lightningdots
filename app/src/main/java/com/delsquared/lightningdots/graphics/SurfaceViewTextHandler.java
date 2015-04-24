package com.delsquared.lightningdots.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class SurfaceViewTextHandler {

    private String text;
    private JUSTIFY_HORIZONTAL justify_horizontal;
    private JUSTIFY_VERTICAL justify_vertical;
    private double X;
    private double Y;
    private double textWidth;
    private double textHeight;
    private double textHeightFactor;
    private double drawX;
    private double drawY;
    private Paint paint;

    public SurfaceViewTextHandler() {
        text = "";
        justify_horizontal = JUSTIFY_HORIZONTAL.LEFT;
        justify_vertical = JUSTIFY_VERTICAL.TOP;
        X = 0.0;
        Y = 0.0;
        textWidth = 1.0;
        textHeight = 1.0;
        textHeightFactor = 0.05;
        drawX = 0.0;
        drawY = 0.0;
        paint = new Paint();
        calculateActualPosition();
    }

    public SurfaceViewTextHandler(
            String text
            , JUSTIFY_HORIZONTAL justify_horizontal
            , JUSTIFY_VERTICAL justify_vertical
            , double X
            , double Y
            , double textHeightFactor
            , double canvasHeight
            , Paint paint) {

        this.text = text;
        this.justify_horizontal = justify_horizontal;
        this.justify_vertical = justify_vertical;
        this.X = X;
        this.Y = Y;
        this.textHeightFactor = textHeightFactor;
        this.paint = paint;
        setTextHeightAndWidth(canvasHeight * textHeightFactor);
        calculateActualPosition();
    }

    public String getText() { return text; }
    public JUSTIFY_HORIZONTAL getJustify_horizontal() { return justify_horizontal; }
    public JUSTIFY_VERTICAL getJustify_vertical() { return justify_vertical; }
    public double getX() { return X; }
    public double getY() { return Y; }
    public double getDrawX() { return drawX; }
    public double getDrawY() { return drawY; }
    public double getTextHeight() { return textHeight; }
    public double getTextHeightFactor() { return textHeightFactor; }
    public double getTextWidth() { return textWidth; }
    public RectF getDrawBoundingRectF(float offsetX, float offsetY, float multiplier) {

        float multiplierOffsetX = (float) textWidth * (multiplier - 1.0f) / 2.0f;
        float multiplierOffsetY = (float) textHeight * (multiplier - 1.0f) / 2.0f;
        float left = (float) drawX - multiplierOffsetX + offsetX;
        float right = (float) drawX + (float) textWidth + multiplierOffsetX + offsetX;
        float top = (float) drawY - (float) textHeight - multiplierOffsetY + offsetY;
        float bottom = (float) drawY + multiplierOffsetY + offsetY;

        //float left = (float) drawX + offsetX;
        //float right = (float) drawX + (float)textWidth + offsetX;
        //float top = (float) drawY - (float)textHeight + offsetY;
        //float bottom = (float) drawY + offsetY;
        return new RectF(left, top, right, bottom);
    }

    public void setText(String text) {
        this.text = text;
        setTextHeightAndWidth(this.textHeight);
    }

    public void setPosition(double X, double Y) {
        this.X = X;
        this.Y = Y;
        calculateActualPosition();
    }

    public void setTextPaint(Paint textPaint) {
        this.paint = textPaint;
        setTextHeightAndWidth(this.textHeight);
    }

    public void recalculateTextHeight(double canvasHeight) {
        setTextHeightAndWidth(canvasHeight * textHeightFactor);
    }

    public void setTextHeightAndWidth(double textHeight) {

        this.textHeight = textHeight;
        paint.setTextSize((float) this.textHeight);
        this.textWidth = paint.measureText(this.text);
        calculateActualPosition();

    }

    private void calculateActualPosition() {

        switch (justify_horizontal) {

            case LEFT:
                drawX = X;
                break;

            case CENTER:
                drawX = X - (textWidth / 2.0);
                break;

            case RIGHT:
                drawX = X - textWidth;
                break;

            default: // Assume left
                drawX = X;

        }

        switch (justify_vertical) {

            case TOP:
                drawY = Y + textHeight;
                break;

            case CENTER:
                drawY = Y + (textHeight / 2.0);
                break;

            case BOTTOM:
                drawY = Y;
                break;

            default: // Assume top
                drawY = Y + textHeight;

        }

    }

    public void drawText(Canvas canvas) {
        drawText(canvas, 0.0f, 0.0f);
    }

    public void drawText(Canvas canvas, float offsetX, float offsetY) {
        canvas.drawText(text, (float) drawX + offsetX, (float) drawY + offsetY, paint);
    }

    public enum JUSTIFY_HORIZONTAL {
        LEFT
        , CENTER
        , RIGHT
    }

    public enum JUSTIFY_VERTICAL {
        TOP
        , CENTER
        , BOTTOM
    }

}
