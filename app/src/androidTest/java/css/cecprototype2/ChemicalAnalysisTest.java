package css.cecprototype2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import css.cecprototype2.analysis_logic.ChemicalAnalysis;
import css.cecprototype2.analysis_logic.LinearRegression;
import css.cecprototype2.analysis_logic.SheetWriter;
import css.cecprototype2.region_logic.Region;
import css.cecprototype2.region_logic.RegionFinder;
import css.cecprototype2.region_logic.RegionIntensityExtractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class ChemicalAnalysisTest {

    private static final double MOCK_INTENSITY = 0.5;

    private RegionFinder regionFinder;
    private RegionIntensityExtractor mockIntensityExtractor;
    private Bitmap mockCalibrationImage;
    private LinearRegression mockLinearRegression;
    private SheetWriter mockSheetWriter;

    private ChemicalAnalysis chemicalAnalysis;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();

        // Instantiate objects
        regionFinder = new RegionFinder(context);
        mockIntensityExtractor = new RegionIntensityExtractor();
        mockCalibrationImage = createMockImage();

        // Use ArrayList for mock data
        List<Double> mockFluorescenceValues = new ArrayList<>(Arrays.asList(MOCK_INTENSITY, 0.0));
        List<Double> mockConcentrationValues = new ArrayList<>(Arrays.asList(0.3, 0.0));

        // Ensure both lists have the same size
        assertEquals(mockFluorescenceValues.size(), mockConcentrationValues.size());

        // Instantiate mockLinearRegression with List parameters
        mockLinearRegression = new LinearRegression(mockFluorescenceValues, mockConcentrationValues);

        mockSheetWriter = new SheetWriter(context);

        // Instantiate the ChemicalAnalysis class
        chemicalAnalysis = new ChemicalAnalysis(mockSheetWriter);
        chemicalAnalysis.setLinearRegressionModel(mockLinearRegression);
        chemicalAnalysis.setSheetWriter(mockSheetWriter);
    }

    @Test
    public void testCalibrate() {
        // Set up mock data for the region and intensity
        double mockIntensity = MOCK_INTENSITY;
        regionFinder = new RegionFinder(this.context);

        // Set up mock data for the region and intensity
        double mockFluorescence = MOCK_INTENSITY;
        double mockPredictedConcentration = 0.3;

        // Create lists with size 6
        ArrayList<Double> mockFluorescenceValues = new ArrayList<>(Arrays.asList(mockFluorescence, 0.0, 0.0, 0.0, 0.0, 0.0));
        ArrayList<Double> mockConcentrationValues = new ArrayList<>(Arrays.asList(mockPredictedConcentration, 0.0, 0.0, 0.0, 0.0, 0.0));

        // Perform calibration
        chemicalAnalysis.Calibrate(regionFinder, mockCalibrationImage);

        // Verify that linearRegressionModel is initialized
        LinearRegression linearRegressionModel = chemicalAnalysis.getLinearRegressionModel();
        assertNotNull(linearRegressionModel);
    }

    @Test
    public void testAnalyze() {
        // Set up mock data for the region and intensity
        double mockFluorescence = MOCK_INTENSITY;
        double mockPredictedConcentration = 0.3;

        ArrayList<Double> mockFluorescenceValues = new ArrayList<>(Arrays.asList(mockFluorescence, 0.0));
        ArrayList<Double> mockConcentrationValues = new ArrayList<>(Arrays.asList(mockPredictedConcentration, 0.0));

        mockLinearRegression = new LinearRegression(mockFluorescenceValues, mockConcentrationValues);
        chemicalAnalysis.setLinearRegressionModel(mockLinearRegression);

        // Perform analysis
        chemicalAnalysis.Analyze(regionFinder, mockSheetWriter, mockCalibrationImage);

        // Verify that analysisReadings and analysisConcentrations are populated
        List<Double> analysisReadings = chemicalAnalysis.getAnalysisReadings();
        List<Double> analysisConcentrations = chemicalAnalysis.getAnalysisConcentrations();

        assertEquals(6, analysisReadings.size());
        assertEquals(6, analysisConcentrations.size());

        assertEquals(mockFluorescence, analysisReadings.get(0), 0.5);
        assertEquals(mockPredictedConcentration, analysisConcentrations.get(0), 0.5);
    }

    // Utility method to create a mock image
    private Bitmap createMockImage() {
        Bitmap bitmap = Bitmap.createBitmap(3024, 4032, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawCircle(100, 100, 50, paint);  // Draw a circle as a mock region
        return bitmap;
    }
}