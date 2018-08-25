package com.xyy.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
    private SimpleDateFormat sf;

    private static TimeUtil instance;
    private TimeUtil(){
        sf = new SimpleDateFormat("HH:mm");
    }

    public static TimeUtil getInstance(){
        if(instance == null){
            instance = new TimeUtil();
        }
        return instance;
    }

    public String formatTime(long time){
        //检测是否是今天以内
        long timeStart = startTimeOfToday();
        //计算要转换的时间跟今天的起始时间差
        long t = time-timeStart;
        if(t>=0 && t<86400000){
            //今天--显示小时分钟
            return sf.format(new Date(time));
        }else if(t<0 && t>= -86400000){
            //昨天
            return "昨天 "+sf.format(new Date(time));
        }else if(t<-86400000 && t>= -172800000){
            return "前天 "+sf.format(new Date(time));
        }else{
            return "3天前";
        }
    }

    /**
     * 今天的起始时间
     * @return
     */
    private long startTimeOfToday(){
        //系统当前时间
        Calendar c = Calendar.getInstance(); //2018/7/3 16:26:30:521
        c.set(Calendar.HOUR_OF_DAY,0); //2018/7/3 00:26:30:521
        c.set(Calendar.MINUTE,0);     //2018/7/3 00:00:30:521
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        //2018/7/3 00:00:00:0
        return c.getTimeInMillis();
    }
}
