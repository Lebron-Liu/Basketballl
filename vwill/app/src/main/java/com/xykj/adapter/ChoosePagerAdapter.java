package com.xykj.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.xykj.fragments.ArticleFragment;

public class ChoosePagerAdapter extends FragmentPagerAdapter {
    private ArticleFragment[] fs;
    private String[] titles;

    public ChoosePagerAdapter(FragmentManager fm, ArticleFragment[] fs, String[] titles) {
        super(fm);
        this.fs = fs;
        this.titles = titles;
    }

    @Override
    public ArticleFragment getItem(int i) {
        return fs[i];
    }

    @Override
    public int getCount() {
        return fs.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
