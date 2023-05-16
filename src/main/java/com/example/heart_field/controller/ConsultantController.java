package com.example.heart_field.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.heart_field.common.R;
import com.example.heart_field.entity.Consultant;
import com.example.heart_field.service.ConsultantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/consultants")
public class ConsultantController {
    @Autowired
    private ConsultantService consultantService;

    /**
     * 分页查询咨询师列表
     * @param page
     * @param pageSize
     * @param searchValue 搜索条件
     * @param sortType 排序方式 0按照用户满意度 1按照帮助用户数 2表示是否空闲，默认为0
     * @param sort 升序还是降序，0表示降序，1表示升序，默认为0
     * @return
     */
    @GetMapping
    public R<Page>page(int page, int pageSize, String searchValue, int sortType, int sort){
        log.info("分类信息查询，page={},pageSize={},searchValue={}",page,pageSize,searchValue);
        //构造分页构造器
        Page<Consultant> pageinfo = new Page<>(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Consultant> queryWrapper = new LambdaQueryWrapper();

        //根据searchValue对姓名，简介，详细介绍，标签进行模糊查询
        queryWrapper.like(StringUtils.hasText(searchValue),Consultant::getName,searchValue)
                .or().like(Consultant::getBriefIntro,searchValue)
                .or().like(Consultant::getDetailedIntro,searchValue)
                .or().like(Consultant::getExpertiseTag,searchValue)
        //JSON_CONTAINS函数用于判断json数组中是否包含某个元素
        ;

        //按照sortType进行排序，并根据sort确认是升序还是降序
        if(sort == 0){
            switch (sortType){
                case 0:
                    queryWrapper.orderByDesc(Consultant::getRating);
                    break;
                case 1:
                    queryWrapper.orderByDesc(Consultant::getHelpNum);
                    break;
                case 2:
                    //这里因为空闲是0
                    queryWrapper.orderByAsc(Consultant::getCurStatus);
                    break;
                default:
                    break;
            }
        }
        else{
            switch (sortType){
                case 0:
                    queryWrapper.orderByAsc(Consultant::getRating);
                    break;
                case 1:
                    queryWrapper.orderByAsc(Consultant::getHelpNum);
                    break;
                case 2:
                    queryWrapper.orderByDesc(Consultant::getCurStatus);
                    break;
                default:
                    break;
            }
        }
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
//        consultant.setId(Integer.valueOf(consultant.getPhone()));
        consultantService.save(consultant);
        return R.success("新增咨询师成功");
    }

    /**
     * 修改咨询师
     */
    @PutMapping("/{consultantId}/profile")
    public R<Consultant> update(@PathVariable("consultantId") Integer consultantId, @RequestBody Consultant consultant){
        log.info("consultantId:{},consultant:{}",consultantId,consultant);
        consultantService.updateById(consultant);
        return R.success(consultant);
    }

}
