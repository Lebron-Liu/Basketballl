package com.xyy.net.imp;

/**
 * Created by Administrator on 2016/12/27.
 */
public interface ProgressCallback extends Callback<Object>{
    void onProgress(int current, int total);
}
