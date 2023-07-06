package com.example.heart_field.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.entity.Admin;

public interface AdminService extends IService<Admin> {

//    ResultInfo<UserLoginDTO> login(UserLoginParam loginParam);
//
//    ResultInfo<Integer> register(AdminRegisterParam registerParam);

    ResultInfo<Admin> disable(Integer id);

    ResultInfo<Admin> able(Integer id);

}
