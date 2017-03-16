package com.tjw.template.swipeback.main.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tjw.template.R;
import com.tjw.template.bean.Repo;
import com.tjw.template.camera.CameraActivity;
import com.tjw.template.widget.banner.HeaderView;

import java.util.List;

/**
 * ^-^
 * Created by tang-jw on 2017/3/10.
 */

public class Main1RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;
    
    private List<Repo> mDataList;
    
    private Context mActivity;
    private HeaderView mHeaderView;
    
    public HeaderView getHeaderView() {
        return mHeaderView;
    }
    
    public void setHeaderView(HeaderView headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }
    
    public Main1RecyclerViewAdapter(Context activity, List<Repo> dataList) {
        mDataList = dataList;
        mActivity = activity;
    }
    
    public void setDataList(List<Repo> dataList) {
        mDataList = dataList;
        notifyDataSetChanged();
    }
    
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) {
            mHeaderView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new HeaderVH(mHeaderView);
        }
        View inflate = LayoutInflater.from(mActivity).inflate(R.layout.item_main_1, parent, false);
    
        inflate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.startActivity(new Intent(mActivity, CameraActivity.class));
            
            }
        });
    
        return new ItemVH(inflate);
    }
    
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    
        if (getItemViewType(position) == TYPE_HEADER) {
            return;
        }
        Repo repo = mDataList.get(position - 1);
        ItemVH viewHolder = (ItemVH) holder;
        viewHolder.tvName.setText(repo.getName());
        Glide.with(mActivity)
                .load(repo.getOwner().getAvatar_url())
                .into(viewHolder.ivAvatar);
        
        
    }
    
    @Override
    public int getItemCount() {
        return mDataList.size() + 1;
    }
    
    @Override
    public int getItemViewType(int position) {
        if (mHeaderView != null && position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_NORMAL;
    }
    
    private class ItemVH extends RecyclerView.ViewHolder {
        
        TextView tvName;
        ImageView ivAvatar;
        
        ItemVH(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
        }
    }
    
    private class HeaderVH extends RecyclerView.ViewHolder {
        
        HeaderVH(View itemView) {
            super(itemView);
        }
    }
}
