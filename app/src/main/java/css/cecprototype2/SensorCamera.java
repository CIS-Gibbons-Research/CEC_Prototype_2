package css.cecprototype2;

import static androidx.camera.core.ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.core.content.ContextCompat;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;

import com.google.common.util.concurrent.ListenableFuture;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SensorCamera {

    // Constructor: initialize camera
    SensorCamera(Context context, PreviewView previewView) {
        // should be passed the application context which is needed by the camera
        // should also be passed the previewView on the screen where the image should be displayed
        // TODO: is there a better way to connect the camera to the previewView?  Will the previewView change when the phone is rotated?

        startCameraProvider( context,  previewView);
    }

    private Executor executor = Executors.newSingleThreadExecutor();
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    Context context;        // the app context we are running in
    ImageCapture imageCapture;
    ImageAnalysis imageAnalysis;
    Preview imagePreview;
    Bitmap bitmap;      // Bitmap from the image proxy
    Image image;        // Image from the image proxy

    private void startCameraProvider(Context context, PreviewView previewView) {

        cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        //cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        //cameraProviderFuture = ProcessCameraProvider.getInstance(context).get();

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider, previewView);
                //bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(context));
    }

    void startCameraX(@NonNull ProcessCameraProvider cameraProvider, PreviewView previewView){
        //Camera Selector Use Case
        cameraProvider.unbind();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        // Preview Use Case
        imagePreview = new Preview.Builder().build();
        imagePreview.setSurfaceProvider(previewView.getSurfaceProvider());

        // Image Analysis Use Case
        imageAnalysis = new ImageAnalysis.Builder()
                // enable the following line if RGBA output is needed.
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                //.setTargetResolution(new Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        // Image Capture Use Case
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();
        // TODO fix line below
        //cameraProvider.bindToLifecycle(context, cameraSelector, imageAnalysis, imageCapture, imagePreview);
    }

    public void capturePhoto() {
        // public abstraction to take photo.
        // Currently calls analyze photo, but could capture photo to save to disk later...
        // The UI should call this through the MainViewModel
        analyzePhotoProvider();
    }
    private void analyzePhotoProvider() {
        Log.i("CIS4444","Trying to Analyze Photo --- 111");
        executor = Executors.newSingleThreadExecutor();
        Log.i("CIS4444","Trying to Analyze Photo --- 222");
        imageAnalysis.setAnalyzer(executor, new ImageAnalysis.Analyzer() {
            @OptIn(markerClass = ExperimentalGetImage.class) @Override
            public void analyze(@NonNull ImageProxy imageProxy) {
                Log.i("CIS4444","Trying to Analyze Photo --- analyze callback 1");
                int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
                Log.i("CIS4444","analyze callback 1 --- rotationDegrees = "+rotationDegrees);
                // Get the image proxy plane's buffer which is where the pixels are
                ImageProxy.PlaneProxy[] planes = imageProxy.getPlanes();
                Log.i("CIS4444","analyze callback 1 --- planes = "+planes.length);

                ByteBuffer buffer = planes[0].getBuffer();
                // Create a blank bitmap
                Log.i("CIS4444","analyze callback 2");
                bitmap = imageProxy.toBitmap();
                image = imageProxy.getImage();
                //bitmap = Bitmap.createBitmap(imageProxy.getWidth(),imageProxy.getHeight(),Bitmap.Config.ARGB_8888);
                // copy the image proxy plane into the bitmap
                //bitmap.copyPixelsFromBuffer(buffer);
                Log.i("CIS4444", "analyze callback 2 --- bmp height = "+bitmap.getHeight());

                // TODO: add code to crop to just the needed area of the photo
                //bitmap = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height())

                // TODO: notify some software that the image has been updated

                // after done, release the ImageProxy object
                imageProxy.close();
            }
        });
    }

}
