package com.xykj.fragments;

import com.xykj.bean.AdItem;
import com.xykj.model.AdModel;
import com.xykj.persenter.BaseCallback;
import com.xykj.persenter.IPersenter;
import com.xykj.utils.Common;
import com.xykj.view.AdView;
import com.xyy.net.RequestItem;

import java.util.List;

public class AdPersenter extends IPersenter<AdView> {

    public void loadAd(int type, int num) {
        RequestItem reqest = new RequestItem.Builder()
                .url(Common.URL_LOAD_AD + "?type=" + type + "&num=" + num)
                .build();
        new AdModel(reqest).execute(new BaseCallback<List<AdItem>>(this, view) {
            @Override
            public void onSuccess(List<AdItem> data) {
                view.showAd(data);
            }
        });

    }
}
