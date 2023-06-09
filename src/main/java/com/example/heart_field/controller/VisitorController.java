package com.example.heart_field.controller;

import cn.hutool.core.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.heart_field.common.R;
import com.example.heart_field.constant.RegexPattern;
import com.example.heart_field.dto.visitor.VisitorPcychDTO;
import com.example.heart_field.dto.visitor.WxLoginDTO;
import com.example.heart_field.dto.record.RecordDTO;
import com.example.heart_field.dto.record.RecordListDTO;
import com.example.heart_field.dto.record.RecordPage;
import com.example.heart_field.entity.*;
import com.example.heart_field.mapper.*;
import com.example.heart_field.param.VisitorPcychParam;
import com.example.heart_field.param.VisitorUpdateParam;
import com.example.heart_field.param.WxLoginParam;
import com.example.heart_field.service.RecordService;
import com.example.heart_field.service.VisitorService;
import com.example.heart_field.tokens.AdminToken;
import com.example.heart_field.tokens.StaffToken;
import com.example.heart_field.tokens.UserLoginToken;
import com.example.heart_field.utils.TencentCloudImUtil;
import com.example.heart_field.utils.TokenUtil;
import com.example.heart_field.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author albac0020@gmail.com
 * data 2023/5/17 9:12 PM
 */
@Slf4j
@RestController
@RequestMapping("/visitors")
public class VisitorController {
    @Autowired
    private VisitorService visitorService;
    @Autowired
    private RecordService recordService;
    @Autowired
    private VisitorMapper visitorMapper;
    @Autowired
    private ConsultantMapper consultantMapper;
    @Autowired
    private SupervisorMapper supervisorMapper;
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private TencentCloudImUtil tencentCloudImUtil;

    @PostMapping("/auth/login")
    public R<WxLoginDTO> login(@RequestBody WxLoginParam loginParam){
        log.info("loginParam:{}",loginParam);
        //校验参数
        if (StringUtils.isEmpty(loginParam.getCode())){
            return R.login_error("参数不合法");
        }
        R result = visitorService.authLogin(loginParam);
        log.info("{}",result);
        return result;
    }


    /**
     * 分页查询访客列表,有参数username表示根据姓名模糊查询
     * 登录后的管理端使用，访客端或未登录不可
     * @return
     */
    @GetMapping()
    @StaffToken
    public R<Page> page(@RequestParam(value = "searchValue", required = false) String searchValue,
                        @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                        @RequestParam(value = "pageNum", required = false,defaultValue = "1") int pageNum) {
        log.info("searchValue:{}", searchValue);
        log.info("pageSize:{},pageNum:{}", pageSize, pageNum);
        if(pageNum<1||pageSize<1){
            return R.argument_error("分页参数不合法");
        }
        User user = TokenUtil.getTokenUser();
        switch (user.getType()){
            //type为0是Visitor，1是Consultant，2是Admin，3是Supervisor
            case 0:
                return R.auth_error("访客端无法访问");
            case 1:
                Consultant consultant = consultantMapper.selectById(user.getUserId());
                if(consultant==null||consultant.getIsDisabled()==1){
                    return R.auth_error("账号不存在或已被禁用");
                }
                break;
            case 2:
                Admin admin = adminMapper.selectById(user.getUserId());
                if(admin==null||admin.getIsDisabled()==1){
                    return R.auth_error("账号不存在或已被禁用");
                }
                break;
            case 3:
                Supervisor supervisor = supervisorMapper.selectById(user.getUserId());
                if(supervisor==null||supervisor.getIsDisabled()==1){
                    return R.auth_error("账号不存在或已被禁用");
                }
                break;
            default:
                return R.auth_error("账号不存在或已被禁用");
        }
        //构造分页构造器
        Page<Visitor> pageInfo = new Page<>(pageNum,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Visitor> queryWrapper = new LambdaQueryWrapper();

        //模糊查询
        queryWrapper.like(StringUtils.hasText(searchValue), Visitor::getName, searchValue)
                .or().like(StringUtils.hasText(searchValue), Visitor::getPhone, searchValue)
                .or().like(StringUtils.hasText(searchValue), Visitor::getEmergencyName, searchValue)
                .or().like(StringUtils.hasText(searchValue), Visitor::getEmergencyPhone, searchValue);
        visitorService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }


    /**
     * 根据id查询访客个人信息
     * 登录后，均可使用
     * 根据不同角色操作不通过
     * - 访客端：访客可查询自己信息
     * - 管理端：管理端可查看所有访客的个人信息
     * @param visitorId
     * @return
     */
    @GetMapping("/{visitor-id}/profile")
    @UserLoginToken
    public R<Visitor> getVisitorProfile(@PathVariable(value = "visitor-id") Integer visitorId) {
        log.info("visitorId:{}", visitorId);
        if(!UserUtils.checkSelfOrBack(visitorId)) return R.auth_error();
        Visitor visitor = visitorService.getById(visitorId);
        Integer type=TokenUtil.getTokenUser().getType();
        //type为0是Visitor，1是Consultant，2是Admin，3是Supervisor
        if(type==0){
            //访客自己已被封禁-不能查看
            return (visitor==null||visitor.getIsDisabled()==1)
                    ? R.error("本账号不存在或已被禁用")
                    : R.success(visitor);
        }
        else{
            //管理端可以查看已被封禁的
            return visitor==null
                    ? R.error("该访客不存在")
                    : R.success(visitor);
        }

    }

    /**
     * 用于
     *  -微信小程序端：可以自己的修改昵称、真实姓名、紧急联系人、电话号码
     *  -管理端管理员使用
     * @param visitorId
     * @param visitor
     * @return
     */
    @PutMapping("/{visitor-id}/profile")
    @UserLoginToken
    public R updateVisitorProfile(@PathVariable(value = "visitor-id") Integer visitorId,
                                  @RequestBody VisitorUpdateParam visitor) {
        Visitor realVisitor = visitorMapper.selectById(visitorId);
        log.info("checkVisitor:{}", realVisitor);
        if(realVisitor==null||realVisitor.getIsDisabled()==1){
            return R.resource_error();
        }
        log.info("visitorId:{}", visitorId);
        log.info("visitor:{}", visitor);
        String emergencyPhone = visitor.getEmergencyPhone();
        Pattern phonePattern = Pattern.compile(RegexPattern.MOBILE_PHONE_NUMBER_PATTERN);
        //手机号码格式校验
        if(realVisitor.getPhone()==emergencyPhone||!phonePattern.matcher(emergencyPhone).matches()){
            return R.argument_error("请输入正确的手机号码");
        }
        realVisitor.setUsername(visitor.getUsername());
        realVisitor.setName(visitor.getName());
        realVisitor.setEmergencyName(visitor.getEmergencyName());
        realVisitor.setEmergencyPhone(emergencyPhone);
        realVisitor.setGender(visitor.getGender().byteValue());
        realVisitor.setAge(visitor.getAge());
        boolean result=visitorService.updateById(realVisitor);
        log.info("result:{}", result);
        if(result==false){
            return R.error("更新失败");
        }else{
            String identifier = "0_"+realVisitor.getId().toString();
            String name = realVisitor.getName();
            String avatar_url = realVisitor.getAvatar();
            String sex = realVisitor.getGender()==0?"女":"男";
            boolean isSuccess=tencentCloudImUtil.updateAccount(identifier,name,avatar_url,sex);
            if(!isSuccess){
                return R.error("腾讯IM更新账号失败");
            }
            return R.success("更新成功");
        }

    }

    /**
     * 查看访客的心理档案
     * 用于：
     *  -访客本人
     *  -管理端所有用户
     * @param visitorId
     * @return
     */
    @GetMapping("/{visitor-id}/psych-archive")
    @UserLoginToken
    public R<VisitorPcychDTO> getPsychArchive(@PathVariable(value = "visitor-id") Integer visitorId) {
        log.info("visitorId:{}", visitorId);
        Visitor visitor= visitorService.getById(visitorId);
        if(visitor==null||visitor.getIsDisabled()==1){
            return R.auth_error("该访客不存在");
        }
        log.info("visitor:{}", visitor);
        log.info("visitor.getQuestion():{}", visitor.getQuestion());
        List<Integer> questions = new ArrayList<>();
        if(visitor.getQuestion()!=null){
            questions = Arrays.stream(visitor.getQuestion().split(", ")).map(Integer::parseInt).collect(Collectors.toList());
        }
        log.info("questions:{}", questions);
        VisitorPcychDTO visitorPcychDTO=VisitorPcychDTO.builder()
                .id(visitorId)
                .direction(visitor.getDirection()==null?"":visitor.getDirection())
                .question(questions)
                .history(visitor.getHistory()==null?"":visitor.getHistory())
                .puzzle(visitor.getPuzzle()==null?"":visitor.getPuzzle())
                .build();
        log.info("visitorPcychDTO:{}", visitorPcychDTO);
        return R.success(visitorPcychDTO);
    }

    /**
     *修改访客心理档案
     * 用于：
     *  -访客本人
     *  -管理员
     * @param visitorId
     * @param visitor
     * @return
     */
    @UserLoginToken
    @PutMapping("/{visitor-id}/psych-archive")
    public R updatePcychArchive(@PathVariable(value = "visitor-id") Integer visitorId,
                                         @RequestBody VisitorPcychParam visitor){
        if(!UserUtils.checkSelfOrAdmin(visitorId)) return R.auth_error();
        Visitor checkVisitor = visitorService.getById(visitorId);
        if(checkVisitor==null){
            return R.resource_error();
        }
        log.info("visitorId:{}", visitorId);
        log.info("visitor:{}", visitor);
        if(visitor.getQuestion().size()!=0) checkVisitor.setQuestion(visitor.getQuestion().toString().substring(1,visitor.getQuestion().toString().length()-1));
        if(visitor.getHistory()!=null) checkVisitor.setHistory(visitor.getHistory());
        if(visitor.getPuzzle()!=null) checkVisitor.setPuzzle(visitor.getPuzzle());
        if(visitor.getDirection()!=null) checkVisitor.setDirection(visitor.getDirection());
        boolean result = visitorService.updateById(checkVisitor);
        return result
                ? R.success("更新成功")
                : R.error("更新失败");
    }

    @GetMapping("/{visitor-id}/records")
    @UserLoginToken
    public R getRecords(@PathVariable(value = "visitor-id") Integer visitorId,
                                             @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                             @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize){
        if(!UserUtils.checkSelfOrBack(visitorId)) return R.auth_error();
        log.info("visitorId:{}", visitorId);

        if(visitorService.getById(visitorId)==null||visitorService.getById(visitorId).getIsDisabled()==1){
            return R.resource_error();
        }
        List<RecordListDTO> resultInfo = recordService.getRecords(visitorId,pageSize,pageNum);
        int total = resultInfo.size();
        int pages = PageUtil.totalPage(total, pageSize);
        int fromIndex = (pageNum-1)*pageSize;
        int toIndex = pageNum*pageSize>total?total:pageNum*pageSize;
        if(total==0){
            log.info("no records found");
            return R.success(new RecordPage<RecordDTO>(new ArrayList<RecordDTO>(), pages, total));
        }
        if(pageNum>pages){
            return R.success(new RecordPage<RecordDTO>(new ArrayList<RecordDTO>(), pages, total));
        }
        List<RecordListDTO> subList = resultInfo.subList(fromIndex, toIndex);
        RecordPage<RecordListDTO> resPage = new RecordPage<RecordListDTO>(subList, pages, total);
        return R.success(resPage);
    }

    /**
     * 修改访客权限
     * 用于：
     *  管理员
     * @param visitorId
     * @return
     */
    @PutMapping("/{visitor-id}/permission")
    @AdminToken
    public R updatePermission(@PathVariable(value = "visitor-id") Integer visitorId){
        log.info("visitorId:{}", visitorId);
        Visitor visitor = visitorService.getById(visitorId);
        if(visitor==null){
            return R.resource_error();
        }
        visitor.setIsDisabled((byte) (visitor.getIsDisabled()==0?1:0));
        boolean result = visitorService.updateById(visitor);
        return result
                ? R.success("更新成功")
                : R.error("更新失败");
    }

    @Deprecated
    @PostMapping("/test/login")
    public R testLogin(){
        R res=visitorService.testLogin();
        return res;
    }
}