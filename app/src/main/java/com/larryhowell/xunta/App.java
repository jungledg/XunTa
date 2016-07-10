package com.larryhowell.xunta;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.baidu.mapapi.SDKInitializer;
import com.larryhowell.xunta.common.Config;
import com.larryhowell.xunta.common.Constants;
import com.larryhowell.xunta.net.OkHttpUtil;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.L;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;


public class App extends Application {
    public static SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();

        sp = getApplicationContext().getSharedPreferences(Constants.SP_FILENAME, Context.MODE_PRIVATE);

        //initService();
    }

    private void initService() {
        // 启动崩溃统计
        CrashReport.initCrashReport(getApplicationContext(), Constants.BUGLY_APP_ID, false);

        // 初始化请求
        OkHttpUtil.init(getApplicationContext());

        // 初始化网络状态
        getNetworkState();

        // 读取存储好的数据——cookie,公司信息,个人信息
        loadStorageData();

        // 初始化图片加载框架
        initImageLoader();

        // 开启推送服务
        //initPushAgent();

        // 初始化百度地图
        SDKInitializer.initialize(getApplicationContext());

        // 初始化数据统计
        MobclickAgent.startWithConfigure(new MobclickAgent.UMAnalyticsConfig(
                getApplicationContext(), Constants.UMENG_APP_KEY, "developer"));
    }

    private void getNetworkState() {
        // 获取网络连接管理器对象（系统服务对象）
        ConnectivityManager cm
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // 获取网络状态
        NetworkInfo info = cm.getActiveNetworkInfo();

        Config.isConnected = info != null && info.isAvailable();
    }

    private void loadStorageData() {
        Config.telephone = sp.getString(Constants.SP_KEY_TELEPHONE, "");
    }

    private void initImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true).cacheInMemory(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).defaultDisplayImageOptions(defaultOptions)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.FIFO).build();
        L.writeLogs(false);
        ImageLoader.getInstance().init(config);
    }
}
