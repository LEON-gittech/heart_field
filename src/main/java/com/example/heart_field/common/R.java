package com.example.heart_field.common;

import lombok.Data;

/**
 * 通用返回类
 * @param <T>
 * 泛型
 */

@Data
public class R<T> {
    /**
     * 编码：0成功，-1未知错误，表示第三方系统出现的问题
     * -2参数格式不正确，-3接口调用次数超限
     * -4未找到groupId， -5未开通权限
     * -6 超出本月可调用次数限制
     */
    private Integer code;

    private String msg; //错误信息

    private T data; //数据

//    private Map map = new HashMap(); //动态数据

    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.msg = "调用成功";
        r.code = 0;
        return r;
    }

    public static <T> R<T> login_error() {
        R r = new R();
        r.msg = "登录失败";
        r.code = -1;
        return r;
    }

//    public R<T> add(String key, Object value) {
//        this.map.put(key, value);
//        return this;
//    }

    public static <T> R<T> argument_error() {
        R r = new R();
        r.msg = "参数格式出现错误";
        r.code = -2;
        return r;
    }

    public static <T> R<T> url_error() {
        R r = new R();
        r.msg = "请求url不存在";
        r.code = -3;
        return r;
    }

    public static <T> R<T> auth_error() {
        R r = new R();
        r.msg = "没有权限";
        r.code = -4;
        return r;
    }

    public static <T> R<T> call_exceed_error() {
        R r = new R();
        r.msg = "接口调用次数超出限制";
        r.code = -5;
        return r;
    }

    public static <T> R<T> resource_error() {
        R r = new R();
        r.msg = "未发现所请求的某项资源";
        r.code = -6;
        return r;
    }

    public static <T> R<T> file_format_error() {
        R r = new R();
        r.msg = "上传的文件格式错误";
        r.code = -7;
        return r;
    }

    /**
     * 自定义error
     * @param msg
     * @return
     * @param <T>
     */
    public static <T> R<T> error(String msg) {
        R r = new R();
        r.msg = msg;
        r.code = -8;
        return r;
    }
}
