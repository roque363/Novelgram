package com.roque.novelgram.login.presenter;

public interface LoginPresenter {

    void signIn(String username, String password);
    void loginSuccess();
    void loginError(String error);
}
