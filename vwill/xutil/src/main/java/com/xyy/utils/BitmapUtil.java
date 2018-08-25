package com.xyy.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitmapUtil {

    public static Bitmap decodBitmap(byte[] source, int maxWidth, int maxHeight) {
        // 解码配置
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565; //模式为RBG可以减小图片
        // 解析宽高的模式
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(source, 0, source.length, opts);// 得到的是null
        configOpt(opts, maxWidth, maxHeight);
        // 开始解码像素
        opts.inJustDecodeBounds = false;
        Bitmap result = BitmapFactory.decodeByteArray(source, 0, source.length, opts);
        return result;
    }

    public static Bitmap decodBitmap(String path, int maxWidth, int maxHeight) {
        // 解码配置
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        // 解析宽高的模式
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        configOpt(opts, maxWidth, maxHeight);
        // 开始解码像素
        opts.inJustDecodeBounds = false;
        Bitmap result = BitmapFactory.decodeFile(path, opts);
        return result;
    }

    private static void configOpt(BitmapFactory.Options opts, int maxWidth, int maxHeight) {
        int bitmapWidth = opts.outWidth;
        int bitmapHeight = opts.outHeight;
        if (maxWidth == 0) {
            maxWidth = bitmapWidth;
        }
        if (maxHeight == 0) {
            maxHeight = bitmapHeight;
        }
        int w = (int) Math.ceil((float) bitmapWidth / maxWidth);
        int h = (int) Math.ceil((float) bitmapHeight / maxHeight); // 19.2 20
        // 19.9 20
        if (w > 1 || h > 1) {
            if (w > h) {
                opts.inSampleSize = w; // 整数>1 --> 1/w
            } else {
                opts.inSampleSize = h;
            }
        }
    }


    /**
     * 将输入流转为byte数组
     *
     * @param in
     * @return
     */
    public static byte[] getBytes(InputStream in) {
        byte[] result = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        try {
            int num;
            while ((num = in.read(buffer)) != -1) {
                out.write(buffer, 0, num);
            }
            out.flush();
            result = out.toByteArray();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * @param bitmap
     * @param path   /mnt/sdcard/small.jpg
     */
    public static boolean saveBitmap(Bitmap bitmap, String path) {
        FileOutputStream out = null;
        boolean result = false;
        try {
            out = new FileOutputStream(path);
            bitmap.compress(CompressFormat.JPEG, 100, out);
            out.flush();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

}
