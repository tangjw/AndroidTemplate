package com.tjw.template.jpush;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.tjw.template.bean.PushBean;
import com.tjw.template.swipeback.MainActivity;
import com.tjw.template.swipeback.WebActivity;

import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * ^-^
 * Created by tang-jw on 2017/4/17.
 */

public class JPushReceiver extends BroadcastReceiver {
    
    
    private static final String TAG = "JPushReceiver";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        Log.d(TAG, "onReceive => " + intent.getAction());
        
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            
            String regId = extras.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "接收Registration Id : " + regId);
            //send the Registration Id to your server...
            
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            
            Log.d(TAG, "接收到推送下来的自定义消息: " + extras.getString(JPushInterface.EXTRA_MESSAGE));
            
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            
            Log.d(TAG, "接收到推送下来的通知");
            int notifactionId = extras.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "接收到推送下来的通知的ID: " + notifactionId);
            
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            
            Log.d(TAG, "用户点击打开了通知");
            
            String extrasString = extras.getString(JPushInterface.EXTRA_EXTRA);
            if (!TextUtils.isEmpty(extrasString)) {
                Log.i(TAG, extrasString);
                Gson gson = new Gson();
                PushBean pushBean = gson.fromJson(extrasString, PushBean.class);
                String url = pushBean.getUrl();
                
                Intent intent1;
                if (isAppRunning(context)) {
                    intent1 = new Intent(context, WebActivity.class);
                } else {
                    intent1 = new Intent(context, MainActivity.class);
                    
                }
                
                intent1.putExtra("url", url);
                context.startActivity(intent1);
            }
            
            
            //打开自定义的Activity
//            Intent i = new Intent(context, TestActivity.class);
//            i.putExtras(extras);
//            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            context.startActivity(i);
            
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + extras.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
            
        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
            
        } else {
            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
            
        }
    }
    
    private boolean isAppRunning(Context context) {
        if (context == null) {
            return false;
        }
        
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        
        if (list != null && list.size() > 0) {
            ActivityManager.RunningTaskInfo task = list.get(0);
            Log.i(TAG, task.topActivity.getClassName() + "----" + task.numActivities);
            if (task.topActivity.getClassName().contains("LoginActivity") || task.topActivity.getClassName().contains("MainActivity") || task.numActivities > 1) {
                return true;
            }
        }
        
        return false;
    }
    
}
