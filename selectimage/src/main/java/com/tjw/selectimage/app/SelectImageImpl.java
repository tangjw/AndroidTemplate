package com.tjw.selectimage.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.tjw.selectimage.R;
import com.tjw.selectimage.album.helpers.Constants;
import com.tjw.selectimage.album.models.Image;
import com.tjw.selectimage.crop.CropActivity;
import com.tjw.selectimage.model.CropOptions;
import com.tjw.selectimage.model.TContextWrap;
import com.tjw.selectimage.model.TException;
import com.tjw.selectimage.model.TExceptionType;
import com.tjw.selectimage.model.TImage;
import com.tjw.selectimage.model.TIntentWap;
import com.tjw.selectimage.model.TResult;
import com.tjw.selectimage.model.TakePhotoOptions;
import com.tjw.selectimage.permission.PermissionManager;
import com.tjw.selectimage.uitl.IntentUtils;
import com.tjw.selectimage.uitl.TConstant;
import com.tjw.selectimage.uitl.TImageFiles;
import com.tjw.selectimage.uitl.TUriParse;
import com.tjw.selectimage.uitl.TUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * ^-^
 * Created by tang-jw on 2017/3/24.
 */

public class SelectImageImpl implements SelectImage {
    
    private static final String TAG = "SelectImageImpl";
    
    private TContextWrap contextWrap;
    private SelectResultListener listener;
    private Uri rawImageUri;
    private Uri cropImageUri;
    private CropOptions cropOptions;
    private TakePhotoOptions takePhotoOptions;
    private PermissionManager.TPermissionType permissionType;
    private TImage.FromType fromType; //CAMERA图片来源相机，OTHER图片来源其他
    
    public SelectImageImpl(Activity activity, SelectResultListener listener) {
        contextWrap = TContextWrap.of(activity);
        this.listener = listener;
    }
    
    public SelectImageImpl(Fragment fragment, SelectResultListener listener) {
        contextWrap = TContextWrap.of(fragment);
        this.listener = listener;
    }
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            cropOptions = (CropOptions) savedInstanceState.getSerializable("cropOptions");
            takePhotoOptions = (TakePhotoOptions) savedInstanceState.getSerializable("takePhotoOptions");
            rawImageUri = savedInstanceState.getParcelable("outPutUri");
            cropImageUri = savedInstanceState.getParcelable("cropImageUri");
        }
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("cropOptions", cropOptions);
        outState.putSerializable("takePhotoOptions", takePhotoOptions);
        outState.putParcelable("rawImageUri", rawImageUri);
        outState.putParcelable("cropImageUri", cropImageUri);
    }
    
    @Override
    public void permissionNotify(PermissionManager.TPermissionType type) {
        this.permissionType = type;
    }
    
    
    @Override
    public void fromCamera() {
        if (PermissionManager.TPermissionType.WAIT.equals(permissionType)) return;
        this.fromType = TImage.FromType.CAMERA;
        
        this.rawImageUri = file2uri(createOutFile());
        
        try {
            TUtils.captureBySafely(contextWrap, new TIntentWap(IntentUtils.getCaptureIntent(this.rawImageUri), TConstant.RC_PICK_PICTURE_FROM_CAPTURE));
        } catch (TException e) {
            //打开相机失败
            handleResult(null, e.getDetailMessage());
        }
        
        
    }
    
    @Override
    public void fromCamera(@Nullable CropOptions options) {
        if (PermissionManager.TPermissionType.WAIT.equals(permissionType)) return;
        this.fromType = TImage.FromType.CAMERA;
        this.cropOptions = options;
        
        this.rawImageUri = file2uri(createOutFile());
    
        this.cropImageUri = Uri.fromFile(createCropFile());
        
        try {
            TUtils.captureBySafely(contextWrap, new TIntentWap(IntentUtils.getCaptureIntent(this.rawImageUri), TConstant.RC_PICK_PICTURE_FROM_CAPTURE_CROP));
        } catch (TException e) {
            handleResult(null, e.getDetailMessage());
        }
    }
    
    
    @Override
    public void fromAlbum(@IntRange(from = 1, to = 9) int maxCount) {
        if (PermissionManager.TPermissionType.WAIT.equals(permissionType)) {
            return;
        }
        
        this.fromType = TImage.FromType.OTHER;
        
        TUtils.startActivityForResult(contextWrap, new TIntentWap(IntentUtils.getPickMultipleIntent(contextWrap, maxCount), TConstant.RC_PICK_MULTIPLE));
    }
    
    @Override
    public void fromAlbum(@Nullable CropOptions options) {
        if (PermissionManager.TPermissionType.WAIT.equals(permissionType)) {
            return;
        }
        
        this.fromType = TImage.FromType.OTHER;
        this.cropOptions = options;
        this.cropImageUri = Uri.fromFile(createCropFile());
        
        TUtils.startActivityForResult(contextWrap, new TIntentWap(IntentUtils.getPickMultipleIntent(contextWrap, 1), TConstant.RC_PICK_MULTIPLE));
    }
    
    
    @Override
    public void onCrop(Uri imageUri, CropOptions options) throws TException {
        
        if (!TImageFiles.checkMimeType(contextWrap.getActivity(), TImageFiles.getMimeType(contextWrap.getActivity(), imageUri))) {
            Toast.makeText(contextWrap.getActivity(), contextWrap.getActivity().getResources().getText(R.string.tip_type_not_image), Toast.LENGTH_SHORT).show();
            throw new TException(TExceptionType.TYPE_NOT_IMAGE);
        }
    
        System.out.println(imageUri);
    
        Intent intent = new Intent(contextWrap.getActivity(), CropActivity.class);
        intent.putExtra("rawImageUri", imageUri);
        intent.putExtra("cropImageUri", cropImageUri);
        contextWrap.getActivity().startActivityForResult(intent, TConstant.RC_CROP);

//        TUtils.cropWithOtherAppBySafely(contextWrap, imageUri, cropImageUri, options);
    }
    
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TConstant.RC_PICK_PICTURE_FROM_CAPTURE:
                if (resultCode == Activity.RESULT_OK) {
                    // TODO: 3/26 旋转图片 三星问题 
                    /*if (takePhotoOptions != null && takePhotoOptions.isCorrectImage())
                        ImageRotateUtil.of().correctImage(contextWrap.getActivity(), outPutUri);*/
                    try {
                        String rawImagePath = TUriParse.getFilePathWithUri(rawImageUri, contextWrap.getActivity());
                        scanNewImage(rawImagePath);
                        handleResult(TResult.of(TImage.of(rawImagePath, fromType)), null);
                    } catch (TException e) {
                        handleResult(null, e.getDetailMessage());
                    }
                } else {
                    listener.selectCancel();
                }
                break;
            
            case TConstant.RC_PICK_PICTURE_FROM_CAPTURE_CROP://拍取照片,并裁剪
                if (resultCode == Activity.RESULT_OK) {
                   /* if (takePhotoOptions != null && takePhotoOptions.isCorrectImage())
                        ImageRotateUtil.of().correctImage(contextWrap.getActivity(), cropImageUri);*/
                    try {
                        scanNewImage(TUriParse.getFileWithUri(rawImageUri, contextWrap.getActivity()));
                        onCrop(rawImageUri, cropOptions);
                    } catch (TException e) {
                        handleResult(null, e.getDetailMessage());
                    }
                } else {
                    listener.selectCancel();
                }
                break;
            
            case TConstant.RC_CROP://系统app裁剪照片返回结果
//            case Crop.REQUEST_CROP://第三方裁剪照片返回结果
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        TImage image = TImage.of(TUriParse.getFilePathWithUri(cropImageUri, contextWrap.getActivity()), fromType);
                        image.setCropped(true);
                        handleResult(TResult.of(image), null);
                    } catch (TException e) {
                        handleResult(null, e.getDetailMessage());
                        e.printStackTrace();
                    }
    
    
                } else {
                    
                    listener.selectCancel();
                    
                }
                break;
            
            case TConstant.RC_PICK_MULTIPLE://多选图片返回结果
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
                    if (cropOptions != null && images.size() == 1) {
                        try {
                            onCrop(file2uri(new File(images.get(0).path)), cropOptions);
//                            onCrop(MultipleCrop.of(TUtils.convertImageToUri(contextWrap.getActivity(), images), contextWrap.getActivity(), fromType), cropOptions);
                        } catch (TException e) {
                            e.printStackTrace();
                        }
                    } else {
                        handleResult(TResult.of(TUtils.getTImagesWithImages(images, fromType)), null);
                    }
                    
                } else {
                    listener.selectCancel();
                }
                break;
        }
    }
    
    
    /**
     * 创建一个用于输出所拍照片的File
     *
     * @return 在默认相册"/DCIM/camera/"下的一个File
     */
    private File createOutFile() {
        
        File path = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "camera");
        
        if (!path.exists() || !path.isDirectory()) {
            path.mkdirs();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        
        String filename = "IMG_" + dateFormat.format(new Date(System.currentTimeMillis())) + ".jpg";
        
        return new File(path, filename);
        
    }
    
    /**
     * 创建一个用于输出剪切后的File
     *
     * @return cache目录下"/user/avatar.jpg"
     */
    private File createCropFile() {
    
       /* if (file.exists()) {
            file.delete();
        }*/
        
        File file = new File(contextWrap.getActivity().getExternalCacheDir().getAbsolutePath() + "/user/avatar.jpg");
        if (file.exists()) {
            file.delete();
        }
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        /*File path = new File(contextWrap.getActivity().getFilesDir().getAbsolutePath(), "user");
        
        if (!path.exists() || !path.isDirectory()) {
            path.mkdirs();
        }*/
        return file;
        
    }
    
    
    private void handleResult(TResult result, String message) {
        
        if (result == null) {
            listener.selectFail(message);
            return;
        }
        
        listener.selectSuccess(result);
        
        takePhotoOptions = null;
        cropOptions = null;
    }
    
    private Uri file2uri(File file) {
        
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            return TUriParse.convertFileUriToFileProviderUri(contextWrap.getActivity(), Uri.fromFile(file));
        } else {
            return Uri.fromFile(file);
        }
        
        
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
    private void scanNewImage(@NonNull String imagePath) {
        contextWrap.getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + imagePath)));
    }
    
}
