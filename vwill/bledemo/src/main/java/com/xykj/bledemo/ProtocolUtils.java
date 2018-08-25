package com.xykj.bledemo;

import android.graphics.Color;

/**
 * 协议工具类
 */

public class ProtocolUtils {
    //所有的命令应该都是常量
    public static final byte INS_COLOR = 0x07;

    //所有的指令都在这里生产
    public static byte[] sendPasswrod() {
        byte[] buf = getBytes();
        buf[2] = 0x30;
        buf[16] = 0xc;
        return buf;
    }

    private static byte[] getBytes() {
        byte[] buf = new byte[17];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = 0x00;
        }
        buf[0] = 0x55;
        buf[1] = (byte) 0xaa;//(-128 -127)
        return buf;
    }

    //0-255
    public static byte[] sendRGB(int color) {
        byte[] buf = getBytes();
        buf[2] = INS_COLOR;
        //3R 4G 5B
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        buf[3] = (byte) red;
        buf[4] = (byte) green;
        buf[5] = (byte) blue;
        buf[6] = (byte) 0x0;
        buf[7] = (byte) 0xff; //亮度
        return buf;
    }
}