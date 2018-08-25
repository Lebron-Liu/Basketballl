package com.xykj.bledemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by Administrator on 2017/4/12.
 */

public class ColorImageView extends ImageView {

    Bitmap bmp;

    public ColorImageView(Context context) {
        super(context);
        init();
    }

    private void init() {
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.n_image_colorful);
        setImageBitmap(bmp);
    }

    public ColorImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (x < 0 || x > bmp.getWidth() || y < 0 || y > bmp.getHeight()) {
            return false;
        }
        int color = bmp.getPixel(x, y);
        if (color == 0)
            return false;
        else {
            if (onColorSelectoedListener != null)
                onColorSelectoedListener.onColorSelectoed(color);
        }
        return true;
    }


    public void setOnColorSelectoedListener(OnColorSelectoedListener onColorSelectoedListener) {
        this.onColorSelectoedListener = onColorSelectoedListener;
    }

    OnColorSelectoedListener onColorSelectoedListener;

    public interface OnColorSelectoedListener {
        void onColorSelectoed(int color);
    }
}
