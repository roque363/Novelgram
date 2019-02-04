package com.roque.novelgram.post.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.roque.novelgram.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class NewPostActivity extends AppCompatActivity {

    private ImageView imgPhoto;
    private LinearLayout imgNewPost;
    private RelativeLayout imgBackground;

    private static final int REQUEST_CAMERA = 1;
    private String photoPathTemp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        showToolbar("Crear Nuevo Post", true);

        imgPhoto = (ImageView)findViewById(R.id.imgPhoto);
        imgNewPost = (LinearLayout)findViewById(R.id.imgNewPost);
        imgBackground = (RelativeLayout)findViewById(R.id.imgBackground);

        imgNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = ContextCompat.checkSelfPermission(NewPostActivity.this, Manifest.permission.CAMERA);

                String message = getString(R.string.opening_camera);
                Toast.makeText(NewPostActivity.this, message, Toast.LENGTH_SHORT).show();

                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    Log.i("Mensaje", "No se tiene permiso para la camara!.");
                    String messageError = getString(R.string.message_cameraNotAvailablePermission);
                    Toast.makeText(NewPostActivity.this, messageError, Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("Mensaje", "Tienes permiso para usar la camara.");
                    takePicture();
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if  (requestCode == REQUEST_CAMERA && resultCode == NewPostActivity.this.RESULT_OK) {

            Log.d("HomeFragment", "CAMERA OK!! :");
            String messageLoading = getString(R.string.loading_photo);
            Toast.makeText(NewPostActivity.this, messageLoading, Toast.LENGTH_SHORT).show();

            hideImageBackground();
            Picasso.get().load(photoPathTemp).into(imgPhoto);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyMMdd_HH-mm-ss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = NewPostActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File photo = File.createTempFile(imageFileName,".jpg",storageDir);
        photoPathTemp = "file:" + photo.getAbsolutePath();

        return photo;
    }

    private void takePicture() {
        Intent intentTakePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intentTakePicture.resolveActivity(NewPostActivity.this.getPackageManager()) != null){
            File photoFile = null;

            try {
                photoFile = createImageFile();

                if (photoFile != null){
                    String packageName = NewPostActivity.this.getApplicationContext().getPackageName();

                    //Uri photoUri = FileProvider.getUriForFile(getActivity(), packageName, photoFile);
                    Uri photoUri = Uri.fromFile(photoFile);

                    intentTakePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intentTakePicture, REQUEST_CAMERA);
                }
            } catch (Exception e){
                e.printStackTrace();
                //Log.d("Error: ",e.getMessage());
            }
        } else {
            String messageError = getString(R.string.message_cameraNotAvailable);
            Toast.makeText(NewPostActivity.this, messageError, Toast.LENGTH_SHORT).show();
        }
    }

    public void showImageBackground() {
        imgBackground.setVisibility(View.VISIBLE);
    }

    public void hideImageBackground() {
        imgBackground.setVisibility(View.GONE);
    }

    public void showToolbar(String tittle, boolean upButton){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(tittle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
    }
}
