package com.example.heart_field.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.heart_field.common.R;
import com.example.heart_field.dto.ConsultantDto;
import com.example.heart_field.entity.Consultant;
import com.example.heart_field.entity.ExpertiseTag;
import com.example.heart_field.service.AdminService;
import com.example.heart_field.service.ConsultantService;
import com.example.heart_field.utils.TokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/consultants")
public class ConsultantController {
    @Autowired
    private ConsultantService consultantService;

    @Autowired
    private AdminService adminService;
    // 创建一个ObjectMapper实例
    ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 分页查询咨询师列表升序还是降序，0表示降序，1表示升序，默认为0
     * @return
     */
    @GetMapping
    public R<Page>page(HttpServletRequest httpServletRequest){
        httpServletRequest.getParameterMap().forEach((k,v)->{
            log.info("key={},value={}",k,v);
        });
        int page = Integer.parseInt(httpServletRequest.getParameter("page"));
        int pageSize = Integer.parseInt(httpServletRequest.getParameter("pageSize"));
        String searchValue = httpServletRequest.getParameter("searchValue");
        int sortType = Integer.parseInt(httpServletRequest.getParameter("sortType"));
        int sort = Integer.parseInt(httpServletRequest.getParameter("sort"));
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
        consultantService.save(consultant);
        return R.success("新增咨询师成功");
    }

    /**
     * 修改咨询师的信息
     * 使用ConsultantDto作为中转实体类，因为Consultant中的expertiseTag是json数组，需要转换
     */
    @PutMapping("/{consultantId}/profile")
    public R<ConsultantDto> update(@PathVariable("consultantId") Integer consultantId, @RequestBody ConsultantDto consultantDto) throws JsonProcessingException {
        log.info("consultantId:{},consultant:{}",consultantId,consultantDto);
        //将consultantDto的值复制给consultant
        Consultant consultant = consultantService.getById(consultantDto.getId());
        BeanUtils.copyProperties(consultantDto,consultant,"expertiseTag");

        consultant.setExpertiseTag(objectMapper.writeValueAsString(consultantDto.getExpertiseTag()));

        consultantService.updateById(consultant);
        //回返
        BeanUtils.copyProperties(consultant,consultantDto,"expertiseTag");
        consultantDto.setExpertiseTag(objectMapper.readValue(consultant.getExpertiseTag(),new TypeReference<List<ExpertiseTag>>() {}));
        return R.success(consultantDto);
    }

    /**
     * 根据咨询师id查询咨询师的详细信息
     * 从token中获取id并根据id进行查询
     */
    @GetMapping("/profile")
    public R<ConsultantDto> getOwnProfile() throws JsonProcessingException {
        Integer id = Integer.valueOf(TokenUtil.getTokenUserId());
        Consultant consultant = consultantService.getById(id);
        ConsultantDto consultantDto = new ConsultantDto();
        BeanUtils.copyProperties(consultant,consultantDto,"expertiseTag");
        consultantDto.setExpertiseTag(objectMapper.readValue(consultant.getExpertiseTag(),new TypeReference<List<ExpertiseTag>>() {}));
        return R.success(consultantDto);
    }

    /**
     * 根据咨询师id查询咨询师的详细信息
     */
    @GetMapping("/{consultantId}/profile")
    public R<ConsultantDto> getProfile(@PathVariable("consultantId") Integer consultantId) throws JsonProcessingException {
        //判断权限是否可以访问
        Integer id = Integer.valueOf(TokenUtil.getTokenUserId());
        if(id.equals(consultantId) || adminService.getById(id) != null) {
            Consultant consultant = consultantService.getById(consultantId);
            ConsultantDto consultantDto = new ConsultantDto();
            BeanUtils.copyProperties(consultant,consultantDto,"expertiseTag");
            consultantDto.setExpertiseTag(objectMapper.readValue(consultant.getExpertiseTag(),new TypeReference<List<ExpertiseTag>>() {}));
            return R.success(consultantDto);
        }
        else{
            return R.auth_error();
        }
    }


}
