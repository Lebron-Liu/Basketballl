package com.xyy.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xyy.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by huajian.zhang on 2016/11/2.
 * <h3>标签选择布局</h3>
 * <p>将内容按行排列，如果该行已经不能存放新加入的标签则换行</p>
 */
public class FloatingLayout extends ViewGroup {
    //子视图已存放的位置
    private SparseArray<LinkedList<Rect>> childenPos;

    //记录每行的最大高度
    private SparseArray<Integer> linesHeight;

    //子视图所在的行号
    private List<Integer> childLayoutInLineNum;

    private int layoutWidth;

    private int itemHorizontalMargin = 10;
    private int itemVerticalMargin = 10;
    private Context context;

    public FloatingLayout(Context context) {
        super(context);
        init(context);
    }

    public FloatingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        childenPos = new SparseArray<LinkedList<Rect>>();
        linesHeight = new SparseArray<Integer>();
        childLayoutInLineNum = new ArrayList<Integer>();
        this.context = context;
    }

    private OnClickListener itemOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (null != onItemCheckedChangeListener) {
                int position = (int) v.getTag();
                if (onItemCheckedChangeListener.isCanCheck(position, v.isSelected())) {
                    v.setSelected(!v.isSelected());
                    onItemCheckedChangeListener.onItemCheckedChange(position, v.isSelected());
                }
            } else {
                v.setSelected(!v.isSelected());
            }
        }
    };

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int size = getChildCount();
        for (int i = 0; i < size; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            int index = childLayoutInLineNum.get(i);
            Rect rect = childenPos.get(index).removeFirst();
            child.layout(rect.left, rect.top + getPaddingTop(), rect.right, rect.bottom + getPaddingTop());
        }
    }

    private int computeChildPos(View childView) {
        //记录可显示区域最右侧位置
        int maxRight = 0;
        //记录可显示区域最顶部位置
        int minTop = 0;
        int childWidth = childView.getMeasuredWidth();
        int childHeight = childView.getMeasuredHeight();
        int size = childenPos.size();
        int index = 0;
        boolean isFound = false;
        if (size > 0) {
            while (index < size) {
                //取当前行的最后一个
                Rect currentRect = childenPos.get(index).getLast();
                //当前已经布置的子视图的右侧位置
                int right = currentRect.right + itemHorizontalMargin;
                //即将布置的子视图的右侧位置
                int childRight = right + childWidth;
                if (childRight > layoutWidth) {
                    //换下一行
                    maxRight = 0;
                } else {
                    if (maxRight < right) {
                        maxRight = right;
                        minTop = currentRect.top;
                        isFound = true;
                        break;
                    }
                }
                index++;
            }
        }
        if (!isFound && size > 0) {
            //取前一行第一个子视图的底部
            int preBottom = childenPos.get(index - 1).get(0).bottom;
            minTop = getMinTop(maxRight, maxRight + childWidth, childHeight, preBottom) + itemVerticalMargin;
        }
        Rect r = new Rect(maxRight, minTop, maxRight + childWidth, minTop + childHeight);
        if (null == childenPos.get(index)) {
            childenPos.put(index, new LinkedList<Rect>());
        }
        childenPos.get(index).addLast(r);
        return index;
    }

    //获取当前布置的视图可以存放的最小顶部(也就是目前布局中底部最低的点)
    private int getMinTop(int left, int right, int height, int preLineBottom) {
        //从前一行的底部开始往下算，找到最合适放置的顶部位置
        int min = preLineBottom;
        Rect r = new Rect(left, min, right, min + height);
        while (isChildrenContain(r)) {
            min++;
            r.top = min;
            r.bottom = min + height;
        }
        return min;
    }

    //检测新布置位置和目前已经布置的孩子是否有交集
    private boolean isChildrenContain(Rect rect) {
        int size = childenPos.size();
        for (int i = 0; i < size; i++) {
            int lineSize = childenPos.get(i).size();
            for (int j = 0; j < lineSize; j++) {
                Rect r = childenPos.get(i).get(j);
                if (Rect.intersects(rect, r)) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        childenPos.clear();
        linesHeight.clear();
        childLayoutInLineNum.clear();
        //int paddingLeft = getPaddingLeft();
        //int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        int wantHeight = 0;
        //该容器宽度
        layoutWidth = resolveSize(0, widthMeasureSpec);
        int size = getChildCount();
        for (int i = 0; i < size; i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() == View.GONE) {
                continue;
            }

            //LayoutParams params = childView.getLayoutParams();
            //childView.measure(
            //        getChildMeasureSpec(widthMeasureSpec, paddingLeft + paddingRight, params.width),
            //        getChildMeasureSpec(heightMeasureSpec, paddingTop + paddingBottom, params.height)
            //);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);

            int index = computeChildPos(childView);
            childLayoutInLineNum.add(index);
            //获取最新计算出位置的子视图区域
            Rect rect = childenPos.get(index).getLast();
            int currentChildHeight = rect.height() + itemVerticalMargin;
            if (null == linesHeight.get(index)) {
                linesHeight.put(index, currentChildHeight);
            } else {
                int h = Math.max(linesHeight.get(index), currentChildHeight);
                if (h != linesHeight.get(index)) {
                    linesHeight.put(index, h);
                }
            }
        }
        wantHeight = getAllLineHeight();
        wantHeight += paddingBottom + paddingTop;
        setMeasuredDimension(layoutWidth, resolveSize(wantHeight, heightMeasureSpec));
    }

    //获取所有行总高度
    private int getAllLineHeight() {
        int size = linesHeight.size();
        int sum = 0;
        for (int i = 0; i < size; i++) {
            sum += linesHeight.get(i);
        }
        return sum;
    }

    public void setChildViews(List<View> childViews) {
        if (getChildCount() > 0) {
            removeAllViews();
        }
        int size = childViews.size();
        for (int i = 0; i < size; i++) {
            addView(childViews.get(i));
        }
        if (isShown()) {
            requestLayout();
        }

    }

    public void setTags(List<String> list) {
        if (getChildCount() > 0) {
            removeAllViews();
        }
        if (null != list) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                createTagView(list.get(i), i);
            }
        }
    }

    private void createTagView(String str, int position) {
        createTagView(str, position, false);
    }

    private void createTagView(String str, int position, boolean isSelect) {
        TextView tv = new TextView(context);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(lp);
        tv.setText(str);
        tv.setTextSize(20);
        ColorStateList csl = (ColorStateList) context.getResources().getColorStateList(R.color.tag_tx_color);
        tv.setTextColor(csl);
        tv.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.tag_bg));
        tv.setTag(position);
        tv.setSelected(isSelect);
        addView(tv);
        tv.setOnClickListener(itemOnClick);
    }

    /**
     * 接收自定义类型标签
     *
     * @param list   标签列表
     * @param method 能描述标签的字符串方法
     * @param <T>
     */
    public <T> void setTags(List<T> list, String method) {
        if (getChildCount() > 0) {
            removeAllViews();
        }
        addTags(list, method);
    }

    /**
     * 添加标签
     *
     * @param list
     * @param method
     * @param <T>
     */
    public <T> void addTags(List<T> list, String method) {
        int childCount = getChildCount();
        if (null != list) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                T t = list.get(i);
                Class cls = t.getClass();
                try {
                    Method m = cls.getDeclaredMethod(method);
                    String str = (String) m.invoke(t);
                    createTagView(str, childCount + i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 添加标签方法
     *
     * @param t        添加的标签对象
     * @param method   标签名获取的方法
     * @param isSelect 标签是否被选中
     * @param <T>
     */
    public <T> void addItem(T t, String method, boolean isSelect) {
        Class cls = t.getClass();
        try {
            Method m = cls.getDeclaredMethod(method);
            String str = (String) m.invoke(t);
            int pos = getChildCount();
            createTagView(str, pos, isSelect);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnItemCheckedChangeListener {
        void onItemCheckedChange(int position, boolean isChecked);

        boolean isCanCheck(int position, boolean currentChecked);
    }

    private OnItemCheckedChangeListener onItemCheckedChangeListener;

    public OnItemCheckedChangeListener getOnItemCheckedChangeListener() {
        return onItemCheckedChangeListener;
    }

    public void setOnItemCheckedChangeListener(OnItemCheckedChangeListener onItemCheckedChangeListener) {
        this.onItemCheckedChangeListener = onItemCheckedChangeListener;
    }
}
