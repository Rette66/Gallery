package com.example.galleryapi33;

public class image {

    private String id;
    private int orientation;
    private int width;
    private int height;

    public image(String id, int orientation, int width, int height){
        this.id = id;
        this.orientation = orientation;
        this.width = width;
        this.height = height;
    }


    public String getId() {
        return id;
    }

    public int getOrientation() {
        return orientation;
    }
}
