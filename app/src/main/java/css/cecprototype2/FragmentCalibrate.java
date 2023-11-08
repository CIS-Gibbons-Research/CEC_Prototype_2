package css.cecprototype2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.camera.view.PreviewView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import css.cecprototype2.databinding.FragmentCalibrateBinding;

public class FragmentCalibrate extends Fragment {

    private FragmentCalibrateBinding binding;
    private MainViewModel mainViewModel;
    private SensorCamera cam;
    private boolean bitmapAvailable;
    private boolean isPreviewVisible = true;

    Button buttonCalibrate;
    Button buttonCalibrateSample;
    ImageView imageView;
    PreviewView previewView;
    TextView tvStatus;
    TextView tvCalibrate1, tvCalibrate2, tvCalibrate3, tvCalibrate4, tvCalibrate5;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Use the shared ViewModel
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        binding = FragmentCalibrateBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        imageView = binding.imageViewCalibrate;
        previewView = binding.previewViewCalibrate;
        tvStatus = binding.textViewCalibrateStatus;
        tvCalibrate1 = binding.textViewCalibrate1;
        // TODO -- Add bindings for rest of the textview.

        setupCamera();
        setupButtons();
        setupLiveDataObservers();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupButtons() {
        // This app uses the new bindings instead of the old findViewById
        buttonCalibrate = binding.buttonCalibrate;
        buttonCalibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Run calibration logic when taking the first photo
                Log.d("CIS 4444", "Calibrate button clicked");   // log button click for debugging
                calibrateFromPhoto();
            }
        });

        // This app uses the new bindings instead of the old findViewById
        buttonCalibrateSample = binding.buttonCalibrateSample;
        buttonCalibrateSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CIS 4444", "Calibrate with Samples button clicked");   // log button click for debugging
                calibrateFromSampleImage();
            }
        });
    }  // end SetupButtons

    private void setupCamera() {
        Log.i("CIS4444", "Main Activity --- setupCamera");
        cam = new SensorCamera(getActivity(), getActivity(), previewView);
        mainViewModel.initializeCamera(cam);
    }

    private void setupLiveDataObservers() {
        // Observe the LiveData for bitmapAvailable
        mainViewModel.getBitmapAvailableLiveData().observe(getActivity(), new Observer<Boolean>() {
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

    private void calibrateFromPhoto() {
        if (isPreviewVisible) {
            mainViewModel.takePhoto();
            imageView.setImageBitmap(mainViewModel.calibrationBitMap);

            // Run calibration logic when taking the first photo
            mainViewModel.doCalibration();

            // Change the button text and disable it
            buttonCalibrate.setText("Next Reading");
            buttonCalibrate.setEnabled(false);

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
            buttonCalibrate.setText("Take Photo");
            buttonCalibrate.setEnabled(true);

            // Run analysis logic when taking the second photo
            mainViewModel.doAnalysis();

            isPreviewVisible = true;
        }
    }

    private void calibrateFromSampleImage() {
        Log.i("CIS4444", "Load sample image to Calibrate");
        Bitmap sampleBitmap = BitmapFactory.decodeFile("/res/drawable/sample_b.jpg");
        mainViewModel.setCalibrationBitMap(sampleBitmap);
        mainViewModel.doCalibration();
    }


}