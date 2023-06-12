package com.example.heart_field.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.heart_field.common.R;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.dto.UserLoginDTO;
import com.example.heart_field.entity.*;
import com.example.heart_field.mapper.*;
import com.example.heart_field.param.UserLoginParam;
import com.example.heart_field.service.UserService;
import com.example.heart_field.tokens.TokenService;
import com.example.heart_field.utils.Md5Util;
import com.example.heart_field.utils.RandomUtil;
import com.example.heart_field.utils.TencentCloudImUtil;
import com.example.heart_field.utils.TokenUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    public Long MIN_SIZE=0L;
    public Long MAX_SIZE=1024*1024*10L;


    @Autowired
    private VisitorMapper visitorMapper;

    @Autowired
    private TencentCloudImUtil tencentCloudImUtil;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private SupervisorMapper supervisorMapper;

    @Autowired
    private ConsultantMapper consultantMapper;

    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;  //注入bcryct加密

    @Autowired
    private TokenService tokenService;

    @Override
    public ResultInfo<UserLoginDTO> login(UserLoginParam loginParam) {
        LambdaQueryWrapper<User> queryWrapper= Wrappers.lambdaQuery();
        queryWrapper.eq(User::getPhone,loginParam.getPhone());
        Integer count=this.baseMapper.selectCount(queryWrapper);
        if(count==0){
            return ResultInfo.error("用户不存在");
        }
        if(count>1){
            log.error("DB中存在多条相同手机号的账号，phone = " + loginParam.getPhone());
        }
        User user = this.baseMapper.selectOne(queryWrapper);
        log.info(user.getPhone());
        if (!bCryptPasswordEncoder.matches(loginParam.getPassword(),user.getPassword())){
            return ResultInfo.error("用户名或密码错误");
        }
        String token = tokenService.getToken(user);
        UserLoginDTO dto = UserLoginDTO.builder()
                .type(user.getType())
                .id(user.getUserId())
                .token(token)
                .build();
        return ResultInfo.success(dto);

    }
}
