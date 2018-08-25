package com.xykj.model;

import com.alibaba.fastjson.JSON;
import com.xykj.bean.Reply;
import com.xyy.net.NetManager;
import com.xyy.net.RequestItem;
import com.xyy.net.ResponceItem;
import com.xyy.net.imp.Callback;

import java.util.LinkedList;
import java.util.List;

public class ReplyModel extends IModel<List<Object>> {
    private boolean isLoadHotReply;

    public ReplyModel(RequestItem request, boolean isLoadHotReply) {
        super(request);
        this.isLoadHotReply = isLoadHotReply;
    }

    @Override
    public void execute(final ICallback<List<Object>> callback) {
        if (!isLoadHotReply) {
            callback.onStart();
        }
        NetManager.getInstance().execute(request, new Callback<List<Object>>() {
            @Override
            public List<Object> changeData(ResponceItem responce) {
                String jsonStr = responce.getString();
                if (jsonStr.startsWith("[")) {
                    List<Reply> list = JSON.parseArray(jsonStr, Reply.class);
                    //转换数据
                    List<Object> result = new LinkedList<>();
                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        Reply currentReply = list.get(i);
                        //检测是否有@别人
                        if (currentReply.getParent() != 0) {
                            List<Reply> atList = new LinkedList<>();
                            atList.add(currentReply);
                            //当前要找的parentId
                            int p = currentReply.getParent();
                            //找到所@的人，添加到@关系列表中
                            int index = i + 1;
                            while (index < size) {
                                //是否是被@的那个评论
                                Reply r = list.get(index);
                                if (p == r.getId()) {
                                    //找到了
                                    atList.add(r);
                                    p = r.getParent();
                                    if (p == 0) {
                                        break;
                                    }
                                }
                                index++;
                            }
                            result.add(atList);
                        } else {
                            //没有@别人，单独显示
                            result.add(currentReply);
                        }
                    }
                    return result;
                }
                return null;
            }

            @Override
            public void onResult(List<Object> result) {
                if (null != result && result.size() > 0) {
                    callback.onSuccess(result);
                }
                if (!isLoadHotReply) {
                    callback.onComplete();
                }
            }
        });
    }
}
