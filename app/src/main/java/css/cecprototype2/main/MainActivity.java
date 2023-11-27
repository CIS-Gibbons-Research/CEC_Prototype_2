package css.cecprototype2.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;
//import org.apache.commons.math3.stat.regression.SimpleRegression;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import css.cecprototype2.R;
import css.cecprototype2.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // test comment -- delete later

    private ActivityMainBinding binding;
    public SensorCamera cam;
    MainViewModel mainViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupNavBindings();

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

    }

    private void setupCamera() {
        Log.i("CIS4444", "Main Activity --- setupCamera");
        cam = new SensorCamera(this, this);
        mainViewModel.initializeCamera(cam);
    }


    private void setupNavBindings() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_calibrate, R.id.navigation_analyze)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

}