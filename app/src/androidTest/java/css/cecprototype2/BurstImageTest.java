package css.cecprototype2;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import css.cecprototype2.fragments.FragmentAnalyze;
import css.cecprototype2.main.MainViewModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class BurstImageTest {

    //private Bitmap bitmap1, bitmap2;
    private MainViewModel mainViewModel;
    private FragmentAnalyze fragmentAnalyze;

    @Before
    public void setUp() {
        mainViewModel = new MainViewModel(ApplicationProvider.getApplicationContext());
        fragmentAnalyze = new FragmentAnalyze();
        fragmentAnalyze.setMainViewModel(mainViewModel);
    }

    @Test
    public void testBurstImages() {
        // Create a list of sample bitmaps for testing
        List<Bitmap> burstBitmaps = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            burstBitmaps.add(Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888));
        }

        // Set the ImageView in FragmentAnalyze
        ImageView imageView = new ImageView(ApplicationProvider.getApplicationContext());
        fragmentAnalyze.setTestImageView(imageView);

        // Call the consolidateBurstImages method
        Bitmap consolidatedBitmap = fragmentAnalyze.consolidateBurstImages(burstBitmaps);

        // Verify that the consolidated bitmap is not null
        assertNotNull(consolidatedBitmap);

        // Ensure that the imageView is set with a non-null bitmap
        assertNotNull(imageView.getDrawable());

        // Ensure that the displayed bitmap matches the expected consolidated bitmap
        assertEquals(consolidatedBitmap, ((BitmapDrawable) imageView.getDrawable()).getBitmap());
    }
}