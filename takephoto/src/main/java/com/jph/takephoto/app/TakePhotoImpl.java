package com.jph.takephoto.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.jph.takephoto.uitl.ImageRotateUtil;
import com.jph.takephoto.uitl.IntentUtils;
import com.jph.takephoto.uitl.TConstant;
import com.jph.takephoto.uitl.TFileUtils;
import com.jph.takephoto.uitl.TImageFiles;
import com.jph.takephoto.uitl.TUriParse;
import com.jph.takephoto.uitl.TUtils;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * - 支持通过相机拍照获取图片
 * - 支持从相册选择图片
 * - 支持从文件选择图片
 * - 支持多图选择
 * - 支持批量图片裁切
 * - 支持批量图片压缩
 * - 支持对图片进行压缩
 * - 支持对图片进行裁剪
 * - 支持对裁剪及压缩参数自定义
 * - 提供自带裁剪工具(可选)
 * - 支持智能选取及裁剪异常处理
 * - 支持因拍照Activity被回收后的自动恢复
 * Date: 2016/9/21 0007 20:10
 * Version:4.0.0
 * 技术博文：http://www.cboy.me
 * GitHub:https://github.com/crazycodeboy
 * Eamil:crazycodeboy@gmail.com
 */

public class TakePhotoImpl implements SelectImage {
    
    private static final String TAG = IntentUtils.class.getName();
    
    private TContextWrap contextWrap;
    private TakeResultListener listener;
    private Uri outPutUri;
    private Uri tempUri;
    private CropOptions cropOptions;
    private TakePhotoOptions takePhotoOptions;
    private PermissionManager.TPermissionType permissionType;
    private TImage.FromType fromType; //CAMERA图片来源相机，OTHER图片来源其他
    
    private ProgressDialog wailLoadDialog;
    
    public TakePhotoImpl(Activity activity, TakeResultListener listener) {
        contextWrap = TContextWrap.of(activity);
        this.listener = listener;
    }
    
    public TakePhotoImpl(Fragment fragment, TakeResultListener listener) {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        
        switch (requestCode) {
            
            case TConstant.RC_PICK_PICTURE_FROM_GALLERY_CROP:
    
                if (resultCode == Activity.RESULT_OK && data != null) {//从相册选择照片并裁剪
        
                    try {
                        onCrop(data.getData(), outPutUri, cropOptions);
                    } catch (TException e) {
                        takeResult(TResult.of(TImage.of(outPutUri, fromType)), e.getDetailMessage());
                        e.printStackTrace();
                    }
                } else {
                    listener.takeCancel();
                }
                break;
            case TConstant.RC_PICK_PICTURE_FROM_GALLERY_ORIGINAL://从相册选择照片不裁剪
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        takeResult(TResult.of(TImage.of(TUriParse.getFilePathWithUri(data.getData(), contextWrap.getActivity()), fromType)));
                    } catch (TException e) {
                        takeResult(TResult.of(TImage.of(data.getData(), fromType)), e.getDetailMessage());
                        e.printStackTrace();
                    }
                } else {
                    listener.takeCancel();
                }
                break;
            case TConstant.RC_PICK_PICTURE_FROM_DOCUMENTS_ORIGINAL://从文件选择照片不裁剪
                if (resultCode == Activity.RESULT_OK) {
                    if (takePhotoOptions != null)
                        ImageRotateUtil.of().correctImage(contextWrap.getActivity(), tempUri);
                    try {
                        takeResult(TResult.of(TImage.of(TUriParse.getFilePathWithDocumentsUri(data.getData(), contextWrap.getActivity()), fromType)));
                    } catch (TException e) {
                        takeResult(TResult.of(TImage.of(outPutUri, fromType)), e.getDetailMessage());
                        e.printStackTrace();
                    }
                } else {
                    listener.takeCancel();
                }
                break;
            case TConstant.RC_PICK_PICTURE_FROM_DOCUMENTS_CROP://从文件选择照片，并裁剪
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        onCrop(data.getData(), outPutUri, cropOptions);
                    } catch (TException e) {
                        takeResult(TResult.of(TImage.of(outPutUri, fromType)), e.getDetailMessage());
                        e.printStackTrace();
                    }
                } else {
                    listener.takeCancel();
                }
                break;
            case TConstant.RC_PICK_PICTURE_FROM_CAPTURE_CROP://拍取照片,并裁剪
                if (resultCode == Activity.RESULT_OK) {
                    if (takePhotoOptions != null && takePhotoOptions.isCorrectImage())
                        ImageRotateUtil.of().correctImage(contextWrap.getActivity(), tempUri);
                    try {
                        onCrop(tempUri, Uri.fromFile(new File(TUriParse.parseOwnUri(contextWrap.getActivity(), outPutUri))), cropOptions);
                    } catch (TException e) {
                        takeResult(TResult.of(TImage.of(outPutUri, fromType)), e.getDetailMessage());
                        e.printStackTrace();
                    }
                } else {
                    listener.takeCancel();
                }
                break;
            case TConstant.RC_PICK_PICTURE_FROM_CAPTURE://拍取照片
    
                if (resultCode == Activity.RESULT_OK) {
                    if (takePhotoOptions != null && takePhotoOptions.isCorrectImage())
                        ImageRotateUtil.of().correctImage(contextWrap.getActivity(), outPutUri);
                    try {
                        takeResult(TResult.of(TImage.of(TUriParse.getFilePathWithUri(outPutUri, contextWrap.getActivity()), fromType)));
                    } catch (TException e) {
                        takeResult(TResult.of(TImage.of(outPutUri, fromType)), e.getDetailMessage());
                        e.printStackTrace();
                    }
                } else {
                    listener.takeCancel();
                }
                break;
            case TConstant.RC_CROP://裁剪照片返回结果
            case Crop.REQUEST_CROP://裁剪照片返回结果
                if (resultCode == Activity.RESULT_OK) {
    
                    try {
                        TImage image = TImage.of(TUriParse.getFilePathWithUri(outPutUri, contextWrap.getActivity()), fromType);
                        image.setCropped(true);
                        takeResult(TResult.of(image));
                    } catch (TException e) {
                        takeResult(TResult.of(TImage.of(outPutUri.getPath(), fromType)), e.getDetailMessage());
                        e.printStackTrace();
                    } finally {
                        deleteRawFile();
                        
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {//裁剪的照片没有保存
    
                    if (data != null) {
                        Bitmap bitmap = data.getParcelableExtra("data");//获取裁剪的结果数据
                        TImageFiles.writeToFile(bitmap, outPutUri);//将裁剪的结果写入到文件
        
                        TImage image = TImage.of(outPutUri.getPath(), fromType);
                        image.setCropped(true);
                        takeResult(TResult.of(image));
                    } else {
                        listener.takeCancel();
        
                    }
                } else {
    
                    listener.takeCancel();
                    
                }
                break;
            case TConstant.RC_PICK_MULTIPLE://多选图片返回结果
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
                    if (cropOptions != null) {
                        try {
                            onCrop(MultipleCrop.of(TUtils.convertImageToUri(contextWrap.getActivity(), images), contextWrap.getActivity(), fromType), cropOptions);
                        } catch (TException e) {
                            e.printStackTrace();
                        }
                    } else {
                        takeResult(TResult.of(TUtils.getTImagesWithImages(images, fromType)));
                    }
    
                } else {
                    listener.takeCancel();
                }
                break;
            default:
                break;
        }
    }
    
    
    @Override
    public void fromCamera() {
    
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
    public void onPickMultiple(int limit) {
        
        if (PermissionManager.TPermissionType.WAIT.equals(permissionType)) {
            return;
        }
        
        TUtils.startActivityForResult(contextWrap, new TIntentWap(IntentUtils.getPickMultipleIntent(contextWrap, limit), TConstant.RC_PICK_MULTIPLE));
    }
    
    @Override
    public void onPickMultipleWithCrop(int limit, CropOptions options) {
        this.fromType = TImage.FromType.OTHER;
        onPickMultiple(limit);
        this.cropOptions = options;
    }
    
    @Override
    public void onCrop(Uri imageUri, Uri outPutUri, CropOptions options) throws TException {
        if (PermissionManager.TPermissionType.WAIT.equals(permissionType)) return;
        this.outPutUri = outPutUri;
        if (!TImageFiles.checkMimeType(contextWrap.getActivity(), TImageFiles.getMimeType(contextWrap.getActivity(), imageUri))) {
            Toast.makeText(contextWrap.getActivity(), contextWrap.getActivity().getResources().getText(R.string.tip_type_not_image), Toast.LENGTH_SHORT).show();
            throw new TException(TExceptionType.TYPE_NOT_IMAGE);
        }
        cropWithNonException(imageUri, outPutUri, options);
    }
    
    @Override
    public void onCrop(MultipleCrop multipleCrop, CropOptions options) throws TException {
        onCrop(multipleCrop.getUris().get(0), multipleCrop.getOutUris().get(0), options);
    }
    
    private void cropWithNonException(Uri imageUri, Uri outPutUri, CropOptions options) {
        this.outPutUri = outPutUri;
        if (options.isWithOwnCrop()) {
            TUtils.cropWithOwnApp(contextWrap, imageUri, outPutUri, options);
        } else {
            TUtils.cropWithOtherAppBySafely(contextWrap, imageUri, outPutUri, options);
        }
    }
    
    
    @Override
    public void onPickFromGallery() {
        selectPicture(1, false);
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
            takeResult(TResult.of(TImage.of("", fromType)), e.getDetailMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void onPickFromGalleryWithCrop(Uri outPutUri, CropOptions options) {
        this.cropOptions = options;
        this.outPutUri = outPutUri;
        selectPicture(1, true);
    }
    
    
    @Override
    public void onPickFromCapture() {
        File path = new File(Environment.getExternalStorageDirectory() + "/DCIM/camera/");
        if (!path.exists()) {
            path.mkdirs();
        }
        File file = createFile(path, "IMG_", ".jpg");
        
        Uri outPutUri = Uri.fromFile(file);
        
        this.fromType = TImage.FromType.CAMERA;
        
        if (PermissionManager.TPermissionType.WAIT.equals(permissionType)) return;
        
        if (Build.VERSION.SDK_INT >= 23) {
            this.outPutUri = TUriParse.convertFileUriToFileProviderUri(contextWrap.getActivity(), outPutUri);
        } else {
            this.outPutUri = outPutUri;
        }
        
        try {
            TUtils.captureBySafely(contextWrap, new TIntentWap(IntentUtils.getCaptureIntent(this.outPutUri), TConstant.RC_PICK_PICTURE_FROM_CAPTURE));
        } catch (TException e) {
            takeResult(TResult.of(TImage.of("", fromType)), e.getDetailMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void onPickFromCaptureWithCrop(CropOptions options) {
        this.fromType = TImage.FromType.CAMERA;
        if (PermissionManager.TPermissionType.WAIT.equals(permissionType)) return;
        this.cropOptions = options;
    
        File path = new File(Environment.getExternalStorageDirectory() + "/DCIM/camera/");
        if (!path.exists()) {
            path.mkdirs();
        }
        File file = createFile(path, "IMG_", ".jpg");
        
        this.outPutUri = Uri.fromFile(file);
        
        if (Build.VERSION.SDK_INT >= 23) {
            this.tempUri = TUriParse.getTempUri(contextWrap.getActivity());
//            this.tempUri =  TUriParse.convertFileUriToFileProviderUri(contextWrap.getActivity(), outPutUri);
        } else {
            this.tempUri = outPutUri;
        }
        
        try {
            TUtils.captureBySafely(contextWrap, new TIntentWap(IntentUtils.getCaptureIntent(this.tempUri), TConstant.RC_PICK_PICTURE_FROM_CAPTURE_CROP));
        } catch (TException e) {
            takeResult(TResult.of(TImage.of("", fromType)), e.getDetailMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 根据系统时间、前缀、后缀产生一个文件
     */
    public File createFile(File folder, String prefix, String suffix) {
        if (!folder.exists() || !folder.isDirectory()) folder.mkdirs();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        String filename = prefix + dateFormat.format(new Date(System.currentTimeMillis())) + suffix;
        return new File(folder, filename);
    }
    
    
    @Override
    public void setTakePhotoOptions(TakePhotoOptions options) {
        this.takePhotoOptions = options;
    }
    
    @Override
    public void permissionNotify(PermissionManager.TPermissionType type) {
        this.permissionType = type;
    }
    
    private void takeResult(final TResult result, final String... message) {
    
        handleTakeCallBack(result, message);
    
    }
    
    private void deleteRawFile(ArrayList<TImage> images) {
        for (TImage image : images) {
            if (TImage.FromType.CAMERA == fromType) {
                TFileUtils.delete(image.getOriginalPath());
                image.setOriginalPath("");
            }
        }
    }
    
    private void deleteRawFile() {
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                String filePathWithUri = TUriParse.getFilePathWithUri(tempUri, contextWrap.getActivity());
    
                TFileUtils.delete(filePathWithUri);
            } catch (TException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    private void handleTakeCallBack(final TResult result, String... message) {
        if (message.length > 0) {
            listener.takeFail(result, message[0]);
    
        } else {
            listener.takeSuccess(result);
        }
        if (TImage.FromType.CAMERA == fromType) {
            scanNewImage(result.getImages().get(0).getOriginalPath());
        }
        
        clearParams();
    
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
    
    private void clearParams() {
        takePhotoOptions = null;
        cropOptions = null;
    }
    
    
}