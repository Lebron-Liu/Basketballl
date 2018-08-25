package com.xykj.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.xykj.adapter.AdAdapter;
import com.xykj.adapter.FunctionAdapter;
import com.xykj.bean.AdItem;
import com.xykj.utils.Common;
import com.xykj.view.AdView;
import com.xykj.view.BaseFragment;
import com.xykj.view.LiveCenterView;
import com.xykj.vwill.LiveCenterActivity;
import com.xykj.vwill.R;
import com.xykj.vwill.VWillApp;
import com.xykj.widget.AutoScrollViewPager;
import com.xykj.widget.XGridView;
import com.xyy.utils.TipsUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class FoundFragment extends BaseFragment<AdPersenter> implements AdView {

    @BindView(R.id.pager_ad)
    AutoScrollViewPager pagerAd;
    @BindView(R.id.layout_indicator)
    LinearLayout layoutIndicator;
    @BindView(R.id.grid_function)
    XGridView gridFunction;
    Unbinder unbinder;
    private AdAdapter adAdapter;
    private FunctionAdapter functionAdapter;
    private int screenWidth;
    //上一个显示的页面下标
    private int lastIndex;
    private VWillApp app;

    public FoundFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (VWillApp) getActivity().getApplication();
        screenWidth = getActivity().getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_found, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //广告ViewPager设置适配器
        //如果还没有数据，启动加载数据
        if (null == adAdapter) {
            adAdapter = new AdAdapter(getActivity());
            persenter.loadAd(2, 2);
            functionAdapter = new FunctionAdapter(getActivity());
        } else {
            initIndicator(adAdapter.getCount());
            pagerAd.startLoop();
        }
        pagerAd.setAdapter(adAdapter);
        gridFunction.setAdapter(functionAdapter);

        pagerAd.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                //将之前选中的恢复回去
                layoutIndicator.getChildAt(lastIndex).setSelected(false);
                //将当前的设置选中
                layoutIndicator.getChildAt(i).setSelected(true);
                //将当前的显示下标记录为上一个
                lastIndex = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        gridFunction.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        //附近人(需要用户登录)
                        break;
                    case 1:
                        //附近活动
                        break;
                    case 2:
                        //周边美食
                        break;
                    case 3:
                        //直播间
                        Intent it1 = new Intent(getActivity(), LiveCenterActivity.class);
                        startActivity(it1);
                        break;
                    case 4:
                        //附近文章
                        break;
                    case 5:
                        //接朋友
                        if (app.isLogin()) {
                            String msg = "您的好友正在使用VWill“接人”功能来获取您的位置，请点击该链接给他(她)发送您的位置:"
                                    + Common.URL_GET_USER_LOC + "?u_id=" + app.getLoginUser().getId();
                            Intent it = new Intent(Intent.ACTION_SEND);
                            it.setType("text/plain");
                            it.putExtra(Intent.EXTRA_TEXT, msg);
                            Intent targetIntent = Intent.createChooser(it, "发送给好友");
                            if (targetIntent != null) {
                                startActivity(targetIntent);
                            } else {
                                TipsUtil.toast(getActivity(), "未找到可以发送的应用");
                            }
                        } else {
                            TipsUtil.toast(getActivity(), "该功能需要登录后才能使用");
                        }
                        break;

                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        pagerAd.stopLoop();
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void showAd(List<AdItem> list) {
        adAdapter.setList(list);
        pagerAd.startLoop();
        //初始化指示器
        int size = list.size();
        initIndicator(size);
        //让第0个选中
        layoutIndicator.getChildAt(0).setSelected(true);
    }

    private void initIndicator(int size) {
        //每个指示器之间的间距
        int spacing = 10;
        //计算每个指示器的宽度
        int w = (screenWidth - spacing * (size + 1)) / size;
        for (int i = 0; i < size; i++) {
            View v = new View(getActivity());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(w, 10);
            lp.leftMargin = spacing;
            v.setLayoutParams(lp);
            v.setBackgroundResource(R.drawable.ad_pager_indicator);
            layoutIndicator.addView(v);
        }
    }

}
