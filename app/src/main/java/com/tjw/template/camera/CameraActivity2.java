package com.tjw.template.camera;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.orhanobut.logger.Logger;
import com.tjw.selectimage.app.SelectImage;
import com.tjw.selectimage.app.SelectImageImpl;
import com.tjw.selectimage.model.CropOptions;
import com.tjw.selectimage.model.InvokeParam;
import com.tjw.selectimage.model.TContextWrap;
import com.tjw.selectimage.model.TImage;
import com.tjw.selectimage.model.TResult;
import com.tjw.selectimage.permission.InvokeListener;
import com.tjw.selectimage.permission.PermissionManager;
import com.tjw.selectimage.permission.TakePhotoInvocationHandler;
import com.tjw.template.R;
import com.tjw.template.swipeback.BaseSwipeBackActivity;
import com.tjw.template.util.ViewHolder;
import com.tjw.template.widget.RatioImageView;

import java.util.ArrayList;
import java.util.UUID;

/**
 * ^-^
 * Created by tang-jw on 2017/3/16.
 */

public class CameraActivity2 extends BaseSwipeBackActivity implements SelectImage.SelectResultListener, InvokeListener {
    
    private ImageView mImageView;
    
    private InvokeParam mInvokeParam;
    
    private SelectImage mSelectImage;
    
    private GridView mGridView;
    
    private MyAdapter2 mAdapter;
    
    private ArrayList<TImage> mSelectedImage2s;
    
    @Override
    protected void beforeSuperCreate(@Nullable Bundle savedInstanceState) {
        super.beforeSuperCreate(savedInstanceState);
        getSelectImage().onCreate(savedInstanceState);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getSelectImage().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
    
    @Override
    protected void initView() {
        setContentView(R.layout.activity_camera);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mGridView = (GridView) findViewById(R.id.gv_img);
        mSelectedImage2s = new ArrayList<>();
    
        mAdapter = new MyAdapter2();
    
        mGridView.setAdapter(mAdapter);
    
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getSelectImage().onActivityResult(requestCode, resultCode, data);
    }
    
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, mInvokeParam, this);
    }
    
    @Override
    public void selectSuccess(TResult result) {
        
        Glide.with(this)
                .load(result.getImages().get(0).getOriginalPath())
                .signature(new StringSignature(UUID.randomUUID().toString()))
                .error(R.drawable.ic_mine)
                .into(mImageView);
    
        mSelectedImage2s.addAll(result.getImages());
    
    
        mAdapter.setSelectedImgList(mSelectedImage2s);
        
    }
    
    @Override
    public void selectFail(String msg) {
        Logger.i("selectFail");
    }
    
    @Override
    public void selectCancel() {
        Logger.i("selectCancel");
    }
    
    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.mInvokeParam = invokeParam;
        }
        return type;
    }
    
    /**
     * 获取TakePhoto实例
     */
    public SelectImage getSelectImage() {
        if (mSelectImage == null) {
            mSelectImage = (SelectImage) TakePhotoInvocationHandler.of(this).bind(new SelectImageImpl(this, this));
        }
        return mSelectImage;
    }
    
    /**
     * 剪切图片的设置参数
     */
    private CropOptions getCropOptions() {
        CropOptions.Builder builder = new CropOptions.Builder();
        builder.setOutputX(640).setOutputY(640);
        builder.setAspectX(1).setAspectY(1);
        builder.setWithSystemCrop(false);
        return builder.create();
    }
    
    public void setAvatar(View view) {
        
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.dialog_select_image);
        
        bottomSheetDialog.show();
        
        bottomSheetDialog.findViewById(R.id.tv_select_image_cancel)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.cancel();
                    }
                });
        
        bottomSheetDialog.findViewById(R.id.tv_select_image_album)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSelectImage.fromAlbum(getCropOptions());
                        bottomSheetDialog.cancel();
                    }
                });
        
        bottomSheetDialog.findViewById(R.id.tv_select_image_camera)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSelectImage.fromCamera(getCropOptions());
                        bottomSheetDialog.cancel();
                    }
                });
    }
    
    private void pickImage() {
        mSelectImage.fromAlbum(9 - mSelectedImage2s.size());
    }
    
    private class MyAdapter2 extends BaseAdapter {
        
        private ArrayList<TImage> mSelectedImgList;
        
        public MyAdapter2() {
            mSelectedImgList = new ArrayList<>();
        }
    
        public ArrayList<TImage> getSelectedImgList() {
            return mSelectedImgList;
        }
        
        public void setSelectedImgList(ArrayList<TImage> selectedImgList) {
            mSelectedImgList = selectedImgList;
            notifyDataSetChanged();
        }
        
        @Override
        public int getCount() {
            return mSelectedImgList.size() == 9 ? 9 : mSelectedImgList.size() + 1;
        }
        
        @Override
        public String getItem(int position) {
            return null;
        }
        
        @Override
        public long getItemId(int position) {
            return position;
        }
        
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(CameraActivity2.this, R.layout.item_img, null);
            }
            RatioImageView img = ViewHolder.get(convertView, R.id.siv_img);
            ImageView del = ViewHolder.get(convertView, R.id.iv_close);
            
            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectedImgList.remove(position);
                    notifyDataSetChanged();
                }
            });
            
            if (mSelectedImgList.size() > 0 && position < mSelectedImgList.size()) {
                Glide.with(CameraActivity2.this)
                        .load(mSelectedImgList.get(position).getOriginalPath())
                        .into(img);
                del.setVisibility(View.VISIBLE);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        previewImage(position);
                    }
                });
            } else if (position == mSelectedImgList.size()) {
                Glide.with(CameraActivity2.this)
                        .load(R.drawable.compose_pic_add_highlighted)
                        .into(img);
                del.setVisibility(View.GONE);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pickImage();
                    }
                });
            }
            return convertView;
        }
    }
    
    
}
