package com.tjw.template.widget.bottomnav;

import android.content.Context;
import android.os.Parcelable;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.view.menu.MenuView;
import android.support.v7.view.menu.SubMenuBuilder;
import android.view.ViewGroup;

/**
 * Created by Android on 2017/3/3.
 */

public class BottomNavPresenter implements MenuPresenter {
    private MenuBuilder mMenu;
    private BottomNavMenuView mMenuView;
    private boolean mUpdateSuspended = false;
    
    public void setBottomNavMenuView(BottomNavMenuView menuView) {
        mMenuView = menuView;
    }
    
    @Override
    public void initForMenu(Context context, MenuBuilder menu) {
        mMenuView.initialize(mMenu);
        mMenu = menu;
    }
    
    @Override
    public MenuView getMenuView(ViewGroup root) {
        return mMenuView;
    }
    
    @Override
    public void updateMenuView(boolean cleared) {
        if (mUpdateSuspended) return;
        if (cleared) {
            mMenuView.buildMenuView();
        } else {
            mMenuView.updateMenuView();
        }
    }
    
    @Override
    public void setCallback(Callback cb) {
    }
    
    @Override
    public boolean onSubMenuSelected(SubMenuBuilder subMenu) {
        return false;
    }
    
    @Override
    public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
    }
    
    @Override
    public boolean flagActionItems() {
        return false;
    }
    
    @Override
    public boolean expandItemActionView(MenuBuilder menu, MenuItemImpl item) {
        return false;
    }
    
    @Override
    public boolean collapseItemActionView(MenuBuilder menu, MenuItemImpl item) {
        return false;
    }
    
    @Override
    public int getId() {
        return -1;
    }
    
    @Override
    public Parcelable onSaveInstanceState() {
        return null;
    }
    
    @Override
    public void onRestoreInstanceState(Parcelable state) {
    }
    
    public void setUpdateSuspended(boolean updateSuspended) {
        mUpdateSuspended = updateSuspended;
    }
}
