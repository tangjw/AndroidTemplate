package com.jph.takephoto.album.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * ^-^
 * Created by tang-jw on 2017/3/22.
 */

public class PaddingToFrameLayout extends FrameLayout {
    public PaddingToFrameLayout(@NonNull Context context) {
        this(context, null);
    }
    
    public PaddingToFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public PaddingToFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setPaddingTop();
    }
    
    private void setPaddingTop() {
        int compatPadingTop = 0;
        // android 4.4以上将Toolbar添加状态栏高度的上边距，沉浸到状态栏下方
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            compatPadingTop = getStatusBarHeight();
        }
        this.setPadding(getPaddingLeft(), getPaddingTop() + compatPadingTop, getPaddingRight(), getPaddingBottom());
    }
    
    public int getStatusBarHeight() {
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }
    
}
