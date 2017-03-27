package com.tjw.selectimage.album.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * CopyRight
 * Created by tang-jw on 2016/7/28.
 */
public class ImagePreviewViewPager extends ViewPager {
    public ImagePreviewViewPager(Context context) {
        super(context);
    }
    
    public ImagePreviewViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            //uncomment if you really want to see these errors
//			e.printStackTrace();
            return false;
        }
    }
}
