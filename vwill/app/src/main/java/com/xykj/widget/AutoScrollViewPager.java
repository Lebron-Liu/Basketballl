package com.xykj.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 自动轮播，提供一个子线程，让子线程每3秒发送一个改变下标的消息，主线程收到之后给变ViewPager显示下标
 */
public class AutoScrollViewPager extends ViewPager {
    public AutoScrollViewPager(@NonNull Context context) {
        super(context);
    }

    public AutoScrollViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            //如果监听到用户触摸ViewPager(按下，表示用户要自己拖动)，此时停止程序自动轮播
            case MotionEvent.ACTION_DOWN:
                stopLoop();
                break;
            //如果监听到用户手指离开，恢复轮播
            case MotionEvent.ACTION_UP:
                startLoop();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private Thread mThread;
    //更新下标方向
    private int offset = 1;

    public void startLoop() {
        if (null != mThread && mThread.isAlive()) {
            return;
        }
        mThread = new Thread() {
            @Override
            public void run() {
                try {
                    int count = getAdapter().getCount();
                    while (true) {
                        Thread.sleep(3000);
                        //更新下标
                        //取当前的下标
                        int index = getCurrentItem();
                        //计算下一个下标
                        int nextIndex = index + offset;
                        //当页面已经到了第0页则下次切到1,2,3
                        if (nextIndex <= 0) {
                            nextIndex = 0;
                            offset = 1;
                        }
                        //如果页面已经到了最后一个，则下次为前一页,-1 -1
                        else if (nextIndex >= count - 1) {
                            nextIndex = count - 1;
                            offset = -1;
                        }
                        mHandler.obtainMessage(1, nextIndex).sendToTarget();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        mThread.start();
    }

    public void stopLoop() {
        if (null != mThread && mThread.isAlive()) {
            mThread.interrupt();
            mThread = null;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int newIndex = (int) msg.obj;
            setCurrentItem(newIndex, true);
        }
    };
}
