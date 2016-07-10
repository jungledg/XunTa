package com.larryhowell.xunta.net;

import android.content.Context;
import android.util.Log;

import com.larryhowell.xunta.common.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;
import com.zhy.http.okhttp.https.HttpsUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class OkHttpUtil {
    public static void init(Context context) {
        CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(context));
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .cookieJar(cookieJar)
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
    }

    public static void get(Map<String, String> params, StringCallback callback) {
        String url = Urls.SERVER + "?";
        final Set<String> keys = params.keySet();
        for (String key : keys) {
            url += key + "=" + params.get(key) + "&";
        }

        OkHttpUtils.get().url(url).build().execute(callback);
    }
}
