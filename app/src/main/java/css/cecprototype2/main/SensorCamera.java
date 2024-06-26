package css.cecprototype2.main;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class SensorCamera {
    AppCompatActivity context;
    TextureView textureView;
    private String cameraId;
    protected CameraDevice cameraDevice;
    private static final String TAG = "SensorCamera";
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;
    private MutableLiveData<Boolean> bitmapAvailableLiveData;
    //TextureView previewView;
    LifecycleOwner lifecycleOwner;
    ImageCapture imageCapture;
    //Preview imagePreview;
    public Bitmap currentBitmap;
    ImageSaver imageSaver;
    public String imageFilename = "not set yet";        // file name for current bitmap saved file

    //private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    //ProcessCameraProvider cameraProvider;
    public final int photoWidth = 1920;
    public final int photoHeight = 1440;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    public static final int SENSOR_SENSITIVITY_DEFAULT = 500; //ISO
    public static final long EXPOSURE_TIME_DEFAULT = 50_000_000L; // 1/15 sec -- ET
    public static final int FOCUS_DISTANCE_DEFAULT = 0; //FOCAL LENGTH

    //public SensorCamera(AppCompatActivity appContext, TextureView textureView, LifecycleOwner lifecycleOwner) {
    public SensorCamera(AppCompatActivity appContext, LifecycleOwner lifecycleOwner) {

        this.context = appContext;
        //this.textureView = textureView;
        bitmapAvailableLiveData = new MutableLiveData<>();
        bitmapAvailableLiveData.postValue(false);   // initially, not bitmap or photo availalbe
        this.lifecycleOwner = lifecycleOwner;
        /*
        startCameraProvider();
        try {
            this.cameraProvider = cameraProviderFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            // Handle exception or log an error
            e.printStackTrace();
        }
        */
        //openCamera();
    }

    private float focus = FOCUS_DISTANCE_DEFAULT;
    public float setFocus(int newFocus) {
        focus = newFocus;
        Log.d(TAG, "focus distance set to " + focus);
        return focus;
    }

    private Integer iso = SENSOR_SENSITIVITY_DEFAULT;
    public Integer setISO(int newIso) {
        iso = newIso;
        Log.d(TAG, "ISO set to " + iso);
        return iso;
    }

    private Long exposureTime = EXPOSURE_TIME_DEFAULT;
    public Long setExposureTime(long nanoSec) {
        exposureTime = nanoSec;
        Log.d(TAG, "exposureTime set to " + exposureTime +" nanoseconds or " + exposureTime/1_000_000_000 +" seconds");
        return exposureTime;
    }

    public LiveData<Boolean> getAvailableLiveData() {
        return bitmapAvailableLiveData;
    }


    public void updateCameraPreview(TextureView textureView) {
        this.textureView = textureView;
        Log.d(TAG, "Camera textureView Updated, openCamera now");
        startBackgroundThread();
        openCamera();
        /*
        if (cameraProvider != null) {
            startCameraX(cameraProvider, previewView);
            Log.d(TAG, "CameraX Preview Updating...");
        } else {
            Log.e(TAG, "CameraX Update Failed - CameraProvider is Null");
        }
         */
    }


    public void takePicture() {
        if (null == cameraDevice || null == textureView.getSurfaceTexture()) {
            Log.e(TAG, "cameraDevice is null");
            return;
        }
        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            ImageReader reader = ImageReader.newInstance(photoWidth, photoHeight, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));

            // Ensure textureView.getSurfaceTexture() is not null before creating the Surface
            /*
            SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();

            if (surfaceTexture != null) {
                outputSurfaces.add(new Surface(surfaceTexture));
            } else {
                Log.e(TAG, "SurfaceTexture is null");
                return;
            }
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
             */
            Log.d(TAG, "takePicture --- creating captureBuilder");
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);                       // set flash off
            captureBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, 0);                              // set to manual focus
            captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_OFF);             // set to manual flash control
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_OFF);             // set to manual focus control
            captureBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_OFF);           // set to manual white balance
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, 90);                                            // set orientation

            captureBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, focus);                                         // set focal distance
            captureBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, iso);                                         // set ISO sensitivity                 // 66600000 nanoseconds is 1/15sec
            captureBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, exposureTime);

            Log.d(TAG, "takePicture --- done setting up camera attributes");

            File dcimFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            File chemTestFolder = new File(dcimFolder, "ChemTest");

            if (!chemTestFolder.exists()) {
                Log.d(TAG, "DCIM/ChemTest folder does not exist, creating it.");
                chemTestFolder.mkdirs();
            }
            String dateName = new SimpleDateFormat("yyyy_MM_dd_HHmmss", Locale.US).format(new Date());
            final File file = new File(chemTestFolder, "/CECsensor_" + dateName + ".jpg");

            Log.d(TAG, "takePicture --- setting up OnImageAvailableListener");

            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Log.d(TAG, "takePicture --- onImageAvailable");
                    imageSaver = new ImageSaver(reader.acquireNextImage(), file);
                    Log.d(TAG, "takePicture --- imageSaver created");
                    mBackgroundHandler.post(imageSaver);
                    Log.d(TAG, "takePicture --- imageSaver posted");
                }

            };
            Log.d(TAG, "takePicture --- setting up setOnImageAvailableListener");
            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    Log.d(TAG, "takePicture --- CameraCaptureSession");
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(context, "Saved:" + file, Toast.LENGTH_SHORT).show();
                    createCameraPreview();
                }
            };
            Log.d(TAG, "takePicture --- setting up createCaptureSession");
            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        Log.d(TAG, "takePicture --- onConfigured");
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                    Log.d(TAG, "takePicture --- onConfigureFailed");
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves a JPEG {@link Image} into the specified {@link File}.
     */
    private class ImageSaver implements Runnable {

        /**
         * The JPEG image
         */
        private final Image mImage;
        /**
         * The file we save the image into.
         */
        private final File mFile;

        ImageSaver(Image image, File file) {
            Log.d(TAG, "ImageSaver --- constructor");
            mImage = image;
            mFile = file;
        }

        @Override
        public void run() {

            Log.d(TAG, "ImageSaver --- run() --- filename = "+mFile.getName());
            imageFilename = mFile.getName();
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);

            Log.d(TAG, "ImageSaver --- updating currentBitmap");
            currentBitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);

            // rotate bitmap to match JPEG orientation
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap rotatedImg = Bitmap.createBitmap(currentBitmap, 0, 0, currentBitmap.getWidth(), currentBitmap.getHeight(), matrix, true);
            currentBitmap = rotatedImg;
            bitmapAvailableLiveData.postValue(true);   // bitmap or photo availalbe now

            FileOutputStream output = null;
            try {
                output = new FileOutputStream(mFile);
                output.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                if (null != output) {
                    try {
                        //bitmapAvailableLiveData.postValue(true);   // bitmap or photo availalbe now
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    protected void createCameraPreview() {
        try {
            Log.d(TAG, "createCameraPreview ");
            SurfaceTexture texture = textureView.getSurfaceTexture();
            if (texture != null) {
                texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
                Surface surface = new Surface(texture);
                captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                captureRequestBuilder.addTarget(surface);

                cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                        // The camera is already closed
                        if (null == cameraDevice) {
                            Log.e(TAG, "createCameraPreview --- onConfigured -- device null, camera already closed");
                            return;
                        }
                        // When the session is ready, we start displaying the preview.
                        cameraCaptureSessions = cameraCaptureSession;
                        Log.d(TAG, "createCameraPreview --- onConfigured -- about to call updatePreview()");
                        updatePreview();
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                        Log.e(TAG, "createCameraPreview --- onConfigureFailed");
                        Toast.makeText(context, "Configuration change", Toast.LENGTH_SHORT).show();
                    }
                }, null);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
/*
    private void startCameraProvider() {
        Log.i(TAG, "startCameraProvider ");
        cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
            } catch (ExecutionException | InterruptedException e) {
                Log.e("SensorCam", "startCameraProvider --- cameraProviderFuture ERROR " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(context));
    }

    private void startCameraX(@NonNull ProcessCameraProvider cameraProvider, TextureView previewView) {

        cameraProvider.unbindAll();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        // Preview Use Case
        imagePreview = new Preview.Builder().build();
        imagePreview.setSurfaceProvider(previewView.getSurfaceProvider());
        imagePreview.setTargetRotation(Surface.ROTATION_0); // Set the desired rotation to Surface.ROTATION_0

        Log.i(TAG, "startCameraX creating new imageCapture");

        // Create ImageCapture builder and set manual camera settings
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setFlashMode(ImageCapture.FLASH_MODE_OFF)
                .setTargetRotation(Surface.ROTATION_0) // Set the desired rotation to Surface.ROTATION_0
                .build();

        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageCapture, imagePreview);

        Log.i(TAG, "startCameraX bindToLifecycle done");
    }
    */

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void openCamera() {
        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        Log.d(TAG, "is camera open");
        try {
            cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "openCamera done");
    }

    private void updatePreview() {
        if (null == cameraDevice) {
            Log.e(TAG, "updatePreview error, return");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            // This is called when the camera is open
            Log.e(TAG, "onOpened");
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            Log.e(TAG, "onDisconnected");
            cameraDevice.close();
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };


}