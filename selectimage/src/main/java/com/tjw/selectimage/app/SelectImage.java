package com.tjw.selectimage.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

import com.tjw.selectimage.model.CropOptions;
import com.tjw.selectimage.model.TException;
import com.tjw.selectimage.model.TResult;
import com.tjw.selectimage.permission.PermissionManager;


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
     * @param options 剪裁参数,null使用默认参数
     */
    void fromAlbum(@Nullable CropOptions options);
    
    /**
     * 裁剪图片
     *
     * @param imageUri  要裁剪的图片
     * @param outPutUri 图片裁剪之后保存的路径
     * @param options   裁剪配置
     */
    void onCrop(Uri imageUri, CropOptions options) throws TException;
    
    void permissionNotify(PermissionManager.TPermissionType type);
    
    void onCreate(Bundle savedInstanceState);
    
    void onSaveInstanceState(Bundle outState);
    
    /**
     * 处理拍照或从相册选择的图片或裁剪的结果
     */
    void onActivityResult(int requestCode, int resultCode, Intent data);
    
    /**
     * 选择图片结果监听接口
     */
    interface SelectResultListener {
        void selectSuccess(TResult result);
        
        void selectFail(String msg);
        
        void selectCancel();
    }
}