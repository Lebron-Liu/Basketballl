package com.xykj.model;

import com.xyy.net.RequestItem;

/**
 * 数据处理类
 * @param <T>
 */
public abstract class IModel<T> {

    public RequestItem request;

    public IModel(RequestItem request) {
        this.request = request;
    }

    public abstract void execute(ICallback<T> callback);
}
