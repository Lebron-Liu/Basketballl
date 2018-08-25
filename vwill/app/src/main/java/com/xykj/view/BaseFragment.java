package com.xykj.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.xykj.persenter.IPersenter;
import com.xykj.utils.Common;

public abstract class BaseFragment<T extends IPersenter> extends Fragment implements IView {
    private ProgressDialog dialog;
    //业务对象
    protected T persenter;  //LoginActivity<LoginPersenter> ReplyActivity<ReplyPersenter>

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        persenter = createPersenter();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        persenter.registView(this);
    }

    @Override
    public void onDestroyView() {
        persenter.unRegistView();
        super.onDestroyView();
    }

    protected T createPersenter(){
        return Common.createObj(this,0);
    }

    @Override
    public void showLoadingDialog() {
        if(null == dialog){
            dialog = ProgressDialog.show(getActivity(),"","正在处理中...");
        }else{
            dialog.show();
        }
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideLoadingDialog() {
        dialog.dismiss();
    }
}
