package com.tjw.template.toolbar;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

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
        
        setSupportActionBar(mToolbar);
        
        setToolbar();
    }
    
    /**
     * 设置Toolbar的 相关api
     */
    private void setToolbar() {
        ActionBar actionBar = getSupportActionBar();
        
        if (actionBar == null) {
            return;
        }
        
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        
//        toolbar.setn
//        final Drawable upArrow = getResources().getDrawable(android.R.drawable.abc_ic_ab_back_material);
//        upArrow.setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP);
//        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }
}
