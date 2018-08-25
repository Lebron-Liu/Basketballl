package com.xykj.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

@SuppressLint("AppCompatCustomView")
public class XImageView extends ImageView {
    private static final int MODE_DRAG = 1; //拖动
    private static final int MODE_ZOOM = 2; //缩放或者旋转
    //当前用户操作的模式
    private int mode;

    public XImageView(Context context) {
        super(context);
        init();
    }

    public XImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    //移动的上一个点
    private Point lastPoint;
    //上一次两个触控点之间的距离
    private float lastDis;
    //缩放中心点
    private Point center;
    //旋转的起始角度
    private float lastDegress;
    //记录上一次触控离开的时间（如果两次离开的时间间隔比较短，可以当做是双击）
    private long lastUpTime;
    //记录图像原始的状态
    private Matrix origenalMatrix;
    //原始大小
    private RectF origenalRect;

    private void init() {
        lastPoint = new Point();
        center = new Point();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                //单个触控点按下(接触视图瞬间)
                //记录开始拖动的点
                int x = (int) event.getX();
                int y = (int) event.getY();
                lastPoint.set(x, y);
                mode = MODE_DRAG;
                //记录开始操作前的矩阵状态
                if (null == origenalMatrix) {
                    origenalMatrix = new Matrix();
                    origenalMatrix.set(getImageMatrix());
                    origenalRect = new RectF(getImageRect());
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //多个触控点按下
                lastDis = getDis(event);
                getCenter(event);
                lastDegress = getDegress(event);
                if (lastDis > 10) {
                    mode = MODE_ZOOM;
                }
                if (null == origenalMatrix) {
                    origenalMatrix = new Matrix();
                    origenalMatrix.set(getImageMatrix());
                    origenalRect = new RectF(getImageRect());
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //只要有触控点触摸在视图上都会执行move
                switch (mode) {
                    case MODE_DRAG:
                        //当前的坐标
                        int cx = (int) event.getX();
                        int cy = (int) event.getY();
                        //计算x以及y方向的偏移
                        int dX = cx - lastPoint.x;
                        int dY = cy - lastPoint.y;
                        getImageMatrix().postTranslate(dX, dY);
                        //记录上一点
                        lastPoint.set(cx, cy);
                        invalidate();
                        break;
                    case MODE_ZOOM:
                        //获取当前两个触控点的距离
                        float newDis = getDis(event);
                        if (newDis > 10) {
                            getCenter(event);
                            //旋转
                            float newDegress = getDegress(event);
                            //旋转的角度为新旧角度的差
                            float d = newDegress - lastDegress;
                            getImageMatrix().postRotate(d, center.x, center.y);
                            //计算新距离和上一个距离之间的比例
                            float scale = newDis / lastDis;
                            getImageMatrix().postScale(scale, scale, center.x, center.y);
                            //记录当前的角度
                            lastDegress = newDegress;
                            //记录当前的距离为最后一次距离
                            lastDis = newDis;
                            invalidate();
                        }
                        break;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                //多个触控点离开
                mode = 0;
                break;
            case MotionEvent.ACTION_UP:
                //单个触控点离开的瞬间
                mode = 0;
                //离开的时间
                long t = System.currentTimeMillis();
                if (t - lastUpTime <= 300) {
                    //双击
                    getImageRect();
                    //如果新的大小比旧的大小大，双击时缩小，否则放大2倍
                    if (newImageRect.width() > origenalRect.width()) {
                        //缩小到原大小
                        setImageMatrix(origenalMatrix);
                    } else {
                        float[] values = new float[9];
                        getImageMatrix().getValues(values);
                        float px = event.getX();
                        float py = event.getY();
//                        //记录放大前点击位置和原状态左上角的相对位置
//                        float oldLeft = px - newImageRect.left;
//                        float oldTop = py - newImageRect.top;
                        //放大到原来的2倍
                        getImageMatrix().postScale(values[0] * 2, values[4] * 2,px, py);
//                        //获取放大后的图像信息，将点击位置和新状态的放大位置重合
//                        getImageRect();
//                        //放大后理想的左上角位置
//                        float newLeft = px - oldLeft * 2;
//                        float newTop = py - oldTop * 2;
//                        //将图片平移到理想位置
//                        getImageMatrix().postTranslate(newLeft - newImageRect.left, newTop - newImageRect.top);
                    }
                    invalidate();
                }
                lastUpTime = t;
                break;
        }
        return true;
    }

    /**
     * 两个点之间与参考圆垂直直径之间角度
     *
     * @param event
     * @return
     */
    private float getDegress(MotionEvent event) {
        //基于触控位置和中心点，计算触控点和顶部参考线夹角的tan值
        int x = (int) event.getX(1);
        int y = (int) event.getY(1);
        //当前两点之间形成的辅助圆的半径
        float r = getDis(event) / 2;
        float tanValue = (y - center.y + r) / Math.abs(x - center.x);
        //得到弧度（0-2PI）
        float d = (float) Math.atan(tanValue); // 0-2PI
        //将弧度值转为角度(0-360
        float degress = (float) Math.toDegrees(d);
        //当x位于圆心左侧，得到的度数是中心夹角的补角
        float result = degress * 2;
        if (x < center.x) {
            return 360 - result;
        }
        return result;
    }

    /**
     * 获取缩放中心
     *
     * @param event
     */
    private void getCenter(MotionEvent event) {
        //x方向的中心坐标
        int x = (int) ((event.getX(0) + event.getX(1)) / 2);
        //y方向的中心
        int y = (int) ((event.getY(0) + event.getY(1)) / 2);
        center.set(x, y);
    }

    /**
     * 获取两个触控点之间的距离
     *
     * @param event
     * @return
     */
    private float getDis(MotionEvent event) {
        //x方向的偏移
        float x = event.getX(1) - event.getX(0);
        //y方向的偏移
        float y = event.getY(1) - event.getY(0);
        return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    private RectF newImageRect;

    /**
     * 获取当前显示的图片所占的区域
     *
     * @return
     */
    public RectF getImageRect() {
        //原始图片的区域
        Rect rect = getDrawable().getBounds();
        //获取变换的参数
        float[] values = new float[9];
        getImageMatrix().getValues(values);
        //变换之后的左上右下参数
        float scaleWidth = values[0] * rect.width();
        float scaleHeight = values[4] * rect.height();
        float left = values[2];
        float top = values[5];
        if (newImageRect == null) {
            newImageRect = new RectF(left, top, left + scaleWidth, top + scaleHeight);
        } else {
            newImageRect.set(left, top, left + scaleWidth, top + scaleHeight);
        }
        return newImageRect;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        reset();
        super.setImageBitmap(bm);
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        reset();
        super.setImageDrawable(drawable);
    }

    @Override
    public void setImageResource(int resId) {
        reset();
        super.setImageResource(resId);
    }

    @Override
    public void setImageURI(@Nullable Uri uri) {
        reset();
        super.setImageURI(uri);
    }

    //恢复状态
    public void reset() {
        if (null != origenalMatrix) {
            setImageMatrix(origenalMatrix);
            origenalMatrix = null;
            origenalRect = null;
            invalidate();
        }
    }
}
