package com.roque.novelgram.post.view;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.roque.novelgram.NovelgramApplication;
import com.roque.novelgram.R;
import com.roque.novelgram.model.Picture;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NewPostActivity extends AppCompatActivity {

    private ImageView imgPhoto;
    private LinearLayout btnImgContent;
    private RelativeLayout imgBackground;
    private Button btnCreatePost;
    private EditText txtTitle;
    private ProgressBar progressBar;

    private Uri mImageUri;

    private static final String TAG = "NewPostActivity";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_CAMERA = 0;
    private String photoPathTemp = "";
    private String photoName = "";

    private NovelgramApplication app;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;

    private UploadTask uploadTask;
    private StorageTask storageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        showToolbar("Crear Nuevo Post", true);

        app = (NovelgramApplication) getApplicationContext();
        // [START get_firestore_instance]
        storageReference = app.getStorageReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        // [END get_firestore_instance]

        imgPhoto = (ImageView)findViewById(R.id.imgPhoto);
        btnImgContent = (LinearLayout)findViewById(R.id.btnImgContent);
        imgBackground = (RelativeLayout)findViewById(R.id.imgBackground);
        btnCreatePost = (Button)findViewById(R.id.btnCreatePost);
        txtTitle = (EditText)findViewById(R.id.txtTitle);
        progressBar = findViewById(R.id.progressBar);

        btnImgContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PackageManager packageManager = getPackageManager();
                    int permissionCheck = packageManager.checkPermission(Manifest.permission.CAMERA, getPackageName());

                    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                        Log.i(TAG, "Tienes permiso para usar la camara.");
                        selectOption();
                    } else {
                        Log.i(TAG, "Camera Permission error!.");
                        String messageError = getString(R.string.message_cameraNotAvailablePermission);
                        Toast.makeText(NewPostActivity.this, messageError, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(NewPostActivity.this,"Camera Permission error", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                // int permissionCheck = ContextCompat.checkSelfPermission(NewPostActivity.this, Manifest.permission.CAMERA);
            }
        });

        btnCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validatePost()){
                    return;
                }
                if (uploadTask != null && uploadTask.isInProgress() ) {
                    Toast.makeText(NewPostActivity.this,"Subiendo foto", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
                //uploadPhoto();
            }
        });
    }

    private void selectOption() {
        final CharSequence[] options = {"Camara","Galeria","Cancelar"};
        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(NewPostActivity.this);
        builder.setTitle("");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (options[i].equals("Camara")) {
                    dialogInterface.dismiss();
                    String message = getString(R.string.opening_camera);
                    Toast.makeText(NewPostActivity.this, message, Toast.LENGTH_SHORT).show();
                    takePicture();

                } else if (options[i].equals("Galeria")) {
                    dialogInterface.dismiss();
                    openFileChooser();

                } else if (options[i].equals("Cancelar")) {
                    dialogInterface.dismiss();

                }
            }
        });
        builder.show();
    }

    private void uploadPhoto() {
        imgPhoto.setDrawingCacheEnabled(true);
        imgPhoto.buildDrawingCache();

        Bitmap bitmap = imgPhoto.getDrawingCache();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        byte[] photoByte = byteArrayOutputStream.toByteArray();

        final StorageReference photoReference = storageReference.child("postImages/" + photoName);

        UploadTask uploadTask = photoReference.putBytes(photoByte);
        uploadTask
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error al subir la foto " + e.toString());
                        e.printStackTrace();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressBar.setProgress((int) progress);
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        photoReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uriPhoto) {
                                String photoURL = uriPhoto.toString();
                                String timeStamp = new SimpleDateFormat("yyMMdd_HH-mm-ss").format(new Date());
                                String title = txtTitle.getText().toString().trim();
                                Log.w(TAG, "URL Photo > " + photoURL);

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setProgress(0);
                                    }
                                },500);
                                Picture picture = new Picture(photoURL, title, timeStamp,"0");
                                firebaseFirestore.collection("pictures").add(picture);

                                Toast.makeText(NewPostActivity.this,"Subida exitosa", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                });
    }

    private boolean validatePost() {
        boolean valid = true;
        String title = txtTitle.getText().toString();
        imgPhoto.setDrawingCacheEnabled(true);
        imgPhoto.buildDrawingCache();
        Bitmap bitmap = imgPhoto.getDrawingCache();

        if (TextUtils.isEmpty(title)) {
            txtTitle.setError("Campo requerido");
            valid = false;
        } else {
            txtTitle.setError(null);
        }

        if (bitmap == null) {
            valid = false;
            Toast.makeText(this,"Ningún archivo seleccionado",Toast.LENGTH_SHORT).show();
        }

        return valid;
    }

    // Get the file extension from de image
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadFile() {
        if (mImageUri != null) {
            final StorageReference fileReference = storageReference.child("postImages/" + System.currentTimeMillis()+"."+getFileExtension(mImageUri));
            final String timeStamp = new SimpleDateFormat("yyMMdd_HH-mm-ss").format(new Date());
            uploadTask = fileReference.putFile(mImageUri);

            uploadTask
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NewPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        }
                    })
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()){
                                throw Objects.requireNonNull(task.getException());
                            }
                            return fileReference.getDownloadUrl();
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(@NonNull Uri downloadUri) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            },500);
                            Toast.makeText(NewPostActivity.this,"Subida exitosa", Toast.LENGTH_SHORT).show();

                            Picture picture = new Picture(downloadUri.toString(),txtTitle.getText().toString().trim(),timeStamp,"0");
                            firebaseFirestore.collection("pictures").add(picture);

                        }
                    });
        } else {
            Toast.makeText(this,"Ningún archivo seleccionado",Toast.LENGTH_SHORT).show();
        }
    }

    private void openFileChooser() {
        Intent pickPhoto = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );
        startActivityForResult(pickPhoto, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String messageLoading = getString(R.string.loading_photo);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 0:
                    Log.d(TAG, "CAMERA OK!! :");
                    hideImageBackground();

                    photoName = photoPathTemp.substring(photoPathTemp.lastIndexOf("/")+1, photoPathTemp.length());
                    Toast.makeText(NewPostActivity.this, messageLoading, Toast.LENGTH_SHORT).show();
                    Picasso.get().load(photoPathTemp).into(imgPhoto);
                    break;
                case 1:
                    mImageUri = data.getData();
                    hideImageBackground();

                    photoName = System.currentTimeMillis()+"."+getFileExtension(mImageUri);
                    Toast.makeText(NewPostActivity.this, messageLoading, Toast.LENGTH_SHORT).show();
                    Picasso.get().load(mImageUri).into(imgPhoto);
                    break;
            }
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
        Objects.requireNonNull(getSupportActionBar()).setTitle(tittle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
    }

}
