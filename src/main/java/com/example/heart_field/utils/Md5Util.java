package com.example.heart_field.utils;

/**
 * @author albac0020@gmail.com
 * data 2023/5/17 7:30 PM
 */
import org.apache.logging.log4j.util.Strings;
import org.apache.shiro.crypto.hash.Md5Hash;

public class Md5Util {

    /**
     * MD5简易加密
     *
     * @param account 登陆唯一标识
     * @param password 密码
     * @return
     */
    public static String encryptPassword(String account, String password) {
        return encryptPasswordPlus(account, password, Strings.EMPTY);
    }

    /**
     * MD5密码加密；加盐
     *
     * @param account
     * @param password
     * @param salt
     * @return
     */
    public static String encryptPasswordPlus(String account, String password, String salt) {
        return new Md5Hash(account + password + salt).toHex();
    }
}
