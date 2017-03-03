package com.tjw.template.widget.bottomnav;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.PointerIconCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tjw.template.util.DensityUtil;

public class BottomNavItemView extends FrameLayout implements MenuView.ItemView {
    public static final int INVALID_ITEM_POSITION = -1;
    
    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};
    
    private ImageView mIcon;
    private final TextView mSmallLabel;
    private int mItemPosition = INVALID_ITEM_POSITION;
    
    private MenuItemImpl mItemData;
    
    private ColorStateList mIconTint;
    private ImageView mIvRedPoint;
    
    public BottomNavItemView(@NonNull Context context) {
        this(context, null);
    }
    
    public BottomNavItemView(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public BottomNavItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final Resources res = getResources();
        int inactiveLabelSize = res.getDimensionPixelSize(android.support.design.R.dimen.design_bottom_navigation_text_size);
        LayoutInflater.from(context).inflate(android.support.design.R.layout.design_bottom_navigation_item, this, true);
        setBackgroundResource(android.support.design.R.drawable.design_bottom_navigation_item_background);
        mIcon = (ImageView) findViewById(android.support.design.R.id.icon);
        mSmallLabel = (TextView) findViewById(android.support.design.R.id.smallLabel);
        mSmallLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (0.9 * inactiveLabelSize));
        
        //-------添加小红点------- 
        mIvRedPoint = new ImageView(context);
        mIvRedPoint.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mIvRedPoint.setImageResource(com.tjw.template.R.drawable.drawable_msg_point);
        
        LayoutParams pointParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pointParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        pointParams.topMargin = DensityUtil.dip2px(context, 7.5f);
        pointParams.leftMargin = DensityUtil.dip2px(context, 12f);
        mIvRedPoint.setLayoutParams(pointParams);
        addView(mIvRedPoint);

//        LayoutParams pointTextParams = new LayoutParams(48,48);
//        TextView textView = new TextView(context);
//        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
//        textView.setTextColor(Color.WHITE);
//        textView.setGravity(Gravity.CENTER);
//        textView.setBackgroundResource(com.tjw.template.R.drawable.drawable_msg_point);
//        pointTextParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
//        pointTextParams.topMargin = (int) (0.75*mDefaultMargin);
//        pointTextParams.leftMargin = (int) (1.8*mDefaultMargin);
//        textView.setLayoutParams(pointTextParams);
//        textView.setText("1");
//    
//        addView(textView);
        
        //-------添加小红点------- 
        
        showRedPoint(false);
        
    }
    
    /**
     * 是否显示BottomNav 的小红点
     *
     * @param isShow true显示 false不显示
     */
    public void showRedPoint(boolean isShow) {
        mIvRedPoint.setVisibility(isShow ? VISIBLE : INVISIBLE);
    }
    
    @Override
    public void initialize(MenuItemImpl itemData, int menuType) {
        mItemData = itemData;
        setCheckable(itemData.isCheckable());
        setChecked(itemData.isChecked());
        setEnabled(itemData.isEnabled());
        setIcon(itemData.getIcon());
        setTitle(itemData.getTitle());
        setId(itemData.getItemId());
    }
    
    public void setItemPosition(int position) {
        mItemPosition = position;
    }
    
    public int getItemPosition() {
        return mItemPosition;
    }
    
    @Override
    public MenuItemImpl getItemData() {
        return mItemData;
    }
    
    @Override
    public void setTitle(CharSequence title) {
        mSmallLabel.setText(title);
    }
    
    @Override
    public void setCheckable(boolean checkable) {
        refreshDrawableState();
    }
    
    @Override
    public void setChecked(boolean checked) {
//        ViewCompat.setPivotX(mSmallLabel, mSmallLabel.getWidth() / 2);
//        ViewCompat.setPivotY(mSmallLabel, mSmallLabel.getBaseline());
//        
//        if (checked) {
//            mSmallLabel.setVisibility(VISIBLE);
//        } else {
//            mSmallLabel.setVisibility(VISIBLE);
//            
//        }
        
        refreshDrawableState();
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mSmallLabel.setEnabled(enabled);
        mIcon.setEnabled(enabled);
        
        if (enabled) {
            ViewCompat.setPointerIcon(this,
                    PointerIconCompat.getSystemIcon(getContext(), PointerIconCompat.TYPE_HAND));
        } else {
            ViewCompat.setPointerIcon(this, null);
        }
        
    }
    
    @Override
    public int[] onCreateDrawableState(final int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (mItemData != null && mItemData.isCheckable() && mItemData.isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }
    
    @Override
    public void setShortcut(boolean showShortcut, char shortcutKey) {
    }
    
    @Override
    public void setIcon(Drawable icon) {
        if (icon != null) {
            Drawable.ConstantState state = icon.getConstantState();
            icon = DrawableCompat.wrap(state == null ? icon : state.newDrawable()).mutate();
            DrawableCompat.setTintList(icon, mIconTint);
        }
        mIcon.setImageDrawable(icon);
    }
    
    @Override
    public boolean prefersCondensedTitle() {
        return false;
    }
    
    @Override
    public boolean showsIcon() {
        return true;
    }
    
    public void setIconTintList(ColorStateList tint) {
        mIconTint = tint;
        if (mItemData != null) {
            // Update the icon so that the tint takes effect
            setIcon(mItemData.getIcon());
        }
    }
    
    public void setTextColor(ColorStateList color) {
        mSmallLabel.setTextColor(color);
    }
    
    public void setItemBackground(int background) {
        Drawable backgroundDrawable = background == 0
                ? null : ContextCompat.getDrawable(getContext(), background);
        ViewCompat.setBackground(this, backgroundDrawable);
    }
}
