package com.roque.novelgram.login.repository;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roque.novelgram.login.presenter.LoginPresenter;

public class LoginRepositoryImp implements LoginRepository {

    LoginPresenter presenter;
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
                    SharedPreferences preferences = activity.getSharedPreferences("USER", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("email", user.getEmail());
                    editor.commit();
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
