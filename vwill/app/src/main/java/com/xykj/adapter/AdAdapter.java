package com.xykj.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xykj.bean.AdItem;
import com.xykj.utils.Common;
import com.xykj.vwill.R;
import com.xykj.widget.XPagerAdapter;
import com.xyy.utils.XImageLoader;

public class AdAdapter extends XPagerAdapter<AdItem> {
    private int screenWidth;

    public AdAdapter(Context context) {
        super(context);
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    protected View createView() {
        ImageView iv = new ImageView(context);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        iv.setLayoutParams(lp);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        iv.setOnClickListener(onClickListener);
        return iv;
    }

    @Override
    protected void bindView(View itemView, AdItem item) {
        ImageView iv = (ImageView) itemView;
        XImageLoader.getInstance(context).showImage(Common.SERVER_URL + item.getPhoto(), iv, screenWidth, 0, R.drawable.filesystem_icon_photo, false);
        iv.setTag(item.getUrl());
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String url = (String) view.getTag();
            //启动WebActivity(WebView加载网页)

        }
    };
}
