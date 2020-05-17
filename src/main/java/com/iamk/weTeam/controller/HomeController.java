package com.iamk.weTeam.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iamk.weTeam.common.Enum.LoginEnum;
import com.iamk.weTeam.common.Enum.UnicomResponseEnums;
import com.iamk.weTeam.common.Enum.UserEnum;
import com.iamk.weTeam.common.config.MiniProgramConfig;
import com.iamk.weTeam.common.annotation.PassToken;
import com.iamk.weTeam.common.constant.UnionConstant;
import com.iamk.weTeam.common.expection.UnicomRuntimeException;
import com.iamk.weTeam.common.utils.*;
import com.iamk.weTeam.model.entity.Academy;
import com.iamk.weTeam.model.entity.Admin;
import com.iamk.weTeam.model.entity.UnionUser;
import com.iamk.weTeam.model.entity.User;
import com.iamk.weTeam.repository.*;
import com.iamk.weTeam.service.HomeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
    MiniProgramConfig miniProgramConfig;
    @Autowired
    RedisUtil redisUtil;
    @Resource
    AcademyRepository academyRepository;
    @Resource
    AdminRepository adminRepository;
    @Autowired
    HomeService homeService;
    @Resource
    UnionUserRepository unionUserRepository;


    /**
     * 微信授权登录
     * @param code
     * @param data
     * @param encryptedData
     * @param iv
     * @return
     */
    @PassToken
    @PostMapping("/wxLogin")
    public ResultUtil wxLogin2(@RequestParam("code") String code,
                              @RequestParam("data") String data,
                              @RequestParam("encryptedData") String encryptedData,
                              @RequestParam("iv") String iv){
        log.info("用户登录.......");
        JSONObject jsonObject = new JSONObject();
        try {
            // try {
            JSONObject dataJson = JSON.parseObject(data);
            // 发送请求获取session_key openId
            JSONObject sessionKey_openId = WeChatUtil.getSessionKeyOpenId(code, miniProgramConfig.APPID, miniProgramConfig.APPSECRET, miniProgramConfig.URL);
            // openId
            String openId = sessionKey_openId.getString("openid");
            // sessionKey
            String sessionKey = sessionKey_openId.getString("session_key");
            // 消息解密 获取unionId
            JSONObject userInfo = WeChatUtil.getUserInfo(encryptedData, sessionKey, iv);
            String unionId = (String) userInfo.get("unionId");
            log.info("unionId: " + unionId);
            // 方式二
            // String result = AesCbcUtil.decrypt(encryptedData, sessionKey, iv, "UTF-8");
            // String unionId = (String) JSON.parseObject(result).get("unionId");

            // 返回用户信息
            User user = userRepository.findByOpenId(openId);
            if(user == null) {
                // 新用户
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
                user.setLoginEnable(1);
                user.setUserViews(0);
            } else {
                // 老用户
                if(user.getLoginEnable().equals(0)) {    // 禁止登录
                    return new ResultUtil(UnicomResponseEnums.FAIL_FORBID_LOGIN);
                }
            }
            user.setUnionId(unionId);
            user.setLoginLastTime(new Date());
            user = userRepository.save(user);

            // 更新通过union获取的用户信息
            log.info(unionId);
            log.info(user.toString());
            if(StringUtils.isNotBlank(unionId)) {
                log.info("update user info by unionId");
                JSONObject userFromUnionId = MyUtils.getUserFromUnionId(UnionConstant.WEAPPSECRET, miniProgramConfig.APPID, unionId);
                log.info(String.valueOf(userFromUnionId));

                // 用户在i瓜大中存在
                // log.info(String.valueOf(userFromUnionId.get("bound_schoolAccount")));
                // if(StringUtils.isNotBlank(String.valueOf(userFromUnionId.get("bound_schoolAccount")))) {
                //     // isBoundNPU
                //     log.info(String.valueOf(userFromUnionId.get("bound_schoolAccount")));
                //     boolean bound_schoolAccount = (boolean) userFromUnionId.get("bound_schoolAccount");
                //
                //     // school_account
                //     JSONObject school_account = (JSONObject) JSONObject.toJSON(userFromUnionId.get("school_account"));
                //     log.info("school_account: " + school_account);
                //
                //     // unionUser
                //     UnionUser unionUser = unionUserRepository.findByUserId(user.getId());
                //
                //     // isBoundWeChat
                //     int isBoundWX = (boolean) userFromUnionId.get("bound_wechatOfficialAccount") ? 1 : 0;
                //     log.info("isBoundWX: " + isBoundWX);
                //
                //     user.setIsBoundWeChat(isBoundWX);
                //     // 仅更新首次登录，及已经绑定学校账号的用户
                //     if(bound_schoolAccount && unionUser==null) {
                //         log.info("update info from school account");
                //         String school_id = (String) school_account.get("school_id");
                //         log.info(school_id);
                //
                //         // username
                //         user.setUsername(school_id);
                //
                //         // grade
                //         user.setGrade(school_id.substring(0, 4));
                //
                //         // academy
                //         Academy a = academyRepository.findByName(school_account.get("college"));
                //
                //         if(a!=null) {
                //             user.setAcademyId(a.getId());
                //             log.info(a.toString());
                //         }
                //
                //         // 异步更新unionUser表
                //         homeService.update(user, userFromUnionId);
                //         user = userRepository.save(user);
                //     }
                // }
            }

            log.info("create token");
            String token = JwtUtils.createJWT(Integer.toString(user.getId()), openId);
            log.info("get token: " + token);

            // redis 键:  login:user:userId:xx:  值: token
            String key = RedisKeyUtil.createKey("login:user", "userId", user.getId());
            redisUtil.set(key, token, 60*60*24*7);

            // 返回学院信息
            log.info("user.getAcademyId(): " + user.getAcademyId());
            if(user.getAcademyId() != null){
                Academy academy = academyRepository.findById(user.getAcademyId()).orElse(null);
                user.setAcademy(academy.getName());
            }
            // isAdmin
            Admin admin = adminRepository.findByUserId(user.getId());
            user.setUserType(3);

            log.info("admin: " + admin);
            if(admin!=null) {
                user.setUserType(admin.getUserType());
            }

            jsonObject.put("token", token);
            jsonObject.put("user", user);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.success(jsonObject);
    }


    @PassToken
    @RequestMapping("/isLogin")
    public ResultUtil wxLogin2(HttpServletRequest httpServletRequest){
        log.info("in isLogin");
        // 是否登录
        String token = httpServletRequest.getHeader("token");
        if (token == null || "".equals(token)) {
            return ResultUtil.error(LoginEnum.LOGIN_HAS_NOT_LOGIN);
        }
        // 是否过期
        Integer userId = MyUtils.getUserIdFromToken(token);
        String key = RedisKeyUtil.createKey("login:user", "userId", userId);
        if(!redisUtil.hasKey(key)){
            return ResultUtil.error(LoginEnum.LOGIN_OVERDUE);
        }
        // 是否关注公众号
        String openId = MyUtils.getOpenIdFromToken(token);
        User user = userRepository.findByOpenId(openId);
        JSONObject jsonObject = new JSONObject();
        if(!StringUtils.isNotBlank(user.getUnionId())) {
            jsonObject.put("isBoundWeChat", 0);
            return ResultUtil.success(jsonObject);
        }
        JSONObject userFromUnionId = MyUtils.getUserFromUnionId(UnionConstant.WEAPPSECRET, miniProgramConfig.APPID, user.getUnionId());
        System.out.println("userFromUnionId: " + userFromUnionId);
        // 用户在i瓜大不存在
        if(!StringUtils.isNotBlank(String.valueOf(userFromUnionId.get("bound_schoolAccount")))) {
            jsonObject.put("isBoundWeChat", 0);
        } else {
            int isBoundWX = (boolean) userFromUnionId.get("bound_wechatOfficialAccount") ? 1 : 0;
            jsonObject.put("isBoundWeChat", isBoundWX);
        }
        return ResultUtil.success(jsonObject);
    }


    /**
     * 是否关注i瓜大
     * @return
     */
    @RequestMapping("/isBoundWx/{id}")
    public ResultUtil isBoundWx(@PathVariable Integer id) {
        log.info("isBoundWx?: " + id);
        User user = userRepository.findById(id).orElse(null);
        if(user==null) {
            return ResultUtil.error(UserEnum.USER_NO_USER);
        }
        JSONObject jsonObject = new JSONObject();
        if(!StringUtils.isNotBlank(user.getUnionId())) {
            jsonObject.put("isBoundWeChat", 0);
            return ResultUtil.success(jsonObject);
        }
        log.info("unionId: " + user.getUnionId());

        JSONObject userFromUnionId = MyUtils.getUserFromUnionId(UnionConstant.WEAPPSECRET, miniProgramConfig.APPID, user.getUnionId());
        System.out.println("userFromUnionId: " + userFromUnionId);

        if(!StringUtils.isNotBlank(String.valueOf(userFromUnionId.get("bound_schoolAccount")))) {
            jsonObject.put("isBoundWeChat", 0);
        } else {
            int isBoundWX = (boolean) userFromUnionId.get("bound_wechatOfficialAccount") ? 1 : 0;
            jsonObject.put("isBoundWeChat", isBoundWX);
        }
        System.out.println(jsonObject.toJSONString());
        return ResultUtil.success(jsonObject);
    }


    /**
     * 账号密码登录，目前未启用
     * @param username
     * @param password
     * @param session
     * @return
     */
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



    /**
     * 暂弃用
     * @param code
     * @param data
     * @param encryptedData
     * @param iv
     * @return
     */
    @PassToken
    @PostMapping("/wxLogin2")
    public ResultUtil wxLogin(@RequestParam("code") String code,
                              @RequestParam("data") String data,
                              @RequestParam("encryptedData") String encryptedData,
                              @RequestParam("iv") String iv){
        log.info("用户登录.......");
        JSONObject jsonObject = new JSONObject();
        try {
            // try {
            JSONObject dataJson = JSON.parseObject(data);
            // 发送请求获取session_key openId
            Map<String, String> params = new HashMap<>();
            //小程序appId
            params.put("appid", miniProgramConfig.APPID);
            //小程序secret
            params.put("secret", miniProgramConfig.APPSECRET);
            //小程序端返回的code
            params.put("js_code", code);
            //默认参数
            params.put("grant_type", "authorization_code");
            // 发送请求获取openId 、 sessionKey
            JSONObject sessionKey_openId = JSON.parseObject(HttpClientUtil.doPost(miniProgramConfig.URL, params));
            System.out.println(sessionKey_openId);
            // openId
            String openId = sessionKey_openId.getString("openid");
            // sessionKey
            String sessionKey = sessionKey_openId.getString("session_key");
            // 消息解密 获取unionId
            String result = AesCbcUtil.decrypt(encryptedData, sessionKey, iv, "UTF-8");
            String unionId = (String) JSON.parseObject(result).get("unionId");

            // 返回用户信息
            User user = userRepository.findByOpenId(openId);
            // 判断是否是新用户
            String token = "";
            if(user == null) {
                // 用户不存在
                String nickname = dataJson.getString("nickName");
                String avatarUrl = dataJson.getString("avatarUrl");
                String gender = dataJson.getString("gender");
                user = new User();
                user.setNickname(nickname);
                user.setOpenId(openId);
                user.setUnionId(unionId);
                user.setGender(Integer.parseInt(gender));
                user.setShowMe(0);
                user.setAvatarUrl(avatarUrl);
                user.setCreateTime(new Date());
                user.setLoginLastTime(new Date());
                user.setLoginEnable(1);
                user.setUserViews(0);
                User save = userRepository.save(user);
                // 将token和用户信息存入redis   有效期24h
                Integer userId = save.getId();
                // Integer userId = userRepository.findIdByOpenId(openId);
                token = JwtUtils.createJWT(Integer.toString(userId), openId);
                // redis 键:  user:userId:xx:  值: token
                String key = RedisKeyUtil.createKey("login:user", "userId", userId);
                redisUtil.set(key, token, 60*60*24*7);
            } else {
                // 用户存在
                if(user.getLoginEnable().equals(0)) {    // 禁止登录
                    return new ResultUtil(UnicomResponseEnums.FAIL_FORBID_LOGIN);
                }
                user.setUnionId(unionId);

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
