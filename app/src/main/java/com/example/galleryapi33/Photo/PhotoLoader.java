package com.example.galleryapi33.Photo;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;
public class PhotoLoader implements Runnable {
    private Context context;
    private OnPhotosLoadedListener listener;

    public PhotoLoader(Context context, OnPhotosLoadedListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void run() {
        List<Photo> photoList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        Cursor cursor = contentResolver.query(uri, null, null, null, sortOrder);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                int orientation = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION));
                int width = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
                int height = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
                photoList.add(new Photo(id, orientation, width, height));
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Notify the main thread
        new Handler(Looper.getMainLooper()).post(() -> listener.onPhotosLoaded(photoList));
    }

    public interface OnPhotosLoadedListener {
        void onPhotosLoaded(List<Photo> photoList);
    }
}