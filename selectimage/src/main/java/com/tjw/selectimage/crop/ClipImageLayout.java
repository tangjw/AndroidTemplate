package com.tjw.selectimage.crop;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;


/**
 * http://blog.csdn.net/lmj623565791/article/details/39761281
 *
 * @author zhy
 */
public class ClipImageLayout extends RelativeLayout {
    
    private ClipZoomImageView mZoomImageView;
    private ClipImageBorderView mClipImageView;
    
    
    public ClipZoomImageView getZoomImageView() {
        return mZoomImageView;
    }
    
    public ClipImageBorderView getClipImageView() {
        return mClipImageView;
    }
    
    /**
     * 这里测试，直接写死了大小，真正使用过程中，可以提取为自定义属性
     */
    private int mHorizontalPadding = 20;
    
    public ClipImageLayout(Context context) {
        this(context, null);
    }
    
    public ClipImageLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    
    public ClipImageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    
    private void init(Context context) {
        mZoomImageView = new ClipZoomImageView(context);
        mClipImageView = new ClipImageBorderView(context);
        
        android.view.ViewGroup.LayoutParams lp = new LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        
        /**
         * 这里测试，直接写死了图片，真正使用过程中，可以提取为自定义属性
         */
//        mZoomImageView.setImageDrawable(getResources().getDrawable(
//                R.drawable.iidid));
        
        this.addView(mZoomImageView, lp);
        this.addView(mClipImageView, lp);
        
        
        // 计算padding的px
        mHorizontalPadding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, mHorizontalPadding, getResources()
                        .getDisplayMetrics());
        mZoomImageView.setHorizontalPadding(mHorizontalPadding);
        mClipImageView.setHorizontalPadding(mHorizontalPadding);
    }
    
    
    /**
     * 对外公布设置边距的方法,单位为dp
     *
     * @param mHorizontalPadding
     */
    public void setHorizontalPadding(int mHorizontalPadding) {
        this.mHorizontalPadding = mHorizontalPadding;
    }
    
    /**
     * 裁切图片
     *
     * @return
     */
    public Bitmap clip() {
        return mZoomImageView.clip();
    }
    
}
