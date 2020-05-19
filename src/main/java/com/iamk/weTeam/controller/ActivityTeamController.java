package com.iamk.weTeam.controller;

import com.iamk.weTeam.common.annotation.PassToken;
import com.iamk.weTeam.common.utils.ResultUtil;
import com.iamk.weTeam.repository.ActivityRepository;
import com.iamk.weTeam.repository.ActivityTeamUserRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/activityTeam")
public class ActivityTeamController {

    @Resource
    ActivityRepository activityRepository;
    @Resource
    ActivityTeamUserRepository activityTeamUserRepository;

    @PassToken                      // 没有这个注解请求时需要带上token令牌才能访问
    @RequestMapping("/test")
    public ResultUtil test() {
        System.out.println("--------test---------");
        return ResultUtil.success();
    }
}
