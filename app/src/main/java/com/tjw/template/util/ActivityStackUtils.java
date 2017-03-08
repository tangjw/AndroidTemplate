package com.tjw.template.util;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;

import java.util.Stack;

/**
 * Activity 栈管理
 * Created by Android on 2017/2/15.
 */

public class ActivityStackUtils {
    
    private static ActivityStackUtils sInstance;
    private static Stack<Activity> sActivityStack;
    
    private ActivityStackUtils() {
    }
    
    public static ActivityStackUtils getInstance() {
        if (sInstance == null) {
            sInstance = new ActivityStackUtils();
        }
        return sInstance;
    }
    
    /**
     * 添加 Activity 到栈管理
     *
     * @param activity 一般情况可以传入 BaseActivity
     */
    public void addActivity(@NonNull Activity activity) {
        if (sActivityStack == null) {
            sActivityStack = new Stack<>();
        }
        sActivityStack.add(activity);
    }
    
    /**
     * 结束当前 Activity
     */
    public void finishActivity() {
        Activity activity = sActivityStack.lastElement();
        finishActivity(activity);
        
    }
    
    /**
     * 关闭传入的 Activity
     */
    public void finishActivity(@NonNull Activity activity) {
        
        if (!activity.isFinishing()) {
            Logger.e("finish -->" + activity.getClass().getSimpleName());
            activity.finish();
        }
        
        if (sActivityStack.contains(activity)) {
            sActivityStack.remove(activity);
            Logger.e("remove stack -->" + activity.getClass().getSimpleName());
        }
        
    }
    
    
    /**
     * 根据类名关闭Activity
     *
     * @param clazz 类名
     * @param isAll 是否关闭所有与该类名一样的Activity
     */
    public void finishActivity(Class<?> clazz, boolean isAll) {
        
        for (int i = 0; i < sActivityStack.size(); i++) {
            
            Activity activity = sActivityStack.get(i);
            
            if (clazz.equals(activity.getClass())) {
                finishActivity(activity);
                i--;
                if (!isAll) {
                    return;
                }
            }
        }
        
    }
    
    /**
     * 关闭其它Activity，栈内只有当前Activity, 经常在 MainActivity中使用
     */
    public void finishOtherActivity() {
        
        int tempSize = sActivityStack.size();
        
        for (int i = 0; i < (tempSize - 1); i++) {
            Activity activity = sActivityStack.get(0);
            finishActivity(activity);
        }
        
    }
    
    /**
     * 关闭所有Activity
     */
    public void finishAllActivity() {
        int tempSize = sActivityStack.size();
        for (int i = 0; i < tempSize; i++) {
            Activity activity = sActivityStack.get(0);
            finishActivity(activity);
        }
        sActivityStack.clear();
        
    }
    
    /**
     * 在logcat里打印栈内所有 Activity 类名
     */
    public void showAllActivity() {
        
        for (int i = 0; i < sActivityStack.size(); i++) {
            Activity activity = sActivityStack.get(i);
    
            Logger.i(activity.getClass().getSimpleName());
        }
        
    }
    
}
