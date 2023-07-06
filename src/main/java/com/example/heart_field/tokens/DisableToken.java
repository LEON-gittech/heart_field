package com.example.heart_field.tokens;

/**
 * @author albac0020@gmail.com
 * data 2023/6/20 10:02 PM
 */
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DisableToken{
    boolean required() default true;
}
