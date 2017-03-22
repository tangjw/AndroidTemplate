package com.tjw.template.camera;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.tjw.template.R;
import com.tjw.template.swipeback.BaseActivity;

import java.io.File;

/**
 * ^-^
 * Created by tang-jw on 2017/3/16.
 */

public class CameraActivity extends BaseActivity implements TakePhoto.TakeResultListener, InvokeListener {
    
    private ImageView mImageView;
    
    private InvokeParam mInvokeParam;
    
    private TakePhoto mTakePhoto;
    
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
        File file = new File(Environment.getExternalStorageDirectory() + "/temp/" + System.currentTimeMillis() + ".jpg");
//        if (!file.exists()) {
//            file.mkdirs();
//        }
        Uri imageUri = Uri.fromFile(file);
//        mTakePhoto.onPickFromCaptureWithCrop(imageUri, getCropOptions());
        setCompress();
        setPhotoOption();
        mTakePhoto.onPickFromCapture(imageUri);
        
    }
    
    private void setCompress() {
        CompressConfig compressConfig = new CompressConfig.Builder().create();
        compressConfig.enableReserveRaw(true);
        mTakePhoto.onEnableCompress(compressConfig, false);
    }
    
    private void setPhotoOption() {
        TakePhotoOptions.Builder builder=new TakePhotoOptions.Builder();
        //设置纠正照片方向
        builder.setCorrectImage(true);
        mTakePhoto.setTakePhotoOptions(builder.create());
    }
    
    public void openAlbum(View view) {
        setCompress();
        setPhotoOption();
        mTakePhoto.onPickMultiple(9);
//        mTakePhoto.onPickMultipleWithCrop(1, getCropOptions());
    }
    
    @Override
    public void takeSuccess(TResult result) {
        
        Glide.with(this)
                .load(result.getImages().get(0).getOriginalPath())
                .into(mImageView);
        
    }
    
    @Override
    public void takeFail(TResult result, String msg) {
        System.out.println("takeFail");
    }
    
    @Override
    public void takeCancel() {
        System.out.println("takeCancel");
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
    public TakePhoto getTakePhoto() {
        if (mTakePhoto == null) {
            mTakePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return mTakePhoto;
    }
    
    /**
     * 剪切图片的设置参数
     */
    private CropOptions getCropOptions() {
        CropOptions.Builder builder = new CropOptions.Builder();
        builder.setOutputX(600).setOutputY(600);
        builder.setAspectX(1).setAspectY(1);
        builder.setWithOwnCrop(false);
        return builder.create();
    }
    
}
