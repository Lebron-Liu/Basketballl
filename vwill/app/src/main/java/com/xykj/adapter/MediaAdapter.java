package com.xykj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.xykj.bean.MediaItem;
import com.xykj.utils.Common;
import com.xykj.vwill.R;
import com.xyy.utils.XImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MediaAdapter extends ItemAdapter<MediaItem> {
    private int functionIcon;
    //每个item的宽度
    private int itemWidth;
    protected int type;
    public MediaAdapter(Context context,int functionIcon) {
        super(context);
        this.functionIcon =functionIcon;
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        //将屏幕宽度去掉中间的两个间距(假设为10)均分3等分作为每个item的宽高
        itemWidth = (screenWidth-10)/3;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    protected ViewHolder createHolder(int type) {
        View layout = LayoutInflater.from(context).inflate(R.layout.item_media, null);
        return new MediaHolder(layout);
    }

    @Override
    protected void bindView(MediaItem mediaItem, ViewHolder holder) {
        MediaHolder h= (MediaHolder) holder;
        if(null == mediaItem){
            //显示功能图标
            h.itemIcon.setImageResource(functionIcon);
            h.itemIcon.setScaleType(ImageView.ScaleType.CENTER);
            h.itemChecked.setVisibility(View.GONE);
            h.itemName.setVisibility(View.GONE);
        }else{
            //显示媒体缩略图
            h.itemIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //是音频直接显示默认图标
            if(type == Common.TYPE_AUDIO){
                h.itemIcon.setImageResource(mediaItem.getDefIcon());
                h.itemName.setVisibility(View.VISIBLE);
                h.itemName.setText(mediaItem.getName());
            }else {
                h.itemName.setVisibility(View.GONE);
                XImageLoader.getInstance(context).showImage(mediaItem.getPath(), h.itemIcon, mediaItem.getDefIcon());
            }
            if(mediaItem.isChecked()){
                h.itemChecked.setVisibility(View.VISIBLE);
            }else{
                h.itemChecked.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getCount() {
        return 1+super.getCount();
    }

    @Override
    public MediaItem getItem(int position) {
        if(position == 0 ){
            return null;
        }
        return list.get(position-1);
    }

    class MediaHolder extends ViewHolder {
        @BindView(R.id.item_icon)
        ImageView itemIcon;
        @BindView(R.id.item_checked)
        ImageView itemChecked;
        @BindView(R.id.item_name)
        TextView itemName;

        MediaHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(itemWidth,itemWidth);
            view.setLayoutParams(lp);
        }
    }
}
