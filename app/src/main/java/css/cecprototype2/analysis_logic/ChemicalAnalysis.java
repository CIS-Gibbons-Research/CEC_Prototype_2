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
    private RegionIntensityExtractor intensityExtractor;
    private ArrayList<Double> calibrationIntensities;
    private ArrayList<Double> calibrationConcentrations;
    private ArrayList<Double> analysisReadings;
    private ArrayList<Double> analysisConcentrations;
    private LinearRegression linearRegressionModel;
    public boolean calibrateCompleted = false;              // flag showing calibration is done and analysis is available
    private SheetWriter sheetWriter;

    public Double getCalibrationIntensity(int key) {
        return calibrationConcentrations.get(key);
    }

    public Double getAnalysisReadings(int key) {
        return analysisReadings.get(key);
    }

    public Double getAnalysisConcentrations(int key) {
        return analysisConcentrations.get(key);
    }

    public ArrayList<Double> getCalibrationIntensities() {
        return calibrationIntensities;
    }

    public ArrayList<Double> getCalibrationConcentrations() {
        return calibrationConcentrations;
    }

    public ArrayList<Double> getAnalysisReadings() {
        return analysisReadings;
    }

    public ArrayList<Double> getAnalysisConcentrations() {
        return analysisConcentrations;
    }

    public LinearRegression getLinearRegressionModel() {
        return linearRegressionModel;
    }

    public void setCalibrationIntensities(ArrayList<Double> calibrationIntensities) {
        this.calibrationIntensities = calibrationIntensities;
    }

    public void setCalibrationConcentrations(ArrayList<Double> calibrationConcentrations) {
        this.calibrationConcentrations = calibrationConcentrations;
    }

    public void setAnalysisReadings(ArrayList<Double> analysisReadings) {
        this.analysisReadings = analysisReadings;
    }

    public void setAnalysisConcentrations(ArrayList<Double> analysisConcentrations) {
        this.analysisConcentrations = analysisConcentrations;
    }

    public void setCalibrateCompleted(boolean calibrateCompleted) {
        this.calibrateCompleted = calibrateCompleted;
    }

    public void setIntensityExtractor(RegionIntensityExtractor intensityExtractor) {
        this.intensityExtractor = intensityExtractor;
    }

    public void setLinearRegressionModel(LinearRegression linearRegressionModel) {
        this.linearRegressionModel = linearRegressionModel;
    }

    public void setSheetWriter(SheetWriter sheetWriter) {
        this.sheetWriter = sheetWriter;
    }

    public ChemicalAnalysis(SheetWriter sheetWriter) {
        //calibration concentrations provided by user, can be changed or placed in values.xml
        setSheetWriter(sheetWriter);
        calibrationConcentrations = new ArrayList<>(Arrays.asList(0.1, 0.3, 0.5, 0.7, 0.9, 1.1));
        Log.d("ChemicalAnalysis", "Calibration Concentrations Size: " + calibrationConcentrations.size());
        intensityExtractor = new RegionIntensityExtractor();
    }

    public List<Double> CalibrateWithValues(RegionFinder regionFinder, Bitmap fullCalibrationImage, ArrayList<Double> inCalibrationConcentrations)
    {
        calibrationIntensities = new ArrayList<>();
        int index = 0;
        for (Region region: regionFinder.getStandardRegions())
        {
            Double intensityFromValue = intensityExtractor.getRegionIntensityFromValues(region, fullCalibrationImage, inCalibrationConcentrations.get(index));
            calibrationIntensities.add(intensityFromValue);
            index++;
        }
        Log.d("ChemicalAnalysis", "Calibration Intensities From Values Size: " + calibrationIntensities.size());
        linearRegressionModel = new LinearRegression(calibrationIntensities, inCalibrationConcentrations);
        sheetWriter.writeCalibrationToSheets(calibrationIntensities);
        calibrateCompleted = true;
        return calibrationIntensities;
    }

    public List<Double> Calibrate(RegionFinder regionsFinder, Bitmap fullCalibrationImage) {
        calibrationIntensities = new ArrayList<>();
        //for each region, get intensity of given region from fullCalibrationImage parameter and add to 'calibrationReadings' list.
        for (Region region : regionsFinder.getStandardRegions()) {
            //Log.d("ChemicalAnalysis", "Region with x: " + region.getX() + "  and y: " + region.getX());
            Double currentCalibrationReading = intensityExtractor.getRegionIntensity(region, fullCalibrationImage);
            //Log.d("ChemicalAnalysis", "Region with x: " + region.getX() + " has " + currentCalibrationReading + " intensity.");
            calibrationIntensities.add(currentCalibrationReading);
        }
        Log.d("ChemicalAnalysis", "Calibration Intensities Size: " + calibrationIntensities.size());
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

    public double getSlope()
    {
        return linearRegressionModel.m_slope;
    }

    public double getRSq()
    {
        return linearRegressionModel.regression.getRSquare();
    }
}
