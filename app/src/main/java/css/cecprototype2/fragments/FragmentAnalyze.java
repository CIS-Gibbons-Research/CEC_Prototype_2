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

import css.cecprototype2.R;
import css.cecprototype2.main.MainActivity;
import css.cecprototype2.main.MainViewModel;
import css.cecprototype2.main.SensorCamera;
import css.cecprototype2.databinding.FragmentAnalyzeBinding;

public class FragmentAnalyze extends Fragment {

    private FragmentAnalyzeBinding binding;
    private MainViewModel mainViewModel;
    private SensorCamera cam;
    private boolean bitmapAvailable;
    private boolean isPreviewVisible = true;
    TextView tvStatus;
    ImageView imageView;
    PreviewView previewView;

    Button buttonAnalyze;
    Button buttonAnalyzeSample;
    TextView tvAnalyze1, tvAnalyze2, tvAnalyze3, tvAnalyze4, tvAnalyze5, tvAnalyze6;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Use the shared ViewModel
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        binding = FragmentAnalyzeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        imageView = binding.imageViewAnalyze;
        previewView = binding.previewViewAnalyze;
        tvStatus = binding.textViewAnalyzeStatus;
        tvAnalyze1 = binding.textViewAnalyze1;
        tvAnalyze2 = binding.textViewAnalyze2;
        tvAnalyze3 = binding.textViewAnalyze3;
        tvAnalyze4 = binding.textViewAnalyze4;
        tvAnalyze5 = binding.textViewAnalyze5;
        tvAnalyze6 = binding.textViewAnalyze6;

        setupCameraPreview();
        setupButtons();
//        setupLiveDataObservers();


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupButtons() {
        // This app uses the new bindings instead of the old findViewById
        buttonAnalyze = binding.buttonAnalyze;
        buttonAnalyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Run calibration logic when taking the first photo
                doButtonTakePhoto();
                Log.d("CIS 4444", "Calibrate button clicked");   // log button click for debugging
            }
        });

        // This app uses the new bindings instead of the old findViewById
        buttonAnalyzeSample = binding.buttonAnalyzeSample;
        buttonAnalyzeSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CIS 4444", "Calibrate with Samples button clicked");   // log button click for debugging
                calibrateFromSampleImage();
            }
        });
    }  // end SetupButtons

    private void setupCameraPreview() {
        Log.i("CIS4444", "Fragment Calibrarte --- setupCameraPreview");
        cam =((MainActivity)getActivity()).cam;
        mainViewModel.initializeCamera(cam);
    }

//    private void setupLiveDataObservers() {
//        // Observe the LiveData for bitmapAvailable
//        mainViewModel.getBitmapAvailableLiveData().observe(getActivity(), new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean isAvailable) {
//                bitmapAvailable = isAvailable;
//                if (bitmapAvailable) {
//                    // Get the bitmap from the ViewModel
//                    Bitmap photoBitmap = mainViewModel.getCalibrationBitmap();
//
//                    // Display the photo bitmap in the imageView
//                    imageView.setImageBitmap(photoBitmap);
//                } else {
//                    tvStatus.setText("Bitmap not available");
//                }
//            }
//        });
//    }

    private void doButtonTakePhoto() {
        if (isPreviewVisible) {
            mainViewModel.takePhoto();
            imageView.setImageBitmap(mainViewModel.calibrationBitMap);

            // Run calibration logic when taking the first photo
            mainViewModel.doCalibration();

            // Change the button text and disable it
            buttonAnalyze.setText("Next Reading");
            buttonAnalyze.setEnabled(false);

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
            buttonAnalyze.setText("Take Photo");
            buttonAnalyze.setEnabled(true);

            // Run analysis logic when taking the second photo
            mainViewModel.doAnalysis();

            isPreviewVisible = true;
        }
    }

    private void calibrateFromSampleImage() {
        Log.i("CIS4444", "Load sample image to Calibrate");
        // Use the resource identifier to load the sample image
        Bitmap sampleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_a);
        Log.i("CIS4444", "Sample image size width="+sampleBitmap.getWidth()+ " and height="+sampleBitmap.getHeight());

        mainViewModel.setCalibrationBitMap(sampleBitmap);
        mainViewModel.doCalibration();
        updateAnalyzeUI();
    }

    private void updateAnalyzeUI() {
        // TODO: The textviews should be in a list and this should be a loop
        tvAnalyze1.setText(mainViewModel.calibrationIntensities.get(0).toString());
        tvAnalyze2.setText(mainViewModel.calibrationIntensities.get(1).toString());
        tvAnalyze3.setText(mainViewModel.calibrationIntensities.get(2).toString());
        tvAnalyze4.setText(mainViewModel.calibrationIntensities.get(3).toString());
        tvAnalyze5.setText(mainViewModel.calibrationIntensities.get(4).toString());
        tvAnalyze6.setText(mainViewModel.calibrationIntensities.get(5).toString());

    }


}