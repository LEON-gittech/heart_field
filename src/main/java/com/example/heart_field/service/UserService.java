package com.example.heart_field.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.dto.user.UserLoginDTO;
import com.example.heart_field.entity.User;
import com.example.heart_field.param.UserLoginParam;


public interface UserService extends IService<User> {

    ResultInfo<UserLoginDTO> login(UserLoginParam loginParam);
}
