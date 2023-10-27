package css.cecprototype2;

import android.graphics.Bitmap;

public class Region
{
    private int xCenter;
    private int yCenter;
    private int xWidth;
    private int yHeight;
    private int xUpperLeft, yUpperLeft;

    /**
     * This is currently a rectangle, not a circle
     * TODO: Replace with circular structure
     * @param yHheight region's yHheight
     * @param xWidth region's xWidth
     * @param xCenter region's xCenter location
     * @param yCenter region's yCenter location
    * */
    public Region(int xCenter, int yCenter, int xWidth, int yHheight)
    {
        this.xCenter = xCenter;
        this.yCenter = yCenter;
        this.xWidth = xWidth;
        this.yHeight = yHheight;

        // set the upper left coordinates
        xUpperLeft = this.xCenter - xWidth;
        if (xUpperLeft < 0)  xUpperLeft = 0;

        yUpperLeft = this.yCenter - yHeight;
        if (yUpperLeft < 0)  yUpperLeft = 0;
    }

    public Bitmap getBitmapRegion(Bitmap wholeImageBitmap) {
        return Bitmap.createBitmap(wholeImageBitmap, xUpperLeft, yUpperLeft, xWidth, yHeight);
    }


    //getters

    /**
     *
     * @return this region's x value
     */
    public int getxCenter()
    {
        return xCenter;
    }

    /**
     *
     * @return this region's y value
     */
    public int getyCenter()
    {
        return yCenter;
    }

    /**
     *
     * @return this region's width value
     */
    public int getxWidth()
    {
        return xWidth;
    }

    /**
     *
     * @return this region's height value
     */
    public int getyHeight()
    {
        return yHeight;
    }
}
