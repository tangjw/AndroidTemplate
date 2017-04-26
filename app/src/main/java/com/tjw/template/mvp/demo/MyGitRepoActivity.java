package com.tjw.template.mvp.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.tjw.template.R;
import com.tjw.template.bean.Repo;
import com.tjw.template.swipeback.main.adapter.Main1RecyclerViewAdapter;
import com.tjw.template.widget.banner.SimpleHeaderView;
import com.tjw.template.widget.recycler.DividerItemDecoration;

import java.util.List;

/**
 * ^-^
 * Created by tang-jw on 2017/4/24.
 */

public class MyGitRepoActivity extends AppCompatActivity implements MyGitRepoContract.View {
    
    private Main1RecyclerViewAdapter mAdapter;
    
    private RecyclerView mRecyclerView;
    private ImageView mImageView;
    private MyGitRepoContract.Presenter mPresenter;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_mygitrepo);
        
        mRecyclerView = (RecyclerView) findViewById(R.id.rl_mygitrepo);
        initRecyclerView();
        mImageView = (ImageView) findViewById(R.id.iv_empty);
    
        MyGitRepoPresenter presenter = new MyGitRepoPresenter(this);
    
        mPresenter.getRepos("tangjw");
    }
    
    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        
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
    public void showRepoList(List<Repo> repos) {
        if (mAdapter == null) {
            mAdapter = new Main1RecyclerViewAdapter(this, repos);
            SimpleHeaderView simpleHeaderView = new SimpleHeaderView(this);
            mAdapter.setHeaderView(simpleHeaderView);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setDataList(repos);
        }
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
