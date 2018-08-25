package com.xykj.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class XPagerAdapter<T> extends PagerAdapter {
    //显示的视图
    private Map<Integer, View> showViews;
    //缓存队列
    private LinkedList<View> caches;
    protected List<T> list;

    protected Context context;

    public XPagerAdapter(Context context) {
        this.context = context;
        showViews = new HashMap<Integer, View>();
        caches = new LinkedList<View>();
    }

    @Override
    public int getCount() {
        return null == list ? 0 : list.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //获取要移除的视图（之前显示过的）
        View v = showViews.get(position);
        container.removeView(v);
        //将移除的视图对象记录起来
        caches.addLast(v);
        showViews.remove(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //给数据配置显示视图
        View itemView;
        //如果缓存区中有视图，可以直接拿来显示
        if (!caches.isEmpty()) {
            itemView = caches.removeFirst();
        } else {
            itemView = createView();
        }
        //获取要显示的数据
        T t = list.get(position);
        //将数据和显示视图绑定
        bindView(itemView, t);
        itemView.setTag(position);
        container.addView(itemView);
        //将当前显示的视图用一位置记录起来
        showViews.put(position, itemView);
        return itemView;
    }

    /**
     * 创建每个页面需要的视图
     *
     * @return
     */
    protected abstract View createView();

    /**
     * 绑定视图
     *
     * @param itemView
     * @param item
     */
    protected abstract void bindView(View itemView, T item);

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    /**
     * 按位置获取当前显示的视图对象
     * @param position
     * @return
     */
    public View getView(int position) {
        return showViews.get(position);
    }
}
