package com.example.heart_field.controller;

import cn.hutool.core.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.heart_field.common.R;
import com.example.heart_field.constant.RegexPattern;
import com.example.heart_field.dto.WxLoginDTO;
import com.example.heart_field.dto.consultant.record.RecordListDTO;
import com.example.heart_field.dto.consultant.record.RecordPage;
import com.example.heart_field.entity.User;
import com.example.heart_field.entity.Visitor;
import com.example.heart_field.mapper.UserMapper;
import com.example.heart_field.mapper.VisitorMapper;
import com.example.heart_field.param.VisitorUpdateParam;
import com.example.heart_field.param.WxLoginParam;
import com.example.heart_field.service.RecordService;
import com.example.heart_field.service.VisitorService;
import com.example.heart_field.utils.TokenUtil;
import com.example.heart_field.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Pattern;

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
    private UserMapper userMapper;

    @Autowired
    private VisitorMapper visitorMapper;

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
   // @ExceptVisitorToken
    public R<Page> page(@RequestParam(value = "searchValue", required = false) String searchValue,
                        @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                        @RequestParam(value = "pageNum", required = false,defaultValue = "1") int pageNum) {
        log.info("searchValue:{}", searchValue);
        log.info("pageSize:{},pageNum:{}", pageSize, pageNum);
        if(pageNum<1||pageSize<1){
            return R.argument_error("分页参数不合法");
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
    //@UserLoginToken
    public R<Visitor> getVisitorProfile(@PathVariable(value = "visitor-id") Integer visitorId) {
        log.info("visitorId:{}", visitorId);
        //if(!UserUtils.checkSelfOrBack(visitorId)) return R.auth_error();
        Visitor visitor = visitorService.getById(visitorId);
        Integer type=TokenUtil.getTokenUser().getType();
        //type为0是Visitor，1是Consultant，2是Admin，3是Supervisor
        if(type==0){
            return (visitor==null||visitor.getIsDisabled()==1)
                    ? R.error("本账号已被禁用")
                    : R.success(visitor);
        }
        else{
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
//    @UserLoginToken
    public R updateVisitorProfile(@PathVariable(value = "visitor-id") Integer visitorId,
                                  @RequestBody VisitorUpdateParam visitor) {
        //if(!UserUtils.checkSelfOrAdmin(visitorId)) return R.auth_error();
        Visitor realVisitor = visitorMapper.selectById(visitorId);
        log.info("checkVisitor:{}", realVisitor);
        if(realVisitor==null||realVisitor.getIsDisabled()==1){
            return R.resource_error();
        }
        log.info("visitorId:{}", visitorId);
        log.info("visitor:{}", visitor);
        String newPhone = visitor.getPhone();
        String emergencyPhone = visitor.getEmergencyPhone();
        Pattern phonePattern = Pattern.compile(RegexPattern.MOBILE_PHONE_NUMBER_PATTERN);
        //手机号码格式校验
        if(newPhone==emergencyPhone||!phonePattern.matcher(newPhone).matches()||!phonePattern.matcher(emergencyPhone).matches()){
            return R.argument_error("请输入正确的手机号码");
        }
        realVisitor.setUsername(visitor.getUsername());
        realVisitor.setName(visitor.getName());
        realVisitor.setEmergencyName(visitor.getEmergencyName());
        realVisitor.setPhone(newPhone);
        realVisitor.setEmergencyPhone(emergencyPhone);
        realVisitor.setGender(visitor.getGender().byteValue());
        boolean result=visitorService.updateById(realVisitor);
        if(result==false){
            return R.error("更新失败");
        }else{
            User user = userMapper.selectOne(new QueryWrapper<User>().eq("type", 0).eq("user_id",visitorId));
            user.setPhone(newPhone);
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
    //@UserLoginToken
    public R<Visitor> getPsychArchive(@PathVariable(value = "visitor-id") Integer visitorId) {
        //if(!UserUtils.checkSelfOrAdmin(visitorId)) return R.auth_error();
        log.info("visitorId:{}", visitorId);
        Visitor visitor = visitorService.getById(visitorId);
        return visitor==null
                ? R.error("该咨询师不存在")
                : R.success(visitor);
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
    //    @UserLoginToken
    @PutMapping("/{visitor-id}/psych-archive")
    public R updatePcychArchive(@PathVariable(value = "visitor-id") Integer visitorId,
                                         @RequestBody Visitor visitor){
        //if(!UserUtils.checkSelfOrAdmin(visitorId)) return R.auth_error();
        Visitor checkVisitor = visitorService.getById(visitorId);
        if(checkVisitor==null){
            return R.resource_error();
        }
        log.info("visitorId:{}", visitorId);
        log.info("visitor:{}", visitor);
        visitor.setId(visitorId);
        boolean result = visitorService.updateById(visitor);
        return result
                ? R.success("更新成功")
                : R.error("更新失败");

    }

    /**
     * todo:分页待测试
     * 查看访客的咨询记录
     * 用于：
     *  -访客本人
     *  -管理端
     * @param visitorId
     * @param state
     * @return
     */
    @GetMapping("/{visitor-id}/records")
    //@UserLoginToken
    public R getRecords(@PathVariable(value = "visitor-id") String visitorId,
                                             @RequestParam(value = "recordState", required = false) String state,
                                             @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                             @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize){
        //if(!UserUtils.checkSelfOrBack(visitorId)) return R.auth_error();
        log.info("visitorId:{}", visitorId);
        if(state!=null&&!state.equals("0")&&!state.equals("1")&&!state.equals("2")){
            return R.argument_error("recordState参数错误");
        }
        if(visitorService.getById(visitorId)==null||visitorService.getById(visitorId).getIsDisabled()==1){
            return R.resource_error();
        }
        List<RecordListDTO> resultInfo = recordService.getRecords(visitorId,state,pageSize,pageNum);
        int pages = PageUtil.totalPage(resultInfo.size(), pageSize);
        Page<RecordListDTO> resPage = new Page<RecordListDTO>(pageNum, pageSize, pages).setRecords(resultInfo);
        RecordPage<RecordListDTO> res =new RecordPage(resPage,pages);
        return R.success(res);
    }

    /**
     * 修改访客权限
     * 用于：
     *  管理员
     * @param visitorId
     * @return
     */
    @PutMapping("/{visitor-id}/permission")
    //@AdminToken
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

    @PostMapping("/test/login")
    public R testLogin(){
        R res=visitorService.testLogin();
        return res;
    }

}
