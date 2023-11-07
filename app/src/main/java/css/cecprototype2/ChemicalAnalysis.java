package css.cecprototype2;

import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.Arrays;

public class ChemicalAnalysis {
    RegionIntensityExtractor intensityExtractor;
    private ArrayList<Double> calibrationIntensities;
    private ArrayList<Double> calibrationConcentrations;
    private ArrayList<Double> analysisReadings;
    private ArrayList<Double> analysisConcentrations;
    private LinearRegression linearRegressionModel;


    public Double getCalibrationIntensity(int key) {
        return calibrationConcentrations.get(key);
    }

    public Double getAnalysisReadings(int key) {
        return analysisReadings.get(key);
    }

    public Double getAnalysisConcentrations(int key) {
        return analysisConcentrations.get(key);
    }

    public ChemicalAnalysis() {
        //calibration concentrations provided by user, can be changed or placed in values.xml
        calibrationConcentrations = new ArrayList<>(Arrays.asList(0.1, 0.3, 0.5, 0.7, 0.9, 1.0));
        intensityExtractor = new RegionIntensityExtractor();
    }

    public void Calibrate(RegionFinder regions, Bitmap fullCalibrationImage) {
        calibrationIntensities = new ArrayList<>();
        //for each region, get intensity of given region from fullCalibrationImage parameter and add to 'calibrationReadings' list.
        for (Region region : regions.getStandardRegions()) {
            Double currentCalibrationReading = intensityExtractor.getRegionIntensity(region, fullCalibrationImage);
            calibrationIntensities.add(currentCalibrationReading);
        }
        //TODO: calibration readings are all 0.0, so intesnsityExtractor isn't working
        linearRegressionModel = new LinearRegression(calibrationIntensities, calibrationConcentrations);
    }

    public void Analyze(RegionFinder regions, Bitmap fullAnalysisImage) {
        analysisReadings = new ArrayList<>();
        analysisConcentrations = new ArrayList<>();
        //for each region, get intensity of given region from fullAnalysisImage parameter and add it to analysisReadings list, then run LRM.predict() and add to analysisConcentrations.
        for (Region region : regions.getStandardRegions()) {
            Double currentAnalysisFluorescence = intensityExtractor.getRegionIntensity(region, fullAnalysisImage);
            analysisReadings.add(currentAnalysisFluorescence);
            analysisConcentrations.add(linearRegressionModel.predict(currentAnalysisFluorescence));
        }
    }
}
