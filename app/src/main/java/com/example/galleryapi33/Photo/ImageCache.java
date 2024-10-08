package com.example.galleryapi33.Photo;

import android.graphics.Bitmap;
import android.util.LruCache;

public class ImageCache {
    private LruCache<String, Bitmap> cache;

    public ImageCache(int maxMemory) {
//        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        this.cache = new LruCache<>(maxMemory);
    }

    public void put(String key, Bitmap bitmap) {
        cache.put(key, bitmap);
    }

    public Bitmap get(String key) {
        return cache.get(key);
    }

    public void clear() {
        cache.evictAll();
    }
}
