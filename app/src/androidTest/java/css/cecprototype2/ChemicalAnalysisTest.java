package css.cecprototype2;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class ChemicalAnalysisTest {

    @Mock
    RegionFinder mockRegionFinder;

    @Mock
    RegionIntensityExtractor mockIntensityExtractor;

    @Mock
    Bitmap mockCalibrationImage;

    @Mock
    Bitmap mockAnalysisImage;

    @Mock
    LinearRegression mockLinearRegression;

    @Mock
    Region mockRegion;

    @Mock
    SheetWriter mockSheetWriter;

    @InjectMocks
    private ChemicalAnalysis chemicalAnalysis;

    private Context context;

    @Before
    public void setUp() {
        // Obtain the context from ApplicationProvider
        context = ApplicationProvider.getApplicationContext();
        //FIXME: This doesn't work - java.lang.IllegalStateException: Could not initialize plugin: interface org.mockito.plugins.MockMaker
        MockitoAnnotations.openMocks(this);
        chemicalAnalysis.linearRegressionModel = mockLinearRegression;
        chemicalAnalysis.sheetWriter = new SheetWriter(context);
        mockRegion = new Region(0, 0, 0);
    }

    @Test
    public void testCalibrate() {
        // Set up mock data for the region and intensity
        double mockIntensity = 0.5;

        // Mock calibration data with the same size
        ArrayList<Double> mockCalibrationIntensities = new ArrayList<>(Arrays.asList(mockIntensity));
        ArrayList<Double> mockCalibrationConcentrations = new ArrayList<>(Arrays.asList(1.0));

        when(mockRegionFinder.getStandardRegions()).thenReturn(Arrays.asList(mockRegion));
        when(mockIntensityExtractor.getRegionIntensity(mockRegion, mockCalibrationImage)).thenReturn(mockIntensity);

        // Set up mock calibration data with the same size
        chemicalAnalysis.calibrationIntensities = mockCalibrationIntensities;
        chemicalAnalysis.calibrationConcentrations = mockCalibrationConcentrations;

        // Perform calibration
        chemicalAnalysis.Calibrate(mockRegionFinder, mockCalibrationImage);

        // Verify that linearRegressionModel is initialized
        LinearRegression linearRegressionModel = chemicalAnalysis.linearRegressionModel;
        assert linearRegressionModel != null;
    }

    @Test
    public void testAnalyze() {
        // Set up mock data for the region and intensity
        double mockFluorescence = 0.5;
        double mockPredictedConcentration = 0.3;

        ArrayList<Double> mockFluorescenceValues = new ArrayList<>(Arrays.asList(mockFluorescence, 0.0));
        ArrayList<Double> mockConcentrationValues = new ArrayList<>(Arrays.asList(mockPredictedConcentration, 0.0));

        mockLinearRegression = new LinearRegression(mockFluorescenceValues, mockConcentrationValues);
        chemicalAnalysis.linearRegressionModel = mockLinearRegression;

        // Mock behavior for RegionFinder
        when(mockRegionFinder.getStandardRegions()).thenReturn(Arrays.asList(mockRegion));

        // Mock behavior for RegionIntensityExtractor
        when(mockIntensityExtractor.getRegionIntensity(mockRegion, mockAnalysisImage))
                .thenReturn(mockFluorescence);

        // Perform analysis
        chemicalAnalysis.Analyze(mockRegionFinder, mockSheetWriter, mockAnalysisImage);

        // Verify that analysisReadings and analysisConcentrations are populated
        List<Double> analysisReadings = chemicalAnalysis.analysisReadings;
        List<Double> analysisConcentrations = chemicalAnalysis.analysisConcentrations;

        assertEquals(1, analysisReadings.size());
        assertEquals(1, analysisConcentrations.size());

        assertEquals(mockFluorescence, analysisReadings.get(0), 0.0);
        assertEquals(mockPredictedConcentration, analysisConcentrations.get(0), 0.0);

        // Add more assertions or verifications as needed
    }

    // Add more tests for the Analyze method and other functionalities as needed
}