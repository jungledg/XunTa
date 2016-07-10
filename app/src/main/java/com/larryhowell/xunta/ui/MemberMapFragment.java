package com.larryhowell.xunta.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.map.MapView;
import com.larryhowell.xunta.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MemberMapFragment extends Fragment {
    @Bind(R.id.mapView)
    MapView mMapView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_map, container, false);

        ButterKnife.bind(this, view);

        initView();

        return view;
    }

    private void initView() {
        mMapView.showZoomControls(false);
        mMapView.showScaleControl(false);

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("正在获取对方当前位置");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Handler().postDelayed(() -> {
            progressDialog.dismiss();
            mMapView.setVisibility(View.VISIBLE);
        }, 2000);
    }

    @Override
    public void onPause() {
        super.onPause();

        mMapView.onPause();
        mMapView.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();

        mMapView.onResume();
        mMapView.setVisibility(View.VISIBLE);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            //可见时执行的操作
        } else {
            //不可见时执行的操作
        }
    }
}
