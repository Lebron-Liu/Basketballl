package com.xykj.persenter;

import com.xykj.bean.LiveRoom;
import com.xykj.model.LiveRoomModel;
import com.xykj.utils.Common;
import com.xykj.view.LiveCenterView;
import com.xyy.net.RequestItem;

import java.util.List;

public class LiveRoomPersenter extends IPersenter<LiveCenterView> {

    public void loadLiveRoom(int userId) {
        StringBuilder sb = new StringBuilder(Common.URL_LOAD_LIVE_ROOMS);
        if (userId != 0) {
            sb.append("?u_id=").append(userId);
        }
        RequestItem r = new RequestItem.Builder()
                .url(sb.toString())
                .build();
        new LiveRoomModel(r).execute(new BaseCallback<List<LiveRoom>>(this, view) {
            @Override
            public void onSuccess(List<LiveRoom> data) {
                view.showLiveRooms(data);
            }
        });
    }
}
