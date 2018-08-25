package com.xykj.rxandroidandretrofit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Observable为被观察者，Observer为观察者，Observable发送消息，而Observer用于消费消息，
 * 在实际开发中，更多的是选择Observer的一个子类Subscriber来消费消息。在消息发送的过程中，
 * Observable可以发送任意数量任意类型的消息，当Observable所发送的消息被成功处理或者消息
 * 出错时，一个流程结束。
 * Observable会用它的每一个Subscriber（观察者）的onNext方法进行消息处理，在消息成功处理
 * 后以onComplete()方法结束流程，如果消息在处理的过程中出现了任何异常，则以onError()方法
 * 结束流程。
 */
public class RxAndroidActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RxAndroid";
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_android);
        iv = (ImageView) findViewById(R.id.iv);
        findViewById(R.id.btn_create).setOnClickListener(this);
        findViewById(R.id.btn_from).setOnClickListener(this);
        findViewById(R.id.btn_just).setOnClickListener(this);
        findViewById(R.id.btn_map).setOnClickListener(this);
        findViewById(R.id.btn_flatmap).setOnClickListener(this);
        findViewById(R.id.btn_merge).setOnClickListener(this);
        findViewById(R.id.btn_on).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_create:
                //create方法
                methodCreate();
                break;
            case R.id.btn_from:
                //from方法
                methodFrom();
                break;
            case R.id.btn_just:
                //just方法
                methodJust();
                break;
            case R.id.btn_map:
                //map方法
                methodMap();
                break;
            case R.id.btn_flatmap:
                //flatMap方法
                methodFlatMap();
                break;
            case R.id.btn_merge:
                //merge方法
                methodMerge();
                break;
            case R.id.btn_on:
                //线程控制
                methodThreadControll();
                break;
        }
    }

    private void methodCreate() {
        //创建被观察对象
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                //业务处理流程
                e.onNext("Hello");
                e.onNext("World");
                e.onComplete();
            }

        }).subscribe(new Observer<String>() { //观察者消费事务
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onCompleted: onCompleted()");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError:onError() ");
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "onNext: " + s);
            }
        });
        //输出：  Hello
        //       World
    }

    /**
     * Observale中的from函数接受一个数组，
     * 这个方法返回一个按数组元素的顺序来发射这些数据的Observale
     */
    int count = 0;

    //可采用 Disposable.dispose() 切断观察者与被观察者之间的连接
    private void methodFrom() {
        Observable.fromArray(new String[]{"秋水共长天一色", "落霞与孤鹜齐飞", "长河落日圆"})
                .subscribe(new Observer<String>() {
                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError："+e.getMessage() );
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete" );
                    }

                    @Override
                    public void onNext(String s) {
                        count++;
                        if (count == 2) {
                            //设置在接收到第二个事件后切断观察者和被观察者的连接
                            disposable.dispose();
                        }
                        Log.d(TAG, "onNext: " + s);
                    }
                });
        //输出: 秋水共长天一色
        //      落霞与孤鹜齐飞
    }

    /**
     * just函数它接受最多10个参数，返回一个按参数顺序发射这些数据的Observable
     */
    private void methodJust() {
        Observable.just("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
                //Observable还可以通过以下方法控制发射元素数量
//                //指定某个具体位置的元素
//                .elementAt(4)
//                //只发射前N个元素
//                .take(2)
//                //只发射最后N个元素
//                .takeLast(2)
//                //只发射第一个元素
//                .first()
//                //只发射最后一个元素
//                .last()
//                //跳过前两个
//                .skip(2)
//                //跳过最后两个
//                .skipLast(2)
//                //数据过滤，过滤掉重复数据
//                .distinct()
                .subscribe(new Observer<String>() {
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
                    public void onNext(String s) {
                        Log.d(TAG, "onNext: " + s);
                    }
                });
    }

    /**
     * map函数可以对Observable创建的原始数据进行二次加工，然后再被观察者获取
     */
    private void methodMap() {
        Observable.fromArray(new String[]{"1", "2"})
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        return s + "-abc";
                    }

                })
                .subscribe(new Observer<String>() {
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
                    public void onNext(String s) {
                        Log.d(TAG, "onNext: " + s);
                    }
                });
        //输出  1-abc
        //      2-abc
    }

    /**
     * flatMap函数接受一个Observable函数作为输入函数，然后在这
     * 个输入的基础上再创建一个新的Observable进行输出
     */
    private void methodFlatMap() {
        Observable.just("111", "222")
                .flatMap(new Function<String, Observable<String>>() {
                    @Override
                    public Observable<String> apply(String s) throws Exception {
                        return Observable.fromArray(new String[]{s + "aaa", s + "bbb"});
                    }
                }).subscribe(new Observer<String>() {
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
            public void onNext(String s) {
                Log.d(TAG, "onNext: " + s);
            }
        });
//输出  //  111aaa
        //  111bbb
        //  222aaa
        //  222bbb
    }

    /**
     * merge函数可以用来合并多个Observable数据源
     * zip也可以合并，并且可以实现合并后二次修改
     */
    private void methodMerge() {
        Observable<String> observable1 = Observable.just("1", "2");
        Observable<String> observable2 = Observable.just("3", "4");
        Observable.merge(observable1, observable2)
                .subscribe(new Observer<String>() {
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
                    public void onNext(String s) {
                        Log.d(TAG, "onNext: " + s);
                    }
                });
        //输出
        // 1
        // 2
        // 3
        // 4
    }

    /**
     * subscribeOn表示指定被观察者执行的线程，而observeOn则表示观察者执行的线程
     * Schedulers.io()表示子线程处理，AndroidSchedulers.mainThread()表示主线程
     */
    private void methodThreadControll() {
        Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(ObservableEmitter<Bitmap> e) throws Exception {
                e.onNext(getBitmap());
            }

        })
                //设置数据加载在子线程进行
                .subscribeOn(Schedulers.io())
                //设置图片加载在主线程进行
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bitmap>() {
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
                    public void onNext(Bitmap bitmap) {
                        iv.setImageBitmap(bitmap);
                    }
                });
    }

    private Bitmap getBitmap() {
        HttpURLConnection con;
        try {
            URL url = new URL("http://www.bz55.com/uploads/allimg/110824/1H15C104-12.jpg");
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5 * 1000);
            con.connect();
            if (con.getResponseCode() == 200) {
                return BitmapFactory.decodeStream(con.getInputStream());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
