package com.roque.novelgram.post.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.roque.novelgram.R;
import com.roque.novelgram.adapter.PictureAdapterRecyclerView;
import com.roque.novelgram.model.Picture;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private FloatingActionButton fabCamera;
    private RecyclerView picturesRecycle;
    private ProgressBar progressBarHome;

    private static final String TAG = "HomeFragment";

    private FirebaseFirestore firebaseFirestore;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        showToolbar(getResources().getString(R.string.tap_home), false, view);

        firebaseFirestore = FirebaseFirestore.getInstance();

        fabCamera = (FloatingActionButton)view.findViewById(R.id.fabCamera);
        progressBarHome = view.findViewById(R.id.progressBarHome);
        picturesRecycle = (RecyclerView) view.findViewById(R.id.pictureRecycler);
        picturesRecycle.setHasFixedSize(true);
        showProgressBar();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        picturesRecycle.setLayoutManager(linearLayoutManager);

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

                                    Log.d(TAG, "Id: " + document.getId() + ", Nombre: " + name);
                                    pictures.add(new Picture(key, picture, name, time, like_number, description, extra));
                                }
                            }
                            pictureAdapterRecyclerView.notifyDataSetChanged();
                            hideProgressBar();
                        } else {
                            hideProgressBar();
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para la camara!.");
            String message = getString(R.string.message_cameraNotAvailablePermission);
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        } else {
            Log.i("Mensaje", "Tienes permiso para usar la camara.");
        }

        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewPostActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    public void showToolbar(String tittle, boolean upButton, View view){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(tittle);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
    }

    public void showProgressBar() { progressBarHome.setVisibility(View.VISIBLE); }

    public void hideProgressBar() { progressBarHome.setVisibility(View.INVISIBLE); }

}
