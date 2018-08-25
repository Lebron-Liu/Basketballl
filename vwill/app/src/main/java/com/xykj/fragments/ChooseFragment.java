package com.xykj.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.xykj.adapter.ChoosePagerAdapter;
import com.xykj.utils.Common;
import com.xykj.vwill.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseFragment extends Fragment {


    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.m_pager)
    ViewPager mPager;
    Unbinder unbinder;
    private ChoosePagerAdapter adapter;

    public ChooseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    private int currentIndex;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(null == adapter){
            ArticleFragment[] fs = new ArticleFragment[4];
            fs[0] = ArticleFragment.newInstance(Common.TYPE_TEXT);
            fs[1] = ArticleFragment.newInstance(Common.TYPE_IMAGE);
            fs[2] = ArticleFragment.newInstance(Common.TYPE_VIDEO);
            fs[3] = ArticleFragment.newInstance(Common.TYPE_AUDIO);
            String titles[] = getActivity().getResources().getStringArray(R.array.article_types_title);
            adapter = new ChoosePagerAdapter(getChildFragmentManager(),fs,titles);
            //当界面显示的时候默认先加载“文章”的内容
            mPager.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    adapter.getItem(0).loadData();
                    mPager.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }
        mPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(mPager);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                adapter.getItem(i).loadData();
                if(currentIndex == 2){
                    //从视频页面切换到其他页面，停止视频播放
                    adapter.getItem(2).stopVideoPlay();
                }
                currentIndex = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
