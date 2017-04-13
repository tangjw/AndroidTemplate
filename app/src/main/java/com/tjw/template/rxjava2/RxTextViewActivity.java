package com.tjw.template.rxjava2;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.tjw.selectimage.uitl.L;
import com.tjw.template.R;
import com.tjw.template.swipeback.BaseSwipeBackActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * ^-^
 * Created by tang-jw on 2017/4/13.
 */

public class RxTextViewActivity extends BaseSwipeBackActivity {
    
    private static final java.lang.String TAG = "RxTextViewActivity";
    private EditText mEtKeyword;
    private TextView mTvResult;
    
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }
    
    @Override
    protected void initView() {
        setContentView(R.layout.activity_rxtextview);
        mEtKeyword = (EditText) findViewById(R.id.et_search_keyword);
        mTvResult = (TextView) findViewById(R.id.tv_search_keyword);
        
        
        setEditText();
        
    }
    
    @Override
    protected void loadData() {
        
    }
    
    private void setEditText() {
        RxTextView.textChanges(mEtKeyword)
//                .subscribeOn(AndroidSchedulers.mainThread())
                .debounce(600L, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .filter(new Predicate<CharSequence>() {
                    @Override
                    public boolean test(@NonNull CharSequence charSequence) throws Exception {
                        boolean empty = TextUtils.isEmpty(charSequence);
                        if (empty) {
                            mTvResult.setText("什么也没搜索");
                        }
                        
                        return !empty;
                    }
                })
                /*.switchMap(new Function<CharSequence, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(@NonNull CharSequence charSequence) throws Exception {
                        return null;
                    }
                })*/
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(@NonNull CharSequence charSequence) throws Exception {
                        L.i(TAG, Thread.currentThread().getName());
                        L.i(TAG, "EditText 动态监控结果 => " + charSequence.toString().trim());
                        mTvResult.setText("EditText 动态监控结果 => " + charSequence.toString().trim());
                    }
                });
        
        
    }
}
