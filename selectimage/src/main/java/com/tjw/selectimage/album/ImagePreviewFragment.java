package com.tjw.selectimage.album;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.tjw.selectimage.R;
import com.tjw.selectimage.album.models.Image;
import com.tjw.selectimage.album.widget.ZoomOutPageTransformer;
import com.tjw.selectimage.photoview.OnPhotoTapListener;
import com.tjw.selectimage.photoview.PhotoView;
import com.tjw.selectimage.uitl.L;

import java.util.ArrayList;

public class ImagePreviewFragment extends DialogFragment implements ViewPager.OnPageChangeListener {
    
    private ViewPager mViewPager;
    private ArrayList<Image> mImageArrayList;
    private Toolbar mToolbar;
    private CheckBox mCbSelect;
    private View mPaddingLayout;
    private Window mDialogWindow;
    
    private int currentPosition;
    
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
            L.e("onCreate ----------");
            mImageArrayList = getArguments().getParcelableArrayList("images");
        }
        
        
    }
    
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preview_image, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.vp_images);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mCbSelect = (CheckBox) view.findViewById(R.id.cb_select);
        mPaddingLayout = view.findViewById(R.id.paddinglayout);
        setToolbar();
        return view;
    }
    
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
    
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
    
        mViewPager.addOnPageChangeListener(this);
    
        final BrowseAdapter adapter = new BrowseAdapter();
        mViewPager.setAdapter(adapter);
        
        mCbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    
                unselectImage(currentPosition);
    
                L.e("mImageArrayList.size " + mImageArrayList.size());
    
                mImageArrayList.get(mViewPager.getCurrentItem()).isSelected = isChecked;
                adapter.notifyDataSetChanged();
            }
        });
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        refresh();
    }
    
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        
    }
    
    @Override
    public void onPageSelected(int position) {
        mToolbar.setTitle(position + 1 + "/" + mImageArrayList.size());
        mCbSelect.setChecked(mImageArrayList.get(position).isSelected);
        currentPosition = position;
        L.i("currentPosition => " + position);
    }
    
    @Override
    public void onPageScrollStateChanged(int state) {
        
    }
    
    /**
     * 图片适配器
     */
    private class BrowseAdapter extends PagerAdapter {
        
        @Override
        public int getCount() {
            return mImageArrayList.size();
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
    
            if (mImageArrayList.get(position).path.endsWith(".gif") || mImageArrayList.get(position).path.contains(".gif")) {
                
                Glide.with(context)
                        .load(mImageArrayList.get(position).path)
                        .into(new GlideDrawableImageViewTarget(image, 0));
            } else {
                Glide.with(context)
                        .load(mImageArrayList.get(position).path)
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
    
    @SuppressLint("PrivateResource")
    private void setToolbar() {
    
        mToolbar.setTitle("1/" + mImageArrayList.size());
        
        mToolbar.setNavigationIcon(android.support.design.R.drawable.abc_ic_ab_back_material);
        
        mToolbar.setContentInsetStartWithNavigation(0);
        
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    
    }
    
    public void tabFullscreen() {
        if (mPaddingLayout.getVisibility() == View.VISIBLE) {
            mPaddingLayout.setAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
            mPaddingLayout.setVisibility(View.GONE);
            fullScreen(true);
        } else {
            fullScreen(false);
            mPaddingLayout.setAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
            mPaddingLayout.setVisibility(View.VISIBLE);
        }
    }
    
    private void fullScreen(boolean enable) {
        
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        
        if (enable) {
            WindowManager.LayoutParams lp = mDialogWindow.getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            mDialogWindow.setAttributes(lp);
//            mDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = mDialogWindow.getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
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
    
    
    private OnFragmentInteractionListener mListener;
    
    private void unselectImage(int index) {
        if (mListener != null) {
            mListener.onChangeImageStatus(index);
        }
    }
    
    private void refresh() {
        if (mListener != null) {
            mListener.onRefreshImageList();
        }
    }
    
    public interface OnFragmentInteractionListener {
        void onChangeImageStatus(int index);
        
        void onRefreshImageList();
    }
    
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    
}