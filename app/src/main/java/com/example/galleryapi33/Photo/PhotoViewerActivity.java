package com.example.galleryapi33.Photo;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.galleryapi33.R;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class PhotoViewerActivity extends AppCompatActivity {
    private ImageView imageView;
    private ScaleGestureDetector scaleGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);

        imageView = findViewById(R.id.photo_image_viewer);
        String photoId = getIntent().getStringExtra("photo_id");

        scaleGestureDetector = new ScaleGestureDetector(this, new scaleListener());

        //display image using bitmap
        Bitmap bitmap = null;
        try {
            bitmap = loadBitmap(photoId);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        imageView.setImageBitmap(bitmap);
    }


    private Bitmap loadBitmap(String photoId) throws FileNotFoundException {
        InputStream is = getContentResolver().openInputStream(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, photoId));
        return BitmapFactory.decodeStream(is);
    }

    private class scaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        private float scaleFactor = 1.0f;

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(1.0f, Math.min(scaleFactor, 5.0f));
            imageView.setScaleX(scaleFactor);
            imageView.setScaleY(scaleFactor);
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            scaleFactor = 1.0f;
        }
    }


}