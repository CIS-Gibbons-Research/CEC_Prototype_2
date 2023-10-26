package css.cecprototype2;

import android.app.Application;
import android.graphics.Color;
import android.media.Image;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Map;

public class MainViewModel extends AndroidViewModel {

    private SensorCamera cam;
    Bitmap bitMap;
    Application application;
    RegionFinder regionFinder;
    CircleIntensityExtractor circleIntensityExtractor;

    public MainViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        regionFinder = new RegionFinder();
    }
    public Bitmap getBitmap()
    {
        return this.bitMap;
    }

    /**
     *
     * @param circleIntensityMap map to display
     * @return a formatted String that can be displayed to UI
     */
    public String updateUIWithCircleIntensities(Map<String, Integer> circleIntensityMap) //update UI with region map
    {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : circleIntensityMap.entrySet())
        {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }

    /**
     * populates a map of all regions with their index as a key and returns it
     * @return a map of all regions
     * FIXME update map key values with index or other useful information
     */
    public Map<String, Integer> getRegionMap()
    {
        List<Region> regions = analyzeCapturedImage(bitMap); // initialize list of regions
        Map<String, Integer> intensityMap = null; //reset regions map

        // Calculate average intensities for each region and place in intensity map
        for (int i = 0; i < regions.size(); i++)
        {
            intensityMap.put("Region " + i + ": ", circleIntensityExtractor.getRegionIntensity(regions.get(i)));
        }
        return intensityMap;
    }

    /**
     * calls regionFinder.findRegions to get all regions in a given image
     * @param bitmap image to be analyzed
     * @return a list of regions in the image
     */
    public List<Region> analyzeCapturedImage(Bitmap bitmap) //convert image into a list of regions
    {
        List<Region> regions = regionFinder.findRegions(bitmap);
        return regions;
    }

    public void initializeCamera(SensorCamera sensorCamera) {
        cam = sensorCamera;
    }

    public void takePhoto() {
        Log.i("CIS4444","MainViewModel --- takePhoto");
        Toast.makeText(application, "MainViewModel --- takePhoto", Toast.LENGTH_SHORT );

        //image = cam.capturePhotoImage();
        bitMap = cam.capturePhotoBitmap();
    }

    public int analyzePixel(int x, int y) {
        int pixel;
        //ChemicalAnalysis chemAnalysis = new ChemicalAnalysis();
        if (bitMap != null) {
            pixel = bitMap.getPixel(x, y); //temp variable for incoming pixel
           return Color.red(pixel) + Color.green(pixel) + Color.blue(pixel);
        } else {
            // Handle the case when bitMap is null
            Log.e("CIS4444","MainViewModel --- analyzePixel -- error with null bitmap");
            return -1;
        }
    }


    public LiveData<Boolean> getBitmapAvailableLiveData() {
        //return cam.getBitmapAvailableLiveData();
        return null;
    }

}
