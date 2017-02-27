package com.tjw.template.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

/**
 * 自定义的Toast 避免Toast长时间不消失 Created by tang-jw on 2016/6/3.
 */
public class MyToast {

    private static Toast mToast;

    private static void showToast(Context context, String text, boolean flag) {
        if (TextUtils.isEmpty(text)) return;

        int length = flag ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        if (mToast == null) {
            mToast = Toast.makeText(context.getApplicationContext(), text, length);
        }
        mToast.setDuration(length);
        mToast.setText(text);
//		mToast.setGravity(Gravity.CENTER, mToast.getXOffset() / 2, mToast.getYOffset() / 2);
        mToast.setGravity(Gravity.CENTER, mToast.getXOffset() / 2, 4 * mToast.getYOffset() / 5);

        mToast.show();
    }

    public static void show(Context context, String string) {
        showToast(context, string, false);
    }

    public static void showLong(Context context, String string) {
        showToast(context, string, true);
    }

    public static void cancel() {
        if (mToast != null) {
            mToast.cancel();
        }

    }
}
