package com.xykj.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Common {

    ///注销的广播
    public static final String ACTION_LOGOUT="action_logout";
    public static String TOKEN = null;
    //纯文本
    public static final int TYPE_TEXT = 1;
    //图像
    public static final int TYPE_IMAGE = 2;
    //视频
    public static final int TYPE_VIDEO = 3;
    //音频
    public static final int TYPE_AUDIO = 4;
    //关注
    public static final int TYPE_ATTENTION = 100;


    public static <T> T createObj(Object obj, int i) {
        // class ClassName<T,K,V>
        Type superType = obj.getClass().getGenericSuperclass();
        if (superType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) superType;
            //遍历尖括号中的类型
            Type[] ts = pType.getActualTypeArguments();
            Class<T> cls = (Class<T>) ts[i];
            try {
                return cls.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static final String IP = "192.168.18.251";
    public static final int PORT = 8080;

    public static final String SERVER_URL = "http://"+IP+":"+PORT+"/VWillServer";
    //文章加载
    public static final String URL_LOAD_ARTICLE = SERVER_URL + "/list";
    //用户关注的文章
    public static final String URL_LOAD_ATTENTION = SERVER_URL + "/attention";
    //加载系统中的所有标签
    public static final String URL_LOAG_ALL_GROUP = SERVER_URL + "/all_group";
    //注册
    public static final String URL_REGIST = SERVER_URL + "/regist";
    //账号或者昵称登录
    public static final String URL_LOGIN = SERVER_URL + "/login";
    //注销登录
    public static final String URL_LOGOUT=SERVER_URL+"/logout";
    //赞/踩
    public static final String URL_SUPPORT=SERVER_URL+"/support";
    //取消赞/踩
    public static final String URL_SUPPORT_CANCEL=SERVER_URL+"/cancel_support";
    //最新评论
    public static final String URL_LOAD_NEW_REPLY=SERVER_URL+"/r_list";
    //热门评论
    public static final String URL_LOAD_HOT_REPLY=SERVER_URL+"/hot_r_list";
    //发布评论
    public static final String URL_PUBLISH_REPLY=SERVER_URL+"/reply";
    //发布文章
    public static final String URL_PUBLISH_ARTICLE = SERVER_URL+"/publish";
    //加载用户基本信息
    public static final String URL_LOAD_USER_INFO=SERVER_URL+"/look";
    //加载某个用户发布的文章
    public static final String URL_ARTICLE_BY_USER=SERVER_URL+"/article_by_user";
    //获取群组信息
    public static final String URL_LOAD_GROUP_INFO = SERVER_URL+"/get_group_info";
    //加载群组列表
    public static final String URL_LOAD_GROUP_LIST = SERVER_URL+"/group_i_joined";
    //加载广告
    public static final String URL_LOAD_AD=SERVER_URL+"/get_ad";
    //接人功能地址(去获取好友的位置)
    public static final String URL_GET_USER_LOC = SERVER_URL+"/get_u_loc";
    //获取直播房间
    public static final String URL_LOAD_LIVE_ROOMS=SERVER_URL+"/live/get_all_room";
    //创建房间
    public static final String URL_CREATE_ROOM = SERVER_URL+"/live/create_room";
    //开始直播
    public static final String URL_START_LIVE=SERVER_URL+"/live/start_live";
    //停止直播
    public static final String URL_STOP_LIVE=SERVER_URL+"/live/stop_live";
    //加入房间
    public static final String URL_JOIN_ROOM = SERVER_URL+"/live/join_room";
    //退出房间
    public static final String URL_EXIT_ROOM = SERVER_URL+"/live/exit_room";
    //手机登录
    public static final String URL_LOGIN_BY_PHONE=SERVER_URL+"/login_by_phone";
    //使用第三方账号登录
    public static final String URL_LOGIN_BY_OPENID=SERVER_URL+"/login_by_openid";
    //更新用户基本信息
    public static final String URL_UPDATE_USER_INFO = SERVER_URL+"/update";


}