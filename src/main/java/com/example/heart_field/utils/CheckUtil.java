package com.example.heart_field.utils;

import com.example.heart_field.common.result.BaseResult;
import com.example.heart_field.constant.RegexPattern;
import com.google.common.primitives.Chars;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author albac0020@gmail.com
 * data 2023/5/17 7:58 PM
 */

public class CheckUtil {

    public static Boolean isEmptyId(Integer id) {
        return id == null || id <= 0;
    }

    public static Boolean isNotEmptyId(Integer id) {
        return !isEmptyId(id);
    }

    public static Boolean anyEmptyIds(Integer... ids) {
        if (Objects.isNull(ids) || ids.length == 0) {
            return false;
        }
        return Arrays.stream(ids).anyMatch(CheckUtil::isEmptyId);
    }

    public static Boolean anyEmptyIds(Collection<Integer> ids) {
        if (Objects.isNull(ids) || CollectionUtils.isEmpty(ids)) {
            return false;
        }
        return ids.stream().anyMatch(CheckUtil::isEmptyId);
    }

    public static BaseResult checkPassword(String password) {
        if (StringUtils.isEmpty(password)) {
            return BaseResult.error("密码不可为空");
        }
        if (!password.matches(RegexPattern.PASSWORD_PATTERN)) {
            return BaseResult.error("密码中只能包含字母或数字，且长度应在0-100内！");
        }
        return BaseResult.SUCCESS;
    }

    public static BaseResult checkPhone(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return BaseResult.error("手机号不可为空");
        }
        if (!Pattern.compile(RegexPattern.MOBILE_PHONE_NUMBER_PATTERN).matcher(phone).matches()) {
            return BaseResult.error("请输入正确的手机号");
        }
        return BaseResult.SUCCESS;
    }

    public static BaseResult checkName(String name) {
        if (StringUtils.isEmpty(name)) {
            return BaseResult.error("姓名不可为空");
        }
        if (name.length() < RegexPattern.NAME_MIN_LENGTH || name.length() > RegexPattern.NAME_MAX_LENGTH) {
            return BaseResult.error(String.format("姓名长度应在%d-%d个字符", RegexPattern.NAME_MIN_LENGTH, RegexPattern.NAME_MAX_LENGTH));
        }
        for (Character c : Chars.asList(RegexPattern.LIMIT_CHARS)) {
            if (name.contains(c.toString())) {
                return BaseResult.error("名字中不能包含特殊字符: " + c);
            }
        }
        return BaseResult.SUCCESS;
    }

}

