package com.xyy.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.widget.SeekBar;

import com.xyy.utils.MediaUtil;
import com.xyy.utils.TipsUtil;

public class XVideoView extends TextureView {
    //视频的宽度和高度
    private int mVideoWidth;
    private int mVideoHeight;
    private String videoPath;
    private MediaUtil mediaUtil;

    public XVideoView(Context context) {
        super(context);
        init(context);
    }

    public XVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setSurfaceTextureListener(surfaceTextureListener);
        mediaUtil = new MediaUtil(context);
        mediaUtil.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
    }

    private SurfaceTextureListener surfaceTextureListener = new SurfaceTextureListener() {
        //Surface创建成功
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Surface displaySurface = new Surface(surface);
            mediaUtil.setSurface(displaySurface);
            if (videoPath != null) {
                //开始播放视频
                mediaUtil.play(videoPath);
            }
        }

        //Surface大小发生变化
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        //Surface被销毁
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            stop();
            return true;
        }

        //Surface内容更新
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    public void play(String url){
        mediaUtil.stop();
        mediaUtil.play(url);
        this.videoPath = url;
    }

    //视频尺寸大小变化监听
    private MediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            mVideoWidth = width;
            mVideoHeight = height;
            if (width > 0 && height > 0) {
                //请求重新布局(重新测量 布局 显示效果)
                requestLayout();
            }
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Log.i("@@@@", "onMeasure(" + MeasureSpec.toString(widthMeasureSpec) + ", "
        //        + MeasureSpec.toString(heightMeasureSpec) + ")");

        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
        if (mVideoWidth > 0 && mVideoHeight > 0) {

            int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

            if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
                // the size is fixed
                width = widthSpecSize;
                height = heightSpecSize;

                // for compatibility, we adjust size based on aspect ratio
                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
            } else if (widthSpecMode == MeasureSpec.EXACTLY) {
                // only the width is fixed, adjust the height to match aspect ratio if possible
                width = widthSpecSize;
                height = width * mVideoHeight / mVideoWidth;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    height = heightSpecSize;
                }
            } else if (heightSpecMode == MeasureSpec.EXACTLY) {
                // only the height is fixed, adjust the width to match aspect ratio if possible
                height = heightSpecSize;
                width = height * mVideoWidth / mVideoHeight;
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    width = widthSpecSize;
                }
            } else {
                // neither the width nor the height are fixed, try to use actual video size
                width = mVideoWidth;
                height = mVideoHeight;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // too tall, decrease both width and height
                    height = heightSpecSize;
                    width = height * mVideoWidth / mVideoHeight;
                }
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // too wide, decrease both width and height
                    width = widthSpecSize;
                    height = width * mVideoHeight / mVideoWidth;
                }
            }
        } else {
            // no size yet, just adopt the given spec sizes
        }
        setMeasuredDimension(width, height);
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void start() {
        mediaUtil.start();
    }

    public void pause() {
        mediaUtil.pause();
    }

    public void seek(int pos){
        mediaUtil.seek(pos);
    }

    public void stop() {
        mediaUtil.stop();
    }

    public boolean isPlaying() {
        return mediaUtil.isPlaying();
    }

    public int getCurrentPos(){
        return mediaUtil.getCurrentPos();
    }

    public void setPlayPos(int playPos){
        mediaUtil.setPlayPos(playPos);
    }

    public void setSeekBar(SeekBar seekBar) {
        mediaUtil.setSeekBar(seekBar);
    }

    public void setOnMediaListener(MediaUtil.OnMediaListener onMediaListener) {
        mediaUtil.setOnMediaListener(onMediaListener);
    }
}
