package com.example.heart_field.controller;

import com.example.heart_field.common.R;
import com.example.heart_field.common.result.BaseResult;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.entity.Admin;
import com.example.heart_field.entity.Consultant;
import com.example.heart_field.param.AdminRegisterParam;
import com.example.heart_field.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author albac0020@gmail.com
 * data 2023/5/17 7:55 PM
 */
@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

//    /**
//     * 新增超管
//     * 返回超管id
//     * 仅用作后端测试
//     */
//    @PostMapping("/register")
//    public R<Integer> register(@RequestBody AdminRegisterParam registerParam){
//        BaseResult checkResult = registerParam.checkRegisterParam();
//        if(!checkResult.isRight()){
//            return R.argument_error();
//        }
//        ResultInfo<Integer> registerInfo = adminService.register(registerParam);
//        if(registerInfo.isRight()){
//            return R.success(registerInfo.getData());
//        }
//        return R.error(registerInfo.getMessage());
//    }

    /**
     * 管理员禁用，后台测试
     * @param id
     * @return
     */
    @PostMapping("disable/{id}")
    public R<Admin> disable(@PathVariable(name="id")Integer id){
        ResultInfo<Admin> resultInfo = adminService.disable(id);
        if(resultInfo.isRight()){
            return R.success(resultInfo.getData());
        }
        return R.error(resultInfo.getMessage());
    }

    /**
     * 管理员启用，后台测试
     * @param id
     * @return
     */
    @PostMapping("able/{id}")
    public R<Admin> able(@PathVariable(name="id")Integer id){
        ResultInfo<Admin> resultInfo = adminService.able(id);
        if(resultInfo.isRight()){
            return R.success(resultInfo.getData());
        }
        return R.error(resultInfo.getMessage());
    }
}
