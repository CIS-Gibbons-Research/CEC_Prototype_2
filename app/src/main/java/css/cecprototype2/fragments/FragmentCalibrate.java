package css.cecprototype2.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import css.cecprototype2.main.MainViewModel;
import css.cecprototype2.R;
import css.cecprototype2.databinding.FragmentCalibrateBinding;
import css.cecprototype2.region_logic.Region;

public class FragmentCalibrate extends Fragment {

    private FragmentCalibrateBinding binding;
    private MainViewModel mainViewModel;
    private boolean bitmapAvailable;
    private boolean isPreviewVisible = true;
    Button buttonCalibrate, buttonCalibrateSample, buttonSubmitCalibrationChanges;
    ImageView imageView;
    TextureView textureView;
    Bitmap calibrationBitMap;
    TextView tvStatus, tvSlope, tvRSq;
    EditText etCalibrate1, etCalibrate2, etCalibrate3, etCalibrate4, etCalibrate5, etCalibrate6;
    EditText etNotes;
    TextView tvCalibrateIntensity1, tvCalibrateIntensity2, tvCalibrateIntensity3, tvCalibrateIntensity4, tvCalibrateIntensity5, tvCalibrateIntensity6;
    BoundingBoxOverlay wellBoxOverlay;

    Spinner unitSpinner;
    String selectedConcentration;
    Double concentrationFactor;

    List<EditText> concentrationEditTexts;
    ArrayList<Double> newConcentrationValues;
    List<TextView> calibrationIntensityTextViews;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Use the shared ViewModel
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        binding = FragmentCalibrateBinding.inflate(inflater, container, false);

        Log.d("CalibrationFragment", "onCreateView");
        newConcentrationValues = new ArrayList<>();

        wellBoxOverlay = new BoundingBoxOverlay(mainViewModel);

        View root = binding.getRoot();
        setUpBindings();
        setupUnitsSpinner();
        setupCameraPreview();
        setupButtons();
        setupLiveDataObservers();
        // Inflate the fragment's layout
        return root;
    }

    private void setUpBindings() {
        //spinner
        unitSpinner = binding.unitsSpinner;

        //camera and images
        imageView = binding.imageViewCalibrate;
        textureView = binding.textureViewCalibrate;
        //wellBoxOverlay = binding.boundingBoxOverlay;
        tvStatus = binding.textViewCalibrateStatus;

        //concentration EditTexts
        concentrationEditTexts = new ArrayList<>();
        etCalibrate1 = binding.editTextCalibration1;
        concentrationEditTexts.add(etCalibrate1);
        etCalibrate2 = binding.editTextCalibration2;
        concentrationEditTexts.add(etCalibrate2);
        etCalibrate3 = binding.editTextCalibration3;
        concentrationEditTexts.add(etCalibrate3);
        etCalibrate4 = binding.editTextCalibration4;
        concentrationEditTexts.add(etCalibrate4);
        etCalibrate5 = binding.editTextCalibration5;
        concentrationEditTexts.add(etCalibrate5);
        etCalibrate6 = binding.editTextCalibration6;
        concentrationEditTexts.add(etCalibrate6);

        etNotes = binding.editTextNotes;

        //linear regression info TextViews
        tvSlope = binding.textViewCalibrateSlope;
        tvRSq = binding.textViewCalibrateRSq;

        //buttons
        buttonCalibrate = binding.buttonCalibrate;
        buttonCalibrateSample = binding.buttonCalibrateSample;
        buttonSubmitCalibrationChanges = binding.buttonSubmitCalibrationChanges;

        //calibration intensity TextViews
        calibrationIntensityTextViews = new ArrayList<>();
        tvCalibrateIntensity1 = binding.textViewCalibrationIntensity1;
        calibrationIntensityTextViews.add(tvCalibrateIntensity1);
        tvCalibrateIntensity2 = binding.textViewCalibrationIntensity2;
        calibrationIntensityTextViews.add(tvCalibrateIntensity2);
        tvCalibrateIntensity3 = binding.textViewCalibrationIntensity3;
        calibrationIntensityTextViews.add(tvCalibrateIntensity3);
        tvCalibrateIntensity4 = binding.textViewCalibrationIntensity4;
        calibrationIntensityTextViews.add(tvCalibrateIntensity4);
        tvCalibrateIntensity5 = binding.textViewCalibrationIntensity5;
        calibrationIntensityTextViews.add(tvCalibrateIntensity5);
        tvCalibrateIntensity6 = binding.textViewCalibrationIntensity6;
        calibrationIntensityTextViews.add(tvCalibrateIntensity6);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupButtons() {

        buttonCalibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CalibrationFragment", "Calibrate button clicked");
                calibrateFromPhoto();
            }
        });


        buttonCalibrateSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CalibrationFragment", "Calibrate with Samples button clicked");
                calibrateFromSampleImage();
            }
        });

        buttonSubmitCalibrationChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CalibrationFragment", "Submit Calibration Changes button clicked");
                handleSubmitCalibrationChange();
            }
        });
    }

    private void handleSubmitCalibrationChange() {
        newConcentrationValues = getValuesForCalibration(concentrationEditTexts);
        Log.d("CalibrationFragment", "new nCV: " + newConcentrationValues);
        mainViewModel.setChemAnalysisCalibrationConcentraions(newConcentrationValues);
        //updateCalibrateUI(newConcentrationValues);
    }

    private void setupCameraPreview() {
        Log.i("CalibrationFragment", "Fragment Calibrate --- setupCameraPreview");
        mainViewModel.setCameraPreview(textureView);
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
        // Read concentration values and store in array
        handleSubmitCalibrationChange();
        mainViewModel.doCalibration(etNotes.getText().toString());
        calibrationBitMap = mainViewModel.getCalibrationBitmap();
        if (calibrationBitMap == null) {
            Log.i("CalibrationFragment", "Fragment Calibrate --- calibrationBitMap still NULL");
        } else {
            imageView.setImageBitmap(calibrationBitMap);
            updateCalibrateUI(mainViewModel.calibrationIntensities);
        }
        calibrationBitMap = displayWellBoxes();
        imageView.setImageBitmap(calibrationBitMap);
    }

    private void calibrateFromPhoto() {
        if (isPreviewVisible) {
            Log.i("CalibrationFragment", "calibrateFromPhoto --- isPreviewVisible TRUE");
            mainViewModel.takePhoto();
            calibrationBitMap = mainViewModel.getCalibrationBitmap();
            //mainViewModel.doCalibration();   // do this when livedata shows image is ready

            buttonCalibrate.setText("Processing");
            buttonCalibrate.setEnabled(false);

            mainViewModel.setCalibrationBitMap(calibrationBitMap);
            imageView.setImageBitmap(calibrationBitMap);

            //updateCalibrateUI(mainViewModel.calibrationIntensities);

            textureView.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);

            isPreviewVisible = false;


        } else {
            Log.i("CalibrationFragment", "calibrateFromPhoto --- isPreviewVisible FALSE");
            textureView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.INVISIBLE);

            buttonCalibrate.setText("Calibrate");
            buttonCalibrate.setEnabled(true);

            isPreviewVisible = true;
        }
    }

    private void calibrateFromSampleImage() {
        calibrationBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_a);
        Log.i("CalibrationFragment", "Sample image size width=" + calibrationBitMap.getWidth() + " and height=" + calibrationBitMap.getHeight());
        mainViewModel.setCalibrationBitMap(calibrationBitMap);
        mainViewModel.doCalibration(etNotes.getText().toString());
        imageView.setImageBitmap(calibrationBitMap);
        updateCalibrateUI(mainViewModel.calibrationIntensities);
        textureView.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.VISIBLE);
    }

    private void updateCalibrateUI(List<Double> calibrationIntensities) {

        int index = 0;
        for (TextView tv : calibrationIntensityTextViews) {
            if (calibrationIntensities.get(index) != null)
                tv.setText(String.format("%,.0f", calibrationIntensities.get(index)));
            else tv.setText(String.format("%.1f", "0"));
            index++;
        }


        tvSlope.setText(String.format("%.5f", mainViewModel.getCalibrationSlope()));
        tvRSq.setText(String.format("%.5f", mainViewModel.getCalibrationRSq()));

        buttonCalibrate.setText("Rerun Calibration?");
        buttonCalibrate.setEnabled(true);
    }

    private void setupUnitsSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.concentration_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(adapter);

        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedConcentration = parentView.getItemAtPosition(position).toString();
                concentrationFactor = 0.001;        // default value
                if (selectedConcentration.equalsIgnoreCase("Micro"))
                    concentrationFactor = 0.000_001;
                if (selectedConcentration.equalsIgnoreCase("Nano"))
                    concentrationFactor = 0.000_000_001;
                if (selectedConcentration.equalsIgnoreCase("Pico"))
                    concentrationFactor = 0.000_000_000_001;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    private ArrayList<Double> getValuesForCalibration(List<EditText> inCalibrationEditTexts) {
        ArrayList<Double> outConcentrationValues = new ArrayList<>();
        Double adjustedValue = 0.0;  // used to adjust for units
        for (EditText et : inCalibrationEditTexts) {
            String newValue = et.getText().toString();
            if (newValue == null || newValue.equals(""))
                newValue = "0";
            adjustedValue = Double.parseDouble(newValue) * concentrationFactor;
            outConcentrationValues.add(adjustedValue);
        }
        return outConcentrationValues;
    }

    private Bitmap displayWellBoxes() {
        Log.i("CIS4444", "CalibrationFragment - displayWellBoxes=" );
        List<Region> regions = mainViewModel.setupStandardRegions();
        //imageView.setImageBitmap(wellBoxOverlay.drawBoundingBoxes(regions, imageView));
        return wellBoxOverlay.drawBoundingBoxes(regions, imageView);
    }

}