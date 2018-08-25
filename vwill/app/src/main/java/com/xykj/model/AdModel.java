package com.xykj.model;

import com.alibaba.fastjson.JSON;
import com.xykj.bean.AdItem;
import com.xyy.net.NetManager;
import com.xyy.net.RequestItem;
import com.xyy.net.ResponceItem;
import com.xyy.net.imp.Callback;

import java.util.List;

public class AdModel extends IModel<List<AdItem>> {
    public AdModel(RequestItem request) {
        super(request);
    }

    @Override
    public void execute(final ICallback<List<AdItem>> callback) {
        callback.onStart();
        NetManager.getInstance().execute(request, new Callback<List<AdItem>>() {
            @Override
            public List<AdItem> changeData(ResponceItem responce) {
                String json = responce.getString();
                if (json.startsWith("[")) {
                    List<AdItem> list = JSON.parseArray(json, AdItem.class);
                    return list;
                }
                return null;
            }

            @Override
            public void onResult(List<AdItem> result) {
                if (null != result) {
                    callback.onSuccess(result);
                } else {
                    callback.onFail("广告加载失败");
                }
                callback.onComplete();
            }
        });
    }
}
