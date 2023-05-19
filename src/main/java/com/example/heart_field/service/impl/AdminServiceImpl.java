package com.example.heart_field.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.dto.UserLoginDTO;
import com.example.heart_field.entity.Admin;
import com.example.heart_field.entity.User;
import com.example.heart_field.mapper.AdminMapper;
import com.example.heart_field.param.AdminRegisterParam;
import com.example.heart_field.param.UserLoginParam;
import com.example.heart_field.service.AdminService;
import com.example.heart_field.tokens.TokenService;
import com.example.heart_field.utils.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
    @Autowired
    private TokenService tokenService;

    @Override
    public ResultInfo<UserLoginDTO> login(UserLoginParam loginParam) {
        LambdaQueryWrapper<Admin> queryWrapper= Wrappers.lambdaQuery();
        queryWrapper.eq(Admin::getPhone,loginParam.getPhone());
        Integer count=this.baseMapper.selectCount(queryWrapper);
        if(count==0){
            return ResultInfo.error("用户不存在");
        }
        if(count>1){
            log.error("DB中存在多条相同手机号的账号，phone = " + loginParam.getPhone());
        }
        queryWrapper.eq(Admin::getPassword, Md5Util.encryptPassword(loginParam.getPhone(), loginParam.getPassword()));
        List<Admin> pos=this.baseMapper.selectList(queryWrapper);
        if(CollectionUtils.isEmpty(pos)){
            return ResultInfo.error("密码错误，请重试！");
        }
        UserLoginDTO userLoginDTO=new UserLoginDTO();
        userLoginDTO.setId(pos.get(0).getId());
        userLoginDTO.setType(Byte.valueOf((byte) 2));

        User userForBase = new User();
        userForBase.setId(pos.get(0).getId());
        userForBase.setPassword(Md5Util.encryptPassword(loginParam.getPhone(), loginParam.getPassword()));
        userForBase.setType(2);
        String token = tokenService.getToken(userForBase);
        userLoginDTO.setToken(token);
        return ResultInfo.success(userLoginDTO);
    }

    @Override
    public ResultInfo<Integer> register(AdminRegisterParam registerParam) {
        int count = new LambdaQueryChainWrapper<>(this.baseMapper)
                .eq(Admin::getPhone,registerParam.getPhone())
                .count();
        if(count!=0){
            return ResultInfo.error("该手机号已被注册");
        }
        Admin admin = Admin.builder()
                .phone(registerParam.getPhone())
                .password(Md5Util.encryptPassword(registerParam.getPhone(), registerParam.getPassword()))
                .username("admin")
                .build();
        this.save(admin);
        return ResultInfo.success(admin.getId());
    }

    @Override
    public ResultInfo<Admin> disable(Integer id) {
        LambdaQueryWrapper<Admin> queryWrapper= Wrappers.lambdaQuery();
        queryWrapper.eq(Admin::getId,id).eq(Admin::getIsDisabled,0);
        Integer count=this.baseMapper.selectCount(queryWrapper);
        if(count!=1){
            return ResultInfo.error("用户不存在或已被封禁");
        }
        List<Admin> pos = this.baseMapper.selectList(queryWrapper);
        Admin admin = pos.get(0);
        admin.setIsDisabled((byte) 1);
        this.update(admin,queryWrapper);
        return ResultInfo.success(admin);

    }

    @Override
    public ResultInfo<Admin> able(Integer id) {
        LambdaQueryWrapper<Admin> queryWrapper= Wrappers.lambdaQuery();
        queryWrapper.eq(Admin::getId,id).eq(Admin::getIsDisabled,1);
        Integer count=this.baseMapper.selectCount(queryWrapper);
        if(count!=1){
            return ResultInfo.error("启用失败，请检查数据库中该用户相关记录");
        }
        List<Admin> pos = this.baseMapper.selectList(queryWrapper);
        Admin admin = pos.get(0);
        admin.setIsDisabled((byte) 0);
        this.update(admin,queryWrapper);
        return ResultInfo.success(admin);
    }
}