package com.tjw.template.widget.banner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.tjw.template.R;
import com.tjw.template.bean.Banner;

public class ViewBanner extends RelativeLayout implements View.OnClickListener {
    
    private Banner banner;
    private ImageView iv_banner;
    
    public ViewBanner(Context context) {
        super(context, null);
        init(context);
    }
    
    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_news_banner, this, true);
        iv_banner = (ImageView) findViewById(R.id.iv_banner);
        setOnClickListener(this);
    }
    
    public void initData(Banner banner) {
        this.banner = banner;
        Glide.with(getContext()).load(banner.getImg()).into(iv_banner);
    }
    
    
    @Override
    public void onClick(View v) {
        // TODO: 2017/2/21 判断是否需要跳转 
//        ScrollingActivity.show(getContext(),banner.getName());
    }
}
