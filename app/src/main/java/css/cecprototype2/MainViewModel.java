package css.cecprototype2;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private SensorCamera cam;
    Bitmap calibrationBitMap, analysisBitMap;
    Application application;
    RegionFinder regionFinder;
    ChemicalAnalysis chemicalAnalysis;
    List<Region> regions;


    public MainViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        regions = new ArrayList<>();
        regionFinder = new RegionFinder();
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
        calibrationBitMap = takePhoto();
        setupStandardRegions();
        chemicalAnalysis.Calibrate(regionFinder,calibrationBitMap);
    }

    public void doAnalysis(){
        analysisBitMap = takePhoto();
        //setupRegions(analysisBitMap);
        setupStandardRegions();
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

    public Bitmap takePhoto() {
        Log.i("CIS4444","MainViewModel --- takePhoto");
        Bitmap bitmap = cam.capturePhotoBitmap();
        return bitmap;
    }

    public LiveData<Boolean> getBitmapAvailableLiveData() {
        //return cam.getBitmapAvailableLiveData();
        return null;
    }

}
