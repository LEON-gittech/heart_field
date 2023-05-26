package com.example.heart_field.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author albac0020@gmail.com
 * data 2023/5/26 9:46 AM
 */
@Data
@Builder
public class WxLoginDTO {
    private Integer userId;

    private String accessToken;

    private Boolean fstLogin;

    private String chatUserId;

    private String chatUserSig;

    private WxUserInfo userInfo;


}
