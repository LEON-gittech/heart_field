package com.example.heart_field.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;

/*
* @author MRC
* @date 2019年4月5日 下午1:14:53
* @version 1.0
*/
public class TokenUtil {

    public static String getTokenUserId() {
        String token = getRequest().getHeader("token");
        Claims claims = Jwts.parser()
                .setSigningKey("my1231231231231231231231312313112312313131312321312331")
                .parseClaimsJws(token)
                .getBody();
        String userId = claims.getSubject();
        System.out.println("用户时间:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").
                format(claims.getIssuedAt()));System.out.println("过期时间:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").
                format(claims.getExpiration()));
        System.out.println("用户角色:"+claims.get("type"));
        System.out.println("用户密码:"+claims.get("password"));
        return userId;
    }

    /**
     * 获取request
     *
     * @return
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        return requestAttributes == null ? null : requestAttributes.getRequest();
    }
}