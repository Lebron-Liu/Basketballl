package com.xykj.persenter;

import com.xykj.model.ReplyModel;
import com.xykj.utils.Common;
import com.xykj.view.ReplyView;
import com.xyy.net.RequestItem;

import java.util.List;

public class ReplyPersenter extends IPersenter<ReplyView> {

    public void loadNewsRely(int articleId) {
        RequestItem request = new RequestItem.Builder()
                .url(Common.URL_LOAD_NEW_REPLY + "?articleId=" + articleId)
                .build();
        new ReplyModel(request,false).execute(new BaseCallback<List<Object>>(this, view) {
            @Override
            public void onSuccess(List<Object> data) {
                view.showNewReplies(data);
            }
        });
    }

    public void loadHotReply(int articleId) {
        RequestItem request = new RequestItem.Builder()
                .url(Common.URL_LOAD_HOT_REPLY + "?articleId=" + articleId)
                .build();
        new ReplyModel(request,true).execute(new BaseCallback<List<Object>>(this,view) {
            @Override
            public void onSuccess(List<Object> data) {
                view.showHotReplies(data);
            }
        });
    }
}
