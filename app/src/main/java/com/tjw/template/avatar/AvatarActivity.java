package com.tjw.template.avatar;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tjw.template.R;
import com.tjw.template.swipeback.BaseSwipeBackActivity;

/**
 * ^-^
 * Created by tang-jw on 2017/8/28.
 */

public class AvatarActivity extends BaseSwipeBackActivity {
    
    private ImageView mImageView;
    
    @Override
    protected void initView() {
        setContentView(R.layout.activity_avatar);
        mImageView = (ImageView) findViewById(R.id.imageView);
    }
    
    @Override
    protected void setListener() {
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    
        Uri uri = data.getData();
    
        System.out.println(uri.toString());
    
        System.out.println(Utils.getPath(this, uri));
        
        if (TextUtils.isEmpty(Utils.getPath(this, uri))) return;
        Glide.with(this)
                .load(Utils.getPath(this, uri))
                .into(mImageView);

//        String action = data.getAction();
//        //如果 intent.putExtra("return-data", true);则可以得到返回的Bitmap数据  
//        final Bundle extras = data.getExtras();
//        Bitmap bitmap = extras.getParcelable("data");
//        mImageView.setImageBitmap(bitmap);
//    
    }
    
    /**
     * 打开 Document 选择图片
     */
    private void pickImage() {
        Intent intent;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
             intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, null);
        } 
        
        intent.setType("image/*");
        //intent.putExtra("return-data", true);
        startActivityForResult(intent, 123);
    }
}
