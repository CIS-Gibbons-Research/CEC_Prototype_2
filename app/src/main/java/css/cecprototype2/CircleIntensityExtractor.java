package css.cecprototype2;

import android.media.Image;
import java.util.List;

public class CircleIntensityExtractor
{
    private Image image;
    private List<Region> regions;

    public CircleIntensityExtractor(Image image, List<Region> regions)
    {
        this.image = image;
        this.regions = regions;
    }

    public Integer getRegionIntensity(Region inRegion) {
        int sumIntensity = 0;
        int numPixels = inRegion.getHeight() * inRegion.getWidth();

        for (int x = inRegion.getX(); x < inRegion.getX() + inRegion.getWidth(); x++) //iterate columns
        {
            for (int y = inRegion.getY(); y < inRegion.getY() + inRegion.getHeight(); y++) //iterate rows
            {
                int pixelIntensity = 0;
                sumIntensity += pixelIntensity;
            }
        }
        if (numPixels > 0) {
            // Calculate and return the average intensity
            int averageIntensity = sumIntensity / numPixels;
            return averageIntensity;
        } else {
            // No pixels in the region, return a default value (e.g., 0)
            return 0;
        }
    }

}
