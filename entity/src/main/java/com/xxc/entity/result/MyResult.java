package com.xxc.entity.result;

import java.io.Serializable;

/**
 * @version 1.0.0
 * @Author: xixincan
 * 2019-06-07
 */
public class MyResult<T> implements Serializable {

    private boolean success = true;

    private Integer code = 200;

    private String message = "操作成功";

    private Throwable exp;

    private T data;

    private Long currentTimeMills;

    public MyResult() {
    }

    public MyResult(T data) {
        this.data = data;
    }

    public MyResult(Integer code, String message, Throwable... exp) {
        this.code = code;
        this.message = message;
        if (null != exp && exp.length > 0) {
            this.exp = exp[0];
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public MyResult<T> setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public MyResult<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public MyResult<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public Throwable getExp() {
        return exp;
    }

    public MyResult<T> setExp(Throwable exp) {
        this.exp = exp;
        return this;
    }

    public T getData() {
        return data;
    }

    public MyResult<T> setData(T data) {
        this.data = data;
        return this;
    }

    public Long getCurrentTimeMills() {
        return System.currentTimeMillis();
    }

}
