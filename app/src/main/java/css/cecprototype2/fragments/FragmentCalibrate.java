package css.cecprototype2.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.camera.view.PreviewView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import css.cecprototype2.main.MainViewModel;
import css.cecprototype2.R;
import css.cecprototype2.databinding.FragmentCalibrateBinding;

public class FragmentCalibrate extends Fragment {

    private FragmentCalibrateBinding binding;
    private MainViewModel mainViewModel;
    private boolean bitmapAvailable;
    private boolean isPreviewVisible = true;

    Button buttonCalibrate, buttonCalibrateSample;
    ImageView imageView;
    PreviewView previewView;
    Bitmap calibrationBitMap;
    TextView tvStatus, tvSlope, tvRSq;
    EditText etCalibrate1, etCalibrate2, etCalibrate3, etCalibrate4, etCalibrate5, etCalibrate6;

    Spinner concentrationSpinner;
    String selectedConcentration;

    List<EditText> concentrationEditTexts;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Use the shared ViewModel
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        concentrationEditTexts = new ArrayList(Arrays.asList(etCalibrate1, etCalibrate2, etCalibrate3, etCalibrate4, etCalibrate5, etCalibrate6));
        binding = FragmentCalibrateBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        imageView = binding.imageViewCalibrate;
        previewView = binding.previewViewCalibrate;
        tvStatus = binding.textViewCalibrateStatus;
        etCalibrate1 = binding.editTextCalibration1;
        etCalibrate2 = binding.editTextCalibration2;
        etCalibrate3 = binding.editTextCalibration3;
        etCalibrate4 = binding.editTextCalibration4;
        etCalibrate5 = binding.editTextCalibration5;
        etCalibrate6 = binding.editTextCalibration6;

        tvSlope = binding.textViewCalibrateSlope;
        tvRSq = binding.textViewCalibrateRSq;

        concentrationSpinner = binding.concentrationSpinner;
        setupConcentrationSpinner();

        setupCameraPreview();
        setupButtons();
        setupLiveDataObservers();
        setupEditTextListeners();


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupEditTextListeners() {
        for (final EditText e : concentrationEditTexts) {
            e.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().isEmpty()) {
                        validateAndSaveEditTextValue(e);
                    }
                }
            });
        }
    }

    private void validateAndSaveEditTextValue(EditText editText) {
        String valueStr = editText.getText().toString().trim();
        try {
            double value = Double.parseDouble(valueStr);
            int index = concentrationEditTexts.indexOf(editText);
            if (index != -1) {
                mainViewModel.calibrationIntensities.set(index, value);
            } else {
                Log.e("FragmentCalibrate", "EditText index not found.");
            }
        } catch (NumberFormatException e) {
            // Invalid input, handle accordingly (e.g., show error message)
            editText.setError("Invalid input");
        }
    }

    private void setupButtons() {
        buttonCalibrate = binding.buttonCalibrate;
        buttonCalibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CalibrationFragment", "Calibrate button clicked");
                calibrateFromPhoto();
            }
        });

        buttonCalibrateSample = binding.buttonCalibrateSample;
        buttonCalibrateSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CalibrationFragment", "Calibrate with Samples button clicked");
                calibrateFromSampleImage();
            }
        });
    }

    private void setupCameraPreview() {
        Log.i("CalibrationFragment", "Fragment Calibrate --- setupCameraPreview");
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
                    Log.i("CalibrationFragment", "Fragment Calibrate --- LiveData --- bitmap NOT Available");
                    tvStatus.setText("Bitmap not available");
                }
            }
        });

    }

    private void readingsAvailableUpdateUI() {
        Log.i("CalibrationFragment", "Fragment Calibrate --- LiveData --- bitmap Available");
        mainViewModel.retrieveCalibrationBitmapFromCamera();
        mainViewModel.doCalibration();
        calibrationBitMap = mainViewModel.getCalibrationBitmap();
        if (calibrationBitMap == null) {
            Log.i("CalibrationFragment", "Fragment Calibrate --- calibrationBitMap still NULL");
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
        Log.i("CalibrationFragment", "Sample image size width="+calibrationBitMap.getWidth()+ " and height="+calibrationBitMap.getHeight());
        mainViewModel.setCalibrationBitMap(calibrationBitMap);
        mainViewModel.doCalibration();
        imageView.setImageBitmap(calibrationBitMap);
        updateCalibrateUI();
        previewView.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.VISIBLE);
    }

    private void updateCalibrateUI() {
        List<Double> calibrationIntensities = mainViewModel.calibrationIntensities;
        etCalibrate1.setText(String.valueOf(calibrationIntensities.get(0)));
        etCalibrate2.setText(String.valueOf(calibrationIntensities.get(1)));
        etCalibrate3.setText(String.valueOf(calibrationIntensities.get(2)));
        etCalibrate4.setText(String.valueOf(calibrationIntensities.get(3)));
        etCalibrate5.setText(String.valueOf(calibrationIntensities.get(4)));
        etCalibrate6.setText(String.valueOf(calibrationIntensities.get(5)));
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