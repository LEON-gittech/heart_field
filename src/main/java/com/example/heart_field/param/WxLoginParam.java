package com.example.heart_field.param;

import lombok.Data;

/**
 * @author albac0020@gmail.com
 * data 2023/5/26 9:44 AM
 */
@Data
public class WxLoginParam {
    String iv;

    String code;

    String encryptData;

}
