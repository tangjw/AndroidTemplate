package com.tjw.template.swipeback;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.tjw.template.util.ActivityStackUtils;
import com.tjw.template.util.StatusBarUtil;

/**
 * ^-^
 * 必须在 Application 的 onCreate 方法中执行 BGASwipeBackManager.getInstance().init(this)
 * 来初始化滑动返回
 * Created by tang-jw on 2017/3/7.
 */

public abstract class BaseActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 在 super.onCreate(savedInstanceState) 之前调用该方法
        beforeSuperCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        
        // 将BaseActivity 添加到栈管理
        ActivityStackUtils.getInstance().addActivity(this);
        
        
        initView();
        
        setListener();
        
        loadData();
        
    }
    
    protected void loadData() {
    
    }
    
    protected void beforeSuperCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
    
    
    protected abstract void initView();
    
    protected void setListener() {
        
    }
    
    
    protected void setStatusBarColor(@ColorInt int color) {
        setStatusBarColor(color, StatusBarUtil.DEFAULT_STATUS_BAR_ALPHA);
    }
    
    protected void setStatusBarColor(@ColorInt int color, @IntRange(from = 0, to = 255) int statusBarAlpha) {
        StatusBarUtil.setColorForSwipeBack(this, color, statusBarAlpha);
    }
    
    /**
     * activity.finish() 操作 使用 ActivityStackUtils 进行管理
     */
    @Override
    public void finish() {
        if (ActivityStackUtils.getInstance().removeActivity()) {
            super.finish();
        }
    }
}
