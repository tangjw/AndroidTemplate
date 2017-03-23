package com.tjw.template.swipeback.main;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.tjw.template.R;
import com.tjw.template.bottomnav.BaseFragment;
import com.tjw.template.swipeback.main.adapter.MyAdapter;
import com.tjw.template.widget.banner.SimpleHeaderView;
import com.tjw.template.widget.recycler.XRecyclerView;

import java.util.ArrayList;

/**
 * ^-^
 * Created by tang-jw on 2017/3/9.
 */

public class MainFragment2 extends BaseFragment {
    
    private XRecyclerView mXRecyclerView;
    private MyAdapter mAdapter;
    private ArrayList<String> listData;
    private int refreshTime = 0;
    private int times = 0;
    
    public MainFragment2() {
        // Required empty public constructor
    }
    
    public static MainFragment2 newInstance() {
        MainFragment2 fragment = new MainFragment2();
        Bundle args = new Bundle();
//        args.putInt(ARG_PARAM1, position);
        fragment.setArguments(args);
        return fragment;
    }
    
    
    @Override
    protected int getFraLayout() {
        return R.layout.fragment_main_2;
    }
    
    @Override
    protected void initView() {
    
        mXRecyclerView = (XRecyclerView) mRootView.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mXRecyclerView.setLayoutManager(layoutManager);

//        mXRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
//        mXRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
//        mXRecyclerView.setArrowImageView(R.drawable.arrow_down);
    
        mXRecyclerView.setPullRefreshEnabled(true);
        mXRecyclerView.setLoadingMoreEnabled(true);
    
        mXRecyclerView.addHeaderView(new SimpleHeaderView(mActivity));
    
        View emptyView = mRootView.findViewById(R.id.emptyview);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            
               /* for (int i = 0; i < 15; i++) {
                    listData.add("item" + (1 + listData.size()));
                }
                mAdapter.notifyDataSetChanged();*/
            }
        });
        mXRecyclerView.setEmptyView(emptyView);

//        mXRecyclerView
        
        mXRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                refreshTime ++;
                times = 0;
                new Handler().postDelayed(new Runnable(){
                    public void run() {
                        listData.clear();
                        for(int i = 0; i < 15 ;i++){
                            listData.add("item" + i + "after " + refreshTime + " times of refresh");
                        }
                        mAdapter.notifyDataSetChanged();
                        mXRecyclerView.refreshComplete();
                    }
            
                }, 1000);            //refresh data here
            }
    
            @Override
            public void onLoadMore() {
                if(times < 2){
                    new Handler().postDelayed(new Runnable(){
                        public void run() {
                            for(int i = 0; i < 15 ;i++){
                                listData.add("item" + (1 + listData.size() ) );
                            }
                            mXRecyclerView.loadMoreComplete();
                            mAdapter.notifyDataSetChanged();
                        }
                    }, 1000);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            for(int i = 0; i < 9 ;i++){
                                listData.add("item" + (1 + listData.size() ) );
                            }
                            mXRecyclerView.setNoMore(true);
                            mAdapter.notifyDataSetChanged();
                        }
                    }, 1000);
                }
                times ++;
            }
        });
    
        listData = new ArrayList<>();
        for(int i = 0; i < 15 ;i++){
            listData.add("item" + (1 + listData.size() ) );
        }
        mAdapter = new MyAdapter(listData);
        mXRecyclerView.setAdapter(mAdapter);
        mXRecyclerView.refresh();
    }
}
