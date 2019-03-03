package com.roque.novelgram.login.view;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.widget.Toolbar;
import android.support.v7.widget.Toolbar;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roque.novelgram.R;

import java.util.Objects;
import java.util.regex.Pattern;

public class CreateAccountActivity extends AppCompatActivity {

    // [START declare_auth]
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    // [END declare_auth]

    private Button btnCreateAccount;
    private EditText txtEmail, txtPassword, txtConfirmPassword, txtNombre;
    private ProgressBar progressBarCreateAccount;
    private String TAG = "CreateAccountActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        showToolbar(getResources().getString(R.string.toolbar_title_createaccount), true);
        // Progress Bar
        progressBarCreateAccount = (ProgressBar)findViewById(R.id.progressBarCreateAccount);
        hideProgressBar();

        // Views
        txtEmail = (EditText)findViewById(R.id.emailCreate);
        txtPassword = (EditText)findViewById(R.id.passwordCreate);
        txtConfirmPassword = (EditText)findViewById(R.id.confirmPasswordCreate);
        txtNombre = (EditText)findViewById(R.id.nameCreate);

        // Buttons
        btnCreateAccount = (Button)findViewById(R.id.btnCreateAccount);

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
                } else {
                    Log.w(TAG, "Usuario NO Logeado");
                }
            }
        };

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });

    }

    private void createAccount() {

        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        Log.d(TAG, "createAccount: " + email);
        // Check the forms
        if(!validateForm()){
            return;
        }

        showProgressBar();

        // [START create_user_with_email]
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success");
                    Toast.makeText(CreateAccountActivity.this, "Cuenta Creada Exitosamente", Toast.LENGTH_SHORT).show();
                    } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(CreateAccountActivity.this, "Ocurrió un Error al crear la cuenta", Toast.LENGTH_SHORT).show();
                    }
                    hideProgressBar();
                }
        });
        // [END create_user_with_email]
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

    private boolean validateForm() {
        boolean valid = true;

        String email = txtEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            txtEmail.setError("Campo requerido");
            valid = false;
        } else if (!validarEmail(email)) {
            txtEmail.setError("Email no válido");
            valid = false;
        } else {
            txtEmail.setError(null);
        }

        String nombre = txtNombre.getText().toString();
        if (nombre.matches("[0-9]")) {
            txtNombre.setError("El nombre no puede tener numeros");
            valid = false;
        } else {
            txtNombre.setError(null);
        }

        String password = txtPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            txtPassword.setError("Campo requerido");
            valid = false;
        } else {
            txtPassword.setError(null);
        }

        String confirmPasswpord = txtConfirmPassword.getText().toString();
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCreate);
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
