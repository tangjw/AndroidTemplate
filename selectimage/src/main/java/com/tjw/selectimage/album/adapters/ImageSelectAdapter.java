package com.tjw.selectimage.album.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.tjw.selectimage.R;
import com.tjw.selectimage.album.models.Image;

import java.util.ArrayList;

public class ImageSelectAdapter extends CustomGenericAdapter<Image> {
    
    public ImageSelectAdapter(Context context, ArrayList<Image> images) {
        super(context, images);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.grid_view_item_image_select, parent, false);
            
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view_image_select);
            viewHolder.view = convertView.findViewById(R.id.view_alpha);
            
            convertView.setTag(viewHolder);
            
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        
        viewHolder.imageView.getLayoutParams().width = size;
        viewHolder.imageView.getLayoutParams().height = size;
        
        viewHolder.view.getLayoutParams().width = size;
        viewHolder.view.getLayoutParams().height = size;
        
        if (arrayList.get(position).isSelected) {
            viewHolder.view.setAlpha(0.5f);
            ((FrameLayout) convertView).setForeground(context.getResources().getDrawable(R.drawable.ic_done_white));
            
        } else {
            viewHolder.view.setAlpha(0.0f);
            ((FrameLayout) convertView).setForeground(null);
        }
        
        if (arrayList.get(position).path.endsWith(".gif") || arrayList.get(position).path.contains(".gif")) {
            Glide.with(context)
                    .load(arrayList.get(position).path)
                    .placeholder(R.drawable.image_placeholder)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(new GlideDrawableImageViewTarget(viewHolder.imageView, 0));
        } else {
            Glide.with(context)
                    .load(arrayList.get(position).path)
                    .placeholder(R.drawable.image_placeholder)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.imageView);
        }
        
        return convertView;
    }
    
    private static class ViewHolder {
        ImageView imageView;
        private View view;
    }
}
