package com.tjw.template.toolbar;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.tjw.template.R;

/**
 * ToolbarActivity
 */
public class ToolbarActivity extends AppCompatActivity {
    
    private Toolbar mToolbar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar);
        
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        
        //若完全使用 toolbar menu api, 注释掉即可 
//        setSupportActionBar(mToolbar);
        
        setToolbar();
    }
    
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }*/
    
    
    /**
     * 设置Toolbar的 相关api
     */
    private void setToolbar() {
        ActionBar actionBar = getSupportActionBar();
    
        System.out.println("actionbar => "+actionBar);
        if (actionBar != null) {
            // 显示 title 默认为 true ,设为 false Toolbar上的 title才会显示
            actionBar.setDisplayShowTitleEnabled(false);
            // 显示返回箭头 ← 默认不显示
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mToolbar.setLogo(R.mipmap.ic_launcher);
        
        mToolbar.setTitle("我是Toolbar");
        
        mToolbar.setSubtitle("我是小标题");
        
        mToolbar.setNavigationContentDescription("这是什么?");
        
        // 设置返回键图标
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mToolbar.inflateMenu(R.menu.menu_toolbar);

        mToolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_add));
        
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });
    }
}
