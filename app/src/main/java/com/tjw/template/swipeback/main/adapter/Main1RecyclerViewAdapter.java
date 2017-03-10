package com.tjw.template.swipeback.main.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tjw.template.R;
import com.tjw.template.bean.Repo;

import java.util.List;

/**
 * ^-^
 * Created by tang-jw on 2017/3/10.
 */

public class Main1RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    
    private List<Repo> mDataList;
    
    private Context mActivity;
    
    public Main1RecyclerViewAdapter( Context activity,List<Repo> dataList) {
        mDataList = dataList;
        mActivity = activity;
    }
    
    public void setDataList(List<Repo> dataList) {
        mDataList = dataList;
        notifyDataSetChanged();
    }
    
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    
        View inflate =  LayoutInflater.from(mActivity).inflate(R.layout.item_main_1, parent,false);
        
        return new MyViewHolder(inflate);
    }
    
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Repo repo = mDataList.get(position);
        MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.tvName.setText(repo.getName());
        Glide.with(mActivity)
                .load(repo.getOwner().getAvatar_url())
                .into(viewHolder.ivAvatar);
    }
    
    @Override
    public int getItemCount() {
        return mDataList.size();
    }
    
    
    private class MyViewHolder extends RecyclerView.ViewHolder {
        
        TextView tvName;
        ImageView ivAvatar;
        
        MyViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
        }
    }
}
