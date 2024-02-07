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
        mockBitmap = createMockBitmap(context);
        mockRegion = new Region(50, 50, 20);
    }

    @Test
    public void testGetRegionIntensity_ValidInputs() {
        // Test when the inputs are valid

        // Assuming the mockBitmap has a known region with known intensity, adjust the expected value accordingly
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
        Region regionNoPixels = new Region(50, 50, 0);

        // Call the method to get the region intensity
        Double actualIntensity = intensityExtractor.getRegionIntensity(regionNoPixels, mockBitmap);

        // Verify that the result is 0.0
        assertEquals(0.0, actualIntensity, 0.001);
    }

    @Test
    public void testGetAlternateRegionIntensity_ValidInputs() {
        // Test when the inputs are valid for getAlternateRegionIntensity

        // Assuming the mockBitmap has a known region with known intensity, adjust the expected value accordingly
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
        Region regionNoPixelsMeetCriteria = new Region(50, 50, 20);

        // Call the method to get the alternative region intensity
        Double actualIntensity = intensityExtractor.getAlternateRegionIntensity(regionNoPixelsMeetCriteria, mockBitmap);

        // Verify that the result is 0
        assertEquals(0, actualIntensity.intValue());
    }

    // Helper method to create a mock Bitmap for testing
    private Bitmap createMockBitmap(Context context) {
        // Load the Bitmap from the resource
        int resourceId = R.drawable.sample_a; // Assuming 'sample_a.jpg' is in the res/drawable folder
        return BitmapFactory.decodeResource(context.getResources(), resourceId);
    }
}