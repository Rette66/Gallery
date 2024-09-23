package com.example.galleryapi33;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.Manifest.permission.READ_MEDIA_IMAGES;

import com.example.galleryapi33.Photo.ImageCache;
import com.example.galleryapi33.Photo.Photo;
import com.example.galleryapi33.Photo.PhotoAdaptor;
import com.example.galleryapi33.Photo.PhotoLoader;

public class MainActivity extends AppCompatActivity implements PhotoLoader.OnPhotosLoadedListener{

    // on below line we are creating variables for
    // our array list, recycler view and adapter class.
    private static final int PERMISSION_REQUEST_CODE = 200;
    private RecyclerView recyclerView;
    private ImageCache imageCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initializing recycler view.
        prepareRecyclerView();

        imageCache = new ImageCache((int) (Runtime.getRuntime().maxMemory() / 8));

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
    public void onPhotosLoaded(List<Photo> photoList) {
        runOnUiThread(() -> {
            recyclerView.setAdapter(new PhotoAdaptor(photoList, imageCache, this));
        });
    }

    private void requestPermissions() {
        if (checkPermission()) {
            //permission granted, load photos
            loadPhotos();
        } else {
            //no permission, request permission
            requestPermission();
        }
    }

    private boolean checkPermission() {
        //checking if the permissions are granted or not and returning the result.
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_MEDIA_IMAGES);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void loadPhotos(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new PhotoLoader(this, this));
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{READ_MEDIA_IMAGES}, PERMISSION_REQUEST_CODE);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        // this method is called after permissions has been granted.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // we are checking the permission code.
        if (requestCode == PERMISSION_REQUEST_CODE) {// in this case we are checking if the permissions are accepted or not.
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
