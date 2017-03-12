package com.tjw.template.swipeback;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.tjw.template.R;
import com.tjw.template.util.ActivityStackUtils;
import com.tjw.template.util.StatusBarUtil;

import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;

/**
 * ^-^
 * 必须在 Application 的 onCreate 方法中执行 BGASwipeBackManager.getInstance().init(this)
 * 来初始化滑动返回
 * Created by tang-jw on 2017/3/7.
 */

public abstract class BaseActivity extends AppCompatActivity
        implements BGASwipeBackHelper.Delegate {
    
    protected BGASwipeBackHelper mSwipeBackHelper;
    
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 在 super.onCreate(savedInstanceState) 之前调用该方法
        initSwipeBackFinish();
    
        beforeSuperOnCreate();
        
        super.onCreate(savedInstanceState);
        // 将BaseActivity 添加到栈管理
        ActivityStackUtils.getInstance().addActivity(this);
        
        initView(savedInstanceState);
        setListener();
    
        loadData();
        
    }
    
    protected void loadData(){}
    
    protected void beforeSuperOnCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
    
    /**
     * 初始化滑动返回
     */
    private void initSwipeBackFinish() {
        mSwipeBackHelper = new BGASwipeBackHelper(this, this);
        
        // 设置滑动返回是否可用。默认值为 true
        mSwipeBackHelper.setSwipeBackEnable(true);
        // 设置是否仅仅跟踪左侧边缘的滑动返回。默认值为 true
        mSwipeBackHelper.setIsOnlyTrackingLeftEdge(true);
        // 设置是否是微信滑动返回样式。默认值为 true
        mSwipeBackHelper.setIsWeChatStyle(true);
        // 设置阴影资源 id。默认值为 R.drawable.bga_sbl_shadow
        mSwipeBackHelper.setShadowResId(R.drawable.bga_sbl_shadow);
        // 设置是否显示滑动返回的阴影效果。默认值为 true
        mSwipeBackHelper.setIsNeedShowShadow(true);
        // 设置阴影区域的透明度是否根据滑动的距离渐变。默认值为 true
        mSwipeBackHelper.setIsShadowAlphaGradient(true);
        mSwipeBackHelper.setIsOnlyTrackingLeftEdge(false);
    }
    
    @Override
    public boolean isSupportSwipeBack() {
        return true;
    }
    
    @Override
    public void onSwipeBackLayoutSlide(float slideOffset) {
        
    }
    
    @Override
    public void onSwipeBackLayoutCancel() {
        
    }
    
    @Override
    public void onSwipeBackLayoutExecuted() {
        //滑动执行完毕后 关闭Activity
        mSwipeBackHelper.swipeBackward();
    }
    
    protected abstract void initView(Bundle savedInstanceState);
    
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
    protected void onDestroy() {
        super.onDestroy();
        ActivityStackUtils.getInstance().finishActivity();
    }
}
