package css.cecprototype2.analysis_logic;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import css.cecprototype2.region_logic.Region;
import css.cecprototype2.region_logic.RegionFinder;
import css.cecprototype2.region_logic.RegionIntensityExtractor;

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
            Log.d("ChemicalAnalysis", "Region with x: " + region.getX() + " has " + currentCalibrationReading + " intensity.");
            calibrationIntensities.add(currentCalibrationReading);
        }
        //FIXME: calibration readings are all 0.0, so intensityExtractor isn't working -- possibly wrong values for locations
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

    public void writeToGoogleSheets() {
        String spreadsheetId = "yourSpreadsheetId";
        String sheetName = "Sheet1"; // Change to the name of your sheet
        int numRowsToSkip = 1; // Skip header row

        try {
            // Get the existing data to determine the next available row
            List<List<Object>> existingData = SheetsReader.readFromGoogleSheets(spreadsheetId, sheetName, numRowsToSkip);
            int nextRow = existingData.size() + numRowsToSkip + 1; // Add 1 to skip header row

            // Prepare the new data
            List<List<Object>> values = new ArrayList<>();
            values.add(Arrays.asList("Concentration", "Analysis Value"));

            for (int i = 0; i < analysisConcentrations.size(); i++) {
                values.add(Arrays.asList(calibrationConcentrations.get(i), analysisConcentrations.get(i)));
            }

            // Append the new data to the end of the sheet
            SheetsWriter.appendRowsToGoogleSheets(spreadsheetId, sheetName, values, nextRow);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }
    }
}
