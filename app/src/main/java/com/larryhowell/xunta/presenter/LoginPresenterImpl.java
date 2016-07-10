package com.larryhowell.xunta.presenter;

import com.larryhowell.xunta.common.Config;
import com.larryhowell.xunta.net.OkHttpUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class LoginPresenterImpl implements ILoginPresenter {
    private ILoginView iLoginView;

    public LoginPresenterImpl(ILoginView iLoginView) {
        this.iLoginView = iLoginView;
    }

    @Override
    public void login(String telephone) {
        if (telephone == null || "".equals(telephone)) {
            iLoginView.onLoginResult(true, "");     //false?
            return;
        }

        Map<String,String> params = new HashMap<>();
        params.put("type","login");
        params.put("id", telephone);
        params.put("operation","get");

        OkHttpUtil.get(params, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                iLoginView.onLoginResult(false, call.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    Config.nickname = jsonObject.getString("name");
                    Config.portrait = jsonObject.getString("img");

                    iLoginView.onLoginResult(true, "");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });




//        new Handler().postDelayed(() -> {
//            Config.nickname = "Leung Howell";
//            Config.portrait = "http://taskmoment.image.alimmdn.com/portrait/12465.jpg";
//            Config.telephone = telephone;
//
//            SharedPreferences.Editor editor = App.sp.edit();
//            editor.putString(Constants.SP_KEY_TELEPHONE, Config.telephone);
//            editor.putString(Constants.SP_KEY_NICKNAME, Config.nickname);
//            editor.putString(Constants.SP_KEY_PORTRAIT, Config.portrait);
//            editor.apply();
//
//            iLoginView.onLoginResult(true, "");
//        }, 2000);
    }
}
