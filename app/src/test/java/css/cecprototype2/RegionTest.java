package css.cecprototype2;

import junit.framework.TestCase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import org.junit.Test;

import static org.junit.Assert.*;

public class RegionTest {

    @Test
    public void testGetBitmapRegion_ValidInputs() {
        // Test when the inputs are valid

        // Create a sample wholeImageBitmap
        Bitmap wholeImageBitmap = createSampleBitmap();

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

        // Create a sample wholeImageBitmap
        Bitmap wholeImageBitmap = createSampleBitmap();

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

        // Create a sample wholeImageBitmap
        Bitmap wholeImageBitmap = createSampleBitmap();

        // Create a Region object with radius zero
        Region regionZeroRadius = new Region(50, 50, 0);
        // Call the getBitmapRegion method
        Bitmap regionBitmapZeroRadius = regionZeroRadius.getBitmapRegion(wholeImageBitmap);
        // Verify that the result is null
        assertNull(regionBitmapZeroRadius);
    }

    // Helper method to create a sample bitmap for testing
    private Bitmap createSampleBitmap() {
        int width = 100;
        int height = 100;
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);

        // Draw a square in the center of the bitmap
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        Rect rect = new Rect(40, 40, 60, 60);
        canvas.drawRect(rect, paint);

        return bitmap;
    }
}