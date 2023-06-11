package com.example.heart_field.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.dto.UserLoginDTO;
import com.example.heart_field.entity.User;
import com.example.heart_field.param.UserLoginParam;
import org.springframework.web.multipart.MultipartFile;


public interface UserService extends IService<User> {

    ResultInfo<UserLoginDTO> login(UserLoginParam loginParam);
}
