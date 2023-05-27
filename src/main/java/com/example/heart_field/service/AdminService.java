package com.example.heart_field.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.dto.UserLoginDTO;
import com.example.heart_field.entity.Admin;
import com.example.heart_field.entity.Consultant;
import com.example.heart_field.param.AdminRegisterParam;
import com.example.heart_field.param.UserLoginParam;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

public interface AdminService extends IService<Admin> {

//    ResultInfo<UserLoginDTO> login(UserLoginParam loginParam);
//
//    ResultInfo<Integer> register(AdminRegisterParam registerParam);

    ResultInfo<Admin> disable(Integer id);

    ResultInfo<Admin> able(Integer id);

}
