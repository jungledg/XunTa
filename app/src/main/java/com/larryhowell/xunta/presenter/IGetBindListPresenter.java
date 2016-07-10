package com.larryhowell.xunta.presenter;

/**
 * Created by Leunghowell on 16/6/19.
 */
public interface IGetBindListPresenter {
    void getBindList();

    interface IGetBindListView {
        void OnGetBindListResult(Boolean result, String info);
    }
}
