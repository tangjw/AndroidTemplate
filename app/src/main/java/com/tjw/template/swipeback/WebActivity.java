package com.tjw.template.swipeback;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tjw.selectimage.uitl.L;
import com.tjw.template.R;
import com.tjw.template.util.ToastUtils;
import com.tjw.template.widget.MyBottomSheetDialog;
import com.tjw.template.widget.webview.VideoEnabledWebChromeClient;
import com.tjw.template.widget.webview.VideoEnabledWebView;

/**
 * ^-^
 * Created by tang-jw on 2017/4/7.
 */

public class WebActivity extends BaseSwipeBackActivity {
    
    private Toolbar mToolbar;
    private VideoEnabledWebView mWebView;
    private ProgressBar mProgressBar;
    private VideoEnabledWebChromeClient mWebChromeClient;
    private int mCurrentProgress;
    private boolean isAnimStart;
    private TextView mTvComment;
    private EditText mEtComment;
    private KeyMapDailog dialog;
    
    @Override
    protected void beforeSuperCreate(@Nullable Bundle savedInstanceState) {
        super.beforeSuperCreate(savedInstanceState);
    }
    
    @Override
    protected void initView() {
        setContentView(R.layout.activity_web);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mWebView = (VideoEnabledWebView) findViewById(R.id.webView);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading);
        mTvComment = (TextView) findViewById(R.id.tv_comment_start);
        
        setToolbar(mToolbar);
        setWebView();
    }
    
    @Override
    protected void loadData() {
        mWebView.loadUrl("http://s4.uczzd.cn/ucnews/news?app=ucnews-iflow&aid=16871624809867081690");
    }
    
    @Override
    protected void setListener() {
        mTvComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentDialog();

//                dialog = new KeyMapDailog("回复小明：", new KeyMapDailog.SendBackListener() {
//                    @Override
//                    public void sendBack(final String inputText) {
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                dialog.hideProgressdialog();
//                                
//                                dialog.dismiss();
//                            }
//                        }, 2000);
//                    }
//                });
//    
//                dialog.show(getSupportFragmentManager(), "dialog");
            }
        });
    }
    
    private void showCommentDialog() {
        final MyBottomSheetDialog bottomSheetDialog = new MyBottomSheetDialog(this, R.style.Dialog_Comment);

        bottomSheetDialog.setContentView(R.layout.dialog_comment_web);
        mEtComment = (EditText) bottomSheetDialog.findViewById(R.id.et_comment_edit);
        
        bottomSheetDialog.show();
    
        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                ToastUtils.show(WebActivity.this, "评论框关闭了");
            }
        });
        
    }
    
    private void setWebView() {
        View nonVideoLayout = findViewById(R.id.nonVideoLayout);
        nonVideoLayout.setVisibility(View.VISIBLE);
//        View loadingView = getLayoutInflater().inflate(R.layout.view_loading_video, null);
        final ViewGroup videoLayout = (ViewGroup) findViewById(R.id.videoLayout);
        
        initWebView();
        
        mWebChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, new ProgressBar(this), mWebView) {
            
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                
                L.i("newProgress => " + newProgress);
                
                mCurrentProgress = mProgressBar.getProgress();
                if (newProgress >= 61 && !isAnimStart) {
                    isAnimStart = true;
                    mProgressBar.setProgress(newProgress);
                    startDismissAnimation(mProgressBar.getProgress());
                } else {
                    startProgressAnimation(newProgress);
                }
            }
        };
        
        mWebChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback() {
            @Override
            public void toggledFullscreen(boolean fullscreen) {
                
                if (fullscreen) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                
                fullScreen(fullscreen);
            }
        });
        
        mWebView.setWebChromeClient(mWebChromeClient);
        
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setAlpha(1.0f);
            }
        });
        
    }
    
    private void initWebView() {
        
        WebSettings settings = mWebView.getSettings();
        
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        
        settings.setAppCacheEnabled(true);
        settings.setAppCacheMaxSize(1024 * 1024 * 8);
        settings.setAppCachePath(getCacheDir().getAbsolutePath());
        settings.setAllowFileAccess(true);
        
        settings.setSupportZoom(true);
        
        // Enable Javascript
        settings.setJavaScriptEnabled(true);
        
        // Use WideViewport and Zoom out if there is no viewport defined
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        
        // Enable pinch to zoom without the zoom buttons
        settings.setBuiltInZoomControls(true);
        
        // Allow use of Local Storage
        settings.setDomStorageEnabled(true);
        
        // Hide the zoom controls for HONEYCOMB+
        settings.setDisplayZoomControls(false);
        
        // Enable remote debugging via chrome://inspect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        
    }
    
    @Override
    public void setToolbar(Toolbar toolbar) {
        super.setToolbar(toolbar);
        toolbar.setTitle("新闻详情");
    }
    
    @Override
    public void onPause() {
        super.onPause();
        try {
            Class.forName("android.webkit.WebView")
                    .getMethod("onPause", (Class[]) null)
                    .invoke(mWebView, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        try {
            Class.forName("android.webkit.WebView")
                    .getMethod("onResume", (Class[]) null)
                    .invoke(mWebView, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    /**
     * progressBar递增动画
     */
    private void startProgressAnimation(int newProgress) {
        ObjectAnimator animator = ObjectAnimator.ofInt(mProgressBar, "progress", mCurrentProgress, newProgress + 24);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }
    
    /**
     * progressBar消失动画
     */
    private void startDismissAnimation(final int progress) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(mProgressBar, "alpha", 1.0f, 0.0f);
        anim.setDuration(1000L);  // 动画时长
        anim.setInterpolator(new DecelerateInterpolator());     // 减速
        // 关键, 添加动画进度监听器
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fraction = valueAnimator.getAnimatedFraction();      // 0.0f ~ 1.0f
                int offset = 100 - progress;
                mProgressBar.setProgress((int) (progress + offset * fraction));
            }
        });
        
        anim.addListener(new AnimatorListenerAdapter() {
            
            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束
                mProgressBar.setProgress(0);
                mProgressBar.setVisibility(View.GONE);
                isAnimStart = false;
            }
        });
        anim.start();
    }
}
