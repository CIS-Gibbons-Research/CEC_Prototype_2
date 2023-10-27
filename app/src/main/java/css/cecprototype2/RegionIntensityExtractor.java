package css.cecprototype2;

import android.graphics.Bitmap;
import java.util.List;

public class RegionIntensityExtractor
{
    private Bitmap bitMap;
    private List<Region> regions;

    /**
     * @param bitmap the image to be analyzed
     * @param regions the list of regions to be analyzed
     *
     * instantiates the image and list of regions values
     */
    public RegionIntensityExtractor(Bitmap bitmap, List<Region> regions)
    {
        this.bitMap = bitmap;
        this.regions = regions;
    }

    /**
     * calculates the average intensity of RGB values given the region's location and size.
     *
     * @param inRegion
     * @return average intensity of RGB pixels for the given region.
     */
    public Integer getRegionIntensity(Region inRegion) {
        int sumIntensity = 0;
        int numPixels = inRegion.getyHeight() * inRegion.getxWidth();

        for (int x = inRegion.getxCenter(); x < inRegion.getxCenter() + inRegion.getxWidth(); x++) //iterate columns
        {
            for (int y = inRegion.getyCenter(); y < inRegion.getyCenter() + inRegion.getyHeight(); y++) //iterate rows
            {
                //TODO: replace with pixel intensity logic
                int pixelIntensity = 0;
                sumIntensity += pixelIntensity;
            }
        }
        if (numPixels > 0) {
            // Calculate and return the average intensity
            int averageIntensity = sumIntensity / numPixels;
            return averageIntensity;
        } else
        {
            // No pixels in the region, return a default value
            return 0;
        }
    }

}
