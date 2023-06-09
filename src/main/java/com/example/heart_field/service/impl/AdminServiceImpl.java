package com.example.heart_field.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.entity.*;
import com.example.heart_field.mapper.AdminMapper;
import com.example.heart_field.service.AdminService;
import com.example.heart_field.tokens.TokenService;
import com.example.heart_field.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserUtils userUtils;




//    @Override
//    public ResultInfo<Integer> register(AdminRegisterParam registerParam) {
//        int count = new LambdaQueryChainWrapper<>(this.baseMapper)
//                .eq(Admin::getPhone,registerParam.getPhone())
//                .count();
//        if(count!=0){
//            return ResultInfo.error("该手机号已被注册");
//        }
//        Admin admin = Admin.builder()
//                .phone(registerParam.getPhone())
//                .password(Md5Util.encryptPassword(registerParam.getPhone(), registerParam.getPassword()))
//                .username("admin")
//                .build();
//        this.save(admin);
//
//        userUtils.saveUser(admin);
//        return ResultInfo.success(admin.getId());
//    }

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