package com.example.heart_field.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.heart_field.constant.RegexPattern;
import com.example.heart_field.entity.*;
import com.example.heart_field.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.regex.Pattern;

@Component
@Slf4j
public class UserUtils {

    @Lazy
    @Autowired
    private ConsultantService consultantService;
    @Autowired
    @Lazy
    private SupervisorService supervisorService;
    @Autowired
    @Lazy
    private UserService userService;
    @Lazy
    @Autowired
    private AdminService adminService;
    @Lazy
    @Autowired
    private VisitorService visitorService;
    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;  //注入bcryct加密

    /**
     *用于检查用户是否具备访客端本人或管理[员]的权限
     */
    public static boolean checkSelfOrAdmin(Integer visitorId) {
        Integer type=TokenUtil.getTokenUser().getType();
        //type为0是Visitor，1是Consultant，2是Admin，3是Supervisor
        if(type==2){
            return true;
        }
        else{
            return TokenUtil.getTokenUser().getUserId().equals(visitorId);
        }
    }

    /**
     *用于检查用户是否具备访客端本人或管理[端]的权限
     */
    public static boolean checkSelfOrBack(Integer visitorId) {
        Integer type=TokenUtil.getTokenUser().getType();
        //type为0是Visitor，1是Consultant，2是Admin，3是Supervisor
        if(type!=0){
            return true;
        }
        else{
            return TokenUtil.getTokenUser().getUserId().equals(visitorId);
        }
    }

    /**
     * 保存用户信息
     * @param object
     * @return
     * @param <T>
     */
    public <T> User saveUser(T object){
        User user = new User();
        Integer id = null;
        String password = null;
        String phone =null;
        //咨询师
        if (object.getClass().equals(Consultant.class)) {
            LambdaQueryWrapper<Consultant> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Consultant::getPhone, ((Consultant) object).getPhone());
            Consultant consultant = consultantService.getOne(lambdaQueryWrapper);
            id = consultant.getId();
            phone = consultant.getPhone();
            password = bCryptPasswordEncoder.encode(consultant.getPassword()) ;
            //更新角色表中password为加密后的密码
            consultant.setPassword(password);
            consultantService.updateById(consultant);
            user.setType(1);
        }
        //督导
        else if (object.getClass().equals(Supervisor.class)) {
            LambdaQueryWrapper<Supervisor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Supervisor::getPhone, ((Supervisor) object).getPhone());
            Supervisor supervisor = supervisorService.getOne(lambdaQueryWrapper);
            id = supervisor.getId();
            phone = supervisor.getPhone();
            password = bCryptPasswordEncoder.encode(supervisor.getPassword()) ;
            //更新角色表中password为加密后的密码
            supervisor.setPassword(password);
            supervisorService.updateById(supervisor);
            user.setType(3);
        }
        //管理员
        else if (object.getClass().equals(Admin.class)) {
            LambdaQueryWrapper<Admin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Admin::getPhone, ((Admin) object).getPhone());
            Admin admin = adminService.getOne(lambdaQueryWrapper);
            id = admin.getId();
            phone = admin.getPhone();
            password = bCryptPasswordEncoder.encode(admin.getPassword()) ;
            //更新角色表中password为加密后的密码
            admin.setPassword(password);
            adminService.updateById(admin);
            user.setType(2);
        }
        //访客
        else if (object.getClass().equals(Visitor.class)){
            LambdaQueryWrapper<Visitor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Visitor::getPhone, ((Visitor) object).getPhone());
            Visitor visitor = visitorService.getOne(lambdaQueryWrapper);
            id = visitor.getId();
            phone = visitor.getPhone();
            user.setType(0);
        }
        user.setUserId(id);
        user.setPhone(phone);
        user.setPassword(password);
        userService.save(user);
        return user;
    }
    //根据传入的User信息匹配数据库中的User
    public User getUser(User user){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserId, user.getUserId());
        queryWrapper.eq(User::getType,user.getType());
        User user_r = userService.getOne(queryWrapper);
        return user_r;
    }

    /**
     * 用于修改电话号码时校验电话号码是否合法
     * @param phone
     * @return
     */
    public boolean checkPhone(String phone) {
        //手机号码格式校验
        Pattern phonePattern = Pattern.compile(RegexPattern.MOBILE_PHONE_NUMBER_PATTERN);
        if(!phonePattern.matcher(phone).matches()){
            return false;
        }
        //手机号码是否已经被注册
        LambdaQueryWrapper<Consultant> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Consultant::getPhone, phone);
        if(consultantService!=null){
            Consultant consultant = consultantService.getOne(lambdaQueryWrapper);
            if (consultant != null) {
                return false;
            }
        }

        LambdaQueryWrapper<Supervisor> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.eq(Supervisor::getPhone, phone);
        if(supervisorService!=null)
        {
            Supervisor supervisor = supervisorService.getOne(lambdaQueryWrapper1);
            if (supervisor != null) {
                return false;
            }
        }
        LambdaQueryWrapper<Admin> lambdaQueryWrapper2 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper2.eq(Admin::getPhone, phone);
        if(adminService!=null){
            Admin admin = adminService.getOne(lambdaQueryWrapper2);
            if (admin != null) {
                return false;
            }
        }
        LambdaQueryWrapper<Visitor> lambdaQueryWrapper3 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper3.eq(Visitor::getPhone, phone);
        if(visitorService!=null){
            Visitor visitor = visitorService.getOne(lambdaQueryWrapper3);
            if (visitor != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 删除User
     * @param user
     */
    public void deleteUser(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserId, user.getUserId());
        queryWrapper.eq(User::getType,user.getType());
        userService.remove(queryWrapper);
    }
}
