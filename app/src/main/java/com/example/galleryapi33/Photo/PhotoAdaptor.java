package com.example.galleryapi33.Photo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.example.galleryapi33.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PhotoAdaptor extends RecyclerView.Adapter<PhotoAdaptor.PhotoViewHolder> {

    private List<Photo> photoList;
    private ImageCache imageCache;
    private Context context;

    public PhotoAdaptor(List<Photo> photoList, ImageCache imageCache, Context context){
        this.photoList = photoList;
        this.imageCache = imageCache;
        this.context = context;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        Photo photo = photoList.get(position);
        Bitmap thumbnail = loadThumbnail(photo.getId());
        holder.imageView.setImageBitmap(thumbnail);
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;

        public PhotoViewHolder(View itemView){
            super(itemView);
            imageView = this.itemView.findViewById(R.id.idIVImage);
            this.imageView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Photo photo = photoList.get(position);
                    Intent intent = new Intent(this.itemView.getContext(), PhotoViewerActivity.class);
                    intent.putExtra("photo_id", photo.getId());
                    this.itemView.getContext().startActivity(intent);
                }
            });
        }
    }

    private Bitmap loadThumbnail(String photoId) {
        Bitmap thumbnail = imageCache.get(photoId);
        if (thumbnail == null) {
            try{
                InputStream is = context.getContentResolver().openInputStream(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, photoId));
                if (is != null) {
                    // decode input stream
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    is.close();  // close stream

                    // create thumbnail
                    thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 300, 300);
                    imageCache.put(photoId, thumbnail);  // save thumbnail in cache
                } else {
                    Log.e("PhotoLoader", "InputStream is null for photoId: " + photoId);
                }
            } catch (IOException e) {
                Log.e("PhotoLoader", "Error loading thumbnail for photoId: " + photoId, e);
            }
        }
        return thumbnail;    }
}
