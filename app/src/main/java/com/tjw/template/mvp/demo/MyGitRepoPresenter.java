package com.tjw.template.mvp.demo;

import com.tjw.template.bean.Repo;
import com.tjw.template.net.HttpMethods;

import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * ^-^
 * Created by tang-jw on 2017/4/24.
 */

public class MyGitRepoPresenter implements MyGitRepoContract.Presenter {
    
    
    private MyGitRepoContract.View mView;
    
    public MyGitRepoPresenter(MyGitRepoContract.View view) {
        mView = view;
        mView.setPresenter(this);
    }
    
    @Override
    public void subscribe() {
        
    }
    
    @Override
    public void unsubscribe() {
        
    }
    
    @Override
    public void getRepos(@NonNull String username) {
        HttpMethods.getInstance().getRepos(username, new Consumer<List<Repo>>() {
            @Override
            public void accept(@NonNull List<Repo> repos) throws Exception {
                if (repos.size() > 0) {
                    mView.showRepoList(repos);
                } else {
                    mView.showEmptyView();
                }
            }
        });
        
    }
    
    @Override
    public void populateTask() {
        
    }
    
    @Override
    public boolean isDataMissing() {
        return false;
    }
}
