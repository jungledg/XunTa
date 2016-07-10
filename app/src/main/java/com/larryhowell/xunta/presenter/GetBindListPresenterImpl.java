package com.larryhowell.xunta.presenter;

import com.larryhowell.xunta.bean.Person;
import com.larryhowell.xunta.common.Config;
import com.larryhowell.xunta.net.OkHttpUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;


public class GetBindListPresenterImpl implements IGetBindListPresenter {
    private IGetBindListView iGetBindListView;

    public GetBindListPresenterImpl(IGetBindListView iGetBindListView) {
        this.iGetBindListView = iGetBindListView;
    }

    @Override
    public void getBindList() {

        Map<String,String> params = new HashMap<>();
        params.put("type","getBindList");
        params.put("id", Config.telephone);
        params.put("operation","get");
        OkHttpUtil.get(params, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                iGetBindListView.OnGetBindListResult(false, call.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    int number = -1;
                    String name, img, tel;

                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                        number = jsonObject2.getInt("id");
                        name = jsonObject2.getString("name");
                        img = jsonObject2.getString("img");
                        tel = jsonObject2.getString("tel");
                        Config.bindList.add(new Person(name, tel, img));
                    }

                    if (number == -1) {
                        iGetBindListView.OnGetBindListResult(false, "非法请求");
                    } else {
                        iGetBindListView.OnGetBindListResult(true, "");
                    }
                } catch (JSONException e) {
                    iGetBindListView.OnGetBindListResult(false, e.getMessage());
                    e.printStackTrace();
                }
            }
        });

//        new Handler().postDelayed(() -> {
//            Config.bindList.clear();
//
//            Config.bindList.add(new Person("哇咔咔", "18168061837", "http://taskmoment.image.alimmdn.com/portrait/8786.jpg"));
//            Config.bindList.add(new Person("炒鸡大傻逼", "18168061837", "http://taskmoment.image.alimmdn.com/portrait/17196.jpg"));
//            Config.bindList.add(new Person("最熟悉的陌生银", "18168061837", "http://taskmoment.image.alimmdn.com/portrait/17197.jpg"));
//            Config.bindList.add(new Person("呵呵", "18168061837", "http://taskmoment.image.alimmdn.com/portrait/12465.jpg"));
//
//            iGetBindListView.OnGetBindListResult(true, "");
//        }, 2000);
    }
}
