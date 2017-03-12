package com.tjw.template.swipeback.main;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.orhanobut.logger.Logger;
import com.tjw.template.R;
import com.tjw.template.bean.Repo;
import com.tjw.template.bottomnav.BaseFragment;
import com.tjw.template.net.GitHubApi;
import com.tjw.template.swipeback.main.adapter.Main1RecyclerViewAdapter;
import com.tjw.template.widget.banner.SimpleHeaderView;
import com.tjw.template.widget.recycler.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ^-^
 * Created by tang-jw on 2017/3/9.
 */

public class MainFragment1 extends BaseFragment {
    
    private RecyclerView mRecyclerView;
    private Main1RecyclerViewAdapter mAdapter;
    
    public MainFragment1() {
        // Required empty public constructor
    }
    
    public static MainFragment1 newInstance() {
        MainFragment1 fragment = new MainFragment1();
        Bundle args = new Bundle();
//        args.putInt(ARG_PARAM1, position);
        fragment.setArguments(args);
        return fragment;
    }
    
    
    @Override
    protected int getFraLayout() {
        return R.layout.fragment_main_1;
    }
    
    @Override
    protected void initData(Bundle arguments) {
        
    }
    
    @Override
    protected void initView() {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.rv_main_1);
        initRecyclerView();
        mAdapter = new Main1RecyclerViewAdapter(mActivity, new ArrayList<Repo>());
        SimpleHeaderView simpleHeaderView = new SimpleHeaderView(mActivity);
        mAdapter.setHeaderView(simpleHeaderView);
        mRecyclerView.setAdapter(mAdapter);
        
    }
    
    
    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
        
    }
    
    @Override
    protected void loadData() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
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
        api.getRepos("tangjw")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Repo>>() {
                    
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    
                    @Override
                    public void onNext(List<Repo> repos) {
                        Logger.d(repos.get(0).getArchive_url());
    
                        mAdapter.setDataList(repos);
                        
                    }
                    
                    @Override
                    public void onError(Throwable e) {
                    }
                    
                    @Override
                    public void onComplete() {
                    }
                });
    }
}
