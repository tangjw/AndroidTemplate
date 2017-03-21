package com.jph.takephoto.album.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jph.takephoto.R;
import com.jph.takephoto.album.models.Album;

import java.util.ArrayList;

/**
 * Created by Darshan on 4/14/2015.
 */
public class CustomAlbumSelectAdapter extends CustomGenericAdapter<Album> {
    public CustomAlbumSelectAdapter(Context context, ArrayList<Album> albums) {
        super(context, albums);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.grid_view_item_album_select, parent, false);
            
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view_album_image);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.text_view_album_name);
            
            convertView.setTag(viewHolder);
            
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        
        viewHolder.imageView.getLayoutParams().width = size;
        viewHolder.imageView.getLayoutParams().height = size;
    
        viewHolder.textView.setText(arrayList.get(position).getName());
        Glide.with(context)
                .load(arrayList.get(position).getCover())
                .placeholder(R.drawable.image_placeholder).centerCrop().into(viewHolder.imageView);
        
        return convertView;
    }
    
    private static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}