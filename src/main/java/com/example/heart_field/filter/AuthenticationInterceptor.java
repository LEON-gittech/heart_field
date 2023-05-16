package com.example.heart_field.filter;

import com.example.heart_field.entity.User;
import com.example.heart_field.service.AdminService;
import com.example.heart_field.service.UserService;
import com.example.heart_field.tokens.AdminToken;
import com.example.heart_field.tokens.PassToken;
import com.example.heart_field.tokens.UserLoginToken;
import com.example.heart_field.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class AuthenticationInterceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;
    @Autowired
    AdminService adminService;
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
        String token = httpServletRequest.getHeader("token");// 从 http 请求头中取出 token
        // 如果不是映射到方法直接通过
        if(!(object instanceof HandlerMethod)){
            return true;
        }
        HandlerMethod handlerMethod=(HandlerMethod)object;
        Method method=handlerMethod.getMethod();
        //检查是否有passtoken注释，有则跳过认证
        if (method.isAnnotationPresent(PassToken.class)) {
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (passToken.required()) {
                return true;
            }
        }
        //检查有没有需要用户权限的注解
        if (method.isAnnotationPresent(UserLoginToken.class)) {
            UserLoginToken userLoginToken = method.getAnnotation(UserLoginToken.class);
            if (userLoginToken.required()) {
                // 执行认证
                if (token == null) {
                    throw new RuntimeException("无token，请重新登录");
                }
                // 获取 token 中的 user id
                String userId = TokenUtil.getTokenUserId();
//                User user = userService.getById(userId);
                User user = new User();
                user.setId(Integer.valueOf("1"));
                user.setPassword("Aaa@1234");
                user.setType(0);
                if (user == null) {
                    throw new RuntimeException("用户不存在，请重新登录");
                }
                // 验证 token
                if(!userId.equals(user.getId().toString())){
                    throw new RuntimeException("用户不存在，请重新登录");
                }
                return true;
            }
        }

        //检查有没有需要管理员权限的注解
        if(method.isAnnotationPresent(AdminToken.class)){
            AdminToken adminToken = method.getAnnotation(AdminToken.class);
            if(adminToken.required()) {
                // 执行认证
                if (token == null) {
                    throw new RuntimeException("无token，请重新登录");
                }
                // 获取 token 中的 user id
                String userId = TokenUtil.getTokenUserId();
                if(adminService.getById(userId) == null){
                    throw new RuntimeException("非管理员，权限限制");
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}