package com.xykj.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.xykj.fragments.ActiveByUserFragment;
import com.xykj.fragments.ArticleByUserFragment;
import com.xykj.view.BaseFragment;

public class UserCenterPagerAdapter extends FragmentPagerAdapter {
    String[] title = {"发布的文章", "发布的活动"};
    BaseFragment[] fs;

    public UserCenterPagerAdapter(FragmentManager fm, int userId) {
        super(fm);
        fs = new BaseFragment[]{
                ArticleByUserFragment.getInstance(userId), ActiveByUserFragment.getInstance(userId)};
    }

    @Override
    public Fragment getItem(int i) {
        return fs[i];
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }
}
