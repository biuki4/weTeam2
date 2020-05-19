package com.iamk.weTeam.controller;

import com.alibaba.fastjson.JSONObject;
import com.iamk.weTeam.common.Enum.UnicomResponseEnums;
import com.iamk.weTeam.common.constant.MiniProgramConstant;
import com.iamk.weTeam.common.constant.UnionConstant;
import com.iamk.weTeam.common.expection.UnicomRuntimeException;
import com.iamk.weTeam.common.annotation.PassToken;
import com.iamk.weTeam.common.utils.*;

import com.iamk.weTeam.model.entity.Game;
import com.iamk.weTeam.model.entity.GameTag;
import com.iamk.weTeam.model.entity.TeamUser;
import com.iamk.weTeam.model.entity.User;
import com.iamk.weTeam.repository.GameRepository;
import com.iamk.weTeam.repository.GameTagRepository;

import com.iamk.weTeam.repository.TeamUserRepository;
import com.iamk.weTeam.repository.UserRepository;
import com.iamk.weTeam.wxMp.config.WxMpProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.annotation.Resource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Resource
    GameRepository gameRepository;
    @Resource
    GameTagRepository gameTagRepository;
    @Resource
    TeamUserRepository teamUserRepository;
    @Resource
    UserRepository userRepository;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private WxMpProperties properties;

    /**
     * 时间测试 比较大小
     * @return
     * @throws ParseException
     */
    @RequestMapping("/test1")
    public ResultUtil test() throws ParseException {
        Game game = gameRepository.findById(1).orElse(null);
        Date date1 = DateUtil.parseDate2("2020-05-05");
        Date date2 = DateUtil.parseDate2("2020-05-06");
        Date date3 = DateUtil.parseDate2("2020-05-07");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date now = sdf.parse(sdf.format(new Date()));

        System.out.println(now);
        System.out.println(game.getRegisterStartTime());
        boolean after = game.getRegisterStartTime().before(now);
        boolean after2 = game.getRegisterEndTime().before(now);
        boolean after3 = game.getGameStartTime().before(now);
        System.out.println(after);
        System.out.println(after2);
        System.out.println(after3);
        return ResultUtil.success();
   }

    /**
     * 测试repository是否能用实体类做变量
     * @return
     * @throws ParseException
     */
    @RequestMapping("/test2")
    public ResultUtil test2() throws ParseException {
        TeamUser teamU = teamUserRepository.findByTeamIdAndUserId(9, 1);
        teamU.setTeamId(teamU.getTeamId());
        teamU.setUserId(teamU.getUserId());
        teamU.setType(2);
        // teamUserRepository.flush();
        // Integer save = teamUserRepository.updateBase(teamU);
        TeamUser save = teamUserRepository.saveAndFlush(teamU);
        // Integer save = teamUserRepository.test();
        System.out.println(save);

        return ResultUtil.success(save);
    }

    /**
     * 测试unionId解析用户信息
     * @return
     * @throws ParseException
     */
    @RequestMapping("/test3")
    public ResultUtil test3() throws ParseException {
        // 存在
        JSONObject user1 = MyUtils.getUserFromUnionId(UnionConstant.WEAPPSECRET, MiniProgramConstant.APPID, "oySnI1KkVx229hXS78T5MHY6TVWw");
        // 不存在
        JSONObject user2= MyUtils.getUserFromUnionId(UnionConstant.WEAPPSECRET, MiniProgramConstant.APPID, "oySnI1KkVx229hXS78T5MHY6TVWw11");
        System.out.println(user1);
        System.out.println(user2);

        Object name = user1.get("name");
        Object name2 = user2.get("name");
        System.out.println(name);
        System.out.println(name2);

        boolean f1 = StringUtils.isNotBlank((String) name);
        boolean f2 = StringUtils.isNotBlank((String) name2);
        System.out.println(f1);
        System.out.println(f2);

        User user = new User();
        user = userRepository.findByOpenId("456789");
        user.setId(789);
        System.out.println(user);

        return ResultUtil.success(user1);
    }


    /**
     * 字符串替换
     * @return
     * @throws ParseException
     */
    @RequestMapping("/test4")
    public ResultUtil test4(@RequestParam String str)  {
        System.out.println(str);

        String regex = "(\")(.*)(\")";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str );
        while (matcher.find()) {
            str = "“" + matcher.group(2) + "”";
            matcher = pattern.matcher(str);
        }

        System.out.println(str);
        return ResultUtil.success();
    }


    /**
     * 生成小程序码
     * @return
     */
    @RequestMapping("/test5")
    public ResultUtil test5(@RequestParam String scene, @RequestParam(defaultValue = "false") String isHyaline) {
        String GET_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx42ac70376e53ee05&secret=13ff5f902585ed936bbc7c1f786644d3";
        String assess_token = HttpClientUtil.doGet(GET_URL);

        String POST_URL = "https://api.weixin.qq.com/cgi-bin/wxaapp/createwxaqrcode?access_token=" + assess_token;
        Map<String, String> params = new HashMap<String, String>();
        params.put("scene", scene);
        params.put("is_hyaline", isHyaline);
        String s = HttpClientUtil.doPost(POST_URL, params);

        try {
            FileOutputStream fos = new FileOutputStream("H:\\intelliJ\\workspace\\weTeam\\src\\main\\resources\\tmp\\test.png");
            int ch = 0;
            fos.write(Integer.parseInt(s));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResultUtil.success();
    }

    public static void main(String[] args) {

    }

}
