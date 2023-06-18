package com.example.heart_field.annotation;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.heart_field.constant.RegexPattern;
import com.example.heart_field.entity.Consultant;
import com.example.heart_field.entity.Supervisor;
import com.example.heart_field.service.ConsultantService;
import com.example.heart_field.service.SupervisorService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.regex.Pattern;

public class IdValidator implements ConstraintValidator<Id, String> {
    @Autowired
    private ConsultantService consultantService;
    @Autowired
    private SupervisorService supervisorService;
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
        // 假设 cardid 是一个变量

        // 创建查询包装器
        LambdaQueryWrapper<Consultant> consultantQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Supervisor> supervisorQueryWrapper = new LambdaQueryWrapper<>();

        // 构建查询条件
        consultantQueryWrapper.eq(StringUtils.isNotEmpty(value), Consultant::getCardId, value);
        supervisorQueryWrapper.eq(StringUtils.isNotEmpty(value), Supervisor::getCardId, value);

        // 使用 LambdaQueryChainWrapper 进行联合查询
        boolean isCardIdPresent = false;
        List<Consultant> consultants = consultantService.list(consultantQueryWrapper);
        List<Supervisor> supervisors = supervisorService.list(supervisorQueryWrapper);
        isCardIdPresent = consultants.size()!=0 || supervisors.size()!=0;
        if(isCardIdPresent){
            // 清除默认错误消息
            context.disableDefaultConstraintViolation();
            // 设置自定义错误消息
            context.buildConstraintViolationWithTemplate(" 身份证已存在").addConstraintViolation();
        }
        return value != null && !isCardIdPresent && (pattern_18||pattern_15);
    }
}