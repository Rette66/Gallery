package com.example.galleryapi33;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.Manifest.permission.READ_MEDIA_IMAGES;

public class MainActivity extends AppCompatActivity implements Loader.OnPhotosLoadedListener{

    // on below line we are creating variables for
    // our array list, recycler view and adapter class.
    private static final int READ_IMAGE_PERMISSION_CODE = 100;
    private RecyclerView recyclerView;
    private Cache cache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initializing recycler view.
        prepareRecyclerView();

        //set cutout to be displayed
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        window.setAttributes(params);

        //initial imageCache
        cache = new Cache((int) (Runtime.getRuntime().maxMemory() / 8));

        // request permissions
        requestPermissions();
    }


    private void prepareRecyclerView() {
        recyclerView = findViewById(R.id.idRVImages);
        // creating a new grid layout manager.
        GridLayoutManager manager = new GridLayoutManager(MainActivity.this, 4);

        //setting layout manager
        recyclerView.setLayoutManager(manager);
    }

    @Override
    public void onPhotosLoaded(List<image> imageList) {
        runOnUiThread(() -> {
            recyclerView.setAdapter(new Adaptor(imageList, cache, this));
        });
    }

    private void requestPermissions() {
        if (checkPermission()) {
            //permission granted, load photos
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(new Loader(this, this));
        } else {
            //no permission, request permission
            ActivityCompat.requestPermissions(this, new String[]{READ_MEDIA_IMAGES}, READ_IMAGE_PERMISSION_CODE);
        }
    }

    private boolean checkPermission() {
        //checking if the permissions are granted or not and returning the result.
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_MEDIA_IMAGES);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void loadPhotos(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Loader(this, this));
    }


    @Override
    public void onRequestPermissionsResult(int code, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(code, permissions, grantResults);
        if (code == READ_IMAGE_PERMISSION_CODE) {// in this case we are checking if the permissions are accepted or not.
            if (grantResults.length > 0) {
                boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (storageAccepted) {
                    //load photos
                    loadPhotos();
                } else {
                    // if permissions are denied close the app and displaying the toast message.
                    Toast.makeText(this, "Permissions denied, Permissions are required to use the app..", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}
