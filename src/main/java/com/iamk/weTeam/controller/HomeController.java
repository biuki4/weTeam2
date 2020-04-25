package com.iamk.weTeam.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iamk.weTeam.common.UnicomResponseEnums;
import com.iamk.weTeam.common.WeChatProperties;
import com.iamk.weTeam.common.annotation.PassToken;
import com.iamk.weTeam.common.utils.*;
import com.iamk.weTeam.model.entity.Academy;
import com.iamk.weTeam.model.entity.Admin;
import com.iamk.weTeam.model.entity.User;
import com.iamk.weTeam.repository.AcademyRepository;
import com.iamk.weTeam.repository.AdminRepository;
import com.iamk.weTeam.repository.UserAttentionRepository;
import com.iamk.weTeam.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class HomeController {

    @Resource
    SessionHelper sessionHelper;
    @Resource
    UserRepository userRepository;
    @Autowired
    WeChatProperties weChatProperties;
    @Resource
    UserAttentionRepository userAttentionRepository;
    @Autowired
    RedisUtil redisUtil;
    @Resource
    AcademyRepository academyRepository;
    @Resource
    AdminRepository adminRepository;


    @PassToken
    @PostMapping("/login")
    public ResponseEntity login(@RequestParam("username") String username,
                                @RequestParam("password") String password, HttpSession session){
        try {
            // User user = sessionHelper.login(session, username, password);
            //User user = userRepository.findByUsername(username);
            JSONObject jsonObject=new JSONObject();
            User user = userRepository.findByUsernameAndPassword(username, password);
            if(user != null) {
                // String token = tokenService.getToken(user);
                // jsonObject.put("token", token);
                jsonObject.put("user", user);
                System.out.println(jsonObject);
                return ResponseEntity.ok().body(BasicResponse.ok().message("登录成功").data(jsonObject));
            }else {
                return ResponseEntity.ok().body(ResultUtil.error("400", "账号或密码错误！"));
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(BasicResponse.fail().message(e.getMessage()));
        }
    }

    @PassToken
    @PostMapping("/wxLogin")
    public ResultUtil wxLogin(@RequestParam("code") String code,
                              @RequestParam("data") String data){
        JSONObject jsonObject = new JSONObject();
        try {
            // try {
            JSONObject dataJson = JSON.parseObject(data);
            // System.out.println(data);
            // System.out.println(dataJson);
            // 发送请求获取session_key openId
            Map<String, String> params = new HashMap<>();
            //小程序appId
            params.put("appid", weChatProperties.APPID);
            //小程序secret
            params.put("secret", weChatProperties.APPSECRET);
            //小程序端返回的code
            params.put("js_code", code);
            //默认参数
            params.put("grant_type", "authorization_code");
            // 发送请求获取openId 、 sessionKey
            JSONObject sessionKey_openId = JSON.parseObject(HttpClientUtil.doPost(weChatProperties.URL, params));
            // openId
            String openId = sessionKey_openId.getString("openid");
            // sessionKey
            String sessionKey = sessionKey_openId.getString("session_key");
            // 判断是否是新用户
            User user = userRepository.findByOpenId(openId);
            String token = "";
            if(user == null) {
                // 用户不存在
                String nickname = dataJson.getString("nickName");
                String avatarUrl = dataJson.getString("avatarUrl");
                String gender = dataJson.getString("gender");
                user = new User();
                user.setNickname(nickname);
                user.setOpenId(openId);
                user.setGender(Integer.parseInt(gender));
                user.setShowMe(0);
                user.setAvatarUrl(avatarUrl);
                user.setCreateTime(new Date());
                user.setLoginLastTime(new Date());
                user.setLoginEnable(1);
                user.setUserViews(0);
                // user.setUserType(3);
                System.out.println("create: " + user);
                userRepository.save(user);
                // 将token和用户信息存入redis   有效期24h
                Integer userId = userRepository.findIdByOpenId(openId);
                token = JwtUtils.createJWT(Integer.toString(userId), openId);
                // redis 键:  user:userId:xx:  值: token
                String key = RedisKeyUtil.createKey("login:user", "userId", userId);
                redisUtil.set(key, token, 60*60*24*7);
                // if(redisUtil.hasKey(key)) {
                //     System.out.println("123");
                // }else {
                //     System.out.println("456");
                // }
            }else {
                // 用户存在
                if(user.getLoginEnable() == 0) {    // 禁止登录
                    return new ResultUtil(UnicomResponseEnums.FAIL_FORBID_LOGIN);
                }
                // 更新数据
                user.setLoginLastTime(new Date());
                userRepository.save(user);
                // 更新redis有效期
                String key = RedisKeyUtil.createKey("login:user", "userId", user.getId());
                // if(redisUtil.hasKey(key)){
                // TODO 生成的token也有过期时间，目前解决方案是每次都生成一个新token，正确的解决方案是refreshToken机制解决
                // token = (String) redisUtil.get(key);
                token = JwtUtils.createJWT(Integer.toString(user.getId()), openId);
                redisUtil.set(key, token, 60*60*24*7);
                // redisUtil.expire(key, 60*60*24*7);
                // }else {
                //     token = JwtUtils.createJWT(Integer.toString(user.getId()), openId);
                //     redisUtil.set(key, token, 60*60*24*7);
                // }
            }
            // 封装响应消息
            // 关注
            // int attention = userAttentionRepository.countByUserId(user.getId());
            // 粉丝
            // int fans = userAttentionRepository.countByAttentionId(user.getId());
            // 设置学院信息
            if(user.getAcademyId() != null){
                Academy academy = academyRepository.findById(user.getAcademyId()).orElse(null);
                user.setAcademy(academy.getName());
            }
            // admin
            Admin admin = adminRepository.findByUserId(user.getId());
            user.setUserType(3);
            if(admin!=null) {
                user.setUserType(admin.getUserType());
            }
            jsonObject.put("token", token);
            jsonObject.put("user", user);
            // jsonObject.put("attention", attention);
            // jsonObject.put("fans", fans);

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return ResultUtil.success(jsonObject);

    }
}
