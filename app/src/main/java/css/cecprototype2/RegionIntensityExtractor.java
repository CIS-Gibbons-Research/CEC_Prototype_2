package css.cecprototype2;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.List;

public class RegionIntensityExtractor
{
    /**
     * instantiates the image and list of regions values
     */
    public RegionIntensityExtractor()
    {

    }

    /**
     * calculates the average intensity of RGB values given the region's location and size.
     *
     * @param inRegion
     * @return average intensity of RGB pixels for the given region.
     */
    public Double getRegionIntensity(Region inRegion, Bitmap bitMap) {
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
            return averageIntensity + 0.0;
        } else {
            // No pixels in the region, return a default value
            return 0.0;
        }
    }

    public Integer getAlterntiveRegionIntensity(Region inRegion, Bitmap bitMap) {
        int sumIntensity = 0;
        int numPixels = 0;
        final int GREEN_THRESHOLD = 10; // Minimum green value threshold

        // Get the Bitmap region for the given region
        Bitmap regionBitmap = inRegion.getBitmapRegion(bitMap);

        if (regionBitmap != null) {
            int width = regionBitmap.getWidth();
            int height = regionBitmap.getHeight();

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int pixelColor = regionBitmap.getPixel(x, y);
                    int green = Color.green(pixelColor);

                    // Check if the green value is above the threshold
                    if (green >= GREEN_THRESHOLD) {
                        // Calculate pixel intensity using the green component
                        sumIntensity += green;
                        numPixels++;
                    }
                }
            }
        }

        if (numPixels > 0) {
            // Calculate and return the average intensity
            int averageIntensity = sumIntensity / numPixels;
            return averageIntensity;
        } else {
            // No pixels in the region that meet the criteria, return a default value
            return 0;
        }
    }

}
