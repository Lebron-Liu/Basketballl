package com.xykj.view;

import com.xykj.bean.LiveRoom;

import java.util.List;

public interface LiveCenterView extends IView {

    void showLiveRooms(List<LiveRoom> rooms);
}
