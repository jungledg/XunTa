package com.larryhowell.xunta.presenter;

import com.larryhowell.xunta.common.Config;
import com.larryhowell.xunta.net.OkHttpUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Leunghowell on 16/6/12.
 */
public class GetUserInfoPresenterImpl implements IGetUserInfoPresenter {
    public IGetUserInfoView iGetUserInfoView;

    public GetUserInfoPresenterImpl(IGetUserInfoView iGetUserInfoView) {
        this.iGetUserInfoView = iGetUserInfoView;
    }

    @Override
    public void getUserInfo(String telephone) {
        if (telephone == null || "".equals(telephone)) {
            iGetUserInfoView.onGetUserInfoResult(true, "");     //false?
            return;
        }

        Map<String,String> params = new HashMap<>();
        params.put("type","getUserInfo");
        params.put("id", telephone);
        params.put("operation","get");

        OkHttpUtil.get(params, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                iGetUserInfoView.onGetUserInfoResult(false, call.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    Config.nickname = jsonObject.getString("name");
                    Config.portrait = jsonObject.getString("img");

                    iGetUserInfoView.onGetUserInfoResult(true, "");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

//        new Handler().postDelayed(() -> {
//            Config.nickname = "Leung Howell";
//            Config.portrait = "http://taskmoment.image.alimmdn.com/portrait/12465.jpg";
//
//            iGetUserInfoView.onGetUserInfoResult(true, "");
//        }, 2000);
    }
}


