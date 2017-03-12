package com.tjw.template;

import android.app.Application;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import cn.bingoogolapple.swipebacklayout.BGASwipeBackManager;

/**
 * Application 用来初始化一些操作
 * Created by Android on 2017/2/22.
 */

public class TemplateApp extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        //初始化 SwipeBack
        BGASwipeBackManager.getInstance().init(this);
        
        initLogger();
        
    }
    
    private void initLogger() {
        Logger
                .init("Temp")                 // default PRETTYLOGGER or use just init()
                .methodCount(3)                 // default 2
//                .hideThreadInfo()               // default shown
                .logLevel(LogLevel.FULL)        // default LogLevel.FULL
                .methodOffset(2);               // default 0
                //.logAdapter(new AndroidLogAdapter()); //default AndroidLogAdapter
    }
    
}
