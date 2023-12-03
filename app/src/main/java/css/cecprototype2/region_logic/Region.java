package css.cecprototype2.region_logic;

import android.graphics.Bitmap;

public class Region
{
    private int xCenter, yCenter, radius;
    // Pascal_VOC
    public int xUpperLeft, yUpperLeft, xLowerRight, yLowerRight;

    public Region(int x, int y, int rad) {
        this.xCenter = x;
        this.yCenter = y;
        this.radius = rad;
        xUpperLeft = Math.max(xCenter - radius, 0);
        yUpperLeft = Math.max(yCenter - radius, 0);
        xLowerRight = xCenter + radius;
        yLowerRight = yCenter + radius;
    }

    public int getxCenter() {
        return xCenter;
    }

    public void setxCenter(int xCenter) {
        this.xCenter = xCenter;
    }

    public int getyCenter() {
        return yCenter;
    }

    public void setyCenter(int yCenter) {
        this.yCenter = yCenter;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public Bitmap getBitmapRegion(Bitmap wholeImageBitmap) {
        //convert center value, x, y to xUpperLeft and yUpperLeft using radius of circle
        xUpperLeft = Math.max(xCenter - radius, 0);
        yUpperLeft = Math.max(yCenter - radius, 0);
        xLowerRight = Math.min(xCenter + radius, wholeImageBitmap.getWidth());
        yLowerRight = Math.min(yCenter + radius, wholeImageBitmap.getHeight());
        //width and height of bitmap is 2 * radius
        int xWidth = Math.min(2 * radius, wholeImageBitmap.getWidth() - xUpperLeft);
        int yHeight = Math.min(2 * radius, wholeImageBitmap.getHeight() - yUpperLeft);
        //Log.d("Region", "xUL, yUL, xWid, yHeight, rad: " + xUpperLeft + ", " + yUpperLeft + ", "+ xWidth + ", "+ yHeight + ", " + rad);
        // Ensure that the indices are within valid range
        if (xCenter >= 0 && yCenter >= 0 && radius > 0) {
            // Convert circle dimensions to square shape
            return Bitmap.createBitmap(wholeImageBitmap, xUpperLeft, yUpperLeft, xWidth, yHeight);
        } else {
            // Handle invalid dimensions or provide a default bitmap
            return null;
        }
    }


}