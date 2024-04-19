package css.cecprototype2.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import css.cecprototype2.R;
import css.cecprototype2.databinding.ActivityMainBinding;
import css.cecprototype2.main.MainViewModel;
import css.cecprototype2.databinding.FragmentHomeBinding;

public class FragmentHome extends Fragment {

    private FragmentHomeBinding binding;
    private Button buttonCalibrate, buttonAnalyze, buttonSubmitCamSettings;
    private EditText etISO, etFocus, etExposureTime;
    private MainViewModel mainViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        setupButtons();
        setUpEditTexts();
        return root;
    }

    private void setupButtons() {
        // This app uses the new bindings instead of the old findViewById
        buttonCalibrate = binding.buttonHomeCalibrate;
        buttonCalibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CIS 4444", "Goto Calibrate button clicked");   // log button click for debugging
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.action_navigation_home_to_navigation_calibrate);
            }
        });

        // This app uses the new bindings instead of the old findViewById
        buttonAnalyze = binding.buttonHomeAnalyze;
        buttonAnalyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CIS 4444", "Goto Analyze button clicked");   // log button click for debugging
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.action_navigation_home_to_navigation_calibrate);
            }
        });

        buttonSubmitCamSettings = binding.buttonSubmitSettings;
        buttonSubmitCamSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("FragmentHome", "Submit Camera Settings Clicked...");   // log button click for debugging
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.action_navigation_home_to_navigation_analyze);

                Log.w("FragmentHome", "Submit Camera Settings Clicked...");
                //ISO: 100-1000
                // FOCUS: 0-200
                //EXPOSURE: 0.12 - 100 milliseconds or 120 - 100,000 input

                //if (etISO.getText().toString().equals("")) etISO.setText("0");
                //if (etFocus.getText().toString().equals("")) etFocus.setText("0");
                //if (etExposureTime.getText().toString().equals("")) etExposureTime.setText("0");
                Integer iso = Integer.parseInt(etISO.getText().toString());
                if (iso < 1000 && iso > 99 ) {
                    mainViewModel.cam.setISO(iso);
                    Log.d("FragmentHome", "Setting ISO to "+iso);
                }
                Integer focus = Integer.parseInt(etFocus.getText().toString());
                if ( focus < 201 && focus > -1) {
                    mainViewModel.cam.setFocus(focus);
                    Log.d("FragmentHome", "Setting focus to " + focus);
                }

                Double exposure_seconds = Double.parseDouble(etExposureTime.getText().toString());
                Long exposure_nanoSeconds = (long) (exposure_seconds * 1_000_000_000);  // convert to an integer
                if (exposure_seconds < 100 && exposure_seconds > 0.000_000_001 ) {
                    mainViewModel.cam.setExposureTime(exposure_nanoSeconds);
                    Log.d("FragmentHome", "Setting exposure to " + exposure_seconds +" seconds or "+ exposure_nanoSeconds + "nano seconds" );
                }

                Log.d("HomeFragment", "Cam ISO set to: " +  iso);
                Log.d("HomeFragment", "Cam Focal Length set to: " +  focus);
                Log.d("HomeFragment", "Cam Exposure Time set to: 1,000,000,000 * " +  exposure_seconds);

            }
        });
    }

    private void setUpEditTexts()
    {
        etISO = binding.etISO;
        etFocus = binding.etFocus;
        etExposureTime = binding.etExposureTime;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}