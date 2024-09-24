package com.example.galleryapi33;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Adaptor extends RecyclerView.Adapter<Adaptor.PhotoViewHolder> {

    private List<image> imageList;
    private Cache cache;
    private Context context;

    public Adaptor(List<image> imageList, Cache cache, Context context){
        this.imageList = imageList;
        this.cache = cache;
        this.context = context;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnails, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        image image = imageList.get(position);
        Bitmap thumbnail = loadThumbnail(image.getId(), image.getOrientation());
        holder.imageView.setImageBitmap(thumbnail);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;

        public PhotoViewHolder(View itemView){
            super(itemView);
            imageView = this.itemView.findViewById(R.id.thumbnailsImage);
            this.imageView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    image image = imageList.get(position);
                    Intent intent = new Intent(this.itemView.getContext(), ViewerActivity.class);
                    intent.putExtra("photo_id", image.getId());
                    this.itemView.getContext().startActivity(intent);
                }
            });
        }
    }

    private Bitmap loadThumbnail(String photoId, int orientation) {
        Bitmap thumbnail = cache.get(photoId);
        if (thumbnail == null) {
            try{
                InputStream is = context.getContentResolver().openInputStream(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, photoId));
                if (is != null) {
                    // decode input stream
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    is.close();  // close stream

                    // create thumbnail
                    if (orientation != 0) {
                        bitmap = rotateBitmap(bitmap, orientation);
                    }

                    thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 300, 300);
                    cache.put(photoId, thumbnail);  // save thumbnail in cache
                } else {
                    Log.e("PhotoLoader", "InputStream is null for photoId: " + photoId);
                }
            } catch (IOException e) {
                Log.e("PhotoLoader", "Error loading thumbnail for photoId: " + photoId, e);
            }
        }
        return thumbnail;
    }


    private Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case 90:
                matrix.postRotate(90);
                break;
            case 180:
                matrix.postRotate(180);
                break;
            case 270:
                matrix.postRotate(270);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap adjusted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return adjusted;
        } catch (OutOfMemoryError e) {
            return bitmap;
        }
    }

}
