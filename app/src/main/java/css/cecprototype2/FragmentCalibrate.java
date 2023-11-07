package css.cecprototype2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import css.cecprototype2.databinding.FragmentCalibrateBinding;

public class FragmentCalibrate extends Fragment {

    private FragmentCalibrateBinding binding;
    private MainViewModel viewModel;
    Button buttonCalibrate;
    Button buttonCalibrateSample;
    TextView tvCalibrate1, tvCalibrate2, tvCalibrate3, tvCalibrate4, tvCalibrate5;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Use the shared ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        binding = FragmentCalibrateBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        tvCalibrate1 = binding.textViewCalibrate1;
        // TODO -- Add bindings for rest of the textview.

        setupButtons();

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
                Log.d("CIS 4444", "Calibrate button clicked");   // log button click for debugging
            }
        });

        // This app uses the new bindings instead of the old findViewById
        buttonCalibrateSample = binding.buttonCalibrateSample;
        buttonCalibrateSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CIS 4444", "Calibrate with Samples button clicked");   // log button click for debugging
            }
        });
    }  // end SetupButtons
}