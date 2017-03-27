package com.tjw.selectimage.album.activities;

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
import android.support.v4.util.LongSparseArray;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.ListPopupWindow;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tjw.selectimage.R;
import com.tjw.selectimage.album.ImagePreviewFragment;
import com.tjw.selectimage.album.adapters.AlbumSelectAdapter;
import com.tjw.selectimage.album.adapters.CustomImageSelectAdapter;
import com.tjw.selectimage.album.helpers.Constants;
import com.tjw.selectimage.album.models.Album;
import com.tjw.selectimage.album.models.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ImageSelectActivity extends HelperActivity {
    private ArrayList<Image> images;
    private LongSparseArray<Image> allImages;
    private String album;
    
    private ArrayList<Album> albums;
    
    private TextView errorDisplay;
    
    private ProgressBar progressBar;
    private GridView gridView;
    private CustomImageSelectAdapter adapter;
    private AlbumSelectAdapter mAlbumSelectAdapter;
    
    private int countSelected;
    
    private ContentObserver observer;
    private Handler handler;
    private Thread thread;
    private Thread thread2;
    
    private final String[] projection = new String[]{
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA};
    
    private final String[] projection2 = new String[]{
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATA};
    
    private Toolbar mToolbar;
    private MenuItem mActionSelectDone;
    private TextView mTvPreview;
    private TextView mTvAlbum;
    private RelativeLayout mBottomToolBar;
    private ListPopupWindow mListPopupWindow;
    private int mCurrentSelectedAlbum;
    private ArrayList<Long> mSelectImages;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);

//        setView(findViewById(R.id.layout_image_select));
        
        mTvPreview = (TextView) findViewById(R.id.tv_preview);
        
        mTvAlbum = (TextView) findViewById(R.id.tv_select_album);
        
        mBottomToolBar = (RelativeLayout) findViewById(R.id.rl_select_album);
        
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        
        setToolbar();
        allImages = new LongSparseArray<>();
        mSelectImages = new ArrayList<>();
        
        
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
        
        setClickListener();
    }
    
    private void setClickListener() {
        
        mListPopupWindow = new ListPopupWindow(ImageSelectActivity.this);
        mListPopupWindow.setAnchorView(mBottomToolBar);
        mListPopupWindow.setWidth(ListPopupWindow.MATCH_PARENT);
        mListPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
        mListPopupWindow.setModal(true);
        
        mTvAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (albums != null && albums.size() > 0) {
                    mListPopupWindow.show();
                }
            }
        });
        
        mListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
                mCurrentSelectedAlbum = position;
                
                for (int i = 0; i < albums.size(); i++) {
                    albums.get(i).setSelected(i == position);
                }
                mAlbumSelectAdapter.notifyDataSetChanged();
                mListPopupWindow.dismiss();
                if (position != 0) {
                    
                    album = albums.get(position).getName();
                    mToolbar.setTitle(album);
                } else {
                    album = null;
                    mToolbar.setTitle("所有图片");
                }
                loadImages();
                
            }
        });
        
        mTvPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Image> selected = getSelected();
                if (selected != null && selected.size() > 0) {
                    ImagePreviewFragment.newInstance(selected).show(getSupportFragmentManager(), "preview");
                }
                
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
                        loadAlbums();
                        loadImages();
                        break;
                    }
                    
                    case Constants.FETCH_STARTED: {
//                        progressBar.setVisibility(View.VISIBLE);
//                        gridView.setVisibility(View.INVISIBLE);
                        break;
                    }
                    case Constants.ALBUM_FETCH_COMPLETED: {
                        albums.get(mCurrentSelectedAlbum).setSelected(true);
                        if (mAlbumSelectAdapter == null) {
                            mAlbumSelectAdapter = new AlbumSelectAdapter(albums);
                            mListPopupWindow.setAdapter(mAlbumSelectAdapter);
                        } else {
                            mAlbumSelectAdapter.notifyDataSetChanged();
                        }
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
                    case Constants.EMPTY: {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(ImageSelectActivity.this, "没有图片", Toast.LENGTH_SHORT).show();
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
                loadAlbums();
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
        stopThread2();
        
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
        allImages.put(images.get(position).id, images.get(position));
        if (images.get(position).isSelected) {
            countSelected++;
            mSelectImages.add(images.get(position).id);
        } else {
            mSelectImages.remove(images.get(position).id);
            countSelected--;
        }
        adapter.notifyDataSetChanged();
        
        setActionBarText();
        
    }
    
    
    private void setActionBarText() {
        if (countSelected >= 1) {
            mTvPreview.setTextColor(Color.WHITE);
            mTvPreview.setText("预览(" + countSelected + ")");
            mActionSelectDone.setTitle("完成(" + countSelected + "/" + Constants.limit + ")");
            
        } else {
            mActionSelectDone.setTitle("完成");
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
    
    public void setImageSelect(long id, boolean isChecked) {
        
        if (isChecked && !allImages.get(id).isSelected) {
            countSelected++;
            mSelectImages.add(id);
            allImages.get(id).isSelected = true;
            for (Image image : images) {
                if (image.id == id) {
                    image.isSelected = true;
                }
            }
            adapter.notifyDataSetChanged();
            setActionBarText();
        } else if (!isChecked && allImages.get(id).isSelected) {
            mSelectImages.remove(id);
            countSelected--;
            allImages.get(id).isSelected = false;
            
            for (Image image : images) {
                if (image.id == id) {
                    image.isSelected = false;
                }
            }
            adapter.notifyDataSetChanged();
            
            
            setActionBarText();
        }
        
        
    }
    
    private ArrayList<Image> getSelected() {
        ArrayList<Image> selectedImages = new ArrayList<>();
        
        for (int i = 0, nsize = allImages.size(); i < nsize; i++) {
            Image obj = allImages.valueAt(i);
            if (obj.isSelected) {
                selectedImages.add(obj);
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
            boolean isAllAlbum = TextUtils.isEmpty(album);
            if (isAllAlbum) {
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
                        cursor.close();
                        return;
                    }
                    
                    long id = cursor.getLong(cursor.getColumnIndex(projection[0]));
                    String name = cursor.getString(cursor.getColumnIndex(projection[1]));
                    String path = cursor.getString(cursor.getColumnIndex(projection[2]));
                    boolean isSelected = selectedImages.contains(id);
                    if (isSelected) {
                        tempCountSelected++;
                    }
                    isSelected = mSelectImages.contains(id);
                    file = new File(path);
                    if (file.exists()) {
                        Image image = new Image(id, name, path, isSelected);
                        temp.add(image);
                        allImages.put(id, image);
                        
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
    
    private void startThread2(Runnable runnable) {
        stopThread2();
        thread2 = new Thread(runnable);
        thread2.start();
    }
    
    
    private void loadAlbums() {
        startThread2(new AlbumLoaderRunnable());
    }
    
    private class AlbumLoaderRunnable implements Runnable {
        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            
            if (mAlbumSelectAdapter == null) {
                sendMessage(Constants.ALBUM_FETCH_STARTED);
            }
            
            Cursor cursor = getContentResolver()
                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection2,
                            null, null, MediaStore.Images.Media.DATE_ADDED);
            if (cursor == null) {
                sendMessage(Constants.ERROR);
                return;
            }
            
            
            ArrayList<Album> temp = new ArrayList<>(cursor.getCount());
//            HashSet<Long> albumSet = new HashSet<>();
            
            HashMap<Long, Integer> albumMap = new HashMap<>();
            
            File file;
            int i = 0;
            if (cursor.moveToLast()) {
                do {
                    if (Thread.interrupted()) {
                        cursor.close();
                        return;
                    }
                    
                    long albumId = cursor.getLong(cursor.getColumnIndex(projection2[0]));
                    String album = cursor.getString(cursor.getColumnIndex(projection2[1]));
                    String image = cursor.getString(cursor.getColumnIndex(projection2[2]));
    
    
                    /*Cursor cursor1 = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                            MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =?", new String[]{album}, MediaStore.Images.Media.DATE_ADDED);
                    if (cursor1 == null) {
                        sendMessage(Constants.ERROR);
                        return;
                    }
                    
                    int count = cursor1.getCount();
                    
                    cursor1.close();*/
                    
                    if (!albumMap.containsKey(albumId)) {
                        file = new File(image);
                        if (file.exists()) {
                            temp.add(new Album(album, image, 1));
                            albumMap.put(albumId, i++);
                        }
                    } else {
                        Album album1 = temp.get(albumMap.get(albumId));
                        album1.setCount(album1.getCount() + 1);
                    }
                    
                    /*if (!albumSet.contains(albumId)) {
                        *//*
                        It may happen that some image file paths are still present in cache,
                        though image file does not exist. These last as long as media
                        scanner is not run again. To avoid get such image file paths, check
                        if image file exists.
                         *//*
                        file = new File(image);
                        if (file.exists()) {
                            temp.add(new Album(album, image, 1));
                            albumSet.add(albumId);
                        }
                    }*/
                    
                } while (cursor.moveToPrevious());
            }
            if (temp.size() == 0) {
                sendMessage(Constants.EMPTY);
                cursor.close();
                return;
            }
            Album allalbum = new Album("所有图片", temp.get(0).getCover(), cursor.getCount());
            
            cursor.close();
            
            if (albums == null) {
                albums = new ArrayList<>();
            }
            albums.clear();
//            albums.add(all);
            albums.add(allalbum);
            
            albums.addAll(temp);
            
            sendMessage(Constants.ALBUM_FETCH_COMPLETED);
        }
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
    
    private void stopThread2() {
        if (thread2 == null || !thread2.isAlive()) {
            return;
        }
        
        thread2.interrupt();
        try {
            thread2.join();
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
        setSupportActionBar(mToolbar);
        
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