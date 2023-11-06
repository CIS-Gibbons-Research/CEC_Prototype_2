package css.cecprototype2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
//import org.apache.commons.math3.stat.regression.SimpleRegression;


import java.util.Map;

public class MainActivity extends AppCompatActivity {

    MainViewModel mainViewModel;
    TextView tvStatus;
    Button buttonTakePhoto;
    ImageView imageView;
    PreviewView previewView;
    private SensorCamera cam;
    private boolean bitmapAvailable;
    private boolean isPreviewVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tvStatus);
        imageView = findViewById(R.id.imageView);
        previewView = findViewById(R.id.previewView);
        buttonTakePhoto = findViewById(R.id.buttonTakePhoto);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setupButtonTakePhoto();
        setupCamera();
        setupLiveDataObservers();
    }

    private void setupCamera()
    {
        Log.i("CIS4444","Main Activity --- setupCamera");
        cam = new SensorCamera(this, previewView);
        mainViewModel.initializeCamera(cam);
    }

    private void setupLiveDataObservers() {
        // Observe the LiveData for bitmapAvailable
        mainViewModel.getBitmapAvailableLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isAvailable) {
                bitmapAvailable = isAvailable;
                if (bitmapAvailable) {
                    // Get the bitmap from the ViewModel
                    Bitmap photoBitmap = mainViewModel.getCalibrationBitmap();

                    // Display the photo bitmap in the imageView
                    imageView.setImageBitmap(photoBitmap);
                } else {
                    tvStatus.setText("Bitmap not available");
                }
            }
        });
    }

    private void setupButtonTakePhoto() {
        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPreviewVisible) {
                    mainViewModel.takePhoto();
                    imageView.setImageBitmap(mainViewModel.calibrationBitMap);

                    // Run calibration logic when taking the first photo
                    mainViewModel.doCalibration();

                    // Change the button text and disable it
                    buttonTakePhoto.setText("Next Reading");
                    buttonTakePhoto.setEnabled(false);

                    // Make the previewView invisible and imageViewCamera visible
                    previewView.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.VISIBLE);

                    isPreviewVisible = false;
                } else {
                    // When "Next Reading" is clicked
                    // Make the previewView visible and imageViewCamera invisible
                    previewView.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.INVISIBLE);

                    // Change the button text back to "Take Photo" and enable it
                    buttonTakePhoto.setText("Take Photo");
                    buttonTakePhoto.setEnabled(true);

                    // Run analysis logic when taking the second photo
                    mainViewModel.doAnalysis();

                    isPreviewVisible = true;
                }
            }
        });
    }
}