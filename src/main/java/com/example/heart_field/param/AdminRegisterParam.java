package com.example.heart_field.param;

import com.example.heart_field.common.result.BaseResult;
import com.example.heart_field.utils.CheckUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author albac0020@gmail.com
 * data 2023/5/17 7:57 PM
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AdminRegisterParam {
    private String phone;

    private String password;

    public BaseResult checkRegisterParam() {

        // phone
        BaseResult phoneCheckResult = CheckUtil.checkPhone(this.phone);
        if (!phoneCheckResult.isRight()) {
            return phoneCheckResult;
        }

        // password
        BaseResult passwordResult = CheckUtil.checkPassword(this.password);
        if (!passwordResult.isRight()) {
            return passwordResult;
        }

        return BaseResult.SUCCESS;
    }
}
