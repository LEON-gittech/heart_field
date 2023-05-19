package com.example.heart_field.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.heart_field.common.R;
import com.example.heart_field.dto.*;
import com.example.heart_field.entity.Binding;
import com.example.heart_field.entity.Consultant;
import com.example.heart_field.mapper.AdminMapper;
import com.example.heart_field.service.*;
import com.example.heart_field.tokens.AdminToken;
import com.example.heart_field.tokens.UserLoginToken;
import com.example.heart_field.utils.TokenUtil;
import com.example.heart_field.utils.UserUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@UserLoginToken
@RequestMapping("/consultants")
public class ConsultantController {
    @Autowired
    private ConsultantService consultantService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private RecordService recordService;
    @Autowired
    private UserService userService;
    @Autowired
    private VisitorService visitorService;
    @Autowired
    private BindingService bindingService;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private SupervisorService supervisorService;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private AdminMapper adminMapper;
    // 创建一个ObjectMapper实例
    ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 分页查询咨询师列表升序还是降序，0表示降序，1表示升序，默认为0
     */
    @GetMapping
    public R<List<ConsultantDto>>page(HttpServletRequest httpServletRequest) throws JsonProcessingException {
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
        LambdaQueryWrapper<Consultant> queryWrapper = new LambdaQueryWrapper<>();

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
        ConsultantDto consultantDto = new ConsultantDto();
        List<Consultant> consultants = pageinfo.getRecords();
        List<ConsultantDto> consultantDtos = new ArrayList<>();
        //对consultans进行批处理
        for(Consultant consultant:consultants){
            //将expertiseTag转换为List<ExpertiseTag>
            List<ExpertiseTag> expertiseTags = objectMapper.readValue(consultant.getExpertiseTag(), new TypeReference<List<ExpertiseTag>>() {});
            consultantDto.setExpertiseTag(expertiseTags);
            //id
            consultantDto.setId(String.valueOf(consultant.getId()));
            //briefIntroduction
            consultantDto.setBriefIntroduction(consultant.getBriefIntro());
            //consultantAvatar
            consultantDto.setConsultantAvatar(consultant.getAvatar());
            //consultantName
            consultantDto.setConsultantName(consultant.getName());
            //consultantState
            consultantDto.setConsultState(consultant.getCurStatus());
            //helpCount
            consultantDto.setHelpCount(consultant.getHelpNum());
            //consultantTotalCount，查询每个访客对应的record数量
            consultantDto.setConsultTotalCount(consultant.getHelpTotalNum());
            //supervisorBindings,获取该咨询师绑定的所有督导
            List<SupervisorBinding> supervisorBindings = consultantService.getSupervisorBindings(consultant);
            consultantDto.setSupervisorBindings(supervisorBindings);
            //workArrangement，获取咨询师对应的所有排班
            List<Integer> workArrangement = consultantService.getWorkArrangement(consultant);
            consultantDto.setWorkArrangement(workArrangement);
            //consultTotalTime
            consultantDto.setConsultTotalTime(consultant.getTotalHelpTime());
            //averageRank
            consultantDto.setAverageRank((int) Math.floor(consultant.getRating()));
            consultantDtos.add(consultantDto);
        }

        return R.success(consultantDtos);
    }

    /**
     * 新增咨询师
     */
    @AdminToken
    @PostMapping
    public R<String> save(@RequestBody Consultant consultant,HttpServletRequest httpServletRequest){
        log.info("consultant:{}",consultant);
        consultantService.save(consultant);
        //同步添加到User类,从Consultant表中获取id
        userUtils.saveUser(consultant);
        return R.success("新增咨询师成功");
    }

    /**
     * 修改咨询师的信息
     * 使用ConsultantDto作为中转实体类，因为Consultant中的expertiseTag是json数组，需要转换
     */
    @PutMapping("/{consultantId}/profile")
    public R<ConsultantDto> update(@PathVariable("consultantId") Integer consultantId, @RequestBody ConsultantDto consultantDto) throws JsonProcessingException {
        log.info("consultantId:{},consultant:{}",consultantId,consultantDto);
        //权限验证
        Integer id = TokenUtil.getTokenUser().getId();
        if(!consultantId.equals(id)&&adminService.getById(consultantId)==null){
            return R.auth_error();
        }
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
    public R<ConsultantProfileDto> getOwnProfile() throws JsonProcessingException {
        Integer id = TokenUtil.getTokenUser().getId();
        Consultant consultant = consultantService.getById(id);
        ConsultantProfileDto consultantProfileDto = new ConsultantProfileDto();
        BeanUtils.copyProperties(consultant,consultantProfileDto,"expertiseTag");
        //id
        consultantProfileDto.setId(consultant.getId().toString());
        //maxCount
        consultantProfileDto.setMaxCount(consultant.getMaxNum());
        //maxConcurrentCount
        consultantProfileDto.setMaxConcurrentCount(consultant.getMaxConcurrent());
        //todayTotalTime
        consultantProfileDto.setTodayTotalTime(consultant.getTodayTotalHelpTime());
        //todayTotalCount
        consultantProfileDto.setTodayTotalCount(consultant.getTodayTotalHelpCount());
        return R.success(consultantProfileDto);
    }

    /**
     * 根据咨询师id查询咨询师的详细信息
     */
    @GetMapping("/{consultantId}/profile")
    public R<AnyConsultantProfileDto> getProfile(@PathVariable("consultantId") Integer consultantId) throws JsonProcessingException {
        //权限验证
        Integer id = TokenUtil.getTokenUser().getId();
        if(!consultantId.equals(id)&&adminService.getById(id)==null){
            return R.auth_error();
        }
        //返回数据
        AnyConsultantProfileDto anyConsultantProfileDto = new AnyConsultantProfileDto();
        Consultant consultant = consultantService.getById(consultantId);
        anyConsultantProfileDto.setConsultantName(consultant.getName());
        anyConsultantProfileDto.setAvatar(consultant.getAvatar());
        anyConsultantProfileDto.setBriefIntroduction(consultant.getBriefIntro());
        anyConsultantProfileDto.setDetailIntroduction(consultant.getDetailedIntro());
        anyConsultantProfileDto.setState(consultant.getCurStatus());
        //workArrangement
        List<Integer> workArrangement = consultantService.getWorkArrangement(consultant);
        anyConsultantProfileDto.setWorkArrangement(workArrangement);
        //supervisorBindings
        anyConsultantProfileDto.setSupervisorBind(consultantService.getSupervisorBindings(consultant));
        //helpCount
        anyConsultantProfileDto.setHelpCount(consultant.getTodayTotalHelpCount());
        //consultTotalCount
        anyConsultantProfileDto.setConsultTotalCount(consultant.getHelpTotalNum());
        //consultTotalTime
        anyConsultantProfileDto.setConsultTotalTime(String.valueOf(consultant.getTotalHelpTime()));
        //averageRank
        anyConsultantProfileDto.setAverageRank(consultant.getRating());
        //comments
        List<CommentDto> list = consultantService.getCommentDto(consultantId,1,10);
            //将comments转为Comment格式
        List<AnyConsultantProfileDto.Comment> comments = new ArrayList<>();
        for(CommentDto commentDto:list){
            AnyConsultantProfileDto.Comment comment = new AnyConsultantProfileDto.Comment();
            comment.setComment(commentDto.getVisitorComment());
            comment.setUser(commentDto.getUserName());
            comment.setRange(commentDto.getVisitorScore());
            comments.add(comment);
        }
        anyConsultantProfileDto.setComments(comments);
        return R.success(anyConsultantProfileDto);
    }

    /**
     * 获取咨询师的评价列表
     * @param consultantId
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/{consultantId}/comments")
    public R<Page> getComments(@PathVariable("consultantId") Integer consultantId, HttpServletRequest httpServletRequest){
        //权限验证
        Integer id = TokenUtil.getTokenUser().getId();
        if(!consultantId.equals(id)&&adminService.getById(consultantId)==null){
            return R.auth_error();
        }
        //分页查询
        int page = Integer.parseInt(httpServletRequest.getParameter("page"));
        int pageSize = Integer.parseInt(httpServletRequest.getParameter("pageSize"));
        List<CommentDto> list = consultantService.getCommentDto(consultantId,page,pageSize);
        Page<CommentDto> commentDtoPage = new Page<>();
        commentDtoPage.setRecords(list);
        return R.success(commentDtoPage);
    }

    /**
     * 更新咨询师的绑定列表
     * @param consultantId
     */
    @AdminToken
    @PutMapping("/{consultantId}/bindings")
    public R<SupervisorBindedDto> updateBindings(@PathVariable("consultantId") Integer consultantId,@RequestBody SupervisorBindedDto supervisorBindedDto){
        //获取指定咨询师的所有绑定记录
        List<Integer> supervisorBinded = supervisorBindedDto.getSupervisorBinded();
        LambdaQueryWrapper<Binding> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Binding::getConsultantId,consultantId);
        List<Binding> bindings = bindingService.list(queryWrapper);
        //删除解绑的督导
        for(Binding binding : bindings){
            if(!supervisorBinded.contains(binding.getSupervisorId())){
                bindingService.removeById(binding.getId());
            }
        }
        //加入新绑定的
        for(Integer supervisorId: supervisorBinded){
            if(!bindings.stream().map(Binding::getSupervisorId).collect(Collectors.toList()).contains(supervisorId)){
                Binding binding = new Binding();
                binding.setConsultantId(consultantId);
                binding.setSupervisorId(supervisorId);
                bindingService.save(binding);
            }
        }
        return R.success(supervisorBindedDto);
    }

    /**
     * 更新咨询师的权限
     * @param consultantId
     * @return
     */
    @AdminToken
    @PutMapping("/{consultantId}/permission")
    public R<String> updatePermission(@PathVariable("consultantId") Integer consultantId){
        //更新权限
        Consultant consultant = consultantService.getById(consultantId);
            //权限取反
        consultant.setDisabled(!consultant.isDisabled());
        consultantService.updateById(consultant);
        return R.success("更新咨询师权限成功");
    }
}
