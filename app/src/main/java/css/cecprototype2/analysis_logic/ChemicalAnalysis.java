package css.cecprototype2.analysis_logic;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import css.cecprototype2.region_logic.Region;
import css.cecprototype2.region_logic.RegionFinder;
import css.cecprototype2.region_logic.RegionIntensityExtractor;

public class ChemicalAnalysis {
    public RegionIntensityExtractor intensityExtractor;
    public List<Region> regions;
    public ArrayList<Double> calibrationIntensities;
    public ArrayList<Double> calibrationConcentrations;
    public ArrayList<Double> analysisReadings;
    public ArrayList<Double> analysisConcentrations;
    public LinearRegression linearRegressionModel;
    public boolean calibrateCompleted = false;              // flag showing calibration is done and analysis is available

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

    public List<Double> Calibrate(RegionFinder regionsFinder, SheetWriter sheetWriter, Bitmap fullCalibrationImage) {
        calibrationIntensities = new ArrayList<>();
        //for each region, get intensity of given region from fullCalibrationImage parameter and add to 'calibrationReadings' list.
            regions = regionsFinder.getStandardRegions();
        for (Region region : regions) {
            //Log.d("ChemicalAnalysis", "Region with x: " + region.getX() + "  and y: " + region.getX());
            Double currentCalibrationReading = intensityExtractor.getRegionIntensity(region, fullCalibrationImage);
            //Log.d("ChemicalAnalysis", "Region with x: " + region.getX() + " has " + currentCalibrationReading + " intensity.");
            calibrationIntensities.add(currentCalibrationReading);
        }
        //FIXME: calibration readings are all 0.0, so intensityExtractor isn't working -- possibly wrong values for locations
        linearRegressionModel = new LinearRegression(calibrationIntensities, calibrationConcentrations);
        sheetWriter.writeCalibrationToSheets(calibrationIntensities);
        calibrateCompleted = true;          // turn on flag that calibration is done and analysis can be performed.
        return calibrationIntensities;
    }

    public List<Double> Analyze(RegionFinder regions, SheetWriter sheetWriter, Bitmap fullAnalysisImage) {
        analysisReadings = new ArrayList<>();
        analysisConcentrations = new ArrayList<>();
        //for each region, get intensity of given region from fullAnalysisImage parameter and add it to analysisReadings list, then run LRM.predict() and add to analysisConcentrations.
        for (Region region : regions.getStandardRegions()) {
            Double currentAnalysisFluorescence = intensityExtractor.getRegionIntensity(region, fullAnalysisImage);
            analysisReadings.add(currentAnalysisFluorescence);
            analysisConcentrations.add(linearRegressionModel.predict(currentAnalysisFluorescence));
        }
        sheetWriter.writeAnalysisToSheets(analysisConcentrations);
        return analysisConcentrations;
    }
}
