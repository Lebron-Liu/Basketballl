package com.xykj.model;

import com.alibaba.fastjson.JSON;
import com.xykj.bean.GroupInfo;
import com.xyy.net.NetManager;
import com.xyy.net.RequestItem;
import com.xyy.net.ResponceItem;
import com.xyy.net.imp.Callback;

import java.util.LinkedList;
import java.util.List;

public class GroupListModel extends IModel<List<Object>> {
    private int userId;

    public GroupListModel(RequestItem request, int userId) {
        super(request);
        this.userId = userId;
    }

    @Override
    public void execute(final ICallback<List<Object>> callback) {
        callback.onStart();
        NetManager.getInstance().execute(request, new Callback<List<Object>>() {
            @Override
            public List<Object> changeData(ResponceItem responce) {
                String json = responce.getString();
                List<Object> result = new LinkedList<>();
                List<GroupInfo> list=null;
                List<GroupInfo> createGroups = new LinkedList<>();
                if (!json.startsWith("{result")) {
                    //用户所在的群
                    list = JSON.parseArray(json, GroupInfo.class);
                    //创建的群(群的创建者是自己)
                    int index = 0;
                    while (index < list.size()) {
                        GroupInfo g = list.get(index);
                        if (g.getCreatorId() == userId) {
                            createGroups.add(g);
                            list.remove(index);
                        } else {
                            index++;
                        }
                    }
                }
                result.add("所创建的群(" + createGroups.size() + ")");
                result.addAll(createGroups);
                //加入的群
                result.add("所加入的群(" + (null == list ? 0 : list.size()) + ")");
                result.addAll(list);
                return result;
            }

            @Override
            public void onResult(List<Object> result) {
                callback.onSuccess(result);
                callback.onComplete();
            }
        });
    }
}
