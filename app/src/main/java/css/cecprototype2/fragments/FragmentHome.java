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
    private Button buttonCalibrate, buttonAnalyze;
    private EditText etISO, etFocus, etExposureTime;
    private MainViewModel mainViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

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
                navController.navigate(R.id.action_navigation_home_to_navigation_analyze);
            }
        });
    }

    private void setUpEditTexts()
    {
        etISO = binding.etISO;
        etFocus = binding.etFocus;
        etExposureTime = binding.etExposureTime;

        if (mainViewModel != null && mainViewModel.cam != null)
        {
            mainViewModel.cam.setISO(Integer.parseInt(etISO.getText().toString()));
            mainViewModel.cam.setFocus(Integer.parseInt(etFocus.getText().toString()));
            mainViewModel.cam.setExposureTime(Integer.parseInt(etExposureTime.getText().toString()));

            Log.d("HomeFragment", "Cam ISO set to: " +  etISO.getText().toString());
            Log.d("HomeFragment", "Cam Focal Length set to: " +  etFocus.getText().toString());
            Log.d("HomeFragment", "Cam Exposure Time set to: " +  etExposureTime.getText().toString());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}