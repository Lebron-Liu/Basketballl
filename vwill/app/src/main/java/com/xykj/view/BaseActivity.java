package com.xykj.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.xykj.persenter.IPersenter;
import com.xykj.utils.Common;
import com.xykj.vwill.VWillBaseActivity;

/**
 * 每个界面关联一个业务对象(IPersenter)处理一个视图(IView)显示
 */
public abstract class BaseActivity<T extends IPersenter> extends VWillBaseActivity implements IView {

    private ProgressDialog dialog;
    //业务对象
    protected T persenter;  //LoginActivity<LoginPersenter> ReplyActivity<ReplyPersenter>

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        persenter = createPersenter();
        super.onCreate(savedInstanceState);
        persenter.registView(this);
    }

    @Override
    protected void onDestroy() {
        persenter.unRegistView();
        super.onDestroy();
    }

    protected T createPersenter() {
        return Common.createObj(this, 0);
    }

    @Override
    public void showLoadingDialog() {
        if (null == dialog) {
            dialog = ProgressDialog.show(this, "", "正在处理中...");
        } else if(!dialog.isShowing()){
            dialog.show();
        }
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideLoadingDialog() {
        dialog.dismiss();
    }
}
