package com.courserec.coursesystem.common;

public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    // 成功时的返回
    public static <T> Result<T> success(String message) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        return result;
    }
    // 新增这个方法：既返回成功提示，又返回具体数据
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    // 失败/被拦截时的返回
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500); // 500 代表业务拦截报错
        result.setMessage(message);
        return result;
    }

    // --- Getter 和 Setter ---
    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}