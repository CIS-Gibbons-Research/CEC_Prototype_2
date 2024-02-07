package css.cecprototype2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import css.cecprototype2.region_logic.Region;

public class RegionTest {

    private Context context;
    Bitmap wholeImageBitmap;
    @Before
    public void setUp()
    {
        context = ApplicationProvider.getApplicationContext();
        wholeImageBitmap = createMockBitmap(context);
    }
    @Test
    public void testGetBitmapRegion_ValidInputs() {
        // Test when the inputs are valid

        // Create a Region object
        Region region = new Region(50, 50, 20);

        // Call the getBitmapRegion method
        Bitmap regionBitmap = region.getBitmapRegion(wholeImageBitmap);

        // Verify that the result is not null
        assertNotNull(regionBitmap);

        // Verify the dimensions of the resulting bitmap
        assertEquals(40, regionBitmap.getWidth()); // 2 * radius
        assertEquals(40, regionBitmap.getHeight()); // 2 * radius
    }

    @Test
    public void testGetBitmapRegion_NegativeInputs() {
        // Test when x or y is negative
        // Create a Region object with negative x
        Region regionNegativeX = new Region(-10, 50, 20);
        // Call the getBitmapRegion method
        Bitmap regionBitmapNegativeX = regionNegativeX.getBitmapRegion(wholeImageBitmap);
        // Verify that the result is null
        assertNull(regionBitmapNegativeX);

        // Create a Region object with negative y
        Region regionNegativeY = new Region(50, -10, 20);
        // Call the getBitmapRegion method
        Bitmap regionBitmapNegativeY = regionNegativeY.getBitmapRegion(wholeImageBitmap);
        // Verify that the result is null
        assertNull(regionBitmapNegativeY);
    }

    @Test
    public void testGetBitmapRegion_ZeroRadius() {
        // Test when the radius is zero

        // Create a Region object with radius zero
        Region regionZeroRadius = new Region(50, 50, 0);
        // Call the getBitmapRegion method
        Bitmap regionBitmapZeroRadius = regionZeroRadius.getBitmapRegion(wholeImageBitmap);
        // Verify that the result is null
        assertNull(regionBitmapZeroRadius);
    }

    // Helper method to create a sample bitmap for testing
    private Bitmap createMockBitmap(Context context) {
        // Load the Bitmap from the resource
        int resourceId = R.drawable.sample_a; // Assuming 'sample_a.jpg' is in the res/drawable folder
        return BitmapFactory.decodeResource(context.getResources(), resourceId);
    }
}