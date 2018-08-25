package com.xykj.utils;

/**
 * Created by admin on 2016/10/10.
 */
public class Common {
    public static final String IP = "192.168.2.183";

    public static final int PORT = 8080;
    //服务器的地址
    public static final String BASE_URL = "http://" + IP + ":" + PORT + "/AskMeServer/";
    /**
     * 读取时间
     */
    public static int READ_TIMEOUT = 30;

    /**
     * 连接时间
     */
    public static int CONNECT_TIMEOUT = 30;

    /**
     * 写入时间
     */
    public static int WRITE_TIMEOUT = 30;
}
