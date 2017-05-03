package com.tjw.template.swipeback;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.tjw.template.R;

/**
 * ^-^
 * Created by tang-jw on 2017/5/3.
 */

public class LoginActivity extends BaseSwipeBackActivity {
    
    private Toolbar mToolbar;
    
    @Override
    protected void initView() {
        setContentView(R.layout.activity_login);
        
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolbar();
        
    }
    
    private void setToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        
        if (actionBar != null) {
            // 显示 title 默认为 true ,设为 false Toolbar上的 title才会显示
            actionBar.setDisplayShowTitleEnabled(false);
            // 显示返回箭头 ← 默认不显示
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        
        mToolbar.setContentInsetStartWithNavigation(0);
        
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
    }
}
