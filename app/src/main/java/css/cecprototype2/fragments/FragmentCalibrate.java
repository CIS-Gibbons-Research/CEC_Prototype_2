package css.cecprototype2.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
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
    TextView tvStatus, tvSlope, tvRSq;
    TextView tvCalibrate1, tvCalibrate2, tvCalibrate3, tvCalibrate4, tvCalibrate5, tvCalibrate6;

    Spinner concentrationSpinner;
    String selectedConcentration;

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
        tvSlope = binding.textViewCalibrateSlope;
        tvRSq = binding.textViewCalibrateRSq;

        concentrationSpinner = binding.concentrationSpinner;
        setupConcentrationSpinner();

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
        buttonCalibrate = binding.buttonCalibrate;
        buttonCalibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CIS 4444", "Calibrate button clicked");
                calibrateFromPhoto();
            }
        });

        buttonCalibrateSample = binding.buttonCalibrateSample;
        buttonCalibrateSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CIS 4444", "Calibrate with Samples button clicked");
                calibrateFromSampleImage();
            }
        });
    }

    private void setupCameraPreview() {
        Log.i("CIS4444", "Fragment Calibrate --- setupCameraPreview");
        mainViewModel.setCameraPreview(previewView);
    }

    private void setupLiveDataObservers() {
        mainViewModel.getBitmapAvailableLiveData().observe(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isAvailable) {
                bitmapAvailable = isAvailable;
                if (bitmapAvailable) {
                    readingsAvailableUpdateUI();
                } else {
                    Log.i("CIS4444", "Fragment Calibrate --- LiveData --- bitmap NOT Available");
                    tvStatus.setText("Bitmap not available");
                }
            }
        });
    }

    private void readingsAvailableUpdateUI() {
        Log.i("CIS4444", "Fragment Calibrate --- LiveData --- bitmap Available");
        mainViewModel.retrieveCalibrationBitmapFromCamera();
        mainViewModel.doCalibration();
        calibrationBitMap = mainViewModel.getCalibrationBitmap();
        if (calibrationBitMap == null) {
            Log.i("CIS4444", "Fragment Calibrate --- calibrationBitMap still NULL");
        } else {
            imageView.setImageBitmap(calibrationBitMap);
            updateCalibrateUI();
        }
    }

    private void calibrateFromPhoto() {
        if (isPreviewVisible) {
            mainViewModel.takePhoto();
            calibrationBitMap = mainViewModel.getCalibrationBitmap();
            mainViewModel.doCalibration();

            buttonCalibrate.setText("Processing");
            buttonCalibrate.setEnabled(false);

            imageView.setImageBitmap(calibrationBitMap);

            previewView.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);

            isPreviewVisible = false;
        } else {
            previewView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.INVISIBLE);

            buttonCalibrate.setText("Calibrate");
            buttonCalibrate.setEnabled(true);

            isPreviewVisible = true;
        }
    }

    private void calibrateFromSampleImage() {
        calibrationBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_a);
        Log.i("CIS4444", "Sample image size width="+calibrationBitMap.getWidth()+ " and height="+calibrationBitMap.getHeight());
        mainViewModel.setCalibrationBitMap(calibrationBitMap);
        mainViewModel.doCalibration();
        imageView.setImageBitmap(calibrationBitMap);
        updateCalibrateUI();
        previewView.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.VISIBLE);
    }

    private void updateCalibrateUI() {
        tvCalibrate1.setText(mainViewModel.calibrationIntensities.get(0).toString());
        tvCalibrate2.setText(mainViewModel.calibrationIntensities.get(1).toString());
        tvCalibrate3.setText(mainViewModel.calibrationIntensities.get(2).toString());
        tvCalibrate4.setText(mainViewModel.calibrationIntensities.get(3).toString());
        tvCalibrate5.setText(mainViewModel.calibrationIntensities.get(4).toString());
        tvCalibrate6.setText(mainViewModel.calibrationIntensities.get(5).toString());
        tvSlope.setText(String.valueOf(mainViewModel.getCalibrationSlope()));
        tvRSq.setText(String.valueOf(mainViewModel.getCalibrationRSq()));

        buttonCalibrate.setText("Reset Calibration?");
        buttonCalibrate.setEnabled(true);
    }

    private void setupConcentrationSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.concentration_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        concentrationSpinner.setAdapter(adapter);

        concentrationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedConcentration = parentView.getItemAtPosition(position).toString();
                // Handle actions based on the selected concentration if needed
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
    }
}
