package com.xykj.utils;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xykj.adapter.ImageAdapter;
import com.xykj.vwill.ImageShowActivity;
import com.xykj.widget.XGridView;
import com.xyy.utils.XImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PhotoViewUtil {

    private Context context;
    private int screenWidth;

    public PhotoViewUtil(Context context) {
        this.context = context;
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
    }

    public View createImageShowView(String[] urls) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (urls.length == 1) {
            //单张
            ImageView iv = new ImageView(context);
            iv.setLayoutParams(lp);
            iv.setMinimumHeight(200);
            XImageLoader.getInstance(context).showImage(urls[0], iv, screenWidth, 0,0, false);
            iv.setOnClickListener(onImageClick);
            iv.setTag(urls[0]);
            return iv;
        } else {
            //多张
            XGridView grid = new XGridView(context);
            grid.setNumColumns(3);
            grid.setVerticalSpacing(10);
            grid.setHorizontalSpacing(10);
            grid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            grid.setLayoutParams(lp);
            List<String> images = Arrays.asList(urls);
            grid.setAdapter(new ImageAdapter(context, images));
            grid.setOnItemClickListener(onImageItemClick);
            return grid;
        }
    }

    private View.OnClickListener onImageClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //启动图片预览界面
            String url = (String) view.getTag();
            ArrayList<String> list = new ArrayList<>(1);
            list.add(url);
            Intent it = new Intent(context, ImageShowActivity.class);
            it.putStringArrayListExtra("data", list);
            context.startActivity(it);
        }
    };

    private AdapterView.OnItemClickListener onImageItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            //从适配器中获取所有的图片地址
            List<String> list = ((ImageAdapter) adapterView.getAdapter()).getList();
            //将图片地址当道动态数组中传给图片显示的Activity
            ArrayList<String> images = new ArrayList<>(list.size());
            images.addAll(list);
            Intent it = new Intent(context, ImageShowActivity.class);
            it.putStringArrayListExtra("data", images);
            it.putExtra("index", i);
            context.startActivity(it);
        }
    };
}
