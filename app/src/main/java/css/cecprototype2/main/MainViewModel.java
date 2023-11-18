package css.cecprototype2.main;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.Preview;
import androidx.camera.view.PreviewView;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import css.cecprototype2.R;
import css.cecprototype2.analysis_logic.ChemicalAnalysis;
import css.cecprototype2.region_logic.Region;
import css.cecprototype2.region_logic.RegionFinder;

public class MainViewModel extends AndroidViewModel {

    private SensorCamera cam;
    public Bitmap calibrationBitMap, analysisBitMap;
    Application application;
    RegionFinder regionFinder;
    ChemicalAnalysis chemicalAnalysis;
    List<Region> regions;
    public List<Double> calibrationIntensities;


    public MainViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        regions = new ArrayList<>();
        regionFinder = new RegionFinder(application.getApplicationContext());
        chemicalAnalysis = new ChemicalAnalysis();
    }
    public Bitmap getCalibrationBitmap()
    {
        return this.calibrationBitMap;
    }
    public Bitmap getAnalysisBitMapBitmap()
    {
        return this.analysisBitMap;
    }

    public void doCalibration(){
        //calibrationBitMap = takePhoto();
        setupStandardRegions();
        Log.i("MainViewModel", ".doCalibration(): Calibration Bitmap H: " + calibrationBitMap.getHeight() + " | Calibration Bitmap W: " + calibrationBitMap.getWidth());
        calibrationIntensities = chemicalAnalysis.Calibrate(regionFinder,calibrationBitMap);
    }

    public void doAnalysis(){
        //analysisBitMap = takePhoto();
        //setupRegions(analysisBitMap);
        setupStandardRegions();
        Log.i("MainViewModel", ".doAnalysis(): Analysis Bitmap H: " + analysisBitMap.getHeight() + " | Analysis Bitmap W: " + analysisBitMap.getWidth());
        chemicalAnalysis.Analyze(regionFinder,analysisBitMap);
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
        cam = sensorCamera;
    }

    public void setCameraPreview(PreviewView preview) {
        cam.previewView = preview;
    }

    public Bitmap takePhoto() {
        Log.i("CIS4444","MainViewModel --- takePhoto");
        //commented out for testing -- use a bitmap of sample_image_a instead of capturing an image
        Bitmap bitmap = cam.capturePhotoBitmap();
        //Bitmap bitmap = BitmapFactory.decodeResource(application.getResources(), R.drawable.sample_image_a);
        Log.i("MainViewModel", ".takePhoto(): Standard Bitmap H: " + bitmap.getHeight() + " | Standard Bitmap W: " + bitmap.getWidth());
        return bitmap;
    }

    public void setCalibrationBitMap(Bitmap sampleBitmap) {
        calibrationBitMap = sampleBitmap;
    }

    public void setAnalysisBitMap(Bitmap sampleBitmap) {
        analysisBitMap = sampleBitmap;
    }

    // TODO: This is causing an error

//    public LiveData<Boolean> getBitmapAvailableLiveData() {
//        return cam.getAvailableLiveData();
//    }

}
