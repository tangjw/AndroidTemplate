package com.tjw.template.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.tjw.template.R;
import com.tjw.template.swipeback.BaseActivity;

/**
 * ^-^
 * Created by tang-jw on 2017/3/16.
 */

public class CameraActivity extends BaseActivity {
    
    private static final int TAKE_PHOTO = 201;
    private static final int TAKE_ALBUM = 202;
    private ImageView mImageView;
    
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_camera);
        
        mImageView = (ImageView) findViewById(R.id.imageView);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        
        switch (requestCode) {
            case TAKE_PHOTO:
                Bundle extras = data.getExtras();
                Bitmap bitmap = (Bitmap) extras.get("data");
                mImageView.setImageBitmap(bitmap);
                
                break;
            
            case TAKE_ALBUM:
//                Bundle extras1 = data.getExtras();
//                Bitmap bitmap1 = (Bitmap) extras1.get("data");
//                mImageView.setImageBitmap(bitmap1);
                Uri data1 = data.getData();
    
                System.out.println(data1);
    
                break;
        }
        
        
    }
    
    public void openCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PHOTO);
        
        
    }
    
    public void openAlbum(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, TAKE_ALBUM);
    }
}
