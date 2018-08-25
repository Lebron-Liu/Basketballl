package com.xykj.holder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xykj.bean.Article;
import com.xykj.utils.Common;
import com.xykj.utils.PhotoViewUtil;
import com.xykj.vwill.ArticleDetailActivity;
import com.xykj.vwill.R;
import com.xykj.vwill.UserCenterActivity;
import com.xykj.widget.SpannableViewTouchListner;
import com.xykj.widget.VideoController;
import com.xyy.utils.TipsUtil;
import com.xyy.utils.XImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ArticleHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.item_icon)
    CircleImageView itemIcon;
    @BindView(R.id.item_distance)
    TextView itemDistance;
    @BindView(R.id.item_user_nick)
    TextView itemUserNick;
    @BindView(R.id.item_time)
    TextView itemTime;
    @BindView(R.id.item_title)
    TextView itemTitle;
    @BindView(R.id.item_text_content)
    TextView itemTextContent;
    @BindView(R.id.item_media_content)
    LinearLayout itemMediaContent;
    @BindView(R.id.item_location_name)
    TextView itemLocationName;
    @BindView(R.id.item_support)
    CheckBox itemSupport;
    @BindView(R.id.item_no_support)
    CheckBox itemNoSupport;
    @BindView(R.id.item_reply_num)
    TextView itemReplyNum;
    private Context context;
    private int screenWidth;
    //关联的文章
    private Article article;
    private VideoController videoController;

    public ArticleHolder(Context context, View itemView, OnArticleListener onArticleListener, VideoController videoController) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = context;
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        this.onArticleListener = onArticleListener;
        this.videoController = videoController;
    }

    @OnClick({R.id.item_icon, R.id.item_user_nick, R.id.item_location_name, R.id.item_support, R.id.item_no_support, R.id.item_reply_num, R.id.item_share, R.id.item_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.item_icon:
            case R.id.item_user_nick:
                if(article.getIsHide()==0) {
                    Intent it = new Intent(context, UserCenterActivity.class);
                    it.putExtra("userId",article.getAuthorId());
                    it.putExtra("nick",article.getShowName());
                    context.startActivity(it);
                }else{
                    TipsUtil.toast(context,"该用户为匿名用户");
                }
                break;
            case R.id.item_location_name:
                break;
            case R.id.item_support:
                doSupportOrNoSupport(1);
                break;
            case R.id.item_no_support:
                doSupportOrNoSupport(-1);
                break;
            case R.id.item_share:

                break;
            case R.id.item_reply_num:
            case R.id.item_layout:
                //跳转之前
                if (onArticleListener != null) {
                    //检测当前是否有正在播放的媒体(视频/音频)
                    if (article.getMediaUrls() != null) {
                        if (videoController.isHasPlayer()) {
                            //检测播放的地址是不是当前文章的地址
                            String url = videoController.getCurrentPlayUrl();
                            int size = article.getMediaUrls().length;
                            for (int i = 0; i < size; i++) {
                                if (article.getMediaUrls()[i].equals(url)) {
                                    article.setPlayUrl(url);
                                    article.setPlayPos(videoController.getCurrentPos());
                                    break;
                                }
                            }
                        }
                    }
                    onArticleListener.onArticleClick(article);
                    article.setPlayUrl(null);
                    article.setPlayPos(0);
                }
                break;
        }
    }

    public void bindView(Article article, PhotoViewUtil photoViewUtil) {
        this.article = article;
        itemUserNick.setText(article.getShowName());
        //头像
        String photo = article.getUserPhoto();
        if (null != photo && !"".equals(photo) && !"null".equals(photo)) {
            String url = Common.SERVER_URL + photo;
            XImageLoader.getInstance(context).showImage(url, itemIcon, R.drawable.icon_usr_def);
        } else {
            itemIcon.setImageResource(R.drawable.icon_usr_def);
        }
        //标题
        itemTitle.setText(article.getTitle());
        //时间
        itemTime.setText(article.getTime());
        //文本内容
        //检测是否有文章标签(是否有文章指定的标签)
        if (article.getTagId() == 0) {
            itemTextContent.setText(article.getTextContent());
            itemTextContent.setOnTouchListener(null);
        } else {
            //有标签,拼接标签显示的超链接
            SpannableStringBuilder sb = new SpannableStringBuilder();
            String tagName = article.getTagName();
            sb.append("#").append(tagName).append("#");
            sb.setSpan(new TagClickSpan(article.getTagId()), 0, sb.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            //拼接文本内容
            sb.append(" ").append(article.getTextContent());
            itemTextContent.setText(sb);
            itemTextContent.setOnTouchListener(spannableViewTouchListner);
        }

        itemSupport.setText(String.valueOf(article.getSupport()));
        itemNoSupport.setText(String.valueOf(article.getNoSupport()));
        itemReplyNum.setText(String.valueOf(article.getReplyCount()));
        if (null != article.getLocationName() && !"".equals(article.getLocationName())) {
            itemLocationName.setVisibility(View.VISIBLE);
            itemLocationName.setText(article.getLocationName());
        } else {
            itemLocationName.setVisibility(View.GONE);
        }
        //赞或者踩的状态
        itemSupport.setChecked(false);
        itemNoSupport.setChecked(false);
        if (article.getSupportState() != 0) {
            if (article.getSupportState() == 1) {
                //赞
                itemSupport.setChecked(true);
            } else {
                //踩
                itemNoSupport.setChecked(true);
            }
        }
        //将之前的媒体的显示先移除
        itemMediaContent.removeAllViews();
        //显示媒体内容
        switch (article.getType()) {
            case Common.TYPE_IMAGE:
                itemMediaContent.addView(photoViewUtil.createImageShowView(article.getMediaUrls()));
                break;
            case Common.TYPE_VIDEO:
                //显示缩略图（播放器的容器）
                if (article.getMediaUrls() != null && article.getMediaUrls().length > 0) {
                    for (int i = 0; i < article.getMediaUrls().length; i++) {
                        //将播放地址标记到播放容器上以便于在按钮被点击时获取该容器要播放的地址设置大播放器上
                        String url = article.getMediaUrls()[i];
                        if (article.getPlayUrl() != null && url.equals(article.getPlayUrl()) && videoController.getCurrentVideoPlayGroup() != null) {
                            //第二次播放的状态恢复（播放位置）

                            itemMediaContent.addView(videoController.getCurrentVideoPlayGroup());
                        } else {
                            RelativeLayout videoPlayGroup = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.item_video_layout, null);
                            ImageView ivThumb = videoPlayGroup.findViewById(R.id.item_thumb);
                            //显示缩略图
                            XImageLoader.getInstance(context).showImage(article.getThumbs()[i], ivThumb, screenWidth, 200, 0, false);
                            //播放按钮的点击监听
                            ImageView itemPlay = videoPlayGroup.findViewById(R.id.item_play_video);
                            //将播放容器标记到按钮上，方便在点击监听中获知是哪个位置要播放
                            itemPlay.setTag(videoPlayGroup);

                            videoPlayGroup.setTag(url);
                            itemPlay.setOnClickListener(videoController);
                            //将视频播放容器添加媒体内容区
                            itemMediaContent.addView(videoPlayGroup);
                            //检测是否需要马上播放(有播放器)
                            if (article.getPlayUrl() != null && url.equals(article.getPlayUrl())) {
                                videoController.addPlayer(videoPlayGroup, url, article.getPlayPos());
                            }
                        }
                    }
                }
                break;
        }
    }

    //标签触摸监听(处理其点击)
    private SpannableViewTouchListner spannableViewTouchListner = new SpannableViewTouchListner();

    class TagClickSpan extends ClickableSpan {
        private int tagId;

        TagClickSpan(int tagId) {
            this.tagId = tagId;
        }

        @Override
        public void onClick(View view) {
            TipsUtil.toast(context, "点击了:" + tagId);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(context.getResources().getColor(R.color.colorAccent));
        }
    }

    private void doSupportOrNoSupport(int state) {

        //如果要操作状态跟文章的状态不一样则需要提醒20
        if (article.getSupportState() == 0 || article.getSupportState() == state) {
            //赞或者踩
            //取消赞或者踩
            if (null != onArticleListener) {
                onArticleListener.onSupportOrNoSupport(article, state, this);
            }
        } else {
            //已经被赞或者踩过了
            if (article.getSupportState() != state) {
                //提醒
                if (article.getSupportState() == 1) {
                    TipsUtil.toast(context, "您已经赞过了，不能直接踩");
                    itemNoSupport.setChecked(false);
                } else {
                    TipsUtil.toast(context, "您已经踩过了，不能直接赞");
                    itemSupport.setChecked(false);
                }
            }
        }
    }

    private OnArticleListener onArticleListener;

    public void setOnArticleListener(OnArticleListener onArticleListener) {
        this.onArticleListener = onArticleListener;
    }

    public void setSupportState(int state, int num, boolean check) {
        if (state == 1) {
            itemSupport.setText(String.valueOf(num));
            itemSupport.setChecked(check);
        } else {
            itemNoSupport.setText(String.valueOf(num));
            itemNoSupport.setChecked(check);
        }
    }
}
