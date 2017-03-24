package com.jph.takephoto.uitl;

import android.util.Log;

/**
 * 自定义的log类，默认TAG为“print”，改变isDebug可以控制日志是否输出
 * 当然也可以自定义日志的TAG
 * Created by tang-jw on 2016/5/25.
 */
public class L {
    
    private L() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }
    
    private static boolean isDebug = true;
    
    private static final String TAG = "print";
    
    // 下面四个是默认tag的函数  
    public static void i(String msg) {
        if (isDebug)
            Log.i(TAG, msg);
    }
    
    public static void d(String msg) {
        if (isDebug)
            Log.d(TAG, msg);
    }
    
    public static void e(String msg) {
        if (isDebug)
            Log.e(TAG, msg);
    }
    
    public static void v(String msg) {
        if (isDebug)
            Log.v(TAG, msg);
    }
    
    public static void w(String msg) {
        if (isDebug)
            Log.w(TAG, msg);
    }
    
    // 下面是传入自定义tag的函数  
    public static void i(String tag, String msg) {
        if (isDebug)
            Log.i(tag, msg);
    }
    
    public static void d(String tag, String msg) {
        if (isDebug)
            Log.d(tag, msg);
    }
    
    public static void e(String tag, String msg) {
        if (isDebug)
            Log.e(tag, msg);
    }
    
    public static void v(String tag, String msg) {
        if (isDebug)
            Log.v(tag, msg);
    }
    
    public static void w(String tag, String msg) {
        if (isDebug)
            Log.w(tag, msg);
    }
}

