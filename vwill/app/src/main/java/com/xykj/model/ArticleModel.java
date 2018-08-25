package com.xykj.model;

import com.alibaba.fastjson.JSON;
import com.xykj.bean.Article;
import com.xyy.net.NetManager;
import com.xyy.net.RequestItem;
import com.xyy.net.ResponceItem;
import com.xyy.net.imp.Callback;

import java.io.FileOutputStream;
import java.util.List;

public class ArticleModel extends IModel<List<Article>> {
    public ArticleModel(RequestItem request) {
        super(request);
    }

    @Override
    public void execute(final ICallback<List<Article>> callback) {
        callback.onStart();
        NetManager.getInstance().execute(request, new Callback<List<Article>>() {
            @Override
            public List<Article> changeData(ResponceItem responce) {
                String json = responce.getString();
                if(json.startsWith("[")){
                    List<Article> list = JSON.parseArray(json,Article.class);
                    return list;
                }
                return null;
            }

            @Override
            public void onResult(List<Article> result) {
                if(result!=null && result.size()>0){
                    callback.onSuccess(result);
                }else{
                    callback.onFail("暂无数据");
                }
                callback.onComplete();
            }
        });
    }
}
