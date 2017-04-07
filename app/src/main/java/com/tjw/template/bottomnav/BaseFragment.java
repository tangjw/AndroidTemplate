package com.tjw.template.bottomnav;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;

/**
 * ^-^
 * Created by tang-jw on 3/2.
 */

public abstract class BaseFragment extends Fragment {
    
    protected AppCompatActivity mActivity;
    protected boolean isViewInitiated;
    protected boolean isPrepare;
    protected boolean isDataInitiated;
    protected boolean isVisibleToUser;
    protected View mRootView;
    
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (AppCompatActivity) context;
    }
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        
        mRootView = inflater.inflate(getFraLayout(), container, false);
        
        if (getArguments() != null) {
            initData(getArguments());
        } else {
            Logger.e("getArguments == null, initData() not work");
        }
        
        initView();
        
        initListener();
        
        loadData();
        
        isPrepare = true;
        
        return mRootView;
    }
    
    protected void loadData() {
    }
    
    
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isViewInitiated = true;
        prepareLoadData();
    }
    
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isViewInitiated = true;
        prepareLoadData();
    }
    
    public boolean prepareLoadData() {
        return prepareLoadData(true);
    }
    
    public boolean prepareLoadData(boolean forceUpdate) {
        if (isVisibleToUser && isViewInitiated && (!isDataInitiated || forceUpdate)) {
            onVisible();
            isDataInitiated = true;
            return true;
        } else {
            if (isDataInitiated) {
                onInvisible();
            }
        }
        return false;
    }
    
    protected void initListener() {
        
    }
    
    protected abstract int getFraLayout();
    
    /**
     * 初始化数据
     *
     * @param arguments 接收到的从其他地方传递过来的参数
     */
    protected void initData(Bundle arguments) {
        
    }
    
    /**
     * 初始化View
     */
    protected void initView() {
        
    }
    
    
    /**
     * 懒加载，仅当用户可见切view初始化结束后才会执行
     */
    protected void onVisible() {
        
    }
    
    /**
     * 懒加载，仅当用户可见切view初始化结束后才会执行
     */
    protected void onInvisible() {
        
    }
}
