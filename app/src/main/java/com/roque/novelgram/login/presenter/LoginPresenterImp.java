package com.roque.novelgram.login.presenter;

import android.view.View;

import com.roque.novelgram.login.interactor.LoginInteractor;
import com.roque.novelgram.login.interactor.LoginInteractorImp;
import com.roque.novelgram.login.view.LoginView;

public class LoginPresenterImp implements LoginPresenter {

    private LoginView loginView;
    private LoginInteractor interactor;

    public LoginPresenterImp(LoginView loginView) {
        this.loginView = loginView;
        interactor = new LoginInteractorImp(this);
    }

    @Override
    public void signIn(String username, String password) {
        loginView.disableImputs();
        loginView.showProgressBar();
        interactor.signIn(username, password);
    }

    @Override
    public void loginSuccess() {
        loginView.goHome();
        loginView.hideProgressBar();
    }

    @Override
    public void loginError(String error) {
        loginView.enableInputs();
        loginView.hideProgressBar();
        loginView.loginError(error);
    }
}
