package com.xykj.model;

/**
 * 数据处理状态接口
 * @param <T>
 */
public interface ICallback<T> {
    /**
     * 开始处理数据
     */
    void onStart();

    /**
     * 处理成功
     * @param data
     */
    void onSuccess(T data);

    /**
     * 处理失败
     * @param msg
     */
    void onFail(String msg);

    /**
     * 处理出现异常
     */
    void onError();

    /**
     * 处理结束
     */
    void onComplete();
}
