package com.xykj.model;

import com.xyy.net.NetManager;
import com.xyy.net.RequestItem;
import com.xyy.net.ResponceItem;
import com.xyy.net.imp.Callback;

import org.json.JSONException;
import org.json.JSONObject;

public class ArticlePublishModel extends IModel<Boolean> {
    public ArticlePublishModel(RequestItem request) {
        super(request);
    }

    @Override
    public void execute(final ICallback<Boolean> callback) {
        callback.onStart();
        NetManager.getInstance().execute(request, new Callback<Boolean>() {
            @Override
            public Boolean changeData(ResponceItem responce) {
                String json = responce.getString();
                try {
                    JSONObject obj = new JSONObject(json);
                    return obj.optInt("result") == 1;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onResult(Boolean result) {
                if (result != null && result) {
                    callback.onSuccess(true);
                }else{
                    callback.onFail("提交失败");
                }
                callback.onComplete();
            }
        });
    }
}
