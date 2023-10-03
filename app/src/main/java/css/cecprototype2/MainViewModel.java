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

public class MainViewModel extends AndroidViewModel {

    private SensorCamera cam;       // our camera object
    Image image;                    // most recent captured image
    Bitmap bitMap;
    Application application;

    public MainViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
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

    public Double analyzePixel(int x, int y) {
        int pixel;
        ChemicalAnalysis chemAnalysis = new ChemicalAnalysis();

        if (bitMap != null) {
            pixel = bitMap.getPixel(x, y);

            // Extract RGB values from the pixel
            int red = Color.red(pixel);
            int green = Color.green(pixel);
            int blue = Color.blue(pixel);


            // Get the chemical reading
            double chemicalReading = chemAnalysis.runAnalysis(red, green, blue);

            return chemicalReading;
        } else {
            // Handle the case when bitMap is null
            return -1.0;
        }
    }


}
