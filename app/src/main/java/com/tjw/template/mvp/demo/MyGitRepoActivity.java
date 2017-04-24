package com.tjw.template.mvp.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.tjw.template.R;

/**
 * ^-^
 * Created by tang-jw on 2017/4/24.
 */

public class MyGitRepoActivity extends AppCompatActivity implements MyGitRepoContract.View {
    
    private RecyclerView mRecyclerView;
    private ImageView mImageView;
    private MyGitRepoContract.Presenter mPresenter;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_mygitrepo);
        
        mRecyclerView = (RecyclerView) findViewById(R.id.rl_mygitrepo);
        mImageView = (ImageView) findViewById(R.id.iv_empty);
        
        setPresenter(new MyGitRepoPresenter());
        
        mPresenter.saveTask();
    }
    
    @Override
    public void setPresenter(MyGitRepoContract.Presenter presenter) {
        mPresenter = presenter;
    }
    
    @Override
    public void showEmptyView() {
        mImageView.setVisibility(View.VISIBLE);
    }
    
    @Override
    public void showRepoList() {
        
    }
    
    @Override
    public void setTitle(String title) {
        
    }
    
    @Override
    public void setDescription(String description) {
        
    }
    
    @Override
    public boolean isActive() {
        return false;
    }
}
