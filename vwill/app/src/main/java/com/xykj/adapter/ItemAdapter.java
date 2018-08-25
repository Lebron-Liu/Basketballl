package com.xykj.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class ItemAdapter<T> extends BaseAdapter {
    protected List<T> list;
    protected Context context;

    public ItemAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public T getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        //初始化视图
        ViewHolder holder;
        int itemType = getItemViewType(i);
        if (null == convertView || ((holder = (ViewHolder) convertView.getTag()) != null && holder.itemType != itemType)) {
            //创建每个item的显示布局
            holder = createHolder(itemType);
            //记录类型
            holder.itemType = itemType;
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //绑定数据（效果显示）
        bindView(getItem(i), holder);
        holder.position = i;
        return holder.itemView;
    }

    /**
     * 产生每个item的布局(包括各个视图的初始化)
     *
     * @return
     */
    protected abstract ViewHolder createHolder(int itemType);

    /**
     * 绑定数据到Holder上，处理显示效果
     *
     * @param t
     * @param holder
     */
    protected abstract void bindView(T t, ViewHolder holder);

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public static class ViewHolder {
        protected View itemView;
        protected int position;
        private int itemType;

        public ViewHolder(View itemView) {
            this.itemView = itemView;
            itemView.setTag(this);
        }

        public View getItemView() {
            return itemView;
        }

        public void setItemView(View itemView) {
            this.itemView = itemView;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

    }
}
