package css.cecprototype2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    MainViewModel mainViewModel;   // View Model for the main activity
    TextView tvStatus;
    Button buttonUpdate;
    ImageView imageViewCamera;
    PreviewView previewView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up UI components
        tvStatus = findViewById(R.id.tvStatus);
        imageViewCamera = findViewById(R.id.imageView);
        previewView = findViewById(R.id.previewView);
        imageViewCamera = findViewById(R.id.imageView);
        // set up modelView
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.initializeCamera(this, previewView);
        // set up buttons
        setupButtonUpdate();
    }

    private void setupButtonUpdate() {
        buttonUpdate = findViewById(R.id.button1);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewModel.takePhoto();
                int pixel = mainViewModel.getPixel(50,60);
                tvStatus.setText("The pixel value at 50, 60 is "+pixel);
            }
        });
    }
}