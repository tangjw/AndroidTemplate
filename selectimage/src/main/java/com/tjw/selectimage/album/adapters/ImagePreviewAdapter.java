package com.tjw.selectimage.album.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.tjw.selectimage.album.models.Image;
import com.tjw.selectimage.photoview.OnPhotoTapListener;
import com.tjw.selectimage.photoview.PhotoView;

import java.util.ArrayList;

/**
 * ^-^
 * Created by tang-jw on 3/30.
 */

public class ImagePreviewAdapter extends PagerAdapter {
    
    private ArrayList<Image> mDataList;
    
    public ImagePreviewAdapter(ArrayList<Image> dataList) {
        mDataList = dataList;
    }
    
    @Override
    public int getCount() {
        return mDataList.size();
    }
    
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
    
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Context context = container.getContext();
        PhotoView image = new PhotoView(context);
        image.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        
        if (mDataList.get(position).path.endsWith(".gif") || mDataList.get(position).path.contains(".gif")) {
            
            Glide.with(context)
                    .load(mDataList.get(position).path)
                    .fitCenter()
                    .into(new GlideDrawableImageViewTarget(image, 0));
        } else {
            Glide.with(context)
                    .load(mDataList.get(position).path)
                    .fitCenter()
                    .into(image);
        }
        
        image.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
//                tabFullscreen();
            }
        });
        
        container.addView(image);
        return image;
    }
    
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
    
}
