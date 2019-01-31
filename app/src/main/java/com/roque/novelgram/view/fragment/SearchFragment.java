package com.roque.novelgram.view.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import com.roque.novelgram.R;
import com.roque.novelgram.adapter.PictureAdapterRecyclerView;
import com.roque.novelgram.model.Picture;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private int numberOfColumns = 2;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        RecyclerView picturesRecycler = (RecyclerView)view.findViewById(R.id.search_fragment_recycler);

        GridLayoutManager gridLayout = new GridLayoutManager(getContext(), numberOfColumns);

        picturesRecycler.setLayoutManager(gridLayout);

        PictureAdapterRecyclerView pictureAdapterRecyclerView =
                new PictureAdapterRecyclerView(buildPicture(), R.layout.cardview_picture, getActivity());
        picturesRecycler.setAdapter(pictureAdapterRecyclerView);

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

}
