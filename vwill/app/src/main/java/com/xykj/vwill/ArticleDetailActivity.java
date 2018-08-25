package com.xykj.vwill;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.xykj.adapter.ArticleDetailAdapter;
import com.xykj.bean.Article;
import com.xykj.bean.Reply;
import com.xykj.persenter.ReplyPersenter;
import com.xykj.utils.Common;
import com.xykj.view.BaseActivity;
import com.xykj.view.ReplyView;
import com.xykj.widget.ArticleListener;
import com.xykj.widget.VideoController;
import com.xyy.net.NetManager;
import com.xyy.net.ResponceItem;
import com.xyy.net.StringRequestItem;
import com.xyy.net.imp.Callback;
import com.xyy.utils.TipsUtil;
import com.xyy.view.XVideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ArticleDetailActivity extends BaseActivity<ReplyPersenter> implements ReplyView {
    //登录来评论
    private static final int REQUEST_LOGIN_REPLY = 1;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.ch_collect)
    CheckBox chCollect;
    @BindView(R.id.reply_window_bg)
    View replyWindowBg;
    @BindView(R.id.main_layout)
    RelativeLayout mainLayout;
    private ArticleDetailAdapter adapter;
    private Article article;
    private VideoController videoController;
    //所发布的评论的跟帖的目标
    private int parentId;
    private VWillApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent it = getIntent();
        article = it.getParcelableExtra("article");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getActivityTitle() {
        return article.getTitle();
    }

    @Override
    protected int getType() {
        return TYPE_BACK;
    }

    //记录页面上下滑动的总量
    private int offsetY;

    @Override
    protected void initLayout() {
        super.initLayout();
        app = (VWillApp) getApplication();
        //设置显示方式
        recycler.setLayoutManager(new LinearLayoutManager(this));
        videoController = new VideoController(this);
        //适配器
        adapter = new ArticleDetailAdapter(this, article, videoController);
        articleListener = new ArticleListener(this) {
            @Override
            public void onArticleClick(Article article) {
                //显示评论框
                parentId = 0;
                showReplyWindow("评论文章");
            }
        };
        //文章监听
        adapter.setOnArticleListener(articleListener);
        //评论监听(@别人完成跟帖功能)
        adapter.setOnReplyListener(new ArticleDetailAdapter.OnReplyListener() {
            @Override
            public void onReplyClick(Reply reply) {
                parentId = reply.getId();
                showReplyWindow("@" + reply.getShowName());
            }
        });
        recycler.setAdapter(adapter);
        //监听滚动
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                offsetY += dy;
                //获取文章显示的总高度
                int h = adapter.getArticleViewHeight();
                //播放状态下处理播放器的切换（原位置和浮动窗之间）
                if (h > 0) {
                    if (offsetY <= h - 60) {
                        //正常的在RecyclerView中播放
                        if (isWindowShow) {
                            hidePlayerInWindow();
                            isWindowShow = false;
                        }
                    } else {
                        if (videoController.isPlaying()) {
                            //浮动窗上播放
                            if (!isWindowShow) {
                                isWindowShow = true;
                                showPlayerInWindow();
                            }
                        }
                    }
                }
            }
        });
    }

    private boolean isWindowShow;
    private WindowManager wm;
    private WindowManager.LayoutParams lp;
    //播放器的布局
    private View windowPlayer;

    //显示浮动窗的播放，停止RecyclerView中的播放
    private void showPlayerInWindow() {
        if (null == wm) {
            wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
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
        //将RecyclerView中播放器上地址和位置取出来
        String url = videoController.getCurrentPlayUrl();
        int pos = videoController.getCurrentPos();
        //设置到窗口的播放器上
        XVideoView videoView = ((XVideoView) windowPlayer.findViewById(R.id.video_view));
        videoView.setVideoPath(url);
        videoView.setPlayPos(pos);
        //对适配器中的文章设置播放地址（为了在下一次适配器bindView时可以去指定播放器所要添加的新的容器）
        adapter.getArticle().setPlayUrl(url);
        //停掉RecyclerView中的播放
        videoController.removePlayer();
        //浮动窗上添加播放器
        wm.addView(windowPlayer, lp);
    }

    //隐藏浮动窗的播放，恢复RecyclerView中的播放
    private void hidePlayerInWindow() {
        //从浮动窗播放器中取出播放地址和位置
        XVideoView videoView = ((XVideoView) windowPlayer.findViewById(R.id.video_view));
        int pos = videoView.getCurrentPos();
        String url = videoView.getVideoPath();
        //移除窗口上的播放
        wm.removeView(windowPlayer);
        //将播放地址和位置添加到RecyclerView原来的播放器上
        videoController.resumePlayer(url, pos);
    }

    private ArticleListener articleListener;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ArticleListener.REQUEST_LOGIN:
                articleListener.loginResult(resultCode);
                break;
            case REQUEST_LOGIN_REPLY:
                //登录来评论
                if (resultCode == RESULT_OK) {
                    //提交评论信息
                    publishReply();
                }
                break;
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_article_detail;
    }

    @Override
    public void showHotReplies(List<Object> list) {
        adapter.setHotReplies(list);
    }

    @Override
    public void showNewReplies(List<Object> list) {
        adapter.setNewReplies(list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter.getNewReplies() == null || adapter.getNewReplies().size() == 0) {
            persenter.loadHotReply(article.getId());
            persenter.loadNewsRely(article.getId());
        }
    }

    @OnClick({R.id.ch_collect, R.id.foot_view})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ch_collect:
                break;
            case R.id.foot_view:
                //显示发布评论框
                parentId = 0;
                showReplyWindow("评论文章");
                break;
        }
    }

    private PopupWindow popupWindow;
    private TextInputLayout etInputLayout;
    private TextInputEditText etContent;
    private CheckBox chIsHide;

    //显示评论发布的弹出框
    private void showReplyWindow(String hint) {
        //第一次显示初始化弹窗对象以及布局对象
        if (null == popupWindow) {
            View layout = LayoutInflater.from(this).inflate(R.layout.window_publish_reply, null);
            etInputLayout = layout.findViewById(R.id.et_layout);
            chIsHide = layout.findViewById(R.id.ch_hide);
            //初始布局中的元素
            etContent = layout.findViewById(R.id.et_content);
            layout.findViewById(R.id.btn_publish_reply).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //发布评论
                    //检测用户是否登录
                    if (app.isLogin()) {
                        //登录了直接发布，未登录需要登录完毕时提交
                        publishReply();
                    } else {
                        Intent it = new Intent(ArticleDetailActivity.this, LoginActivity.class);
                        startActivityForResult(it, REQUEST_LOGIN_REPLY);
                    }
                }
            });

            popupWindow = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, 200);
            popupWindow.setBackgroundDrawable(new ColorDrawable());
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);
            //监听窗口隐藏
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    //窗口隐藏时将背景层隐藏
                    replyWindowBg.setVisibility(View.GONE);
                }
            });
        }
        //显示窗口
        replyWindowBg.setVisibility(View.VISIBLE);
        etContent.setHint(hint);
        etInputLayout.setHint(hint);
        popupWindow.showAtLocation(mainLayout, Gravity.BOTTOM, 0, 0);
    }

    private void publishReply() {
        String content = etContent.getText().toString();
        if (!TextUtils.isEmpty(content)) {
            NetManager.getInstance().execute(new StringRequestItem.Builder()
                    .url(Common.URL_PUBLISH_REPLY)
                    .addHead("token", Common.TOKEN)
                    .addStringParam("articleId", String.valueOf(article.getId()))
                    .addStringParam("content", content)
                    .addStringParam("parentId", String.valueOf(parentId))
                    .addStringParam("isHide", chIsHide.isChecked() ? "1" : "0")
                    .build(), new Callback<Boolean>() {
                @Override
                public Boolean changeData(ResponceItem responce) {
                    String json = responce.getString();
                    try {
                        JSONObject obj = new JSONObject(json);
                        return obj.optInt("result") == 1;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return false;
                }

                @Override
                public void onResult(Boolean result) {
                    if (result) {
                        //隐藏发布框
                        etContent.setText("");
                        popupWindow.dismiss();
                        //发布成功，将最新的评论加载出来
                        persenter.loadNewsRely(article.getId());
                    }

                }
            });
        } else {
            TipsUtil.toast(this, "评论内容不能为空");
        }
    }

}
