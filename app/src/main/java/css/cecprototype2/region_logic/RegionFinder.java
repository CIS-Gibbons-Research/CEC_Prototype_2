package css.cecprototype2.region_logic;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * Helper class to find regions given an image.
 * Uses standard region set from Takudzwa Majaraunga 10/31/2023
 */
public class RegionFinder
{
    private List<Region> standardRegions;

    //private static final int IMAGE_WIDTH = 3024;        // size of photo that region measurements used
    //private static final int IMAGE_HEIGHT = 4032;
    private static final int IMAGE_WIDTH = 1440;        // size of photo that region measurements used
    private static final int IMAGE_HEIGHT = 1920;
    private final int photoWidth = 1440;                // size of final photo that camera used
    private final int photoHeight = 1920;
    //android:layout_width="240dp"
    //android:layout_height="320dp"
    private final int imageViewWidth = 240;
    private final int imageViewHeight = 320;
    private final int NUM_REGIONS = 6;          // The 7th region is for equalization and not used in linear regression
    private Context context;
    private double multiplier;


    public RegionFinder(Context context)
    {
        this.context = context;
        standardRegions = populateStandardRegionsList(context);
        multiplier = (double) imageViewWidth/IMAGE_WIDTH;
    }


    public List<Region> getCustomRegions(Bitmap bitmap)
    {
        //TODO: implement custom region logic
        return null;
    }

    public List<Region> populateStandardRegionsList(Context context) {
        standardRegions = new ArrayList<Region>();
        // Initialize an array of resource names for x, y, and rad values
        String[] xResourceNames = {"region1_x", "region2_x", "region3_x", "region4_x", "region5_x", "region6_x", "region7_x"};
        String[] yResourceNames = {"region1_y", "region2_y", "region3_y", "region4_y", "region5_y", "region6_y", "region7_y"};
        String[] radResourceNames = {"region1_rad", "region2_rad", "region3_rad", "region4_rad", "region5_rad", "region6_rad", "region7_rad"};
        String[] tagResourceNames = {"region1_tag", "region2_tag", "region3_tag", "region4_tag", "region5_tag", "region6_tag", "region7_tag"};

        for (int i = 0; i < NUM_REGIONS; i++) {
            int xResource = context.getResources().getIdentifier(xResourceNames[i], "integer", context.getPackageName());
            int yResource = context.getResources().getIdentifier(yResourceNames[i], "integer", context.getPackageName());
            int radResource = context.getResources().getIdentifier(radResourceNames[i], "integer", context.getPackageName());
            int tagResource = context.getResources().getIdentifier(tagResourceNames[i], "string", context.getPackageName());

            // Retrieve integer values from resources
            int xValue = context.getResources().getInteger(xResource);
            int yValue = context.getResources().getInteger(yResource);
            int radValue = context.getResources().getInteger(radResource);
            String tagValue = context.getResources().getString(tagResource);

            // Convert to percentage values
            double percentageX = Math.round((float) xValue / IMAGE_WIDTH);
            double percentageY = Math.round((float) yValue / IMAGE_HEIGHT);
            double percentageRad = Math.round((float) radValue / Math.min(IMAGE_WIDTH, IMAGE_HEIGHT) );

            int updatedX = (int) ((float)xValue * photoWidth / IMAGE_WIDTH);
            int updatedY = (int) ((float)yValue * photoHeight / IMAGE_HEIGHT);
            int updatedRad = (int) ((float)radValue * photoHeight / IMAGE_HEIGHT);
            //Log.d("CIS4444", "Region Finder - xValue, yValue, radValue, updatedX, updatedY, updatedRad: " + xValue + ", " + yValue + ", "+ radValue + ", "+ updatedX + ", " + updatedY + ", "+ updatedRad);

            // Create a Region object with the converted values
            // TODO : decide if we need to use percentages
            //Region region = new Region(percentageX, percentageY, percentageRad, 1.0);
            Region region = new Region(updatedX, updatedY, updatedRad, multiplier, tagValue);

            standardRegions.add(region);
        }
        return standardRegions;
    }

    public List<Region> getStandardRegions()
    {
        standardRegions = populateStandardRegionsList(this.context);
        return standardRegions;
    }
    public Region getStandardRegion(int regionNumber)
    {
        return standardRegions.get(regionNumber);
    }
}
