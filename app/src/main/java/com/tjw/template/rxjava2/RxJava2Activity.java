package com.tjw.template.rxjava2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.tjw.template.R;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * RxJava2 从头学起
 * <p>
 * Observable(可被观察的)
 * <p>
 * subscribe(订阅操作)(纽带)
 * <p>
 * Observer(观察者)
 * <p>
 * Created by Android on 2017/2/27.
 */

public class RxJava2Activity extends AppCompatActivity {
    
    private Button mButton2;
    private long mTimeMillis0;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_rxjava2);
        
        mButton2 = (Button) findViewById(R.id.button2);
    }
    
    public void click1(View view) {
        
        // 创建一个事件流上游 Observable
        Observable<Integer> observable =
                Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> emitter/*emitter 发出者,发射极*/) throws Exception {
                        //一些事件
                        emitter.onNext(1);
                        emitter.onNext(2);
                        emitter.onNext(3);
                        emitter.onComplete(); //Observer 会走 onComplete()方法
                    }
                });
        
        // 创建一个下游 Observer
        Observer<Integer> observer =
                new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e("observer", "onSubscribe() => 开始订阅调用");
                    }
                    
                    @Override
                    public void onNext(Integer integer) {
                        Log.e("observer", "onNext() => " + integer);
                    }
                    
                    @Override
                    public void onError(Throwable e) {
                        Log.e("observer", "onError() => 出错了");
                    }
                    
                    @Override
                    public void onComplete() {
                        Log.e("observer", "onComplete() => 事件结束");
                    }
                };
        
        
        //建立连接 纽带 
        observable.subscribe(observer);
        
        System.out.println("-------------------------分割线---------------------------");
        
        //(可观察订阅观察者,类似:报社拉客买报纸的客户,为了 链式操作 事件)
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter/*emitter 发出者,发射极*/) throws Exception {
                //一些事件
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete(); //Observer 会走 onComplete()方法
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e("observer", "onSubscribe() => 开始订阅调用");
            }
            
            @Override
            public void onNext(Integer integer) {
                Log.e("observer", "onNext() => " + integer);
            }
            
            @Override
            public void onError(Throwable e) {
                Log.e("observer", "onError() => 出错了");
            }
            
            @Override
            public void onComplete() {
                Log.e("observer", "onComplete() => 事件结束");
            }
        });
        
        System.out.println("-------------------------分割线---------------------------");
        
        
        
        /*
        相比RxJava
        两个陌生的玩意：ObservableEmitter和Disposable.
        ObservableEmitter： Emitter是发射器的意思，那就很好猜了，这个就是用来发出事件的，它可以发出三种类型
        的事件，通过调用emitter的onNext(T value)、onComplete()和onError(Throwable error)就可以分别发出
        next事件、complete事件和error事件。

        但是，请注意，并不意味着你可以随意乱七八糟发射事件，需要满足一定的规则：
         * 上游可以发送无限个onNext, 下游也可以接收无限个onNext.
         * 当上游发送了一个onComplete后, 上游onComplete之后的事件将会继续发送, 而下游收到onComplete事件之后将不再继续接收事件.
         * 当上游发送了一个onError后, 上游onError之后的事件将继续发送, 而下游收到onError事件之后将不再继续接收事件.
         * 上游可以不发送onComplete或onError.
         * 最为关键的是onComplete和onError必须唯一并且互斥, 即不能发多个onComplete, 也不能发多个onError, 
         * 也不能先发一个onComplete, 然后再发一个onError, 反之亦然
        注: 关于onComplete和onError唯一并且互斥这一点, 是需要自行在代码中进行控制, 如果你的代码逻辑中违背了
        这个规则, 并不一定会导致程序崩溃. 比如发送多个onComplete是可以正常运行的, 依然是收到第一个onComplete
        就不再接收了, 但若是发送多个onError, 则收到第二个onError事件会导致程序会崩溃.
        
        
        Disposable, 这个单词的字面意思是一次性用品,用完即可丢弃的. 那么在RxJava中怎么去理解它呢, 
        对应于上面的水管的例子, 我们可以把它理解成两根管道之间的一个机关, 当调用它的dispose()方法时, 
        它就会将两根管道切断, 从而导致下游收不到事件.

        注意: 调用dispose()并不会导致上游不再继续发送事件, 上游会继续发送剩余的事件.
        
        */
        
        //(可观察订阅观察者,类似:报社拉客买报纸的客户,为了 链式操作 事件)
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.e("observable", "emitter1");
                emitter.onNext(1);
                Log.e("observable", "emitter2");
                emitter.onNext(2);
                Log.e("observable", "emitter3");
                emitter.onNext(3);
                Log.e("observable", "onComplete()");
                emitter.onComplete();
                Log.e("observable", "emitter4");
                emitter.onNext(4);
            }
        }).subscribe(new Observer<Integer>() {
            
            
            private Disposable mDisposable;
            private int mEventCount;
            
            @Override
            public void onSubscribe(Disposable d) {
                Log.e("observer", "onSubscribe(Disposable d) => 开始订阅调用");
                mDisposable = d;
            }
            
            @Override
            public void onNext(Integer integer) {
                Log.e("observer", "onNext() => " + integer);
                mEventCount++;
                if (mEventCount == 2) {
                    mDisposable.dispose();
                    Log.d("observer", "onNext() => isDisposed : " + mDisposable.isDisposed());
                }
            }
            
            @Override
            public void onError(Throwable e) {
                Log.e("observer", "onError() => 出错了");
            }
            
            @Override
            public void onComplete() {
                Log.e("observer", "onComplete() => 事件结束");
            }
        });
        
        System.out.println("-------------------------分割线---------------------------");
        
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.e("observable", "emitter1");
                emitter.onNext(1);
                Log.e("observable", "emitter2");
                emitter.onNext(2);
                Log.e("observable", "emitter3");
                emitter.onNext(3);
                Log.e("observable", "onComplete()");
                emitter.onComplete();
                Log.e("observable", "emitter4");
                emitter.onNext(4);
            }
        }).subscribe(new Consumer<Integer>()/*consumer 消费者,用户,只关心onNext*/ {
            @Override
            public void accept/*接受,认可*/(@NonNull Integer integer) throws Exception {
                Log.e("observer", "onNext() => " + integer);
            }
        });
    }
    
    
    public void click2(View view) {
       /* Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                Log.e("observable", "subscribe() => Thread name => " + Thread.currentThread().getName());
                e.onNext(1);
                SystemClock.sleep(1000L);
                e.onNext(2);
                SystemClock.sleep(1000L);
                e.onNext(3);
            }
        })
                //多次指定订阅的线程只有第一次指定的有效
                .subscribeOn(Schedulers.newThread())
                //多次指定观察者 观察的线程每指定一次就切换一次
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Log.e("consumer", "accept() => Thread name => " + Thread.currentThread().getName());
                        Log.e("consumer", "onNext => " + integer);
                        mButton2.setText("RxJava2线程切换(" + integer + ")");
                    }
                });*/
        
        System.out.println("-------------------------分割线---------------------------");
    
        /*
        在RxJava中, 已经内置了很多线程选项供我们选择, 例如有

         * Schedulers.io() 代表io操作的线程, 通常用于网络,读写文件等io密集型的操作
         * Schedulers.computation() 代表CPU计算密集型的操作, 例如需要大量计算的操作
         * Schedulers.newThread() 代表一个常规的新线程
         * AndroidSchedulers.mainThread() 代表Android的主线程
        */
        /*
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                Log.e("observable", "subscribe() => Thread name => " + Thread.currentThread().getName());
                e.onNext(1);
                SystemClock.sleep(1000L);
                e.onNext(2);
                SystemClock.sleep(1000L);
                e.onNext(3);
            }
        })
                //多次指定订阅的线程只有第一次指定的有效
                .subscribeOn(Schedulers.newThread())
                //多次指定观察者 观察的线程每指定一次就切换一次
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Log.e("consumer1", "accept() => Thread name => " + Thread.currentThread().getName());
                        Log.e("consumer1", "onNext => " + integer);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Log.e("consumer2", "accept() => Thread name => " + Thread.currentThread().getName());
                        Log.e("consumer2", "onNext => " + integer);
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Log.e("consumer3", "accept() => Thread name => " + Thread.currentThread().getName());
                        Log.e("consumer3", "onNext => " + integer);
                        mButton2.setText("RxJava2线程切换(" + integer + ")");
                    }
                }); */
        
       /* OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(10L, TimeUnit.SECONDS);
        builder.connectTimeout(15L, TimeUnit.SECONDS);
    
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        builder.addInterceptor(logInterceptor);
        
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        
        GitHubApi api = retrofit.create(GitHubApi.class);
        mTimeMillis0 = System.currentTimeMillis();
        api.getRepos("tangjw")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Repo>>() {
    
                    private long mTimeMillis1;
                    private long mTimeMillis2;
                    private long mTimeMillis3;
                    private long mTimeMillis4;
    
                    @Override
                    public void onSubscribe(Disposable d) {
                        mTimeMillis1 = System.currentTimeMillis();
                        Log.i("observer", "onSubscribe() time => " + (mTimeMillis1-mTimeMillis0));
                    }
                    
                    @Override
                    public void onNext(List<Repo> repos) {
                        mTimeMillis2 = System.currentTimeMillis();
                        Log.i("observer", "onNext() time => " + (mTimeMillis2-mTimeMillis1));
                    }
                    
                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(RxJava2Activity.this, "异常: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    
                    @Override
                    public void onComplete() {
                        mTimeMillis4 = System.currentTimeMillis();
                        Log.i("observer", "onComplete() time => " + (mTimeMillis4-mTimeMillis1));
                    }
                });*/
        
        
    }
}
