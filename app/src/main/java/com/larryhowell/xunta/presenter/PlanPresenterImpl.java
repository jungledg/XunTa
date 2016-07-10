package com.larryhowell.xunta.presenter;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.larryhowell.xunta.bean.Plan;
import com.larryhowell.xunta.common.Config;
import com.larryhowell.xunta.net.OkHttpUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by gzd on 2016/7/10.
 */
public class PlanPresenterImpl implements IPlanPresenter {
    private IPlanView iPlanView;

    public PlanPresenterImpl(IPlanView iPlanView) {
        this.iPlanView = iPlanView;
    }

    @Override
    public void makePlan(Plan plan) {
//        new Handler().postDelayed(() -> {
//            iPlanView.onMakePlanResult(true, "");
//        }, 2000);

        if(plan == null){
            iPlanView.onMakePlanResult(false,"计划错误");
            return;
        }

        Map<String,String> params = new HashMap<>();

        params.put("type","makePlan");
        params.put("id", Config.telephone);
        params.put("startTime",plan.getStartTime());                //开始时间戳
        params.put("desc",plan.getDesc());                          //备注
        params.put("grade", String.valueOf(plan.getGrade()));       //等级
        params.put("arrival",plan.getArrival());                    //结束时间戳
        params.put("departureAddress",plan.getDeparture().name);                           //起始地名
        params.put("departureLongitude", String.valueOf(plan.getDeparture().location.longitude));  //起始地点经度
        params.put("departureLatitude", String.valueOf(plan.getDeparture().location.latitude));  //起始地点纬度
        params.put("terminalAddress",plan.getTerminal().name);                             //终点地名
        params.put("terminalLongitude", String.valueOf(plan.getTerminal().location.longitude));   //终点经度
        params.put("terminalLatitude", String.valueOf(plan.getTerminal().location.latitude));   //终点经度
        params.put("operation","get");

        OkHttpUtil.get(params, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                iPlanView.onMakePlanResult(false, call.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                if (response.equals("true")) {
                    iPlanView.onMakePlanResult(true, "");
                } else {
                    iPlanView.onMakePlanResult(false, "错误");
                }
            }
        });

    }

    @Override
    public void getPlan() {
//        new Handler().postDelayed(() -> {
//            Config.plan = null;
//            iPlanView.onGetPlanResult(true, "");
//        }, 2000);

        Map<String,String> params = new HashMap<>();
        params.put("type","getPlan");
        params.put("id",Config.telephone);
        params.put("operation","get");

        OkHttpUtil.get(params, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                iPlanView.onGetPlanResult(false,call.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    PoiInfo departure = new PoiInfo();
                    departure.location = new LatLng(Double.parseDouble(jsonObject.getString("departureLongitude")), Double.parseDouble(jsonObject.getString("departureLatitude")));
                    departure.name = jsonObject.getString("departureAddress");
                    PoiInfo terminal = new PoiInfo();
                    terminal.location = new LatLng(jsonObject.getDouble("terminalLongitude"), jsonObject.getDouble("terminalLatitude"));
                    terminal.name = jsonObject.getString("terminalAddress");
                    Config.plan = new Plan(jsonObject.getString("desc"),
                            jsonObject.getInt("grade"),
                            jsonObject.getString("startTime"),
                            jsonObject.getString("arrival"),
                            departure,terminal
                            );

                    iPlanView.onGetPlanResult(true, "");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
