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
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xykj.bean.Article;
import com.xykj.holder.ArticleHolder;
import com.xykj.holder.OnArticleListener;
import com.xykj.utils.Common;
import com.xykj.utils.PhotoViewUtil;
import com.xykj.vwill.ImageShowActivity;
import com.xykj.vwill.R;
import com.xykj.widget.SpannableViewTouchListner;
import com.xykj.widget.VideoController;
import com.xykj.widget.XGridView;
import com.xyy.utils.TipsUtil;
import com.xyy.utils.XImageLoader;
import com.xyy.view.XVideoView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleHolder> {

    private List<Article> list;
    private Context context;
    private LayoutInflater inflater;
    PhotoViewUtil photoViewUtil;
    VideoController videoController;

    public ArticleAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        photoViewUtil = new PhotoViewUtil(context);
        videoController = new VideoController(context);
    }

    private OnArticleListener onArticleListener;

    public void setOnArticleListener(OnArticleListener onArticleListener) {
        this.onArticleListener = onArticleListener;
    }

    @Override
    public ArticleHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layout = inflater.inflate(R.layout.item_article_layout, null);
        return new ArticleHolder(context, layout, onArticleListener, videoController);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleHolder holder, int p) {
        Article a = list.get(p);
        holder.bindView(a, photoViewUtil);
    }


    @Override
    public int getItemCount() {
        return null == list ? 0 : list.size();
    }


    public List<Article> getList() {
        return list;
    }

    public boolean addList(List<Article> loadList, int page) {
        if (list == null) {
            list = new LinkedList<>();
            list.addAll(loadList);
            notifyDataSetChanged();
            return true;
        } else {
            //当前页显示的条数
            int currentShowSize = list.size() - (page - 1) * 20;
            //检测当前页的总数和加载的数量是否一样，一样则表示没有更新，不一样则取多余的部分
            if (currentShowSize < loadList.size()) {
                //有新内容
                for (int i = currentShowSize; i < loadList.size(); i++) {
                    list.add(0, loadList.get(i));
                }
                notifyDataSetChanged();
                return true;
            } else {
                //没有新内容
                return false;
            }
        }
    }

    public void removePlayer() {
        videoController.removePlayer();
    }

}
