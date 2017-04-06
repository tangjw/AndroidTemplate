package com.tjw.template.swipeback;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.tjw.swipeback.SwipeBackLayout;
import com.tjw.template.util.ActivityStackUtils;

/**
 * ^-^
 * Created by tang-jw on 2017/4/6.
 */

public abstract class BaseSwipeBackActivity extends AppCompatActivity implements SwipeBackLayout.SwipeBackListener {
    
    private SwipeBackLayout mSwipeBackLayout;
    private View mChild_bg;
    
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
    
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(getSwipeBackContainer());
        mSwipeBackLayout.addView(LayoutInflater.from(this).inflate(layoutResID, null));
    }
    
    private View getSwipeBackContainer() {
        FrameLayout container = new FrameLayout(this);
        mSwipeBackLayout = new SwipeBackLayout(this);
        mSwipeBackLayout.setDragEdge(SwipeBackLayout.DragEdge.LEFT);
        mSwipeBackLayout.setOnSwipeBackListener(this);
        
        mSwipeBackLayout.setEnableFlingBack(true);
        
        mSwipeBackLayout.setOnlyLeftEdgeSwipe(false);
        
        mChild_bg = new View(this);
        mChild_bg.setBackgroundColor(0x7f000000);
        container.addView(mChild_bg, new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        
        container.addView(mSwipeBackLayout);
        
        return container;
    }
    
    @Override
    public void onViewPositionChanged(float fractionAnchor, float fractionScreen) {
        mChild_bg.setAlpha(1 - fractionScreen);
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
    
    
   /* protected void setStatusBarColor(@ColorInt int color) {
        setStatusBarColor(color, StatusBarUtil.DEFAULT_STATUS_BAR_ALPHA);
    }
    
    protected void setStatusBarColor(@ColorInt int color, @IntRange(from = 0, to = 255) int statusBarAlpha) {
        StatusBarUtil.setColorForSwipeBack(this, color, statusBarAlpha);
    }*/
    
    /**
     * activity.finish() 操作 使用 ActivityStackUtils 进行管理
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityStackUtils.getInstance().finishActivity();
    }
}
