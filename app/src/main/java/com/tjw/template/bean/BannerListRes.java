package com.tjw.template.bean;

import java.util.List;

public class BannerListRes extends BaseResponse {
    
    private List<Banner> data;
    
    public List<Banner> getData() {
        return data;
    }
    
    public void setData(List<Banner> data) {
        this.data = data;
    }
    
}
