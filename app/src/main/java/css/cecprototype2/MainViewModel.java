package css.cecprototype2;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.camera.view.PreviewView;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends AndroidViewModel {

    private SensorCamera cam;
    Context appCcontext;

    public MainViewModel(@NonNull Application application) {
        super(application);
        appCcontext = application;
    }

    public void initializeCamera(PreviewView previewView) {
        cam = new SensorCamera(appCcontext,previewView);
    }

    public void takePhoto() {

    }


}
