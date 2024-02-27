package css.cecprototype2.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.camera2.CaptureRequest;
import android.util.Log;
import androidx.annotation.NonNull;
import android.media.Image;
import android.view.Surface;
import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.TextureView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraProvider;
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        PreviewView previewView;
        ProcessCameraProvider cameraProvider;
        LifecycleOwner lifecycleOwner;
        ImageCapture imageCapture;
        Preview imagePreview;
        Bitmap currentBitmap;
        private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

        private static final int REQUEST_CAMERA_PERMISSION = 200;

        private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
        static {
            ORIENTATIONS.append(Surface.ROTATION_0, 90);
            ORIENTATIONS.append(Surface.ROTATION_90, 0);
            ORIENTATIONS.append(Surface.ROTATION_180, 270);
            ORIENTATIONS.append(Surface.ROTATION_270, 180);
        }

        private final int photoWidth = 1920;
        private final int photoHeight = 1440;

        public SensorCamera(AppCompatActivity appContext, TextureView textureView, LifecycleOwner lifecycleOwner) {
            this.context = appContext;
            this.textureView = textureView;
            bitmapAvailableLiveData = new MutableLiveData<>();
            this.lifecycleOwner = lifecycleOwner;
            startCameraProvider();
        }

        public void startCameraProvider() {
            Log.i(TAG,"startCameraProvider ");
            cameraProviderFuture = ProcessCameraProvider.getInstance(context);
            cameraProviderFuture.addListener(() -> {
                try {
                    cameraProvider = cameraProviderFuture.get();
                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                    Log.e("SensorCam","startCameraProvider --- cameraProviderFuture ERROR " + e.getMessage());
                }
            }, ContextCompat.getMainExecutor(context));
        }

        private float focus;
        public float setFocus(int newFocus){
            focus = newFocus;
            Log.d(TAG, "focus distance set to "+focus);
            return focus;
        }

        private Integer iso;
        public Integer setISO(int newIso){
            // range of 100 to 1000
            iso = newIso;
            Log.d(TAG, "ISO set to "+iso);
            return iso;
        }

        private Long exposureTime;
        public Long setExposureTime(long milSec){
            // range of
            exposureTime = milSec;
            Log.d(TAG, "exposureTime set to "+exposureTime);
            return exposureTime ;
        }

        private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(CameraDevice camera) {
                //This is called when the camera is open
                Log.e(TAG, "onOpened");
                cameraDevice = camera;
                createCameraPreview();
            }

            @Override
            public void onDisconnected(CameraDevice camera) {
                cameraDevice.close();
            }

            @Override
            public void onError(CameraDevice camera, int error) {
                cameraDevice.close();
                cameraDevice = null;
            }
        };
        final CameraCaptureSession.CaptureCallback captureCallbackListener = new CameraCaptureSession.CaptureCallback() {
            @Override
            public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                super.onCaptureCompleted(session, request, result);
                // TODO fix this --- Toast.makeText(context, "Saved:" + file, Toast.LENGTH_SHORT).show();
                createCameraPreview();
            }
        };

    private void startCameraX(@NonNull ProcessCameraProvider cameraProvider, PreviewView previewView){

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


        public LiveData<Boolean> getAvailableLiveData() {
            return bitmapAvailableLiveData;
        }

        public void updateCameraPreview(PreviewView previewView) {
            this.previewView = previewView;
            if (cameraProvider != null) {
                startCameraX(cameraProvider, previewView);
                Log.d(TAG, "CameraX Preview Updating...");
            }else {Log.e(TAG, "CameraX Update Failed - CameraProvider is Null");}

        }

        public void takePicture() {
            if (null == cameraDevice) {
                Log.e(TAG, "cameraDevice is null");
                return;
            }
            CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            try {
                ImageReader reader = ImageReader.newInstance(photoWidth, photoHeight, ImageFormat.JPEG, 1);
                List<Surface> outputSurfaces = new ArrayList<Surface>(2);
                outputSurfaces.add(reader.getSurface());
                outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
                Log.d(TAG, "takePicture --- creating captureBuilder");
                final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                captureBuilder.addTarget(reader.getSurface());
                captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_OFF);             // set to manual flash control
                captureBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);                       // set flash off
                captureBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, 0);                              //set to manual focus
                captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_OFF);              // set manual focus
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
                final File file = new File(chemTestFolder,"/CECsensor_"+dateName+".jpg");

                Log.d(TAG, "takePicture --- setting up OnImageAvailableListener");

                ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                    @Override
                    public void onImageAvailable(ImageReader reader) {
                        Log.d(TAG, "takePicture --- onImageAvailable");
                        mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(), file));
                    }
                    private void save(byte[] bytes) throws IOException {
                        Log.d(TAG, "takePicture --- save");

                        OutputStream output = null;
                        try {
                            output = new FileOutputStream(file);
                            output.write(bytes);
                        } finally {
                            if (null != output) {
                                output.close();
                            }
                        }
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
                    }
                }, mBackgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        /**
         * Saves a JPEG {@link Image} into the specified {@link File}.
         */
        private static class ImageSaver implements Runnable {

            /**
             * The JPEG image
             */
            private final Image mImage;
            /**
             * The file we save the image into.
             */
            private final File mFile;

            ImageSaver(Image image, File file) {
                mImage = image;
                mFile = file;
            }

            @Override
            public void run() {
                Log.d(TAG, "ImageSaver --- run()");
                ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
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
                assert texture != null;
                texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
                Surface surface = new Surface(texture);
                captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                captureRequestBuilder.addTarget(surface);
                cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                        //The camera is already closed
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
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        public void openCamera() {
            CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            Log.d(TAG, "is camera open");
            try {
                cameraId = manager.getCameraIdList()[0];
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                assert map != null;
                imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
                // Add permission for camera and let user grant the permission
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                    return;
                }
                manager.openCamera(cameraId, stateCallback, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "openCamera done");
        }

        protected void updatePreview() {
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

        private void closeCamera() {
            if (null != cameraDevice) {
                cameraDevice.close();
                cameraDevice = null;
            }
            if (null != imageReader) {
                imageReader.close();
                imageReader = null;
            }
        }

}
