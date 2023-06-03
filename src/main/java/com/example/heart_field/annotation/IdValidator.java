package com.example.heart_field.annotation;

import com.example.heart_field.constant.RegexPattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class IdValidator implements ConstraintValidator<Id, String> {
    @Override
    public void initialize(Id constraintAnnotation) {
        // 初始化操作
    }
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Pattern namePattern = Pattern.compile(RegexPattern.ID_PATTERN);
        return value != null && namePattern.matcher(value).matches();
    }
}