package css.cecprototype2.main;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.camera.view.PreviewView;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import css.cecprototype2.analysis_logic.ChemicalAnalysis;
import css.cecprototype2.analysis_logic.SheetWriter;
import css.cecprototype2.region_logic.Region;
import css.cecprototype2.region_logic.RegionFinder;

public class MainViewModel extends AndroidViewModel {

    public SensorCamera cam;
    public Bitmap calibrationBitMap, analysisBitMap;
    Application application;
    RegionFinder regionFinder;
    SheetWriter sheetWriter;
    ChemicalAnalysis chemicalAnalysis;
    List<Region> regions;
    public List<Double> calibrationConcentraions;
    public List<Double> calibrationIntensities;

    public List<Double> analysisIntensities;
    private ImageStacker imageStacker;


    public MainViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        regions = new ArrayList<>();
        regionFinder = new RegionFinder(application.getApplicationContext());
        sheetWriter = new SheetWriter(application);
        chemicalAnalysis = new ChemicalAnalysis(sheetWriter);
    }
    public Bitmap getCalibrationBitmap()
    {
        return this.calibrationBitMap;
    }
    public Bitmap getAnalysisBitmap()
    {
        return this.analysisBitMap;
    }

    public Bitmap retrieveCalibrationBitmapFromCamera()
    {
        calibrationBitMap = cam.currentBitmap;
        return this.calibrationBitMap;
    }
    public void setImageStacker(ImageStacker inImageStacker)
    {
        this.imageStacker = inImageStacker;
    }

    public void doCalibration(String userNotes){
       Log.e("MainViewModel", "doCalibration() being called");
        if (getCalibrationBitmap() != null)
        {
            setupStandardRegions();

            calibrationIntensities = chemicalAnalysis.Calibrate(regionFinder, cam.imageFilename, userNotes, getCalibrationBitmap());
            setChemAnalysisCalibrationIntensities(calibrationIntensities);
        }
        else Log.e("MainViewModel", "doCalibration() received a null bitmap");
    }

//    public void doCalibrationFromValues(ArrayList <Double> inConcentrationValues)
//    {
//        if (getCalibrationBitmap() != null)
//        {
//            setupStandardRegions();
//            calibrationIntensities = chemicalAnalysis.CalibrateWithValues(regionFinder, getCalibrationBitmap(), inConcentrationValues);
//            setChemAnalysisCalibrationIntensities(calibrationIntensities);
//        }
//    }

    public Bitmap retrieveAnalysisBitmapFromCamera()
    {
        analysisBitMap = cam.currentBitmap;
        return this.analysisBitMap;
    }

    public void setChemAnalysisCalibrationIntensities(List<Double> intensities)
    {
        chemicalAnalysis.setCalibrationIntensities((ArrayList<Double>) intensities);
    }

    public void setChemAnalysisCalibrationConcentraions(List<Double> concentraions)
    {
        chemicalAnalysis.setCalibrationConcentrations((ArrayList<Double>) concentraions);
    }

    public boolean doAnalysis(){
        if (chemicalAnalysis.calibrateCompleted) {

            setupStandardRegions();
            Log.i("MainViewModel", ".doAnalysis(): Analysis Bitmap H: " + analysisBitMap.getHeight() + " | Analysis Bitmap W: " + analysisBitMap.getWidth());
            analysisIntensities = chemicalAnalysis.Analyze(regionFinder, sheetWriter, analysisBitMap);
            return true;
        } else {
            Log.i("MainViewModel", "Must do calibration before analsysis");
            return false;
        }
    }


    /**
     * calls regionFinder.findRegions to get all regions in a given image
     * @return a list of regions in the image
     */
    public List<Region> setupStandardRegions() //convert image into a list of regions
    {
        regions = regionFinder.getStandardRegions();
        return regions;
    }


    public void initializeCamera(SensorCamera sensorCamera) {
        Log.i("CIS4444","MainViewModel --- initializeCamera");
        cam = sensorCamera;
    }

    public void setCameraPreview(TextureView textureView) {
        Log.i("CIS4444","MainViewModel --- setCameraPreview cam = "+cam.toString());
        cam.textureView = textureView;
        cam.updateCameraPreview(textureView);
    }

    public void takePhoto() {
        Log.i("CIS4444", "MainViewModel --- takePhoto");

        // Ensure cam is not null
        if (cam != null) {
            Log.e("CIS4444", "MainViewModel --- calling cam.takePicture");

            cam.takePicture();

            // Ensure cam.currentBitmap is not null before assigning to calibrationBitMap
            /*  this will always be null because the camera takes photos asynch
            if (cam.currentBitmap != null) {
                this.calibrationBitMap = cam.currentBitmap;
            } else {
                Log.e("CIS4444", "MainViewModel --- takePhoto - cam.currentBitmap is null");
            }
            */

        } else {
            Log.e("CIS4444", "MainViewModel --- takePhoto - cam is null");
        }
    }

    public void setCalibrationBitMap(Bitmap sampleBitmap) {
        calibrationBitMap = sampleBitmap;
    }

    public void setAnalysisBitMap(Bitmap sampleBitmap) {
        analysisBitMap = sampleBitmap;
    }

    public LiveData<Boolean> getBitmapAvailableLiveData() {
        Log.i("CIS4444","MainViewModel --- getBitmapAvailableLiveData = "+ cam.getAvailableLiveData().toString());
        return cam.getAvailableLiveData();
    }

    public double getCalibrationRSq()
    {
        return chemicalAnalysis.getRSq();
    }

    public double getCalibrationSlope()
    {
        return chemicalAnalysis.getSlope();
    }

    public String getAnalysisIntensityAt(int index)
    {
        return analysisIntensities.get(index).toString();
    }

}
