package com.roque.novelgram.view.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.roque.novelgram.R;
import com.roque.novelgram.adapter.PictureAdapterRecyclerView;
import com.roque.novelgram.model.Picture;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private RecyclerView picturesRecycle;
    private FirebaseFirestore firebaseFirestore;
    private static final String TAG = "ProfileFragment";
    private TextView userName;

    // vars
    String uid, name, email;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        showToolbar("", false, view);

        firebaseFirestore = FirebaseFirestore.getInstance();

        picturesRecycle = view.findViewById(R.id.picture_profile_recycler);
        userName = view.findViewById(R.id.userName);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        picturesRecycle.setLayoutManager(linearLayoutManager);

        buildPicture();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            uid = user.getUid();
            name = user.getDisplayName();
            email = user.getEmail();

            userName.setText(name);
        } else {
            // No user is signed in
        }
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

    private void showToolbar(String tittle, boolean upButton, View view){
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle(tittle);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(upButton);
    }
}
