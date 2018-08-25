package com.xykj.adapter;

import android.content.Context;

import com.xykj.bean.MediaItem;

public class MediaAdapterInPubish extends MediaAdapter {
    //最大数量，图片9，视频1，音频2
    private int maxSize;

    public MediaAdapterInPubish(Context context, int functionIcon) {
        super(context, functionIcon);
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public int getCount() {
        int dataSize = list == null ? 0 : list.size();
        if (dataSize == maxSize) {
            return dataSize;
        }
        return super.getCount();
    }

    @Override
    public MediaItem getItem(int position) {
        int dataSize = list == null ? 0 : list.size();
        if (dataSize == maxSize) {
            return list.get(position);
        }else if(position<dataSize){
            return list.get(position);
        }else{
            return null;
        }
    }
}
