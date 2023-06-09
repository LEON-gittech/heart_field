package com.example.heart_field.tokens;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author albac0020@gmail.com
 * data 2023/5/19 10:52 AM
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface StaffToken {
    boolean required() default true;
}


