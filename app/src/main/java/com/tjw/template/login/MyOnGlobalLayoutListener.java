package com.tjw.template.login;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * ^-^
 * Created by tang-jw on 7/1.
 */

public class MyOnGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
    
    private int rootBottom;
    
    private View rootView;
    //软键盘改变布局的高度 最小参考值
    private int keyboardHeight = 200;
    private KeyboardVisibilityListener mListener;
    
    /**
     * 通过监听整个rootView的位置变化，判断键盘是否弹出
     *
     * @param rootView       根布局
     * @param keyboardHeight 高度改变()px时当作键盘弹出，100px不会再低了吧
     * @param listener       回调
     */
    public MyOnGlobalLayoutListener(View rootView, int keyboardHeight, KeyboardVisibilityListener listener) {
        this.rootView = rootView;
        mListener = listener;
        this.keyboardHeight = keyboardHeight;
    }
    
    @Override
    public void onGlobalLayout() {
        Rect rect = new Rect();
        
        rootView.getGlobalVisibleRect(rect);
    
        if (rootBottom == 0) {
            //初始化
            rootBottom = rect.bottom;
    
            return;
        }
    
        if (rootBottom - rect.bottom > 0) {
            int changedValue = rootBottom - rect.bottom;
            mListener.onChange(rootBottom - changedValue);
        }
    }
    
    
    public interface KeyboardVisibilityListener {
        void onChange(int positionY);
    }
    
    
}
