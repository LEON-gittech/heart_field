package com.example.heart_field.annotation;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.heart_field.constant.RegexPattern;
import com.example.heart_field.entity.Consultant;
import com.example.heart_field.service.ConsultantService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PhoneValidator implements ConstraintValidator<Phone, String> {
    @Autowired
    private ConsultantService consultantService;
    @Override
    public void initialize(Phone constraintAnnotation) {
        // 初始化操作
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 执行电话号码的验证逻辑
        // 这里可以根据需要实现自定义的电话号码验证规则
        // 返回 true 表示验证通过，返回 false 表示验证失败
        Pattern phonePattern = Pattern.compile(RegexPattern.MOBILE_PHONE_NUMBER_PATTERN);
        //判重
        boolean isDouble = false;
        if(consultantService.getOne(new LambdaQueryWrapper<Consultant>().eq(Consultant::getPhone,value))!=null){
            isDouble = true;
        }
        return value != null && !isDouble && phonePattern.matcher(value).matches();
    }
}

