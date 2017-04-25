package com.tjw.template.mvp.demo;

import com.tjw.template.bean.Repo;
import com.tjw.template.mvp.BasePresenter;
import com.tjw.template.mvp.BaseView;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface MyGitRepoContract {
    
    interface View extends BaseView<Presenter> {
        
        void showEmptyView();
    
        void showRepoList(List<Repo> repos);
        
        void setTitle(String title);
        
        void setDescription(String description);
        
        boolean isActive();
    }
    
    interface Presenter extends BasePresenter {
    
        void getRepos(String username);
        
        void populateTask();
        
        boolean isDataMissing();
    }
}
