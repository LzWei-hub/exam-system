package com.zw.exception;

/**
 * API异常类
 */
public class ApiException extends RuntimeException {
    
    private Integer code;
    
    public ApiException(String message) {
        super(message);
        this.code = 500;
    }
    
    public ApiException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }
    
    public ApiException(Integer code, String message) {
        super(message);
        this.code = code;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
} 