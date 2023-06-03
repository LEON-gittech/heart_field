package com.example.heart_field.utils;

import com.example.heart_field.common.R;
import com.example.heart_field.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConsultantUtil {
    @Autowired
    private AdminService adminService;
    public R<Object> isConsultantOrAdmin(Integer consultantId) {
        Integer id = TokenUtil.getTokenUser().getUserId();
        boolean isAdmin = adminService.getById(id)!=null;
        boolean isConsultantSelf = consultantId.equals(id);
        if(!isConsultantSelf&&!isAdmin){
            return R.auth_error();
        }
        return null;
    }
}
