package com.roque.novelgram.post.view;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.roque.novelgram.NovelgramApplication;
import com.roque.novelgram.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PictureDetailActivity extends AppCompatActivity {

    private ImageView imageHeader;
    private TextView titlePictureDetail, likeNumberDetail, timeCardDetail, contentDescription, contentPresentation;
    private Button btnEliminarPicture;

    private NovelgramApplication app;
    private static final String TAG = "PictureDetailActivity";

    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firebaseFirestore;

    private String key, photo, title, time, like_number, description, extra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_detail);
        showToolbar("", true);

        app = (NovelgramApplication) getApplicationContext();
        storageReference = app.getStorageReference();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        imageHeader = findViewById(R.id.imageHeader);
        titlePictureDetail = findViewById(R.id.titlePictureDetail);
        likeNumberDetail = findViewById(R.id.likeNumberDetail);
        timeCardDetail = findViewById(R.id.timeCardDetail);
        contentDescription = findViewById(R.id.contentImageDescription);
        contentPresentation = findViewById(R.id.contentImagePresentation);

        btnEliminarPicture = findViewById(R.id.btnEliminarPicture);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setEnterTransition(new Fade());
        }
        
        showData();

        btnEliminarPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePicture();
            }
        });
    }

    private void deletePicture() {
        // Create a reference to the file to delete
        final StorageReference desertRef = firebaseStorage.getReferenceFromUrl(photo);
        Log.d(TAG, "Picture: "+photo);

        firebaseFirestore.collection("pictures").document(key)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        // Delete the file
                        desertRef.delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // File deleted successfully
                                        Toast.makeText(PictureDetailActivity.this, "Imagen eliminada", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Uh-oh, an error occurred!
                                        Log.w(TAG, "Error deleting the file");
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    private void showData() {
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            key = extras.getString("strKey");
            photo = extras.getString("strPicture");
            title = extras.getString("strTitle");
            time = extras.getString("strTime");
            like_number = extras.getString("strLikeNumber");
            description = extras.getString("strDescription");
            extra = extras.getString("strExtra");
            Log.d("PictureDetailActivity", "Picture: " + key);
        } else {
            Toast.makeText(PictureDetailActivity.this, "Ocurrió un error al traer la foto", Toast.LENGTH_SHORT).show();
        }

        Picasso.get().load(photo).placeholder(R.drawable.image_default).into(imageHeader);
        titlePictureDetail.setText(title);
        timeCardDetail.setText(time);
        likeNumberDetail.setText(like_number);
        contentDescription.setText(description);
        contentPresentation.setText(extra);
    }

    public void showToolbar(String tittle, boolean upButton){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(tittle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);

        CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
    }
}
