package com.tjw.template.widget.bottomnav;

import android.content.Context;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.view.MenuItem;
import android.view.SubMenu;

/**
 * Created by Android on 2017/3/3.
 */

public class BottomNavMenu extends MenuBuilder {
    public static final int MAX_ITEM_COUNT = 5;
    
    public BottomNavMenu(Context context) {
        super(context);
    }
    
    @Override
    public SubMenu addSubMenu(int group, int id, int categoryOrder, CharSequence title) {
        throw new UnsupportedOperationException("BottomNavigationView does not support submenus");
    }
    
    @Override
    protected MenuItem addInternal(int group, int id, int categoryOrder, CharSequence title) {
        if (size() + 1 > MAX_ITEM_COUNT) {
            throw new IllegalArgumentException(
                    "Maximum number of items supported by BottomNavigationView is " + MAX_ITEM_COUNT
                            + ". Limit can be checked with BottomNavigationView#getMaxItemCount()");
        }
        stopDispatchingItemsChanged();
        final MenuItem item = super.addInternal(group, id, categoryOrder, title);
        if (item instanceof MenuItemImpl) {
            ((MenuItemImpl) item).setExclusiveCheckable(true);
        }
        startDispatchingItemsChanged();
        return item;
    }
}
