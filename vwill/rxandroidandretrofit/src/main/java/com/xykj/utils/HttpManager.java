package com.xykj.utils;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.xykj.factory.CustomConverterFactory;
import com.xykj.factory.HttpCacheInterceptor;
import com.xykj.model.imp.DataServer;
import com.xykj.model.imp.RxDataServer;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * 网络请求工具
 */
public class HttpManager {
    //普通Retrofit使用的数据接口
    private DataServer dataServer;
    //Retrofit以及RxJava结合使用时的数据接口
    private RxDataServer rxDataServer;

    private Retrofit retrofit;
    private static HttpManager instance;
    private Context context;

    private HttpManager(Context context) {
        this.context = context;
        Gson gson = new GsonBuilder()
                //配置你的Gson
                .setDateFormat("yyyy-MM-dd hh:mm:ss")
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl(Common.BASE_URL)
                .addConverterFactory(CustomConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getBuilder().build())
                .build();
    }

    public static HttpManager getInstance(Context context) {
        if (instance == null) {
            instance = new HttpManager(context);
        }
        return instance;
    }

    public DataServer getDataServer() {
        if (dataServer == null) {
            dataServer = retrofit.create(DataServer.class);
        }
        return dataServer;
    }

    public RxDataServer getRxDataServer() {
        if (rxDataServer == null) {
            rxDataServer = retrofit.create(RxDataServer.class);
        }
        return rxDataServer;
    }

    private OkHttpClient.Builder getBuilder() {
        File cacheFile = new File(context.getCacheDir().getAbsolutePath(), "HttpCache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new HttpCacheInterceptor());
        builder.cache(cache);
        builder.readTimeout(Common.READ_TIMEOUT, TimeUnit.SECONDS);
        builder.connectTimeout(Common.CONNECT_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(Common.WRITE_TIMEOUT, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);
        return builder;
    }
}
