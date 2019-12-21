package com.roque.novelgram.login.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.muddzdev.styleabletoast.StyleableToast;
import com.roque.novelgram.R;
import com.roque.novelgram.login.presenter.LoginPresenter;
import com.roque.novelgram.login.presenter.LoginPresenterImp;
import com.roque.novelgram.util.Constants;
import com.roque.novelgram.view.ContainerActivity;

import java.security.MessageDigest;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class LoginActivity extends AppCompatActivity implements LoginView, View.OnClickListener {

    private TextInputLayout username, password;
    private Button btnLogin, btnLoginFacebook;
    private LinearLayout progressBackground;
    private ProgressBar progressBarLogin;

    private LoginPresenter presenter;

    // [START declare_auth]
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    // [END declare_auth]
    private static final String TAG = "LoginActivity";
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.usernameLogin);
        password = findViewById(R.id.passwordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnLoginFacebook = findViewById(R.id.btnLoginFacebook);
        progressBackground = findViewById(R.id.progressBackground);
        progressBarLogin = findViewById(R.id.progressBarLogin);

        hideProgressBar();
        username.setTypeface(ResourcesCompat.getFont(this,R.font.quicksand_medium));
        password.setTypeface(ResourcesCompat.getFont(this,R.font.quicksand_medium));

        btnLogin.setOnClickListener(this);
        btnLoginFacebook.setOnClickListener(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        firebaseAuth = FirebaseAuth.getInstance(); // Initialize Firebase Auth

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Check if user is signed in
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    Log.d(TAG, "Id  --> " + currentUser.getUid());
                    Log.d(TAG, "User  --> " + currentUser.getEmail());

                    currentUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        @Override
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if(task.isSuccessful()) {
                                String idToken = task.getResult().getToken();
                                Log.d(TAG, "Token  --> " + idToken);
                            } else {
                                Log.w(TAG, "Error  --> " + task.getException());
                            }
                        }
                    });

                    SharedPreferences preferences = getSharedPreferences(Constants.NOVEL_GRAM_APP, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(Constants.USER_ID, currentUser.getUid());
                    editor.putString(Constants.USER_NAME, currentUser.getDisplayName());
                    editor.putString(Constants.USER_EMAIL, currentUser.getEmail());
                    editor.apply();

                    goHome();
                } else {
                    Log.d(TAG, "NO User");
                }
            }
        };

        presenter = new LoginPresenterImp(this);

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "Facebook Login Success, Token: " + loginResult.getAccessToken().getApplicationId());
                Log.d(TAG, "Facebook: Success: " + loginResult);
                signInFacebookFireBase(loginResult.getAccessToken());
            }
            @Override
            public void onCancel() {
                enableInputs();
                hideProgressBar();
                Log.d(TAG, "Facebook Login Cancel");
            }
            @Override
            public void onError(FacebookException error) {
                enableInputs();
                hideProgressBar();
                StyleableToast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT, R.style.ToastError).show();
                Log.w(TAG, "Facebook Login Error: " + error.toString());
                error.printStackTrace();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin: {
                String txtUsername = username.getEditText().getText().toString();
                String txtPassword = password.getEditText().getText().toString();

                if(txtUsername.equals("") || txtPassword.equals("")) {
                    StyleableToast.makeText(LoginActivity.this, getString(R.string.login_blank), Toast.LENGTH_SHORT, R.style.ToastInfo).show();
                } else {
                    StyleableToast.makeText(this, getString(R.string.estableciendo_conexion), Toast.LENGTH_LONG, R.style.ToastSync).show();
                    singIn(txtUsername, txtPassword);
                }
                break;
            }
            case R.id.btnLoginFacebook: {
                disableImputs();
                showProgressBar();
                StyleableToast.makeText(this, getString(R.string.estableciendo_conexion), Toast.LENGTH_LONG, R.style.ToastSync).show();
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
                break;
            }
        }

    }

    private void signInFacebookFireBase(AccessToken accessToken) {
        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken.getToken());

        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user= task.getResult().getUser();

                    SharedPreferences preferences = getSharedPreferences(Constants.NOVEL_GRAM_APP, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(Constants.USER_ID, user.getUid());
                    editor.putString(Constants.USER_NAME, user.getDisplayName());
                    editor.putString(Constants.USER_EMAIL, user.getEmail());
                    editor.putString(Constants.USER_PHOTO, user.getPhotoUrl().toString());
                    editor.apply();

                    StyleableToast.makeText(LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT, R.style.ToastSuccess).show();
                    goHome();
                } else {
                    enableInputs();
                    hideProgressBar();
                    StyleableToast.makeText(LoginActivity.this, getString(R.string.login_error_face), Toast.LENGTH_SHORT, R.style.ToastError).show();
                }
            }
        });
    }

    private void singIn(String txtUsername, String txtPassword) {
        presenter.signIn(txtUsername, txtPassword, this, firebaseAuth);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    public void loginError(String error) {
        StyleableToast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT, R.style.ToastError).show();
    }

    @Override
    public void enableInputs() {
        username.setEnabled(true);
        password.setEnabled(true);
        btnLogin.setEnabled(true);
    }

    @Override
    public void disableImputs() {
        username.setEnabled(false);
        password.setEnabled(false);
        btnLogin.setEnabled(false);
    }

    @Override
    public void showProgressBar() {
        progressBackground.setVisibility(View.VISIBLE);
        progressBarLogin.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBackground.setVisibility(View.GONE);
        progressBarLogin.setVisibility(View.GONE);
    }

    public void goCreateAccount(View view){
        goCreateAccount();
    }

    @Override
    public void goCreateAccount() {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }

    @Override
    public void goHome() {
        Intent intent = new Intent(this, ContainerActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

}
