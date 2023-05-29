package com.example.heart_field.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.dto.UserLoginDTO;
import com.example.heart_field.entity.*;
import com.example.heart_field.mapper.*;
import com.example.heart_field.param.UserLoginParam;
import com.example.heart_field.service.UserService;
import com.example.heart_field.tokens.TokenService;
import com.example.heart_field.utils.Md5Util;
import com.example.heart_field.utils.RandomUtil;
import com.example.heart_field.utils.TokenUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    public Long MIN_SIZE=0L;
    public Long MAX_SIZE=1024*1024*10L;
    @Autowired
    private VisitorMapper visitorMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private SupervisorMapper supervisorMapper;

    @Autowired
    private ConsultantMapper consultantMapper;

    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;  //注入bcryct加密

    @Autowired
    private TokenService tokenService;


    public String upload(MultipartFile avatar) throws IOException {
        Long size = avatar.getSize();
        if(size<=MIN_SIZE||size>MAX_SIZE){
            throw new IOException("文件大小不符合要求");
        }
        // 首先校验图片格式
        List<String> imageType = Lists.newArrayList("jpg","jpeg", "png", "bmp", "gif");
        // 获取文件名，带后缀
        String originalFilename = avatar.getOriginalFilename();
        // 获取文件的后缀格式
        String fileSuffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (imageType.contains(fileSuffix)) {
            // 只有当满足图片格式时才进来，重新赋图片名，防止出现名称重复的情况
            String newFileName = RandomUtil.genRandomNum(10) + originalFilename;
            // 该方法返回的为当前项目的工作目录，即在哪个地方启动的java线程
            String dirPath = System.getProperty("user.dir");
            String path = File.separator + "uploadImg" + File.separator + newFileName;
            File destFile = new File(dirPath + path);
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            try {
                avatar.transferTo(destFile);
                // 将相对路径返回给前端
                return path;
            } catch (IOException e) {
                log.error("文件上传失败");
                throw new IOException("文件上传失败，请检查文件格式");
            }
        } else {
            // 非法文件
            log.error("文件后缀名非法");
            throw new IOException("文件上传失败，请检查文件格式");
        }
    }
    @Override
    public ResultInfo<String> uploadAvatar(MultipartFile avatar) throws Exception {
        try {
            String url = upload(avatar);
            if (url != null) {
                User user = TokenUtil.getTokenUser();
                Integer type = user.getType();
                Integer id = user.getUserId();
                switch (type) {
                    case 0:
                        Visitor visitor = visitorMapper.selectById(id);
                        visitor.setAvatar(url);
                        visitorMapper.updateById(visitor);
                        break;
                    case 2:
                        Admin admin = adminMapper.selectById(id);
                        admin.setAvatar(url);
                        adminMapper.updateById(admin);
                        break;
                    case 1:
                        Consultant consultant = consultantMapper.selectById(id);
                        consultant.setAvatar(url);
                        consultantMapper.updateById(consultant);
                        break;
                    case 3:
                        Supervisor supervisor = supervisorMapper.selectById(id);
                        supervisor.setAvatar(url);
                        supervisorMapper.updateById(supervisor);
                        break;
                    default:
                        break;
                }
                return ResultInfo.success(url);
            }
            return ResultInfo.error("上传失败，请重试");
        }catch (Exception e){
            log.error("上传失败");
            throw new Exception("上传失败，请重试");

        }
    }

    @Override
    public ResultInfo<UserLoginDTO> login(UserLoginParam loginParam) {
        LambdaQueryWrapper<User> queryWrapper= Wrappers.lambdaQuery();
        queryWrapper.eq(User::getPhone,loginParam.getPhone());
        Integer count=this.baseMapper.selectCount(queryWrapper);
        if(count==0){
            return ResultInfo.error("用户不存在");
        }
        if(count>1){
            log.error("DB中存在多条相同手机号的账号，phone = " + loginParam.getPhone());
        }
        User user = this.baseMapper.selectOne(queryWrapper);
        log.info(user.getPhone());
        if (!bCryptPasswordEncoder.matches(loginParam.getPassword(),user.getPassword())){
            return ResultInfo.error("用户名或密码错误");
        }
        String token = tokenService.getToken(user);
        UserLoginDTO dto = UserLoginDTO.builder()
                .type(user.getType())
                .id(user.getUserId())
                .token(token)
                .build();
        return ResultInfo.success(dto);

    }
}
