package com.example.heart_field.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.heart_field.common.R;
import com.example.heart_field.constant.TypeConstant;
import com.example.heart_field.dto.binding.OnlineBinding;
import com.example.heart_field.dto.binding.SupervisorBindedDto;
import com.example.heart_field.dto.consultant.ConsultantDto;
import com.example.heart_field.dto.consultant.ConsultantsDto;
import com.example.heart_field.dto.consultant.PasswordDto;
import com.example.heart_field.dto.consultant.PhoneDto;
import com.example.heart_field.dto.consultant.comment.CommentDto;
import com.example.heart_field.dto.consultant.comment.CommentsDto;
import com.example.heart_field.dto.consultant.profile.AnyConsultantProfileDto;
import com.example.heart_field.dto.consultant.profile.ConsultantProfileDto;
import com.example.heart_field.dto.consultant.profile.UpdateConsultantProfileDto;
import com.example.heart_field.dto.user.ExpertiseTag;
import com.example.heart_field.entity.*;
import com.example.heart_field.service.*;
import com.example.heart_field.tokens.AdminOrSupervisorToken;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
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
        ConsultantsDto consultantsDto = new ConsultantsDto();
        List<Consultant> consultants = consultantService.getConsultants(searchValue,sort,sortType,page,pageSize,consultantsDto);
        //DTO 转换
        List<ConsultantDto> consultantDtos = new ArrayList<>();
        //对consultants进行批处理
        for(Consultant consultant:consultants){
            consultantDtos.add(consultant.convert2ConsultantDto(consultantService));
        }
        consultantsDto.setConsultants(consultantDtos);
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
        R r = consultantUtil.isConsultantOrAdmin(consultantId);
        if(r!=null) return r;
        //将consultantDto的值复制给consultan
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
            userUtils.updateUser(consultant,null);
        }
        return R.success("修改咨询师详情成功");
    }

    /**
     * 更新密码
     * @return
     */
    @PutMapping("/{consultant-id}/password")
    public R<String> updatePassword(@PathVariable("consultant-id") String consultantId,@Validated @RequestBody PasswordDto body) {
        //权限判断
        R r = consultantUtil.isConsultantOrAdmin(Integer.valueOf(consultantId));
        if(r!=null) return r;
        //更新密码
        Consultant consultant = consultantService.getById(consultantId);
        consultant.setPassword(body.getPassword());
        consultantService.updateById(consultant);
        //同步更新User表
        userUtils.updateUser(consultant,"password");
        return R.success("更新密码成功");
    }

    @PutMapping("/{consultant-id}/phone")
    public R<String> updatePhone(@PathVariable("consultant-id") String consultantId,@Validated @RequestBody PhoneDto body) {
        //权限判断
        consultantUtil.isConsultantOrAdmin(Integer.valueOf(consultantId));
        //更新手机号
        Consultant consultant = consultantService.getById(consultantId);
        consultant.setPhone(body.getPhone());
        consultantService.updateById(consultant);
        //同步更新User表
        userUtils.updateUser(consultant,null);
        return R.success("更新手机号成功");
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
        //返回数据
        AnyConsultantProfileDto anyConsultantProfileDto = new AnyConsultantProfileDto();
        Consultant consultant = consultantService.getById(consultantId);
        if(consultant == null) return R.error("请求的咨询师不存在");
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
     * 分页查询咨询师的评价列表
     * @param consultantId
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/{consultantId}/comments")
    public R<CommentsDto> getComments(@PathVariable("consultantId") Integer consultantId, HttpServletRequest httpServletRequest){
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
        //判断该咨询师是否存在
        Consultant consultant = consultantService.getById(consultantId);
        if(consultant==null) return R.resource_error();
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
        //判断是否有该咨询师
        consultantUtil.isExist(consultantId);
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
        R r = consultantUtil.isConsultantOrAdmin(Integer.valueOf(consultantId));
        if(r!=null) return r;
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
        R r = consultantUtil.isConsultantOrAdmin(Integer.valueOf(consultantId));
        if(r!=null) return r;
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
    @AdminOrSupervisorToken
    public R<String> addConsultantToSchedule(@PathVariable("date") String date,@RequestBody HashMap<String, List<LinkedHashMap<String, Object>>> consultants){
        List<LinkedHashMap<String, Object>> consultantSchedules = consultants.get("consultants");
        //添加咨询师
        for(LinkedHashMap<String, Object> consultant:consultantSchedules){
            //判断该咨询师是否存在
            consultantUtil.isExist((Integer) consultant.get("consultantId"));
            //添加排班
            Schedule schedule = new Schedule();
            schedule.setStaffType(1);
            schedule.setStaffId((Integer) consultant.get("consultantId"));
            schedule.setWorkday(Integer.valueOf(date));
            //判断是不是当天，如果是当天的话也要更新咨询师的 isOnline 属性
            Integer day = LocalDate.now().getDayOfMonth();
            if(day.toString().equals(date)){
                Integer id = (Integer) consultant.get("consultantId");
                Consultant consultant1 = consultantService.getById(id);
                consultant1.setIsOnline(1);
                consultantService.updateById(consultant1);
            }
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
    @AdminOrSupervisorToken
    public R<String> deleteConsultantFromSchedule(@PathVariable("date") Integer date,@RequestBody HashMap<String,Integer> consultants){
        //判断该咨询师是否存在
        Integer consultantId = consultants.get("consultantId");
        consultantUtil.isExist(consultantId);
        //删除咨询师
        LambdaQueryWrapper<Schedule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Schedule::getStaffId,consultantId);
        queryWrapper.eq(Schedule::getWorkday,date);
        scheduleService.remove(queryWrapper);
        //判断是不是当天，如果是当天的话也要更新咨询师的 isOnline 属性
        Integer day = LocalDate.now().getDayOfMonth();
        if(day.equals(date)){
            Consultant consultant1 = consultantService.getById(consultantId);
            consultant1.setIsOnline(0);
            consultantService.updateById(consultant1);
        }
        return R.success("删除咨询师排班成功");
    }

    /**
     * 咨询师获取本人的排班信息
     */
    @GetMapping("/schedule")
    public R<List<Integer>> getConsultantSchedule(){
        //非咨询师无权限
        User user = TokenUtil.getTokenUser();
        if(user.getType()!= TypeConstant.CONSULTANT) return R.error("非咨询师，无权限");
        //查询排班
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
        //非咨询师无权限
        User user = TokenUtil.getTokenUser();
        if(user.getType()!= TypeConstant.CONSULTANT) return R.error("非咨询师，无权限");
        //获取对应的record
        Record record = recordService.getById(recordId);
        if(record==null) return R. resource_error();
        record.setEvaluation(String.valueOf(evaluation.get("evaluation")));
        record.setConsultType(String.valueOf(evaluation.get("consultType")));
        record.setConsultantCompleted(1);
        //更新record
        recordService.updateById(record);
        return R.success("咨询师填写用户评估成功");
    }

    @GetMapping("/{consultant-id}/online/bindings")
    public R<List<OnlineBinding>> getOnlineBindings(@PathVariable("consultant-id") String consultantId) {
        //权限判断
        R r = consultantUtil.isConsultantOrAdmin(Integer.valueOf(consultantId));
        if(r!=null) return r;
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
        List<Binding> bindings = new ArrayList<>();
        if(supervisorIds.size()!=0){
            queryWrapper.apply("supervisor_id IN (SELECT id FROM supervisor WHERE id IN (" + StringUtils.join(supervisorIds, ",") + "))");
            bindings = bindingService.list(queryWrapper);
        }
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
