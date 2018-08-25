package com.xykj.model;

import com.alibaba.fastjson.JSON;
import com.xykj.bean.User;
import com.xykj.utils.Common;
import com.xyy.net.NetManager;
import com.xyy.net.RequestItem;
import com.xyy.net.ResponceItem;
import com.xyy.net.imp.Callback;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginModel extends IModel<User> {
    public LoginModel(RequestItem request) {
        super(request);
    }

    @Override
    public void execute(final ICallback<User> callback) {
        callback.onStart();
        NetManager.getInstance().execute(request, new Callback<Object>() {
            @Override
            public Object changeData(ResponceItem responce) {
                String json = responce.getString();
                try {
                    JSONObject obj = new JSONObject(json);
                    int result = obj.optInt("result");
                    if(result != 0){
                        String extras = "用户名或者密码错误";
                        if(result == 2){
                            extras = obj.optString("extras");
                        }
                        return extras;
                    }else{
                        User u = (User) JSON.parseObject(json,User.class);
                        //记录登录成功的token信息
                        Common.TOKEN = responce.getHeads().get("token").get(0);
                        return u;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onResult(Object result) {
                if(result instanceof User){
                    callback.onSuccess((User) result);
                }else{
                    callback.onFail((String) result);
                }
                callback.onComplete();
            }
        });
    }
}
