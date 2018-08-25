package com.xykj.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class XGridView extends GridView {
    public XGridView(Context context) {
        super(context);
    }

    public XGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //让当前视图的高度尽可能高刚好把其所有孩子括起来
        int height = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, height);
    }
}
