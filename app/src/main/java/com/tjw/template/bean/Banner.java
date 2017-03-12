package com.tjw.template.bean;

/**
 * Created by Android on 2017/2/20.
 */

public class Banner {
    
    private int id;
    private int meetingId;
    private String name;
    private String img;
    private int bannerType;
    private String url;
    private String descr;
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getMeetingId() {
        return meetingId;
    }
    
    public void setMeetingId(int meetingId) {
        this.meetingId = meetingId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getImg() {
        return img;
    }
    
    public void setImg(String img) {
        this.img = img;
    }
    
    public int getBannerType() {
        return bannerType;
    }
    
    public void setBannerType(int bannerType) {
        this.bannerType = bannerType;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getDescr() {
        return descr;
    }
    
    public void setDescr(String descr) {
        this.descr = descr;
    }
}
