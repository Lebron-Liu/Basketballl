package com.xykj.view;

import com.xykj.bean.AdItem;

import java.util.List;

public interface AdView extends IView {
    void showAd(List<AdItem> list);
}
