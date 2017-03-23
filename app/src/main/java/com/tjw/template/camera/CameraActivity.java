package com.tjw.template.camera;

import android.content.Intent;
import android.os.Bundle;
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
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
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
        // new File 正确姿势
        /*File path = new File(Environment.getExternalStorageDirectory() + "/img_tmp/");
        if (!path.exists()) {
            path.mkdirs();
        }
        File file = new File(path, System.currentTimeMillis() + ".jpg");
    
        // 某国产手机不支持
//        File file2 = new File(Environment.getExternalStorageDirectory() + "/temp/" + ".jpg");
        
        
        Uri imageUri = Uri.fromFile(file);*/
        setCompress();
        setPhotoOption();
//        mTakePhoto.onPickFromCapture(imageUri);
        mTakePhoto.onPickFromCaptureWithCrop(getCropOptions());
        
    }
    
    private void setCompress() {
        CompressConfig compressConfig = new CompressConfig.Builder().create();
        compressConfig.enableReserveRaw(true);
        mTakePhoto.onEnableCompress(compressConfig, false);
    }
    
    private void setPhotoOption() {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        //设置纠正照片方向
        builder.setCorrectImage(true);
        mTakePhoto.setTakePhotoOptions(builder.create());
    }
    
    public void openAlbum(View view) {
//        mTakePhoto.onPickMultiple(9);
        mTakePhoto.onPickMultipleWithCrop(1, getCropOptions());
    }
    
    @Override
    public void takeSuccess(TResult result) {
    
        Logger.i(result.getImages().get(0).getOriginalPath());
        
        Glide.with(this)
                .load(result.getImages().get(0).getOriginalPath())
                .into(mImageView);

//        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//                Uri.parse("file://" + result.getImages().get(0).getOriginalPath())));
    
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
        builder.setOutputX(480).setOutputY(480);
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
