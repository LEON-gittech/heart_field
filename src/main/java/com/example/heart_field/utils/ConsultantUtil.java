package com.example.heart_field.utils;

import com.example.heart_field.common.CustomException;
import com.example.heart_field.common.R;
import com.example.heart_field.entity.Consultant;
import com.example.heart_field.service.AdminService;
import com.example.heart_field.service.ConsultantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConsultantUtil {
    @Autowired
    private AdminService adminService;
    @Autowired
    private ConsultantService consultantService;
    public R<Object> isConsultantOrAdmin(Integer consultantId) {
        //判断是否有该咨询师
        Consultant consultant = consultantService.getById(consultantId);
        if(consultant==null) return R.resource_error();
        Integer type = TokenUtil.getTokenUser().getType();
        Integer id = TokenUtil.getTokenUser().getUserId();
        boolean isAdmin = type==2;
        boolean isConsultantSelf = consultantId.equals(id);
        if(!isConsultantSelf&&!isAdmin){
            return R.auth_error();
        }
        return null;
    }

    public  void isExist(Integer consultantId){
        Consultant consultant = consultantService.getById(consultantId);
        if(consultant==null) throw new CustomException("无该咨询师");
    }
}
