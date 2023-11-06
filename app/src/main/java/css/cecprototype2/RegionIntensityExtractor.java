package css.cecprototype2;

import android.graphics.Bitmap;
import android.graphics.Color;

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
        int numPixels = 0;

        // Get the Bitmap region for the given region
        Bitmap regionBitmap = inRegion.getBitmapRegion(bitMap);

        if (regionBitmap != null) {
            int width = regionBitmap.getWidth();
            int height = regionBitmap.getHeight();

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int pixelColor = regionBitmap.getPixel(x, y);
                    int green = Color.green(pixelColor);

                    // Calculate pixel intensity using the green component
                    sumIntensity += green;
                    numPixels++;
                }
            }
        }

        if (numPixels > 0) {
            // Calculate and return the average intensity
            int averageIntensity = sumIntensity / numPixels;
            return averageIntensity;
        } else {
            // No pixels in the region, return a default value
            return 0;
        }
    }

}
