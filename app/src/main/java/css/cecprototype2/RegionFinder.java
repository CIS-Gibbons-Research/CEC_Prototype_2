package css.cecprototype2;

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
    public RegionFinder()
    {
        standardRegions = new ArrayList<>();
        populateStandardRegionsList();
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

    public void populateStandardRegionsList() {
        //x values
        Integer[] xResources = {R.integer.region1_x, R.integer.region2_x, R.integer.region3_x, R.integer.region4_x, R.integer.region5_x, R.integer.region6_x};
        //y values
        Integer[] yResources ={R.integer.region1_y, R.integer.region2_y, R.integer.region3_y, R.integer.region4_y, R.integer.region5_y, R.integer.region6_y};
        //radius values
        Integer[] radResources = {R.integer.region1_rad, R.integer.region2_rad, R.integer.region3_rad, R.integer.region4_rad, R.integer.region5_rad, R.integer.region6_rad};
        for (int i = 0; i < 6; i++)
        {
            Region region = new Region(xResources[i], yResources[i], radResources[i]);
            standardRegions.add(region);
        }
    }

    public Region getStandardRegion(int regionNumber)
    {
        return standardRegions.get(regionNumber);
    }
}
