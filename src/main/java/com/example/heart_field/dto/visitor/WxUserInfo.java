package com.example.heart_field.dto.visitor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author albac0020@gmail.com
 * data 2023/5/26 9:48 AM
 */
@Data
@Builder
@AllArgsConstructor
public class WxUserInfo {
    private String nickName;
    private String gender;//0男1女
    private String language;
    private String city;
    private String province;
    private String country;
    private String avatarUrl;
    private Watermark watermark;



    /**
     * {
     * "nickName":"樱",
     * "gender":0,
     * "language":"zh_CN",
     * "city":"",
     * "province":"",
     * "country":"",
     * "avatarUrl":"https://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83eq0icXlPdicwa6YHTQH9nQdnBuPbRdUpwayceD3nw6oBxCWIeMQF7yA9NPhk0rbmPK0CwEicHMIfHOXA/132",
     * "watermark":
     *          {
     *          "timestamp":1685069890,
     *          "appid":"wxb9f9c2c27af57638"
     *          }
     * }
     */

}

class Watermark{
    private String timestamp;
    private String appid;
}
