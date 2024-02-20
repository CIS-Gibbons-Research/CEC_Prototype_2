package css.cecprototype2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import css.cecprototype2.region_logic.Region;
import css.cecprototype2.region_logic.RegionIntensityExtractor;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class RegionIntensityExtractorTest {

    private RegionIntensityExtractor intensityExtractor;
    private Bitmap mockBitmap;
    private Region mockRegion;
    private Context context;

    @Before
    public void setUp() {
        intensityExtractor = new RegionIntensityExtractor();
        context = ApplicationProvider.getApplicationContext();
        mockBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.sample_a);
        mockRegion = new Region(50, 50, 20, 1.0);
    }

    @Test
    public void testGetRegionIntensity_ValidInputs() {
        // Test when the inputs are valid

        //TODO: Replace expectedIntensity with a real value
        Double expectedIntensity = 100.0; // Adjust this value based on your expectations

        // Call the method to get the region intensity
        Double actualIntensity = intensityExtractor.getRegionIntensity(mockRegion, mockBitmap);

        // Verify that the result is not null and within an acceptable range
        assertEquals(expectedIntensity, actualIntensity, 0.001); // Adjust the delta value based on your needs
    }

    @Test
    public void testGetRegionIntensity_NoPixels() {
        // Test when there are no pixels in the region

        // Set up a mockRegion with zero radius to ensure no pixels in the region
        Region regionNoPixels = new Region(50, 50, 0, 1.0);

        // Call the method to get the region intensity
        Double actualIntensity = intensityExtractor.getRegionIntensity(regionNoPixels, mockBitmap);

        // Verify that the result is 0.0
        assertEquals(0.0, actualIntensity, 0.001);
    }

    @Test
    public void testGetAlternateRegionIntensity_ValidInputs() {
        // Test when the inputs are valid for getAlternateRegionIntensity

        //TODO: Replace expectedIntensity with a real value
        Integer expectedIntensity = 50; // Adjust this value based on your expectations

        // Call the method to get the alternative region intensity
        Double actualIntensity = intensityExtractor.getAlternateRegionIntensity(mockRegion, mockBitmap);

        // Verify that the result is not null and matches the expected intensity
        assertEquals(expectedIntensity.intValue(), actualIntensity.intValue());
    }

    @Test
    public void testGetAlternateRegionIntensity_NoPixelsMeetCriteria() {
        // Test when there are no pixels in the region that meet the criteria

        // Set up a mockRegion with a high GREEN_THRESHOLD to ensure no pixels meet the criteria
        Region regionNoPixelsMeetCriteria = new Region(50, 50, 20, 1.0);

        // Call the method to get the alternative region intensity
        Double actualIntensity = intensityExtractor.getAlternateRegionIntensity(regionNoPixelsMeetCriteria, mockBitmap);

        // Verify that the result is 0
        assertEquals(0, actualIntensity.intValue());
    }
}