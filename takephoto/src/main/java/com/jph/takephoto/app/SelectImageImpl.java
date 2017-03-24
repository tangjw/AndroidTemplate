package com.jph.takephoto.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.MultipleCrop;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TException;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TIntentWap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.uitl.IntentUtils;
import com.jph.takephoto.uitl.TConstant;
import com.jph.takephoto.uitl.TUriParse;
import com.jph.takephoto.uitl.TUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * ^-^
 * Created by tang-jw on 2017/3/24.
 */

public class SelectImageImpl implements SelectImage {
    
    private static final String TAG = "SelectImageImpl";
    
    private TContextWrap contextWrap;
    private TakeResultListener listener;
    private Uri outPutUri;
    private Uri tempUri;
    private CropOptions cropOptions;
    private TakePhotoOptions takePhotoOptions;
    private PermissionManager.TPermissionType permissionType;
    private TImage.FromType fromType; //CAMERA图片来源相机，OTHER图片来源其他
    
    public SelectImageImpl(Activity activity, TakeResultListener listener) {
        contextWrap = TContextWrap.of(activity);
        this.listener = listener;
    }
    
    public SelectImageImpl(Fragment fragment, TakeResultListener listener) {
        contextWrap = TContextWrap.of(fragment);
        this.listener = listener;
    }
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            cropOptions = (CropOptions) savedInstanceState.getSerializable("cropOptions");
            takePhotoOptions = (TakePhotoOptions) savedInstanceState.getSerializable("takePhotoOptions");
            outPutUri = savedInstanceState.getParcelable("outPutUri");
            tempUri = savedInstanceState.getParcelable("tempUri");
        }
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("cropOptions", cropOptions);
        outState.putSerializable("takePhotoOptions", takePhotoOptions);
        outState.putParcelable("outPutUri", outPutUri);
        outState.putParcelable("tempUri", tempUri);
    }
    
    @Override
    public void permissionNotify(PermissionManager.TPermissionType type) {
        this.permissionType = type;
    }
    
    @Override
    public void setTakePhotoOptions(TakePhotoOptions options) {
        
    }
    
    
    @Override
    public void fromCamera() {
        if (PermissionManager.TPermissionType.WAIT.equals(permissionType)) return;
        fromType = TImage.FromType.CAMERA;
        
        Uri outPutUri = Uri.fromFile(createOutFile());
        
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            this.outPutUri = TUriParse.convertFileUriToFileProviderUri(contextWrap.getActivity(), outPutUri);
        } else {
            this.outPutUri = outPutUri;
        }
        
        
        TUtils.captureBySafely(contextWrap, new TIntentWap(IntentUtils.getCaptureIntent(this.outPutUri), TConstant.RC_PICK_PICTURE_FROM_CAPTURE));
        
        
    }
    
    
    @Override
    public void fromCamera(@Nullable CropOptions options) {
        
    }
    
    @Override
    public void fromAlbum(@IntRange(from = 1, to = 9) int maxCount) {
        
    }
    
    @Override
    public void fromAlbum(@Nullable CropOptions options) {
        
    }
    
    
    @Override
    public void onPickFromGallery() {
        
    }
    
    @Override
    public void onPickFromGalleryWithCrop(Uri outPutUri, CropOptions options) {
        
    }
    
    
    @Override
    public void onPickMultiple(int limit) {
        
    }
    
    @Override
    public void onPickMultipleWithCrop(int limit, CropOptions options) {
        
    }
    
    @Override
    public void onPickFromCapture() {
        
    }
    
    @Override
    public void onPickFromCaptureWithCrop(CropOptions options) {
        
    }
    
    @Override
    public void onCrop(Uri imageUri, Uri outPutUri, CropOptions options) throws TException {
        
    }
    
    @Override
    public void onCrop(MultipleCrop multipleCrop, CropOptions options) throws TException {
        
    }
    
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        
    }
    
    /**
     * 创建一个用于输出所拍照片的File
     *
     * @return 在默认相册"/DCIM/camera/"下的一个File
     */
    private File createOutFile() {
        
        File path = new File(Environment.getExternalStorageDirectory() + "/DCIM/camera/");
        
        if (!path.exists() || !path.isDirectory()) {
            path.mkdirs();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        
        String filename = "IMG_" + dateFormat.format(new Date(System.currentTimeMillis())) + ".jpg";
        
        return new File(path, filename);
        
    }
    
    
    /**
     * 通知系统重新扫描 图片
     *
     * @param imageFile 图片文件
     */
    private void scanNewImage(File imageFile) {
        contextWrap.getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(imageFile)));
    }
    
    
    /**
     * 通知系统重新扫描 图片
     *
     * @param imagePath 图片路径
     */
    private void scanNewImage(String imagePath) {
        contextWrap.getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + imagePath)));
    }
    
}
