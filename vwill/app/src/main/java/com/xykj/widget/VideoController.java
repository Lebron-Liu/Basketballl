package com.xykj.widget;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xykj.vwill.R;
import com.xyy.utils.TipsUtil;
import com.xyy.view.XVideoView;

public class VideoController implements View.OnClickListener {
    //记录当前播放器的布局对象
    private View playerLayout;
    //记录当前正在播放的容器
    private RelativeLayout currentVideoPlayGroup;
    private Context context;

    public VideoController(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_play_video:
                onPlayBtnClick(view);
                break;
            case R.id.video_view:
                XVideoView videoView = (XVideoView) view;
                View tipsView = (View) videoView.getTag();
                if (videoView.isPlaying()) {
                    videoView.pause();
                    tipsView.setVisibility(View.VISIBLE);
                } else {
                    tipsView.setVisibility(View.GONE);
                    videoView.start();
                }
                break;
        }

    }

    protected void onPlayBtnClick(View view){
        removePlayer();
        //向播放容器中添加播放器
        RelativeLayout videoPlayGroup = (RelativeLayout) view.getTag();
        String url = (String) videoPlayGroup.getTag();
        addPlayer(videoPlayGroup, url, 0);
    }

    public void removePlayer() {
        //如果当前列表中存在正在播放的容器（带着播放器）
        if (currentVideoPlayGroup != null && playerLayout != null && currentVideoPlayGroup.indexOfChild(playerLayout) > -1) {
            //先停止当前的播放
            currentVideoPlayGroup.removeView(playerLayout);
        }
    }

    public void resumePlayer(String url, int pos) {
        if (currentVideoPlayGroup != null && playerLayout != null && currentVideoPlayGroup.indexOfChild(playerLayout) == -1) {
           addPlayer(currentVideoPlayGroup,url,pos);
        }
    }

    public void setCurrentVideoPlayGroup(RelativeLayout currentVideoPlayGroup) {
        this.currentVideoPlayGroup = currentVideoPlayGroup;
    }

    public RelativeLayout getCurrentVideoPlayGroup() {
        return currentVideoPlayGroup;
    }

    public void addPlayer(RelativeLayout videoPlayGroup, String url, int pos) {
        //设置播放地址
        if (playerLayout == null) {
            playerLayout = createPlayerView();
        }
        //将暂停指示隐藏
        playerLayout.findViewById(R.id.tv_tips).setVisibility(View.GONE);
        XVideoView videoView = ((XVideoView) playerLayout.findViewById(R.id.video_view));
        videoView.setVideoPath(url);
        if (pos > 0) {
            videoView.setPlayPos(pos);
        }
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        videoPlayGroup.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        videoPlayGroup.addView(playerLayout, lp);
        //记录当前播放的容器
        currentVideoPlayGroup = videoPlayGroup;
    }

    public View createPlayerView() {
        View playerLayout = LayoutInflater.from(context).inflate(R.layout.video_player_layout, null);
        //进度更新的视图设置
        SeekBar seek = playerLayout.findViewById(R.id.seek);
        XVideoView videoView = playerLayout.findViewById(R.id.video_view);
        videoView.setSeekBar(seek);
        View tipsView = playerLayout.findViewById(R.id.tv_tips);
        videoView.setTag(tipsView);
        videoView.setOnClickListener(this);
        playerLayout.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        return playerLayout;
    }

    //是否有播放器正在播放
    public boolean isHasPlayer() {
        return playerLayout != null && currentVideoPlayGroup != null && currentVideoPlayGroup.indexOfChild(playerLayout) > -1;
    }

    //是否有播放器在播放
    public boolean isPlaying() {
        if (isHasPlayer()) {
            return ((XVideoView) playerLayout.findViewById(R.id.video_view)).isPlaying();
        }
        return false;
    }

    //获取当前播放的地址
    public String getCurrentPlayUrl() {
        return ((XVideoView) playerLayout.findViewById(R.id.video_view)).getVideoPath();
    }

    //获取当前播放位置
    public int getCurrentPos() {
        return ((XVideoView) playerLayout.findViewById(R.id.video_view)).getCurrentPos();
    }

}
