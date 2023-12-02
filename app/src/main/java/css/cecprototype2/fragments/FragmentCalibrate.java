package css.cecprototype2.fragments;

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

import css.cecprototype2.main.MainViewModel;
import css.cecprototype2.R;
import css.cecprototype2.databinding.FragmentCalibrateBinding;

public class FragmentCalibrate extends Fragment {

    private FragmentCalibrateBinding binding;
    private MainViewModel mainViewModel;
    private boolean bitmapAvailable;
    private boolean isPreviewVisible = true;

    Button buttonCalibrate;
    Button buttonCalibrateSample;
    ImageView imageView;
    PreviewView previewView;
    Bitmap calibrationBitMap;
    TextView tvStatus;
    TextView tvCalibrate1, tvCalibrate2, tvCalibrate3, tvCalibrate4, tvCalibrate5, tvCalibrate6;


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
        tvCalibrate2 = binding.textViewCalibrate2;
        tvCalibrate3 = binding.textViewCalibrate3;
        tvCalibrate4 = binding.textViewCalibrate4;
        tvCalibrate5 = binding.textViewCalibrate5;
        tvCalibrate6 = binding.textViewCalibrate6;

        setupCameraPreview();
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

    private void setupCameraPreview() {
        Log.i("CIS4444", "Fragment Calibrarte --- setupCameraPreview");
        mainViewModel.setCameraPreview(previewView);
    }

    private void setupLiveDataObservers() {
        // Observe the LiveData for bitmapAvailable
        mainViewModel.getBitmapAvailableLiveData().observe(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isAvailable) {
                bitmapAvailable = isAvailable;
                if (bitmapAvailable) {
                    readingsAvailableUpdateUI();
                } else {
                    Log.i("CIS4444", "Fragment Calibrarte --- LiveData --- bitmap NOT Available");
                    tvStatus.setText("Bitmap not available");
                }
            }
        });
    }

    private void readingsAvailableUpdateUI() {
        Log.i("CIS4444", "Fragment Calibrarte --- LiveData --- bitmap Available");
        // Run calibration logic when taking the first photo
        mainViewModel.doCalibration();
        // Get the bitmap from the ViewModel
        calibrationBitMap = mainViewModel.getCalibrationBitmap();
        if (calibrationBitMap == null) {
            Log.i("CIS4444", "Fragment Calibrarte --- calibrationBitMap still NULL");
        } else {
            // Display the photo bitmap in the imageView
            imageView.setImageBitmap(calibrationBitMap);
            // Update the UI with new calibration readings
            updateCalibrateUI();
        }
    }

    private void calibrateFromPhoto() {
        if (isPreviewVisible) {
            mainViewModel.takePhoto();


            // Change the button text and disable it
            buttonCalibrate.setText("Processing");
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
            buttonCalibrate.setText("Take Calibration Photo");
            buttonCalibrate.setEnabled(true);

            isPreviewVisible = true;
        }
    }

    private void calibrateFromSampleImage() {
        // Use the resource identifier to load the sample image
        Bitmap sampleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_a);
        Log.i("CIS4444", "Sample image size width="+sampleBitmap.getWidth()+ " and height="+sampleBitmap.getHeight());

        mainViewModel.setCalibrationBitMap(sampleBitmap);
        mainViewModel.doCalibration();
        updateCalibrateUI();
    }

    private void updateCalibrateUI() {
        // TODO: The textviews should be in a list and this should be a loop
        tvCalibrate1.setText(mainViewModel.calibrationIntensities.get(0).toString());
        tvCalibrate2.setText(mainViewModel.calibrationIntensities.get(1).toString());
        tvCalibrate3.setText(mainViewModel.calibrationIntensities.get(2).toString());
        tvCalibrate4.setText(mainViewModel.calibrationIntensities.get(3).toString());
        tvCalibrate5.setText(mainViewModel.calibrationIntensities.get(4).toString());
        tvCalibrate6.setText(mainViewModel.calibrationIntensities.get(5).toString());
        // Change the button text and disable it
        buttonCalibrate.setText("Another Calibration?");
        buttonCalibrate.setEnabled(true);
    }

}