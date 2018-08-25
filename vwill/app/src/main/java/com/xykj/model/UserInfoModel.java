package com.xykj.model;

import com.alibaba.fastjson.JSON;
import com.xykj.bean.User;
import com.xyy.net.NetManager;
import com.xyy.net.RequestItem;
import com.xyy.net.ResponceItem;
import com.xyy.net.imp.Callback;

public class UserInfoModel extends IModel<User> {
    public UserInfoModel(RequestItem request) {
        super(request);
    }

    @Override
    public void execute(final ICallback<User> callback) {
        callback.onStart();
        NetManager.getInstance().execute(request, new Callback<User>() {
            @Override
            public User changeData(ResponceItem responce) {
                String json = responce.getString();
                if(!json.startsWith("{result")) {
                    User u = (User) JSON.parseObject(json, User.class);
                    return u;
                }
                return null;
            }

            @Override
            public void onResult(User result) {
                if(null != result){
                    callback.onSuccess(result);
                }else{
                    callback.onFail("加载异常");
                }
                callback.onComplete();
            }
        });
    }
}
