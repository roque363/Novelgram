package com.roque.novelgram.view.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roque.novelgram.R;
import com.roque.novelgram.adapter.PictureAdapterRecyclerView;
import com.roque.novelgram.model.Picture;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        showToolbar("", false, view);

        RecyclerView picturesRecycle = (RecyclerView) view.findViewById(R.id.picture_profile_recycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        picturesRecycle.setLayoutManager(linearLayoutManager);

        PictureAdapterRecyclerView pictureAdapterRecyclerView = new PictureAdapterRecyclerView(buildPicture(), R.layout.cardview_picture, getActivity());
        picturesRecycle.setAdapter(pictureAdapterRecyclerView);

        return view;

    }

    public ArrayList<Picture> buildPicture(){
        ArrayList<Picture> pictures = new ArrayList<>();
        pictures.add(new Picture("https://pm1.narvii.com/6984/9ed41b1a0e7457a59d74dc093b77ee49ebf3d65br1-519-519v2_hq.jpg", "Utaha Kasumigaoka" , "5 dias", "17 Me Gusta"));
        pictures.add(new Picture("https://66.media.tumblr.com/c1deef47dd21775a9143dd275e71c068/tumblr_pkf99yqJv51up5qwko1_1280.jpg", "Miku Nakano" , "2 dias", "8 Me Gusta"));
        pictures.add(new Picture("https://pm1.narvii.com/6983/4e0c53b03f73478d1f1b4229a5b60d8307749c38r1-1080-1080v2_hq.jpg", "Eriri Spencer" , "4 dias", "21 Me Gusta"));
        pictures.add(new Picture("https://roque363.github.io/novel-music/src/assets/img/contenido.png", "roque363" , "1 dias", "7 Me Gusta"));
        return pictures;
    }

    public void showToolbar(String tittle, boolean upButton, View view){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle(tittle);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(upButton);
    }
}
