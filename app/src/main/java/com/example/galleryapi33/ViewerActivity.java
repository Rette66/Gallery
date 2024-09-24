package com.example.galleryapi33;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ScaleGestureDetector;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ViewerActivity extends AppCompatActivity {
    private ImageView imageView;
    private ScaleGestureDetector scaleGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);

        setCutoutDisplay();

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
        imageView.setOnTouchListener((v, event) -> {
            scaleGestureDetector.onTouchEvent(event);
            return true;
        });
//        imageView.setScaleType(ImageView.ScaleType.MATRIX);

    }

    private void setCutoutDisplay(){
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        window.setAttributes(params);
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
            imageView.invalidate(); // 通知ImageView重绘
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
//            scaleFactor = 1.0f;
        }
    }


}