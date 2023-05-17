package com.example.heart_field.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.heart_field.common.R;
import com.example.heart_field.common.result.BaseResult;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.dto.UserLoginDTO;
import com.example.heart_field.entity.Supervisor;
import com.example.heart_field.entity.User;
import com.example.heart_field.param.UserLoginParam;
import com.example.heart_field.service.AdminService;
import com.example.heart_field.service.ConsultantService;
import com.example.heart_field.service.SupervisorService;
import com.example.heart_field.service.UserService;
import com.example.heart_field.tokens.TokenService;
import com.example.heart_field.tokens.UserLoginToken;
import com.example.heart_field.utils.TokenUtil;
import com.example.heart_field.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
public class UserApi {
    @Autowired
    UserService userService;
    @Autowired
    TokenService tokenService;
    @Autowired
    UserUtils userUtils;

    @Autowired
    AdminService adminService;

    @Autowired
    SupervisorService supervisorService;

    @Autowired
    ConsultantService consultantService;

    @PostMapping("/admin/login")
    public R<UserLoginDTO> userLogin(@RequestBody UserLoginParam loginParam){
        BaseResult checkResult = loginParam.checkLoginParam();
        if(!checkResult.isRight()){
            return R.login_error();
        }
        ResultInfo<UserLoginDTO> adminLoginResult = adminService.login(loginParam);
        if(adminLoginResult.isRight()){
            return R.success(adminLoginResult.getData());
        }
        ResultInfo<UserLoginDTO> supervisorLoginResult = supervisorService.login(loginParam);
        if(supervisorLoginResult.isRight()){
            return R.success(supervisorLoginResult.getData());
        }
        ResultInfo<UserLoginDTO> consultantLoginResult = consultantService.login(loginParam);
        if(consultantLoginResult.isRight()){
            return R.success(consultantLoginResult.getData());
        }
        return R.login_error("用户不存在或密码错误");
    }

    // 登录
    @GetMapping("/login")
    public Object login(@RequestBody User user, HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject();
        User user_r = userUtils.getUser(user);

        if (!user_r.getPassword().equals(user.getPassword())) {
            jsonObject.put("message", "登录失败,密码错误");
            return jsonObject;
        } else {
            String token = tokenService.getToken(user_r);
            jsonObject.put("token", token);

            Cookie cookie = new Cookie("token", token);
            cookie.setPath("/");
            response.addCookie(cookie);

            return jsonObject;
        }
    }

    /***
     * 这个请求需要验证token才能访问
     * 
     * @author: MRC
     * @date 2019年5月27日 下午5:45:19
     * @return String 返回类型
     */
    @UserLoginToken
    @GetMapping("/getMessage")
    public String getMessage() {

        // 取出token中带的用户id 进行操作
        System.out.println(TokenUtil.getTokenUser());

        return "你已通过验证";
    }
}