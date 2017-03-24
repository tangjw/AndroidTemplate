package com.jph.takephoto.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.MultipleCrop;
import com.jph.takephoto.model.TException;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.jph.takephoto.permission.PermissionManager;

public interface SelectImage {
    
    
    /**
     * 从相机拍照,一次只能拍一张
     */
    void fromCamera();
    
    /**
     * 从相机拍照并剪裁, 用于选择头像
     *
     * @param options 剪裁参数,null使用默认参数
     */
    void fromCamera(@Nullable CropOptions options);
    
    /**
     * 从相册选择图片
     *
     * @param maxCount 最多张数
     */
    void fromAlbum(@IntRange(from = 1, to = 9) int maxCount);
    
    /**
     * 从相册选择图片并剪裁, 用于选择头像
     *
     * @param options  剪裁参数,null使用默认参数
     */
    void fromAlbum(@Nullable CropOptions options);
    
    
    
    /**
     * 从相册中获取图片（不裁剪）
     */
    void onPickFromGallery();
    /**
     * 从相册中获取图片并裁剪
     * @param outPutUri 图片裁剪之后保存的路径
     * @param options 裁剪配置
     */
    void onPickFromGalleryWithCrop(Uri outPutUri, CropOptions options);
    
    
    /**
     * 图片多选
     *
     * @param limit 最多选择图片张数的限制
     */
    void onPickMultiple(int limit);
    
    /**
     * 图片多选，并裁切
     *
     * @param limit   最多选择图片张数的限制
     * @param options 裁剪配置
     */
    void onPickMultipleWithCrop(int limit, CropOptions options);
    
    
    /**
     * 从相机获取图片(不裁剪)
     */
    void onPickFromCapture();
    
    /**
     * 从相机获取图片并裁剪
     *
     * @param options 裁剪配置
     */
    void onPickFromCaptureWithCrop(CropOptions options);
    
    /**
     * 裁剪图片
     *
     * @param imageUri  要裁剪的图片
     * @param outPutUri 图片裁剪之后保存的路径
     * @param options   裁剪配置
     */
    void onCrop(Uri imageUri, Uri outPutUri, CropOptions options) throws TException;
    
    /**
     * 裁剪多张图片
     *
     * @param multipleCrop 要裁切的图片的路径以及输出路径
     * @param options      裁剪配置
     */
    void onCrop(MultipleCrop multipleCrop, CropOptions options) throws TException;
    
    void permissionNotify(PermissionManager.TPermissionType type);
    
    
    /**
     * 设置TakePhoto相关配置
     *
     * @param options
     */
    void setTakePhotoOptions(TakePhotoOptions options);
    
    void onCreate(Bundle savedInstanceState);
    
    void onSaveInstanceState(Bundle outState);
    
    /**
     * 处理拍照或从相册选择的图片或裁剪的结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    void onActivityResult(int requestCode, int resultCode, Intent data);
    
    /**
     * 拍照结果监听接口
     */
    interface TakeResultListener {
        void takeSuccess(TResult result);
        
        void takeFail(TResult result, String msg);
        
        void takeCancel();
    }
}