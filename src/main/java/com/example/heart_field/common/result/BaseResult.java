package com.example.heart_field.common.result;

import lombok.AllArgsConstructor;
import lombok.AllArgsConstructor;
/**
 * @author albac0020@gmail.com
 * data 2023/5/17 7:20 PM
 */

@AllArgsConstructor
public class BaseResult {

    public static final BaseResult SUCCESS =  new BaseResult(true, "SUCCESS");

    private final Boolean isRight;

    private final String message;

    public Boolean isRight() {
        return this.isRight;
    }

    public String getMessage() {
        return this.message;
    }

    public static BaseResult error(String message) {
        return new BaseResult(false, message);
    }
}
