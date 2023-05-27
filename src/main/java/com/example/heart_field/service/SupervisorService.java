package com.example.heart_field.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.dto.UserLoginDTO;
import com.example.heart_field.entity.Supervisor;
import com.example.heart_field.param.UserLoginParam;
import org.springframework.stereotype.Service;

public interface SupervisorService extends IService<Supervisor> {
   // ResultInfo<UserLoginDTO> login(UserLoginParam loginParam);
}
