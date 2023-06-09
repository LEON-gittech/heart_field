package com.example.heart_field.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.heart_field.common.R;
import com.example.heart_field.common.result.BaseResult;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.dto.UserLoginDTO;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
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
    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;  //注入bcryct加密


    @PostMapping("/backend/login")
    public R<UserLoginDTO> userLogin(@RequestBody UserLoginParam loginParam){
//        BaseResult checkResult = loginParam.checkLoginParam();
//        if(!checkResult.isRight()){
//            return R.login_error("手机号或密码格式错误");
//        }
        ResultInfo<UserLoginDTO> loginInfo = userService.login(loginParam);
        return loginInfo.isRight()
                ? R.success(loginInfo.getData())
                : R.login_error("用户不存在或密码错误");
    }

    // 登录
    @Deprecated
    @GetMapping("/login")
    public Object login(@RequestBody User user, HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject();
        User user_r = userUtils.getUser(user);
        if (!bCryptPasswordEncoder.matches(user.getPassword(),user_r.getPassword())) {
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

    @UserLoginToken
    @PostMapping("/avatar")
    public R<String> uploadAvatar(@RequestParam("avatar") MultipartFile avatar){
        try{
            ResultInfo<String> uploadInfo = userService.uploadAvatar(avatar);
            if(uploadInfo.isRight()){
                return R.success(uploadInfo.getData());
            }
            return R.error(uploadInfo.getMessage());
        }catch (Exception e){
            return R.error("上传失败，请重试");
        }

    }



}