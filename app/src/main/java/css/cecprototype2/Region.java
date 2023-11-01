package css.cecprototype2;

import android.graphics.Bitmap;

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
        //convert circle dimensions to square shape
        int xUpperLeft = x - rad;
        int yUpperLeft = y - rad;
        int xWidth = 2*rad;
        int yHeight = 2*rad;

        return Bitmap.createBitmap(wholeImageBitmap, xUpperLeft, yUpperLeft, xWidth, yHeight);
    }


}
