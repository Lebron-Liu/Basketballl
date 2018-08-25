package com.xykj.persenter;

import com.xykj.bean.User;
import com.xykj.model.UserInfoModel;
import com.xykj.utils.Common;
import com.xykj.view.UserInfoView;
import com.xyy.net.RequestItem;

public class UserInfoPersenter extends IPersenter<UserInfoView> {

    public void loadUserInfo(int userId){
        RequestItem request = new RequestItem.Builder()
                .url(Common.URL_LOAD_USER_INFO+"?u_id="+userId)
                .build();
        new UserInfoModel(request).execute(new BaseCallback<User>(this,view) {
            @Override
            public void onSuccess(User data) {
                view.showUserInfo(data);
            }
        });

    }
}
