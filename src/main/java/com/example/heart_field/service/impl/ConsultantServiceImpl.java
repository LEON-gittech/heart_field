package com.example.heart_field.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.dto.UserLoginDTO;
import com.example.heart_field.entity.Admin;
import com.example.heart_field.entity.Consultant;
import com.example.heart_field.entity.User;
import com.example.heart_field.mapper.ConsultantMapper;
import com.example.heart_field.param.UserLoginParam;
import com.example.heart_field.service.ConsultantService;
import com.example.heart_field.tokens.TokenService;
import com.example.heart_field.utils.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultantServiceImpl extends ServiceImpl<ConsultantMapper, Consultant> implements ConsultantService {
    @Autowired
    private TokenService tokenService;

    @Override
    public ResultInfo<UserLoginDTO> login(UserLoginParam loginParam) {
        LambdaQueryWrapper<Consultant> queryWrapper= Wrappers.lambdaQuery();
        queryWrapper.eq(Consultant::getPhone,loginParam.getPhone());
        Integer count=this.baseMapper.selectCount(queryWrapper);
        if(count==0){
            return ResultInfo.error("用户不存在");
        }
        if(count>1){
            log.error("DB中存在多条相同手机号的账号，phone = " + loginParam.getPhone());
        }
        queryWrapper.eq(Consultant::getPassword, Md5Util.encryptPassword(loginParam.getPhone(), loginParam.getPassword()));
        List<Consultant> pos=this.baseMapper.selectList(queryWrapper);
        if(CollectionUtils.isEmpty(pos)){
            return ResultInfo.error("密码错误，请重试！");
        }
        UserLoginDTO userLoginDTO=new UserLoginDTO();
        userLoginDTO.setId(pos.get(0).getId());
        userLoginDTO.setType(Byte.valueOf((byte) 1));

        User userForBase = new User();
        userForBase.setId(pos.get(0).getId());
        userForBase.setPassword(Md5Util.encryptPassword(loginParam.getPhone(), loginParam.getPassword()));
        userForBase.setType(1);
        String token = tokenService.getToken(userForBase);
        userLoginDTO.setToken(token);
        return ResultInfo.success(userLoginDTO);
    }
}
