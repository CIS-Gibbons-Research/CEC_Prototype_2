package css.cecprototype2;

public class Region
{
    private int x;
    private int y;
    private int width;
    private int height;

    /**
     * This is currently a rectangle, not a circle
     * TODO: Replace with circular structure
     * @param height region's height
     * @param width region's width
     * @param x region's x location
     * @param y region's y location
    * */
    public Region(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }


    //getters

    /**
     *
     * @return this region's x value
     */
    public int getX()
    {
        return x;
    }

    /**
     *
     * @return this region's y value
     */
    public int getY()
    {
        return y;
    }

    /**
     *
     * @return this region's width value
     */
    public int getWidth()
    {
        return width;
    }

    /**
     *
     * @return this region's height value
     */
    public int getHeight()
    {
        return height;
    }
}
