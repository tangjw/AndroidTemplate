package com.jph.takephoto.album.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jph.takephoto.R;
import com.jph.takephoto.album.adapters.CustomImageSelectAdapter;
import com.jph.takephoto.album.helpers.Constants;
import com.jph.takephoto.album.models.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

public class ImageSelectActivity extends HelperActivity {
    private ArrayList<Image> images;
    private String album;
    
    private TextView errorDisplay;
    
    private ProgressBar progressBar;
    private GridView gridView;
    private CustomImageSelectAdapter adapter;
    
    private int countSelected;
    
    private ContentObserver observer;
    private Handler handler;
    private Thread thread;
    
    private final String[] projection = new String[]{
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA};
    
    private Toolbar mToolbar;
    private MenuItem mActionSelectDone;
    private TextView mTvPreview;
    private TextView mTvAlbum;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);

//        setView(findViewById(R.id.layout_image_select));
        
        mTvPreview = (TextView) findViewById(R.id.tv_preview);
        
        mTvAlbum = (TextView) findViewById(R.id.tv_select_album);
        
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        
        setToolbar();
        
        
        Intent intent = getIntent();
        Constants.limit = intent.getIntExtra(Constants.INTENT_EXTRA_LIMIT, 0);
        
        album = intent.getStringExtra(Constants.INTENT_EXTRA_ALBUM);
        
        errorDisplay = (TextView) findViewById(R.id.text_view_error);
        errorDisplay.setVisibility(View.INVISIBLE);
        
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_image_select);
        gridView = (GridView) findViewById(R.id.grid_view_image_select);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toggleSelection(position);
            }
        });
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Constants.PERMISSION_GRANTED: {
                        loadImages();
                        break;
                    }
                    
                    case Constants.FETCH_STARTED: {
                        progressBar.setVisibility(View.VISIBLE);
                        gridView.setVisibility(View.INVISIBLE);
                        break;
                    }
                    
                    case Constants.FETCH_COMPLETED: {
                        /*
                        If adapter is null, this implies that the loaded images will be shown
                        for the first time, hence send FETCH_COMPLETED message.
                        However, if adapter has been initialised, this thread was run either
                        due to the activity being restarted or content being changed.
                         */
                        if (adapter == null) {
                            adapter = new CustomImageSelectAdapter(getApplicationContext(), images);
                            gridView.setAdapter(adapter);
                            
                            progressBar.setVisibility(View.INVISIBLE);
                            gridView.setVisibility(View.VISIBLE);
                            orientationBasedUI(getResources().getConfiguration().orientation);
                            
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        break;
                    }
                    
                    case Constants.ERROR: {
                        progressBar.setVisibility(View.INVISIBLE);
                        errorDisplay.setVisibility(View.VISIBLE);
                        break;
                    }
                    
                    default: {
                        super.handleMessage(msg);
                    }
                }
            }
        };
        observer = new ContentObserver(handler) {
            @Override
            public void onChange(boolean selfChange) {
                loadImages();
            }
        };
        getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, observer);
        
        checkPermission();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        
        stopThread();
        
        getContentResolver().unregisterContentObserver(observer);
        observer = null;
        
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        images = null;
        if (adapter != null) {
            adapter.releaseResources();
        }
        gridView.setOnItemClickListener(null);
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        orientationBasedUI(newConfig.orientation);
    }
    
    private void orientationBasedUI(int orientation) {
        final WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        final DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        
        if (adapter != null) {
            int size = orientation == Configuration.ORIENTATION_PORTRAIT ? metrics.widthPixels / 3 : metrics.widthPixels / 5;
            adapter.setLayoutParams(size);
        }
        gridView.setNumColumns(orientation == Configuration.ORIENTATION_PORTRAIT ? 3 : 5);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Constants.limit > 1) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_select_toolbar, menu);
            mActionSelectDone = menu.getItem(0);
        }
        return super.onCreateOptionsMenu(menu);
    }
    
    
    private void toggleSelection(int position) {
        
        if (1 == Constants.limit) {
            images.get(position).isSelected = !images.get(position).isSelected;
            if (images.get(position).isSelected) {
                countSelected++;
            } else {
                countSelected--;
            }
            sendIntent();
            return;
        }
        
        
        if (!images.get(position).isSelected && countSelected >= Constants.limit) {
            Toast.makeText(
                    getApplicationContext(),
                    String.format(getString(R.string.limit_exceeded), Constants.limit),
                    Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        
        images.get(position).isSelected = !images.get(position).isSelected;
        if (images.get(position).isSelected) {
            countSelected++;
        } else {
            countSelected--;
        }
        adapter.notifyDataSetChanged();
        
        if (countSelected < 1) {
            mActionSelectDone.setTitle("完成");
        } else {
            mActionSelectDone.setTitle("完成(" + countSelected + "/" + Constants.limit + ")");
        }
        
        if (countSelected >= 1) {
            mTvPreview.setTextColor(Color.WHITE);
            mTvPreview.setText("预览(" + countSelected + ")");
          
        } else {
            mTvPreview.setTextColor(getResources().getColor(R.color.colorTextGray));
            mTvPreview.setText("预览");
        }
        
    }
    
    private void unselectAll() {
        for (int i = 0, l = images.size(); i < l; i++) {
            images.get(i).isSelected = false;
        }
        countSelected = 0;
        adapter.notifyDataSetChanged();
    }
    
    private ArrayList<Image> getSelected() {
        ArrayList<Image> selectedImages = new ArrayList<>();
        for (int i = 0, l = images.size(); i < l; i++) {
            if (images.get(i).isSelected) {
                selectedImages.add(images.get(i));
            }
        }
        return selectedImages;
    }
    
    private void sendIntent() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES, getSelected());
        setResult(RESULT_OK, intent);
        finish();
    }
    
    private void loadImages() {
        startThread(new ImageLoaderRunnable());
    }
    
    private class ImageLoaderRunnable implements Runnable {
        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            /*
            If the adapter is null, this is first time this activity's view is
            being shown, hence send FETCH_STARTED message to show progress bar
            while images are loaded from phone
             */
            if (adapter == null) {
                sendMessage(Constants.FETCH_STARTED);
            }
            
            File file;
            HashSet<Long> selectedImages = new HashSet<>();
            if (images != null) {
                Image image;
                for (int i = 0, l = images.size(); i < l; i++) {
                    image = images.get(i);
                    file = new File(image.path);
                    if (file.exists() && image.isSelected) {
                        selectedImages.add(image.id);
                    }
                }
            }
            
            Cursor cursor;
            if (TextUtils.isEmpty(album)) {
                cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                        null, null, MediaStore.Images.Media.DATE_ADDED);
            } else {
                cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =?", new String[]{album}, MediaStore.Images.Media.DATE_ADDED);
            }
            
            if (cursor == null) {
                sendMessage(Constants.ERROR);
                return;
            }

            /*
            In case this runnable is executed to onChange calling loadImages,
            using countSelected variable can result in a race condition. To avoid that,
            tempCountSelected keeps track of number of selected images. On handling
            FETCH_COMPLETED message, countSelected is assigned value of tempCountSelected.
             */
            int tempCountSelected = 0;
            ArrayList<Image> temp = new ArrayList<>(cursor.getCount());
            if (cursor.moveToLast()) {
                do {
                    if (Thread.interrupted()) {
                        return;
                    }
                    
                    long id = cursor.getLong(cursor.getColumnIndex(projection[0]));
                    String name = cursor.getString(cursor.getColumnIndex(projection[1]));
                    String path = cursor.getString(cursor.getColumnIndex(projection[2]));
                    boolean isSelected = selectedImages.contains(id);
                    if (isSelected) {
                        tempCountSelected++;
                    }
                    
                    file = new File(path);
                    if (file.exists()) {
                        temp.add(new Image(id, name, path, isSelected));
                    }
                    
                } while (cursor.moveToPrevious());
            }
            cursor.close();
            
            if (images == null) {
                images = new ArrayList<>();
            }
            images.clear();
            images.addAll(temp);
            
            sendMessage(Constants.FETCH_COMPLETED, tempCountSelected);
        }
    }
    
    private void startThread(Runnable runnable) {
        stopThread();
        thread = new Thread(runnable);
        thread.start();
    }
    
    private void stopThread() {
        if (thread == null || !thread.isAlive()) {
            return;
        }
        
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private void sendMessage(int what) {
        sendMessage(what, 0);
    }
    
    private void sendMessage(int what, int arg1) {
        if (handler == null) {
            return;
        }
        
        Message message = handler.obtainMessage();
        message.what = what;
        message.arg1 = arg1;
        message.sendToTarget();
    }
    
    @Override
    protected void permissionGranted() {
        sendMessage(Constants.PERMISSION_GRANTED);
    }
    
    @Override
    protected void hideViews() {
        progressBar.setVisibility(View.INVISIBLE);
        gridView.setVisibility(View.INVISIBLE);
    }
    
    
    //-------------------------- 修改--------------------------
    
    /**
     * 设置Toolbar的 相关api
     */
    private void setToolbar() {
        
        ActionBar actionBar = getSupportActionBar();
        
        if (actionBar != null) {
            // 显示 title 默认为 true ,设为 false Toolbar上的 title才会显示
            actionBar.setDisplayShowTitleEnabled(false);
            // 显示返回箭头 ← 默认不显示
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
//        mToolbar.setLogo(R.mipmap.ic_launcher);
        
        mToolbar.setTitle("所有图片");

//        mToolbar.setSubtitle("我是小标题");
        
        // 设置返回键图标
//        mToolbar.setNavigationIcon(R.drawable.ic_back);
        
        mToolbar.setContentInsetStartWithNavigation(0);
        
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unselectAll();
                finish();
            }
        });

//        mToolbar.inflateMenu(R.menu.menu_toolbar);

//        mToolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_add));
        
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_select_done) {
                    if (countSelected > 0) {
                        sendIntent();
                    } else {
                        finish();
                    }
                }
                
                return true;
            }
        });
    }
    
    
}
