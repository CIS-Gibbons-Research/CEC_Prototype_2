package css.cecprototype2;

import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.Arrays;

public class ChemicalAnalysis {
    private ArrayList<Double> calibrationReadings;
    private ArrayList<Double> calibrationConcentrations;
    private ArrayList<Double> analysisReadings;
    private ArrayList<Double> analysisConcentrations;
    private LinearRegression linearRegressionModel;

    public Double getCalibrationReading(int key) {
        return calibrationReadings.get(key);
    }

    public Double getCalibrationConcentrations(int key) {
        return calibrationConcentrations.get(key);
    }

    public Double getAnalysisReadings(int key) {
        return analysisReadings.get(key);
    }

    public Double getAnalysisConcentrations(int key) {
        return analysisConcentrations.get(key);
    }

    public ChemicalAnalysis() {
        //calibration concentrations provided by user
        calibrationConcentrations = new ArrayList<>(Arrays.asList(0.1, 0.3, 0.5, 0.7, 0.9));
    }

    public void Calibrate(RegionFinder regions, Bitmap fullCalibrationImage) {
        calibrationReadings = new ArrayList<>();

        for (Region region : regions.getStandardRegions()) {
            RegionIntensityExtractor intensityExtractor = new RegionIntensityExtractor();
            Bitmap bitmapRegion = region.getBitmapRegion(fullCalibrationImage);
            Double currentCalibrationReading = intensityExtractor.getRegionIntensity(region, fullCalibrationImage);
            calibrationReadings.add(currentCalibrationReading);
        }
        linearRegressionModel = new LinearRegression(calibrationReadings, calibrationConcentrations);
    }

    public void Analyze(RegionFinder regions, Bitmap fullAnalysisImage) {
        analysisReadings = new ArrayList<>();
        analysisConcentrations = new ArrayList<>();

        RegionIntensityExtractor intensityExtractor = new RegionIntensityExtractor();

        for (Region region : regions.getStandardRegions()) {
            Bitmap bitmapRegion = region.getBitmapRegion(fullAnalysisImage);
            Double currentAnalysisFluorescence = intensityExtractor.getRegionIntensity(region, fullAnalysisImage);
            analysisReadings.add(currentAnalysisFluorescence);
            analysisConcentrations.add(linearRegressionModel.predict(currentAnalysisFluorescence));
        }
    }
}
