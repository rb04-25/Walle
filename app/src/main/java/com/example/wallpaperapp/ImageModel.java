package com.example.wallpaperapp;

import java.io.Serializable;

public class ImageModel implements Serializable {

    public int id;
    public int width;
    public int height;
    public String url;
    public String photographer;
    public String photographer_url;
    public int photographer_id;
    public String avg_color;
    public UrlModel src;
    public boolean liked;
    public String alt;

    public int getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getUrl() {
        return url;
    }

    public String getPhotographer() {
        return photographer;
    }

    public String getPhotographer_url() {
        return photographer_url;
    }

    public int getPhotographer_id() {
        return photographer_id;
    }

    public String getAvg_color() {
        return avg_color;
    }

    public UrlModel getSrc() {
        return src;
    }

    public boolean isLiked() {
        return liked;
    }

    public String getAlt() {
        return alt;
    }
}
