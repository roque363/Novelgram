package com.roque.novelgram.login.view;

import android.view.View;

public interface LoginView {

    void enableInputs();
    void disableImputs();

    void showProgressBar();
    void hideProgressBar();

    void loginError(String error);

    void goCreateAccount();
    void goHome();
}
