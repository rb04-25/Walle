package com.example.wallpaperapp;

public interface Fetchlistener {

    void onFetch(SearchModel response , String message);
    void onError(String message);

}
