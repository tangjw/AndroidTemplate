package com.tjw.template.widget.banner;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tjw.template.R;
import com.tjw.template.bean.Banner;

import java.util.List;

public class SimpleHeaderView extends HeaderView {
    
    private TextView mTitleTextView;
    
    public SimpleHeaderView(Context context) {
        super(context);
    }
    
    @Override
    protected void init() {
        super.init();
        mTitleTextView = (TextView) findViewById(R.id.tv_title);
    }
    
    @Override
    protected int getLayoutId() {
        return R.layout.layout_news_header_view;
    }
    
    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);
        
        if (mBannerList.size() != 0) {
            mTitleTextView.setText("标题" + mBannerList.get(position % mBannerList.size()).getName());
        }
    }
    
    
    @Override
    public void loadBanner(List<Banner> bannerList) {
        super.loadBanner(bannerList);
        
        if (bannerList.size() > 0 && mCurrentItem == 0) {
            mTitleTextView.setText(bannerList.get(0).getName());
        }
    }
    
    @Override
    protected Object instantiateItem(ViewGroup container, int position) {
        ViewBanner view = new ViewBanner(getContext());
        if (mBannerList.size() != 0) {
            int p = position % mBannerList.size();
            if (p >= 0 && p < mBannerList.size()) {
                view.initData(mBannerList.get(p));
                
            }
        }
        container.addView(view);
        return view;
    }
    
    @Override
    protected void destroyItem(ViewGroup container, int position, Object object) {
        if (object instanceof ViewBanner) {
            container.removeView((ViewBanner) object);
        }
    }
}
