package com.example.heart_field.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

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

    private Map map = new HashMap(); //动态数据

    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    public static <T> R<T> error(String msg) {
        R r = new R();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}
