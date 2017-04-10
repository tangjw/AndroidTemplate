package com.tjw.template.swipeback;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.tjw.template.R;

/**
 * ^-^
 * Created by tang-jw on 2017/4/10.
 */

public class CommentDialog extends DialogFragment {
    
    private EditText mEditText;
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_Comment);
    }
    
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.dialog_comment_web, container, false);
        
        mEditText = (EditText) inflate.findViewById(R.id.et_comment_edit);
        
        return inflate;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.BOTTOM; // 紧贴底部
//        lp.alpha = 1;
//        lp.dimAmount = 0.5f;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度持平
            window.setAttributes(lp);
        }
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        
        
        return super.onCreateDialog(savedInstanceState);
    }
    
    public static CommentDialog newInstance() {
        
        Bundle args = new Bundle();
        
        CommentDialog fragment = new CommentDialog();
        fragment.setArguments(args);
        return fragment;
    }
}
