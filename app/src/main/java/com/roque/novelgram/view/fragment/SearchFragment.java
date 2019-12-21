package com.roque.novelgram.view.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.roque.novelgram.R;
import com.roque.novelgram.adapter.PictureAdapterRecyclerView;
import com.roque.novelgram.adapter.PictureSmallAdapterRecyclerView;
import com.roque.novelgram.model.Picture;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private RecyclerView picturesRecycler;
    private FirebaseFirestore firebaseFirestore;
    private static final String TAG = "SearchFragment";

    private int numberOfColumns = 2;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        picturesRecycler = (RecyclerView)view.findViewById(R.id.search_fragment_recycler);

        firebaseFirestore = FirebaseFirestore.getInstance();

        GridLayoutManager gridLayout = new GridLayoutManager(getContext(), numberOfColumns);
        picturesRecycler.setLayoutManager(gridLayout);

        buildPicture();

        return view;
    }

    public void buildPicture(){

        final ArrayList<Picture> pictures = new ArrayList<>();

        final PictureSmallAdapterRecyclerView pictureAdapterRecyclerView = new PictureSmallAdapterRecyclerView(pictures, R.layout.cardview_picture_small, getActivity());
        picturesRecycler.setAdapter(pictureAdapterRecyclerView);

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
}
