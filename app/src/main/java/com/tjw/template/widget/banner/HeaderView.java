package com.tjw.template.widget.banner;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.tjw.template.R;
import com.tjw.template.bean.Banner;
import com.tjw.template.widget.indicator.CirclePagerIndicator;

import java.util.ArrayList;
import java.util.List;


public abstract class HeaderView extends RelativeLayout implements ViewPager.OnPageChangeListener, Runnable {
    
    protected ViewPager mViewPager;
    
    protected CirclePagerIndicator mIndicator;
    
    protected BannerAdapter mAdapter;
    
    protected Handler mHandler;
    
    protected int mCurrentItem;
    
    protected List<Banner> mBannerList;
    
    private boolean isScrolling;
    
    public HeaderView(Context context) {
        super(context);
        init();
    }
    
    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    protected void init() {
        
        mBannerList = new ArrayList<>();
        
        // TODO: 2017/2/21 尝试从缓存中取到List 
        
        LayoutInflater.from(getContext()).inflate(getLayoutId(), this, true);
        
        mViewPager = (ViewPager) findViewById(R.id.vp_banner);
        mIndicator = (CirclePagerIndicator) findViewById(R.id.indicator);
        
        mAdapter = new BannerAdapter();
        
        mViewPager.addOnPageChangeListener(this);
        
        mViewPager.setAdapter(mAdapter);
        
        mIndicator.bindViewPager(mViewPager);
        
        mIndicator.setCount(mBannerList.size());
        
        new SmoothScroller(getContext()).bingViewPager(mViewPager);
        
        mViewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isScrolling = true;
                    case MotionEvent.ACTION_UP:
                        isScrolling = false;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        isScrolling = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        isScrolling = true;
                        break;
                }
                return false;
            }
        });
        
        loadBanner(mBannerList);
    }
    
    @Override
    public void run() {
        mHandler.postDelayed(this, 5000);
        if (isScrolling) {
            return;
        }
        mCurrentItem = mCurrentItem + 1;
        mViewPager.setCurrentItem(mCurrentItem);
    }
    
    public void loadBanner(List<Banner> bannerList) {
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.removeCallbacks(this);
        
        if (bannerList != null) {
            mHandler.removeCallbacks(this);
            mBannerList.clear();
            mBannerList.addAll(bannerList);
            mViewPager.getAdapter().notifyDataSetChanged();
            mIndicator.setCount(mBannerList.size());
            mIndicator.notifyDataSetChanged();
            if (mCurrentItem == 0 && mBannerList.size() != 1) {
                mCurrentItem = mBannerList.size() * 1000;
                mViewPager.setCurrentItem(mCurrentItem);
            }
            if (mBannerList.size() > 1) {
                mHandler.postDelayed(this, 5000);
            }
        }
        
        
    }
    
    protected abstract int getLayoutId();
    
    protected abstract Object instantiateItem(ViewGroup container, int position);
    
    protected abstract void destroyItem(ViewGroup container, int position, Object object);
    
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        isScrolling = mCurrentItem != position;
    }
    
    @Override
    public void onPageSelected(int position) {
        isScrolling = false;
        mCurrentItem = position;
    }
    
    @Override
    public void onPageScrollStateChanged(int state) {
        isScrolling = state != ViewPager.SCROLL_STATE_IDLE;
    }
    
    private class BannerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mBannerList.size() == 1 ? 1 : Integer.MAX_VALUE;
        }
        
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
        
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return HeaderView.this.instantiateItem(container, position);
        }
        
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            HeaderView.this.destroyItem(container, position, object);
        }
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mHandler == null)
            mHandler = new Handler();
        mHandler.postDelayed(this, 5000);
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mHandler == null)
            return;
        mHandler.removeCallbacks(this);
        mHandler = null;
    }
}
