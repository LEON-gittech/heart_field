package com.example.heart_field.param;

import com.example.heart_field.common.result.BaseResult;
import com.example.heart_field.constant.RegexPattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.regex.Pattern;

/**
 * @author albac0020@gmail.com
 * data 2023/5/17 7:15 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginParam {
    @NotNull(message = "手机号不能为空")
    private String phone;

    @NotNull(message = "密码不能为空")
    private String password;

    public BaseResult checkLoginParam(){
        Pattern phonePattern = Pattern.compile(RegexPattern.MOBILE_PHONE_NUMBER_PATTERN);
        if(!phonePattern.matcher(phone).matches()){
            return BaseResult.error("请输入正确的手机号");
        }
        Pattern passwordPattern = Pattern.compile(RegexPattern.PASSWORD_PATTERN);
        if(!passwordPattern.matcher(password).matches()){
            return BaseResult.error("密码格式不正确");
        }
        return BaseResult.SUCCESS;
    }
}
