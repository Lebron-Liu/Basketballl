package com.xykj.adapter;

import android.content.Context;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.xyy.utils.XImageLoader;

import java.util.List;

public class ImageAdapter extends ItemAdapter<String> {
    private int itemWidth;

    public ImageAdapter(Context context, List<String> list) {
        super(context);
        this.list = list;
        itemWidth = context.getResources().getDisplayMetrics().widthPixels / 3;
        itemWidth -= 10;
    }

    @Override
    protected ItemAdapter.ViewHolder createHolder(int type) {
        ImageView iv = new ImageView(context);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(itemWidth, itemWidth);
        iv.setLayoutParams(lp);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new ViewHolder(iv);
    }

    @Override
    protected void bindView(String s, ItemAdapter.ViewHolder holder) {
        XImageLoader.getInstance(context).showImage(s, (ImageView) holder.itemView,200,200,0,false);
    }
}
