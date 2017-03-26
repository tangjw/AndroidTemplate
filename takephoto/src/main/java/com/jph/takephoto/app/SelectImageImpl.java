package com.jph.takephoto.app;

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
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.jph.takephoto.R;
import com.jph.takephoto.album.helpers.Constants;
import com.jph.takephoto.album.models.Image;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.MultipleCrop;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TException;
import com.jph.takephoto.model.TExceptionType;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TIntentWap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.uitl.IntentUtils;
import com.jph.takephoto.uitl.TConstant;
import com.jph.takephoto.uitl.TImageFiles;
import com.jph.takephoto.uitl.TUriParse;
import com.jph.takephoto.uitl.TUtils;

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
    private TakeResultListener listener;
    private Uri rawImageUri;
    private Uri cropImageUri;
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
    public void setTakePhotoOptions(TakePhotoOptions options) {
        
    }
    
    
    @Override
    public void fromCamera() {
        if (PermissionManager.TPermissionType.WAIT.equals(permissionType)) return;
        this.fromType = TImage.FromType.CAMERA;
        
        Uri outPutUri = Uri.fromFile(createOutFile());
        
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            this.rawImageUri = TUriParse.convertFileUriToFileProviderUri(contextWrap.getActivity(), outPutUri);
        } else {
            this.rawImageUri = outPutUri;
        }
    
    
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
        Uri outPutUri = Uri.fromFile(createOutFile());
    
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            this.rawImageUri = TUriParse.convertFileUriToFileProviderUri(contextWrap.getActivity(), outPutUri);
        } else {
            this.rawImageUri = outPutUri;
        }
        this.cropImageUri = TUriParse.getTempUri(contextWrap.getActivity());
    
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
        this.cropImageUri = TUriParse.getTempUri(contextWrap.getActivity());
    
        TUtils.startActivityForResult(contextWrap, new TIntentWap(IntentUtils.getPickMultipleIntent(contextWrap, 1), TConstant.RC_PICK_MULTIPLE));
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
        if (!TImageFiles.checkMimeType(contextWrap.getActivity(), TImageFiles.getMimeType(contextWrap.getActivity(), imageUri))) {
            Toast.makeText(contextWrap.getActivity(), contextWrap.getActivity().getResources().getText(R.string.tip_type_not_image), Toast.LENGTH_SHORT).show();
            throw new TException(TExceptionType.TYPE_NOT_IMAGE);
        }
        TUtils.cropWithOtherAppBySafely(contextWrap, imageUri, outPutUri, options);
    }
    
    @Override
    public void onCrop(MultipleCrop multipleCrop, CropOptions options) throws TException {
        onCrop(multipleCrop.getUris().get(0), multipleCrop.getOutUris().get(0), options);
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
                    listener.takeCancel();
                }
                break;
        
            case TConstant.RC_PICK_PICTURE_FROM_CAPTURE_CROP://拍取照片,并裁剪
                if (resultCode == Activity.RESULT_OK) {
                   /* if (takePhotoOptions != null && takePhotoOptions.isCorrectImage())
                        ImageRotateUtil.of().correctImage(contextWrap.getActivity(), cropImageUri);*/
                    try {
                        scanNewImage(TUriParse.getFileWithUri(rawImageUri, contextWrap.getActivity()));
                        onCrop(rawImageUri, Uri.fromFile(new File(TUriParse.parseOwnUri(contextWrap.getActivity(), cropImageUri))), cropOptions);
                    } catch (TException e) {
                        handleResult(null, e.getDetailMessage());
                    }
                } else {
                    listener.takeCancel();
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
                
                    listener.takeCancel();
                
                }
                break;
        
            case TConstant.RC_PICK_MULTIPLE://多选图片返回结果
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
                    if (cropOptions != null && images.size() == 1) {
                        try {
                            Uri uri = FileProvider.getUriForFile(contextWrap.getActivity(), TConstant.getFileProviderName(contextWrap.getActivity()), new File(images.get(0).path));
                        
                            onCrop(uri, Uri.fromFile(new File(TUriParse.parseOwnUri(contextWrap.getActivity(), cropImageUri))), cropOptions);
//                            onCrop(MultipleCrop.of(TUtils.convertImageToUri(contextWrap.getActivity(), images), contextWrap.getActivity(), fromType), cropOptions);
                        } catch (TException e) {
                            e.printStackTrace();
                        }
                    } else {
                        handleResult(TResult.of(TUtils.getTImagesWithImages(images, fromType)), null);
                    }
                
                } else {
                    listener.takeCancel();
                }
                break;
        }
    }
    
    
    private void selectPicture(int defaultIndex, boolean isCrop) {
        this.fromType = TImage.FromType.OTHER;
        if (takePhotoOptions != null && takePhotoOptions.isWithOwnGallery()) {
            onPickMultiple(1);
            return;
        }
        if (PermissionManager.TPermissionType.WAIT.equals(permissionType)) return;
        ArrayList<TIntentWap> intentWapList = new ArrayList<>();
        intentWapList.add(new TIntentWap(IntentUtils.getPickIntentWithDocuments(), isCrop ? TConstant.RC_PICK_PICTURE_FROM_DOCUMENTS_CROP : TConstant.RC_PICK_PICTURE_FROM_DOCUMENTS_ORIGINAL));
        intentWapList.add(new TIntentWap(IntentUtils.getPickIntentWithGallery(), isCrop ? TConstant.RC_PICK_PICTURE_FROM_GALLERY_CROP : TConstant.RC_PICK_PICTURE_FROM_GALLERY_ORIGINAL));
        try {
            TUtils.sendIntentBySafely(contextWrap, intentWapList, defaultIndex, isCrop);
        } catch (TException e) {
            handleResult(null, e.getDetailMessage());
            e.printStackTrace();
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
    
    
    private void handleResult(TResult result, String message) {
        
        if (result == null) {
            listener.takeFail(null, message);
            return;
        }
        
        listener.takeSuccess(result);
        
        takePhotoOptions = null;
        cropOptions = null;
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
