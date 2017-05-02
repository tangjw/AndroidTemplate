package com.tjw.template.net;

/**
 * ^-^
 * Created by tang-jw on 2017/4/28.
 */

public class ApiException extends Exception {
    
    private final int code;
    private final String message;
    
    public ApiException(int code, String message) {
        super(getMessage(code, message));
        this.code = code;
        this.message = message;
    }
    
    
    private static String getMessage(int code, String message) {
        if (message == null) throw new NullPointerException("message == null");
        return "HTTP " + code + " " + message;
    }
}
