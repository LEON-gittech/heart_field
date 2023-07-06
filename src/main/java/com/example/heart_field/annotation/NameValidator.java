package com.example.heart_field.annotation;

import com.example.heart_field.constant.RegexPattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class NameValidator implements ConstraintValidator<Name, String> {
    @Override
    public void initialize(Name constraintAnnotation) {
        // 初始化操作
    }
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Pattern namePattern = Pattern.compile(RegexPattern.NAME_PATTERN);
        return value != null && namePattern.matcher(value).matches();
    }
}