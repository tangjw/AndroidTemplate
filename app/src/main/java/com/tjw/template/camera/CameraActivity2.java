package com.tjw.template.camera;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jph.takephoto.app.SelectImage;
import com.jph.takephoto.app.SelectImageImpl;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.orhanobut.logger.Logger;
import com.tjw.template.R;
import com.tjw.template.swipeback.BaseActivity;

/**
 * ^-^
 * Created by tang-jw on 2017/3/16.
 */

public class CameraActivity2 extends BaseActivity implements SelectImage.TakeResultListener, InvokeListener {
    
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
    public void takeSuccess(TResult result) {
        
        Glide.with(this)
                .load(result.getImages().get(0).getOriginalPath())
                .into(mImageView);
        
        for (TImage img : result.getImages()) {
            System.out.println(img.getOriginalPath());
        }
        
    }
    
    @Override
    public void takeFail(TResult result, String msg) {
        Logger.i("takeFail");
    }
    
    @Override
    public void takeCancel() {
        Logger.i("takeCancel");
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
        builder.setWithOwnCrop(false);
        return builder.create();
    }
    
    public void open1(View view) {
        mTakePhoto.onPickFromCapture();
    }
    
    public void open2(View view) {
        mTakePhoto.onPickMultiple(9);
    }
}