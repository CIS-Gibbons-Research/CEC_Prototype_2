package css.cecprototype2.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
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

import java.util.ArrayList;
import java.util.List;

import css.cecprototype2.R;
import css.cecprototype2.main.ImageStacker;
import css.cecprototype2.main.MainViewModel;
import css.cecprototype2.databinding.FragmentAnalyzeBinding;

public class FragmentAnalyze extends Fragment {

    private FragmentAnalyzeBinding binding;
    private MainViewModel mainViewModel;
    private boolean bitmapAvailable;
    private boolean isPreviewVisible = true;
    TextView tvStatus;
    ImageView imageView;
    PreviewView previewView;
    Bitmap analysisBitMap;
    Button buttonAnalyze;
    Button buttonAnalyzeSample;
    Button buttonAnalyzeBurst;
    TextView tvAnalyze1, tvAnalyze2, tvAnalyze3, tvAnalyze4, tvAnalyze5, tvAnalyze6;
    private ImageView testImageView;

    public void setTestImageView(ImageView testImageView) {
        this.testImageView = testImageView;
    }

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
        setupLiveDataObservers();

        return root;
    }

    public void setMainViewModel(MainViewModel vm)
    {
        this.mainViewModel = vm;
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
                // Run analysis logic when taking the first photo
                Log.d("CIS 4444", "Analysis button clicked");   // log button click for debugging
                analyzeFromPhoto();
            }
        });

        // This app uses the new bindings instead of the old findViewById
        buttonAnalyzeSample = binding.buttonAnalyzeSample;
        buttonAnalyzeSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CIS 4444", "Analysis with Samples button clicked");   // log button click for debugging
                analyzeFromSampleImage();
            }
        });

        buttonAnalyzeBurst = binding.buttonAnalyzeBurst;
        buttonAnalyzeBurst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CIS 4444", "Analysis with Burst button clicked");   // log button click for debugging
                analyzeFromBurst();
            }
        });
    }  // end SetupButtons

    private void setupCameraPreview() {
        Log.i("CIS4444", "Fragment Analysis --- setupCameraPreview");
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
                    Log.i("CIS4444", "Fragment Analysis --- LiveData --- bitmap NOT Available");
                    tvStatus.setText("Bitmap not available");
                }
            }
        });
    }

    public void analyzeFromBurst() {
        // Number of photos to capture in the burst
        int burstCount = 6;

        // List to store the captured bitmaps
        List<Bitmap> burstBitmaps = new ArrayList<>();

        // Create a new ImageStacker instance for this burst
        ImageStacker burstImageStacker = new ImageStacker();

        // Capture a series of photos in succession
        for (int i = 0; i < burstCount; i++) {
            mainViewModel.takePhoto();
            mainViewModel.retrieveAnalysisBitmapFromCamera();
            burstBitmaps.add(mainViewModel.getAnalysisBitmap());
        }

        // Set the ImageStacker for the mainViewModel
        mainViewModel.setImageStacker(burstImageStacker);

        // Use some function to consolidate the captured images (e.g., average)
        Bitmap consolidatedBitmap = burstImageStacker.averagePixelValues(burstBitmaps);

        // Set the consolidated bitmap in the ViewModel for analysis
        mainViewModel.setAnalysisBitMap(consolidatedBitmap);

        // Perform analysis on the consolidated image
        mainViewModel.doAnalysis();

        // Display the consolidated bitmap in the imageView
        imageView.setImageBitmap(consolidatedBitmap);

        // Update the UI with new analysis readings
        updateAnalyzeUI();
    }

    public Bitmap consolidateBurstImages(List<Bitmap> burstBitmaps) {
        if (burstBitmaps == null || burstBitmaps.isEmpty()) {
            return null; // Handle empty or null list
        }

        // Implement your logic to consolidate burst images (e.g., averaging)
        ImageStacker imageStacker = new ImageStacker();
        return imageStacker.averagePixelValues(burstBitmaps);
    }

    private void readingsAvailableUpdateUI() {
        Log.i("CIS4444", "Fragment Analysis --- LiveData --- bitmap Available");
        // Run Analysis logic when taking the first photo
        mainViewModel.retrieveAnalysisBitmapFromCamera();
        mainViewModel.doAnalysis();
        // Get the bitmap from the ViewModel
        analysisBitMap = mainViewModel.getAnalysisBitmap();
        if (analysisBitMap == null) {
            Log.i("CIS4444", "Fragment Analysis --- analysisBitMap still NULL");
        } else {
            // Display the photo bitmap in the imageView
            imageView.setImageBitmap(analysisBitMap);
            // Update the UI with new analysis readings
            updateAnalyzeUI();
        }
    }

    private void analyzeFromPhoto() {
        if (isPreviewVisible) {
            mainViewModel.takePhoto();

            // Change the button text and disable it
            buttonAnalyze.setText("Processing");
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
            buttonAnalyze.setText("Analyze");
            buttonAnalyze.setEnabled(true);

            isPreviewVisible = true;
        }
    }

    private void analyzeFromSampleImage() {
        // Use the resource identifier to load the sample image
        analysisBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_a);
        Log.i("CIS4444", "Sample image size width="+analysisBitMap.getWidth()+ " and height="+analysisBitMap.getHeight());
        mainViewModel.setAnalysisBitMap(analysisBitMap);
        mainViewModel.doAnalysis();
        // Display the photo bitmap in the imageView
        imageView.setImageBitmap(analysisBitMap);
        // Update the UI with new calibration readings
        updateAnalyzeUI();
        // Make the previewView invisible and imageViewCamera visible
        previewView.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.VISIBLE);
    }

    private void updateAnalyzeUI() {
        // TODO: The textviews should be in a list and this should be a loop
        tvAnalyze1.setText(mainViewModel.getAnalysisIntensityAt(0));
        tvAnalyze2.setText(mainViewModel.getAnalysisIntensityAt(1));
        tvAnalyze3.setText(mainViewModel.getAnalysisIntensityAt(2));
        tvAnalyze4.setText(mainViewModel.getAnalysisIntensityAt(3));
        tvAnalyze5.setText(mainViewModel.getAnalysisIntensityAt(4));
        tvAnalyze6.setText(mainViewModel.getAnalysisIntensityAt(5));
        // Change the button text and disable it
        buttonAnalyze.setText("Reset Analysis?");
        buttonAnalyze.setEnabled(true);
    }


}