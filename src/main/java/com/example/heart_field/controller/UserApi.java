package com.example.heart_field.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.heart_field.common.R;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.dto.user.UserLoginDTO;
import com.example.heart_field.entity.*;
import com.example.heart_field.mapper.AdminMapper;
import com.example.heart_field.mapper.ConsultantMapper;
import com.example.heart_field.mapper.SupervisorMapper;
import com.example.heart_field.mapper.VisitorMapper;
import com.example.heart_field.param.UserLoginParam;
import com.example.heart_field.param.VisitorAvatarParam;
import com.example.heart_field.service.AdminService;
import com.example.heart_field.service.UserService;
import com.example.heart_field.tokens.TokenService;
import com.example.heart_field.tokens.UserLoginToken;
import com.example.heart_field.utils.TencentCOSUtils;
import com.example.heart_field.utils.TencentCloudImUtil;
import com.example.heart_field.utils.TokenUtil;
import com.example.heart_field.utils.UserUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
//import org.apache.commons.codec.binary.Base64;
@RestController
@Slf4j
public class UserApi {
    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private VisitorMapper visitorMapper;
    @Autowired
    private TencentCloudImUtil tencentCloudImUtil;
    @Autowired
    AdminService adminService;
    @Autowired
    private TencentCOSUtils tencentCOSUtils;
    @Autowired
    private ConsultantMapper consultantMapper;
    @Autowired
    private SupervisorMapper supervisorMapper;
    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;  //注入bcryct加密


    /**
     * 登录时不做手机号码格式校验
     * @param loginParam
     * @return
     */
    @PostMapping("/backend/login")
    public R<UserLoginDTO> userLogin(@RequestBody UserLoginParam loginParam){
        ResultInfo<UserLoginDTO> loginInfo = userService.login(loginParam);
        return loginInfo.isRight()
                ? R.success(loginInfo.getData())
                : R.login_error("用户不存在或密码错误");
    }

    /*
    测试登录
     */
    @Deprecated
    @GetMapping("/login")
    public Object login(@RequestBody User user, HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject();
        User user_r = userUtils.getUser(user);
        //如果是访客的话就不需要进行校验
        if(!(user.getType()==0)){
            if (!bCryptPasswordEncoder.matches(user.getPassword(),user_r.getPassword())) {
                jsonObject.put("message", "登录失败,密码错误");
                return jsonObject;
            }
        }
        String token = tokenService.getToken(user_r);
        jsonObject.put("token", token);
        Cookie cookie = new Cookie("token", token);
        cookie.setPath("/");
        response.addCookie(cookie);
        return jsonObject;
    }

    /***
     * 这个请求需要验证token才能访问
     * 
     * @author: MRC
     * @date 2019年5月27日 下午5:45:19
     * @return String 返回类型
     */
    @Deprecated
    @UserLoginToken
    @GetMapping("/getMessage")
    public String getMessage() {

        // 取出token中带的用户id 进行操作
        System.out.println(TokenUtil.getTokenUser());

        return "你已通过验证";
    }

    /**访客上传头像
     * @throws Exception
     */
    @UserLoginToken
    @PostMapping("/visitors/avatar")
    public R<String> uploadVisitorAvatar(@RequestBody VisitorAvatarParam uploadAvatar) throws Exception {
        String avatar = uploadAvatar.getAvatar();
        //base64字符中若含有“data:image/jpeg;base64,”，截去这一段；
//        String titleDelete = "data:image/jpeg;base64,";
//        avatar = avatar.replace(titleDelete,"");
        if(avatar.contains("base64"))
        {
           // log.info("判断成功");
            int location = avatar.indexOf("base64");
            //log.info("location:{}",location);
            avatar = avatar.substring(location+7);
        }
        //log.info("avatar-base64:{}",avatar);
        //base64转为文件字节数组
        byte[] imageBytes = Base64.getDecoder().decode(avatar);
        //创建临时文件
        String tempFilePath = "temp_Avatar.jpg";
        Path tempPath = Paths.get(tempFilePath);
        try{
            Files.write(tempPath,imageBytes);
        }catch (IOException e){
            e.printStackTrace();
            return R.error("base64数据转为文件出错");
        }
        //上传至腾讯云IM
        List<String> imageType = Lists.newArrayList("jpg","jpeg", "png", "bmp", "gif");
        String fileSuffix = tempFilePath.substring(tempFilePath.lastIndexOf(".") + 1).toLowerCase();
        log.info(fileSuffix + "文件格式");
        String imageUrl=null;
        if (imageType.contains(fileSuffix)){
            log.info("文件上传，文件名：{}",tempFilePath);
            String url = tencentCOSUtils.uploadImageBytes(imageBytes,tempFilePath);
            log.info("文件上传完成，文件访问的url为：{}",url);
            User user = TokenUtil.getTokenUser();
            switch (user.getType()) {
                case 0:
                    Visitor visitor = visitorMapper.selectById(user.getUserId());
                    visitor.setAvatar(url);
                    visitorMapper.updateById(visitor);
                    String identifier = "0_"+visitor.getId().toString();
                    String name = visitor.getUsername();
                    String avatar_url = url;
                    String sex = null;
                    switch (visitor.getGender()){
                        case 0:
                            sex = "女";
                            break;
                        case 1:
                            sex = "男";
                            break;
                        default:
                            sex = "未知";
                            break;
                    }
                    Boolean isSuccess=tencentCloudImUtil.updateAccount(identifier,name,avatar_url,sex);
                    if(!isSuccess){
                        return R.error("腾讯IM更新账号失败");
                    }
                    break;
                default:
                    return R.error("非访客不可使用");
            }
            //return R.success(url);
            imageUrl = url;
        }else{
            return R.argument_error("图片格式错误");
        }

        //删除临时文件
        try{
            Files.deleteIfExists(tempPath);
            return R.success(imageUrl,"头像上传成功");
        }catch (IOException e){
            e.printStackTrace();
            String msg = "头像上传成功，但本地临时文件未删除，请手动删除文件，文件路径为"+tempPath;
            return R.error(msg);
        }

    }
    @UserLoginToken
    @PostMapping("/avatar")
    public R<String> uploadAvatar(@RequestBody MultipartFile image) throws Exception {
        // 校验图片格式
        List<String> imageType = Lists.newArrayList("jpg","jpeg", "png", "bmp", "gif");
        // 获取文件名，带后缀
        String originalFilename = image.getOriginalFilename();
        // 获取文件的后缀格式
        String fileSuffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        log.info(fileSuffix + "文件格式");
        if (imageType.contains(fileSuffix)){
            log.info("文件上传，文件名：{}",image.getOriginalFilename());
            String url = tencentCOSUtils.upload(image);
            log.info("文件上传完成，文件访问的url为：{}",url);
            User user = TokenUtil.getTokenUser();
            switch (user.getType()) {
                case 0:
                    Visitor visitor = visitorMapper.selectById(user.getUserId());
                    visitor.setAvatar(url);
                    visitorMapper.updateById(visitor);
                    String identifier = "0_"+visitor.getId().toString();
                    String name = visitor.getUsername();
                    String avatar_url = url;
                    String sex = null;
                    switch (visitor.getGender()){
                        case 0:
                            sex = "女";
                            break;
                        case 1:
                            sex = "男";
                            break;
                        default:
                            sex = "未知";
                            break;
                    }
                    Boolean isSuccess=tencentCloudImUtil.updateAccount(identifier,name,avatar_url,sex);
                    if(!isSuccess){
                        return R.error("腾讯IM更新账号失败");
                    }
                    break;
                case 2:
                    Admin admin = adminMapper.selectById(user.getUserId());
                    admin.setAvatar(url);
                    adminMapper.updateById(admin);
                    identifier = "2_"+admin.getId().toString();
                    name = admin.getUsername();
                    avatar_url = url;
                    sex = "未知";
                    isSuccess=tencentCloudImUtil.updateAccount(identifier,name,avatar_url,sex);
                    if(!isSuccess){
                        return R.error("腾讯IM更新账号失败");
                    }
                    break;
                case 1:
                    Consultant consultant = consultantMapper.selectById(user.getUserId());
                    consultant.setAvatar(url);
                    consultantMapper.updateById(consultant);

                    identifier = "1_"+consultant.getId().toString();
                    name = consultant.getName();
                    avatar_url = url;
                    switch (consultant.getGender()){
                        case 0:
                            sex = "女";
                            break;
                        case 1:
                            sex = "男";
                            break;
                        default:
                            sex = "未知";
                            break;
                    }
                    isSuccess=tencentCloudImUtil.updateAccount(identifier,name,avatar_url,sex);
                    if(!isSuccess){
                        return R.error("腾讯IM更新账号失败");
                    }
                    break;
                case 3:
                    Supervisor supervisor = supervisorMapper.selectById(user.getUserId());
                    supervisor.setAvatar(url);
                    supervisorMapper.updateById(supervisor);
                    identifier = "3_"+supervisor.getId().toString();
                    name = supervisor.getName();
                    avatar_url = url;
                    switch (supervisor.getGender()){
                        case 0:
                            sex = "女";
                            break;
                        case 1:
                            sex = "男";
                            break;
                        default:
                            sex = "未知";
                            break;
                    }
                    isSuccess=tencentCloudImUtil.updateAccount(identifier,name,avatar_url,sex);
                    if(!isSuccess){
                        return R.error("腾讯IM更新账号失败");
                    }
                    break;
                default:
                    break;
            }
            return R.success(url);
        }else{
            return R.argument_error("图片格式错误");
        }
    }
}