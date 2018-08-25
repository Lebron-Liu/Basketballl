package com.xykj.persenter;

import com.xykj.view.ActiveView;

public class ActivePersonter extends IPersenter<ActiveView> {

    /**
     *  creator: （可选参数）活动发布者
        type：（可选参数）活动类型
        group_id：（可选参数）活动所在的组
        state：（可选参数）活动状态 0正常，1已失效，2被取消
     */
    public void loadActiveList(int userId,int type,int groupId,int state){

    }
}
