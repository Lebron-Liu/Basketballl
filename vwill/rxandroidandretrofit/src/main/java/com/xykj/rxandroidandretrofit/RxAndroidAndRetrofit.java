package com.xykj.rxandroidandretrofit;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.xykj.model.Article;
import com.xykj.model.imp.RxDataServer;
import com.xykj.utils.HttpManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RxAndroidAndRetrofit extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RxAndroidAndRetrofit";
    private RxDataServer server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_android_and_retrofit);
        findViewById(R.id.btn_get).setOnClickListener(this);
        findViewById(R.id.btn_post).setOnClickListener(this);
        findViewById(R.id.btn_upload).setOnClickListener(this);
        findViewById(R.id.btn_interval).setOnClickListener(this);
        server = HttpManager.getInstance(getApplicationContext()).getRxDataServer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get:
                getTest();
                break;
            case R.id.btn_post:

                break;
            case R.id.btn_upload:

                break;
            case R.id.btn_interval:
                intervalTest();
                break;
        }
    }

    private void getTest() {
        Observable<List<Article>> observable = server.getArticles(1);
        observable
                //在子线程访问数据
                .subscribeOn(Schedulers.io())
                //在主线程显示数据
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Article>>() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Article> articles) {
                        if (null != articles) {
                            Log.e(TAG, "==>size:" + articles.size() + " ==>" + Thread.currentThread().getName());
                        }
                    }
                });
    }

    private void intervalTest(){
        //若要实现有限次轮询，仅需将interval()改成intervalRange()即可
        //参数(第一次延迟时间，间隔时间，单位)
        Observable.interval(2,1, TimeUnit.SECONDS)
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        //收到定时器的触发
                    }
                }).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Long value) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
}
