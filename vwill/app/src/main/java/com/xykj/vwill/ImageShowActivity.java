package com.xykj.vwill;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.TextView;

import com.xykj.adapter.ImagePagerAdapter;
import com.xykj.widget.XViewPager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 显示大图预览的界面
 */
public class ImageShowActivity extends AppCompatActivity {

    @BindView(R.id.m_pager)
    XViewPager mPager;
    @BindView(R.id.tv_tips)
    TextView tvTips;
    List<String> urls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_image_show);
        ButterKnife.bind(this);
        Intent it = getIntent();
        //获取显示的图片的地址
        urls = it.getStringArrayListExtra("data");
        //当前默认显示的图片下标
        int index = it.getIntExtra("index", 0);
        ImagePagerAdapter adapter = new ImagePagerAdapter(this);
        adapter.setList(urls);
        mPager.setAdapter(adapter);
        //默认位置的显示
        if (index > 0) {
            mPager.setCurrentItem(index);
        }
        //默认的下标指示
        tvTips.setText((index + 1) + "/" + urls.size());
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                tvTips.setText((i + 1) + "/" + urls.size());
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }
}
