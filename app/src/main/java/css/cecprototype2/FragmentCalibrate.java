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
    Button buttonUpdateOrder;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //DashboardViewModel dashboardViewModel =  new ViewModelProvider(this).get(DashboardViewModel.class);
        // Use the shared ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        binding = FragmentCalibrateBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        viewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        setupButton();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupButton() {
        //buttonUpdateOrder = findViewById(R.id.buttonUpdateOrder);
        // This app uses the new bindings instead of the old findViewById
        buttonUpdateOrder= binding.buttonUpdateOrder;
        buttonUpdateOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CIS 3334", "Place order button clicked");   // log button click for debugging using "CIS 3334" tag
                viewModel.setText("Order Placed");
            }
        });
    }
}