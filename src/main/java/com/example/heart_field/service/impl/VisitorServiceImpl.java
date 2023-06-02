package com.example.heart_field.service.impl;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.heart_field.common.R;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.constant.TypeConstant;
import com.example.heart_field.dto.WxLoginDTO;
import com.example.heart_field.dto.WxUserInfo;
import com.example.heart_field.entity.Admin;
import com.example.heart_field.entity.Consultant;
import com.example.heart_field.entity.User;
import com.example.heart_field.entity.Visitor;
import com.example.heart_field.mapper.UserMapper;
import com.example.heart_field.mapper.VisitorMapper;
import com.example.heart_field.param.WxLoginParam;
import com.example.heart_field.service.VisitorService;
import com.example.heart_field.tokens.TokenService;
import com.example.heart_field.utils.TencentCloudImUtil;
import com.example.heart_field.utils.TokenUtil;
import com.example.heart_field.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import cn.hutool.http.HttpUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.AlgorithmParameterSpec;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class VisitorServiceImpl extends ServiceImpl<VisitorMapper, Visitor> implements VisitorService {
    @Value("${weixin.appid}")
    private String appid;

    @Value("${weixin.secret}")
    private String secret;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    UserUtils userUtils;

    @Autowired
    TokenService tokenService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    private TencentCloudImUtil tencentCloudImUtil;

    private String REDIS_KEY = "wx_session_id";

    public String getSessionId(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid={0}&secret={1}&js_code={2}&grant_type=authorization_code";
        String replaceUrl = url.replace("{0}", appid).replace("{1}", secret).replace("{2}", code);
        String res = HttpUtil.get(replaceUrl);
        log.info("发送链接后获得的数据{}",res);
        String s = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(REDIS_KEY + s, res);
        return s;
    }

    public String wxDecrypt(String encryptedData, String sessionId, String vi) throws Exception {
        // 开始解密
        String json =  redisTemplate.opsForValue().get(REDIS_KEY + sessionId);
        log.info("信息："+json);
        JSONObject jsonObject = JSON.parseObject(json);

        String sessionKey = (String) jsonObject.get("session_key");
        byte[] encData = cn.hutool.core.codec.Base64.decode(encryptedData);
        byte[] iv = cn.hutool.core.codec.Base64.decode(vi);
        byte[] key = Base64.decode(sessionKey);
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        return new String(cipher.doFinal(encData), "UTF-8");
    }

    public String getOpenId(String sessionId) throws Exception {
        /*
        信息：{"session_key":"MgP6m7jVGCL97ImUJ1ZiPw==",
        "openid":"obG6z5Dos51CvHxZXHiqi4YZx4l8"}
        */
        String json = redisTemplate.opsForValue().get(REDIS_KEY + sessionId);
        JSONObject object= JSONObject.parseObject(json);
        return (String) object.get("openid");

    }

    //生成随机用户名，数字和字母组成,
    public String getStringRandom(int length) {

        StringBuilder val = new StringBuilder();
        Random random = new Random();

        //参数length，表示生成几位随机数
        for (int i = 0; i < length; i++) {

            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if ("char".equalsIgnoreCase(charOrNum)) {
                //输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val.append((char) (random.nextInt(26) + temp));
            } else {
                val.append(random.nextInt(10));
            }
        }
        return val.toString();
    }


    public R testLogin(){
        String openId ="just-test";
        String wxRes="{\"nickName\":\"ZHY'\",\"gender\":0,\"language\":\"zh_CN\",\"city\":\"\",\"province\":\"\",\"country\":\"\",\"avatarUrl\":\"https://thirdwx.qlogo.cn/mmopen/vi_32/6X3UWePYiaUzbaGXplicYh5gQkElq3RYZgsCNtterro8V5V6o0tSsQYHH1S7Z5loe59zX7uWyf74PDS6FULWnCcw/132\",\"watermark\":{\"timestamp\":1685671920,\"appid\":\"wxb9f9c2c27af57638\"}}";
        WxUserInfo wxUserInfo = JSON.parseObject(wxRes,WxUserInfo.class);
        log.info("用户信息：{}"+wxUserInfo);
        //根据openId查用户是否存在
        LambdaQueryWrapper<Visitor> query= Wrappers.lambdaQuery();
        query.eq(Visitor::getOpenId,openId);
        Visitor visitor = baseMapper.selectOne(query);
        if(visitor != null){//用户已经存在
            log.info("用户已经存在，id为：{}"+visitor.getId());
            return R.success("just a test,success");
        }else{
            //用户不存在，创建用户
            visitor =Visitor.builder()
                    .avatar(wxUserInfo.getAvatarUrl())
                    .openId(openId)
                    .gender(Byte.parseByte(wxUserInfo.getGender()))
                    .username(wxUserInfo.getNickName())
                    .createTime(LocalDateTime.now())
                    .isDisabled((byte) 0)
                    .updateTime(LocalDateTime.now())
                    .build();

            baseMapper.insert(visitor);
            log.info("新建visitor，id为：{}"+visitor.getId());

            //同步user，获取token
            User user=userUtils.saveUser(visitor);

            String token = tokenService.getToken(user);
            log.info("token为：{}"+token);

            //创建im账号
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Identifier",user.getType().toString()+"_"+user.getUserId().toString());
            jsonObject.put("Nick",visitor.getUsername());
            jsonObject.put("FaceUrl",visitor.getAvatar());
            String identifier = user.getType().toString()+"_"+user.getUserId().toString();
            boolean isSuccess = tencentCloudImUtil.accountImport(identifier);
            log.info("腾讯IM导入账号结果：{}"+isSuccess);
            if(!isSuccess){
                this.baseMapper.delete(new LambdaQueryWrapper<Visitor>().eq(Visitor::getId,visitor.getId()));
                userUtils.deleteUser(user);
                return R.error("腾讯IM导入账号失败");
            }
            WxLoginDTO res=WxLoginDTO.builder()
                    .accessToken(token)
                    .fstLogin(false)
                    .chatUserId(identifier)
                    .chatUserSig(tencentCloudImUtil.getTxCloudUserSig())
                    .userId(visitor.getId())
                    .userInfo(visitor)
                    .build();
            return R.success(res);
        }

    }

    /**
     * 用户登录，通过openId判断是否存在，同步添加到user表中
     * @param loginParam
     * @return
     */

    @Override
    public R authLogin(WxLoginParam loginParam) {
        try{
            String sessionId = getSessionId(loginParam.getCode());
            String openId = getOpenId(sessionId);
            //获取用户信息
            boolean success = tryDecrypt(sessionId);
            if(!success){
                return R.error(errorCodeDecyrpt(sessionId),errorMsgDecyrpt(sessionId));
            }
            String wxRes = wxDecrypt(loginParam.getEncryptedData(), sessionId, loginParam.getIv());
            log.info("登录信息：{}"+wxRes);
            //String wxRes="{\"nickName\":\"ZHY'\",\"gender\":0,\"language\":\"zh_CN\",\"city\":\"\",\"province\":\"\",\"country\":\"\",\"avatarUrl\":\"https://thirdwx.qlogo.cn/mmopen/vi_32/6X3UWePYiaUzbaGXplicYh5gQkElq3RYZgsCNtterro8V5V6o0tSsQYHH1S7Z5loe59zX7uWyf74PDS6FULWnCcw/132\",\"watermark\":{\"timestamp\":1685671920,\"appid\":\"wxb9f9c2c27af57638\"}}";
            WxUserInfo wxUserInfo = JSON.parseObject(wxRes,WxUserInfo.class);
            log.info("用户信息："+wxUserInfo);
            //根据openId查用户是否存在
            LambdaQueryWrapper<Visitor> query= Wrappers.lambdaQuery();
            query.eq(Visitor::getOpenId,openId);
            Visitor visitor = baseMapper.selectOne(query);
            if(visitor != null){//用户已经存在
                log.info("此次登陆为fastLogin");
                //更新用户信息
                visitor.setOpenId(openId);
                visitor.setUsername(wxUserInfo.getNickName());
                visitor.setAvatar(wxUserInfo.getAvatarUrl());
                visitor.setGender(Byte.parseByte(wxUserInfo.getGender()));
                baseMapper.updateById(visitor);

                //获取token
                LambdaQueryWrapper<User> queryWrapper= Wrappers.lambdaQuery();
                queryWrapper.eq(User::getType, TypeConstant.VISITOR).eq(User::getUserId,visitor.getId());
                User user=userMapper.selectOne(queryWrapper);
                String token = tokenService.getToken(user);
                log.info("token为：{}"+token);

                //获取im相关信息，更新im信息
                String identifier = user.getType().toString()+"_"+user.getUserId().toString();
                String sex = null;
                switch (visitor.getGender()){
                    case 0:
                        sex = "女";
                        break;
                    case 1:
                        sex = "男";
                        break;
                    default:
                        break;
                }
                tencentCloudImUtil.updateAccount(identifier, visitor.getUsername(), visitor.getAvatar(),sex);

                WxLoginDTO res=WxLoginDTO.builder()
                        .accessToken(token)
                        .fstLogin(true)
                        .chatUserId(identifier)
                        .chatUserSig(tencentCloudImUtil.getTxCloudUserSig())
                        .userId(visitor.getId())
                        .userInfo(visitor)
                        .build();
                return R.success(res);
            }else{
                //用户不存在，创建用户
                visitor =Visitor.builder()
                        .avatar(wxUserInfo.getAvatarUrl())
                        .openId(openId)
                        .gender((byte) (wxUserInfo.getGender().equals("0")?1:0))
                        .username(wxUserInfo.getNickName())
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .isDisabled((byte)0)
                        .build();

                baseMapper.insert(visitor);
                log.info("新建visitor，id为：{}"+visitor.getId());

                //同步user，获取token
                User user=userUtils.saveUser(visitor);

                String token = tokenService.getToken(user);
                log.info("token为：{}"+token);

                //创建im账号
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Identifier",user.getType().toString()+"_"+user.getUserId().toString());
                jsonObject.put("Nick",visitor.getName());
                jsonObject.put("FaceUrl",visitor.getAvatar());
                String identifier = user.getType().toString()+"_"+user.getUserId().toString();
                boolean isSuccess = tencentCloudImUtil.accountImport(identifier);
                if(!isSuccess){
                    this.baseMapper.delete(new LambdaQueryWrapper<Visitor>().eq(Visitor::getId,visitor.getId()));
                    userUtils.deleteUser(user);
                    return R.error("腾讯IM导入账号失败");
                }
                WxLoginDTO res=WxLoginDTO.builder()
                        .accessToken(token)
                        .fstLogin(false)
                        .chatUserId(identifier)
                        .chatUserSig(tencentCloudImUtil.getTxCloudUserSig())
                        .userId(visitor.getId())
                        .userInfo(visitor)
                        .build();
                return R.success(res);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return R.login_error("登录失败");
    }

    private boolean tryDecrypt(String sessionId) {
        String json =  redisTemplate.opsForValue().get(REDIS_KEY + sessionId);
        log.info("信息："+json);
        JSONObject jsonObject = JSON.parseObject(json);
        if (jsonObject.get("errcode") != null) {
            String errorCode = jsonObject.get("errcode").toString();
            String errorMsg= jsonObject.get("errmsg").toString();
            return false;
        }
        return true;
    }

    private String errorMsgDecyrpt(String sessionId){
        String json =  redisTemplate.opsForValue().get(REDIS_KEY + sessionId);
        log.info("信息："+json);
        JSONObject jsonObject = JSON.parseObject(json);
        String errorMsg= jsonObject.get("errmsg").toString();
        return errorMsg;
    }

    private Integer errorCodeDecyrpt(String sessionId){
        String json =  redisTemplate.opsForValue().get(REDIS_KEY + sessionId);
        log.info("信息："+json);
        JSONObject jsonObject = JSON.parseObject(json);
        Integer errorCode= Integer.parseInt(jsonObject.get("errcode").toString());
        return errorCode;
    }


}
