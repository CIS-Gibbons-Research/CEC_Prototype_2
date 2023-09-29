package css.cecprototype2;

import android.app.Application;
import android.content.Context;
import android.media.Image;
import android.graphics.Bitmap;

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

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public void initializeCamera(SensorCamera sensorCamera) {
        cam = sensorCamera;
    }

    public void takePhoto() {
        image = cam.capturePhotoImage();
        bitMap = cam.capturePhotoBitmap();
    }

    public int getPixel(int x, int y) {
        // getting the pixel values from an Image seems hard
        //Image.Plane[] planes = image.getPlanes();
        //ByteBuffer buffer = planes[0].getBuffer();
        int pixel = bitMap.getPixel(x,y);
        return pixel;
    }




}
