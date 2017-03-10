package com.tjw.template.swipeback.main;

import com.tjw.template.base.BasePresenter;
import com.tjw.template.base.BaseView;

/**
 * ^-^ 指定 View 和 Presenter 之间的合同
 * Created by tang-jw on 2017/3/9.
 */

public interface MainContract {
    
    interface View extends BaseView<Presenter> {
        
    }
    
    
    interface Presenter extends BasePresenter {
        
        
        
    }
}
