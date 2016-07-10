package com.larryhowell.xunta.presenter;

/**
 * Created by Leunghowell on 16/6/19.
 */
public interface ILoginPresenter {
    void login(String telephone);

    interface ILoginView {
        void onLoginResult(Boolean result, String info);
    }
}
