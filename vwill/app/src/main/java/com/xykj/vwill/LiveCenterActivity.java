package com.xykj.vwill;

import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.xykj.adapter.LiveRoomAdapter;
import com.xykj.bean.LiveRoom;
import com.xykj.persenter.LiveRoomPersenter;
import com.xykj.view.BaseActivity;
import com.xykj.view.LiveCenterView;

import java.util.List;

public class LiveCenterActivity extends BaseActivity<LiveRoomPersenter> implements LiveCenterView {
    private LiveRoomAdapter adapter;
    private VWillApp app;

    @Override
    protected int getType() {
        return TYPE_BACK_SURE;
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_live_center;
    }

    private LiveRoom room;

    @Override
    protected void initLayout() {
        app = (VWillApp) getApplication();
        RecyclerView mRecycler = findViewById(R.id.m_recycler);
        //显示方式（瀑布流 ）
        mRecycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter = new LiveRoomAdapter(this);
        adapter.setOnLiveRoomClickListener(new LiveRoomAdapter.OnLiveRoomClickListener() {
            @Override
            public void onLiveRoomClick(LiveRoom room) {
                LiveCenterActivity.this.room = room;
                if (app.isLogin()) {
                    onClickPlay();
                } else {
                    Intent it = new Intent(LiveCenterActivity.this, LoginActivity.class);
                    startActivityForResult(it, 102);
                }
            }
        });
        mRecycler.setAdapter(adapter);
        //设置Item之间的修饰
        mRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.left = 5;
                outRect.right = 5;
                outRect.top = 10;
                outRect.bottom = 10;
            }
        });
    }


    public void onClickPlay() {
        Intent i = new Intent(LiveCenterActivity.this, LiveChatActivity.class);
        i.putExtra("url_play", room.getPlayUrl());
        i.putExtra("state",room.getState());
        i.putExtra("room_id",room.getRoomId());
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //加载所有房间列表
        persenter.loadLiveRoom(0);
    }

    @Override
    public void showLiveRooms(List<LiveRoom> rooms) {
        adapter.setList(rooms);
    }

    @Override
    protected String getTitleRightText() {
        return "我的直播间";
    }

    @Override
    protected String getActivityTitle() {
        return "直播间";
    }

    @Override
    protected void onTitleRightViewClick(View v) {
        //进入个人直播界面
        if (app.isLogin()) {
            Intent it = new Intent(this, MyLiveCenterActivity.class);
            startActivity(it);
        } else {
            Intent it = new Intent(this, LoginActivity.class);
            startActivityForResult(it, 101);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 101:
                if (resultCode == RESULT_OK) {
                    Intent it = new Intent(this, MyLiveCenterActivity.class);
                    startActivity(it);
                }
                break;
            case 102:
                if (resultCode == RESULT_OK) {
                    if(room.getAnchorId() == app.getLoginUser().getId()){
                        Intent it = new Intent(this, MyLiveCenterActivity.class);
                        startActivity(it);
                    }else {
                        onClickPlay();
                    }
                }
                break;
        }
    }
}
