package com.example.wallpaperapp;

import android.app.Dialog;
import android.content.Context;
import android.widget.ImageButton;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public class ApiUtilities {
    public static final String API = "563492ad6f91700001000001db090b1d29b94949bfc1b4f477c73d39";
    Context context ;
    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://api.pexels.com/v1/")
                            .addConverterFactory(GsonConverterFactory.create()).build();

    public ApiUtilities(Context context) {
        this.context = context;
    }

    public void getapi(Fetchlistener listener , int page , int per_page){
        ApiInterface intf = retrofit.create(ApiInterface.class);
        Call<SearchModel> call = intf.getImage(page,per_page);
        call.enqueue((new Callback<SearchModel>() {
            @Override
            public void onResponse(Call<SearchModel> call, Response<SearchModel> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(context,"Error Occured!!",Toast.LENGTH_SHORT).show();
                    listener.onError(response.message());
                    return ;
                }
                listener.onFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<SearchModel> call, Throwable t) {
                listener.onError("Internet");
            }
        }));
    }



    public void getapisearch(Fetchlistener listener ,String query , int page , int per_page){
        ApiInterface intf = retrofit.create(ApiInterface.class);
        Call<SearchModel> call = intf.getSearchImage(query,page,per_page);
        call.enqueue((new Callback<SearchModel>() {
            @Override
            public void onResponse(Call<SearchModel> call, Response<SearchModel> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(context,"Error Occured!!",Toast.LENGTH_SHORT).show();
                    listener.onError(response.message());
                    return ;
                }
                listener.onFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<SearchModel> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        }));
    }

    public interface ApiInterface {

        @Headers("Authorization: "+API)
        @GET("curated")
        Call<SearchModel> getImage(
                @Query("page") int page,
                @Query("per_page") int per_page
        );

        @Headers("Authorization: "+API)
        @GET("search")
        Call<SearchModel> getSearchImage(
                @Query("query") String query,
                @Query("page") int page,
                @Query("per_page") int per_page
        );
    }
}
