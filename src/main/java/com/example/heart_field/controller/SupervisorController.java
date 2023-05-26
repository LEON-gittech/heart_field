package com.example.heart_field.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.heart_field.common.R;
import com.example.heart_field.dto.*;
import com.example.heart_field.entity.*;
import com.example.heart_field.service.*;
import com.example.heart_field.tokens.AdminToken;
import com.example.heart_field.tokens.UserLoginToken;
import com.example.heart_field.utils.PwdCheckUtil;
import com.example.heart_field.utils.TokenUtil;
import com.example.heart_field.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Transactional
@UserLoginToken
@RestController
@EnableWebMvc
@Slf4j
@RequestMapping("/supervisors")
public class SupervisorController {
    @Autowired
    private SupervisorService supervisorService;
    @Autowired
    private  UserUtils userUtils;
    @Autowired
    private BindingService bindingService;
    @Autowired
    private ConsultantService consultantService;
    @Autowired
    private HelpService helpService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private ScheduleService scheduleService;
    /**
     * 新增一个督导
     */

    @AdminToken

    @PostMapping
    public R<String> addSupervisor(@RequestBody SupervisorDto supervisorDto) {
        try {
            String phone = supervisorDto.phone;
            String password = supervisorDto.password;
            log.info("new新增督导电话:{},密码:{}", phone, password);
            if (phone.length() != 11) {
                return R.error("电话号码不合格");
            }
            //密码最小八位数，最大20位
            if (!PwdCheckUtil.checkPasswordLength(password,"8","20") ) {
                return R.error("密码长度不符合");
            }else{
                int i = 0;
                if(PwdCheckUtil.checkContainDigit(password) && PwdCheckUtil.checkContainCase(password))
                    i++;
                if(PwdCheckUtil.checkContainLowerCase(password)&&PwdCheckUtil.checkContainUpperCase(password))
                    i++;
                if(PwdCheckUtil.checkContainSpecialChar(password))
                    i++;
                if(i<2)
                    return R.error("密码强度不符合");
            }

            Supervisor supervisor = new Supervisor();
            supervisor.setPhone(phone);
            supervisor.setPassword(password);
            supervisorService.save(supervisor);
            User user = userUtils.saveUser(supervisor);
            log.info("新增成功,新增id:{}", user.getId());
            return R.success("添加成功");
        } catch (Exception e) {
            return R.error("系统错误");
        }

    }
    /**
     * 获取简易在线督导列表
     */
    @AdminToken
    @GetMapping("/preview")
    public R<List<SupervisorPreviewDto>> getSupervisorPreview(){
        List<SupervisorPreviewDto> list = new ArrayList<>();
        try{
            LambdaQueryWrapper<Supervisor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Supervisor::isOnline,1);
            List<Supervisor> supervisors = supervisorService.list(queryWrapper);
            for(int i = 0;i<supervisors.size();i++)
            {
                Supervisor one = new Supervisor();
                one = supervisors.get(i);
                SupervisorPreviewDto supervisorPreview = new SupervisorPreviewDto();
                supervisorPreview.id = one.getId();
                supervisorPreview.supervisorName=one.getName();
                if(one.isDisabled()){
                    if(2*one.getConcurrentNum() > one.getMaxConcurrent()){
                        supervisorPreview.state = "1";
                    }
                    else{
                        supervisorPreview.state="0";
                    }
                }else{
                    supervisorPreview.state="2";
                }
                list.add(supervisorPreview);
        }
        return R.success(list);
            }catch(Exception e){
               return R.error("系统错误");
          }
    }
    /**
     * 获取督导列表(可搜索)
     * 分页
     * 权限：管理员
     */
   // @AdminToken
    @GetMapping
    public R<SupervisorPageSearchDto> getSupervisorList(HttpServletRequest httpServletRequest){
        //try{

            httpServletRequest.getParameterMap().forEach((k,v)->{
                log.info("key={},value={}",k,v);
            });
            int page = Integer.parseInt(httpServletRequest.getParameter("page"));
            log.info("page:{}",page);
            int pageSize = Integer.parseInt(httpServletRequest.getParameter("pageSize"));
            log.info("pageSize:{}",pageSize);
            String supervisorName = httpServletRequest.getParameter("supervisorName");
            log.info("supervisorName:{}",supervisorName);
            int sortType = Integer.parseInt(httpServletRequest.getParameter("sortType"));
            log.info("sortType:{}",sortType);
            int sort = Integer.parseInt(httpServletRequest.getParameter("sort"));
            log.info("sort:{}",sort);

            Page<Supervisor> pageInfo = new Page<>(page,pageSize);
            //Page<SupervisorComDto> pageInfo = new Page<>(page,pageSize);
            LambdaQueryWrapper<Supervisor> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.like(StringUtils.hasText(supervisorName),Supervisor::getName,supervisorName);
            if(sort == 0){
                switch (sortType){
                    case 0:
                        queryWrapper.orderByDesc(Supervisor::isOnline);
                        break;
                    case 1:
                        queryWrapper.orderByAsc(Supervisor::isDisabled);
                        break;
                    case 2:
                        queryWrapper.orderByAsc(Supervisor::getName);
                    default:
                        break;
                }
            }else {
                switch (sortType){
                    case 0:
                        queryWrapper.orderByAsc(Supervisor::isOnline);
                        break;
                    case 1:
                        queryWrapper.orderByAsc(Supervisor::isDisabled);
                        break;
                    case 2:
                        queryWrapper.orderByAsc(Supervisor::getName);
                    default:
                        break;
                }
            }
            supervisorService.page(pageInfo,queryWrapper);
            // 实现返回部分字段
            List<Supervisor> supervisors = supervisorService.list(queryWrapper);
            List<SupervisorComDto> list = new ArrayList<>();
            for(int i=0;i<supervisors.size();i++){
                SupervisorComDto supervisorCom = new SupervisorComDto();
                Supervisor one = supervisors.get(i);
                Integer supervisorId = one.getId();
                supervisorCom.id = one.getId().toString();
                supervisorCom.supervisorAvatar = one.getAvatar();
                supervisorCom.supervisorName = one.getName();
                // 查找关联咨询师
                List<ConsultantEasyDto> consultantList = new ArrayList<>();
                LambdaQueryWrapper<Binding> bindingWrapper = new LambdaQueryWrapper<>();
                //LambdaQueryWrapper<Consultant> consultantWrapper = new LambdaQueryWrapper<>();
                bindingWrapper.eq(Binding::getSupervisorId,one.getId());
                List<Binding> binds = bindingService.list(bindingWrapper);
                for(int j=0;j<binds.size();j++){
                    Binding oneBind = binds.get(j);
                    ConsultantEasyDto consultant = new ConsultantEasyDto();

                    Integer conId = oneBind.getConsultantId();
                    consultant.id = conId.toString();

                    //consultantWrapper.eq(Consultant::getId,conId);
                    Consultant consultant1 = consultantService.getById(conId);
                    consultant.consultantName =consultant1.getName();
                    consultantList.add(consultant);
                }
                //统计帮助次数和时长
                LambdaQueryWrapper<Help> helpLambdaQueryWrapper = new LambdaQueryWrapper<>();
                helpLambdaQueryWrapper.eq(Help::getSupervisorId,supervisorId);
                List<Help> helpList = helpService.list(helpLambdaQueryWrapper);
                int total = 0;
                long time = 0;
                for(int k =0;k<helpList.size();k++){
                    Help oneHelp = helpList.get(k);
                    Duration duration = Duration.between(oneHelp.getEndTime(),oneHelp.getStartTime());
                    time += duration.toMinutes();
                    total++;
                }
                supervisorCom.consultTotalCount= total;
                supervisorCom.consultTotalTime = time;
                list.add(supervisorCom);
            }

            for(int i=0;i<list.size();i++){
                log.info("i:{},list:{}",i,list.get(i));
            }
            Page<SupervisorComDto> onePage = new Page<SupervisorComDto>(page,pageSize);
            //onePage.addOrder((OrderItem) list);
            onePage.setTotal(list.size());
            onePage.setRecords(list);
            SupervisorPageSearchDto supervisorPageSearchDto = new SupervisorPageSearchDto();
            supervisorPageSearchDto.setSupervisors(onePage.getRecords());
            Integer pageTotals = 0;
            if(list.size()%pageSize!=0)
                pageTotals = list.size()/pageSize+1;
            else
                pageTotals = list.size()/pageSize;
            supervisorPageSearchDto.setPageNum(pageTotals);
            return R.success(supervisorPageSearchDto,"成功");
            //return R.error("系统错误");
      /*  }catch (Exception e){
            return R.error("系统错误");

        }*/

    }

    /**
     * 修改督导个人信息
     * 督导和管理员权限
     */
    @PutMapping("/{supervisor-id}/profile")
    public R<String> updateSupervisorProfile(@PathVariable("supervisor-id") String supervisorId, @RequestBody UpdateConsultantDto updateConsultantDto){
            Integer id = Integer.valueOf(updateConsultantDto.getId());
            Integer tokenId = Integer.valueOf(TokenUtil.getTokenUserId());
            //判断传入的id参数是否与tokenid一致且不属于管理员,无权修改
            if(!tokenId.equals(Integer.valueOf(supervisorId)) && adminService.getById(tokenId)==null)
            {return R.auth_error();}
            LambdaQueryWrapper<Supervisor> supervisorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            supervisorLambdaQueryWrapper.eq(Supervisor::getId,id);
            Supervisor supervisor=supervisorService.getOne(supervisorLambdaQueryWrapper);
            supervisor.setId(id);
            supervisor.setPassword(updateConsultantDto.getPassword());
            supervisor.setPhone(updateConsultantDto.getPhone());
            supervisor.setQualificationId(updateConsultantDto.getQualificationNumber());
            supervisor.setQualification(updateConsultantDto.getQualification());
            supervisor.setAvatar(updateConsultantDto.getAvatar());
            supervisor.setEmail(updateConsultantDto.getEmail());
            supervisor.setCardId(updateConsultantDto.getIdentificationNumber());
            supervisor.setWorkplace(updateConsultantDto.getWorkPlace());
            supervisor.setTitle(updateConsultantDto.getTitle());
            supervisor.setGender(Byte.parseByte(updateConsultantDto.getGender()));
            supervisor.setName(updateConsultantDto.getSupervisorName());
            supervisorService.updateById(supervisor);
            log.info("supervisor:{}",supervisor);
            return R.success("更新成功");


    }
    /**
     *获取督导首页个人信息
     * 从token中获取id
     * 督导本人可查看？机构管理员呢
     */
    @GetMapping("/profile")
    public R<SupervisorProfileDto> getSupervisorProfile(){
        try{
            Integer id = Integer.valueOf(TokenUtil.getTokenUserId());
            LambdaQueryWrapper<Supervisor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Supervisor::getId,id);
            Supervisor supervisor = supervisorService.getOne(queryWrapper);
            if(supervisor==null)
                return R.resource_error();
            SupervisorProfileDto supervisorProfileDto = new SupervisorProfileDto();
            supervisorProfileDto.setType(1);
            supervisorProfileDto.setName(supervisor.getName());
            supervisorProfileDto.setMaxConcurrentCount(supervisor.getMaxConcurrent());
            supervisorProfileDto.setMaxCount(supervisor.getMaxNum());
            supervisorProfileDto.setCurrentSessionCount(supervisor.getConcurrentNum());
            if(supervisor.isOnline())
                supervisorProfileDto.setState(1);
            else
                supervisorProfileDto.setState(0);
            LambdaQueryWrapper<Help> queryWrapper1 = new LambdaQueryWrapper<>();
            LocalDateTime localDateTime = LocalDateTime.now();
            queryWrapper1.eq(Help::getSupervisorId,id)
                    .eq(Help::getUpdateTime,localDateTime)
            ;
            List<Help> helpList = helpService.list(queryWrapper1);
            long todayTotalTime = 0;
            Integer totalTotalCount =0;
            for(int i=0;i<helpList.size();i++){
                Help one = helpList.get(i);
                totalTotalCount++;
                Duration duration = Duration.between(one.getEndTime(),one.getStartTime());
                todayTotalTime += 60*duration.toMinutes();

            }
            supervisorProfileDto.setTodayTotalCount(totalTotalCount);
            supervisorProfileDto.setTodayTotalTime(todayTotalTime);
            return R.success(supervisorProfileDto);
        }catch (Exception e){
            return R.error("系统错误");
        }


    }

    /**
     * 获取督导详细个人信息
     * 督导和机构管理员都可以
     */
    @GetMapping("/{supervisor-id}/profile")
    public R<SupervisorInfoDto> getSupervisorInfo(@PathVariable("supervisor-id") Integer supervisorId){
        Integer id = Integer.valueOf(TokenUtil.getTokenUserId());
        if(id.equals(supervisorId) || adminService.getById(id)!=null) {
            Supervisor supervisor = supervisorService.getById(supervisorId);
            //督导不存在
            if(supervisor==null)
                return R.resource_error();
            SupervisorInfoDto supervisorInfoDto = new SupervisorInfoDto();
            BeanUtils.copyProperties(supervisor,supervisorInfoDto);
            return R.success(supervisorInfoDto);
        }else{
            return R.auth_error();
        }
    }

    /**
     * 修改督导绑定咨询师列表
     * 管理员权限
     */
    @AdminToken
    @PutMapping("/{supervisor-id}/bindings")
    public R<String> updateSupervisorBindings(@PathVariable("supervisor-id") Integer supervisorId,@RequestBody ConsultantBindedDto consultantBinded){
        Integer id = Integer.valueOf(TokenUtil.getTokenUserId());
        Supervisor supervisor = supervisorService.getById(supervisorId);
        //督导不存在
        if(supervisor==null)
            return R.resource_error();
        //不再多重判断传入的咨询师列表是否合法，默认合法,有自带外键约束！
        LambdaQueryWrapper<Binding> bindingLambdaQueryWrapper = new LambdaQueryWrapper<>();
        bindingLambdaQueryWrapper.eq(Binding::getSupervisorId,supervisorId);
        List<Binding> bindings = bindingService.list(bindingLambdaQueryWrapper);
        for(int i = 0;i<bindings.size();i++){
            Binding oneBind = bindings.get(i);
            oneBind.setIsDeleted(1);
        }
        for(int j = 0;j<consultantBinded.getConsultantBinded().size();j++){
            Binding newBind = new Binding();
            newBind.setConsultantId(Integer.valueOf(consultantBinded.getConsultantBinded().get(j)));
            newBind.setSupervisorId(supervisorId);
            bindingService.save(newBind);
        }
        return R.success("更新绑定成功");

    }
    /**
     * 修改督导访问能力
     * 管理员权限
     */
    @AdminToken
    @PutMapping("/{supervisor-id}/permission")
    public R<String> updateSupervisorEnable(@PathVariable("supervisor-id") Integer supervisorId,@RequestBody SupervisorPermissionDto permission){

        boolean enable = permission.isEnable();
        log.info("value:{}",enable);
        Supervisor supervisor=supervisorService.getById(supervisorId);
        if(supervisor==null)
            return R.resource_error();
        if(enable)//开启督导使用权限
            supervisor.setDisabled(false);
        else supervisor.setDisabled(true);
        supervisorService.updateById(supervisor);
        return R.success("修改督导使用状态成功");

    }

    /**
     * 修改督导同时最大咨询人数
     * 权限：督导本人与管理员
     */
    @PutMapping("/{supervisor-id}/max-concurrent-count")
    public R<String> updateSupervisorMaxCount(@PathVariable("supervisor-id") String supervisorId,@RequestBody SupervisorMaxCountDto supervisorMaxCount){
        //通过token判断是否满足权限
        Integer superId = Integer.valueOf(supervisorId);
        User user = TokenUtil.getTokenUser();
        Integer id = user.getUserId();
        Integer type = user.getType();
        boolean authFlag = false;
        if(type==2) {
            Admin admin = adminService.getById(id);
            if (admin == null)
                authFlag = false;
            else authFlag = true;
        }
        else if (id.equals(superId) && type == 3) {
            authFlag = true;}
        if(authFlag){
                log.info("--------");
                Integer num = supervisorMaxCount.getNum();
                log.info("num:{}", num);
                Supervisor supervisor = supervisorService.getById(superId);
                supervisor.setMaxConcurrent(num);
                supervisorService.updateById(supervisor);
                return R.success("修改成功");
            }
        else
            return R.auth_error();
    }

    /**
     * 修改督导的今日最大咨询人数
     * 权限：督导本人
     */
    @PutMapping("/{supervisor-id}/max-consult-count")
    public R<String> updateConsulantBindedMaxCount(@PathVariable("supervisor-id") String supervisorId, @RequestBody SupervisorConsultMaxCountDto supervisorConsultMaxCount){
        Supervisor supervisor = supervisorService.getById(supervisorId);
        User user = TokenUtil.getTokenUser();
        if(user.getType()!=3 && user.getUserId()!= Integer.valueOf(supervisorId))
            return R.auth_error();
        supervisor.setMaxNum(supervisorConsultMaxCount.getNum());
        supervisorService.updateById(supervisor);
        return R.success("修改成功");
    }

    /**
     * 移去督导某天的排班
     * 权限：管理员
     */
    @AdminToken
    @DeleteMapping("/{supervisor-id}/schedules/{date}")
    public R<String> deleteSupervisorSchedulesSomeday(@PathVariable("supervisor-id") String supervisorId,@PathVariable("date") Integer date){
        Supervisor supervisor = supervisorService.getById(supervisorId);
        if(supervisor==null)
            return R.resource_error();
        if(date>31 || date<1)
            return R.argument_error();
        LambdaQueryWrapper<Schedule> scheduleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        scheduleLambdaQueryWrapper.eq(Schedule::getWorkday,date)
                .eq(Schedule::getStaffId,supervisorId)
                .eq(Schedule::getStaffType,1);
        Schedule schedule = scheduleService.getOne(scheduleLambdaQueryWrapper);
        if(schedule==null)
            return R.error("该排班记录不存在");
        scheduleService.removeById(schedule.getId());
        return R.success("删除排班成功");

    }
    /**
     * 在排班表中某天添加督导，给督导排班
     * 权限：管理员
     */
    @AdminToken
    @PostMapping("/schedules/{date}")
    public R<String> addSupervisorSchedule(@PathVariable("date") Integer date,@RequestBody AddSupervisorsDto addSupervisorList){
        List<Integer> list = addSupervisorList.getSupervisorsId();
        for(int i = 0;i<list.size();i++){
            //判断督导id是否合法，即是否在督导表中
            Supervisor supervisor = supervisorService.getById(list.get(i));
            if(supervisor==null||date>31||date<1)
                return R.argument_error();
            Schedule schedule = new Schedule();
            schedule.setStaffId(list.get(i));
            schedule.setWorkday(date);
            schedule.setCreateTime(LocalDateTime.now());
            schedule.setStaffType(1);
            scheduleService.save(schedule);
        }
        return R.success("添加成功");
    }
    /**
     * 获取督导排班信息
     * 权限：督导本人和管理员
     */
    @GetMapping("/{supervisor-id}/schedules")
    public R<List<Integer>> getSupervisorSchedule(@PathVariable("supervisor-id") String supervisorId){
        //验证权限
        //Integer id = TokenUtil.getTokenUserId();
        User user = TokenUtil.getTokenUser();
        Integer id = user.getUserId();
        Integer type = user.getType();
        log.info("1===id:{}",user.getUserId());
        log.info("id:{}",id);
        Integer superId = Integer.valueOf(supervisorId);
        if(type ==3 && !id.equals(superId)) {
            return R.auth_error();
        }
        else if(type==2){
            Admin admin = adminService.getById(id);
            if(admin==null)
                return R.auth_error();
        }
        LambdaQueryWrapper<Schedule> scheduleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        scheduleLambdaQueryWrapper.eq(Schedule::getStaffId,superId)
                .eq(Schedule::getStaffType,1);
        List<Schedule> schedules = scheduleService.list(scheduleLambdaQueryWrapper);
        List<Integer> workdayList = new ArrayList<>();
        for(int i = 0;i<schedules.size();i++){
            Schedule one = schedules.get(i);
            workdayList.add(one.getWorkday());
        }
        return R.success(workdayList,"获取排班信息成功");

    }
}
