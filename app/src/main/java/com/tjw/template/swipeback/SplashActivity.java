package com.tjw.template.swipeback;

import android.content.Intent;
import android.os.Handler;
import android.widget.ImageView;

import com.tjw.template.R;

/**
 * ^-^
 * Created by tang-jw on 2017/4/7.
 */

public class SplashActivity extends BaseActivity {
    
    @Override
    protected void initView() {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.bg_sky);
        imageView.setBackgroundResource(R.color.colorDivider);
        setContentView(imageView);
    }
    
    @Override
    protected void loadData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
                overridePendingTransition(0, 0);
            }
        }, 2000L);
    }
}
