package com.roque.novelgram.login.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roque.novelgram.R;
import com.roque.novelgram.login.presenter.LoginPresenter;
import com.roque.novelgram.login.presenter.LoginPresenterImp;
import com.roque.novelgram.view.ContainerActivity;

import java.security.MessageDigest;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements LoginView {

    private TextInputEditText username, password;
    private Button btnLogin;
    private LoginButton btnLoginFacebook;
    private ProgressBar progressBarLogin;
    private LoginPresenter presenter;

    // [START declare_auth]
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    // [END declare_auth]
    private String TAG = "LoginRepositoryImp";
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (TextInputEditText)findViewById(R.id.usernameLogin);
        password = (TextInputEditText)findViewById(R.id.passwordLogin);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLoginFacebook = (LoginButton) findViewById(R.id.btnLoginFacebook);
        progressBarLogin = (ProgressBar)findViewById(R.id.progressBarLogin);

        hideProgressBar();

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        // [START initialize_auth]
        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Check if user is signed in
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    Log.w(TAG, "Usuario Logeado" + currentUser.getEmail());
                    Toast.makeText(LoginActivity.this, "Usuario Logeado: " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
                    goHome();
                } else {
                    Log.w(TAG, "Usuario NO Logeado");
                    Toast.makeText(LoginActivity.this, "Usuario NO Logeado", Toast.LENGTH_SHORT).show();
                }
            }
        };

        presenter = new LoginPresenterImp(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtUsername = username.getText().toString();
                String txtPassword = password.getText().toString();

                if(txtUsername.equals("") || txtPassword.equals("")) {
                    Toast.makeText(LoginActivity.this, getString(R.string.login_blank), Toast.LENGTH_SHORT).show();
                } else {
                    singIn(txtUsername, txtPassword);
                }
            }
        });

        btnLoginFacebook.setReadPermissions(Arrays.asList("email"));
        btnLoginFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.w(TAG, "Facebook Login Success, Token: " + loginResult.getAccessToken().getApplicationId());
                Log.w(TAG, "Facebook: Success: " + loginResult);
                signInFacebookFireBase(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.w(TAG, "Facebook Login Cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.w(TAG, "Facebook Login Error: " + error.toString());
                error.printStackTrace();
            }
        });
    }

    private void signInFacebookFireBase(AccessToken accessToken) {
        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken.getToken());

        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user= task.getResult().getUser();
                    SharedPreferences preferences = getSharedPreferences("USER", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("email", user.getEmail());
                    editor.commit();

                    goHome();
                    Toast.makeText(LoginActivity.this, "Login Facebook Exitoso", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Login Facebook NO Exitoso", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void singIn(String txtUsername, String txtPassword) {
        presenter.signIn(txtUsername, txtPassword, this, firebaseAuth);
    }

    // [START on_start_check_user]
    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        firebaseAuth.addAuthStateListener(authStateListener);
    }
    // [END on_start_check_user]

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
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
    public void showProgressBar() { progressBarLogin.setVisibility(View.VISIBLE); }

    @Override
    public void hideProgressBar() { progressBarLogin.setVisibility(View.GONE); }

    @Override
    public void loginError(String error) {
        Toast.makeText(this, getString(R.string.login_error) + error, Toast.LENGTH_SHORT).show();
    }

    public void goCreateAccount(View view){ goCreateAccount(); }

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
