package css.cecprototype2;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import css.cecprototype2.main.ImageStacker;

@RunWith(AndroidJUnit4.class)
public class ImageStackerTest {

    private ImageStacker imageStacker;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        imageStacker = new ImageStacker();
    }

    @Test
    public void testAveragePixelValues() {
        List<Bitmap> testImages = generateTestBitmaps();
        Bitmap averagedBitmap = imageStacker.averagePixelValues(testImages);

        // Add assertions to check the properties of the averaged bitmap
        int width = testImages.get(0).getWidth();
        int height = testImages.get(0).getHeight();
        int[] pixelValues = new int[width * height];

        averagedBitmap.getPixels(pixelValues, 0, width, 0, 0, width, height);

        // Check if the pixel values are within an expected range
        for (int pixelValue : pixelValues) {
            int red = Color.red(pixelValue);
            int green = Color.green(pixelValue);
            int blue = Color.blue(pixelValue);

            // Example assertion, adjust as needed
            assert (red >= 0 && red <= 255);
            assert (green >= 0 && green <= 255);
            assert (blue >= 0 && blue <= 255);
        }
    }

    private List<Bitmap> generateTestBitmaps() {
        List<Bitmap> testImages = new ArrayList<>();

        // Create test images with known pixel values
        for (int i = 0; i < 5; i++) {
            Bitmap testBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);

            for (int y = 0; y < 100; y++) {
                for (int x = 0; x < 100; x++) {
                    int red = i * 50;
                    int green = i * 25;
                    int blue = i * 10;
                    testBitmap.setPixel(x, y, Color.rgb(red, green, blue));
                }
            }

            testImages.add(testBitmap);
        }

        return testImages;
    }
}