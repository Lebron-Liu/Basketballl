package com.xykj.vwill;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.xykj.bean.RoomState;
import com.xykj.fragments.LiveChatFragment;
import com.xykj.utils.Common;
import com.xyy.net.NetManager;
import com.xyy.net.ResponceItem;
import com.xyy.net.StringRequestItem;
import com.xyy.net.imp.Callback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;

public class LiveChatActivity extends FragmentActivity {

    @BindView(R.id.video_view)
    VideoView videoView;
    @BindView(R.id.tv_tips)
    TextView tvTips;
    private int roomId;
    private String playUrl;
    VWillApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //初始化Vitamio库
        Vitamio.isInitialized(this);
        setContentView(R.layout.activity_live_chat);
        ButterKnife.bind(this);
        app = (VWillApp) getApplication();
        Intent it = getIntent();
        //观众端获取拉流地址
        playUrl = it.getStringExtra("url_play");
        roomId = it.getIntExtra("room_id", 0);
        //设置播放地址
        videoView.setVideoPath(playUrl);
        int state = it.getIntExtra("state", 0);
        if (state == 1) {
            //开始观看直播
            videoView.start();
            tvTips.setVisibility(View.GONE);
        }
        //初始化聊天页面
        LiveChatFragment fragment = (LiveChatFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_live_chat);
        fragment.initInfo(app.getLoginUser().getId(), roomId);

        //注册监听服务发来的消息
        EventBus.getDefault().register(this);
        //进入房间
        joinOrExitRoom(Common.URL_JOIN_ROOM);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RoomState state) {
        if (state.getState() == 1) {
            videoView.setVideoPath(playUrl);
            videoView.start();
            tvTips.setVisibility(View.GONE);
        } else {
            tvTips.setVisibility(View.VISIBLE);
            videoView.stopPlayback();
        }
    }

    private void joinOrExitRoom(String url) {
        NetManager.getInstance().execute(new StringRequestItem.Builder()
                .url(url)
                .addStringParam("u_id", String.valueOf(app.getLoginUser().getId()))
                .addStringParam("room_id", String.valueOf(roomId))
                .build(), new Callback<Boolean>() {
            @Override
            public Boolean changeData(ResponceItem responce) {
                return null;
            }

            @Override
            public void onResult(Boolean result) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        //停止观看直播
        videoView.stopPlayback();
        EventBus.getDefault().unregister(this);
        //退出房间
        joinOrExitRoom(Common.URL_EXIT_ROOM);
        super.onDestroy();
    }
}
