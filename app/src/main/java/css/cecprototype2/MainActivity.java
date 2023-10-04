package css.cecprototype2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;
import androidx.lifecycle.ViewModelProvider;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    MainViewModel mainViewModel;   // View Model for the main activity
    TextView tvStatus;
    Button buttonUpdate;
    ImageView imageViewCamera;
    PreviewView previewView;
    CircleIntensityExtractor circleIntensityExtractor;
    private SensorCamera cam;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up UI components
        tvStatus = findViewById(R.id.tvStatus);
        imageViewCamera = findViewById(R.id.imageView);
        previewView = findViewById(R.id.previewView);
        imageViewCamera = findViewById(R.id.imageView);
        // set up modelView
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        // set up buttons
        setupButtonUpdate();
        // set up camera
        setupCamera();
    }

    private void setupButtonUpdate() {
        buttonUpdate = findViewById(R.id.buttonTakePhoto);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("CIS4444","Update Button onClick");
                mainViewModel.takePhoto();
                imageViewCamera.setImageBitmap(mainViewModel.bitMap);
                Double pixel = mainViewModel.analyzePixel(50,60);
                tvStatus.setText("The pixel value at 50, 60 is "+pixel);

                List<Region> regions = analyzeCapturedImage(mainViewModel.getImage()); //list of regions

                // Calculate average intensities for each region
                Map<String, Double> circleIntensityMap = circleIntensityExtractor.extractCircleIntensities(regions);

                // Update UI with circle intensities
                updateUIWithCircleIntensities(circleIntensityMap);
            }
        });
    }

    private void setupCamera() {
        Log.i("CIS4444","Main Activity --- setupCamera");
        cam = new SensorCamera(this, previewView);
        mainViewModel.initializeCamera(cam);

    }

    private List<Region> analyzeCapturedImage(Image image) //convert image into a list of regions
    {
        List<Region> regions = mainViewModel.analyzeImage(image);
        return regions;
    }

    private void updateUIWithCircleIntensities(Map<String, Double> circleIntensityMap) {
        // Update your UI components (e.g., TextView) with circle intensity information
        // Use the `circleIntensityMap` to display or further process the data.
        // Example:
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Double> entry : circleIntensityMap.entrySet())
        {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        tvStatus.setText(sb.toString());
    }
}