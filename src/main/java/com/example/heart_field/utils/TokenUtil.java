package com.example.heart_field.utils;

import com.example.heart_field.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/*
* @author MRC
* @date 2019年4月5日 下午1:14:53
* @version 1.0
*/
public class TokenUtil {

    public static User getTokenUser() {
        String token = getRequest().getHeader("token");
        Claims claims = Jwts.parser()
                .setSigningKey("my1231231231231231231231312313112312313131312321312331")
                .parseClaimsJws(token)
                .getBody();
        String userId = claims.getSubject();
        Integer type = Integer.parseInt((String) claims.get("type"));
        String password = (String) claims.get("password");
        User user = new User();
        user.setPassword(password);
        user.setType(type);
        user.setId(Integer.parseInt(userId));
        return user;
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