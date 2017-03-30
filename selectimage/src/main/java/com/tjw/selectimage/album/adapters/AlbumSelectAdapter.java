package com.tjw.selectimage.album.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.tjw.selectimage.R;
import com.tjw.selectimage.album.models.Album;

import java.util.ArrayList;

/**
 * ^-^
 * Created by tang-jw on 2017/3/21.
 */

public class AlbumSelectAdapter extends BaseAdapter {
    
    private ArrayList<Album> albums;
    
    public AlbumSelectAdapter(ArrayList<Album> albums) {
        this.albums = albums;
    }
    
    
    public void setAlbums(ArrayList<Album> albums) {
        this.albums = albums;
        notifyDataSetChanged();
    }
    
    @Override
    public int getCount() {
        return albums.size();
    }
    
    @Override
    public Album getItem(int position) {
        return albums.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        ViewHolder viewHolder;
        
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_view_item_album_select, parent, false);
            
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view_album_image);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.text_view_album_name);
            viewHolder.textView1 = (TextView) convertView.findViewById(R.id.tv_image_count);
            viewHolder.radioButton = (RadioButton) convertView.findViewById(R.id.radiobutton);
            
            convertView.setTag(viewHolder);
            
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        
        
        viewHolder.textView.setText(albums.get(position).getName());
        viewHolder.textView1.setText(albums.get(position).getCount() + "张");
        viewHolder.radioButton.setChecked(albums.get(position).isSelected());
        
        if (albums.get(position).getCover().endsWith(".gif") || albums.get(position).getCover().contains(".gif")) {
            Glide.with(parent.getContext())
                    .load(albums.get(position).getCover())
                    .centerCrop()
                    .into(new GlideDrawableImageViewTarget(viewHolder.imageView, 0));
        } else {
            Glide.with(parent.getContext())
                    .load(albums.get(position).getCover())
                    .centerCrop()
                    .into(viewHolder.imageView);
        }
        
        return convertView;
    }
    
    private static class ViewHolder {
        ImageView imageView;
        TextView textView;
        TextView textView1;
        RadioButton radioButton;
    }
}
