package com.tjw.template.widget.banner;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class BannerView extends ViewPager implements Runnable {
    private Handler mHandler;
    private int mDelay;
    
    public BannerView(Context context) {
        super(context);
    }
    
    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }
        return super.onTouchEvent(ev);
    }
    
    @Override
    public void run() {
        
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mHandler = new Handler();
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }
    
}
