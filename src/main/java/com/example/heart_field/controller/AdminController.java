package com.example.heart_field.controller;

import com.example.heart_field.common.R;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.entity.Admin;
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
