package com.xyy.net.imp;

import com.xyy.net.ResponceItem;

/**
 * 网络请求返回数据的回调
 */
public interface Callback<T> {
    /**
     * 将网络数据转为对象(子线程中)
     * @param responce
     * @return
     */
    T changeData(ResponceItem responce);

    /**
     * 提交交过的地方(主线程)
     * @param result
     */
    void onResult(T result);
}
