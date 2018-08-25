package com.xykj.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.xykj.bean.Article;
import com.xykj.bean.Reply;
import com.xykj.holder.ArticleHolder;
import com.xykj.holder.OnArticleListener;
import com.xykj.utils.Common;
import com.xykj.utils.PhotoViewUtil;
import com.xykj.vwill.R;
import com.xykj.vwill.UserCenterActivity;
import com.xykj.widget.SpannableViewTouchListner;
import com.xykj.widget.VideoController;
import com.xyy.utils.TipsUtil;
import com.xyy.utils.XImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ArticleDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //文章
    private static final int TYPE_ARTICLE = 1;
    //指示
    private static final int TYPE_TIPS = 2;
    //评论
    private static final int TYPE_REPLY = 3;
    //热评
    private List<Object> hotReplies;
    //新评论
    private List<Object> newReplies;

    private Context context;

    private Article article;

    private PhotoViewUtil photoViewUtil;
    private int articleViewHeight;
    private VideoController videoController;

    public ArticleDetailAdapter(Context context, Article article,VideoController videoController) {
        this.context = context;
        this.article = article;
        photoViewUtil = new PhotoViewUtil(context);
        this.videoController = videoController;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        switch (type) {
            case TYPE_REPLY:
                //评论的布局
                View replyView = LayoutInflater.from(context).inflate(R.layout.item_reply, null);
                return new ReplyHolder(replyView);
            case TYPE_ARTICLE:
                //文章布局
                final View layout = LayoutInflater.from(context).inflate(R.layout.item_article_layout, null);
                layout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        articleViewHeight = layout.getMeasuredHeight();
                        if (articleViewHeight > 0) {
                            layout.getViewTreeObserver().removeOnPreDrawListener(this);
                        }
                        return true;
                    }
                });
                return new ArticleHolder(context, layout, onArticleListener, videoController);
            case TYPE_TIPS:
                TextView tv = new TextView(context);
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
                lp.bottomMargin = 5;
                tv.setLayoutParams(lp);
                tv.setTextSize(20);
                tv.setPadding(5, 5, 5, 5);
                tv.setBackgroundColor(0xFFFFFFCC);
                tv.setTextColor(0xFF6DC592);
                return new TipsHolder(tv);

        }
        return null;
    }

    public int getArticleViewHeight() {
        return articleViewHeight;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ReplyHolder) {
            ReplyHolder replyHolder = (ReplyHolder) viewHolder;
            Object obj;
            //热评的数量
            int hSize = hotReplies == null ? 0 : hotReplies.size();
            //热评还是最新评论
            if (position > 1 && position < 2 + hSize) {
                //热评
                obj = hotReplies.get(position - 2);
            } else {
                //最新评论
                obj = newReplies.get(position - hSize - 3);
            }
            //要显示的第一条信息
            Reply firstReply;
            if (obj instanceof List) {
                List<Reply> atList = (List<Reply>) obj;
                firstReply = atList.get(0);
                //拼接@关系
                SpannableStringBuilder sb = new SpannableStringBuilder();
                sb.append(firstReply.getContent()).append(" ");
                //拼接@的人以及他的评论内容
                int atSize = atList.size();
                int st, en;
                for (int i = 1; i < atSize; i++) {
                    Reply r = atList.get(i);
                    st = sb.length();
                    sb.append("@").append(r.getShowName());
                    en = sb.length();
                    sb.setSpan(new UserClick(r), st, en, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    //拼接评论内容
                    sb.append(" ");
                    st = en + 1;
                    sb.append(r.getContent());
                    en = sb.length();
                    sb.setSpan(new ReplyClick(r), st, en, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                replyHolder.itemContent.setText(sb);
                replyHolder.itemContent.setOnTouchListener(spannableViewTouchListner);
            } else {
                firstReply = (Reply) obj;
                replyHolder.itemContent.setText(firstReply.getContent());
                replyHolder.itemContent.setOnTouchListener(null);
            }
            replyHolder.itemNick.setText(firstReply.getShowName());
            String photo = firstReply.getPhoto();
            if (null != photo && !"".equals(photo) && !"null".equals(firstReply)) {
                XImageLoader.getInstance(context).showImage(Common.SERVER_URL + photo, replyHolder.itemIcon, R.drawable.icon_usr_def);
            } else {
                replyHolder.itemIcon.setImageResource(R.drawable.icon_usr_def);
            }
        } else if (viewHolder instanceof ArticleHolder) {
            ArticleHolder h = (ArticleHolder) viewHolder;
            h.bindView(article, photoViewUtil);
        } else {
            TipsHolder h = (TipsHolder) viewHolder;
            TextView tv = (TextView) h.itemView;
            if (position == 1) {
                tv.setText("热评");
            } else {
                tv.setText("最新评论");
            }
        }
    }

    public Reply getLastReply(int position){
        //热评的数量
        int hSize = hotReplies == null ? 0 : hotReplies.size();
        Object obj;
        //热评还是最新评论
        if (position > 1 && position < 2 + hSize) {
            //热评
            obj = hotReplies.get(position - 2);
        } else {
            //最新评论
            obj = newReplies.get(position - hSize - 3);
        }
        //要显示的第一条信息
        Reply firstReply;
        if (obj instanceof List) {
            List<Reply> atList = (List<Reply>) obj;
            firstReply = atList.get(0);
        }else{
            firstReply = (Reply) obj;
        }
        return firstReply;
    }

    public Article getArticle() {
        return article;
    }

    private SpannableViewTouchListner spannableViewTouchListner = new SpannableViewTouchListner();

    class UserClick extends ClickableSpan {
       private Reply r;

        UserClick(Reply r) {
           this.r = r;
        }

        @Override
        public void onClick(View view) {
            //进入用户个人(评论者)中心
            if(r.getIsHide()==0) {
                Intent it = new Intent(context, UserCenterActivity.class);
                it.putExtra("userId",r.getUserId());
                it.putExtra("nick",r.getShowName());
                context.startActivity(it);
            }else{
                TipsUtil.toast(context,"该用户为匿名用户");
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(context.getResources().getColor(R.color.colorAccent));
        }
    }

    class ReplyClick extends ClickableSpan {
        private Reply reply;

        ReplyClick(Reply reply) {
            this.reply = reply;
        }

        @Override
        public void onClick(View view) {
            if(null != onReplyListener){
                onReplyListener.onReplyClick(reply);
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {

        }
    }

    //评论的监听
    public interface OnReplyListener{
        void onReplyClick(Reply reply);
    }

    private OnReplyListener onReplyListener;

    public void setOnReplyListener(OnReplyListener onReplyListener) {
        this.onReplyListener = onReplyListener;
    }

    private OnArticleListener onArticleListener;

    public void setOnArticleListener(OnArticleListener onArticleListener) {
        this.onArticleListener = onArticleListener;
    }


    class TipsHolder extends RecyclerView.ViewHolder {

        public TipsHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class ReplyHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_icon)
        CircleImageView itemIcon;
        @BindView(R.id.item_nick)
        TextView itemNick;
        @BindView(R.id.item_content)
        TextView itemContent;

        public ReplyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.item_icon, R.id.item_nick, R.id.item_share, R.id.item_support,R.id.item_view})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.item_icon:
                case R.id.item_nick:
                    Reply r = getLastReply(getLayoutPosition());
                    //进入用户个人(评论者)中心
                    if(r.getIsHide()==0) {
                        Intent it = new Intent(context, UserCenterActivity.class);
                        it.putExtra("userId",r.getUserId());
                        it.putExtra("nick",r.getShowName());
                        context.startActivity(it);
                    }else{
                        TipsUtil.toast(context,"该用户为匿名用户");
                    }
                    break;
                case R.id.item_share:
                    break;
                case R.id.item_support:
                    break;
                case R.id.item_view:
                    //对评论布局点击，跟当前评论中的第一个
                    if(null != onReplyListener){
                        Object obj;
                        int position = getLayoutPosition();
                        //热评的数量
                        int hSize = hotReplies == null ? 0 : hotReplies.size();
                        //热评还是最新评论
                        if (position > 1 && position < 2 + hSize) {
                            //热评
                            obj = hotReplies.get(position - 2);
                        } else {
                            //最新评论
                            obj = newReplies.get(position - hSize - 3);
                        }
                        Reply firstReply;
                        if (obj instanceof List) {
                            firstReply = ((List<Reply>)obj).get(0);
                        }else{
                            firstReply = (Reply) obj;
                        }
                        onReplyListener.onReplyClick(firstReply);
                    }
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        int hSize = hotReplies == null ? 0 : hotReplies.size();
        int nSize = newReplies == null ? 0 : newReplies.size();
        return hSize + nSize + 3;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_ARTICLE;
        } else if (position == 1 || position == 2 + (hotReplies == null ? 0 : hotReplies.size())) {
            return TYPE_TIPS;
        } else {
            return TYPE_REPLY;
        }
    }

    public List<Object> getHotReplies() {
        return hotReplies;
    }

    public void setHotReplies(List<Object> hotReplies) {
        this.hotReplies = hotReplies;
        //刷新变化的条目（起始下标，刷新的总数）
        notifyItemRangeChanged(2, hotReplies.size());
    }

    public List<Object> getNewReplies() {
        return newReplies;
    }

    public void setNewReplies(List<Object> newReplies) {
        this.newReplies = newReplies;
        notifyItemRangeChanged(3 + (hotReplies == null ? 0 : hotReplies.size()), newReplies.size());
    }
}
