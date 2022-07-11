package com.example.wallpaperapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

public class BookMarkFragment extends Fragment {

    Activity mactivity ;
    FirebaseFirestore fStore ;
    FirebaseAuth fAuth ;
    Dialog pdialog ;
    RecyclerView recyclerView ;
    BookMarkAdapter badapter ;
    TextView booktext ;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(context instanceof Activity){
            mactivity = (Activity) context ;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_book_mark, container, false);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recyclerviewbookmark);
        booktext=view.findViewById(R.id.booktext);
        pdialog=new Dialog(mactivity);
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        pdialog.setContentView(R.layout.progress_bar);
        pdialog.show();
        if(fAuth.getCurrentUser()!=null){
            DocumentReference documentReference = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
            if(fStore!=null){
                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error==null){
                            if(value.exists()){
                                pdialog.dismiss();
                                ArrayList<String> arr = ( ArrayList<String>) value.get("bookmark_list");
                                if(arr.size()==0) {
                                    booktext.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.INVISIBLE);
                                }
                                else {
                                    booktext.setVisibility(View.INVISIBLE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                }
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setLayoutManager(new GridLayoutManager(mactivity,2));
                                badapter = new BookMarkAdapter(mactivity,arr);
                                recyclerView.setAdapter(badapter);
                            }
                        }
                        else {
                            Log.d("tag", "onEvent: Document do not exists");
                        }
                    }
                });
            }
        }


        return view ;
    }
}