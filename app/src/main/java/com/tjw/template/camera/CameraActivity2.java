package com.tjw.template.camera;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
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
import com.tjw.template.swipeback.BaseActivity;

import java.util.UUID;

/**
 * ^-^
 * Created by tang-jw on 2017/3/16.
 */

public class CameraActivity2 extends BaseActivity implements SelectImage.SelectResultListener, InvokeListener {
    
    private ImageView mImageView;
    
    private InvokeParam mInvokeParam;
    
    private SelectImage mTakePhoto;
    
    @Override
    protected void beforeSuperCreate(@Nullable Bundle savedInstanceState) {
        super.beforeSuperCreate(savedInstanceState);
        getTakePhoto().onCreate(savedInstanceState);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
    
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_camera);
        mImageView = (ImageView) findViewById(R.id.imageView);
        
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
    }
    
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, mInvokeParam, this);
    }
    
    public void openCamera(View view) {
        mTakePhoto.fromCamera(getCropOptions());
    }
    
    public void openAlbum(View view) {
        mTakePhoto.fromAlbum(getCropOptions());
        
    }
    
    @Override
    public void selectSuccess(TResult result) {
        
        Glide.with(this)
                .load(result.getImages().get(0).getOriginalPath())
                .signature(new StringSignature(UUID.randomUUID().toString()))
                .error(R.drawable.ic_mine)
                .into(mImageView);
    
    
        for (TImage img : result.getImages()) {
            System.out.println(img.getOriginalPath());
        }
        
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
    public SelectImage getTakePhoto() {
        if (mTakePhoto == null) {
            mTakePhoto = (SelectImage) TakePhotoInvocationHandler.of(this).bind(new SelectImageImpl(this, this));
        }
        return mTakePhoto;
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
    
    
    public void open1(View view) {
        mTakePhoto.fromCamera();
    }
    
    public void open2(View view) {
        mTakePhoto.fromAlbum(9);
    }
    
}
