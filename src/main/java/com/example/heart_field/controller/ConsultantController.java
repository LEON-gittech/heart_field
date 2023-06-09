package com.example.heart_field.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.heart_field.common.R;
import com.example.heart_field.dto.consultant.ConsultantDto;
import com.example.heart_field.dto.consultant.ConsultantsDto;
import com.example.heart_field.dto.consultant.ExpertiseTag;
import com.example.heart_field.dto.consultant.PasswordDto;
import com.example.heart_field.dto.consultant.binding.OnlineBinding;
import com.example.heart_field.dto.consultant.binding.SupervisorBindedDto;
import com.example.heart_field.dto.consultant.comment.CommentDto;
import com.example.heart_field.dto.consultant.comment.CommentsDto;
import com.example.heart_field.dto.consultant.profile.AnyConsultantProfileDto;
import com.example.heart_field.dto.consultant.profile.ConsultantProfileDto;
import com.example.heart_field.dto.consultant.profile.UpdateConsultantProfileDto;
import com.example.heart_field.entity.*;
import com.example.heart_field.service.*;
import com.example.heart_field.tokens.AdminToken;
import com.example.heart_field.tokens.UserLoginToken;
import com.example.heart_field.utils.ConsultantUtil;
import com.example.heart_field.utils.TencentCloudImUtil;
import com.example.heart_field.utils.TokenUtil;
import com.example.heart_field.utils.UserUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
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
    private TencentCloudImUtil tencentCloudImUtil;
    @Autowired
    private ConsultantUtil consultantUtil;
    // 创建一个ObjectMapper实例
    ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 分页查询咨询师列表升序还是降序，0表示降序，1表示升序，默认为0
     */
    @GetMapping
    public R<ConsultantsDto>page(HttpServletRequest httpServletRequest) throws JsonProcessingException {
        httpServletRequest.getParameterMap().forEach((k,v)->{
            log.info("key={},value={}",k,v);
        });
        int page = Integer.parseInt(httpServletRequest.getParameter("page"));
        int pageSize = Integer.parseInt(httpServletRequest.getParameter("pageSize"));
        String searchValue = httpServletRequest.getParameter("searchValue") ;
        int sortType = Integer.parseInt(httpServletRequest.getParameter("sortType"));
        int sort = Integer.parseInt(httpServletRequest.getParameter("sort"));
        log.info("分类信息查询，page={},pageSize={},searchValue={}",page,pageSize,searchValue);
        log.info("sort={},sortType={}",sort,sortType);
        //构造分页构造器
        Page<Consultant> pageinfo = new Page<>(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Consultant> queryWrapper = new LambdaQueryWrapper<>();

        //根据searchValue对姓名，简介，详细介绍，标签进行模糊查询
        if(!(searchValue.equals(null)||searchValue.equals(""))){
            queryWrapper.like(Consultant::getName,searchValue)
                    .or().like(Consultant::getBriefIntro,searchValue)
                    .or().like(Consultant::getDetailedIntro,searchValue)
                    .or().like(Consultant::getExpertiseTag,searchValue);
        }
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
        //筛选在线的咨询师
        queryWrapper.eq(Consultant::getIsOnline,1);
        //筛选今天有绑定督导的咨询师
        List<Binding> bindings = bindingService.list();
        Set<Integer> hasBindingConsultants = new HashSet<>();
        for(Binding binding : bindings){
            hasBindingConsultants.add(binding.getConsultantId());
        }
        //执行查询
        consultantService.page(pageinfo,queryWrapper);
        ConsultantsDto consultantsDto = new ConsultantsDto();
        List<Consultant> consultants = pageinfo.getRecords();
        //筛选今天有绑定督导的咨询师
        consultants.removeIf(consultant -> !hasBindingConsultants.contains(consultant.getId()));
        //DTO 转换
        List<ConsultantDto> consultantDtos = new ArrayList<>();
        Integer pageNum = Math.toIntExact(pageinfo.getPages());
        consultantsDto.setPageNum(pageNum);
        consultantsDto.setConsultants(consultantDtos);
        //对consultans进行批处理
        for(Consultant consultant:consultants){
            consultantDtos.add(consultant.convert2ConsultantDto(consultantService));
        }

        return R.success(consultantsDto);
    }

    /**
     * 新增咨询师
     */
    @AdminToken
    @PostMapping
    public R<String> save(@Validated @RequestBody Consultant consultant){
        log.info("consultant:{}",consultant);
        String phone = consultant.getPhone();
        //判断手机号是否已经注册
        if(consultantService.getOne(new LambdaQueryWrapper<Consultant>().eq(Consultant::getPhone,phone))!=null){
            return R.error("该手机号已经注册");
        }
        consultantService.save(consultant);
        //同步添加到User类,从Consultant表中获取id
        User user = userUtils.saveUser(consultant);
        //导入账号到腾讯云IM
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Identifier",user.getType().toString()+"_"+user.getUserId().toString());
        jsonObject.put("Nick",consultant.getName());
        jsonObject.put("FaceUrl",consultant.getAvatar());
        String identifier = user.getType().toString()+"_"+user.getUserId().toString();
        boolean isSuccess = tencentCloudImUtil.accountImport(identifier);
        if(!isSuccess){
            consultantService.remove(new LambdaQueryWrapper<Consultant>().eq(Consultant::getId,consultant.getId()));
            userUtils.deleteUser(user);
            return R.error("腾讯IM导入账号失败");
        }
        return R.success(consultant.getPhone(),"新增咨询师成功");
    }

    /**
     * 修改咨询师的信息
     * 使用ConsultantDto作为中转实体类，因为Consultant中的expertiseTag是json数组，需要转换
     */
    @PutMapping("/{consultantId}/profile")
    public R<String> update(@PathVariable("consultantId") Integer consultantId,@Validated @RequestBody UpdateConsultantProfileDto updateConsultantProfileDto) throws JsonProcessingException {
        log.info("consultantId:{},consultant:{}",consultantId,updateConsultantProfileDto);
        //权限验证
        consultantUtil.isConsultantOrAdmin(consultantId);
        //将consultantDto的值复制给consultant
        Consultant consultant = consultantService.getById(updateConsultantProfileDto.getId());
        BeanUtils.copyProperties(updateConsultantProfileDto,consultant,"expertiseTag");
        if(updateConsultantProfileDto.getExpertiseTag()!=null) consultant.setExpertiseTag(objectMapper.writeValueAsString(updateConsultantProfileDto.getExpertiseTag()));
        //同步更新腾讯云IM
        String identifier = "1"+"_"+consultantId.toString();
        String gender = "Gender_Type_Unknown";
        switch (consultant.getGender()){
            case 0:
                gender = "Gender_Type_Female";
                break;
            case 1:
                gender = "Gender_Type_Male";
                break;
            default:
                break;
        }
        boolean isSuccess = tencentCloudImUtil.updateAccount(identifier,consultant.getName(),consultant.getAvatar(),gender);
        if(!isSuccess){
            return R.error("腾讯IM更新账号失败");
        }
        else{
            //同步更新Consultant表
            consultantService.updateById(consultant);
            //同步更新User表
            userUtils.updateUser(consultant);
        }
        return R.success("修改咨询师详情成功");
    }

    /**
     * 根据咨询师id查询咨询师的详细信息
     * 从token中获取id并根据id进行查询
     */
    @GetMapping("/profile")
    public R<ConsultantProfileDto> getOwnProfile() throws JsonProcessingException {
        User user = TokenUtil.getTokenUser();
        Integer id = user.getUserId();
        Integer type = user.getType();
        if(type != 1) return R.error("非咨询师本人无权限");
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
        consultantUtil.isConsultantOrAdmin(consultantId);
        //返回数据
        AnyConsultantProfileDto anyConsultantProfileDto = new AnyConsultantProfileDto();
        Consultant consultant = consultantService.getById(consultantId);
        BeanUtils.copyProperties(consultant,anyConsultantProfileDto,"expertiseTag","id");
        anyConsultantProfileDto.setConsultantName(consultant.getName());
        anyConsultantProfileDto.setAvatar(consultant.getAvatar());
        anyConsultantProfileDto.setDetailIntro(consultant.getDetailedIntro());
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
        List<CommentDto> list = consultantService.getCommentDto(consultantId,1,10,null);
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
        //id
        anyConsultantProfileDto.setId(consultant.getId().toString());
        //expertiseTag
        if(consultant.getExpertiseTag()!=null) anyConsultantProfileDto.setExpertiseTag(objectMapper.readValue(consultant.getExpertiseTag(),new TypeReference<List<ExpertiseTag>>() {}));
        return R.success(anyConsultantProfileDto);
    }

    /**
     * 获取咨询师的评价列表
     * @param consultantId
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/{consultantId}/comments")
    public R<CommentsDto> getComments(@PathVariable("consultantId") Integer consultantId, HttpServletRequest httpServletRequest){
        //权限验证
        consultantUtil.isConsultantOrAdmin(consultantId);
        //分页查询
        int page = Integer.parseInt(httpServletRequest.getParameter("page"));
        int pageSize = Integer.parseInt(httpServletRequest.getParameter("pageSize"));
        CommentsDto commentsDto = new CommentsDto();
        consultantService.getCommentsDto(commentsDto,consultantId,page,pageSize);
        return R.success(commentsDto);
    }

    /**
     * 更新咨询师的绑定列表
     * @param consultantId
     */
    @AdminToken
    @PutMapping("/{consultantId}/bindings")
    public R<SupervisorBindedDto> updateBindings(@PathVariable("consultantId") Integer consultantId, @RequestBody SupervisorBindedDto supervisorBindedDto){
        //获取指定咨询师的所有绑定记录
        List<Integer> supervisorBinded = supervisorBindedDto.getSupervisorBinded();
        LambdaQueryWrapper<Binding> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Binding::getConsultantId,consultantId);
        List<Binding> bindings = bindingService.list(queryWrapper);
        //删除解绑的督导
        if(bindings!=null){
            for(Binding binding : bindings){
                if(!supervisorBinded.contains(binding.getSupervisorId())){
                    bindingService.removeById(binding.getId());
                }
            }
        }
        if(supervisorBinded!=null){
            //加入新绑定的
            for(Integer supervisorId: supervisorBinded){
                if(!bindings.stream().map(Binding::getSupervisorId).collect(Collectors.toList()).contains(supervisorId)){
                    Binding binding = new Binding();
                    binding.setConsultantId(consultantId);
                    binding.setSupervisorId(supervisorId);
                    bindingService.save(binding);
                }
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
        consultant.setIsDisabled(consultant.getIsDisabled()==1?0:1);
        consultantService.updateById(consultant);
        return R.success("更新咨询师权限成功");
    }

    /**
     * 更新咨询师的最大咨询数
     * @param consultantId
     * @param body
     * @return
     */
    @PutMapping("/{consultantId}/max-consult-count")
    public R<String> updateMaxConsultCount(@PathVariable("consultantId") String consultantId,@RequestBody Map body){
        //权限验证
        consultantUtil.isConsultantOrAdmin(Integer.valueOf(consultantId));
        //更新最大咨询数
        Consultant consultant = consultantService.getById(Integer.parseInt(consultantId));
        consultant.setMaxNum((Integer) body.get("consultMaxCount"));
        consultantService.updateById(consultant);
        return R.success("更新最大咨询数成功");
    }

    /**
     * 更新咨询师的最大同时咨询人数
     */
    @PutMapping("/{consultantId}/max-concurrent-count")
    public R<String> updateMaxConcurrentCount(@PathVariable("consultantId") String consultantId,@RequestBody Map body){
        //权限验证
        Integer id = TokenUtil.getTokenUser().getUserId();
        if(!consultantId.equals(id.toString())&&adminService.getById(consultantId)==null){
            return R.auth_error();
        }
        //更新最大同时咨询人数
        Consultant consultant = consultantService.getById(Integer.parseInt(consultantId));
        consultant.setMaxConcurrent((Integer) body.get("consultMaxConcurrentCount"));
        consultantService.updateById(consultant);
        return R.success("更新最大同时咨询人数成功");
    }

    /**
     * 更新咨询师的排班
     * @param date
     * @param consultants
     * @return
     */
    @PostMapping("/schedule/{date}")
    public R<String> addConsultantToSchedule(@PathVariable("date") String date,@RequestBody HashMap<String, List<LinkedHashMap<String, Object>>> consultants){
        List<LinkedHashMap<String, Object>> consultantSchedules = consultants.get("consultants");
        //添加咨询师
        for(LinkedHashMap<String, Object> consultant:consultantSchedules){
            //判断该咨询师当天是否已经有排班
            LambdaQueryWrapper<Schedule> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Schedule::getStaffId,consultant.get("consultantId"));
            queryWrapper.eq(Schedule::getWorkday,Integer.valueOf(date));
            if(scheduleService.list(queryWrapper).size()>0){
                return R.error("该咨询师"+consultant.get("consultantName")+"当天已经有排班");
            }
            Schedule schedule = new Schedule();
            schedule.setStaffType(1);
            schedule.setStaffId((Integer) consultant.get("consultantId"));
            schedule.setWorkday(Integer.valueOf(date));
            scheduleService.save(schedule);
        }
        return R.success("添加咨询师排班成功");
    }

    /**
     * 删除咨询师的排班
     * @param date
     * @param consultants
     * @return
     */
    @DeleteMapping("/schedule/{date}")
    public R<String> deleteConsultantFromSchedule(@PathVariable("date") Integer date,@RequestBody HashMap<String,Integer> consultants){
        //权限判断,只有督导员和管理员可以更新
        User user = TokenUtil.getTokenUser();
        if(user.getType().equals(1)){
            return R.auth_error();
        }
        Integer consultantId = consultants.get("consultantId");
        //删除咨询师
        LambdaQueryWrapper<Schedule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Schedule::getStaffId,consultantId);
        queryWrapper.eq(Schedule::getWorkday,date);
        scheduleService.remove(queryWrapper);
        return R.success("删除咨询师排班成功");
    }

    /**
     * 咨询师获取本人的排班信息
     */
    @GetMapping("/schedule")
    public R<List<Integer>> getConsultantSchedule(){
        Integer consultantId = TokenUtil.getTokenUser().getUserId();
        LambdaQueryWrapper<Schedule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Schedule::getStaffId,consultantId);
        queryWrapper.eq(Schedule::getStaffType,1);
        List<Schedule> schedules = scheduleService.list(queryWrapper);
        List<Integer> workdays = new ArrayList<>();
        for(Schedule schedule:schedules){
            workdays.add(schedule.getWorkday());
        }
        return R.success(workdays);
    }

    /**
     * 咨询师填写用户评估，修改Record表
     */
    @PostMapping("/evaluations/{recordId}")
    public R<String> addEvaluation(@PathVariable("recordId") Integer recordId,@RequestBody HashMap<String,String> evaluation){
        //获取对应的record
        Record record = recordService.getById(recordId);
        record.setEvaluation(String.valueOf(evaluation.get("evaluation")));
        record.setConsultType(String.valueOf(evaluation.get("consultType")));
        //更新record
        recordService.updateById(record);
        return R.success("咨询师填写用户评估成功");
    }

    /**
     * 更新密码
     * @return
     */
    @PutMapping("/{consultant-id}/password")
    public R<String> updatePassword(@PathVariable("consultant-id") String consultantId,@Validated @RequestBody PasswordDto body) {
        //权限判断
        consultantUtil.isConsultantOrAdmin(Integer.valueOf(consultantId));
        //更新密码
        Consultant consultant = consultantService.getById(consultantId);
        consultant.setPassword(body.getPassword());
        consultantService.updateById(consultant);
        //同步更新User表
        userUtils.updateUser(consultant);
        return R.success("更新密码成功");
    }

//    @PutMapping("/{consultant-id}/phone")
//    public R<String> updatePhone(@PathVariable("consultant-id") String consultantId,@RequestBody Map body) {
//        //权限判断
//        consultantUtil.isConsultantOrAdmin(Integer.valueOf(consultantId));
//        //更新手机号
//        Consultant consultant = consultantService.getById(consultantId);
//        consultant.setPhone((String) body.get("phone"));
//        consultantService.updateById(consultant);
//        //同步更新User表
//        userUtils.updateUser(consultant);
//        return R.success("更新手机号成功");
//    }
    @GetMapping("/{consultant-id}/online/bindings")
    public R<List<OnlineBinding>> getOnlineBindings(@PathVariable("consultant-id") String consultantId) {
        //权限判断
        consultantUtil.isConsultantOrAdmin(Integer.valueOf(consultantId));
        //督导需在线
        LambdaQueryWrapper<Supervisor> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Supervisor::getIsOnline,1);
        List<Supervisor> supervisors = supervisorService.list(queryWrapper1);
        List<Integer> supervisorIds = supervisors.stream()
                .map(Supervisor::getId)
                .collect(Collectors.toList());
        //获取绑定信息
        LambdaQueryWrapper<Binding> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Binding::getConsultantId,consultantId);
        queryWrapper.apply("supervisor_id IN (SELECT id FROM supervisor WHERE id IN (" + StringUtils.join(supervisorIds, ",") + "))");
        List<Binding> bindings = bindingService.list(queryWrapper);
        //输出Dto
        List<OnlineBinding> onlineBindings = new ArrayList<>();
        for(Binding binding:bindings){
            OnlineBinding onlineBinding = new OnlineBinding();
            onlineBinding.setId(binding.getSupervisorId());
            Supervisor supervisor = supervisorService.getById(binding.getSupervisorId());
            onlineBinding.setName(supervisor.getName());
            onlineBindings.add(onlineBinding);
        }
        return R.success(onlineBindings);
    }
}
