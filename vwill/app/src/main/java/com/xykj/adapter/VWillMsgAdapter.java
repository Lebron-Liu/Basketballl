package com.xykj.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mct.model.CMessage;
import com.xykj.bean.VWillMessage;
import com.xykj.utils.Common;
import com.xykj.vwill.R;
import com.xykj.widget.VideoController;
import com.xyy.utils.XImageLoader;
import com.xyy.view.XVideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class VWillMsgAdapter extends ItemAdapter<VWillMessage> {
    private int screenWidth;

    public VWillMsgAdapter(Context context) {
        super(context);
        screenWidth = context.getResources().getDisplayMetrics().widthPixels - 20;
        videoController = new VideoController(context) {
            @Override
            protected void onPlayBtnClick(View view) {
                String url = (String) view.getTag();
                //显示弹出框播放
                showPlayerInWindow(url);
            }
        };
    }

    private boolean isWindowShow;
    private WindowManager wm;
    private WindowManager.LayoutParams lp;
    //播放器的布局
    private View windowPlayer;

    //显示浮动窗的播放，停止RecyclerView中的播放
    private void showPlayerInWindow(String url) {
        if (null == wm) {
            wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            lp = new WindowManager.LayoutParams();
            //应用级别窗口
            lp.type = WindowManager.LayoutParams.TYPE_APPLICATION;
            //窗口的特性
            lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
            lp.gravity = Gravity.LEFT | Gravity.TOP;
            lp.width = wm.getDefaultDisplay().getWidth();
            lp.height = 300;
            lp.y = 80;
            windowPlayer = videoController.createPlayerView();
        }
        //设置到窗口的播放器上
        XVideoView videoView = ((XVideoView) windowPlayer.findViewById(R.id.video_view));
        //检测是否有正在播放，如果有并且当前播放跟要播放的地址不同，停掉原来的，播放信息
        if (!isWindowShow) {
            videoView.setVideoPath(url);
            //浮动窗上添加播放器
            wm.addView(windowPlayer, lp);
            isWindowShow = true;
        } else {
            if (!url.equals(videoView.getVideoPath())) {
                videoView.play(url);
            }
        }
    }

    //隐藏浮动窗的播放，恢复RecyclerView中的播放
    public void hidePlayerInWindow() {
        if (isWindowShow) {
            //移除窗口上的播放
            wm.removeView(windowPlayer);
            isWindowShow = false;
        }
    }

    public boolean isWindowShow() {
        return isWindowShow;
    }

    @Override
    protected ViewHolder createHolder(int t) {
        View layout = LayoutInflater.from(context).inflate(R.layout.item_vwillmsg_layout, null);
        return new MsgHolder(layout);
    }

    @Override
    protected void bindView(VWillMessage vWillMessage, ViewHolder holder) {
        MsgHolder h = (MsgHolder) holder;
        if (vWillMessage.getType() == VWillMessage.TYPE_RECEIV) {
            //显示左边的 隐藏右边的
            h.itemInContentLayout.setVisibility(View.VISIBLE);
            h.itemInIcon.setVisibility(View.VISIBLE);
            h.itemInNick.setVisibility(View.VISIBLE);

            h.itemOutContentLayout.setVisibility(View.GONE);
            h.itemOutIcon.setVisibility(View.GONE);
            h.itemOutNick.setVisibility(View.GONE);
            showMsg(h.itemInIcon, h.itemInNick, h.itemInText, h.itemInMedia, vWillMessage);
        } else {
            //发出的 隐藏坐标 显示右边
            h.itemInContentLayout.setVisibility(View.GONE);
            h.itemInIcon.setVisibility(View.GONE);
            h.itemInNick.setVisibility(View.GONE);

            h.itemOutContentLayout.setVisibility(View.VISIBLE);
            h.itemOutIcon.setVisibility(View.VISIBLE);
            h.itemOutNick.setVisibility(View.VISIBLE);
            showMsg(h.itemOutIcon, h.itemOutNick, h.itemOutText, h.itemOutMedia, vWillMessage);
        }
    }

    private void showMsg(ImageView ivIcon, TextView tvNick, TextView tvTextContent, LinearLayout mediaLayout, VWillMessage msg) {
        String photo = msg.getPhoto();
        if (null != photo && !"".equals(photo)) {
            XImageLoader.getInstance(context).showImage(Common.SERVER_URL + photo, ivIcon, R.drawable.icon_usr_def);
        } else {
            ivIcon.setImageResource(R.drawable.icon_usr_def);
        }
        tvNick.setText(msg.getNick());
        int contentType = msg.getContentType();
        if (contentType == CMessage.TYPE_TEXT || contentType == CMessage.TYPE_LOCATION) {
            tvTextContent.setVisibility(View.VISIBLE);
        } else {
            tvTextContent.setVisibility(View.GONE);
        }
//        tvTextContent.setText(msg.getMsg());
        //移除之前的显示
        mediaLayout.removeAllViews();
        switch (contentType) {
            case CMessage.TYPE_TEXT:
                tvTextContent.setText(msg.getMsg());
                break;
            case CMessage.TYPE_LOCATION:
                //将位置名称显示到文本框中
                try {
                    JSONObject obj = new JSONObject(msg.getMsg());
                    String locName = obj.optString("extras");
                    tvTextContent.setText(locName);
                    //显示一张默认的位置指示
                    ImageView iv = createImageView();
                    iv.setImageResource(R.drawable.ic_map);
                    mediaLayout.addView(iv);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case CMessage.TYPE_PICTURE:
                //图像
                ImageView iv = createImageView();
                String url;
                if (msg.getType() == VWillMessage.TYPE_RECEIV) {
                    //网址（相对地址）
                    url = Common.SERVER_URL + msg.getMsg();
                } else {
                    //如果是发出的，则图像地址为地址
                    url = msg.getMsg();
                }
                XImageLoader.getInstance(context).showImage(url, iv, screenWidth, 0, R.drawable.filesystem_icon_photo, false);
                mediaLayout.addView(iv);
                break;
            case CMessage.TYPE_VIDEO:
                String videoUrl = "", videoThumb = "";
                if (msg.getType() == VWillMessage.TYPE_RECEIV) {
                    //{file:"/video/1111",thumb:"/video/asas"}
                    try {
                        JSONObject object = new JSONObject(msg.getMsg());
                        videoUrl = Common.SERVER_URL + object.optString("file");
                        videoThumb = Common.SERVER_URL + object.optString("thumb");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    videoUrl = videoThumb = msg.getMsg();
                }
                RelativeLayout videoPlayGroup = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.item_video_layout, null);
                ImageView ivThumb = videoPlayGroup.findViewById(R.id.item_thumb);
                //显示缩略图
                XImageLoader.getInstance(context).showImage(videoThumb, ivThumb, screenWidth, 200, 0, false);
                //播放按钮的点击监听
                ImageView itemPlay = videoPlayGroup.findViewById(R.id.item_play_video);
                //将播放容器标记到按钮上，方便在点击监听中获知是哪个位置要播放
                itemPlay.setTag(videoUrl);

                itemPlay.setOnClickListener(videoController);
                //将视频播放容器添加媒体内容区
                mediaLayout.addView(videoPlayGroup);
                break;
            default:
                tvTextContent.setVisibility(View.VISIBLE);
                tvTextContent.setText(msg.getMsg());
                break;
        }
    }

    private ImageView createImageView() {
        ImageView iv = new ImageView(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        iv.setLayoutParams(lp);
        return iv;
    }

    private VideoController videoController;

    class MsgHolder extends ViewHolder {
        @BindView(R.id.item_in_icon)
        CircleImageView itemInIcon;
        @BindView(R.id.item_in_nick)
        TextView itemInNick;
        @BindView(R.id.item_in_text)
        TextView itemInText;
        @BindView(R.id.item_in_media)
        LinearLayout itemInMedia;
        @BindView(R.id.item_in_content_layout)
        LinearLayout itemInContentLayout;
        @BindView(R.id.item_out_icon)
        CircleImageView itemOutIcon;
        @BindView(R.id.item_out_nick)
        TextView itemOutNick;
        @BindView(R.id.item_out_text)
        TextView itemOutText;
        @BindView(R.id.item_out_media)
        LinearLayout itemOutMedia;
        @BindView(R.id.item_out_content_layout)
        LinearLayout itemOutContentLayout;

        public MsgHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.item_in_icon, R.id.item_in_nick})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.item_in_icon:
                case R.id.item_in_nick:

                    break;
            }
        }
    }

    public void addMsg(VWillMessage msg) {
        if (null == list) {
            list = new LinkedList<>();
        }
        list.add(msg);
        notifyDataSetChanged();
    }


}
