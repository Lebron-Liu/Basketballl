package com.xykj.widget;

import android.content.Context;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class XViewPager extends ViewPager {
    public XViewPager(@NonNull Context context) {
        super(context);
        init();
    }

    public XViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public void setCurrentItem(int item) {
        currentIndex = item;
        super.setCurrentItem(item);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        currentIndex = item;
        super.setCurrentItem(item, smoothScroll);
    }

    //记录当前显示的下标
    private int currentIndex;

    private void init() {
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (currentIndex != position) {
                    XImageView xImageView = (XImageView) ((XPagerAdapter) getAdapter()).getView(currentIndex);
                    xImageView.reset();
                    currentIndex = position;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //上一个点
    private float lastX, lastY;
    //当前显示的图片视图
    private XImageView xImageView;
    //当前图片内容的状态
    private RectF imageRect;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 多点触控时，直接将事件直接传给孩子
        if (ev.getPointerCount() > 1) {
            return false;
        }
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastX = ev.getX();
                lastY = ev.getY();
                //获取当前显示的视图（从适配器中获取）
                xImageView = (XImageView) ((XPagerAdapter) getAdapter()).getView(getCurrentItem());
                break;
            case MotionEvent.ACTION_MOVE:
                //计算水平方向以及垂直方向的偏移
                float cx = ev.getX();
                float cy = ev.getY();
                imageRect = xImageView.getImageRect();
                boolean result;
                //比较水平和垂直的偏移
                if (Math.abs(cx - lastX) > Math.abs(cy - lastY)) {
                    //水平滑动
                    //向右则表示新的x比旧的x大
                    if (cx > lastX) {
                        //检测内容的左边是否在图片视图的区域内容
                        if (imageRect.left >= 0) {
                            //让ViewPager处理切换
                            result = true;
                        } else {
                            result = false;
                        }
                    } else {
                        //向左滑动，检测内容的右边是否在图片视图区域之内
                        if (imageRect.right <= xImageView.getMeasuredWidth()) {
                            result = true;
                        } else {
                            result = false;
                        }
                    }
                } else {
                    //垂直滑动
                    //让图片视图处理内容的移动
                    result = false;
                }
                lastX = cx;
                lastY = cy;
                return result;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
