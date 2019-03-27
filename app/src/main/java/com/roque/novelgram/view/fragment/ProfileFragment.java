package com.roque.novelgram.view.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.roque.novelgram.R;
import com.roque.novelgram.adapter.PictureAdapterRecyclerView;
import com.roque.novelgram.model.Picture;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private RecyclerView picturesRecycle;
    private FirebaseFirestore firebaseFirestore;
    private static final String TAG = "ProfileFragment";

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        showToolbar("", false, view);

        firebaseFirestore = FirebaseFirestore.getInstance();

        picturesRecycle = (RecyclerView) view.findViewById(R.id.picture_profile_recycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        picturesRecycle.setLayoutManager(linearLayoutManager);

        buildPicture();

        return view;

    }

    public void buildPicture(){

        final ArrayList<Picture> pictures = new ArrayList<>();

        final PictureAdapterRecyclerView pictureAdapterRecyclerView = new PictureAdapterRecyclerView(pictures, R.layout.cardview_picture, getActivity());
        picturesRecycle.setAdapter(pictureAdapterRecyclerView);

        firebaseFirestore.collection("pictures")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    String key = document.getId();
                                    String picture = document.get("picture").toString();
                                    String name = document.get("name").toString();
                                    String time = document.get("time").toString();
                                    String like_number = document.get("like_number").toString();
                                    String description = document.get("description").toString();;
                                    String extra = document.get("extra").toString();;

                                    pictures.add(new Picture(key, picture, name, time, like_number, description, extra));
                                }
                            }
                            pictureAdapterRecyclerView.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public void showToolbar(String tittle, boolean upButton, View view){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle(tittle);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(upButton);
    }
}
