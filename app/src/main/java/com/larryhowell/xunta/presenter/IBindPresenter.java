package com.larryhowell.xunta.presenter;

/**
 * Created by Leunghowell on 16/6/19.
 */
public interface IBindPresenter {
    void bind(String telephone);

    interface IBindView {
        void onBindResult(Boolean result, String info);
    }
}
