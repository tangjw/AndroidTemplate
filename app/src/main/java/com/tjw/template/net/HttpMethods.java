package com.tjw.template.net;

import com.tjw.template.bean.Repo;
import com.tjw.template.bean.RepoSearchResult;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * ^-^
 * Created by tang-jw on 2017/4/13.
 */

public class HttpMethods {
    
    private static final String HOST_URL = "https://api.github.com/";
    
    private Retrofit mRetrofit;
    
    private GitHubApi mGitHubApi;
    
    private static volatile HttpMethods sInstance;
    
    private HttpMethods() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient okHttpClient = builder.addInterceptor(logInterceptor)
                .connectTimeout(15L, TimeUnit.SECONDS)
                .readTimeout(15L, TimeUnit.SECONDS)
                .build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(HOST_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
        
        
        mGitHubApi = mRetrofit.create(GitHubApi.class);
        
    }
    
    
    public static HttpMethods getInstance() {
        if (sInstance == null) {
            synchronized (HttpMethods.class) {
                if (sInstance == null) {
                    sInstance = new HttpMethods();
                }
            }
        }
        
        return sInstance;
    }
    
    
    public void getRepos(String username, Consumer<List<Repo>> consumer) {
        mGitHubApi.getRepos(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }
    
    public void searchRepos(String keyword, Consumer<RepoSearchResult> consumer) {
        mGitHubApi.searchRepos(keyword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }
    
}
