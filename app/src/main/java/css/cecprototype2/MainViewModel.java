package css.cecprototype2;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageProxy;
import androidx.camera.view.PreviewView;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModel;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainViewModel extends AndroidViewModel {

    private SensorCamera cam;       // our camera object
    Image image;                    // most recent captured image
    Bitmap bitMap;
    Application application;

    RegionFinder regionFinder;
    CircleIntensityExtractor circleIntensityExtractor;

    public MainViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        regionFinder = new RegionFinder();
    }

    public Image getImage()
    {
        return this.image;
    }

    public String updateUIWithCircleIntensities(Map<String, Integer> circleIntensityMap) //update UI with region map
    {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : circleIntensityMap.entrySet())
        {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }

    //get region map for given image
    public Map<String, Integer> getRegionMap()
    {
        List<Region> regions = analyzeCapturedImage(image); // initialize list of regions
        Map<String, Integer> intensityMap = null; //reset regions map

        // Calculate average intensities for each region and place in intensity map
        for (int i = 0; i < regions.size(); i++)
        {
            intensityMap.put("Region " + i + ": ", circleIntensityExtractor.getRegionIntensity(regions.get(i)));
        }
        return intensityMap;
    }

    public List<Region> analyzeCapturedImage(Image image) //convert image into a list of regions
    {
        List<Region> regions = analyzeImage(image);
        return regions;
    }

    public void initializeCamera(SensorCamera sensorCamera) {
        cam = sensorCamera;
    }

    public void takePhoto() {
        Log.i("CIS4444","MainViewModel --- takePhoto");
        Toast.makeText(application, "MainViewModel --- takePhoto", Toast.LENGTH_SHORT );

        image = cam.capturePhotoImage();
        bitMap = cam.capturePhotoBitmap();
    }

    //convert image into a list of regions
    public List<Region> analyzeImage(Image img)
    {
        return regionFinder.findRegions(img);
    }


}
