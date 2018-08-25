package com.xyy.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.webkit.URLUtil;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 缩略图加载工具
 */
public class XImageLoader {

    private static XImageLoader instance;
    //内存缓存类
    private LruCache<String, Bitmap> caches;
    //本地缓存的文件夹
    private String localCachePath;
    //处理缩略图生成的线程池
    private ExecutorService executorService;
    //记录当前加载的任务情况(一个地址，关联一个视图)
    private Map<String, ImageView> currentTask;

    private XImageLoader(Context context) {
        //获取系统分配给应用的运行空间的1/8来作为缓存的最大空间
        int size = (int) (Runtime.getRuntime().maxMemory() / 8);
        //初始化内存缓存对象
        caches = new LruCache<String, Bitmap>(size) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //处理缓存的图片的大小计算
                return value.getRowBytes() * value.getHeight();
            }
        };
        //获取缓存文件夹 /sdcard/Android/data/应用包名/cache
        localCachePath = context.getApplicationContext().getExternalCacheDir().toString();
        //初始化线程池
        executorService = Executors.newCachedThreadPool();
        currentTask = new HashMap<String, ImageView>();
    }

    public static XImageLoader getInstance(Context context) {
        if (instance == null) {
            instance = new XImageLoader(context);
        }
        return instance;
    }

    public void showImage(String path, ImageView iv,int defIcon) {
        showImage(path, iv, 100, 100,defIcon, false);
    }

    /**
     * 显示图像缩略图
     *
     * @param path 假如 ：/mnt/sdcard/123.jpg_100-100   -->/sdcard/Android/data/应用包名/cache/mntsdcard123jpg100100
     * @param iv
     */
    public void showImage(String path, ImageView iv, int maxWidth, int maxHeight,int defIcon, boolean isResize) {
        String key = path;
        if (isResize) {
            key = path + "_" + maxWidth + "-" + maxHeight;
        }
        //针对该路径检测内存中是否有对应的缩略图
        if (caches.get(key) != null) {
            iv.setImageBitmap(caches.get(key));
        } else {
            //没有缩略图，检测本地是否有缩略图
            String thumbPath = getLocalCachePath(key);
            File f = new File(thumbPath);
            if (f.exists()) {
                //有缩略图,从本地缓存文件夹中加载缩略图
                Bitmap thumb = BitmapUtil.decodBitmap(thumbPath, maxWidth, maxHeight);
                if (null != thumb) {
                    //保存到内存中，方便下次可以从内存中快速的读取
                    caches.put(key, thumb);
                    //显示
                    iv.setImageBitmap(thumb);
                } else {
                    //显示默认图标
                    iv.setImageResource(defIcon);
                }
            } else {
                //检测当前路径是否有任务在生成中
                if (currentTask.containsKey(key)) {
                    //改变该路径对应的新视图
                    currentTask.put(key, iv);
                    return;
                }
                //记录当前要加载的路径对应的显示视图
                currentTask.put(key, iv);
                if(URLUtil.isNetworkUrl(path)){
                    //下载图片，生成缩略图
                    executorService.execute(new DownloadRunnable(path, key, maxWidth, maxHeight,defIcon));
                }else {
                    //生成缩略图
                    executorService.execute(new CreateThumbRunnable(path, key, maxWidth, maxHeight,defIcon));
                }
            }

        }
    }

    class CreateThumbRunnable extends Handler implements Runnable {

        protected String path; //源图片地址
        protected int width;
        protected int height;
        protected String key; //按尺寸记录在内存中的key
        protected int defIcon;

        public CreateThumbRunnable(String path, String key, int width, int height,int defIcon) {
            this.path = path;
            this.width = width;
            this.height = height;
            this.key = key;
            this.defIcon = defIcon;
        }

        @Override
        public void run() {
            //从原图上生成缩略图
            Bitmap thumb = BitmapUtil.decodBitmap(path, width, height);
            if (thumb != null) {
                //保存到本地缓存文件夹中
                String thumbPath = getLocalCachePath(key);
                BitmapUtil.saveBitmap(thumb, thumbPath);
                //保存到内存中
                caches.put(key, thumb);
                //显示
                obtainMessage(1, thumb).sendToTarget();
            } else {
                sendEmptyMessage(0);
            }
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    ImageView iv = currentTask.remove(key);
                    if (iv.isShown()) {
                        //生成成功
                        Bitmap b = (Bitmap) msg.obj;
                        iv.setImageBitmap(b);
                    }
                    break;
                case 0:
                    //生成失败，显示默认图标
                    ImageView iv1 = currentTask.remove(key);
                    iv1.setImageResource(defIcon);
                    break;
            }
        }
    }

    class DownloadRunnable extends  CreateThumbRunnable{

        public DownloadRunnable(String path, String key, int width, int height,int defIcon) {
            super(path, key, width, height,defIcon);
        }

        @Override
        public void run() {
            try {
                //1、创建URL(地址)
                URL url = new URL(path);
                TipsUtil.log("start download:"+path);
                //2、打开连接
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //检测是否成功
                int code = conn.getResponseCode();
                if (code == 200) {
                    Bitmap thumb;
                    //3、从服务器中获取内容
                    InputStream in = conn.getInputStream();
                    if(conn.getContentLength() >= 1048576){
                        //如果是大图，建议先保存到磁盘再做
                        String thumbPath = getLocalCachePath(key);
                        FileOutputStream out = new FileOutputStream(thumbPath);
                        byte[] buf = new byte[2048];
                        int num;
                        while((num = in.read(buf))!=-1){
                            out.write(buf,0,num);
                        }
                        out.flush();
                        out.close();
                        //生成缩略图
                        thumb = BitmapUtil.decodBitmap(thumbPath,width,height);
                    }else{
                        //如果是小图可以转为byte[]然后在转
                        thumb = BitmapUtil.decodBitmap(inputStrem2ByteAry(in),width,height);
                        String thumbPath = getLocalCachePath(key);
                        BitmapUtil.saveBitmap(thumb, thumbPath);
                    }
                    //保存到内存中
                    caches.put(key, thumb);
                    //显示
                    obtainMessage(1, thumb).sendToTarget();
                    in.close();
                }else{
                    sendEmptyMessage(0);
                }
                conn.disconnect();
            }catch (Exception e){
                e.printStackTrace();
                sendEmptyMessage(0);
            }

        }
    }

    private byte[] inputStrem2ByteAry(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        byte[] result=null;
        int num;
        while ((num = in.read(buf)) != -1) {
            out.write(buf, 0, num);
        }
        result = out.toByteArray();
        out.close();
        return result;
    }

    /**
     * 检测本地是否有缩略图
     *
     * @param path
     * @return
     */
    public boolean isHasLocalCache(String path) {
        String cachePath = getLocalCachePath(path);
        return new File(cachePath).exists();
    }

    /**
     * 保存本地缩略图
     *
     * @param path
     * @param thumb
     */
    public void saveThumbInLocal(String path, Bitmap thumb) {
        //保存到本地缓存文件夹中
        String thumbPath = getLocalCachePath(path);
        BitmapUtil.saveBitmap(thumb, thumbPath);
        //保存到内存中
        caches.put(path, thumb);
    }


    /**
     * 基于源文件获取所对应的缩略图的路径
     *
     * @param path
     * @return
     */
    private String getLocalCachePath(String path) {
        String name;
        if (URLUtil.isNetworkUrl(path)) {
            //该地址是一个网址
            int index = path.lastIndexOf("/");
            if (index < 0) {
                index = 0;
            }
            name = path.substring(index).replaceAll("[^\\w]", "");
        } else {
            if (new File(path).getParent().equals(localCachePath)) {
                return path;
            }
            //将路径中的特殊字符去掉当做缩略图的文件名
            name = path.replaceAll("[^\\w]", "");
        }
        return localCachePath + File.separator + name;
    }

}
