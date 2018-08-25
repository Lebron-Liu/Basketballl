package com.xykj.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xykj.bean.Active;
import com.xykj.persenter.ActivePersonter;
import com.xykj.view.ActiveView;
import com.xykj.view.BaseFragment;
import com.xykj.vwill.R;

import java.util.List;

public class ActiveByUserFragment extends BaseFragment<ActivePersonter> implements ActiveView {
    public static ActiveByUserFragment getInstance(int userId) {
        ActiveByUserFragment f = new ActiveByUserFragment();
        Bundle b = new Bundle();
        b.putInt("userId", userId);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        RecyclerView mRecyler = view.findViewById(R.id.m_recycler);
        mRecyler.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    @Override
    public void showActiveList(List<Active> list) {

    }
}
