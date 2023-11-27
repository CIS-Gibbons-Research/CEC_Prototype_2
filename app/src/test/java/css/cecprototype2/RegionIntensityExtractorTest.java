package css.cecprototype2;

import android.graphics.Bitmap;
import android.graphics.Color;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import css.cecprototype2.region_logic.Region;
import css.cecprototype2.region_logic.RegionIntensityExtractor;

public class RegionIntensityExtractorTest {

    private RegionIntensityExtractor intensityExtractor;
    private Bitmap mockBitmap;
    private Region mockRegion;

    @Before
    public void setUp() {
        intensityExtractor = new RegionIntensityExtractor();
        mockBitmap = createMockBitmap();
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
    private Bitmap createMockBitmap() {
        int width = 100;
        int height = 100;
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);

        // Set some pixels in the mockBitmap to simulate the region
        for (int x = 40; x < 60; x++) {
            for (int y = 40; y < 60; y++) {
                bitmap.setPixel(x, y, Color.rgb(0, 255, 0)); // Green color
            }
        }

        return bitmap;
    }
}