package com.larryhowell.xunta.presenter;

/**
 * Created by gzd on 2016/7/10.
 */
public interface IGetLocationPresenter {
    void getLocation(String telephone);

    interface IGetLocationView {
        void onGetLocationResult(Boolean result, String info);
    }
}
