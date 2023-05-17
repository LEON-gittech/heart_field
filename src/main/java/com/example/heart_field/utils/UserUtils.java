package com.example.heart_field.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.heart_field.entity.*;
import com.example.heart_field.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserUtils {
    @Autowired
    private ConsultantService consultantService;
    @Autowired
    private SupervisorService supervisorService;
    @Autowired
    private UserService userService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private VisitorService visitorService;

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
        //咨询师
        if (object.getClass().equals(Consultant.class)) {
            LambdaQueryWrapper<Consultant> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Consultant::getPhone, ((Consultant) object).getPhone());
            Consultant consultant = consultantService.getOne(lambdaQueryWrapper);
            id = consultant.getId();
            password = consultant.getPassword();
            user.setType(1);
        }
        //督导
        else if (object.getClass().equals(Supervisor.class)) {
            LambdaQueryWrapper<Supervisor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Supervisor::getPhone, ((Supervisor) object).getPhone());
            Supervisor supervisor = supervisorService.getOne(lambdaQueryWrapper);
            id = supervisor.getId();
            password = supervisor.getPassword();
            user.setType(3);
        }
        //管理员
        else if (object.getClass().equals(Admin.class)) {
            LambdaQueryWrapper<Admin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Admin::getPhone, ((Admin) object).getPhone());
            Admin admin = adminService.getOne(lambdaQueryWrapper);
            id = admin.getId();
            password = admin.getPassword();
            user.setType(2);
        }
        //访客
        else if (object.getClass().equals(Visitor.class)){
            LambdaQueryWrapper<Visitor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Visitor::getPhone, ((Visitor) object).getPhone());
            Visitor visitor = visitorService.getOne(lambdaQueryWrapper);
            id = visitor.getId();
            user.setType(0);
        }
        user.setId(id);
        user.setPassword(password);
        userService.save(user);
        return user;
    }
    //根据传入的User信息匹配数据库中的User
    public User getUser(User user){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, user.getId());
        queryWrapper.eq(User::getType,user.getType());
        User user_r = userService.getOne(queryWrapper);
        return user_r;
    }
}
