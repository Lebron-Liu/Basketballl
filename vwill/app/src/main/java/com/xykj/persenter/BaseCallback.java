package com.xykj.persenter;

import com.xykj.model.ICallback;
import com.xykj.view.IView;

public abstract class BaseCallback<T> implements ICallback<T> {
    private IPersenter p;
    private IView v;

    protected BaseCallback(IPersenter p, IView v) {
        this.p = p;
        this.v = v;
    }

    @Override
    public void onStart() {
        if (p.isViewAvailable()) {
            v.showLoadingDialog();
        }
    }

    @Override
    public void onFail(String msg) {
        if (p.isViewAvailable()) {
            v.showToast(msg);
        }
    }

    @Override
    public void onError() {
        if (p.isViewAvailable()) {
            v.showToast("出现异常");
        }
    }

    @Override
    public void onComplete() {
        if (p.isViewAvailable()) {
            v.hideLoadingDialog();
        }
    }
}
