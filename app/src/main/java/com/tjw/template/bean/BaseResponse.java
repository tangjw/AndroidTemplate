package com.tjw.template.bean;

import java.io.Serializable;

/**
 * 服务器响应json的基类
 * code 响应码
 * message 错误信息 Toast 给用户
 * Created by tang-jw on 9/8.
 */
public class BaseResponse implements Serializable {
    
    private int code;
    private String message;
    
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
}
