package com.roque.novelgram.login.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.muddzdev.styleabletoast.StyleableToast;
import com.roque.novelgram.R;
import com.roque.novelgram.util.Constants;

import org.w3c.dom.Text;

import java.util.Objects;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {

    // [START declare_auth]
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    // [END declare_auth]

    private Button btnCreateAccount;
    private TextInputLayout txtEmail, txtNombre, txtPassword, txtConfirmPassword;
    private ProgressBar progressBarCreateAccount;
    private String TAG = "CreateAccountActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        showToolbar(getResources().getString(R.string.toolbar_title_createaccount), true);
        // Progress Bar
        progressBarCreateAccount = findViewById(R.id.progressBarCreateAccount);
        hideProgressBar();
        // Views
        txtEmail = findViewById(R.id.emailCreate);
        txtNombre = findViewById(R.id.nameCreate);
        txtPassword = findViewById(R.id.passwordCreate);
        txtConfirmPassword = findViewById(R.id.confirmPasswordCreate);
        // Buttons
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        btnCreateAccount.setOnClickListener(this);

        txtEmail.setTypeface(ResourcesCompat.getFont(this,R.font.quicksand_medium));
        txtNombre.setTypeface(ResourcesCompat.getFont(this,R.font.quicksand_medium));
        txtPassword.setTypeface(ResourcesCompat.getFont(this,R.font.quicksand_medium));
        txtConfirmPassword.setTypeface(ResourcesCompat.getFont(this,R.font.quicksand_medium));

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Check if user is signed in
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    Log.i(TAG, "Usuario" + currentUser.getEmail());
                } else {
                    Log.i(TAG, "NO Usuario");
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCreateAccount: {
                createAccount();
                break;
            }
        }
    }

    private void createAccount() {
        String email = txtEmail.getEditText().getText().toString();
        String password = txtPassword.getEditText().getText().toString();
        final String nombre = txtNombre.getEditText().getText().toString();

        Log.d(TAG, "createAccount: " + email);
        // Check the forms
        if(!validateForm()){
            return;
        }

        StyleableToast.makeText(this, getString(R.string.estableciendo_conexion), Toast.LENGTH_LONG, R.style.ToastSync).show();
        showProgressBar();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success");
                    updateUser(nombre);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    StyleableToast.makeText(CreateAccountActivity.this, getString(R.string.error_crear_cuenta), Toast.LENGTH_SHORT, R.style.ToastError).show();
                }
                hideProgressBar();
            }
        });
    }

    private void updateUser(String nombre) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(nombre)
                .build();

        try {
            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User profile updated.");
                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        SharedPreferences preferences = getSharedPreferences(Constants.NOVEL_GRAM_APP, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(Constants.USER_ID, user.getUid());
                        editor.putString(Constants.USER_NAME, user.getDisplayName());
                        editor.putString(Constants.USER_EMAIL, user.getEmail());
                        editor.apply();

                        StyleableToast.makeText(
                                CreateAccountActivity.this, getString(R.string.cuenta_creada), Toast.LENGTH_SHORT, R.style.ToastSuccess).show();
                    }
                }
            });
        } catch (Throwable t) {
            Log.e(TAG, "onThrowable: " + t.getMessage(), t);
        }
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

    private boolean validateForm() {
        boolean valid = true;

        String email = txtEmail.getEditText().getText().toString();
        if (TextUtils.isEmpty(email)) {
            txtEmail.setError("Campo requerido");
            valid = false;
        } else if (!validarEmail(email)) {
            txtEmail.setError("Email no válido");
            valid = false;
        } else {
            txtEmail.setError(null);
        }

        String nombre = txtNombre.getEditText().getText().toString();
        if (TextUtils.isEmpty((nombre))) {
            txtNombre.setError("Campo requerido");
            valid = false;
        } else if (nombre.matches("[0-9]")) {
            txtNombre.setError("El nombre no puede tener numeros");
            valid = false;
        } else {
            txtNombre.setError(null);
        }

        String password = txtPassword.getEditText().getText().toString();
        if (TextUtils.isEmpty(password)) {
            txtPassword.setError("Campo requerido");
            valid = false;
        } else {
            txtPassword.setError(null);
        }

        String confirmPasswpord = txtConfirmPassword.getEditText().getText().toString();
        if (TextUtils.isEmpty(password)) {
            txtConfirmPassword.setError("Campo requerido");
            valid = false;
        } else if (!confirmPasswpord.equals(password)) {
            txtConfirmPassword.setError("Las contraseñas no coinciden");
            valid = false;
        } else {
            txtConfirmPassword.setError(null);
        }

        return valid;
    }

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public void showToolbar(String tittle, boolean upButton) {
        Toolbar toolbar = findViewById(R.id.toolbarCreate);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(tittle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
    }

    public void hideProgressBar() {
        progressBarCreateAccount.setVisibility(View.GONE);
    }

    public void showProgressBar() {
        progressBarCreateAccount.setVisibility(View.VISIBLE);
    }

}
