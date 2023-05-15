package com.example.heart_field.controller;

import com.example.heart_field.entity.Consultant;
import com.example.heart_field.service.ConsultantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController("/consultants")
public class ConsultantController {
    @Autowired
    private ConsultantService consultantService;

    /**
     * 分页查询咨询师列表
     * @param page
     * @param pageSize
     * @param searchValue 搜索条件
     * @return
     */
    @GetMapping
    public R<Page>page(int page, int pageSize, String searchValue){
        log.info("分类信息查询，page={},pageSize={},searchValue={}",page,pageSize,searchValue);
        //构造分页构造器
        Page<Consultant> pageinfo = new Page<>(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Consultant> queryWrapper = new LambdaQueryWrapper();

        //根据searchValue对姓名，简介，详细介绍，标签进行模糊查询
        queryWrapper.like(StringUtils.hasText(searchValue),Consultant::getName,searchValue)
                .or().like(Consultant::getBriefIntro,searchValue)
                .or().like(Consultant::getDetailedIntro,searchValue)
                .or().nested(i -> i.apply
                        ("JSON_CONTAINS(expertiseTag->'$[*].expertiseName'," +
                                "concat('%',?,'%'))",searchValue))
        //JSON_CONTAINS函数用于判断json数组中是否包含某个元素
        ;
        //执行查询
        consultantService.page(pageinfo,queryWrapper);

        return R.success(pageinfo);
    }

    /**
     * 新增咨询师
     */
    @PostMapping
    public R<String> save(@RequestBody Consultant consultant){
        log.info("consultant:{}",consultant);
        consultantService.save(consultant);
        return R.success("新增咨询师成功");
    }
    /**
     *
     */
    @GetMapping("/preview")
    public
}
