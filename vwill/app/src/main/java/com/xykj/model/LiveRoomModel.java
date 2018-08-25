package com.xykj.model;

import com.alibaba.fastjson.JSON;
import com.xykj.bean.LiveRoom;
import com.xyy.net.NetManager;
import com.xyy.net.RequestItem;
import com.xyy.net.ResponceItem;
import com.xyy.net.imp.Callback;

import java.util.List;

public class LiveRoomModel extends IModel<List<LiveRoom>> {
    public LiveRoomModel(RequestItem request) {
        super(request);
    }

    @Override
    public void execute(final ICallback<List<LiveRoom>> callback) {
        callback.onStart();
        NetManager.getInstance().execute(request, new Callback<List<LiveRoom>>() {
            @Override
            public List<LiveRoom> changeData(ResponceItem responce) {
                String json = responce.getString();
                if(json.startsWith("[")){
                    List<LiveRoom> list = JSON.parseArray(json,LiveRoom.class);
                    return list;
                }
                return null;
            }

            @Override
            public void onResult(List<LiveRoom> result) {
                if(null != result){
                    callback.onSuccess(result);
                }else{
                    callback.onFail("暂无房间");
                }
                callback.onComplete();
            }
        });
    }
}
