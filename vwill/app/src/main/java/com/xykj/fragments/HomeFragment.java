package com.xykj.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xykj.utils.Common;
import com.xykj.vwill.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    //记录内容页对象
    private Fragment[] fragments = new Fragment[2];

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(currentIndex == -1) {
            showFragment(0);
        }
    }

    private int currentIndex = -1;
    public void showFragment(int index) {
        if (currentIndex != index) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            //如果已经有旧内容在显示 隐藏旧的hide
            if (currentIndex != -1) {
                ft.hide(fragments[currentIndex]);
            }
            //显示新的：
            if (fragments[index] == null) {
                //第一次显示：创建对象（记录） 添加
                switch (index) {
                    case 0:
                        fragments[0] = new ChooseFragment();
                        break;
                    case 1:
                        fragments[1] = ArticleFragment.newInstance(Common.TYPE_ATTENTION);
                        break;
                }
                ft.add(R.id.home_content, fragments[index]);
            } else {
                //已有对象 显示attach
                ft.show(fragments[index]);
            }
            ft.commit();
            currentIndex = index;
        }
    }

    public void removeAllFragment(){
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        for(int i = 0 ; i < fragments.length;i++){
            if(fragments[i] != null){
                ft.remove(fragments[i]);
                fragments[i] = null;
            }
        }
        ft.commitAllowingStateLoss();
    }


}
