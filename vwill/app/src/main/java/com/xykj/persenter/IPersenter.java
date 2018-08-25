package com.xykj.persenter;

import com.xykj.view.IView;

public class IPersenter<T extends IView> {
    public T view;

    public void registView(T view) {
        this.view = view;
    }

    public void unRegistView() {
        this.view = null;
    }

    public boolean isViewAvailable() {
        return view != null;
    }
}
