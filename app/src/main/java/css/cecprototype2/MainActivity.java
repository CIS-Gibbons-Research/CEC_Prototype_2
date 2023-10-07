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
    private SensorCamera cam;
    Map<String, Integer> map;
    ChemicalAnalysis chemicalAnalysis;
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

                //analysis logic

                //populate map of regions from viewModel
                //map = mainViewModel.getRegionMap();
                // Update UI with intensities -- mainViewModel.updateUIWithCircleIntensities formats String for us
                //tvStatus.setText(mainViewModel.updateUIWithCircleIntensities(map));
                //start chemical analysis with map as param
                //chemicalAnalysis = new ChemicalAnalysis(map);
                //FIXME: Replace with UI Display logic -- chemicalAnalysis.getChemicalReading() needs a param (0-6) as an index key
                //tvStatus.setText(chemicalAnalysis.getChemicalReading().toString());
            }
        });
    }

    private void setupCamera()
    {
        Log.i("CIS4444","Main Activity --- setupCamera");
        cam = new SensorCamera(this, previewView);
        mainViewModel.initializeCamera(cam);
    }

}