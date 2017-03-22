package com.jph.takephoto.album;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.jph.takephoto.R;
import com.jph.takephoto.album.models.Image;
import com.jph.takephoto.album.widget.ZoomOutPageTransformer;
import com.jph.takephoto.photoview.OnPhotoTapListener;
import com.jph.takephoto.photoview.PhotoView;

import java.util.ArrayList;

public class ImagePreviewFragment extends DialogFragment {
    
    private ViewPager mViewPager;
    private ArrayList<Image> mImages;
    private Toolbar mToolbar;
    private AppCompatActivity mAppCompatActivity;
    private Dialog mDialog;
    private View mPaddingLayout;
    private Window mDialogWindow;
    
    public static ImagePreviewFragment newInstance(@NonNull ArrayList<Image> images) {
        
        Bundle args = new Bundle();
        args.putParcelableArrayList("images", images);
        ImagePreviewFragment fragment = new ImagePreviewFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MultiImageSelectTheme);
        if (getArguments() != null) {
            mImages = getArguments().getParcelableArrayList("images");
        }
        
        
    }
    
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAppCompatActivity = (AppCompatActivity) context;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preview_image, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.vp_images);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mPaddingLayout = view.findViewById(R.id.paddinglayout);
        setToolbar();
        return view;
    }
    
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        final BrowseAdapter adapter = new BrowseAdapter();
        
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            
            @Override
            public void onPageSelected(int position) {
                mToolbar.setTitle(position + 1 + "/" + mImages.size());
            }
            
        });
        mViewPager.setAdapter(adapter);
        
        
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    
    /**
     * 图片适配器
     */
    private class BrowseAdapter extends PagerAdapter {
        
        @Override
        public int getCount() {
            return mImages.size();
        }
        
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
        
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Context context = container.getContext();
            PhotoView image = new PhotoView(context);
            image.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, -2));
    
            if (mImages.get(position).path.endsWith(".gif") || mImages.get(position).path.contains(".gif")) {
        
                Glide.with(mAppCompatActivity)
                        .load(mImages.get(position).path)
                        .into(new GlideDrawableImageViewTarget(image, 0));
            } else {
                Glide.with(mAppCompatActivity)
                        .load(mImages.get(position).path)
                        .into(image);
            }
    
            image.setOnPhotoTapListener(new OnPhotoTapListener() {
                @Override
                public void onPhotoTap(ImageView view, float x, float y) {
                    tabFullscreen();
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
    
    private void setToolbar() {
    
        mToolbar.setTitle("1/" + mImages.size());
        
        
        mToolbar.setNavigationIcon(android.support.design.R.drawable.abc_ic_ab_back_material);
        
        mToolbar.setContentInsetStartWithNavigation(0);
        
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

//        mToolbar.inflateMenu(R.menu.menu_toolbar);

//        mToolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_add));
        
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                /*if (item.getItemId() == R.id.action_select_done) {
                    if (countSelected > 0) {
                        sendIntent();
                    } else {
                        finish();
                    }
                }*/
                
                return true;
            }
        });
    }
    
    public void tabFullscreen() {
        if (mPaddingLayout.getVisibility() == View.VISIBLE) {
            mPaddingLayout.setAnimation(AnimationUtils.loadAnimation(mAppCompatActivity, android.R.anim.fade_out));
            mPaddingLayout.setVisibility(View.GONE);
            fullScreen(true);
        } else {
            fullScreen(false);
            mPaddingLayout.setAnimation(AnimationUtils.loadAnimation(mAppCompatActivity, android.R.anim.fade_in));
            mPaddingLayout.setVisibility(View.VISIBLE);
        }
    }
    
    private void fullScreen(boolean enable) {
        
        if (enable) {
            WindowManager.LayoutParams lp = mDialogWindow.getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            mDialogWindow.setAttributes(lp);
//            mDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = mDialogWindow.getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mDialogWindow.setAttributes(attr);
//            mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }
    
    @Override
    public void onStart() {
        super.onStart();
        
        if (getDialog().getWindow() != null) {
            mDialogWindow = getDialog().getWindow();
        }
        
        
        mDialogWindow.setWindowAnimations(android.R.style.Animation_Dialog);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDialogWindow.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mDialogWindow.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}