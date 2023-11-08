package css.cecprototype2;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;


/**
 * Helper class to find regions given an image.
 * Uses standard region set from Takudzwa Majaraunga 10/31/2023
 */
public class RegionFinder
{
    private List<Region> standardRegions;
    public RegionFinder(Context context)
    {
        standardRegions = new ArrayList<>();
        populateStandardRegionsList(context);
    }

    public List<Region> getStandardRegions()
    {
        return this.standardRegions;
    }

    public List<Region> getCustomRegions(Bitmap bitmap)
    {
        //TODO: implement custom region logic
        return null;
    }

    public void populateStandardRegionsList(Context context) {
        // Initialize an array of resource names for x, y, and rad values
        String[] xResourceNames = {"region1_x", "region2_x", "region3_x", "region4_x", "region5_x", "region6_x"};
        String[] yResourceNames = {"region1_y", "region2_y", "region3_y", "region4_y", "region5_y", "region6_y"};
        String[] radResourceNames = {"region1_rad", "region2_rad", "region3_rad", "region4_rad", "region5_rad", "region6_rad"};

        for (int i = 0; i < 6; i++) {
            int xResource = context.getResources().getIdentifier(xResourceNames[i], "integer", context.getPackageName());
            int yResource = context.getResources().getIdentifier(yResourceNames[i], "integer", context.getPackageName());
            int radResource = context.getResources().getIdentifier(radResourceNames[i], "integer", context.getPackageName());

            // Retrieve integer values from resources
            int xValue = context.getResources().getInteger(xResource);
            int yValue = context.getResources().getInteger(yResource);
            int radValue = context.getResources().getInteger(radResource);

            // Create a Region object with the retrieved values
            Region region = new Region(xValue, yValue, radValue);
            standardRegions.add(region);
        }
    }

    public Region getStandardRegion(int regionNumber)
    {
        return standardRegions.get(regionNumber);
    }
}
