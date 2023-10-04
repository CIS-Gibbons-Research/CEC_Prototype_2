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

public class MainViewModel extends AndroidViewModel {

    private SensorCamera cam;       // our camera object
    Image image;                    // most recent captured image
    Bitmap bitMap;
    Application application;

    RegionFinder regionFinder;

    public MainViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        regionFinder = new RegionFinder();
    }

    public Image getImage()
    {
        return this.image;
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

//    public Double analyzePixel(int x, int y) {
//        int pixel;
//        ChemicalAnalysis chemAnalysis = new ChemicalAnalysis();
//
//        if (bitMap != null) {
//            pixel = bitMap.getPixel(x, y); //temp variable for incoming pixel
//
//            // Get the RGB values from the pixel and analyze using ChemicalAnalysis class and return as a double
//            return chemAnalysis.runAnalysis(Color.red(pixel), Color.green(pixel), Color.blue(pixel));
//        } else {
//            // Handle the case when bitMap is null
//            return -1.0;
//        }
//    }

    //convert image into a list of regions
    public List<Region> analyzeImage(Image img)
    {
        return regionFinder.findRegions(img);
    }


}
