package com.larryhowell.xunta.presenter;

public interface IGetUserInfoPresenter {
    void getUserInfo(String telephone);

    interface IGetUserInfoView {
        void onGetUserInfoResult(Boolean result, String info);
    }
}
