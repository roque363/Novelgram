package com.roque.novelgram.login.view;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.roque.novelgram.R;
import com.roque.novelgram.login.presenter.LoginPresenter;
import com.roque.novelgram.login.presenter.LoginPresenterImp;
import com.roque.novelgram.view.ContainerActivity;
import com.roque.novelgram.view.CreateAccountActivity;

public class LoginActivity extends AppCompatActivity implements LoginView {

    private TextInputEditText username, password;
    private Button btnLogin;
    private ProgressBar progressBarLogin;
    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (TextInputEditText)findViewById(R.id.usernameLogin);
        password = (TextInputEditText)findViewById(R.id.passwordLogin);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        progressBarLogin = (ProgressBar)findViewById(R.id.progressBarLogin);

        hideProgressBar();

        presenter = new LoginPresenterImp(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtUsername = username.getText().toString();
                String txtPassword = password.getText().toString();

                if(txtUsername.equals("") || txtPassword.equals("")) {
                    Toast.makeText(LoginActivity.this, getString(R.string.login_blank), Toast.LENGTH_SHORT).show();
                } else {
                    presenter.signIn(txtUsername, txtPassword);
                }
            }
        });
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
        progressBarLogin.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBarLogin.setVisibility(View.GONE);
    }

    @Override
    public void loginError(String error) {
        Toast.makeText(this, getString(R.string.login_error) + error, Toast.LENGTH_SHORT).show();
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

}
