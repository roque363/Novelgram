package com.roque.novelgram.login.interactor;

import com.roque.novelgram.login.presenter.LoginPresenter;
import com.roque.novelgram.login.repository.LoginRepository;
import com.roque.novelgram.login.repository.LoginRepositoryImp;

public class LoginInteractorImp implements  LoginInteractor {

    private LoginPresenter presenter;
    private LoginRepository repository;

    public LoginInteractorImp(LoginPresenter presenter) {
        this.presenter = presenter;
        repository = new LoginRepositoryImp(presenter);
    }

    @Override
    public void signIn(String username, String password) {
        repository.signIn(username, password);
    }
}
