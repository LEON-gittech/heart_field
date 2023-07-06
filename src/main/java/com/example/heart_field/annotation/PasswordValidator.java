package com.example.heart_field.annotation;

import com.example.heart_field.constant.RegexPattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    @Override
    public void initialize(Password constraintAnnotation) {
        // 初始化操作
    }
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Pattern passwordPattern = Pattern.compile(RegexPattern.PASSWORD_PATTERN);
        return value != null && passwordPattern.matcher(value).matches();
    }
}
