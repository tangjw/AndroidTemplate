package com.tjw.template.widget.bottomnav;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationMenu;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.tjw.template.R;

/**
 * Created by Android on 2017/3/3.
 */

public class BottomNavView extends FrameLayout {
    
    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};
    private static final int[] DISABLED_STATE_SET = {-android.R.attr.state_enabled};
    
    private final MenuBuilder mMenu;
    private final BottomNavMenuView mMenuView;
    private final BottomNavPresenter mPresenter = new BottomNavPresenter();
    private MenuInflater mMenuInflater;
    
    private OnNavItemSelectedListener mListener;
    
    public BottomNavView(Context context) {
        this(context, null);
    }
    
    public BottomNavView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    @SuppressLint("PrivateResource")
    public BottomNavView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        
        
        // Create the menu
        mMenu = new BottomNavigationMenu(context);
        
        mMenuView = new BottomNavMenuView(context);
        
        if (isInEditMode()) {
            return;
        }
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        mMenuView.setLayoutParams(params);
        
        mPresenter.setBottomNavMenuView(mMenuView);
        mMenuView.setPresenter(mPresenter);
        mMenu.addMenuPresenter(mPresenter);
        mPresenter.initForMenu(getContext(), mMenu);
        
        // Custom attributes
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs,
                android.support.design.R.styleable.BottomNavigationView, defStyleAttr,
                android.support.design.R.style.Widget_Design_BottomNavigationView);
        
        if (a.hasValue(android.support.design.R.styleable.BottomNavigationView_itemIconTint)) {
            mMenuView.setIconTintList(
                    a.getColorStateList(android.support.design.R.styleable.BottomNavigationView_itemIconTint));
        } else {
            mMenuView.setIconTintList(
                    createDefaultColorStateList(android.R.attr.textColorSecondary));
        }
        if (a.hasValue(android.support.design.R.styleable.BottomNavigationView_itemTextColor)) {
            mMenuView.setItemTextColor(
                    a.getColorStateList(android.support.design.R.styleable.BottomNavigationView_itemTextColor));
        } else {
            mMenuView.setItemTextColor(
                    createDefaultColorStateList(android.R.attr.textColorSecondary));
        }
        if (a.hasValue(android.support.design.R.styleable.BottomNavigationView_elevation)) {
            ViewCompat.setElevation(this, a.getDimensionPixelSize(
                    android.support.design.R.styleable.BottomNavigationView_elevation, 0));
        }
        
        int itemBackground = a.getResourceId(android.support.design.R.styleable.BottomNavigationView_itemBackground, 0);
        mMenuView.setItemBackgroundRes(itemBackground);
        
        if (a.hasValue(android.support.design.R.styleable.BottomNavigationView_menu)) {
            inflateMenu(a.getResourceId(android.support.design.R.styleable.BottomNavigationView_menu, 0));
        }
        a.recycle();
        
        addView(mMenuView, params);
//        if (Build.VERSION.SDK_INT < 21) {
        addCompatibilityTopDivider(context);
//        }
        
        mMenu.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                return mListener != null && !mListener.onNavigationItemSelected(item);
            }
            
            @Override
            public void onMenuModeChange(MenuBuilder menu) {
            }
        });
    }
    
    /**
     * Set a listener that will be notified when a bottom navigation item is selected.
     *
     * @param listener The listener to notify
     */
    public void setOnNavItemSelectedListener(
            @Nullable OnNavItemSelectedListener listener) {
        mListener = listener;
    }
    
    /**
     * Returns the {@link Menu} instance associated with this bottom navigation bar.
     */
    @NonNull
    public Menu getMenu() {
        return mMenu;
    }
    
    /**
     * Inflate a menu resource into this navigation view.
     * <p>
     * <p>Existing items in the menu will not be modified or removed.</p>
     *
     * @param resId ID of a menu resource to inflate
     */
    public void inflateMenu(int resId) {
        mPresenter.setUpdateSuspended(true);
        getMenuInflater().inflate(resId, mMenu);
        mPresenter.setUpdateSuspended(false);
        mPresenter.updateMenuView(true);
    }
    
    /**
     * @return The maximum number of items that can be shown in BottomNavigationView.
     */
    public int getMaxItemCount() {
        return BottomNavigationMenu.MAX_ITEM_COUNT;
    }
    
    /**
     * Returns the tint which is applied to our menu items' icons.
     *
     * @attr ref R.styleable#BottomNavigationView_itemIconTint
     * @see #setItemIconTintList(ColorStateList)
     */
    @Nullable
    public ColorStateList getItemIconTintList() {
        return mMenuView.getIconTintList();
    }
    
    /**
     * Set the tint which is applied to our menu items' icons.
     *
     * @param tint the tint to apply.
     * @attr ref R.styleable#BottomNavigationView_itemIconTint
     */
    public void setItemIconTintList(@Nullable ColorStateList tint) {
        mMenuView.setIconTintList(tint);
    }
    
    /**
     * Returns colors used for the different states (normal, selected, focused, etc.) of the menu
     * item text.
     *
     * @return the ColorStateList of colors used for the different states of the menu items text.
     * @attr ref R.styleable#BottomNavigationView_itemTextColor
     * @see #setItemTextColor(ColorStateList)
     */
    @Nullable
    public ColorStateList getItemTextColor() {
        return mMenuView.getItemTextColor();
    }
    
    /**
     * Set the colors to use for the different states (normal, selected, focused, etc.) of the menu
     * item text.
     *
     * @attr ref R.styleable#BottomNavigationView_itemTextColor
     * @see #getItemTextColor()
     */
    public void setItemTextColor(@Nullable ColorStateList textColor) {
        mMenuView.setItemTextColor(textColor);
    }
    
    /**
     * Returns the background resource of the menu items.
     *
     * @attr ref R.styleable#BottomNavigationView_itemBackground
     * @see #setItemBackgroundResource(int)
     */
    @DrawableRes
    public int getItemBackgroundResource() {
        return mMenuView.getItemBackgroundRes();
    }
    
    /**
     * Set the background of our menu items to the given resource.
     *
     * @param resId The identifier of the resource.
     * @attr ref R.styleable#BottomNavigationView_itemBackground
     */
    public void setItemBackgroundResource(@DrawableRes int resId) {
        mMenuView.setItemBackgroundRes(resId);
    }
    
    /**
     * Listener for handling events on bottom navigation items.
     */
    public interface OnNavItemSelectedListener {
        
        /**
         * Called when an item in the bottom navigation menu is selected.
         *
         * @param item The selected item
         * @return true to display the item as the selected item and false if the item should not
         * be selected. Consider setting non-selectable items as disabled preemptively to
         * make them appear non-interactive.
         */
        boolean onNavigationItemSelected(@NonNull MenuItem item);
    }
    
    private void addCompatibilityTopDivider(Context context) {
        View divider = new View(context);
        divider.setBackgroundColor(
                ContextCompat.getColor(context, R.color.colorDivider));
        FrameLayout.LayoutParams dividerParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 1);
        divider.setLayoutParams(dividerParams);
        addView(divider);
    }
    
    private MenuInflater getMenuInflater() {
        if (mMenuInflater == null) {
            mMenuInflater = new SupportMenuInflater(getContext());
        }
        return mMenuInflater;
    }
    
    private ColorStateList createDefaultColorStateList(int baseColorThemeAttr) {
        final TypedValue value = new TypedValue();
        if (!getContext().getTheme().resolveAttribute(baseColorThemeAttr, value, true)) {
            return null;
        }
        ColorStateList baseColor = AppCompatResources.getColorStateList(
                getContext(), value.resourceId);
        if (!getContext().getTheme().resolveAttribute(
                android.support.v7.appcompat.R.attr.colorPrimary, value, true)) {
            return null;
        }
        int colorPrimary = value.data;
        int defaultColor = baseColor.getDefaultColor();
        return new ColorStateList(new int[][]{
                DISABLED_STATE_SET,
                CHECKED_STATE_SET,
                EMPTY_STATE_SET
        }, new int[]{
                baseColor.getColorForState(DISABLED_STATE_SET, defaultColor),
                colorPrimary,
                defaultColor
        });
    }
}
