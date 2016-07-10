package com.larryhowell.xunta.presenter;

import com.larryhowell.xunta.common.Config;
import com.larryhowell.xunta.net.OkHttpUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class BindPresenterImpl implements IBindPresenter {
    private IBindView iBindView;

    public BindPresenterImpl(IBindView iBindView) {
        this.iBindView = iBindView;
    }

    @Override
    public void bind(String telephone) {

        Map<String,String> params = new HashMap<>();
        params.put("type","bind");
        params.put("id", Config.telephone);
        params.put("targetID", telephone);
        params.put("operation","get");

        OkHttpUtil.get(params, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                iBindView.onBindResult(false, call.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getBoolean("result")) {
                        iBindView.onBindResult(true, "");
                    } else {
                        iBindView.onBindResult(false, "");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

//        new Handler().postDelayed(() -> {
//            //iBindView.onBindResult(false, "不存在该用户");
//            iBindView.onBindResult(true, "");
//        }, 2000);
    }
}
