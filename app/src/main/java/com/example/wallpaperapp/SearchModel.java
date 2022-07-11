package com.example.wallpaperapp;

import java.util.ArrayList;

public class SearchModel {

    public int page;
    public int per_page;
    public ArrayList<ImageModel> photos;
    public String next_page;

    public int getPage() {
        return page;
    }

    public int getPer_page() {
        return per_page;
    }

    public ArrayList<ImageModel> getPhotos() {
        return photos;
    }

    public String getNext_page() {
        return next_page;
    }
}
