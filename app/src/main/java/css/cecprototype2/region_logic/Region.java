package css.cecprototype2.region_logic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Region
{
    private int x,y,rad;

    public Region(int x, int y, int rad) {
        this.x = x;
        this.y = y;
        this.rad = rad;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getRad() {
        return rad;
    }

    public void setRad(int rad) {
        this.rad = rad;
    }

    public Bitmap getBitmapRegion(Bitmap wholeImageBitmap) {
        //convert center value, x, y to xUpperLeft and yUpperLeft using radius of circle
        int xUpperLeft = Math.max(x - rad, 0);
        int yUpperLeft = Math.max(y - rad, 0);
        //width and height of bitmap is 2 * radius
        int xWidth = Math.min(2 * rad, wholeImageBitmap.getWidth() - xUpperLeft);
        int yHeight = Math.min(2 * rad, wholeImageBitmap.getHeight() - yUpperLeft);
        Log.d("Region", "xUL, yUL, xWid, yHeight, rad: " + xUpperLeft + ", " + yUpperLeft + ", "+ xWidth + ", "+ yHeight + ", " + rad);
        // Ensure that the indices are within valid range
        if (x >= 0 && y >= 0 && rad > 0) {
            // Convert circle dimensions to square shape
            return Bitmap.createBitmap(wholeImageBitmap, xUpperLeft, yUpperLeft, xWidth, yHeight);
        } else {
            // Handle invalid dimensions or provide a default bitmap
            return null;
        }
    }


}
