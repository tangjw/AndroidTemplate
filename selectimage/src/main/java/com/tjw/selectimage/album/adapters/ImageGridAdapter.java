package com.tjw.selectimage.album.adapters;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

/**
 * ^-^
 * Created by tang-jw on 2017/3/30.
 */

public class ImageGridAdapter extends SimpleCursorAdapter {
    public ImageGridAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }
}
