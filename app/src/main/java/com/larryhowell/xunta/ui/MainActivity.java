package com.larryhowell.xunta.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.ut.impl.UTLifecycleAdapter;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.balysv.materialripple.MaterialRippleLayout;
import com.larryhowell.xunta.R;
import com.larryhowell.xunta.common.Config;
import com.larryhowell.xunta.common.Constants;
import com.larryhowell.xunta.common.UtilBox;
import com.larryhowell.xunta.presenter.IUpdatePresenter;
import com.larryhowell.xunta.presenter.UpdatePresenterImpl;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, BDLocationListener,
        View.OnClickListener, IUpdatePresenter.IUpdateView {

    @Bind(R.id.mapView)
    MapView mMapView;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.nav_view)
    NavigationView mNavigationView;

    @Bind(R.id.ripple)
    MaterialRippleLayout mRippleLayout;

    @Bind(R.id.button)
    Button mButton;

    @Bind(R.id.appBar)
    AppBarLayout mAppBarLayout;

    private CircleImageView mNavigationPortraitImageView;
    private TextView mNicknameTextView;

    public LocationClient mLocationClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        initView();

        mLocationClient = new LocationClient(getApplicationContext());
        initLocation();
        mLocationClient.registerLocationListener(this);

        mLocationClient.start();

        new UpdatePresenterImpl(this).getVersion();
    }

    private void initView() {
        setSupportActionBar(mToolbar);

        initNavigationView();

        mMapView.showZoomControls(false);
        mMapView.showScaleControl(false);

        mRippleLayout.setOnClickListener(v -> {
            if (Config.telephone == null || "".equals(Config.telephone)) {
                showLoginDialog();
            } else {
                startActivity(
                        new Intent(MainActivity.this, BindListActivity.class)
                        , ActivityOptions.makeSceneTransitionAnimation(
                                MainActivity.this,
                                Pair.create(mAppBarLayout, "appBar")
                        ).toBundle());
                //startActivity(new Intent(MainActivity.this, BindListActivity.class));
            }
        });
    }

    private void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_NoActionBar_MinWidth);
        builder.setMessage("需要登录才能使用这个功能哦")
                .setCancelable(true)
                .setPositiveButton("登录", (dialog, which) -> {
                    startActivityForResult(
                            new Intent(MainActivity.this, LoginActivity.class),
                            Constants.CODE_LOGIN,
                            ActivityOptions.makeSceneTransitionAnimation(
                                    MainActivity.this,
                                    Pair.create(mButton, "button"),
                                    Pair.create(mAppBarLayout, "appBar")
                            ).toBundle());
                })
                .setNegativeButton("取消", null);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        //option.setScanSpan(1000); // 定位次数
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        //option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        //option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        //option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        mLocationClient.setLocOption(option);
    }

    Boolean isFirstLoc = true;

    @Override
    public void onReceiveLocation(BDLocation location) {
        BaiduMap mBaiduMap = mMapView.getMap();

        // map view 销毁后不再处理新接收的位置
        if (location == null || mMapView == null) {
            return;
        }
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                .direction(location.getDirection())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();
        mBaiduMap.setMyLocationData(locData);

        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMyLocationConfigeration(
                new MyLocationConfiguration(
                        MyLocationConfiguration.LocationMode.FOLLOWING, true, null));

        ReverseGeoCodeOption option = new ReverseGeoCodeOption();
        option.location(new LatLng(location.getLatitude(), location.getLongitude()));
        GeoCoder geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                String address = reverseGeoCodeResult.getAddress();

                Config.currentCity = address.substring(address.indexOf("省") + 1, address.indexOf("市"));

                Config.currentCityDetail = address.substring(address.indexOf("市") + 1, address.length());
            }
        });
        geoCoder.reverseGeoCode(option);

        if (isFirstLoc) {
            isFirstLoc = false;
            LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(18.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }

    private void initNavigationView() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);
        //mNavigationView.setItemIconTintList(null);

        // 设置昵称
        mNicknameTextView = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.tv_navigation_nickname);
        mNicknameTextView.setText(Config.nickname);

        // 获取抽屉的头像
        mNavigationPortraitImageView = (CircleImageView) mNavigationView.getHeaderView(0).findViewById(R.id.iv_navigation);
        if (!"".equals(Config.portrait)) {
            ImageLoader.getInstance().displayImage(Config.portrait, mNavigationPortraitImageView);
        }

        mNavigationView.getHeaderView(0).findViewById(R.id.ll_nvHeader).setBackgroundResource(R.drawable.nav_header_background);
        mNavigationView.getHeaderView(0).findViewById(R.id.ll_nvHeader).setOnClickListener(this);
    }

    @Override
    public void onGetVersionResult(Boolean result, String info) {
        if(result) {
            int versionCode = UtilBox.getPackageInfo(this).versionCode;

            if(Integer.valueOf(info).compareTo(versionCode) <= 0) {
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_NoActionBar_MinWidth);
            builder.setTitle("是否更新")
                    .setMessage("当前版本:" + versionCode + "\n最新版本:" + info)
                    .setCancelable(true)
                    .setPositiveButton("更新", (dialog, which) -> {
                        Uri uri = Uri.parse("http://xunta.file.alimmdn.com/xunta_" + info + ".apk");
                        startActivity(new Intent(Intent.ACTION_VIEW,uri));
                    })
                    .setNegativeButton("取消", null);

            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        } else {
            UtilBox.reportBug(info);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_nvHeader:
            case R.id.iv_navigation:
            case R.id.tv_navigation_nickname:
                startActivity(new Intent(this, UserInfoActivity.class));
                mDrawer.closeDrawer(GravityCompat.START);
                break;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_NoActionBar_MinWidth);
            builder.setMessage("真的要注销吗")
                    .setNegativeButton("假的", (dialog, which) -> {

                    })
                    .setPositiveButton("真的", (dialog, which) -> {
                        UtilBox.clearAllData(MainActivity.this);

                        ImageLoader.getInstance().displayImage(Config.portrait, mNavigationPortraitImageView);
                        mNicknameTextView.setText(Config.nickname);

                        //UtilBox.showSnackbar(MainActivity.this, "已注销");

                        Toast.makeText(MainActivity.this, "已注销", Toast.LENGTH_SHORT).show();
                    })
                    .setCancelable(true);
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.CODE_LOGIN:
                if (resultCode == RESULT_OK) {
                    ImageLoader.getInstance().displayImage(Config.portrait, mNavigationPortraitImageView);
                    mNicknameTextView.setText(Config.nickname);
                    mDrawer.openDrawer(GravityCompat.START);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        mMapView.setVisibility(View.VISIBLE);
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        mMapView.setVisibility(View.GONE);
        MobclickAgent.onPause(this);
    }
}