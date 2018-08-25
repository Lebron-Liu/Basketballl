package com.xyy.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by admin on 2016/10/11.
 */
public class TipsUtil {
    private static final boolean DEBUG = true;


    public static void toast(Context context, String msg) {
        toast(context, msg, Toast.LENGTH_SHORT);
    }

    public static void toast(Context context, String msg, int time) {
        Toast.makeText(context, msg, time).show();
    }


    public static void log(String msg) {
        log("m_tag", msg);
    }

    public static void log(String tag, String msg) {
        if (DEBUG) {
            Log.e(tag, msg);
        }
    }
}
