package css.cecprototype2.main;

import static androidx.camera.core.ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CaptureRequest;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.camera.camera2.interop.Camera2Interop;
import androidx.camera.camera2.interop.ExperimentalCamera2Interop;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.core.content.ContextCompat;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import android.graphics.Bitmap;
import android.media.Image;
import android.view.Surface;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;


public class SensorCamera {
    private MutableLiveData<Boolean> bitmapAvailableLiveData;   // LiveData to notify the fragments when the photo is ready
    LifecycleOwner  lifecycleOwner;     // the app context cast to a lifecycleOwner needed for camera
    Context context;                    // the app context we are running in
    PreviewView previewView;            // preview widget on fragment's UI
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    ProcessCameraProvider cameraProvider;   // CameraX provider built from Future above
    ImageCapture imageCapture;              // CameraX imageCaputer object
    Preview imagePreview;                   // CameraX preview object
    private Executor executor = Executors.newSingleThreadExecutor();    // thread to run CameraX
    Bitmap currentBitmap;      // Bitmap from the image proxy

    // Constructor: initialize camera
    public SensorCamera(Activity mainActivity, LifecycleOwner  lifecycleOwner) {
        // should be passed the application context which is needed by the camera
        // should also be passed the lifecycleOwner which is also the mainActivity cast to this
        bitmapAvailableLiveData = new MutableLiveData<>();
        context = mainActivity;
        this.lifecycleOwner = lifecycleOwner;
        startCameraProvider();
    }

    public void startCameraProvider() {
        Log.i("CIS4444","startCameraProvider ");
        cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
                Log.i("CIS4444","startCameraProvider --- cameraProviderFuture ERROR " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(context));
    }

    /**
     * Each Fragment calls this to update the camera preview to the one used by that fragement.
     *     Do not start up the camera and bind it to the preview until this is called and preview is set
     * @param previewView
     */
    public void updateCameraPreview(PreviewView previewView) {
        this.previewView = previewView;
        startCameraX(cameraProvider, previewView);

    }
    public LiveData<Boolean> getAvailableLiveData() {
        return bitmapAvailableLiveData;
    }

    private void startCameraX(@NonNull ProcessCameraProvider cameraProvider, PreviewView previewView){
        //Camera Selector Use Case
        cameraProvider.unbindAll();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        // Preview Use Case
        imagePreview = new Preview.Builder().build();
        imagePreview.setSurfaceProvider(previewView.getSurfaceProvider());
        imagePreview.setTargetRotation(Surface.ROTATION_90);          // Set the desired rotation


        Log.i("CIS4444","startCameraX creating new imageCapture");
        // Create ImageCapture builder and set manual camera settings
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setFlashMode(ImageCapture.FLASH_MODE_OFF)
                .setTargetRotation(Surface.ROTATION_90)                 // Set the desired rotation
                .build();


        // TODO: Set camera parameters
        //Camera2Interop.Extender<ImageCapture> extender = new Camera2Interop.Extender<>(imageCapture);
        //extender.setCaptureRequestOption(CaptureRequest.SENSOR_SENSITIVITY, 1600);

        // Now bind all these item to the camera
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageCapture, imagePreview);

        Log.i("CIS4444","startCameraX bindToLifecycle done");

    }

    /**
     *  public abstraction to take photo.
     *     -- remember that updateCameraPreview must be called before this
     */
    public void capturePhotoBitmap() {
        capturePhotoProvider();
        //  return currentBitmap;   // Bitmap captured async so availalbe later with livedata update
    }
    

    /**
     *  Use the camera Capture method to save an image from the camera to a file
     */
    private void capturePhotoProvider() {
        Log.i("CIS4444","Trying to Capture Photo");
        // Notify observers that the bitmap is not available
        bitmapAvailableLiveData.postValue(false);

        String name = new SimpleDateFormat("yyyy_MM_dd_HHmmss", Locale.US).format(new Date());
        name = "CECsensor_"+name;
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ChemTest");
        }

        // Create output options object which contains file + metadata
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions
                .Builder(context.getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues).build();

        executor = Executors.newSingleThreadExecutor();
        Log.i("CIS4444","Trying to Capture Photo 2 -- "+ outputFileOptions.toString());

        imageCapture.takePicture(
                outputFileOptions,
                executor,
                new ImageCapture.OnImageSavedCallback () {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        try {
                            Uri picUri = outputFileResults.getSavedUri();
                            // Load the saved photo into a Bitmap
                            InputStream is = context.getContentResolver().openInputStream(picUri);
                            currentBitmap = BitmapFactory.decodeStream(is);
                            Log.i("CIS4444","onImageSaved -- currentBitmap set and picUri = "+picUri);
                            bitmapAvailableLiveData.postValue(true);
                        } catch (FileNotFoundException e) {
                            Log.i("CIS4444","onImageSaved -- exception  = "+e.getMessage());
                            throw new RuntimeException(e);
                        }
                        Log.i("CIS4444","onImageSaved -- Photo openned at bitmap with width = "+currentBitmap.getWidth());
                }
                    @Override
                    public void onError(@NonNull ImageCaptureException error) {
                        Log.i("CIS4444","onImageSaved -- onError");
                        error.printStackTrace();
                    }
                });

    }

}
