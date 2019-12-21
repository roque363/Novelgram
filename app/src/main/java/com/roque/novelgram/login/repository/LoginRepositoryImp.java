package com.roque.novelgram.login.repository;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roque.novelgram.login.presenter.LoginPresenter;
import com.roque.novelgram.util.Constants;

import androidx.annotation.NonNull;

public class LoginRepositoryImp implements LoginRepository {

    private LoginPresenter presenter;
    private String TAG = "LoginRepositoryImp";

    public LoginRepositoryImp(LoginPresenter presenter) { this.presenter = presenter; }

    @Override
    public void signIn(String username, String password, final Activity activity, FirebaseAuth firebaseAuth) {

        firebaseAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user= task.getResult().getUser();
                    Log.d(TAG, user.getUid());
                    Log.d(TAG, user.getEmail());

                    SharedPreferences preferences = activity.getSharedPreferences(Constants.NOVEL_GRAM_APP, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(Constants.USER_ID, user.getUid());
                    editor.putString(Constants.USER_NAME, user.getDisplayName());
                    editor.putString(Constants.USER_EMAIL, user.getEmail());
                    editor.apply();

                    Log.d(TAG, "signInWithEmail: Success");
                    presenter.loginSuccess();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail: Failure", task.getException());
                    presenter.loginError("La autenticación falló");
                }
            }
        });

    }
}
