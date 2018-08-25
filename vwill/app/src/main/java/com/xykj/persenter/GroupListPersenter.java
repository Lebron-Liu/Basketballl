package com.xykj.persenter;

import com.xykj.model.GroupListModel;
import com.xykj.utils.Common;
import com.xykj.view.GroupListView;
import com.xyy.net.RequestItem;

import java.util.List;

public class GroupListPersenter extends IPersenter<GroupListView> {

    public void loadGroupList(int userId) {
        RequestItem request = new RequestItem.Builder()
                .url(Common.URL_LOAD_GROUP_LIST + "?u_id=" + userId)
                .build();
        new GroupListModel(request, userId).execute(new BaseCallback<List<Object>>(this,view) {
            @Override
            public void onSuccess(List<Object> data) {
                view.showGroupList(data);
            }
        });
    }
}
