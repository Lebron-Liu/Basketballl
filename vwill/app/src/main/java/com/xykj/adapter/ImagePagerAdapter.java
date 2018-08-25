package com.xykj.adapter;

import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.xykj.widget.XImageView;
import com.xykj.widget.XPagerAdapter;
import com.xyy.utils.XImageLoader;

public class ImagePagerAdapter extends XPagerAdapter<String> {
    private Point screenSize;
    public ImagePagerAdapter(Context context) {
        super(context);
        screenSize = new Point();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(screenSize);
    }

    @Override
    protected View createView() {
        XImageView iv = new XImageView(context);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        iv.setLayoutParams(lp);
        iv.setBackgroundColor(0xff000000);
        return iv;
    }

    @Override
    protected void bindView(View itemView, String item) {
        XImageLoader.getInstance(context).showImage(item, (XImageView) itemView,screenSize.x,screenSize.y,0,true);
    }
}
