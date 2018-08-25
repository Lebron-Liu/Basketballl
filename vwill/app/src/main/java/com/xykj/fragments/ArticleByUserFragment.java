package com.xykj.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xykj.adapter.ArticleAdapter;
import com.xykj.bean.Article;
import com.xykj.persenter.ArticlePersenter;
import com.xykj.view.ArticleView;
import com.xykj.view.BaseFragment;
import com.xykj.vwill.R;
import com.xykj.widget.ArticleListener;

import java.util.List;

public class ArticleByUserFragment extends BaseFragment<ArticlePersenter> implements ArticleView {

    public static ArticleByUserFragment getInstance(int userId){
        ArticleByUserFragment f = new ArticleByUserFragment();
        Bundle b = new Bundle();
        b.putInt("userId",userId);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_layout, container, false);
    }

    private ArticleAdapter adapter;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        RecyclerView mRecyler = view.findViewById(R.id.m_recycler);
        mRecyler.setLayoutManager(new LinearLayoutManager(getActivity()));
        if(null == adapter){
            adapter = new ArticleAdapter(getActivity());
            adapter.setOnArticleListener(new ArticleListener(this));
        }
        mRecyler.setAdapter(adapter);
    }

    @Override
    public void showArticles(List<Article> list) {
        adapter.addList(list,1);
    }

    @Override
    public void onResume() {
        super.onResume();
        int userId = getArguments().getInt("userId");
        persenter.loadArticleByUser(userId);
    }
}
