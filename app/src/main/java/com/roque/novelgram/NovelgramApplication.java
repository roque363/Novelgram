package com.roque.novelgram;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.roque.novelgram.login.view.LoginActivity;

import java.security.MessageDigest;

import androidx.annotation.NonNull;

public class NovelgramApplication extends Application {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseStorage firebaseStorage;
    private static final String TAG = "NovelgramApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        // System.out.print("KeyHashe: " + KeyHashes());

        FacebookSdk.sdkInitialize(getApplicationContext());
        Log.w(TAG, "Usuario No logeado");

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    Log.w(TAG, "Usuario logeado" + firebaseUser);
                } else {
                    Log.w(TAG, "Usuario No logeado");
                }
            }
        };

        firebaseStorage = FirebaseStorage.getInstance();
    }

    public StorageReference getStorageReference() {
        return firebaseStorage.getReference();
    }

    public String KeyHashes() {
        PackageInfo info;
        String KeyHashes = null;
        try {
            info = getPackageManager().getPackageInfo("com.roque.novelgram", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                KeyHashes = new String(Base64.encode(md.digest(), 0));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return KeyHashes;
    }

}
