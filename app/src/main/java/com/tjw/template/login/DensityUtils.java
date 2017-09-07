package com.tjw.template.login;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * dip => px  px => dip
 * Created by tang-jw on 5/29.
 */

public class DensityUtils {
    
    /**
     * 根据手机的分辨率从dip的单位转成为px(像素)
     */
    public static int dip2px(Context context, float dp) {
        final float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }
    
    /**
     * 根据手机的分辨率从px(像素)的单位转成为dp
     */
    public static int px2dip(Context context, float px) {
        final float density = context.getResources().getDisplayMetrics().density;
        return (int) (px / density + 0.5f);
    }
    
    /**
     * 获取状态栏的高度
     */
    public static int getStateBarHeight(Context context) {
        int statusBarHeight1 = 0;
        //获取status_bar_height资源的ID  
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值  
            statusBarHeight1 = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight1;
    }
    
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
        
    }
    
    
}
