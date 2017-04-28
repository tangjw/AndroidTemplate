package com.tjw.template.net;

import com.tjw.selectimage.uitl.L;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;


/**
 * ^-^
 * Created by tang-jw on 2017/4/28.
 */

public class ResponseObserver<T> implements Observer<T> {
    public static final String TAG = "ResponseObserver";
    
    @Override
    public void onSubscribe(Disposable d) {
        
    }
    
    @Override
    public void onNext(T t) {
        if (t == null) {
            //json转换异常(服务器报错误日志了)
            onError(new ApiException(9001, "response为null"));
        }
    }
    
    @Override
    public void onError(Throwable e) {
        
        L.e(TAG, e.getMessage());
        
        if (e instanceof HttpException) {
            
        } else if (e instanceof ApiException) {
            
        }
    }
    
    @Override
    public void onComplete() {
        
    }
}
