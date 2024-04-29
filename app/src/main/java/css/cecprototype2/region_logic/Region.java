package css.cecprototype2.region_logic;

import android.graphics.Bitmap;
import android.util.Log;

public class Region
{
    private int xCenter, yCenter, radius;
    private double multiplier;
    public int xUpperLeft, yUpperLeft, xLowerRight, yLowerRight;
    public int xWidth, yHeight;
    public String tag;

    public Region(int x, int y, int rad, double multiplier, String tag) {
        this.xCenter = x;
        this.yCenter = y;
        this.radius = rad;
        this.multiplier = multiplier;
        this.tag = tag;
        updateValues();
        //Log.d("CIS4444", "Region Constructor - xUL, yUL, xWid, yHeight, rad, multiplier: " + xUpperLeft + ", " + yUpperLeft + ", "+ xWidth + ", "+ yHeight + ", " + radius + ", " + multiplier);
    }

    public double getMultiplier()
    {
        return this.multiplier;
    }

    public void setMultiplier(double multiplier)
    {
        this.multiplier = multiplier;
    }

    public int getxCenter() {
        return xCenter;
    }

    public void setxCenter(int xCenter) {
        this.xCenter = xCenter;
        updateValues();
    }

    public int getyCenter() {
        return yCenter;
    }

    public void setyCenter(int yCenter) {
        this.yCenter = yCenter;
        updateValues();
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        updateValues();
    }

    private void updateValues() {
        //convert center value, x, y to xUpperLeft and yUpperLeft using radius of circle
        xUpperLeft = Math.max(xCenter - radius, 0);
        yUpperLeft = Math.max(yCenter - radius, 0);
        xLowerRight = xCenter + radius;
        yLowerRight = yCenter + radius;
        //xLowerRight = Math.min(xCenter + radius, wholeImageBitmap.getWidth());
        //yLowerRight = Math.min(yCenter + radius, wholeImageBitmap.getHeight());
        //width and height of bitmap is 2 * radius
        xWidth = 2 * radius;
        yHeight = 2 * radius;
        //int xWidth = Math.min(2 * radius, wholeImageBitmap.getWidth() - xUpperLeft);
        //int yHeight = Math.min(2 * radius, wholeImageBitmap.getHeight() - yUpperLeft);
        // Ensure that the indices are within valid range
    }

    public Bitmap getBitmapRegion(Bitmap wholeImageBitmap) {
        //convert center value, x, y to xUpperLeft and yUpperLeft using radius of circle
        xUpperLeft = Math.max(xCenter - radius, 0);
        yUpperLeft = Math.max(yCenter - radius, 0);
        xLowerRight = Math.min(xCenter + radius, wholeImageBitmap.getWidth());
        yLowerRight = Math.min(yCenter + radius, wholeImageBitmap.getHeight());
        //width and height of bitmap is 2 * radius
        xWidth = 2 * radius;
        yHeight = 2 * radius;
        int xWidth = Math.min(2 * radius, wholeImageBitmap.getWidth() - xUpperLeft);
        int yHeight = Math.min(2 * radius, wholeImageBitmap.getHeight() - yUpperLeft);
        // Ensure that the indices are within valid range
        //Log.d("CIS4444", "Region getBitmapRegion - xUL, yUL, xWid, yHeight, rad: " + xUpperLeft + ", " + yUpperLeft + ", "+ xWidth + ", "+ yHeight + ", " + radius);


        if (xCenter >= 0 && yCenter >= 0 && radius > 0) {
            // Convert circle dimensions to square shape
            return Bitmap.createBitmap(wholeImageBitmap, xUpperLeft, yUpperLeft, xWidth, yHeight);
        } else {
            // Handle invalid dimensions or provide a default bitmap
            return null;
        }
    }


}
