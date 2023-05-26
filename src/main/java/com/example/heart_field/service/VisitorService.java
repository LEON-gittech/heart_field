package com.example.heart_field.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.heart_field.common.R;
import com.example.heart_field.entity.Visitor;
import com.example.heart_field.param.WxLoginParam;

public interface VisitorService extends IService<Visitor> {

    R authLogin(WxLoginParam loginParam);
}
