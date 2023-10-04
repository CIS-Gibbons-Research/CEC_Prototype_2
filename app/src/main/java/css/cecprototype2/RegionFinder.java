package css.cecprototype2;

import android.media.Image;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class RegionFinder {

    // Constants to define the number of regions and other parameters
    private static final int NUM_REGIONS = 7;
    private static final int MIN_REGION_SIZE = 50; // Minimum size for a region
    private static final int MAX_REGION_SIZE = 200; // Maximum size for a region


    //test, assuming a 1000x1000px image. This finds the center of the image (500,500), and then gets all of
    //the regions surrounding the center, assuming a perfect hexagonal shape.
    public List<Region> findRegions(@NonNull Image image) {
        List<Region> regions = new ArrayList<>();

        // Determine the center of the image
        int centerX = image.getWidth() / 2;
        int centerY = image.getHeight() / 2;

        // Define the size and position parameters for the 7 regions
        int centerRegionSize = 200; // Adjust size as needed
        int outerRegionSize = 100;  // Adjust size as needed
        int angleIncrement = 60;    // Angle increment for the hexagon

        // Create the central region
        int centerRegionX = centerX - centerRegionSize / 2;
        int centerRegionY = centerY - centerRegionSize / 2;
        regions.add(new Region(centerRegionX, centerRegionY, centerRegionSize, centerRegionSize));//add central region to list of regions

        // Create the 6 outer regions forming a hexagon
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(i * angleIncrement);
            int outerRegionX = (int) (centerX + Math.cos(angle) * outerRegionSize) - outerRegionSize / 2;
            int outerRegionY = (int) (centerY + Math.sin(angle) * outerRegionSize) - outerRegionSize / 2;
            regions.add(new Region(outerRegionX, outerRegionY, outerRegionSize, outerRegionSize)); //add this outer region to regions list
        }
        //return list of regions as Region objects.
        return regions;
    }
}
