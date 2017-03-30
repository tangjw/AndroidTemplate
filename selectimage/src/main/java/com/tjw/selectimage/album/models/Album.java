package com.tjw.selectimage.album.models;

/**
 * Created by Darshan on 4/14/2015.
 */
public class Album {
    private String name;
    private String cover;
    private int count;
    private boolean selected;
    
    public int getCount() {
        return count;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
    
    public String getCover() {
        return cover;
    }
    
    public void setCover(String cover) {
        this.cover = cover;
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Album(String name, String cover, int count, boolean isSelected) {
        this.name = name;
        this.cover = cover;
        this.count = count;
        this.selected = isSelected;
    }
}
