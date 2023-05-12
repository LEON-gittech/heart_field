package com.example.heart_field.filter;

import com.alibaba.fastjson.JSON;
import com.example.heart_field.common.BaseContext;
import com.example.heart_field.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        /**
         * 1.获取本次请求的URL
         * 2.判断本次请求是否需要处理
         * 3.如果不需要处理，则直接放行
         * 4.判断登录状态，如果已登录，则直接放行
         * 5.如果未登录则返回登录结果
         */
        //1.获取本次请求的URL
        String requestURI = request.getRequestURI();
        log.info("本次请求的URL为：{}",requestURI);

        //不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };

        //2.判断本次请求是否需要处理
        boolean check = check(requestURI, urls);

        //3.如果不需要处理，则直接放行
        if(check){
            log.info("本次请求无需处理，直接放行");
            filterChain.doFilter(request,response);
            return;
        }

        //4.判断登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("employeeId") != null){
            log.info("本次请求已登录，直接放行,用户ID为:{}",request.getSession().getAttribute("employeeId"));
            
            Long empId = (Long) request.getSession().getAttribute("employeeId");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }

        if(request.getSession().getAttribute("user") != null){
            log.info("本次请求已登录，直接放行,用户ID为:{}",request.getSession().getAttribute("user"));

            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        }

        //5.如果未登录则返回登录结果,通过输出流方式向客户端响应数据
        log.info("未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    public boolean check(String requestURI, String[] urls){
        for(String url : urls){
            if(PATH_MATCHER.match(url,requestURI)){
                return true;
            }
        }
        return false;
    }
}
