package com.tjw.template.login;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.tjw.template.R;

/**
 * ^-^
 * Created by tang-jw on 2017/8/29.
 */

public class LoginActivity extends AppCompatActivity implements View.OnFocusChangeListener {
    
    private MyOnGlobalLayoutListener mOnGlobalLayoutListener;
    private View mRootView;
    private ScrollView mScrollView;
    private RelativeLayout mViewById;
    private RelativeLayout mEtlayout1;
    private RelativeLayout mEtLayout2;
    private EditText mEditText1;
    private EditText mEditText2;
    private RelativeLayout mRelativeLayout;
    private int mRelativeLayoutTop;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_logins);
        
        
        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        
        mViewById = (RelativeLayout) findViewById(R.id.rl);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.rl0);
        
        mEtlayout1 = (RelativeLayout) findViewById(R.id.rlet1);
        mEtLayout2 = (RelativeLayout) findViewById(R.id.rlet2);
        
        mEditText1 = (EditText) findViewById(R.id.et_1);
        mEditText2 = (EditText) findViewById(R.id.et_2);
        
        
        mEditText1.setOnFocusChangeListener(this);
        mEditText2.setOnFocusChangeListener(this);
        
        mRootView = ((ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content)).getChildAt(0);
        mOnGlobalLayoutListener = new MyOnGlobalLayoutListener(mRootView, 200, new MyOnGlobalLayoutListener.KeyboardVisibilityListener() {
            @Override
            public void onChange(int positionY) {
                mScrollView.smoothScrollTo(0, 1300 - positionY);
                
            }
        }) {
            @Override
            public void onGlobalLayout() {
                super.onGlobalLayout();
                if (mRelativeLayoutTop == 0) {
                    mRelativeLayoutTop = mRelativeLayout.getTop()
                            + DensityUtils.dip2px(LoginActivity.this, 98)
                            + DensityUtils.getScreenHeight(LoginActivity.this);
                    
                }
            }
        };
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
        
        setupUI(mRootView);
        
    }
    
    @Override
    protected void onDestroy() {
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
        super.onDestroy();
    }
    
    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()) {
            case R.id.et_1:
                mEtlayout1.setBackgroundResource(b ? R.drawable.bg_edit_layout_checked : R.drawable.bg_edit_layout_normal);
                break;
            
            case R.id.et_2:
                mEtLayout2.setBackgroundResource(b ? R.drawable.bg_edit_layout_checked : R.drawable.bg_edit_layout_normal);
                break;
        }
    }
    
    
    public void setupUI(View view) {
        
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(LoginActivity.this);
                    return false;
                }
            });
        }
        
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
    
    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
