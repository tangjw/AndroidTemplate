package com.tjw.template.widget.bottomnav;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v4.util.Pools;
import android.support.v4.view.ViewCompat;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Android on 2017/3/3.
 */

public class BottomNavMenuView extends ViewGroup implements MenuView {
    
    private final int mActiveItemMaxWidth;
    private final int mItemHeight;
    private final OnClickListener mOnClickListener;
    private static final Pools.Pool<BottomNavItemView> sItemPool = new Pools.SynchronizedPool<>(5);
    
    
    private BottomNavItemView[] mButtons;
    private int mActiveButton = 0;
    private ColorStateList mItemIconTint;
    private ColorStateList mItemTextColor;
    private int mItemBackgroundRes;
    private int[] mTempChildWidths;
    
    private BottomNavPresenter mPresenter;
    private MenuBuilder mMenu;
    
    public BottomNavMenuView(Context context) {
        this(context, null);
    }
    
    public BottomNavMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        final Resources res = getResources();
        
        mActiveItemMaxWidth = res.getDimensionPixelSize(
                android.support.design.R.dimen.design_bottom_navigation_active_item_max_width);
        mItemHeight = res.getDimensionPixelSize(android.support.design.R.dimen.design_bottom_navigation_height);
        
        
        mOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomNavItemView itemView = (BottomNavItemView) v;
                final int itemPosition = itemView.getItemPosition();
                if (!mMenu.performItemAction(itemView.getItemData(), mPresenter, 0)) {
                    activateNewButton(itemPosition);
                }
            }
        };
        mTempChildWidths = new int[BottomNavMenu.MAX_ITEM_COUNT];
    }
    
    @Override
    public void initialize(MenuBuilder menu) {
        mMenu = menu;
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int count = getChildCount();
        
        final int heightSpec = MeasureSpec.makeMeasureSpec(mItemHeight, MeasureSpec.EXACTLY);
        
        
        final int maxAvailable = width / (count == 0 ? 1 : count);
        final int childWidth = Math.min(maxAvailable, mActiveItemMaxWidth);
        int extra = width - childWidth * count;
        for (int i = 0; i < count; i++) {
            mTempChildWidths[i] = childWidth;
            if (extra > 0) {
                mTempChildWidths[i]++;
                extra--;
            }
        }
        
        
        int totalWidth = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            child.measure(MeasureSpec.makeMeasureSpec(mTempChildWidths[i], MeasureSpec.EXACTLY),
                    heightSpec);
            ViewGroup.LayoutParams params = child.getLayoutParams();
            params.width = child.getMeasuredWidth();
            totalWidth += child.getMeasuredWidth();
        }
        setMeasuredDimension(
                ViewCompat.resolveSizeAndState(totalWidth,
                        MeasureSpec.makeMeasureSpec(totalWidth, MeasureSpec.EXACTLY), 0),
                ViewCompat.resolveSizeAndState(mItemHeight, heightSpec, 0));
    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();
        final int width = right - left;
        final int height = bottom - top;
        int used = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                child.layout(width - used - child.getMeasuredWidth(), 0, width - used, height);
            } else {
                child.layout(used, 0, child.getMeasuredWidth() + used, height);
            }
            used += child.getMeasuredWidth();
        }
    }
    
    @Override
    public int getWindowAnimations() {
        return 0;
    }
    
    /**
     * Sets the tint which is applied to the menu items' icons.
     *
     * @param tint the tint to apply
     */
    public void setIconTintList(ColorStateList tint) {
        mItemIconTint = tint;
        if (mButtons == null) return;
        for (BottomNavItemView item : mButtons) {
            item.setIconTintList(tint);
        }
    }
    
    /**
     * Returns the tint which is applied to menu items' icons.
     *
     * @return the ColorStateList that is used to tint menu items' icons
     */
    @Nullable
    public ColorStateList getIconTintList() {
        return mItemIconTint;
    }
    
    /**
     * Sets the text color to be used on menu items.
     *
     * @param color the ColorStateList used for menu items' text.
     */
    public void setItemTextColor(ColorStateList color) {
        mItemTextColor = color;
        if (mButtons == null) return;
        for (BottomNavItemView item : mButtons) {
            item.setTextColor(color);
        }
    }
    
    /**
     * Returns the text color used on menu items.
     *
     * @return the ColorStateList used for menu items' text
     */
    public ColorStateList getItemTextColor() {
        return mItemTextColor;
    }
    
    /**
     * Sets the resource ID to be used for item background.
     *
     * @param background the resource ID of the background
     */
    public void setItemBackgroundRes(int background) {
        mItemBackgroundRes = background;
        if (mButtons == null) return;
        for (BottomNavItemView item : mButtons) {
            item.setItemBackground(background);
        }
    }
    
    /**
     * Returns the resource ID for the background of the menu items.
     *
     * @return the resource ID for the background
     */
    public int getItemBackgroundRes() {
        return mItemBackgroundRes;
    }
    
    public void setPresenter(BottomNavPresenter presenter) {
        mPresenter = presenter;
    }
    
    public void buildMenuView() {
        if (mButtons != null) {
            for (BottomNavItemView item : mButtons) {
                sItemPool.release(item);
            }
        }
        removeAllViews();
        if (mMenu.size() == 0) {
            mButtons = null;
            return;
        }
        mButtons = new BottomNavItemView[mMenu.size()];
        for (int i = 0; i < mMenu.size(); i++) {
            mPresenter.setUpdateSuspended(true);
            mMenu.getItem(i).setCheckable(true);
            mPresenter.setUpdateSuspended(false);
            BottomNavItemView child = getNewItem();
            mButtons[i] = child;
            child.setIconTintList(mItemIconTint);
            child.setTextColor(mItemTextColor);
            child.setItemBackground(mItemBackgroundRes);
            child.initialize((MenuItemImpl) mMenu.getItem(i), 0);
            child.setItemPosition(i);
            child.setOnClickListener(mOnClickListener);
            addView(child);
        }
        mActiveButton = Math.min(mMenu.size() - 1, mActiveButton);
        mMenu.getItem(mActiveButton).setChecked(true);
    }
    
    public void updateMenuView() {
        final int menuSize = mMenu.size();
        if (menuSize != mButtons.length) {
            // The size has changed. Rebuild menu view from scratch.
            buildMenuView();
            return;
        }
        for (int i = 0; i < menuSize; i++) {
            mPresenter.setUpdateSuspended(true);
            if (mMenu.getItem(i).isChecked()) {
                mActiveButton = i;
            }
            mButtons[i].initialize((MenuItemImpl) mMenu.getItem(i), 0);
            mPresenter.setUpdateSuspended(false);
        }
    }
    
    private void activateNewButton(int newButton) {
        if (mActiveButton == newButton) return;
        
        
        mMenu.getItem(newButton).setChecked(true);
        
        mActiveButton = newButton;
    }
    
    private BottomNavItemView getNewItem() {
        BottomNavItemView item = sItemPool.acquire();
        if (item == null) {
            item = new BottomNavItemView(getContext());
        }
        return item;
    }
}
