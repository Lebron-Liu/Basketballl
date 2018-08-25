package com.xykj.exceptions;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Properties;

/**
 * 自定义的异常处理工具，当出现错误时，先由我们自定义的处理工具去收集错误log，上传log，接着将错误处理回传给系统出来
 * （系统处理方式是弹出错误的提示框，结束进程）
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler sysHandler;
    private Context context;
    private static CrashHandler instance;
    private String logPath;

    private CrashHandler(Context context) {
        this.context = context;
        //初始化log存放位置
        logPath = context.getExternalCacheDir().getParent() + "/log";
        File file = new File(logPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static CrashHandler getInstance(Context context) {
        if (instance == null) {
            instance = new CrashHandler(context);
        }
        return instance;
    }

    public void init() {
        //获取系统的异常处理对象
        sysHandler = Thread.getDefaultUncaughtExceptionHandler();
        //将处理异常的对象改为我们自己定义的异常获取对象
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        //出现异常时触发的方法

        //记录log
        recordException(throwable);
        //将错误处理回传给系统
        sysHandler.uncaughtException(thread, throwable);
    }

    private void recordException(final Throwable throwable) {
        new Thread() {
            @Override
            public void run() {
                //手机设备信息
                collectCrashDeviceInfo();
                //保存异常信息
                String path = saveCrashInfoToFile(throwable);
                //上传

            }
        }.start();
    }

    private Properties mDeviceCrashInfo;
    private static final String VERSION_NAME = "versionName";
    private static final String VERSION_CODE = "versionCode";
    private static final String STACK_TRACE = "stack_trace";

    /**
     * 收集程序崩溃的程序版本以及设备信息
     */
    public void collectCrashDeviceInfo() {
        mDeviceCrashInfo = new Properties();
        try {
            //获取包管理器
            PackageManager pm = context.getPackageManager();
            //获取应用包信息（取出版本号以及版本名称）
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                mDeviceCrashInfo.put(VERSION_NAME, pi.versionName == null ? "not set" : pi.versionName);
                mDeviceCrashInfo.put(VERSION_CODE, "" + pi.versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("m_tag", "Error while collect package info", e);
        }
        //使用反射来收集设备信息.在Build类中包含各种设备信息,
        //例如: 系统版本号,设备生产商 等帮助调试程序的有用信息
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                mDeviceCrashInfo.put(field.getName(), "" + field.get(null));
            } catch (Exception e) {
                Log.e("m_tag", "Error while collect crash info", e);
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return
     */
    private String saveCrashInfoToFile(Throwable ex) {
        //将异常信息写入输出流
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);
        //转换全部的异常文本
        String result = info.toString();
        printWriter.close();
        mDeviceCrashInfo.put("EXCEPTION", ex.getLocalizedMessage());
        mDeviceCrashInfo.put(STACK_TRACE, result);
        try {
            Calendar c = Calendar.getInstance();
            String date = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE);
            String time = c.get(Calendar.HOUR_OF_DAY) + "_" + c.get(Calendar.MINUTE) + "_" + c.get(Calendar.SECOND);
            String fileName = logPath + "/crash-" + date + "-" + time + ".log";
            Log.e("m_tag", "=====>" + fileName);
            FileOutputStream trace = new FileOutputStream(fileName);
            mDeviceCrashInfo.store(trace, "");
            trace.flush();
            trace.close();
            return fileName;
        } catch (Exception e) {
            Log.e("m_tag", "an error occured while writing report file...", e);
        }
        return null;
    }
}
