
package com.tjw.template.swipeback.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tjw.template.R;
import com.tjw.template.avatar.AvatarActivity;
import com.tjw.template.bottomnav.BaseFragment;
import com.tjw.template.camera.CameraActivity2;
import com.tjw.template.rxjava2.RxTextViewActivity;
import com.tjw.template.swipeback.LoginActivity;

/**
 * ^-^
 * Created by tang-jw on 2017/3/9.
 */

public class MainFragment3 extends BaseFragment {
    
    public MainFragment3() {
        // Required empty public constructor
    }
    
    public static MainFragment3 newInstance() {
        MainFragment3 fragment = new MainFragment3();
        Bundle args = new Bundle();
//        args.putInt(ARG_PARAM1, position);
        fragment.setArguments(args);
        return fragment;
    }
    
    
    @Override
    protected int getFraLayout() {
        return R.layout.fragment_main_3;
    }
    
    @Override
    protected void initView() {
        mRootView.findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, CameraActivity2.class));
            }
        });
        
        mRootView.findViewById(R.id.button6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, RxTextViewActivity.class));
            }
        });
        
        mRootView.findViewById(R.id.button7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, LoginActivity.class));
            }
        });
        mRootView.findViewById(R.id.button8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, AvatarActivity.class));
            }
        });
    }
}
