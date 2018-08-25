package com.xykj.vwill;

import android.app.Application;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.xykj.bean.User;
import com.xykj.exceptions.CrashHandler;
import com.xykj.utils.Constants;

public class VWillApp extends Application {
    private User loginUser;

    @Override
    public void onCreate() {
        super.onCreate();
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
        //初始化新浪sdk
        WbSdk.install(this,new AuthInfo(this,Constants.APP_KEY,Constants.REDIRECT_URL,Constants.SCOPE));
        CrashHandler.getInstance(this).init();
    }

    public User getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(User loginUser) {
        this.loginUser = loginUser;
    }

    public boolean isLogin() {
        return loginUser != null;
    }
}
