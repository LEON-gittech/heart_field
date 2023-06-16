package com.example.heart_field.annotation;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.heart_field.constant.RegexPattern;
import com.example.heart_field.entity.Consultant;
import com.example.heart_field.service.ConsultantService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class IdValidator implements ConstraintValidator<Id, String> {
    @Autowired
    private ConsultantService consultantService;
    @Override
    public void initialize(Id constraintAnnotation) {
        // 初始化操作
    }
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Pattern namePattern_18 = Pattern.compile(RegexPattern.ID_PATTERN_18);
        Pattern namePattern_15 = Pattern.compile(RegexPattern.ID_PATTERN_15);
        boolean pattern_18 = namePattern_18.matcher(value).matches();
        boolean pattern_15 = namePattern_15.matcher(value).matches();
        boolean isDouble = false;
        if(consultantService.getOne(new LambdaQueryWrapper<Consultant>().eq(Consultant::getId,value))!=null){
            isDouble = true;
        }
        return value != null && !isDouble && (pattern_18||pattern_15);
    }
}