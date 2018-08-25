package com.xyy.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.Surface;
import android.webkit.URLUtil;
import android.widget.SeekBar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class MediaUtil {
    private static final int MSG_PLAY_POS = 1;  //播放进度更新
    private static final int MSG_DOWNLOAD_POS = 2; //缓冲进度更新
    //本地缓存的文件夹
    private String localCachePath;

    //媒体播放器
    private MediaPlayer mediaPlayer;
    //当前是否是停止
    private boolean isStop = true;
    //解码出错的位置
    private int errorPos;
    //进度显示的视图
    private SeekBar seekBar;
    private Surface surface;
    private int playPos;

    public void setPlayPos(int playPos) {
        this.playPos = playPos;
    }

    public MediaUtil(Context context) {
        localCachePath = context.getApplicationContext().getExternalCacheDir().toString();
    }

    /**
     * 播放入口
     *
     * @param url
     */
    public void play(String url) {
        //初始化播放器
        initMediaPlayer();
        //是否是网址
        if (URLUtil.isNetworkUrl(url)) {
            // http://192.168.18.251:8080/musics/abc.mp3   -->abcmp3
            //本地是否有缓存
            String localCacheMedia = getLocalCachePath(url);
            File f = new File(localCacheMedia);
            if (f.exists()) {
                //播放缓存中的媒体
                setDataAndPrepare(localCacheMedia);
            } else {
                //启动下载
                new DownloadThread(url, localCacheMedia, onDownloadListener).start();
            }
        } else {
            //本地的内容直接播放
            File f = new File(url);
            if (f.exists()) {
                setDataAndPrepare(url);
            } else {
                //非法路径
            }
        }
    }

    public boolean isMediaplayNull() {
        return mediaPlayer == null;
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public void start() {
        if (null != mediaPlayer && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            mHandler.sendEmptyMessageDelayed(MSG_PLAY_POS, 200);
        }
    }

    public void pause() {
        if (null != mediaPlayer && mediaPlayer.isPlaying()) {
            mHandler.removeMessages(MSG_PLAY_POS);
            mediaPlayer.pause();
        }
    }

    public void seek(int pos){
        if (null != mediaPlayer ) {
            mediaPlayer.seekTo(pos);
        }
    }

    public void stop() {
        if (null != mediaPlayer) {
            mHandler.removeMessages(MSG_PLAY_POS);
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public int getCurrentPos() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    private OnDownloadListener onDownloadListener = new OnDownloadListener() {
        @Override
        public void loading(int currentSize, int totalSize, String savePath) {
            //计算下载的百分比
            int rate = 100 * currentSize / totalSize;
            if (rate >= 2 && isStop) {
                isStop = false;
                setDataAndPrepare(savePath);
            }
            //更新缓存进度
            if (null != onDownloadListener) {
                mHandler.obtainMessage(MSG_DOWNLOAD_POS, rate).sendToTarget();
            }
        }

        @Override
        public void loadFail(int code, String extras) {
            switch (code) {
                case 0:
                    //缓存文件夹创建失败
                    break;
                case 1:
                    //请求失败
                    break;
                case 2:
                    //下载失败
                    File f = new File(extras);
                    if (f.exists()) {
                        f.delete();
                    }
                    break;

            }
        }
    };

    //初始化播放器
    private void initMediaPlayer() {
        if (null == mediaPlayer) {
            //初始化播放器
            mediaPlayer = new MediaPlayer();
            //设置准备监听
            mediaPlayer.setOnPreparedListener(onPreparedListener);
            //设置错误监听
            mediaPlayer.setOnErrorListener(onErrorListener);
            //播放结束监听
            mediaPlayer.setOnCompletionListener(onCompletionListener);
            mediaPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
        } else {
            mediaPlayer.reset();
        }
        //处于idle状态
        //如果检测到有显示视频画面Surface，则设置显示载体
        if (null != surface) {
            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.setSurface(surface);
        }

    }

    //解码
    private void setDataAndPrepare(String path) {
        //设置播放的媒体源
        try {
            mediaPlayer.setDataSource(path);
            //处于初始化完毕状态(initialized)
            //准备
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            isStop = true;
            mediaPlayer.reset();
        }
    }

    //媒体播放的准备结果监听
    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            //检测是否有错误位置
            if (errorPos > 0) {
                mp.seekTo(errorPos);
            } else if (playPos > 0) {
                mp.seekTo(playPos);
                playPos = 0;
            }
            //准备完成---控制媒体的播放(播放、设置播放位置、获取媒体的总时间、暂停、停止)
            mp.start();
            if (null != onMediaListener) {
                onMediaListener.onDuration(mp.getDuration());
            }
            //开始获取当前时间
            mHandler.sendEmptyMessageDelayed(MSG_PLAY_POS, 200);
        }
    };

    //解码错误监听
    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            //记录出错的位置
            errorPos = mp.getCurrentPosition();
            //停止解码
            mp.stop();
            mp.reset();
            isStop = true;
            return true;
        }
    };

    //播放结束监听
    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {

        }
    };

    class DownloadThread extends Thread {
        private String url;
        private String savePath;  //  /mnt/sdcard/Android/data/com.xykj.meida/caches/abcmp3
        private OnDownloadListener onDownloadListener;

        DownloadThread(String url, String savePath, OnDownloadListener onDownloadListener) {
            this.url = url;
            this.savePath = savePath;
            this.onDownloadListener = onDownloadListener;
        }

        @Override
        public void run() {
            //检测缓存文件夹是否存在，不存在创建
            File saveFile = new File(savePath);
            if (checkFolderExists(saveFile.getParentFile())) {
                //下载文件
                try {
                    URL link = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) link.openConnection();
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    if (conn.getResponseCode() == 200) {
                        // 获取服务器端返回内容的输入流
                        InputStream in = conn.getInputStream();
                        RandomAccessFile out = new RandomAccessFile(saveFile, "rw");
                        int totalLen = conn.getContentLength();
                        out.setLength(totalLen);
                        byte[] buf = new byte[2048];
                        int num;
                        int count = 0;
                        while ((num = in.read(buf)) != -1) {
                            out.write(buf, 0, num);
                            count += num;
                            //检测当前是否有解码器在解码，没有则开始解码
                            if (null != onDownloadListener) {
                                onDownloadListener.loading(count, totalLen, savePath);
                            }
                        }
                        out.close();
                        in.close();
                    } else {
                        if (null != onDownloadListener) {
                            onDownloadListener.loadFail(1, "请求失败");
                        }
                    }
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (null != onDownloadListener) {
                        onDownloadListener.loadFail(2, savePath);
                    }
                }
            } else {
                //下载失败
                if (null != onDownloadListener) {
                    onDownloadListener.loadFail(0, "文件夹创建失败");
                }
            }
        }

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PLAY_POS:
                    //计算播放进度的百分比 5   20 --> 25%
                    if (mediaPlayer != null) {
                        int currentPos = mediaPlayer.getCurrentPosition();
                        if (seekBar != null) {
                            int rate = 100 * currentPos / mediaPlayer.getDuration();
                            seekBar.setProgress(rate);
                        }
                        if (null != onMediaListener) {
                            onMediaListener.onCurrentPosition(currentPos);
                        }
                        sendEmptyMessageDelayed(MSG_PLAY_POS, 200);
                    }
                    break;
                case MSG_DOWNLOAD_POS:
                    int pos = (Integer) msg.obj;
                    if (seekBar != null) {
                        seekBar.setSecondaryProgress(pos);
                    }
                    break;
            }
        }
    };

    interface OnDownloadListener {
        void loading(int currentSize, int totalSize, String savePath);

        void loadFail(int code, String extras);
    }

    public interface OnMediaListener {
        //返回总时间
        void onDuration(int duration);

        //返回当前时间
        void onCurrentPosition(int position);
    }

    private OnMediaListener onMediaListener;

    public void setOnMediaListener(OnMediaListener onMediaListener) {
        this.onMediaListener = onMediaListener;
    }

    private boolean checkFolderExists(File file) {
        if (!file.exists()) {
            return file.mkdirs();
        }
        return true;
    }

    private String getLocalCachePath(String path) {
        //该地址是一个网址
        int index = path.lastIndexOf("/");
        if (index < 0) {
            index = 0;
        }
        String name = path.substring(index).replaceAll("[^\\w]", "");
        return localCachePath + File.separator + name;
    }

    public void setSeekBar(SeekBar seekBar) {
        this.seekBar = seekBar;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (null != mediaPlayer && mediaPlayer.isPlaying()) {
                    mHandler.removeMessages(MSG_PLAY_POS);
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (null != mediaPlayer) {
                    int pos = seekBar.getProgress();
                    //计算要设置播放的位置
                    int currentPos = mediaPlayer.getDuration() * pos / 100;
                    mediaPlayer.seekTo(currentPos);
                    if (mediaPlayer.isPlaying()) {
                        mHandler.sendEmptyMessageDelayed(MSG_PLAY_POS, 200);
                    }
                }
            }
        });
    }

    public void setSurface(Surface surface) {
        this.surface = surface;
    }

    private MediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener;

    public void setOnVideoSizeChangedListener(MediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener) {
        this.onVideoSizeChangedListener = onVideoSizeChangedListener;
    }
}
