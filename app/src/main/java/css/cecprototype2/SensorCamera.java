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
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SensorCamera {

    // Constructor: initialize camera
    SensorCamera(Context actContext, PreviewView previewView) {
        // should be passed the application context which is needed by the camera
        // should also be passed the previewView on the screen where the image should be displayed
        // TODO: is there a better way to connect the camera to the previewView?  Will the previewView change when the phone is rotated?
        context = actContext;
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

    private void startCameraX(@NonNull ProcessCameraProvider cameraProvider, PreviewView previewView){
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
        cameraProvider.bindToLifecycle((LifecycleOwner) context, cameraSelector, imageAnalysis, imageCapture, imagePreview);
    }

    public Image capturePhotoImage() {
        // public abstraction to take photo.
        // Currently calls analyze photo, but could capture photo to save to disk later...
        // The UI should call this through the MainViewModel
        analyzePhotoProvider();
        return image;
    }

    public Bitmap capturePhotoBitmap() {
        // public abstraction to take photo.
        // Currently calls analyze photo, but could capture photo to save to disk later...
        // The UI should call this through the MainViewModel
        analyzePhotoProvider();
        return bitmap;
    }

    /**
     *  Use the camera analyze method to get an image from the camera without saving it to a file
     */
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
                // bitmap = imageProxy.toBitmap();
                // TODO: find out why this no loger works to get the bitmap

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


    /**
     *  Use the camera Capture method to save an image from the camera to a file
     */
    private void capturePhotoProvider() {
        //Toast.makeText(getApplicationContext(), "Trying to Capture Photo", Toast.LENGTH_SHORT );
        Log.i("TEG","Trying to Capture Photo");

        //SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        //File file = new File(getBatchDirectoryName(), mDateFormat.format(new Date())+ ".jpg");
        File file = new File(context.getExternalCacheDir() + File.separator + System.currentTimeMillis() + ".png");

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
        Log.i("TEG","Trying to Capture Photo 222");

        executor = Executors.newSingleThreadExecutor();
        Log.i("TEG","Trying to Capture Photo 333");

        imageCapture.takePicture(
                outputFileOptions,
                executor,
                new ImageCapture.OnImageSavedCallback () {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Log.i("TEG","onImageSaved -- Photo has been taken and saved");
                        //Toast.makeText(getApplicationContext(), "Photo has been taken and saved", Toast.LENGTH_SHORT );
                    }
                    @Override
                    public void onError(@NonNull ImageCaptureException error) {
                        //Toast.makeText(getApplicationContext(), "Error taking and saving photo", Toast.LENGTH_SHORT );
                        error.printStackTrace();
                    }
                });

    }


}
