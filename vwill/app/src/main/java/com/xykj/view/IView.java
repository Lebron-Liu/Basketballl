package com.xykj.view;

public interface IView {

    /**
     * 显示加载框
     */
    void showLoadingDialog();

    /**
     * 吐司提醒(当处理出现错误或者异常时提示)
     * @param msg
     */
    void showToast(String msg);

    /**
     * 隐藏加载框(处理结束时使用)
     */
    void hideLoadingDialog();
}
