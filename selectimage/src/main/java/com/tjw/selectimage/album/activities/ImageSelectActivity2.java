package com.tjw.selectimage.album.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import com.tjw.selectimage.uitl.L;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ImageSelectActivity2 extends HelperActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        ImagePreviewFragment.OnFragmentInteractionListener {
    
    private static final String ALBUM_NAME = "albumName";
    private static final int IMAGE_ALL = 0;
    private static final int ALBUM_ALL = 1;
    private static final int IMAGE_ALBUM = 2;
    private final String[] projection = new String[]{
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA};
    private final String[] projection2 = new String[]{
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATA};
    /**
     * 所有的图片 SparseArray id为key
     */
    /**
     * 当前选中的相册名,默认为 null
     */
    private String mSelectedAlbumName;
    private ArrayList<Album> mAllAlbumLists;
    private TextView errorDisplay;
    private ProgressBar progressBar;
    private GridView gridView;
    private CustomImageSelectAdapter adapter;
    private AlbumSelectAdapter mAlbumSelectAdapter;
    private ContentObserver observer;
    private Toolbar mToolbar;
    private MenuItem mActionSelectDone;
    private TextView mTvPreview;
    private TextView mTvAlbum;
    private RelativeLayout mBottomToolBar;
    private ListPopupWindow mListPopupWindow;
    private int mCurrentSelectedAlbum;
    private ArrayList<Image> mImageList;
    /**
     * 全局选中的Image集合
     */
    private ArrayList<Image> mSelectedImageList;
    /**
     * 全局选中的Image id 集合
     */
    private HashSet<Long> mSelectedIdSet;
    /**
     * 当前选中Image在当前Album所有Image集合中的位置
     */
    private LongSparseArray<Integer> mSparseArray;
    private ArrayList<Image> mPreviewImages;
    private ImagePreviewFragment mImagePreviewFragment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    
        setContentView(R.layout.activity_image_select);
    
        initView();
    
        initData();
    
        setViewActionListener();
    }
    
    /**
     * 初始化数据, 查询图片相册
     */
    private void initData() {
        mSelectedIdSet = new HashSet<>();
        mSelectedImageList = new ArrayList<>();
        
        loadImages(null);
        loadAlbums();
    }
    
    private void initView() {
        mTvPreview = (TextView) findViewById(R.id.tv_preview);
        
        mTvPreview.setVisibility(Constants.isCrop ? View.GONE : View.VISIBLE);
        
        mTvAlbum = (TextView) findViewById(R.id.tv_select_album);
        
        mBottomToolBar = (RelativeLayout) findViewById(R.id.rl_select_album);
        
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        
        setToolbar();
        
        errorDisplay = (TextView) findViewById(R.id.text_view_error);
        errorDisplay.setVisibility(View.INVISIBLE);
        
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_image_select);
        gridView = (GridView) findViewById(R.id.grid_view_image_select);
        
        
        mListPopupWindow = new ListPopupWindow(ImageSelectActivity2.this);
        mListPopupWindow.setAnchorView(mBottomToolBar);
        mListPopupWindow.setWidth(ListPopupWindow.MATCH_PARENT);
        mListPopupWindow.setHeight(1000);
        mListPopupWindow.setModal(true);
    }
    
    private void setViewActionListener() {
        
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toggleSelection(position);
            }
        });
        
        mTvAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAllAlbumLists != null && mAllAlbumLists.size() > 0) {
                    mListPopupWindow.show();
                }
            }
        });
        
        mListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    
                mAllAlbumLists.get(mCurrentSelectedAlbum).setSelected(false);
                mAllAlbumLists.get(position).setSelected(true);
                
                mAlbumSelectAdapter.notifyDataSetChanged();
                
                mListPopupWindow.dismiss();
    
                mCurrentSelectedAlbum = position;
                
                if (position != 0) {
                    mSelectedAlbumName = mAllAlbumLists.get(position).getName();
                    mToolbar.setTitle(mSelectedAlbumName);
                } else {
                    mSelectedAlbumName = null;
                    mToolbar.setTitle("所有图片");
                }
    
                loadImages(mSelectedAlbumName);
                
            }
        });
        
        mTvPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedImageList != null && mSelectedImageList.size() > 0) {
                    if (mPreviewImages == null) {
                        mPreviewImages = new ArrayList<>();
                    } else {
                        mPreviewImages.clear();
                    }
        
                    mPreviewImages.addAll(mSelectedImageList);
    
                    mImagePreviewFragment = ImagePreviewFragment.newInstance(mPreviewImages);
                    mImagePreviewFragment.show(getSupportFragmentManager(), "preview");
                }
                
            }
        });
    }
    
    @Override
    protected void onStart() {
        super.onStart();
    
        /*observer = new ContentObserver(handler) {
            @Override
            public void onChange(boolean selfChange) {
                loadAlbums();
                loadImages(mSelectedAlbumName);
            }
        };
        getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, observer);*/

//        checkPermission();
    }
    
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader imageLoader = null;
        switch (id) {
            case IMAGE_ALL:
                imageLoader = new CursorLoader(
                        this,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        null,
                        null,
                        MediaStore.Images.Media.DATE_ADDED);
                break;
            case IMAGE_ALBUM:
                imageLoader = new CursorLoader(
                        this,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =?",
                        new String[]{args.getString(ALBUM_NAME)},
                        MediaStore.Images.Media.DATE_ADDED);
                break;
        }
        return imageLoader;
    }
    
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    
        if (data == null) {
            L.e("Cursor data 为 null");
            return;
        }
        //-------这里按照时间添加的倒序排序!!!!!!!--------
//        LongSparseArray<Image> allImages = new LongSparseArray<>();
        if (mImageList == null) {
            mImageList = new ArrayList<>();
        } else {
            mImageList.clear();
        }
    
    
        if (!data.moveToLast()) {
            L.e("Cursor data 不能已到最后, 即cursor is empty");
            return;
        }
    
        if (mSparseArray == null) {
            mSparseArray = new LongSparseArray<>();
        } else {
            mSparseArray.clear();
        }
    
        for (int i = 0; i < data.getCount(); i++) {
            long img_id = data.getLong(data.getColumnIndex(projection[0]));
            String img_name = data.getString(data.getColumnIndex(projection[1]));
            String img_path = data.getString(data.getColumnIndex(projection[2]));
//            allImages.put(img_id, new Image(img_id, img_name, img_path, false));
            mImageList.add(new Image(img_id, img_name, img_path, mSelectedIdSet.contains(img_id)));
        
            if (mSelectedIdSet.contains(img_id)) {
                mSparseArray.put(img_id, i);
            }
        
            if (!data.moveToPrevious()) {
                break;
            }
        }

//        do {
//            long img_id = data.getLong(data.getColumnIndex(projection[0]));
//            String img_name = data.getString(data.getColumnIndex(projection[1]));
//            String img_path = data.getString(data.getColumnIndex(projection[2]));
////            allImages.put(img_id, new Image(img_id, img_name, img_path, false));
//            mImageList.add(new Image(img_id, img_name, img_path, mSelectedIdSet.contains(img_id)));
//            
//        } while (data.moveToPrevious());
        
        /*long key = 0;
        for (int i = 0; i < allImages.size(); i++) {
            key = allImages.keyAt(i);
            Object obj = allImages.get(key);
        }*/
    
        if (adapter == null) {
            adapter = new CustomImageSelectAdapter(getApplicationContext(), mImageList);
            gridView.setAdapter(adapter);
        } else {
            L.d("adapter.setArrayList(mImageList);");
            adapter.setArrayList(mImageList);
        }
    
    
        progressBar.setVisibility(View.INVISIBLE);
        gridView.setVisibility(View.VISIBLE);
        orientationBasedUI(getResources().getConfiguration().orientation);
    
    
    }
    
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        
    }
    
    /**
     * 根据相册名加载图片
     *
     * @param albumName null加载所有图片
     */
    private void loadImages(@Nullable String albumName) {
    
        L.e("loadImages => " + albumName);
        
        LoaderManager loaderManager = getSupportLoaderManager();
        Bundle args = new Bundle();
        args.putString(ALBUM_NAME, albumName);
        if (TextUtils.isEmpty(albumName)) {
            loaderManager.initLoader(IMAGE_ALL, args, this);
        } else {
            loaderManager.restartLoader(IMAGE_ALBUM, args, this);
        
        }
        
        
    }
    
    
    private void loadAlbums() {
        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(ALBUM_ALL, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                
                return new CursorLoader(getApplicationContext(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection2, null, null,
                        MediaStore.Images.Media.DATE_ADDED);
            }
            
            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                if (data == null) {
                    L.e("Cursor data 为 null");
                    return;
                }
                
                if (!data.moveToLast()) {
                    L.e("Cursor data 不能已到最后, 即cursor is empty");
                    return;
                }
                
                if (mAllAlbumLists == null) {
                    mAllAlbumLists = new ArrayList<>();
                } else {
                    mAllAlbumLists.clear();
                }
                
                
                HashMap<Long, Integer> albumIdPositionMap = new HashMap<>();
                
                L.i("album count " + data.getCount());
                
                int position = 0;
                
                ArrayList<Album> tempAlbumList = new ArrayList<>();
                
                do {
                    long albumId = data.getLong(data.getColumnIndex(projection2[0]));
                    String albumName = data.getString(data.getColumnIndex(projection2[1]));
                    String albumImage = data.getString(data.getColumnIndex(projection2[2]));
                    
                    if (albumIdPositionMap.containsKey(albumId)) {
                        Album album = tempAlbumList.get(albumIdPositionMap.get(albumId));
                        album.setCount(album.getCount() + 1);
                    } else {
                        tempAlbumList.add(new Album(albumName, albumImage, 1, false));
                        albumIdPositionMap.put(albumId, position++);
                    }
                    
                } while (data.moveToPrevious());
                
                mAllAlbumLists.add(new Album("所有图片", tempAlbumList.get(0).getCover(), data.getCount(), false));
                mAllAlbumLists.addAll(tempAlbumList);
                
                
                mAllAlbumLists.get(mCurrentSelectedAlbum).setSelected(true);
                if (mAlbumSelectAdapter == null) {
                    mAlbumSelectAdapter = new AlbumSelectAdapter(mAllAlbumLists);
                    mListPopupWindow.setAdapter(mAlbumSelectAdapter);
                } else {
                    mAlbumSelectAdapter.setAlbums(mAllAlbumLists);
                }
                
            }
            
            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                
            }
        });
    }
    
    @Override
    protected void onStop() {
        super.onStop();

//        getContentResolver().unregisterContentObserver(observer);
//        observer = null;

//        if (handler != null) {
//            handler.removeCallbacksAndMessages(null);
//            handler = null;
//        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();

//        mAllImageList = null;
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
    
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_select_toolbar, menu);
        mActionSelectDone = menu.getItem(0);
        if (Constants.isCrop) {
            mActionSelectDone.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }
    
    private void toggleSelection(int position) {
    
        if (Constants.isCrop) {
            mSelectedImageList.add(mImageList.get(position));
            sendIntent();
            return;
        }
    
        if (!mImageList.get(position).isSelected && mSelectedImageList.size() >= Constants.limit) {
            Toast.makeText(
                    getApplicationContext(),
                    String.format(getString(R.string.limit_exceeded), Constants.limit),
                    Toast.LENGTH_SHORT)
                    .show();
            return;
        }
    
    
        if (!mImageList.get(position).isSelected) {
            mSelectedImageList.add(mImageList.get(position));
            mSelectedIdSet.add(mImageList.get(position).id);
            mSparseArray.put(mImageList.get(position).id, position);
            
        } else {
            mSelectedImageList.remove(mImageList.get(position));
            mSelectedIdSet.remove(mImageList.get(position).id);
            mSparseArray.remove(mImageList.get(position).id);
        }
    
        mImageList.get(position).isSelected = !mImageList.get(position).isSelected;
        
        adapter.notifyDataSetChanged();
        
        setActionBarText();
        
    }
    
    /**
     * 设置工具栏的文字
     */
    private void setActionBarText() {
        int selectedSize = mSelectedImageList.size();
        if (selectedSize > 0) {
            mTvPreview.setTextColor(Color.WHITE);
            mTvPreview.setText("预览(" + selectedSize + ")");
            mActionSelectDone.setTitle("完成(" + selectedSize + "/" + Constants.limit + ")");
        } else {
            mActionSelectDone.setTitle("完成");
            mTvPreview.setTextColor(getResources().getColor(R.color.colorTextGray));
            mTvPreview.setText("预览");
        }
    }
    
    private void sendIntent() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES, mSelectedImageList);
        setResult(RESULT_OK, intent);
        finish();
    }
    
    
    @Override
    protected void permissionGranted() {
        L.e("permissionGranted ---- ");
        
    }
    
    @Override
    protected void hideViews() {
        progressBar.setVisibility(View.INVISIBLE);
//        gridView.setVisibility(View.INVISIBLE);
    }
    
    /**
     * 设置Toolbar的 相关api
     */
    private void setToolbar() {
    
        setSupportActionBar(mToolbar);
        
        ActionBar actionBar = getSupportActionBar();
        
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        
        mToolbar.setTitle("所有图片");
        
        mToolbar.setContentInsetStartWithNavigation(0);
        
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                unselectAll();
                finish();
            }
        });
        
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_select_done) {
                    if (mSelectedImageList.size() > 0) {
                        sendIntent();
                    } else {
                        finish();
                    }
                }
                
                return true;
            }
        });
    }
    
    
    @Override
    public void onChangeImageStatus(int index) {
    
        L.e("mImageArrayList size2 " + mImagePreviewFragment.mImageArrayList.size());
        
        Image tempImage = mPreviewImages.get(index);

//        Image image = mSelectedImageList.get(index);
    
        Integer currentIndex = mSparseArray.get(tempImage.id);
    
        if (mSelectedIdSet.contains(tempImage.id)) {
            mSelectedIdSet.remove(tempImage.id);
            if (currentIndex != null) {
                mImageList.get(currentIndex).isSelected = false;
            }
            mSelectedImageList.remove(index);
            
        } else {
            mSelectedIdSet.add(tempImage.id);
            if (currentIndex != null) {
                mImageList.get(currentIndex).isSelected = true;
            }
            mSelectedImageList.add(index, tempImage);
        }
        L.e("mImageArrayList size3 " + mImagePreviewFragment.mImageArrayList.size());
    }
    
    @Override
    public void onRefreshImageList() {
        adapter.notifyDataSetChanged();
        setActionBarText();
    }
    
}
