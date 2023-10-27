package css.cecprototype2;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
public class ChemicalAnalysis {
    // Store the concentrations and fluorescence readings from the CALIBRATION (Step 1)
    private ArrayList<Double> calibrationReadings;
    private ArrayList<Double> calibrationConcentrations;

    // Store the concentrations and fluorescence readings from the ANALYSIS (Step 2)
    private ArrayList<Double> analysisReadings;
    private ArrayList<Double> analysisConcentrations;

    public Double getCalibrationReading(int key)
    {
        return calibrationReadings.get(key);
    }
    public Double getCalibrationConcentrations(int key)
    {
        return calibrationConcentrations.get(key);
    }

    public Double getAnalysisReadings(int key)
    {
        return analysisReadings.get(key);
    }
    public Double getAnalysisConcentrations(int key)
    {
        return analysisConcentrations.get(key);
    }

    private LinearRegression linearRegressionModel;

    /**
     * constructor --
     */
    public ChemicalAnalysis()
    {
        // initialize the concentration levels for calibaraions -- TODO: Do this somewhere else?
        calibrationConcentrations = new ArrayList<>(Arrays.asList(0.1, 0.3, 0.5, 0.7, 0.9));
    }

    /**
     *  STEP 1 --Run Calibrate first with an image from the calibration screen
     *     This will analyze the calibration values and set up the LinearRegression model for analysis
     * @param regions
     * @param fullCalibrationImage
     */
    public void Calibrate(RegionFinder regions, Bitmap fullCalibrationImage)
    {
        for (Region region : regions.getAllRegions())
        {
            Bitmap bitmapRegion = region.getBitmapRegion(fullCalibrationImage);
            calibrationReadings.add( runSingleAnalysis(bitmapRegion));
        }
        linearRegressionModel = new LinearRegression(calibrationReadings,calibrationConcentrations);

    }

    /**
     *  STEP 2 --Run Analyze second with an image from the analyze screen
     *     This will analyze the values and set up the LinearRegression model for analysis
     * @param regions
     * @param fullAnalysisImage
     */
    public void Analyze(RegionFinder regions, Bitmap fullAnalysisImage)
    {
        for (Region region : regions.getAllRegions())
        {
            Bitmap bitmapRegion = region.getBitmapRegion(fullAnalysisImage);
            Double currentAnalysisFluorescence = runSingleAnalysis(bitmapRegion);
            analysisReadings.add(currentAnalysisFluorescence );
            analysisConcentrations.add(linearRegressionModel.predict(currentAnalysisFluorescence));
        }
    }


    /**
     * Method to calculate and set the chemical reading based on RGB values
     * TODO: implement code processing via linear regression model
     * @param bitmapRegion
     * @return the processed analysis as a Double value.
     */
    public Double runSingleAnalysis(Bitmap bitmapRegion)
    {
        // analyze bitmap here
        int pixel = bitmapRegion.getPixel(100,100); //temp variable for incoming pixel
        int greenValue = Color.green(pixel);
        Double exampleValue = Double.valueOf(greenValue );
        // TODO -- currenlty just grabs the green value of one pixel at 100,100. Should average region

        return exampleValue;
    }



}
