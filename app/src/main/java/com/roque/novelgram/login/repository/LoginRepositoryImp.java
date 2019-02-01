package com.roque.novelgram.login.repository;

import com.roque.novelgram.login.presenter.LoginPresenter;

public class LoginRepositoryImp implements LoginRepository {

    LoginPresenter presenter;

    public LoginRepositoryImp(LoginPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void signIn(String username, String password) {
        boolean success = true;
        if(success){
            presenter.loginSuccess();
        } else {
            presenter.loginError("Ocurrió un Error");
        }
    }
}
