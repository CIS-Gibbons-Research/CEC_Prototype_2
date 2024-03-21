package css.cecprototype2.region_logic;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;

public class RegionIntensityExtractor {

    /**
     * Instantiates the image and list of regions values
     */
    public RegionIntensityExtractor() {
        // You can add any initialization code here
    }

    /**
     * Calculates the average intensity of RGB values given the region's location and size.
     *
     * @param inRegion
     * @return average intensity of RGB pixels for the given region.
     */
    public Double getRegionIntensity(Region inRegion, Bitmap bitMap) {
        if (bitMap == null) {
            Log.e("RegionIntensityExtractor", "getRegionIntensity: Bitmap is null");
            return 0.0; // or handle the case appropriately
        }

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
            // Calculate and return the average intensity, testing the values also
            double averageIntensity = (double) sumIntensity / numPixels;
            double result = averageIntensity;

            return result + 0.0;
        } else {
            return 0.0;
        }
    }

    //FIXME: Implement Actual Functionality
    public Double getRegionIntensityFromValues(Region inRegion, Bitmap inBitMap, Double concentration)
    {
        return getRegionIntensity(inRegion, inBitMap) * concentration;
    }

    public Double getAlternateRegionIntensity(Region inRegion, Bitmap bitMap) {
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
            double averageIntensity = (double)sumIntensity / numPixels;
            Log.d("RegionIntensityExtractor", "getAlternateRegionIntensity: Result = " + averageIntensity);
            return averageIntensity;
        } else {
            // No pixels in the region that meet the criteria, return a default value
            Log.d("RegionIntensityExtractor", "getAlternateRegionIntensity: No pixels in the region, returning 0");
            return 0.0;
        }
    }
}

