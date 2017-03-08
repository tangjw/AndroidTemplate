package com.tjw.template;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tjw.template.swipeback.BaseActivity;
import com.tjw.template.toolbar.ToolbarActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    
    private TextView mTextView;
    
    @Override
    protected void beforeSuperOnCreate() {
        
    }
    
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.textView);
        
        mTextView.setText("当时发生的发生的发生");
    }
    
    @Override
    protected void setListener() {
        mTextView.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.textView) {
            startActivity(new Intent(this, ToolbarActivity.class));
        }
    }
    
    @Override
    public boolean isSupportSwipeBack() {
        return false;
    }
}
