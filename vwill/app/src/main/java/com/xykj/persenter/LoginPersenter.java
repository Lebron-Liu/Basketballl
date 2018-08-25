package com.xykj.persenter;

import com.xykj.bean.User;
import com.xykj.model.LoginModel;
import com.xykj.utils.Common;
import com.xykj.view.LoginView;
import com.xyy.net.RequestItem;
import com.xyy.net.StringRequestItem;
import com.xyy.utils.Md5Util;

public class LoginPersenter extends IPersenter<LoginView> {

    public void loginByAccount(String name,String psw){
        StringRequestItem request = new StringRequestItem.Builder()
                .url(Common.URL_LOGIN)
                .addStringParam("name",name)
                .addStringParam("psw", Md5Util.getMD5String(psw))
                .build();
        execute(request);
    }

    private void execute(RequestItem request){
        new LoginModel(request).execute(new BaseCallback<User>(this,view) {
            @Override
            public void onSuccess(User data) {
                view.showUser(data);
            }
        });
    }

    public void loginByPhone(String phone){
        StringRequestItem request = new StringRequestItem.Builder()
                .url(Common.URL_LOGIN_BY_PHONE)
                .addStringParam("phone",phone)
                .build();
        execute(request);
    }

    public void loginByThirdPlatform(String openId){
        StringRequestItem request = new StringRequestItem.Builder()
                .url(Common.URL_LOGIN_BY_OPENID)
                .addStringParam("openid",openId)
                .build();
        execute(request);
    }

}
