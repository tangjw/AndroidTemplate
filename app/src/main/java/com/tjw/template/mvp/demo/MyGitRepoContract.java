package com.tjw.template.mvp.demo;

import com.tjw.template.mvp.BasePresenter;
import com.tjw.template.mvp.BaseView;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface MyGitRepoContract {
    
    interface View extends BaseView<Presenter> {
        
        void showEmptyView();
        
        void showRepoList();
        
        void setTitle(String title);
        
        void setDescription(String description);
        
        boolean isActive();
    }
    
    interface Presenter extends BasePresenter {
        
        void saveTask();
        
        void populateTask();
        
        boolean isDataMissing();
    }
}
