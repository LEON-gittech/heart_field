package com.example.heart_field.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author albac0020@gmail.com
 * data 2023/5/26 9:48 AM
 */
@Data
@Builder
public class WxUserInfo {
    private String openId;

    private String username;

    private String realName;

    private Integer age;

    private Byte gender;//0:女 1:男 2:未知

    private String avatar;

    private String phone;

    private String emergencyName;

    private String emergencyPhone;

}
