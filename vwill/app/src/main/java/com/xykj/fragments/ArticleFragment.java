package com.xykj.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xykj.adapter.ArticleAdapter;
import com.xykj.bean.Article;
import com.xykj.holder.ArticleHolder;
import com.xykj.holder.OnArticleListener;
import com.xykj.persenter.ArticlePersenter;
import com.xykj.utils.Common;
import com.xykj.view.ArticleView;
import com.xykj.view.BaseFragment;
import com.xykj.vwill.LoginActivity;
import com.xykj.vwill.R;
import com.xykj.vwill.VWillApp;
import com.xykj.widget.ArticleListener;
import com.xyy.net.NetManager;
import com.xyy.net.ResponceItem;
import com.xyy.net.StringRequestItem;
import com.xyy.net.imp.Callback;
import com.xyy.utils.TipsUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 文章内容页
 */
public class ArticleFragment extends BaseFragment<ArticlePersenter> implements ArticleView {
    @BindView(R.id.list_article)
    RecyclerView listArticle;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;
    Unbinder unbinder;
    //显示的类型
    private int type;
    //当前加载的页码
    private int page = 1;
    private ArticleAdapter adapter;
    VWillApp app;

    public ArticleFragment() {
    }

    public static ArticleFragment newInstance(int type) {
        ArticleFragment f = new ArticleFragment();
        f.type = type;
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (VWillApp) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //设置进度条的颜色
        swipeLayout.setProgressBackgroundColorSchemeColor(0xffcccccc);
        //进度背景颜色
        swipeLayout.setColorSchemeResources(R.color.colorPrimary);
        //设置下拉监听
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMore();
            }
        });
        //设置RecyclerView显示效果
        listArticle.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (null == adapter) {
            adapter = new ArticleAdapter(getActivity());
            onArticleListener = new ArticleListener(this);
            adapter.setOnArticleListener(onArticleListener);
        }
        listArticle.setAdapter(adapter);
    }

    ArticleListener onArticleListener;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ArticleListener.REQUEST_LOGIN) {
            onArticleListener.loginResult(resultCode);
        }
    }

    public void loadData() {
        //如果当前页面没有数据，让程序使用该方法加载，如果有数据则让用自己下拉，监听到下拉刷新再处理加载更多
        if (null != adapter && adapter.getItemCount() == 0) {
            persenter.loadArticleByType(type, 0, page);
        }
    }

    private void loadMore() {
        if (type == Common.TYPE_ATTENTION) {
            if (app.isLogin()) {
                persenter.loadAttentionArticles(app.getLoginUser().getId());
            } else {
                swipeLayout.setRefreshing(false);
            }
        } else {
            persenter.loadArticleByType(type, 0, page);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (app.isLogin() && type == Common.TYPE_ATTENTION && adapter.getItemCount() == 0) {
            persenter.loadAttentionArticles(app.getLoginUser().getId());
        }
    }

    @Override
    public void showLoadingDialog() {
        swipeLayout.setRefreshing(true);
    }

    @Override
    public void hideLoadingDialog() {
        swipeLayout.setRefreshing(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onStop() {
        stopVideoPlay();
        super.onStop();
    }

    public void stopVideoPlay(){
        //移除当前的播放
        adapter.removePlayer();
    }

    @Override
    public void showArticles(List<Article> list) {
        //加载的数量
        int loadSize = list.size();
        //将加载到的内容显示出来(去掉重复内容)
        boolean r = adapter.addList(list, page);
        if (!r) {
            showToast("没有更多数据了");
        } else {
            listArticle.smoothScrollToPosition(0);
        }
        if (loadSize == 20) {
            page++;
        }
    }

}
