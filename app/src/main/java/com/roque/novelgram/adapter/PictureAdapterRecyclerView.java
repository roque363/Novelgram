package com.roque.novelgram.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.roque.novelgram.R;
import com.roque.novelgram.model.Picture;
import com.roque.novelgram.post.view.PictureDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

public class PictureAdapterRecyclerView extends RecyclerView.Adapter<PictureAdapterRecyclerView.PictureViewHolder> {

    private ArrayList<Picture> pictures;
    private int ressource;
    private Activity activity;

    public class PictureViewHolder extends RecyclerView.ViewHolder {

        private ImageView pictureCard;
        private TextView nameCard;
        private TextView timeCard;
        private TextView likeNumberCard;

        public PictureViewHolder(View itemView) {
            super(itemView);

            pictureCard = (ImageView) itemView.findViewById(R.id.picture_card);
            nameCard = (TextView) itemView.findViewById(R.id.name_card);
            timeCard = (TextView) itemView.findViewById(R.id.time_card);
            likeNumberCard = (TextView) itemView.findViewById(R.id.like_number_card);
        }
    }

    public PictureAdapterRecyclerView(ArrayList<Picture> pictures, int ressource, Activity activity) {
        this.pictures = pictures;
        this.ressource = ressource;
        this.activity = activity;
    }

    @NonNull
    @Override
    public PictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(ressource, parent, false);
        return new PictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PictureViewHolder holder, int position) {
        final Picture picture = pictures.get(position);
        holder.nameCard.setText(picture.getName());
        holder.timeCard.setText(picture.getTime());
        holder.likeNumberCard.setText(picture.getLike_number());

        Picasso.get().load(picture.getPicture()).placeholder(R.drawable.image_default).into(holder.pictureCard);

        holder.pictureCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String transitionNamePicture = activity.getString(R.string.transitionname_picture);

                Intent intent = new Intent(activity, PictureDetailActivity.class);
                intent.putExtra("strKey", picture.getKey());
                intent.putExtra("strPicture", picture.getPicture());
                intent.putExtra("strTitle", picture.getName());
                intent.putExtra("strTime", picture.getTime());
                intent.putExtra("strLikeNumber", picture.getLike_number());
                intent.putExtra("strDescription", picture.getDescription());
                intent.putExtra("strExtra", picture.getExtra());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    //Explode explode = new Explode();
                    //explode.setDuration(1000);
                    activity.getWindow().setExitTransition(new Fade());
                    activity.startActivity(intent, ActivityOptionsCompat
                            .makeSceneTransitionAnimation(activity, view, transitionNamePicture)
                            .toBundle());
                } else {
                    activity.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

}
